import java.util.concurrent.*;
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
  private int globalcounter = 0;
  final Lock lock = new ReentrantLock();


  public ParallellSieve(int n, int k) {
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

    Arrays.sort(primes);
    primes[0] = 2;

    /*for(int i = 0; i < primes.length; i++){
      System.out.println(primes[i]);
    }*/
    return primes;
  }


  class ParallellWorker implements Runnable{
    int id;
    int localPrimesCounter;


    public ParallellWorker(int id){
      this.id = id;
      int cells = (n / num_cores) / 16 + 1;
    }


    public void run(){
      findFirstPrimes();
      try{sync.await();}catch(Exception e){}
      countRestPrimes();
      try{sync.await();}catch(Exception e){}
      gatherPrimes();
    }


    private void findFirstPrimes() {
        if(id == 0) primesCounter = 1;
        int squareRootN = (int)Math.sqrt(n);
        if(id == 0) findNecessaryPrimes(squareRootN);
        try{sync.await();}catch(Exception e){}

        int currentPrime = 3;

        while(currentPrime != 0 && currentPrime <= squareRootN) {
            traverse(currentPrime);
            currentPrime = findNextPrime(currentPrime + 2);

            if(id == 0) primesCounter++;
            //System.out.printf("Thread: %d   Current: %d\n", id, currentPrime);
        }
    }

    /**
     * Sequential part: Finds all primes from 0..sqrt(n)
     * @param int rootN: The squareRoot of N
     */
    private void findNecessaryPrimes(int rootN){
      int currentPrime = 3;
      int squareRootN = (int)Math.sqrt(rootN);
      while(currentPrime != 0 && currentPrime <= squareRootN) {
          traverseSmall(currentPrime);
          currentPrime = findNextPrime(currentPrime + 2);
      }
    }

    /**
     * Does the same as traverse(), but 'i' never exceeds sqrt(n)
     * @param int p: The current primenumber
     */
    private void traverseSmall(int p) {
        for (int i = p*p; i < (int) Math.sqrt(n); i += p * 2) {
            //System.out.printf("Thread %d: %d \n",id,i);
            flip(i);
        }
    }


    private void traverse(int p) {
      int gap = byteArray.length/num_cores;
      int low = id*gap;
      int high = (id+1)*gap-1;
      if(id+1 == num_cores) high = byteArray.length-1;

      for (int i = p*p; i < n; i += p * 2) {
        int byteCell = i / 16;
        if(byteCell < low) continue;
        if(byteCell > high) return;
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
        lock.lock();
        primesCounter += localPrimesCounter;
        lock.unlock();
    }

    private void gatherPrimes() {
      if(id == 0){
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
}
