import java.text.NumberFormat;

class Oblig4{
  final static int NUM_LOOPS = 7;

  public static void main(String[] args) {
    Oblig4Test.checkCorrectArgs(args);

    int n = Integer.parseInt(args[0]);
    int seed = Integer.parseInt(args[1]);
    int[] sequentialNumbers = null;
    int[] parallelNumbers = null;

    System.out.printf("\nRUNNING WITH N = %s\n\n", NumberFormat.getNumberInstance().format(n));

    for(int i = 0; i < NUM_LOOPS; i++){
      System.out.printf("Run: %d\n", i+1);
      //Generate arrays
      sequentialNumbers = Oblig4Precode.generateArray(n, seed);
      parallelNumbers = Oblig4Precode.generateArray(n, seed);

      //Run sequential
      Timer timer_seq = new Timer();
      SekvensiellRadix.radix2(sequentialNumbers);
      timer_seq.stop();

      //Run parallel
      Timer timer_para = new Timer();
      ParallellRadix parallel = new ParallellRadix(parallelNumbers);
      parallel.run();
      timer_para.stop();

      //Print runtimes
      long seqTime = timer_seq.getRuntime();
      long paraTime = timer_para.getRuntime();

      System.out.printf("Sequential runtime: %d ms\n", seqTime);
      System.out.printf("Parallel runtime:   %d ms\n", paraTime);
      System.out.printf("Speedup:            %.3fx\n", (float)seqTime/(float)paraTime);
      System.out.println();

      //Test if correct output
      Oblig4Test.checkCorrectArrays(sequentialNumbers, parallelNumbers);

      System.out.println("\n-----------------------------------------------\n");
    }

    Oblig4Precode.saveResults(Oblig4Precode.Algorithm.SEQ, seed, sequentialNumbers);
    Oblig4Precode.saveResults(Oblig4Precode.Algorithm.PARA, seed, parallelNumbers);

  }
}
