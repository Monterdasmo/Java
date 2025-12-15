import java.util.concurrent.locks.ReentrantLock;

public class Account {
    private final int id;
    private final ReentrantLock lock = new ReentrantLock();
    private long balance;

    public Account(int id, long initialBalance) {
        this.id = id;
        this.balance = initialBalance;
    }

    public int getId() {
        return id;
    }

    public ReentrantLock lock() {
        return lock;
    }

    // Викликати ТІЛЬКИ коли lock вже захоплений ззовні
    void depositUnsafe(long amount) {
        balance += amount;
    }

    // Викликати ТІЛЬКИ коли lock вже захоплений ззовні
    boolean withdrawUnsafe(long amount) {
        if (balance < amount) return false;
        balance -= amount;
        return true;
    }

    // Для валідації після тестів
    public long getBalanceWithLock() {
        lock.lock();
        try {
            return balance;
        } finally {
            lock.unlock();
        }
    }
}
