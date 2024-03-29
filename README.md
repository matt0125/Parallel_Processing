# Parallel Processing - COP 4520

<a href="https://www.linkedin.com/in/matthew--daley/"><b>Matthew Daley</b></a>, Spring 2023 <br>
Professor Juan Parra

<h4>Table of contents</h4>
<ul>
  <li>
    <a href="#primejava"><b>Assignment 1-2: Primes</b></a>
    <ul>
      <li><a href="#summary-of-approach-3">Summary of Approach</a></li>
      <li><a href="#experimental-evaluation-1">Experimental Evaluation</a></li>
    </ul>
  <li>
    <a href="#minotaursbirthdayjava"><b>Assignment 2-1: Minotaur's Birthday Party</b></a>
    <ul>
      <li><a href="#summary-of-approach-1">Summary of Approach</a></li>
      <li><a href="#correctness-1">Correctness</a></li>
     </ul>
  </li>
  <li>
    <a href="#minotraurscrystalvasejava"><b>Assignment 2-2: Minotaur's Birthday Party</b></a>
    <ul>
      <li><a href="#summary-of-approach-2">Summary of Approach</a></li>
      <li><a href="#experimental-evaluation">Experimental Evaluation</a></li>
     </ul>
  </li>
  <li>
    <a href="#atrmjava"><b>Assignment 3-2: Atmospheric Temperature Reading Module </b></a>
    <ul>
      <li><a href="#summary-of-approach">Summary of Approach</a></li>
      <li><a href="#correctness">Correctness</a></li>
     </ul>
  </li>
    <li>
    <a href="#presentsjava"><b>Assignment 3-1: Presents</b></a>
  </li>
  </li>
</ul>

## Presents.java

### Problem
The Minotaur’s birthday party was a success. The Minotaur received a lot of presents from his guests. The next day he decided to sort all of his presents and start writing “Thank you” cards. Every present had a tag with a unique number that was associated with the guest who gave it. Initially all of the presents were thrown into a large bag with no particular order. The Minotaur wanted to take the presents from this unordered bag and create a chain of presents hooked to each other with special links (similar to storing elements in a linked-list). In this chain (linked-list) all of the presents had to be ordered according to their tag numbers in increasing order. The Minotaur asked 4 of his servants to help him with creating the chain of presents and writing the cards to his guests. Each servant would do one of three actions in no particular order: 1. Take a present from the unordered bag and add it to the chain in the correct location by hooking it to the predecessor’s link. The servant also had to make sure that the newly added present is also linked with the next present in the chain. 2. Write a “Thank you” card to a guest and remove the present from the chain. To do so, a servant had to unlink the gift from its predecessor and make sure to connect the predecessor’s link with the next gift in the chain. 3. Per the Minotaur’s request, check whether a gift with a particular tag was present in the chain or not; without adding or removing a new gift, a servant would scan through the chain and check whether a gift with a particular tag is already added to the ordered chain of gifts or not. As the Minotaur was impatient to get this task done quickly, he instructed his servants not to wait until all of the presents from the unordered bag are placed in the chain of linked and ordered presents. Instead, every servant was asked to alternate adding gifts to the ordered chain and writing “Thank you” cards. The servants were asked not to stop or even take a break until the task of writingcards to all of the Minotaur’s guests was complete. After spending an entire day on this task the bag of unordered presents and the chain of ordered presents were both finally empty! Unfortunately, the servants realized at the end of the day that they had more presents than “Thank you” notes. What could have gone wrong? Can we help the Minotaur and his servants improve their strategy for writing “Thank you” notes? Design and implement a concurrent linked-list that can help the Minotaur’s 4 servants with this task. In your test, simulate this concurrent “Thank you” card writing scenario by dedicating 1 thread per servant and assuming that the Minotaur received 500,000 presents from his guests

### Solution

To compile and run:
  ```sh
  javac ATRM.java
  ```
To run:
  ```sh
  java Presents
  ```
Output ->  **Console**

### Summary of approach:

My approach to designing and implementing a wait free linked list found inspiration from the book. To accomplish this, I created my own linked list which heavily relied on atomic references and never actually disconnecting nodes. This ensures that no nodes ever get lost and no thread reads the wrong value thinking its hit the end of the list.

One thing that I thought was weird in the instructions, at least the way I interpreted them, was how the third action was included but the servents were never instructed to use them. In that case, I included the method, made sure it worked and commented out lines 201 and 202 which use them.

## ATRM.java

### Problem

