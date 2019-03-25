/**
* @author Magnus Espeland <magnuesp>
* @date 2019.03.06
*/

class SequentialSieve {
    private int[] primes;
    private byte[] byteArray;
    private int n;
    private int primesCounter;

    public SequentialSieve(int n) {
        this.n = n;
        int cells = n / 16 + 1;
        byteArray = new byte[cells];
    }

    public int[] findPrimes() {
        findFirstPrimes();
        countRestPrimes();
        gatherPrimes();

        return primes;
    }

    void findFirstPrimes() {
        primesCounter = 1;
        int currentPrime = 3;
        int squareRootN = (int)Math.sqrt(n);

        while(currentPrime != 0 && currentPrime <= squareRootN) {
            traverse(currentPrime);
            currentPrime = findNextPrime(currentPrime + 2);
            primesCounter++;
        }
    }

    void traverse(int p) {
        for (int i = p*p; i < n; i += p * 2) {
            flip(i);
        }
    }

    void flip(int i) {
        if (i % 2 == 0) {
            return;
        }

        int byteCell = i / 16;
        int bit = (i / 2) % 8;

        byteArray[byteCell] |= (1 << bit);
    }

    int findNextPrime(int startAt) {
        for (int i = startAt; i < n; i += 2) {
            if(isPrime(i)) {
                return i;
            }
        }
        return 0;
    }

    boolean isPrime(int i) {
        if((i % 2) == 0) {
            return false;
        }

        int byteCell = i / 16;
        int bit = (i / 2) % 8;

        return (byteArray[byteCell] & (1 << bit)) == 0;
    }

    void countRestPrimes() {
        int startAt = (int)Math.sqrt(n) + 1;

        if (startAt % 2 == 0) {
            startAt++;
        }

        startAt  = findNextPrime(startAt);
        while(startAt != 0) {
            primesCounter++;
            startAt = findNextPrime(startAt+2);
        }
    }

    void gatherPrimes() {
        primes = new int[primesCounter];
        primes[0] = 2;

        int currentPrime = 3;
        for (int i = 1; i < primesCounter; i++) {
            primes[i] = currentPrime;
            currentPrime = findNextPrime(currentPrime+2);
        }
    }



    /**
    * @author Daniel Dutta <danieldu@student.matnat.uio.no>
    * @date 2019.03.19
    * Factorizes the 100 biggest numbers
    */
    void factorizeLargest(){
      Oblig3Precode precode = new Oblig3Precode(n);
      for (int i = n*n-1; i > (n*n)-101; i--){
        factorize(i, precode);
      }
      //precode.writeFactors();
    }

    /**
     * Finds all factors to a number, starting from the first prime (2) to
     * sqrt(N). Assumes all primes have been found and stored in primes[].
     *
     * @param int num: The number to be factorized
     * @param Oblig3Precode precode: Adds every found factor to the precode's factorlist
     */
    private void factorize(int num, Oblig3Precode precode){
      int original = num;

      for(int i = 0; i < primes.length/* && primes[i] < Math.sqrt(num)*/; i++){
        if(num % primes[i] == 0){
          precode.addFactor(original, primes[i]);
          num = num/primes[i];
          i--;
        }
      }
      if(num > 1) precode.addFactor(original, num);
    }
}
