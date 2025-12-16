package ua.kpi.lab11;

import ua.kpi.lab11.db.DatabaseOperations;

import java.sql.SQLException;

/**
 * Головний клас додатку Lab11.
 * Демонструє роботу з реляційною базою даних через JDBC API.
 *
 * Виконує послідовно всі необхідні операції:
 * 1. Отримати список всіх співробітників
 * 2. Отримати список всіх завдань
 * 3. Отримати список співробітників зазначеного відділу
 * 4. Додати завдання для співробітника
 * 5. Отримати список завдань для співробітника
 * 6. Видалити співробітника
 */
public class Main {

    public static void main(String[] args) {
        printHeader();

        DatabaseOperations dbOps = new DatabaseOperations();

        // Перевірка підключення до бази даних
        System.out.println("=== Перевірка підключення ===");
        if (!dbOps.testConnection()) {
            System.err.println("\n✗ Неможливо продовжити без підключення до бази даних.");
            System.err.println("  Переконайтеся що:");
            System.err.println("  1. MySQL Server запущено");
            System.err.println("  2. База даних company_db створена (виконайте sql/schema.sql та sql/data.sql)");
            System.err.println("  3. Параметри в db.properties правильні");
            System.err.println("  4. MySQL JDBC драйвер (JAR) доданий до classpath");
            return;
        }

        System.out.println();
        waitForUser();

        try {
            // ========================================
            // 1. Отримати список всіх співробітників
            // ========================================
            printSection("1. Список всіх співробітників");
            dbOps.getAllEmployees();
            System.out.println();
            waitForUser();

            // ========================================
            // 2. Отримати список всіх завдань
            // ========================================
            printSection("2. Список всіх завдань");
            dbOps.getAllTasks();
            System.out.println();
            waitForUser();

            // ========================================
            // 3. Отримати список співробітників IT відділу
            // ========================================
            printSection("3. Співробітники IT відділу (ID=1)");
            dbOps.getEmployeesByDepartment(1);
            System.out.println();
            waitForUser();

            // ========================================
            // 4. Додати нове завдання для співробітника
            // ========================================
            printSection("4. Додавання нового завдання");
            System.out.println("  Додаємо завдання для співробітника з ID=1:");
            dbOps.addTaskForEmployee(1, "Оновити документацію API");
            System.out.println();
            waitForUser();

            // ========================================
            // 5. Отримати завдання для співробітника ID=1
            // ========================================
            printSection("5. Завдання для співробітника ID=1");
            System.out.println("  (має містити щойно додане завдання)");
            dbOps.getTasksByEmployee(1);
            System.out.println();
            waitForUser();

            // ========================================
            // 6. Видалити співробітника ID=15
            // ========================================
            printSection("6. Видалення співробітника ID=15");
            System.out.println("  Співробітника буде видалено з бази даних.");
            System.out.println("  Завдання цього співробітника також будуть видалені (CASCADE).");
            System.out.println();
            dbOps.deleteEmployee(15);
            System.out.println();

            // Перевірка результату видалення
            System.out.println("  Перевірка: спроба отримати завдання видаленого співробітника:");
            dbOps.getTasksByEmployee(15);
            System.out.println();

            printFooter();

        } catch (SQLException e) {
            System.err.println("\n✗ Помилка виконання SQL запиту:");
            System.err.println("  " + e.getMessage());
            System.err.println("\nStack trace:");
            e.printStackTrace();
        }
    }

    /**
     * Вивести заголовок програми.
     */
    private static void printHeader() {
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║                                                            ║");
        System.out.println("║          Лабораторна робота 11: JDBC Operations            ║");
        System.out.println("║          Робота з реляційними базами даних                 ║");
        System.out.println("║                                                            ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        System.out.println();
    }

    /**
     * Вивести розділювач секції.
     *
     * @param title назва секції
     */
    private static void printSection(String title) {
        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println("  " + title);
        System.out.println("═══════════════════════════════════════════════════════════");
    }

    /**
     * Вивести підвал програми.
     */
    private static void printFooter() {
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║                                                            ║");
        System.out.println("║  ✓ Всі операції успішно виконано!                         ║");
        System.out.println("║                                                            ║");
        System.out.println("║  Для повторного запуску:                                  ║");
        System.out.println("║  1. Відновіть базу даних: виконайте sql/schema.sql        ║");
        System.out.println("║                            та sql/data.sql в MySQL         ║");
        System.out.println("║  2. Запустіть програму знову                              ║");
        System.out.println("║                                                            ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
    }

    /**
     * Пауза для кращого читання виводу (опціонально).
     * Можна закоментувати якщо не потрібно.
     */
    private static void waitForUser() {
        // Закоментуйте наступні рядки якщо не потрібна пауза між секціями
        // try {
        //     System.out.print("Натисніть Enter для продовження...");
        //     System.in.read();
        // } catch (Exception e) {
        //     // Ігноруємо
        // }
    }
}