You are tasked with the design of the module responsible for measuring the atmospheric temperature of the next generation Mars Rover, equipped with a multicore CPU and 8 temperature sensors. The sensors are responsible for collecting temperature readings at regular intervals and storing them in shared memory space. The atmospheric temperature module has to compile a report at the end of every hour, comprising the top 5 highest temperatures recorded for that hour, the top 5 lowest temperatures recorded for that hour, and the 10-minute interval of time when the largest temperature difference was observed. The data storage and retrieval of the shared memory region must be carefully handled, as we do not want to delay a sensor and miss the interval of time when it is supposed to conduct temperature reading. Design and implement a solution using 8 threads that will offer a solution for this task. Assume that the temperature readings are taken every 1 minute. In your solution, simulate the operation of the temperature reading sensor by generating a random number from -100F to 70F at every reading.

### Solution

To compile and run:
  ```sh
  javac ATRM.java
  ```
To run:
  ```sh
  java ATRM {int h}
  ```
  ```
  Args:
  {int h} optional = number of hours to log; defaults to 3
  ```
Output ->  **Console**

### Summary of approach:

Due to the nature of the problem, I could not risk missing any temperature readings by sleeping any of the threads. Because of this, I utalized spin locks and made sure that any operations I performed were within the temperature reading intervals. While the current time wasnt a whole number value of the temperature reading interval, each thread would spin and wait until the next time it was supposed to read a temperature. This method gives the thread room to do any operations they might need to do.

While using a spin lock does work well in this case, it's not the most processor efficient solution. For example, one thread using a spin lock to keep track of time and notifing other threads using a shared object would be a more efficient solution and is the other thing I might change if I were to do it again.

### Correctness

To ensure data protection, I used a mix of shared volatile integers as well as synchronized methods. Any data the threads would need to read such as the current largest number in the 5 smallest numbers array was labeled as volatile. When a thread wants to add a number to that array, it used a synchronized method and re-checked the condition that lead it to call that method before it did anything.

By only using a spin lock, I guarentee that my program will eventually finish. This is due to my condition in the spin lock being dependant on System.currentTimeMillis(). Another fail-safe that I have in place is any time a thread calls to loop, I make it wait a single millisecond. This is to ensure that no thread iterates more than its meant to. By letting the time increase by a millisecond, the thread cannot break out of the spin lock twice at the same time.



## MinotaursBirthday.java

### Problem

The Minotaur invited N guests to his birthday party. When the guests arrived, he made the following announcement.

The guests may enter his labyrinth, one at a time and only when he invites them to do so. At the end of the labyrinth, the Minotaur placed a birthday cupcake on a plate. When a guest finds a way out of the labyrinth, he or she may decide to eat the birthday cupcake or leave it. If the cupcake is eaten by the previous guest, the next guest will find the cupcake plate empty and may request another cupcake by asking the Minotaur’s servants. When the servants bring a new cupcake the guest may decide to eat it or leave it on the plate.

The Minotaur’s only request for each guest is to not talk to the other guests about her or his visit to the labyrinth after the game has started. The guests are allowed to come up with a strategy prior to the beginning of the game. There are many birthday cupcakes, so the Minotaur may pick the same guests multiple times and ask them to enter the labyrinth. Before the party is over, the Minotaur wants to know if all of his guests have had the chance to enter his labyrinth. To do so, the guests must announce that they have all visited the labyrinth at least once.

Now the guests must come up with a strategy to let the Minotaur know that every guest entered the Minotaur’s labyrinth. It is known that there is already a birthday cupcake left at the labyrinth’s exit at the start of the game. How would the guests do this and not disappoint his generous and a bit temperamental host?

Create a program to simulate the winning strategy (protocol) where each guest is represented by one running thread. In your program you can choose a concrete number for N or ask the user to specify N at the start.

### Solution

To compile and run:
  ```sh
  javac MinotaursBirthday.java
  ```
To run:
  ```sh
  java MinotaursBirthday {int x}
  ```
  ```
  Args:
  {int x} = number of guests (threads) that attend the party
  -p -> turns off printing the party guests that enter the labyrnith
  -r -> turns off assigning a random amount of time (5-15 ms) each guest takes in the labyrinth (defaults to 100 each time)
  ```
Output ->  **Console**

### Summary of approach:

My approach strongly relies on the fact that each guest can see who enters the labyrinth. Since each guest can both request and eat a cupcake at the end of the maze, they all planned on doing so every time they enter. Another thing about the guests is they have a really good memory. Every time they see someone enther the maze they take note of it and after they've seen everyone enter the maze, they know they've already made the manotaur happy so the party ends right then and there.

With that, each thread has its own binary array of length numGuests. They are almost constantly checking to see who the manotaur picked next and they update thier memory every time they check whos next. If they were picked, they enter the maze, request a new cupcake and eat it before exiting the maze, unlocking it and letting the manotaur know the maze is ready for a new guest. The manotaur always picks a new guest after someone leaves until the guests collectively tell him its over.

