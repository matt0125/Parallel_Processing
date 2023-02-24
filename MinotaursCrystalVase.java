import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

public class MinotaursCrystalVase
{
    private static int _numThreads;
    private static Thread[] _threads;
    private static int _solutionNumber;
    private static boolean _print = true;
    private static ReentrantLock _vaseRoom;
    private static boolean [] _sawVase;
    private static int _queue;
    private static Object [] _befores;

    public static void main(String [] args)
    {
        setArgs(args);
        createThreads();
        if(_print) System.out.println("The minotaur invited " + _numThreads + " guests to his party. I hope they all like his crystal vase!");
        startTheParty();
        
        int awe = 0;
        for (int i = 0; i < _numThreads; i++)
        {
            if (_sawVase[i] == true)
                awe++;
        }

        if(_print) System.out.println(awe + " guests were in awe of the vase!");
    }
    
    public static void setArgs(String [] args)
    {
        try{
            if(Integer.valueOf(args[0]) < 0 || Integer.valueOf(args[0]) < 0)
                throw new IllegalArgumentException();
            }
        catch(IllegalArgumentException f)
            {
                System.out.println("ERROR: Please compile with integer x > 0, 0 <= y <= 2");
                System.out.println("    Where x = number of guests (threads)");
                System.out.println("    Where y = solution to use");
                return;
            }
        catch(Exception e)
            {
                System.out.println("ERROR: Please compile with integer x > 0, 0 <= y <= 2");
                System.out.println("    Where x = number of guests (threads)");
                System.out.println("    Where y = solution to use");
                return;
            }

        _numThreads = Integer.valueOf(args[0]);
        _solutionNumber = Integer.valueOf(args[1]);
        _vaseRoom = new ReentrantLock();
        _sawVase = new boolean[_numThreads];
        _queue = 0;

        _befores = new Object[_numThreads+1];

        for(int i = 0; i < (_numThreads + 1); i++)
        {
            _befores[i] = new Object();
        }
        
        if(args.length > 2)
        {
            // Check for -p
            

            for (int i = 2; i < args.length; i++)
                try
                {
                    if(args[i].charAt(0) == '-' && (args[i].toLowerCase().charAt(1) == 'p'))
                    {
                        if(args[i].toLowerCase().charAt(1) == 'p')
                            _print = false;
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
        for(int i = 0; i < _numThreads; i++)
        {
            _threads[i] = new Thread(new guest(i, _vaseRoom, _solutionNumber));
        }
    }

    public static void startTheParty()
    {
        for(int i = 0; i < _numThreads; i++)
        {
            _threads[i].start();
        }
        
        for(int i = 0; i < _numThreads; i++)
        {
            try {
                _threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void baskInTheGloryThatIsTheCrystalVase(int guestNumber)
    {
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if(_print)
            System.out.println("Guest number " + guestNumber + " is in awe of the crystal vase");
        
        _sawVase[guestNumber] = true;
    }

    synchronized static Object[] enterQueue(int guestNumber)
    {
        // Number in line
        _queue++;

        Object[] rtn = new Object[3];
        rtn[0] = _befores[_queue-1];
        rtn[1] = _befores[_queue];
        rtn[2] = _queue;
    
        return rtn;
    }
}

class guest implements Runnable
{
    private int _guestNumber;
    private ReentrantLock _vaseRoom;
    private int _solutionNumber;
    private boolean _sawVase = false;

    public guest(int guestNumber, ReentrantLock vaseRoom, int solutionNumber)
    {
        _guestNumber = guestNumber;
        _vaseRoom = vaseRoom;
        _solutionNumber = solutionNumber;
    }

    @Override
    public void run() {
        switch(_solutionNumber)
        {
            case(1):
                lock();
                return;
            case(2):
                trylock();
                return;
            case(3):
                queue();
                return;
            default:
                System.err.println("Uh oh this wasn't supposed to happen...");
                System.exit(0);
        }
    }

    public void lock()
    {
        Random rand = new Random();
        
        try {
            Thread.sleep(rand.nextInt(1000));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        _vaseRoom.lock();
        MinotaursCrystalVase.baskInTheGloryThatIsTheCrystalVase(_guestNumber);
        _sawVase = true;
        _vaseRoom.unlock();
    }

    public void trylock()
    {
        Random rand = new Random();

        while(!_sawVase)
        {
            try {
                Thread.sleep(rand.nextInt(500));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(_vaseRoom.tryLock())
            {
                MinotaursCrystalVase.baskInTheGloryThatIsTheCrystalVase(_guestNumber);
                _sawVase = true;
                _vaseRoom.unlock();
            }
        }
    }

    public void queue()
    {
        Object objects[] = new Object[3];
        objects = MinotaursCrystalVase.enterQueue(_guestNumber);

        if (((int) objects[2]) == 1)
        {
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        else
        {
            synchronized(objects[0])
            {
                try {
                    objects[0].wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        synchronized(objects[1])
        {
            MinotaursCrystalVase.baskInTheGloryThatIsTheCrystalVase(_guestNumber);
            _sawVase = true;
            objects[1].notify();
        }
    }
}