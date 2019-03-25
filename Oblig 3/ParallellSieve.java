import java.util.concurrent.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.Arrays;

class ParallellSieve{
  private int[] primes;
  private byte[] byteArray;
  private int n;
  private int num_cores;
  private int primesCounter;
  private CyclicBarrier sync;
  private CyclicBarrier sync2;
  private int firstCounter = 0;
  private int globalcounter = 0;
  private long globalFactor;
  final Lock lock = new ReentrantLock();
  final Condition availablePrime = lock.newCondition();
  Oblig3Precode precode;
  long[] top100;
  long[] top100Factored;

  public ParallellSieve(int n, int k) {
      this.n = n;
      this.num_cores = k;
      int cells = n / 16 + 1;
      byteArray = new byte[cells];
      sync = new CyclicBarrier(num_cores);
      sync2 = new CyclicBarrier(num_cores);
      precode = new Oblig3Precode(n);
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

    Arrays.sort(primes);
    primes[0] = 2;

    /*for(int i = 0; i < primes.length; i++){
      System.out.println(primes[i]);
    }*/
    return primes;
  }

  public void factorizeLargest(){
    top100 = new long[100];
    top100Factored = new long[100];
    for(int i = 0; i < 100; i++){
      top100[i] = ((long) n * (long) n)-i-1;
      top100Factored[i] = ((long) n * (long) n)-i-1;
    }

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
      try{sync.await();}catch(Exception e){}
      countRestPrimes();
      try{sync.await();}catch(Exception e){}
      gatherPrimes();

    }


    private void findFirstPrimes() {
        localPrimesCounter = 0;
        int squareRootN = (int)Math.sqrt(n);
        if(id == 0) findNecessaryPrimes(squareRootN);
        try{sync.await();}catch(Exception e){}

        int currentPrime = findInitial();
        int counter = num_cores;

        while(currentPrime != 0 && currentPrime <= squareRootN) {
            traverse(currentPrime);
            for(int i = counter; i > 0; i--){
              currentPrime = findNextPrime(currentPrime + 2);
            }
            localPrimesCounter++;
            if(currentPrime == 0 || currentPrime > squareRootN) break;
            //System.out.printf("Thread: %d   Current: %d\n", id, currentPrime);
            //if(currentPrime == 0 || currentPrime >= squareRootN) break;
        }
    }

    private void findNecessaryPrimes(int rootN){
      int currentPrime = 3;
      int squareRootN = (int)Math.sqrt(rootN);
      while(currentPrime != 0 && currentPrime <= squareRootN) {
          traverseSmall(currentPrime);
          currentPrime = findNextPrime(currentPrime + 2);
      }
    }

    private void traverseSmall(int p) {
        for (int i = p*p; i < (int) Math.sqrt(n); i += p * 2) {
            //System.out.printf("Thread %d: %d \n",id,i);
            flip(i);
        }
    }

    /*private void findFirstPrimes() {
        localPrimesCounter = 1;
        int currentPrime = 3;
        int squareRootN = (int)Math.sqrt(n);

        while(currentPrime != 0 && currentPrime <= squareRootN) {
            traverse(currentPrime);
            currentPrime = findNextPrime(currentPrime + 2);
            localPrimesCounter++;
        }
    }*/
    /*private void findFirstPrimes() {
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
    }*/

  /*  private void traverse(int p) {
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
    }*/

    private int findInitial(){
      int currentPrime = 3;
      int counter = id;
      while(counter != 0){
        currentPrime = findNextPrime(currentPrime + 2);
        counter--;
      }
      return currentPrime;
    }



    private void traverse(int p) {
        for (int i = p*p; i < n; i += p * 2) {
            //System.out.printf("Thread %d: %d \n",id,i);
            flip(i);
        }
    }

    private void flip(int i) {
        if (i % 2 == 0) {
            return;
        }

        int byteCell = i / 16;
        int bit = (i / 2) % 8;
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
        int squareRootN = (int)Math.sqrt(n);
        int gap = (n - squareRootN) / num_cores;
        int low = squareRootN + 1 + gap*id;
        int high = squareRootN + gap*(id+1);
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
    }

    private void gatherPrimes() {
      lock.lock();
      primesCounter += localPrimesCounter;
      lock.unlock();
      try{sync.await();}catch(Exception e){}
      if(id == 0){
        primesCounter++;
        primes = new int[primesCounter];
      }
      try{sync.await();}catch(Exception e){}

      int counter = 0;
      int[] localPrimes = new int[primesCounter];

      for (int i = 0; i < byteArray.length; i++) {
        for(int j = id; j < 8; j += num_cores){
          if((byteArray[i] & (1 << j)) == 0){
            int prime = (j * 2 + 1) + (i * 16);
            if(prime > n) continue;
            localPrimes[counter++] = prime;

            //System.out.printf("Thread %d: %d\n", id, prime);
          }
        }
      }


      lock.lock();
      for(int i = 0; i < primesCounter; i++){
        if(localPrimes[i] == 0) break;
        primes[globalcounter++] = localPrimes[i];
      }
      lock.unlock();
    }
  }





  class ParallellFactorize implements Runnable{
    int id;
    public ParallellFactorize(int id){
      this.id = id;
    }

    public void run(){
      for(int i = 0; i < 100; i++){
        factorize(i);
      }

      try{sync.await();}catch(Exception e){}
      if(id == 0){
        for(int i = 0; i < 100; i++){
          //System.out.printf("%d: %d\n", top100[i], top100Factored[i]);
          if(top100Factored[i] > 1) precode.addFactor(top100[i], top100Factored[i]);
        }
      }
      if(id == 0) precode.writeFactors();
    }



    /**
     * Finds all factors to a number, starting from the first prime (2) to
     * sqrt(N). Assumes all primes have been found and stored in primes[].
     *
     */
    private void factorize(int k){
      long num = top100[k];

      for(int i = id; i < primes.length/* && primes[i] < Math.sqrt(num)*/; i += num_cores){
        if(num % primes[i] == 0){
          num = num/primes[i];
          lock.lock();
          top100Factored[k] = top100Factored[k]/primes[i];
          //System.out.printf("%d: %d\n", num, primes[i]);
          precode.addFactor(top100[k], primes[i]);
          lock.unlock();
          i -= num_cores;
        }
      }
    }
  }
}
