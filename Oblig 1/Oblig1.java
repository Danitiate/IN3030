import java.util.Random;
import java.util.concurrent.*;

class Oblig1{
  final static int CORES = 4;
  final static int RANDOMSEED = 1234;

  public static void main(String[] args) {
    boolean parallell = false;

    if (args.length < 2){
      System.out.println("USAGE: java -ea Oblig1 <n> <k>");
      System.exit(0);
    }else if(args.length > 2){
      if (args[2].equals("-p") || args[2].equals("p") || args[2].equals("parallell")){
        parallell = true;
      }
    }

    int n = Integer.parseInt(args[0]);
    int k = Integer.parseInt(args[1]);
    int[] nums = create_numbers(n);
    int[] control = create_numbers(n);

    if(!parallell){
      int[] times = new int[7];
      int[] times2 = new int[7];

      for (int loops = 0; loops < 7; loops++){
        Sekvensiell oppgave1 = new Sekvensiell(nums, n, k);
        Sekvensiell control1 = new Sekvensiell(control, n, k);
        long tid = System.nanoTime();
        nums = oppgave1.solve();
        tid = (System.nanoTime() - tid)/1000000;
        times[loops] = (int) tid;
        //If this passes without an assertion error, the sorting is correct
        long tid2 = control1.solveControl(nums);
        times2[loops] = (int) tid2;

      }
      insertSort(times, 0, 6); System.out.println("Sequential:    " + times[3] + "ms");
      insertSort(times2, 0, 6); System.out.println("Arrays.sort(): " + times2[3] + "ms");
    }else{
      parallellSolve(nums, n, k);
      insertSort(nums, 0, k-1);
    
      Sekvensiell control1 = new Sekvensiell(control, n, k);
      control1.solveControl(nums);
    }
  }

  public static int[] create_numbers(int n){
    Random r = new Random(RANDOMSEED);
    int[] a = new int[n];

    for(int i = 0; i < n; i++){
      a[i] = r.nextInt(n);
    }
    return a;
  }


  public static void parallellSolve(int[] nums, int n, int k){
    Parallell p = new Parallell(nums, n, k);
    p.solve();

  }

  /** Denne sorterer a[v..h] i synkende rekkefølge med innstikk-algoritmen*/
  public static void insertSort (int [] a, int v, int h) {
    int i, t;
    for (int k = v; k < h; k++) {
      // invariant: a[v..k] er nå sortert synkende (største først)
      t = a[k+1];
      i = k;
      while (i >= v && a[i] < t ) {
        a[i+1] = a[i];
        i--;
      }
      a[i+1] = t;
    } // end for k
  } // end insertSort


}
