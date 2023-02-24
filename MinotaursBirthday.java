import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

public class MinotaursBirthday
{
    private static Thread[] _threads;
    private static int _numThreads; // Integer.valueOf(args[1])
    private static Thread _minotaur;
    private static ReentrantLock _labyrinth;
    private static boolean _cupcake = true;
    private static AtomicInteger _currentGuest;
    private static boolean _party = true;
    private static boolean _print = true;
    private static boolean _random = true;
    private static Random _rand;
    private static Object _guests = new Object();
    private static Object _Minotaur = new Object();
    

    public static void main(String [] args) throws InterruptedException
    {
        setArgs(args);

        System.out.println("The minotaur invited " + _numThreads + " guests to his party. I hope they all like labyrinths and cupcakes!");
        _labyrinth = new ReentrantLock();
        
        createThreads();
        createMinotaur();

        party();

        System.out.println("They sure did! All " + _numThreads + " of them!");
        System.exit(0);
    }

    public static void setArgs(String [] args)
    {
        try{
            if(Integer.valueOf(args[0]) < 0)
                throw new IllegalArgumentException();
            }
        catch(IllegalArgumentException f)
            {
                System.out.println("ERROR: Please compile with integer x > 0");
                System.out.println("    Where x = number of guests (threads)");
                return;
            }
        catch(Exception e)
            {
                System.out.println("ERROR: Please compile with an integer x");
                System.out.println("    Where x = number of guests (threads)");
                return;
            }

        _numThreads = Integer.valueOf(args[0]);
        
        if(args.length > 1)
        {
            // Check for -p
            

            for (int i = 1; i < args.length; i++)
                try
                {
                    if(args[i].charAt(0) == '-' && (args[i].toLowerCase().charAt(1) == 'p' || args[i].toLowerCase().charAt(1) == 'r'))
                    {
                        if(args[i].toLowerCase().charAt(1) == 'p')
                            _print = false;
                        if(args[i].toLowerCase().charAt(1) == 'r')
                            _random = false;
                    }
                    else
                        throw new IllegalArgumentException();
                }
                catch(IllegalArgumentException f)
                {
                    System.out.println("ERROR: unknow argument");
                    System.out.println("       reference readme for valid arguments");
                }
        }
    }
    
    public static void createThreads()
    {
        _threads = new Thread[_numThreads];
        _currentGuest = new AtomicInteger();

        for (int i = 0; i < _numThreads; i++)
        {
            _threads[i] = new Thread(new BirthdayThread(i, _numThreads, _labyrinth, _currentGuest, _print, _guests, _Minotaur));
        }
    }

    public static void createMinotaur()
    {
        _rand = new Random();
        _minotaur = new Thread(new BirthdayMinotaurThread(_numThreads, _guests, _Minotaur));
    }

    public static void party() throws InterruptedException
    {
        _minotaur.start();
        
        for (int i = 0; i < _numThreads; i++)
        {
            _threads[i].start();
        }

        for (int i = 0; i < _numThreads; i++)
        {
            _threads[i].join();
        }
        _party = false;
    }
    
    public static void enterMaze()
    {
        int time;
        if(_random)
            time = _rand.nextInt(10) + 5;
        else
            time = 100;
        try {
            Thread.sleep((new Random()).nextInt(time));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        if(_cupcake == false)
        requestCupcake();   
        eatCupcake();
    }

    public static void resetGuest(int newGuest) {
        _currentGuest.set(newGuest);
    }
    private static void requestCupcake()
    {
        _cupcake = true;
    }
    public static void eatCupcake()
    {
        _cupcake = false;
    }
    public static boolean partyStatus()
    {
        return _party;
    }

}

class BirthdayThread implements Runnable
{
    private boolean[] _memory;
    private int _guestNumber;
    private int _numGuests;
    private ReentrantLock _lab;
    private AtomicInteger _currentGuest;
    private boolean _print;
    private static Object _guests = new Object();
    private static Object _minotaur = new Object();

    public BirthdayThread(int guestNumber, int numGuests, ReentrantLock lab, AtomicInteger currentGuest, boolean print, Object guests, Object minoataur) 
    {
        _guestNumber = guestNumber;
        _numGuests = numGuests;
        _memory = new boolean[numGuests];
        _lab = lab;
        _currentGuest = currentGuest;
        _print = print;
        _guests = guests;
        _minotaur = minoataur;
    }

    public boolean enterMaze()
    {
        
        _lab.lock();
        MinotaursBirthday.enterMaze();
        if(_print) print();
        _lab.unlock();
        return true;
    }

    public void print()
    {
        System.out.print("[");
        for (int i = 0; i < _numGuests; i++)
        {
            System.out.print((i == _guestNumber) ? "o" : (_memory[i] == true) ? "x" : " ");
        }
        System.out.println("]");
    }

    public boolean checkMemory()
    {
        for (int i = 0; i < _numGuests; i++)
        {
            if (_memory[i] == false)
            {
                return false;
            }
        }

        return true;
    }

    public void run()
    {
        boolean ateCupcake = false;
        boolean justEnteredMaze = false;
        int upNext;
        while(true)
        {

            if(!justEnteredMaze)
            {
                synchronized(_guests)
                {
                    try {
                        _guests.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            else
                justEnteredMaze = false;

            upNext = _currentGuest.get();

            if(upNext == _guestNumber)
            {
                ateCupcake = enterMaze();
                justEnteredMaze = true;
            }

            _memory[upNext] = true;

            if (checkMemory() && ateCupcake)
            {
                if(justEnteredMaze)
                {
                    synchronized(_minotaur)
                    {
                        _minotaur.notify();
                    }
                }
                return;
            }
            if(justEnteredMaze)
            {
                synchronized(_minotaur)
                {
                    _minotaur.notify();
                }
            }
        }
    }
}

class BirthdayMinotaurThread implements Runnable
{
    private int _numGuests;
    private Random _rand;
    private static Object _guests = new Object();
    private static Object _minotaur = new Object();
    

    public BirthdayMinotaurThread(int numGuests, Object guests, Object minoataur) 
    {
        _numGuests = numGuests;
        _rand = new Random();
        _guests = guests;
        _minotaur = minoataur;
    }

    public void run()
    {
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        while(MinotaursBirthday.partyStatus())
        {
            synchronized(_guests)
            {
                MinotaursBirthday.resetGuest(_rand.nextInt(_numGuests));
                _guests.notifyAll();
            }
            synchronized(_minotaur)
            {
                try {
                    if(MinotaursBirthday.partyStatus())
                        _minotaur.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}