import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class ParallellFactor{
  int[] primes;
  int n, num_cores;
  long[] top100, top100Factored;
  Oblig3Precode precode;
  private CyclicBarrier sync;
  final Lock lock = new ReentrantLock();

  public ParallellFactor(int n, int k, int[] primes){
    this.n = n;
    this.primes = primes;
    this.num_cores = k;
    precode = new Oblig3Precode(n);
    sync = new CyclicBarrier(num_cores);
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

      for(int i = id; i < primes.length; i += num_cores){
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
