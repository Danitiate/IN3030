import java.util.concurrent.*;

class ParallellRadix{
  int n, globalMax, num_cores, bit1, bit2;
  int[] a, b, allMax, sumCount;
  int[][] allCount;
  CyclicBarrier sync;

  public ParallellRadix(int[] a){
    this.a = a;
    this.n = a.length;
    this.b = new int[n];
    this.num_cores = Runtime.getRuntime().availableProcessors();
    this.globalMax = 0;
    this.sync = new CyclicBarrier(num_cores);
    this.allMax = new int[num_cores];
    this.allCount = new int[num_cores][];
  }

  public void run(){
    Thread[] t = new Thread[num_cores];
    for(int i = 0; i < num_cores; i++){
      t[i] = new Thread(new ParallelWorker(i));
      t[i].start();
    }


    for(int i = 0; i < num_cores; i++){
      try{ t[i].join();}catch (Exception e){return;}
    }
    //System.out.println("globalMax: " + globalMax);
  }




  class ParallelWorker implements Runnable{
    int id;
    int low;
    int high;
    int[] count;

    private ParallelWorker(int id){
      this.id = id;
      this.low = (n/num_cores)*id;
      this.high = (n/num_cores)*(id+1);
      if(id == num_cores-1) high = n;
    }

    public void run(){
      findMax();
      try{sync.await();}catch(Exception e){}
      radixSort(a, b, bit1, 0);
      try{sync.await();}catch(Exception e){}
      radixSort(b, a, bit2, bit1);
    }

    private void findMax(){
      allMax[id] = a[low];
      for(int i = low+1; i < high; i++){
        if(a[i] > allMax[id]) allMax[id] = a[i];
      }
      try{sync.await();}catch(Exception e){}
      if(id == 0) updateGlobalMax();
    }

    private void updateGlobalMax(){
      for(int i = 0; i < num_cores; i++){
        if(allMax[i] > globalMax) globalMax = allMax[i];
      }
      findNumBit();
    }


    private void findNumBit(){
      int numBit = 2;
      while (globalMax >= (1L<<numBit) )numBit++;
      bit1 = numBit/2;
      bit2 = numBit-bit1;
    }

    private void radixSort(int[] a, int[] b, int maskLen, int shift){
      int mask = (1 << maskLen) - 1;
      count = new int [mask+1];
      if(id == 0) sumCount = new int[mask+1];

      countFrequencyRadixValue(mask, shift);
      try{sync.await();}catch(Exception e){}
      if(id == 0) accumulateCountValues(mask);
      try{sync.await();}catch(Exception e){}
      moveNumbers(a, b, shift, mask);
    }

    //Counts the frequency of each value in a[] and stores the values in count[]
    private void countFrequencyRadixValue(int mask, int shift){
      for(int i = low; i < high; i++){
        count[(a[i] >>> shift) & mask]++;
      }

      allCount[id] = count;
    }

    //Adds all the count[] values to a global sumCount[]
    private void accumulateCountValues(int mask){
      //int acumLow = ((mask+1)/num_cores)*id;
      //int acumHigh = ((mask+1)/num_cores)*(id+1)-1;

      int acumVal = 0, k;

      for(int i = 0; i <= mask; i++){
        sumCount[i] = acumVal;
        for(int j = 0; j < num_cores; j++){
          k = allCount[j][i];
          acumVal += k;
        }
      }
    }

    //Moves all the values using the indexes from sumCount and into b[]
    private void moveNumbers(int[] a, int[] b, int shift, int mask){
      int[] localSumCount = copyArray(sumCount);
      for (int i = 0; i < high; i++) {
        while(i < low){
          localSumCount[(a[i] >>> shift) & mask]++;
          i++;
        }
        b[localSumCount[(a[i] >>> shift) & mask]++] = a[i];

      }
    }

    //Creates a copy of a given array with a new pointer (this program copies sumCount)
    private int[] copyArray(int[] array){
      int[] returnArray = new int[array.length];
      for(int i = 0; i < array.length; i++){
        returnArray[i] = array[i];
      }
      return returnArray;
    }
  }
}
