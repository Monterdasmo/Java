import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class RingBuffer<T> {

    private static final class Node<T> {
        T value;
        Node<T> next;
        Node(T value) { this.value = value; }
    }

    private final int capacity;
    private int size = 0;

    private Node<T> head; // звідси беремо (take)
    private Node<T> tail; // сюди кладемо (put)

    private final ReentrantLock lock = new ReentrantLock();
    private final Condition notEmpty = lock.newCondition();
    private final Condition notFull = lock.newCondition();

    public RingBuffer(int capacity) {
        if (capacity <= 0) throw new IllegalArgumentException("capacity must be > 0");
        this.capacity = capacity;

        // Створюємо кільце з capacity вузлів
        Node<T> first = new Node<>(null);
        Node<T> cur = first;
        for (int i = 1; i < capacity; i++) {
            cur.next = new Node<>(null);
            cur = cur.next;
        }
        cur.next = first; // замкнули
        head = first;
        tail = first;
    }

    public void put(T item) throws InterruptedException {
        if (item == null) throw new IllegalArgumentException("null items are not supported");
        lock.lock();
        try {
            while (size == capacity) {
                notFull.await();
            }
            tail.value = item;
            tail = tail.next;
            size++;
            notEmpty.signal();
        } finally {
            lock.unlock();
        }
    }

    public T take() throws InterruptedException {
        lock.lock();
        try {
            while (size == 0) {
                notEmpty.await();
            }
            T item = head.value;
            head.value = null;
            head = head.next;
            size--;
            notFull.signal();
            return item;
        } finally {
            lock.unlock();
        }
    }
}
