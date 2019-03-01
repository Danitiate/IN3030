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

  public void solve(){
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
  }

  public long solveControl(int[] a){
    long tid = System.nanoTime();
    Arrays.sort(nums);
    tid = (System.nanoTime() - tid)/1000000;
    //Checking if both arrays have the k largest numbers sorted
    int j = 0;
    for(int i = n-1; i >= n-k; i--){
      if(nums[i] != a[j++]){
        System.out.println("ERROR!!! Array not correctly sorted!");
        //printArray(a, nums);
        System.exit(1);
      }
    }
    return tid;
  }

  /*USED FOR DEBUGGING - Prints nums[0...k-1] and compares to control array
  public void printArray(int[] a, int[] b){
    int j = n-1;
    for(int i = 0; i < k; i++){
      System.out.println(a[i] + " | " + b[j--]);
    }
  }*/
}