My program scales well and can handle over 1000 guests, though it is not reccomended due to execution time. One of the challenges of this assignment was the nature that the threads cannot talk to each other. While yes, they do share an atomic integer, it signifies every guest can see who goes into the maze at all times. They each still have their own notes of who went in to the maze and still have to notifiy the manotaur that they exited it.

### Correctness:
Knowing that I am using wait and notify, I know that threads arent wasting computation power continuously checking to see if another thread entered the maze. Also, with this set up, I know each thread is taking notes of who went into the maze and checking to see if that was the last person that needed to eat thier cupcake before the party was over the instance the next person entered the maze. I also know this wouldn't be a problem ending early because the last person will not end the party until they are out of the maze.

## MinotraursCrystalVase.java

### Problem

The Minotaur decided to show his favorite crystal vase to his guests in a dedicated showroom with a single door. He did not want many guests to gather around the vase and accidentally break it. For this reason, he would allow only one guest at a time into the showroom. He asked his guests to choose from one of three possible strategies for viewing the Minotaur’s favorite crystal vase:
1) Any guest could stop by and check whether the showroom’s door is open at any time and try to enter the room. While this would allow the guests to roam around the castle and enjoy the party, this strategy may also cause large crowds of eager guests to gather around the door. A particular guest wanting to see the vase would also have no guarantee that she or he will be able to do so and when.
2) The Minotaur’s second strategy allowed the guests to place a sign on the door indicating when the showroom is available. The sign would read “AVAILABLE” or “BUSY.” Every guest is responsible to set the sign to “BUSY” when entering the showroom and back to “AVAILABLE” upon exit. That way guests would not bother trying to go to the showroom if it is not available.
3) The third strategy would allow the quests to line in a queue. Every guest exiting the room was responsible to notify the guest standing in front of the queue that the showroom is available. Guests were allowed to queue multiple times.

Which of these three strategies should the guests choose? Please discuss the advantages and disadvantages.
Implement the strategy/protocol of your choice where each guest is represented by 1 running thread. You can choose a concrete number for the number of guests or ask the user to specify it at the start.

### Solution

To compile and run:
  ```sh
  javac MinotaursBirthday.java
  ```
To run:
  ```sh
  java MinotaursBirthday {int x} {int y}
  ```
  ```
  Args:
  int x = number of guests (threads) that attend the party
  int y = solution number as listed in Problem above
  -p -> turns off printing the party guests that enter the labyrnith
  ```
Output ->  **Console**

### Summary of approach:

To solve this problem, I decided to implement all three solutions, initally thinking option 2 would be the best one. Each solution was a little different in nature:
1) For the first solution, I implemented a single lock shared between all of the threads (guests) and it was a fist come first serve situation. While yes, this might cause a lot of people waiting outside of the door, this ensures that everyone will eventually get in on their first try
2) For the second solution, I also implemented a single shared lock between all of the guests but this time I used a random try-lock. This way, guests could swing by and see if the sign was "AVAILABLE" and continue on thier day. The problem with this is there are times when no one is by the door when the last person flips the sign causing some lost time inbetween people being in awe - not good for runtime.
3) For the third solution, I created relationships between pairs of threads in order of which they appeared in the queue. Doing this, each guest had an object they shared with the person infront of them and one with the person behind them. I believe this is one of the only ways threads can notify the next person in line utalizing wait and notify. This ended up being almost exactly how a lock acts - just adding the computation time of creating those relationships and enforcing them.


### Experimental evaluation:

When timing each approach, I discovered some interesting results. The third method is much btter compared to the others at first in smaller numbers and then as it shoots up in time, the first method breaks free as the winner. If I had to guess why this is, I would have to say the time taken to create the objects required for option 3 grows faster than the time taken to wait for a lock in option 1. These numbers were curated by taking the average of 5 runs of each method. While they are interesting numbers, the true answer is apparent - method 1 is the best option when it comes to larger gatherings and method 3 is the best when the party is a little more personal.

<table>
          <thead>
            <tr>
              <th colspan="4">Average Execution Time Per Method</th>
            </tr>
          </thead>
          <tbody>
            <tr>
              <td> Guest Count </td>
              <td> 8 </td>
              <td> 64 </td>
              <td> <b>100</b> </td>
            </tr>
            <tr>
              <td> Method 1 </td>
              <td> 102 ms</td>
              <td> 111 ms </td>
              <td> 128 ms </td>
            </tr>
            <tr>
              <td> Method 2 </td>
              <td> 68 ms </td>
              <td> 172 ms </td>
              <td> 212 ms </td>
            </tr>
            <tr>
              <td> Method 3 </td>
              <td> 24 ms </td>
              <td> 115 ms </td>
              <td> 172 ms </td>
            </tr>
          </tbody>
        </table>
      </td>
</table>

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
