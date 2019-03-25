/**
* @author Daniel Dutta <danieldu@student.matnat.uio.no>
* @date 2019.03.19
*/

import java.util.Arrays;

class Oblig3{
  final static int LOOPS = 7; //Amount of times to run each algorithm
  static int num_cores; //Amount of cores on my computer
  static int n; //Matrix size (nXn)

  public static void main(String[] args) {
    if (args.length != 2){
      System.out.println("USAGE: java Oblig3 <n> <k threads>");
      System.exit(1);
    }
    //Checks for correct input
    if(args[0].matches("\\d+") && args[1].matches("\\d+")){
      n = Integer.parseInt(args[0]);
      num_cores = Integer.parseInt(args[1]);
      if(n <= 16){
        System.out.println("n should be larger than 16. Got: " + n);
        System.exit(1);
      }
      if(num_cores == 0){
        num_cores = Runtime.getRuntime().availableProcessors();
      }

    }else{
      System.out.println("Invalid input: Expected a positive number");
      System.exit(1);
    }

    for(int i = 0; i < LOOPS; i++){
      System.out.printf("Run: %d\n", i);

      long seqTid = System.nanoTime();
      SequentialSieve s = new SequentialSieve(n);
      int[] primes = s.findPrimes();
      seqTid = (System.nanoTime() - seqTid)/1000000;

      long paraTid = System.nanoTime();
      ParallellSieve p = new ParallellSieve(n, num_cores);
      int[] paraPrimes = p.findPrimes();
      paraTid = (System.nanoTime() - paraTid)/1000000;

      if(!testEqualResults(primes, paraPrimes)){
        System.out.println("ERROR: Sequential primes differentiate from parallell primes!!");
      }

      System.out.printf("Eratosthenes sieve:   Sequential: %d ms    Parallell: %d ms    Speedup: %.3fx\n", seqTid, paraTid, (float) seqTid/(float) paraTid);

      long facTime = System.nanoTime();
      s.factorizeLargest();
      facTime = (System.nanoTime() - facTime)/1000000;

      long facTimePara = System.nanoTime();
      p.factorizeLargest();
      facTimePara = (System.nanoTime() - facTimePara)/1000000;

      System.out.printf("100 factorizations:   Sequential: %d ms    Parallell: %d ms    Speedup: %.3fx\n\n", facTime, facTimePara, (float) facTime/(float) facTimePara);
    }
  }

  public static boolean testEqualResults(int[] seq, int[] para){
    /*if(seq.length != para.length){
      System.out.println("Seq length: " + seq.length + " | Para length: " + para.length);
      return false;
    }*/
    for(int i = seq.length; i < seq.length; i++){
      if(seq[i] != para[i]){
        System.out.println(seq[i] + " | " + para[i]);
        return false;
      }
    }
    return true;
  }

   /**
    * TODO: REFACTOR FACTORIZE Parallell single number
    * TODO: (OPTIONAL) Fix bug FIND PRIMES Parallell: Wrong length
    */

}
