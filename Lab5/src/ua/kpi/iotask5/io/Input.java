package ua.kpi.iotask5.io;

import java.util.Scanner;

public final class Input {
    private Input() {}

    public static int readInt(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = sc.nextLine().trim();
            try {
                return Integer.parseInt(s);
            } catch (NumberFormatException e) {
                System.out.println("Помилка: введіть ціле число.");
            }
        }
    }

    public static String readNonEmpty(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = sc.nextLine();
            if (s != null && !s.trim().isEmpty()) return s.trim();
            System.out.println("Помилка: рядок не може бути порожнім.");
        }
    }

    public static char readChar(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = sc.nextLine();
            if (s != null && !s.isEmpty()) return s.charAt(0);
            System.out.println("Помилка: введіть хоча б 1 символ.");
        }
    }
}
