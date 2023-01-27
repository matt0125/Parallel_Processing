# Parallel Processing - COP 4520

<a href="https://www.linkedin.com/in/matthew--daley/"><b>Matthew Daley</b></a>, Spring 2023 <br>
Professor Juan Parra

<h4>Table of contents</h4>
<ul>
  <li>
    <a href="#primejava"><b>Assignment 1-2: Primes</b></a>
    <ul>
      <li><a href="#summary-of-approach">Summary of Approach</a></li>
      <li><a href="#experimental-evaluation">Experimental Evaluation</a></li>
    </ul>
  </li>
</ul>

## Prime.java

### Problem
Your non-technical manager assigns you the task to find all primes between 1 and 10^8. The assumption is that your company is going to use a parallel machine that supports eight concurrent threads. Thus, in your design you should plan to spawn 8 threads that will perform the necessary computation. Your boss does not have a strong technical background but she is a reasonable person. Therefore, she expects to see that the work is distributed such that the computational execution time is approximately equivalent among the threads. Remember, that your company cannot afford a supercomputer and rents a machine by the minute, so the longer your program takes, the more it costs.

### Solution
To compile and run:
  ```sh
  javac Prime.java
  ```
To run:
  ```sh
  java Prime
  ```

Output:
  ```
  primes.txt:
      <execution time> <total number of primes> <sum of all primes> <list of top 10 primes from lowest to highest>
  ```

### Summary of approach:
  Correctness and efficiency of design:

  This problem was my first experience with using threads. With that, I started with an average prime checking algorithm that ran in O(n) and split the computation among the 8 threads.

  My solution utilizes a binary array to represent every number from 0 to the max prime [inclusive]. Each thread then takes a number from their shared atomic integer and marks each of its subsequent multiples, starting with its square, as not primes since by the definition of prime, a number is not prime if it has more than two factors [itself and 1].

  Once the threads finish, the binary array is then looped over, tallying all of the true values and adding its index to a sum. The last ten true indices are then added to the top ten array and printed to the file created.

  This solution is far greater than my original attempt, cutting the time down from 38 seconds to .415 milliseconds. I believe this algorithm is the most efficient as it utilizes aspects of the O(sqrt n) solution and almost resembles a dynamic programming solution. As seen in the average execution time table below, my solution is most efficient when the number of threads and primes are proportional.


### Experimental evaluation:

When taking the average of each set of primes [10^4, 10^6, 10^8] compared to different thread counts [1, 2, 4, 8], it is apparent that, on average, my solution is most efficient at 10^8 with 8 threads - cutting the execution time in half compared to one thread thus saving the company the most money.

The standard deviation measures the number of factors each thread used. The way my algorithm works, it wasn't fair to measure how many numbers each thread marked false because the first thread always marked half the numbers with factors of 2. With that, I believe the differences in the numbers checked is the best way to measure the separation of work.

<div align="center">
<table>
  <tbody>
    <tr>
      <td>
        <table>
          <thead>
            <tr>
              <th colspan="4">Average Execution Time</th>
            </tr>
          </thead>
          <tbody>
            <tr>
              <td> Thread Count </td>
              <td> 10^4</td>
              <td> 10^6 </td>
              <td> <b>10^8</b> </td>
            </tr>
            <tr>
              <td> 1 </td>
              <td> .62 ms</td>
              <td> 11.43 ms </td>
              <td> 821.79 ms </td>
            </tr>
            <tr>
              <td> 2 </td>
              <td> .61 ms </td>
              <td> 11.90 ms </td>
              <td> 609.88 ms </td>
            </tr>
            <tr>
              <td> 4 </td>
              <td> .75 ms </td>
              <td> 16.49 ms </td>
              <td> 551.12 ms </td>
            </tr>
            <tr>
              <td> <b>8</b> </td>
              <td> .90 ms </td>
              <td> 15.44 ms </td>
              <td> <b>415.48 ms</b> </td>
            </tr>
          </tbody>
        </table>
      </td>
      <td>
        <table>
          <thead>
            <tr>
              <th colspan="4">Average Standard Deviation of Numbers Checked</th>
            </tr>
          </thead>
          <tbody>
            <tr>
              <td> Thread Count </td>
              <td> 10^4 [99]</td>
              <td> 10^6 [999]</td>
              <td> <b>10^8</b> [9999] </td>
            </tr>
            <tr>
              <td> 1 </td>
              <td> 0 </td>
              <td> 0 </td>
              <td> 0 </td>
            </tr>
            <tr>
              <td> 2 </td>
              <td> 2.5 </td>
              <td> 10.5 </td>
              <td> 29.7 </td>
            </tr>
            <tr>
              <td> 4 </td>
              <td> 10.25 </td>
              <td> 18.65 </td>
              <td> 42.12 </td>
            </tr>
            <tr>
              <td> <b>8</b> </td>
              <td> 7.38 </td>
              <td> 36.16 </td>
              <td> <b>92.72</b> </td>
            </tr>
          </tbody>
        </table>
      </td>
    </tr>
  </tbody>
</table>
</div>
