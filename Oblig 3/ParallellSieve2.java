import java.util.concurrent.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.Arrays;

class ParallellSieve2{
  private int[] primes;
  private byte[] byteArray;
  private int n;
  private int num_cores;
  private int primesCounter;
  private CyclicBarrier sync;
  private CyclicBarrier sync2;
  private int globalcounter = 0;
  private int firstCounter = 0;
  final Lock lock = new ReentrantLock();
  final Condition availablePrime = lock.newCondition();
  Oblig3Precode precode = new Oblig3Precode(n);

  public ParallellSieve2(int n, int k) {
      this.n = n;
      this.num_cores = k;
      int cells = n / 16 + 1;
      byteArray = new byte[cells];
      sync = new CyclicBarrier(num_cores);
      sync2 = new CyclicBarrier(num_cores);
  }

  public int[] findPrimes(){
    Thread[] t = new Thread[num_cores];
    for(int i = 0; i < num_cores; i++){
      t[i] = new Thread(new ParallellWorker(i));
      t[i].start();
    }
    for(int i = 0; i < num_cores; i++){
      try{ t[i].join();}catch (Exception e){return null;}
    }

    //Arrays.sort(primes);
    primes[0] = 2;

    /*for(int i = 0; i < primes.length; i++){
      System.out.println(primes[i]);
    }*/
    return primes;
  }

  public void factorizeLargest(){
    Thread[] t = new Thread[num_cores];
    for(int i = 0; i < num_cores; i++){
      t[i] = new Thread(new ParallellFactorize(i));
      t[i].start();
    }
    for(int i = 0; i < num_cores; i++){
      try{ t[i].join();}catch (Exception e){return;}
    }

  }



  class ParallellWorker implements Runnable{
    int id;
    byte[] localData;
    int localPrimesCounter;


    public ParallellWorker(int id){
      this.id = id;
      int cells = (n / num_cores) / 16 + 1;
      this.localData = new byte[cells];
    }


    public void run(){
      findFirstPrimes();
      //System.out.printf("Thread: %d DONE findFirstPrimes\n", id);

      try{sync2.await();}catch(Exception e){}
      if(id == 0) countRestPrimes();
      //System.out.printf("Thread %d DONE countRestPrimes\n", id);

      try{sync.await();}catch(Exception e){}
      if(id == 0) gatherPrimes();
      //System.out.printf("Thread %d DONE\n", id);
    }


    private void findFirstPrimes() {
        if(id == 0) primesCounter = 1;
        int currentPrime = 3;
        int squareRootN = (int)Math.sqrt(n);
        if(id == 0) firstCounter = (int)Math.sqrt(squareRootN);
        //if(id == 0) System.out.println(firstCounter);
        while(currentPrime != 0 && currentPrime <= squareRootN) {
            //if(id == 0) System.out.println(firstCounter);
            traverse(currentPrime);
            currentPrime = findNextPrime(currentPrime + 2);
            //System.out.printf("Thread: %d   currentPrime: %d\n", id, currentPrime);
            if(id == 0) primesCounter++;
            if(firstCounter > 0){
              try{sync.await();}catch(Exception e){}
              if(id == 0) firstCounter--;
              try{sync.await();}catch(Exception e){}
            }
        }
    }

    private void traverse(int p) {
        for (int i = findInitial(p); i < n; i += p * 2) {
            if((i / 16)%num_cores != id){
              while(true){
                i += p*2;
                int byteCell = i/16;
                if((byteCell%num_cores) == id) break;
              }
              if(i > n) break;
            }
            flip(i);
        }
    }

    private int findInitial(int p){
      int num = p*p;
      while(true){
        int byteCell = num/16;
        if(byteCell%num_cores == id){
          return num;
        }
        num += p*2;
      }
    }

    private void flip(int i) {
        if (i % 2 == 0) {
            return;
        }

        int byteCell = i / 16;
        int bit = (i / 2) % 8;

        //System.out.printf("Thread: %d Flipped: %d\n", id, i);
        byteArray[byteCell] |= (1 << bit);
    }

    private int findNextPrime(int startAt) {
        for (int i = startAt; i < n; i += 2) {
            if(isPrime(i)) {
                return i;
            }
        }
        return 0;
    }

    private boolean isPrime(int i) {
        if((i % 2) == 0) {
            return false;
        }

        int byteCell = i / 16;
        int bit = (i / 2) % 8;
        return (byteArray[byteCell] & (1 << bit)) == 0;
    }


    private void countRestPrimes() {
        int startAt = (int)Math.sqrt(n) + 1;

        if (startAt % 2 == 0) {
            startAt++;
        }

        startAt = findNextPrime(startAt);
        while(startAt != 0) {
            primesCounter++;
            startAt = findNextPrime(startAt+2);
        }
    }


    /*private void countRestPrimes() {
        int squareRootN = (int)Math.sqrt(n);
        int gap = (n - squareRootN) / num_cores;
        int low = squareRootN + 1 + gap*id;
        int high = squareRootN + gap*(id+1);
        localPrimesCounter = 0;
        if(id == (num_cores-1)) high = n;

        int startAt = low;

        if (startAt % 2 == 0) {
            startAt++;
        }

        startAt = findNextPrime(startAt);
        while(startAt != 0 && startAt <= high) {
            localPrimesCounter++;
            startAt = findNextPrime(startAt+2);
        }
        lock.lock();
        primesCounter += localPrimesCounter;
        lock.unlock();
    }*/

    private void gatherPrimes() {
      primes = new int[primesCounter];
      primes[0] = 2;

      int currentPrime = 3;
      for (int i = 1; i < primesCounter; i++) {
        primes[i] = currentPrime;
        currentPrime = findNextPrime(currentPrime+2);
      }
    }

  }





  class ParallellFactorize implements Runnable{
    int id;
    public ParallellFactorize(int id){
      this.id = id;
    }

    public void run(){


      for (int i = n*n-id-1; i > (n*n)-101; i -= num_cores){
        factorize(i);
      }

      precode.writeFactors();
    }


    /**
     * Finds all factors to a number, starting from the first prime (2) to
     * sqrt(N). Assumes all primes have been found and stored in primes[].
     *
     * @param int num: The number to be factorized
     */
    private void factorize(int num){
      int original = num;
      for(int i = 0; i < primes.length && primes[i] <= Math.sqrt(num); i++){
        if(num % primes[i] == 0){
          precode.addFactor(original, primes[i]);
          num = num/primes[i];
          i--;
        }
      }
      if(num > 1) precode.addFactor(original, num);
    }
  }
}
