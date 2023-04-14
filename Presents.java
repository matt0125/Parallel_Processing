import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;

public class Presents
{
    public static final int NUM_GIFTS = 500000;
    public static final int NUM_SERVANTS = 4;

    private static int totalGifts;
    private static int totalCards;
    

    public static void main(String[] args)
    {
        
        LinkedList<Integer> presents = new LinkedList<Integer>();
        
        for(int i = 0; i < NUM_GIFTS; i++)
        {
            presents.add(i);
        }
        
        Collections.shuffle(presents);
        
        Queue<Integer> queue = presents;

        MyLinkedList list = new MyLinkedList();

        Presents.totalCards = 0;
        Presents.totalGifts = 0;

        
        Thread[] servants = createThreads(NUM_SERVANTS, queue, list);

        startThreads(servants);
        joinThreads(servants);

        System.out.println(Presents.totalGifts + " gifts, " + Presents.totalCards + " cards.");
    }

    private static Thread[] createThreads(int numThreads, Queue<Integer> queue, MyLinkedList list)
    {
        Thread[] servants = new Thread[numThreads];

        ReentrantLock bagLock = new ReentrantLock();

        for (int i = 0; i < numThreads; i++)
        {
            servants[i] = new Thread(new Servant(list, bagLock, queue));
        }

        return servants;
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

    public synchronized static void cardCount(int cards) { Presents.totalCards += cards; }

    public synchronized static void giftCount(int gifts) { Presents.totalGifts += gifts; }


}

class MyLinkedList
{
    AtomicReference<Node> head;


    public MyLinkedList()
    {
        this.head = new AtomicReference<Node>(new Node());
    }

    public boolean isEmpty()
    {
        return (this.head.get().next.get() == null);
    }

    public int remove() // throws NoSuchElementException
    {
        Node first;
        Node next;
        int value;

        while(true)
        {

            first = head.get();
            next = first.next.get();

            if (first == head.get())
            {
                if (next == null)
                {
                    return head.get().value;
                }
                else
                {
                    value = next.value;
                    if (head.compareAndSet(first, next))
                    return value;
                }
            }

        }
    }
}

class Node
{
    int value;
    AtomicReference<Node> next;
    Node previous;

    public Node()
    {
        this.next = new AtomicReference<Node>(null);
    }

    public Node(int value) {
        this.value = value;
        this.next = new AtomicReference<Node>(null);
    }

    public void insertNext(int value)
    {
        Node newNode = new Node(value);
        Node currentNode = this;

        while(true)
        {
            Node next = currentNode.next.get();
            newNode.next.set(next);


            if(currentNode.next.compareAndSet(next, newNode))
            {
                return;
            }
            
            while(currentNode.next.get() != null && currentNode.next.get().value < value)
            {
                currentNode = currentNode.next.get();
            }

        }
    }
}

class Servant implements Runnable
{
    MyLinkedList list;
    ReentrantLock bagLock;
    Queue<Integer> presents;

    int cards;
    int gifts;

    public Servant(MyLinkedList list,ReentrantLock bagLock, Queue <Integer> presents)
    {
        this.list = list;
        this.bagLock = bagLock;
        this.presents = presents;

        this.cards = 0;
        this.gifts = 0;
    }

    @Override
    public void run()
    {
        int i = 0;
        while(!this.presents.isEmpty() || !this.list.isEmpty())
        {
            if(i%2 == 0)
                linkGift();
            else
                sendCard();

            // if(lookForGift(i))
            //     System.out.println(i);

            i++;
        }

        Presents.cardCount(this.cards);
        Presents.giftCount(this.gifts);


    }

    private void linkGift()
    {
        if(!this.presents.isEmpty())
        {
            int nextGift = -1;

            synchronized(bagLock)
            {
                if(!this.presents.isEmpty())
                    nextGift = this.presents.remove();
            }
            
            if(nextGift == -1)
                return;


            Node node = this.list.head.get();
            
            while(node.next.get() != null && node.next.get().value < nextGift)
            {
                node = node.next.get();
            }
            node.insertNext(nextGift);
            this.gifts++;
        }
    }

    // Removes a present from the chain and sends a thank you card
    private void sendCard()
    {
        int giftNum;

        if ((giftNum = this.list.remove()) != -1)
            this.cards++;

        // if(giftNum != -1)
        //     System.out.println("Sending thank you for gift number " + giftNum);
    }

    // Returns true if a gift is currently in the chain without adding or removing any of them
    private boolean lookForGift(int giftNumber)
    {
        Node currentNode = this.list.head.get();


        while(currentNode.next.get() != null)
        {
            currentNode = currentNode.next.get();

            if(currentNode.value == giftNumber)
                return true;
        }
        if(currentNode.value == giftNumber)
            return true;

        return false;
    }

}