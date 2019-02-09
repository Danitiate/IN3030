import java.util.concurrent.*;
class Parallell{
  int[] nums;
  int n;
  int k;
  int numThreads = Oblig1.CORES;
  int globalMin;
  Semaphore semaphore = new Semaphore(1);
  Semaphore semaphore2 = new Semaphore(1);
  CyclicBarrier b;

  public Parallell(int[] nums, int n, int k){
    this.nums = nums;
    this.n = n;
    this.k = k;
    globalMin = n;
    b = new CyclicBarrier(numThreads);
  }

  public void solve(){
    int[] times = new int[numThreads];
    Thread[] t = new Thread[numThreads];

    for (int i = 0; i < numThreads; i++) {
      t[i] = new Thread(new ParallellWorker(i));
      t[i].start();
    }

    for(int i = 0; i < numThreads; i++){
      try{ t[i].join();}catch (Exception e){return;}
    }

    for(int i = 0; i < k; i++){
      System.out.println(i%4 +": " + nums[i]);
    }
  }


  class ParallellWorker implements Runnable{
    int id;
    int currentSmallest;
    public ParallellWorker(int id){
      this.id = id;
    }

    public void run(){
      long time = System.nanoTime();
      sort(nums, id, k-1);
      //The shortest numbers should now be on nums[k-1-id]

      currentSmallest = changeGlobalMin();
      int i = k+id;
      while(i < n){
        try{
          /*semaphore.acquire();
          if(nums[i] > globalMin){
            nums[k-1-currentSmallest] = nums[i];
            nums[i] = globalMin;
            sort(nums, currentSmallest, k-1);
          }
          semaphore.release();
*/        if(nums[i] > nums[k-numThreads+id]){
            int temp = nums[k-numThreads+id];
            nums[k-numThreads+id] = nums[i];
            nums[i] = temp;
            sort(nums, id, k-1);
          }
          i += numThreads;
          b.await();
        }catch (InterruptedException e){return;
        }catch (BrokenBarrierException e){return;}
      }

      time = (int) (System.nanoTime() - time)/1000000;
      System.out.println("Thread "+ id + ", time:" + time + "ms");
    }

    /*Finds which thread should be the next to change value*/
    public int changeGlobalMin(){
      int changed = -1;
      int localMin = findSmallest();
      try{
        semaphore.acquire();
        if(localMin <= globalMin){
          globalMin = localMin;
          changed = id;
        }
        semaphore.release();
        b.await();
      }catch (InterruptedException e){return -1;
      }catch (BrokenBarrierException e){return -1;}
      return changed;
    }

    /*Find the threads current lowest number*/
    public int findSmallest(){
      return nums[k-1-id];
    }


    /*Sorts a threads numbers from v..h, by jumping numThreads steps*/
    public void sort (int [] a, int v, int h) {
      for (int j = v; j < h; j += numThreads) {
        int temp = a[j+numThreads];
        int i = j;
        while (i >= v && a[i] < temp) {
          a[i+numThreads] = a[i];
          i = i - numThreads;
        }
        a[i+numThreads] = temp;
      }
    }
  }
}
