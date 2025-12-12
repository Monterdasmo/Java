import java.text.NumberFormat;
import java.util.Locale;

public class ParallelMonteCarloPi {

    // За прикладом в умові: 1,000,000,000 (може бути довго; за потреби зменшіть).
    private static final long ITERATIONS = 1_000_000_000L;

    // Фіксоване зерно, щоб можна було порівнювати запуски.
    private static final long SEED = 0x9E3779B97F4A7C15L;

    private static long mix64(long z) {
        // MurmurHash3 finalizer (швидке “перемішування” бітів)
        z ^= (z >>> 33);
        z *= 0xff51afd7ed558ccdL;
        z ^= (z >>> 33);
        z *= 0xc4ceb9fe1a85ec53L;
        z ^= (z >>> 33);
        return z;
    }

    private static double toUnitDouble(long x) {
        // 53 біти мантиси -> [0,1)
        return (x >>> 11) * 0x1.0p-53;
    }

    private static final class Worker extends Thread {
        private final int id;
        private final long startIdx;
        private final long endIdx;
        private long inside;

        Worker(int id, long startIdx, long endIdx) {
            this.id = id;
            this.startIdx = startIdx;
            this.endIdx = endIdx;
        }

        @Override
        public void run() {
            long local = 0;
            // Детерміновано: для кожного i генеруємо x,y через mix64(seed ^ counter)
            for (long i = startIdx; i < endIdx; i++) {
                long c0 = (i << 1);
                long c1 = c0 + 1;

                double x = toUnitDouble(mix64(SEED ^ c0));
                double y = toUnitDouble(mix64(SEED ^ c1));

                double r2 = x * x + y * y;
                if (r2 <= 1.0) local++;
            }
            inside = local;
        }

        long getInside() {
            return inside;
        }
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.err.println("Usage: java ParallelMonteCarloPi <threads>");
            System.exit(2);
        }

        int threads = Integer.parseInt(args[0]);
        if (threads <= 0) {
            System.err.println("threads must be > 0");
            System.exit(2);
        }

        Worker[] workers = new Worker[threads];
        long chunk = ITERATIONS / threads;
        long rem = ITERATIONS % threads;

        long t0 = System.nanoTime();

        long start = 0;
        for (int i = 0; i < threads; i++) {
            long size = chunk + (i < rem ? 1 : 0);
            long end = start + size;
            workers[i] = new Worker(i, start, end);
            workers[i].start();
            start = end;
        }

        long insideTotal = 0;
        for (Worker w : workers) {
            w.join();
            insideTotal += w.getInside();
        }

        long t1 = System.nanoTime();

        double pi = 4.0 * ((double) insideTotal / (double) ITERATIONS);
        double ms = (t1 - t0) / 1_000_000.0;

        NumberFormat nf = NumberFormat.getIntegerInstance(Locale.US);

        System.out.printf(Locale.US, "PI is %.5f%n", pi);
        System.out.println("THREADS " + threads);
        System.out.println("ITERATIONS " + nf.format(ITERATIONS));
        System.out.printf(Locale.US, "TIME %.2fms%n", ms);
    }
}
