1. RUNNING THE PROGRAM

	1.1: Use "java -ea <n> <k>" to run this program

	1.2: The -ea flag is used to make the assert test in the solveControl method work.


2. RETURN VALUES

	2.1: The program prints the runtime of both the sorting and the control test.
	     The sorted array is not shown.


3. RUNTIMES

	
	3.1: Tested on a Windows 7 64 bit computer

	3.2: CPU: Intel(R) Core(TM) i5 750 @ 2.67GHz (4 cores)

	3.3: Each test has been run 7 times, showing the median result.



	k = 20		Arrays.sort()		Sequential	Parallell
	1 000		    0 ms		  0 ms
	10 000		    1 ms		  0 ms
	100 000		   15 ms		  0 ms
	1 000 000	   96 ms		  1 ms
	10 000 000	 1106 ms 		 10 ms
	100 000 000	15866 ms		101 ms 
	


	k = 100		Arrays.sort()		Sequential	Parallell
	1 000		    0 ms		 0 ms
	10 000		    1 ms		 0 ms
	100 000		   15 ms		 0 ms
	1 000 000	   94 ms		 1 ms
	10 000 000	 1113 ms		11 ms
	100 000 000	15965 ms		98 ms