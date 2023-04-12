import java.util.Arrays;
import java.util.Random;

public class ATRM
{
    // Program settings
    public static final int NUM_SENSORS = 8;
    public static final int MINUTE_LEN_IN_MILLIS = 1000;
    public static int numHours = 2;

    public static void main(String[] args)
    {
        if(args.length > 0)
        {
            ATRM.numHours = Integer.parseInt(args[0]);
        }

        Thread[] sensors = createThreads(NUM_SENSORS);

        startThreads(sensors);
        joinThreads(sensors);
    }

    private static Thread[] createThreads(int numThreads)
    {
        Thread[] sensors = new Thread[numThreads];
        SharedMemory memory = new SharedMemory();

        for (int i = 0; i < numThreads; i++)
        {
            sensors[i] = new Thread(new Sensor(numHours, MINUTE_LEN_IN_MILLIS, memory, i));
        }

        return sensors;
    }   

    private static void startThreads(Thread[] threads)
    {
        for(int i = 0; i < threads.length; i++)
        {
            threads[i].start();
        }
    }

    private static void joinThreads(Thread[] threads)
    {
        for(int i = 0; i < threads.length; i++)
        {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


}

class SharedMemory
{
    // Stores the greatest difference over 10 minute intervals
    public int[] intervalDiference;
    public volatile int largestInterval;
    public volatile int smallestInterval;
    
    // Stores the largest number in the lowest 5 array
    // Used to avoid having to iterate though array every time to read
    public volatile int largestOfTheLowestFive;
    public volatile int largestOfTheLowestFiveIndex;
    public int[] lowestFive;

    // Stores the smallest number in the largest 5 array
    // Used to avoid having to iterate though array every time to read
    public volatile int smallestOfTheHighestFive;
    public volatile int smallestOfTheHighestFiveIndex;
    public int[] highestFive;
    


    public SharedMemory()
    {
        this.intervalDiference = new int[6];
        this.lowestFive = new int[5];
        this.highestFive = new int[5];

        intervalReset();
        hourReset();
    }

    public synchronized void setLargestInterval(int temp)
    {
        if(temp > this.largestInterval)
        {
            this.largestInterval = temp;
        }
    }

    public synchronized void setSmallestInterval(int temp)
    {
        if(temp < this.smallestInterval)
        {
            this.smallestInterval = temp;
        }
    }

    public synchronized void logInterval(int interval)
    {
        this.intervalDiference[interval] = this.largestInterval - this.smallestInterval;
        intervalReset();
    }

    private void intervalReset()
    {
        this.largestInterval = -100;
        this.smallestInterval = 70;
    }

    public void logHour(int hour)
    {
        int largestDifference = -1;
        int index = -1;

        for(int i = 0; i < 6; i++)
        {
            if (this.intervalDiference[i] > largestDifference)
            {
                largestDifference = this.intervalDiference[i];
                index = i;
            }
        }

        int lowInterval = (index * 10);
        int highInterval = (index * 10) + 10;

        System.out.println("=========================================================================");
        System.out.printf("Hour %02d log:\n", (hour+1));


        System.out.printf("The largest 10 minute interval difference was recorded from %2d to %2d: %3d\n", lowInterval, highInterval, this.intervalDiference[index]);

        System.out.println("Five lowest temperatures recorded:");
        for(int i = 0; i < 5; i++)
        {
            System.out.printf("%5d ", this.lowestFive[i]);
        }
        System.out.println();


        System.out.println("Five highest temperatures recorded:");
        for(int i = 0; i < 5; i++)
        {
            System.out.printf("%5d ", this.highestFive[i]);
        }
        System.out.println();
        System.out.println("=========================================================================");

        hourReset();

    }

    private void hourReset()
    {
        Arrays.fill(this.highestFive, -100);
        Arrays.fill(this.lowestFive, 70);

        this.largestOfTheLowestFive = 70;
        this.smallestOfTheHighestFive = -100;
    }

    public synchronized void logLowHour(int temp)
    {
        if (temp < this.largestOfTheLowestFive)
        {
            int holder = 0;
            for(int i = 4; i >= 0; i--)
            {
                if (temp < this.lowestFive[i])
                {
                    holder = this.lowestFive[i];
                    this.lowestFive[i] = temp;
                    temp = holder;
                }
            }

            this.smallestOfTheHighestFive = this.highestFive[0];
        }
    }

    public synchronized void logHighHour(int temp)
    {
        if (temp > this.smallestOfTheHighestFive)
        {
            int holder = 0;
            for(int i = 4; i >= 0; i--)
            {
                if (temp > this.highestFive[i])
                {
                    holder = this.highestFive[i];
                    this.highestFive[i] = temp;
                    temp = holder;
                }
            }

            this.smallestOfTheHighestFive = this.highestFive[0];
        }
    }
}

class Sensor implements Runnable
{
    private SharedMemory sharedMemory;
    private final int NUM_HOURS;
    private final int MINUTE_LEN_IN_MILLIS;
    private Random rand;
    private int sensorNumber;

    public Sensor(int NUM_HOURS, int minuteLengthMillis, SharedMemory memory, int sensorNumber) 
    {
        this.NUM_HOURS = NUM_HOURS;
        this.MINUTE_LEN_IN_MILLIS = minuteLengthMillis;
        this.sharedMemory = memory;
        this.sensorNumber = sensorNumber;

        this.rand = new Random();
    }
    
    @Override
    public void run()
    {
        while(System.currentTimeMillis() % MINUTE_LEN_IN_MILLIS != 0)
        {
            // Spin until start of hour
        }

        if(sensorNumber == 0)
            moduleSensor();
        else
            basicSensor();
    }

    private void moduleSensor()
    {
        for (int hour = 0; hour < NUM_HOURS; hour++)
        {
            for (int minute = 0; minute < 60; minute++)
            {
                logData();
                
                if(minute % 10 == 0)
                {
                    sharedMemory.logInterval(minute/10);
                }

                spin();
            }
            sharedMemory.logHour(hour);
        }
    }

    private void basicSensor()
    {
        for (int hour = 0; hour < NUM_HOURS; hour++)
        {
            for (int minute = 0; minute < 60; minute++)
            {
                logData();
                spin();
            }
        }

    }

    private void logData()
    {
        // Generates a psuedo random number [-100, 70]
        int nextReading = rand.nextInt(171) - 100;

        if(nextReading < sharedMemory.smallestInterval)
            sharedMemory.setSmallestInterval(nextReading);
        if(nextReading > sharedMemory.largestInterval)
            sharedMemory.setLargestInterval(nextReading);
        if(nextReading < sharedMemory.largestOfTheLowestFive)
            sharedMemory.logLowHour(nextReading);
        if(nextReading > sharedMemory.smallestOfTheHighestFive)
            sharedMemory.logHighHour(nextReading);
    }

    private void spin()
    {
        // Necessary to avoid iterating multiple times within the same millisecond
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        while(System.currentTimeMillis() % MINUTE_LEN_IN_MILLIS != 0)
        {
            // Spin
        }
    }

}