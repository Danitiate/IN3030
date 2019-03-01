/**
* @author Daniel Dutta <danieldu@student.matnat.uio.no>
* @date 2019.02.28
*/

import java.util.Random;

class Oblig1{
  final static int CORES = 4;
  final static int RANDOMSEED = 1234;
  final static int LOOPS = 7;
  static int n;
  static int k;


  public static void main(String[] args) {
    boolean parallell = false;
    boolean noControl = false;

    if (args.length < 2){
      System.out.println("USAGE: java Oblig1 <n> <k> [p|-p|parallell] [nc|-nc|nocontrol]");
      System.exit(0);
    }else if(args.length > 3){
      if (args[2].matches("p|-p|parallell")) parallell = true;
      if (args[2].matches("nc|-nc|nocontrol")) noControl = true;
      if (args[3].matches("p|-p")) parallell = true;
      if (args[3].matches("nc|-nc|nocontrol")) noControl = true;
    }else if(args.length > 2){
      if (args[2].matches("p|-p|parallell")) parallell = true;
      if (args[2].matches("nc|-nc|nocontrol")) noControl = true;
    }


    n = Integer.parseInt(args[0]);
    k = Integer.parseInt(args[1]);

    int[] times = new int[LOOPS];
    int[] times2 = new int[LOOPS];

    for (int loops = 0; loops < LOOPS; loops++){
      int[] nums = create_numbers();
      times[loops] = runCase(nums, parallell);

      if(!noControl){
        int[] control = create_numbers();
        Sekvensiell c = new Sekvensiell(control, n, k);
        long tid = c.solveControl(nums);
        times2[loops] = (int) tid;
      }
    }

    insertSort(times, 0, LOOPS-1);
    String out = parallell ? "Parallell:\t" : "Sequential:\t";
    out += times[LOOPS/2] + "ms" + "   Fastest time: ";
    out += times[LOOPS-1] + "ms   Slowest time: " + times[0] + "ms";
    System.out.println(out);

    if(!noControl){
      insertSort(times2, 0, LOOPS-1);
      out = "Arrays.sort():\t" + times2[LOOPS/2] + "ms" + "   Fastest time: ";
      out += times2[LOOPS-1] + "ms   Slowest time: " + times2[0] + "ms";
      System.out.println(out);
    }
  }

  /**
   * Creates a random int array of length n with random numbers
   * @return int[]
   */
  public static int[] create_numbers(){
    Random r = new Random(RANDOMSEED);
    int[] a = new int[n];

    for(int i = 0; i < n; i++){
      a[i] = r.nextInt(n);
    }
    return a;
  }


  public static int runCase(int[] nums, boolean parallell){
    long tid = System.nanoTime();
    if(parallell){
      Parallell p = new Parallell(nums, n, k);
      p.solve();
      insertSort(nums, 0, k-1);
    }else{
      Sekvensiell s = new Sekvensiell(nums, n, k);
      s.solve();
    }
    tid = (System.nanoTime() - tid)/1000000;
    return (int) tid;
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
