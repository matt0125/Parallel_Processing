// Matthew Daley, 5154297
// COP 4520, Spring 2023

// =============================================================================
//                                  Prime.java
//            Multi-threaded approach finding primes [2 - MAX_PRIME]
// =============================================================================

// Input: no input, to modify range of primes, modify Prime's MAX_PRIME variable

// Output: primes.txt
//   <execution time> <total number of primes> <sum of all primes>
//   <list of top 10 primes from lowest to highest>


import java.util.concurrent.atomic.*;
import java.util.*;
import java.io.*;

public class Prime
{
  // Modify these numbers for testing
  private static final int MAX_PRIME = (int) 1E8;
  private static final int NUM_THREADS = 8;

  public static void main(String [] args) throws
                                              InterruptedException, IOException
  {
    int numPrimes = 0;
    long primeSum = 0;
    // Stores location of prime numbers
    boolean[] primes = new boolean[MAX_PRIME+1];
    int[] topTen = new int[10];
    // Counter shared across threads
    AtomicInteger counter = new AtomicInteger(2);

    // Initalized to use a sieve for the primes
    Arrays.fill(primes, true);


    Thread[] threads = new Thread[NUM_THREADS];

    for (int i = 0; i < NUM_THREADS; i++)
    {
      threads[i] = new Thread(new SThread(counter, primes, MAX_PRIME));
    }

    // Start the execution clock.
    long startTime = System.nanoTime();

    // Start the threads and wait for them to finish
    for (Thread s : threads)
    {
      s.start();
    }

    for (Thread s : threads)
    {
      s.join();
    }

    // Tallies total number of primes and their sums
    for (int i = 2; i < (MAX_PRIME+1); i++)
    {
      if (primes[i] == true)
      {
        numPrimes++;
        primeSum += i;
      }
    }

    int t = 9; // topTen index
    // Adds the last 10 primes to the topTen array
    for (int i = 0; t >= 0; i++)
    {
      if(primes[MAX_PRIME - i] == true)
      {
        topTen[t] = (MAX_PRIME - i);
        t--;
      }
    }

    // Stop the clock
    long endTime = System.nanoTime();

    // Get the time in milliseconds
    double totalTime = (endTime - startTime) / 1E9;

    // Write to output file and close
    File file = new File("primes.txt");
    FileWriter fr = new FileWriter(file);

    fr.write(totalTime + " " +
             numPrimes + " " +
             primeSum + " " +
             Arrays.toString(topTen));

    fr.close();

  }
}

class SThread implements Runnable
{
  private AtomicInteger counter; // Shared counter
  private boolean[] array; // Shared boolean array
  private int maxPrime;

  public SThread(AtomicInteger atomicInt, boolean[] numbers, int maxNum)
  {
    this.counter = atomicInt;
    this.array = numbers;
    this.maxPrime = maxNum;
  }

  public void run()
  {
    int currentNumber;
    int root = (int) Math.sqrt(this.maxPrime);

    // Take a number from shared atomic integer and mark each of its
    // subsequent multiples as false, starting at its square to avoid
    // repeated computation
    while((currentNumber = this.counter.getAndIncrement()) <= root)
    {
      if (this.array[currentNumber] == true)
      {
        for (int i = currentNumber; i*currentNumber <= this.maxPrime; i++)
        {
          this.array[currentNumber*i] = false;
        }
      }
    }
  }
}
