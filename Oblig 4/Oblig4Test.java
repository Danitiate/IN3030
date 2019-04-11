class Oblig4Test{
  final static int NUM_ARGUMENTS = 2;
  static boolean NO_ERRORS = true;


  /**
   * Tests that input arguments are written correctly.
   * Exits system if wrong input.
   * @param String[] args: Input arguments
   **/
  public static void checkCorrectArgs(String[] args){
    System.out.print("\u001B[31m");
    if(!checkArgsLength(args)){
      System.exit(0);
    }
    if(!checkArgsType(args)){
      System.exit(0);
    }
    System.out.print("\u001B[0m");
  }


  //Checks that String[] args have the correct length
  private static boolean checkArgsLength(String[] args){
    if(args.length == NUM_ARGUMENTS){
      return true;
    }
    System.out.println("USAGE: java Oblig4 <n> <seed>");
    return false;
  }

  //Checks that String[] args are Integers
  private static boolean checkArgsType(String[] args){
    for(int i = 0; i < NUM_ARGUMENTS; i++){
      if(!args[i].matches("\\d+")){
        System.out.printf("ERROR: Expected a positive number, got: %s\n", args[i]);
        return false;
      }
    }
    return true;
  }



  //Runs a set of tests to make sure the arrays are sorted properly
  public static void checkCorrectArrays(int[] sequentialNumbers, int[] parallelNumbers){
    System.out.print("\u001B[31m");
    if(!checkArraySortedCorrectly(sequentialNumbers)) NO_ERRORS = false;
    if(!checkArraySortedCorrectly(parallelNumbers)) NO_ERRORS = false;
    if(!checkBothArraysEqual(sequentialNumbers, parallelNumbers)) NO_ERRORS = false;

    if(NO_ERRORS) System.out.println("\u001B[32m\tALL TESTS PASSED!");
    System.out.print("\u001B[0m");
  }

  /**
   * Checks that all the numbers in the given array is correctly sorted.
   * This implies that every number preceeding the next one is less or equal.
   * This method should be called twice: Once for the sequential and once for
   * the parallel solution.
   *
   * If an error is encountered, the index is printed, and the method returns.
   *
   * @param int[] sortedNumbers: All the sorted numbers from the radix sort
   * @return boolean: True if equal, false otherwise
   **/
  private static boolean checkArraySortedCorrectly(int[] sortedNumbers){
    for(int i = 1; i < sortedNumbers.length; i++){
      if(sortedNumbers[i-1] > sortedNumbers[i]){
        System.out.printf("ERROR: Array not sorted correctly on index %d: %d > %d!\n", i, sortedNumbers[i-1], sortedNumbers[i]);
        return false;
      }
    }
    return true;
  }


  /**
   * Checks that all the numbers sorted in the sequential solution is equal to
   * all the numbers in the parallel solution.
   * @param int[] sequentialNumbers: All the numbers sorted in the sequential solution
   * @param int[] parallelNumbers: All the numbers sorted in the parallel solution
   * @return boolean: True if equal, false otherwise
   **/
  private static boolean checkBothArraysEqual(int[] sequentialNumbers, int[] parallelNumbers){
    for(int i = 0; i < sequentialNumbers.length; i++){
      if(sequentialNumbers[i] != parallelNumbers[i]){
        System.out.printf("ERROR: Arrays not equal on index %d: %d != %d\n", i, sequentialNumbers[i], parallelNumbers[i]);
        return false;
      }
    }
    return true;
  }
}
