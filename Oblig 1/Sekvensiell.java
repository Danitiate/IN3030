import java.util.Arrays;

class Sekvensiell{
  int[] nums;
  int n;
  int k;

  public Sekvensiell(int[] nums, int n, int k){
    this.nums = nums;
    this.n = n;
    this.k = k;
  }

  public int[] solve(){
    Oblig1.insertSort(nums, 0, k-1);
    int i = k;
    while(i < n){
      if(nums[i] > nums[k-1]){
        int temp = nums[k-1];
        nums[k-1] = nums[i];
        nums[i] = temp;
        Oblig1.insertSort(nums, 0, k-1);
      }
      i++;
    }
    return nums;
  }

  public long solveControl(int[] a){
    long tid = System.nanoTime();
    Arrays.sort(nums);
    tid = (System.nanoTime() - tid)/1000000;
    //Checking if both arrays have the k largest numbers sorted
    int j = 0;
    for(int i = n-1; i >= n-k; i--){
      //assert (nums[i] == a[j++]) == true;
      System.out.println(a[j++] + " | " + nums[i]);
    }
    return tid;
  }
}
