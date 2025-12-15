import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.RecursiveTask;

public class BankTransferDemo {

    // Fork/Join для паралельного підрахунку загальної суми в банку
    static class SumTask extends RecursiveTask<Long> {
        private static final int THRESHOLD = 32;
        private final Account[] accounts;
        private final int lo, hi;

        SumTask(Account[] accounts, int lo, int hi) {
            this.accounts = accounts;
            this.lo = lo;
            this.hi = hi;
        }

        @Override
        protected Long compute() {
            int len = hi - lo;
            if (len <= THRESHOLD) {
                long sum = 0;
                for (int i = lo; i < hi; i++) {
                    sum += accounts[i].getBalanceWithLock();
                }
                return sum;
            }
            int mid = lo + len / 2;
            SumTask left = new SumTask(accounts, lo, mid);
            SumTask right = new SumTask(accounts, mid, hi);
            left.fork();
            long r = right.compute();
            long l = left.join();
            return l + r;
        }
    }

    static long totalMoney(Account[] accounts) {
        return ForkJoinPool.commonPool().invoke(new SumTask(accounts, 0, accounts.length));
    }

    public static void main(String[] args) throws Exception {
        final int ACCOUNTS = 300;          // "кілька сотень"
        final int TRANSFERS = 60_000;      // "в декількох тисячах потоків" -> задач дуже багато
        final long MAX_INIT = 50_000;
        final long MAX_AMOUNT = 1_000;

        Random rnd = new Random();
        Account[] accounts = new Account[ACCOUNTS];
        for (int i = 0; i < ACCOUNTS; i++) {
            long init = 1 + (long) (rnd.nextDouble() * MAX_INIT);
            accounts[i] = new Account(i, init);
        }

        Bank bank = new Bank();
        long before = totalMoney(accounts);

        int threads = Math.max(2, Runtime.getRuntime().availableProcessors());
        ExecutorService pool = Executors.newFixedThreadPool(threads);

        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(TRANSFERS);

        for (int i = 0; i < TRANSFERS; i++) {
            pool.execute(() -> {
                try {
                    start.await();

                    int fromIdx = rnd.nextInt(ACCOUNTS);
                    int toIdx = rnd.nextInt(ACCOUNTS);
                    while (toIdx == fromIdx) toIdx = rnd.nextInt(ACCOUNTS);

                    long amount = 1 + rnd.nextInt((int) MAX_AMOUNT);
                    bank.transfer(accounts[fromIdx], accounts[toIdx], amount);

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    done.countDown();
                }
            });
        }

        long t0 = System.currentTimeMillis();
        start.countDown();
        done.await();

        pool.shutdown();
        pool.awaitTermination(30, TimeUnit.SECONDS);

        long after = totalMoney(accounts);
        long t1 = System.currentTimeMillis();

        // Перевірки
        boolean nonNegative = true;
        for (Account a : accounts) {
            if (a.getBalanceWithLock() < 0) {
                nonNegative = false;
                break;
            }
        }

        System.out.println("Accounts: " + ACCOUNTS);
        System.out.println("Transfers: " + TRANSFERS);
        System.out.println("Thread pool size: " + threads);
        System.out.println("Total before: " + before);
        System.out.println("Total after : " + after);
        System.out.println("Total equal : " + (before == after));
        System.out.println("No negative : " + nonNegative);
        System.out.println("Elapsed ms  : " + (t1 - t0));

        if (before != after) {
            throw new AssertionError("Money mismatch! before=" + before + " after=" + after);
        }
        if (!nonNegative) {
            throw new AssertionError("Negative balance detected!");
        }
    }
}
