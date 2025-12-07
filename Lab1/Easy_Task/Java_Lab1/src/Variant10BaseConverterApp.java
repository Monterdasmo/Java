

import java.util.Scanner;

public class Variant10BaseConverterApp {

    private static final String DIGITS = "0123456789ABCDEF";

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Введіть ціле позитивне число (decimal): ");
        if (!sc.hasNextLong()) {
            System.out.println("Помилка: потрібно ціле число.");
            return;
        }

        long n = sc.nextLong();
        if (n <= 0) {
            System.out.println("Помилка: число має бути > 0.");
            return;
        }
        if (n > Integer.MAX_VALUE) {
            System.out.println("Увага: для простоти реалізації обмежимося int (<= 2147483647).");
            return;
        }

        int value = (int) n;

        String bin = toBase(value, 2);
        String oct = toBase(value, 8);
        String hex = toBase(value, 16);

        System.out.println("BIN: " + bin);
        System.out.println("OCT: " + oct);
        System.out.println("HEX: " + hex);
    }

    // Не використовуємо Integer.toString / toBinaryString тощо.
    public static String toBase(int value, int base) {
        if (base < 2 || base > 16) {
            throw new IllegalArgumentException("base має бути в діапазоні [2..16]");
        }
        if (value == 0) return "0";

        StringBuilder sb = new StringBuilder();
        int x = value;

        while (x > 0) {
            int rem = x % base;
            sb.append(DIGITS.charAt(rem));
            x /= base;
        }

        return sb.reverse().toString();
    }
}
