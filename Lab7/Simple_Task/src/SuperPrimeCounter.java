import java.util.Scanner;
import java.util.function.IntPredicate;
import java.util.stream.IntStream;

public class SuperPrimeCounter {

    // Перевірка, чи є число простим (через лямбда-предикат)
    private static final IntPredicate IS_PRIME = n -> {
        if (n < 2) return false;
        if (n == 2) return true;
        if (n % 2 == 0) return false;
        int limit = (int) Math.sqrt(n);
        return IntStream.rangeClosed(3, limit)
                .filter(i -> i % 2 != 0)
                .noneMatch(i -> n % i == 0);
    };

    // Реверс цифр числа
    private static int reverseDigits(int n) {
        int result = 0;
        while (n > 0) {
            result = result * 10 + (n % 10);
            n /= 10;
        }
        return result;
    }

    /**
     * Кількість надпростих чисел у діапазоні [from; to].
     */
    public static long countSuperPrimesInRange(int from, int to) {
        if (from < 1) {
            throw new IllegalArgumentException("Початок діапазону має бути не менше 1");
        }
        if (to > 1000) {
            throw new IllegalArgumentException("Кінець діапазону не може бути > 1000");
        }
        if (from > to) {
            throw new IllegalArgumentException("Початок діапазону не може бути більшим за кінець");
        }

        return IntStream.rangeClosed(from, to)
                .filter(IS_PRIME)
                .filter(x -> IS_PRIME.test(reverseDigits(x)))
                .count();
    }

    /**
     * Варіант з умови (якщо потрібен): [1; n], n <= 1000.
     */
    public static long countSuperPrimes(int n) {
        return countSuperPrimesInRange(1, n);
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        int from;
        int to;

        while (true) {
            System.out.print("Введіть початок діапазону (натуральне число >= 1): ");
            from = scanner.nextInt();

            System.out.print("Введіть кінець діапазону (натуральне число <= 1000): ");
            to = scanner.nextInt();

            if (from < 1) {
                System.out.println("Початок діапазону має бути не менше 1. Спробуйте ще раз.\n");
                continue;
            }
            if (to > 1000) {
                System.out.println("Кінець діапазону не може бути більшим за 1000. Спробуйте ще раз.\n");
                continue;
            }
            if (from > to) {
                System.out.println("Початок діапазону не може бути більшим за кінець. Спробуйте ще раз.\n");
                continue;
            }
            break;
        }

        long count = countSuperPrimesInRange(from, to);

        System.out.println(
                "Кількість надпростих чисел в діапазоні [" +
                        from + "; " + to + "] = " + count
        );
    }
}
