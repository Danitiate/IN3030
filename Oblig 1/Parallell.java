import java.util.concurrent.*;

class Parallell{
  int[] nums;
  int n;
  int k;
  int numThreads = Oblig1.CORES;
  int globalMin;
  int globalMinId;
  Semaphore semaphore = new Semaphore(1);

  public Parallell(int[] nums, int n, int k){
    this.nums = nums;
    this.n = n;
    this.k = k;
  }

  public void solve(){
    Thread[] t = new Thread[numThreads];

    for (int i = 0; i < numThreads; i++) {
      t[i] = new Thread(new ParallellWorker(i));
      t[i].start();
    }

    for(int i = 0; i < numThreads; i++){
      try{ t[i].join();}catch (Exception e){return;}
    }
  }


  class ParallellWorker implements Runnable{
    int id;
    public ParallellWorker(int id){
      this.id = id;
    }

    public void run(){
      sort(nums, id, k-1);
      //The shortest numbers should now be on nums[k-1+id]
      int i = k+id;

      while(i < n){
        try{
          if(nums[i] > globalMin){ //Ignores numbers smaller than nums[k-numThreads...k-1]
            semaphore.acquire();
            setGlobalMin(i);
            semaphore.release();
          }
          i += numThreads;
        }catch (Exception e){return;}
      }
    }

    /**
     * Uses the newly found big number and adds it to the nums[0..k] values.
     * Only sorts the values to the globalMinId thread.
     * Calls findGlobalMin() upon completion to set a new value to globalMin
     * @param int ind: Index value to the big number (nums[ind])
     */
    private void setGlobalMin(int ind){
      nums[k-numThreads+globalMinId] = nums[ind];
      nums[ind] = globalMin;
      sort(nums, globalMinId, k-1);
      findGlobalMin();
    }

    /**
     * Checks the numbers between nums[k-numThreads...k-1] to find the smallest
     * value. The smallest number should be the next to change. Also finds the
     * thread id which is responsible for this number.
     */
    private void findGlobalMin(){
      int min = nums[k-numThreads];
      globalMinId = 0;
      for(int i = k-numThreads+1; i < k; i++){
        if(nums[i] < min){
          min = nums[i];
          globalMinId = i%numThreads;
        }
      }
      globalMin = min;
    }


    /*Sorts a threads numbers from v..h, by jumping numThreads steps*/
    private void sort (int[] a, int v, int h) {
      for (int j = v; j < h; j += numThreads) {
        int temp = a[j+numThreads];
        int i = j;
        while (i >= v && a[i] < temp) {
          a[i+numThreads] = a[i];
          i -= numThreads;
        }
        a[i+numThreads] = temp;
      }
    }
  }
}
