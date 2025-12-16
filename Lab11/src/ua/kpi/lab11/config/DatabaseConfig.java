package ua.kpi.lab11.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Клас для управління конфігурацією підключення до бази даних.
 * Завантажує параметри з файлу db.properties при ініціалізації.
 */
public class DatabaseConfig {
    private static final Properties properties = new Properties();
    private static final String CONFIG_FILE = "db.properties";

    // Статичний блок ініціалізації - виконується при завантаженні класу
    static {
        try (InputStream input = new FileInputStream(CONFIG_FILE)) {
            properties.load(input);
            System.out.println("✓ Конфігурація бази даних успішно завантажена з " + CONFIG_FILE);
        } catch (IOException e) {
            System.err.println("✗ Помилка завантаження конфігурації: " + e.getMessage());
            System.err.println("  Переконайтеся, що файл " + CONFIG_FILE + " існує в кореневій директорії проєкту");
        }
    }

    /**
     * Отримати повний URL підключення до бази даних.
     * Включає базовий URL та додаткові параметри.
     *
     * @return URL підключення до MySQL
     */
    public static String getUrl() {
        String baseUrl = properties.getProperty("db.url");
        String useSSL = properties.getProperty("db.useSSL", "false");
        String timezone = properties.getProperty("db.serverTimezone", "UTC");
        String allowPublicKey = properties.getProperty("db.allowPublicKeyRetrieval", "true");

        return baseUrl + "?useSSL=" + useSSL +
                "&serverTimezone=" + timezone +
                "&allowPublicKeyRetrieval=" + allowPublicKey;
    }

    /**
     * Отримати ім'я користувача бази даних.
     *
     * @return ім'я користувача
     */
    public static String getUser() {
        return properties.getProperty("db.user");
    }

    /**
     * Отримати пароль користувача бази даних.
     *
     * @return пароль
     */
    public static String getPassword() {
        return properties.getProperty("db.password", "");
    }

    /**
     * Отримати ім'я класу JDBC драйвера.
     *
     * @return повне ім'я класу драйвера
     */
    public static String getDriver() {
        return properties.getProperty("db.driver");
    }

    /**
     * Вивести всю конфігурацію (без пароля) для діагностики.
     */
    public static void printConfig() {
        System.out.println("\n=== Конфігурація бази даних ===");
        System.out.println("URL: " + getUrl());
        System.out.println("User: " + getUser());
        System.out.println("Password: " + (getPassword().isEmpty() ? "(порожній)" : "***"));
        System.out.println("Driver: " + getDriver());
        System.out.println("================================\n");
    }
}
