1. RUNNING THE PROGRAM

	1.1: Use "java [-Xmx4096m] Oblig1 <n> <k> [p|-p|parallell] [nc|-nc|nocontrol]" 
	     to run this program

	1.2: Use the 'p' flag to run the program parallell

	1.3: Use the 'nc' flag to disable Array.Sort checking.

	     NOTE! You will not know if nums has been sorted correctly, but as the
	     program works now, this shouldn't be an issue.

	     This flag is excellent for testing large n without waiting for Arrays.sort

	1.4: You can also change the variables LOOPS and CORES in Oblig1.java to respectively
	     change the amount of times the program runs and how many threads to be made.
	     
	     NOTE! Loop #1 will most certainly be slower than any other runs.


2. RETURN VALUES

	2.1: The program prints the runtime of both the sorting and the control test.
	     The sorted array is not shown.

	2.2: The first number is the median time, the second the fastest time and the third
	     the slowest time.


3. RUNTIMES

	
	3.1: Tested on an Ubuntu 18.04.1 64 bit computer

	3.2: CPU: Intel(R) Core(TM) i5 6200U @ 2.30GHz (4 cores)

	3.3: Each test has been run 7 times, showing the median result.



	k = 20		Arrays.sort()		Sequential		Parallell
	1 000		       0 ms		  0 ms			    0 ms
	10 000		       1 ms		  0 ms			    0 ms
	100 000		      13 ms		  0 ms			    0 ms
	1 000 000	      93 ms		  0 ms			    1 ms
	10 000 000	    1138 ms 		  4 ms			    9 ms
	100 000 000	   14142 ms		 47 ms 			   97 ms
	

	
	k = 100		Arrays.sort()		Sequential		Parallell
	1 000		       0 ms		  0 ms			    0 ms
	10 000		       0 ms		  0 ms			    2 ms
	100 000		      12 ms		  0 ms			    2 ms
	1 000 000	      94 ms		  0 ms			    2 ms
	10 000 000	    1124 ms		  5 ms			   11 ms
	100 000 000	   14256 ms		 48 ms			  100 ms
	
	
	3.4: Tested on a Windows 10 Pro 64 bit computer
	
	3.5: CPU: Intel Core i5-4670K @ 3.40 GHz (4 cores)
	
	3.6: Each test has been run 7 times, showing the median result.
	
	
	
	k = 20		Arrays.sort()		Sequential		Parallell
	1 000		   0 ms			  0 ms			   0 ms	
	10 000		   0 ms			  0 ms		   	   0 ms	
	100 000		   5 ms			  0 ms			   1 ms				
	1 000 000	  70 ms			  1 ms			   2 ms
	10 000 000	 835 ms			 19 ms			  10 ms
	100 000 000	9552 ms			193 ms			  83 ms
	
	
	k = 100		Arrays.sort()		Sequential		Parallell
	1 000		   0 ms			  0 ms			   0 ms
	10 000		   0 ms			  0 ms			   0 ms
	100 000		   5 ms			  0 ms			   1 ms
	1 000 000	  71 ms			  2 ms			   2 ms
	10 000 000	 838 ms			 19 ms			   9 ms
	100 000 000	9583 ms			192 ms			  73 ms


	
4. CONCLUSION (UPDATED 1st of March 2019)

	4.1: This new version has a lot of improvements. First of all the program has been
	     easier to read and gotten rid of redundant code. Secondly the parallell solution
	     has been made alot more reliable and faster. However I am still not able to get
	     a speedup > 1 on the first computer, but once I tried with another PC with a faster
	     CPU, I got a speedup of 2.32x (N = 100 000 000, K=20) and 2.63x (K = 100). 

	4.2: This solution uses a global variable: globalMin, which keeps track of what is
 	     the current smallest number in nums[k-numThreads ... k-1]. With this I also need
	     to keep track of which thread is keeping this number; globalMinId. Whenever a smaller
	     number is found, this number is replaced. The program then needs to update the 
	     globalMin and globalMinId values. 

	4.3: I need to use a semophore when updating the global variables. That way, the globalMin
	     won't be read and changed into a smaller value while another thread is still updating.

	4.4: I can't seem to get a better speedup on PC 1, so I have deducted it must be hardware.
	     My initial thought was that the semophore is causing the time loss, but even when
	     commenting it out (this will give the wrong answer!), the program actually used more
	     time! I suppose the reason why is the threads keep updating globalMin, sometimes to
	     a smaller value, thus running the method setGlobalMin() more often.
