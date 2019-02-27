/**
* @author Daniel Dutta <danieldu@student.matnat.uio.no>
* @date 2019.02.27
*/

import java.util.Arrays;

class Oblig2{
  final static int LOOPS = 7; //Amount of times to run each algorithm
  final static int NUM_CORES = 4; //Amount of cores on my computer
  static int n; //Matrix size (nXn)
  static int seed; //RNG seed

  public static void main(String[] args) {
    if (args.length != 2){
      System.out.println("USAGE: java Oblig2 <n> <seed>");
      System.exit(1);
    }

    //Checks for correct input
    if(args[0].matches("\\d+") && args[1].matches("\\d+")){
      n = Integer.parseInt(args[0]);
      seed = Integer.parseInt(args[1]);
    }else{
      System.out.println("Invalid input: Expected a number");
      System.exit(1);
    }

    /**
     * Performs all the calculations and prints out all the times taken
     * 1 = SEQ_NOT_TRANSPOSED     2 = SEQ_A_TRANSPOSED    3 = SEQ_B_TRANSPOSED
     * 4 = PARA_NOT_TRANSPOSED    5 = PARA_A_TRANSPOSED   6 = PARA_B_TRANSPOSED
     */
    for(int i = 1; i <= 6; i++){
      runCase(i);
    }
  }


  //Transposes a given matrix and returns the result.
  public static double[][] transpose(double[][] a){
    double[][] b = new double[n][n];
    for(int i = 0; i < n; i++){
      for(int j = 0; j < n; j++){
        b[j][i] = a[i][j];
      }
    }
    return b;
  }


  /**
   * Multiplies matrix a with b and stores the values in c.
   * @param int d: A switch to determine which algorithm to use.
   * 1: SEQ_NOT_TRANSPOSED    2: SEQ_A_TRANSPOSED     3: SEQ_B_TRANSPOSED
   * 4: PARA_NOT_TRANSPOSED   5: PARA_A_TRANSPOSED    6: PARA_B_TRANSPOSED
   *
   * Prints the time taken by each loop and displays the slowest, fastest and
   * median time.
   *
   * Every parallell solution (d >= 4) is double checked with a sequential
   * algorithm to check if it is correct.
   *
   * Tested on my computer with no errors as of 26th of February 2019.
   */
  public static void runCase(int d){
    double[][] c = null;
    long[] times = new long[LOOPS];
    String out = d + ": [";

    for(int l = 0; l < LOOPS; l++){
      long tid = System.nanoTime();
      double[][] a = Oblig2Precode.generateMatrixA(seed, n);
  		double[][] b = Oblig2Precode.generateMatrixB(seed, n);
      switch(d){
        case 1: //SEQ_NOT_TRANSPOSED
                Sequential s1 = new Sequential(n, 1);
                c = s1.sequential(a, b);
                break;

        case 2: //SEQ_A_TRANSPOSED
                Sequential s2 = new Sequential(n, 2);
                a = transpose(a);
                c = s2.sequential(a, b);
                break;

        case 3: //SEQ_B_TRANSPOSED
                Sequential s3 = new Sequential(n, 3);
                b = transpose(b);
                c = s3.sequential(a, b);
                break;

        case 4: //PARA_NOT_TRANSPOSED
                Parallell p1 = new Parallell(n, a, b, d);
                c = p1.solve();

                //Oblig2Precode.saveResult(seed, Oblig2Precode.Mode.PARA_NOT_TRANSPOSED, c);
                break;

        case 5: //PARA_A_TRANSPOSED
                a = transpose(a);
                Parallell p2 = new Parallell(n, a, b, d);
                c = p2.solve();
                break;

        case 6: //PARA_B_TRANSPOSED
                b = transpose(b);
                Parallell p3 = new Parallell(n, a, b, d);
                c = p3.solve();
                break;

      }

      tid = (System.nanoTime() - tid)/1000000;
      times[l] = tid;
      out += tid + ", ";
    }

    //Double checks if solution is correct
    if(d >= 4){
      if(!checkSolution(c)){
        System.out.println("ERROR!!! Matrix not correct!");
      }
    }

    Arrays.sort(times);
    out = out.substring(0, out.length()-2);
    out += "]  Fastest time: " + times[0] + " ms\tSlowest time: " + times[LOOPS-1];
    out += " ms\tMedian time: " + times[LOOPS/2] + " ms";
    System.out.println(out);

    //Saves the matrixes
    switch(d){
      case 1: Oblig2Precode.saveResult(seed, Oblig2Precode.Mode.SEQ_NOT_TRANSPOSED, c); break;
      case 2: Oblig2Precode.saveResult(seed, Oblig2Precode.Mode.SEQ_A_TRANSPOSED, c); break;
      case 3: Oblig2Precode.saveResult(seed, Oblig2Precode.Mode.SEQ_B_TRANSPOSED, c); break;
      case 4: Oblig2Precode.saveResult(seed, Oblig2Precode.Mode.PARA_NOT_TRANSPOSED, c); break;
      case 5: Oblig2Precode.saveResult(seed, Oblig2Precode.Mode.PARA_A_TRANSPOSED, c); break;
      case 6:	Oblig2Precode.saveResult(seed, Oblig2Precode.Mode.PARA_B_TRANSPOSED, c); break;
    }
  }


  /**
   * Creates a new matrix using the same seed and the sequential algorithm.
   * Checks if the current solution is equal to this.
   * @param double[][] c: The current solution
   * @return true if equal, false otherwise.
   */
  public static boolean checkSolution(double[][] c){
    Sequential s = new Sequential(n, 0);
    double[][] a = Oblig2Precode.generateMatrixA(seed, n);
    double[][] b = Oblig2Precode.generateMatrixB(seed, n);
    double[][] check = s.sequential(a, b);
    for(int i = 0; i < n; i++){
      for(int j = 0; j < n; j++){
        if(check[i][j] != c[i][j]){
          return false;
        }
      }
    }
    return true;
  }
}
