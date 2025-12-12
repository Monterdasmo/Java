package ua.kpi.iotask5;

import ua.kpi.iotask5.io.*;
import ua.kpi.iotask5.model.Book;
import ua.kpi.iotask5.streams.KeyDecryptInputStream;
import ua.kpi.iotask5.streams.KeyEncryptOutputStream;

import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    private static final ObjectFileStorage<Book> storage = new ObjectFileStorage<>();
    private static List<Book> books = new ArrayList<>();

    public static void main(String[] args) {
        try (Scanner sc = new Scanner(System.in)) {
            while (true) {
                printMenu();
                int choice = Input.readInt(sc, "Ваш вибір: ");

                try {
                    switch (choice) {
                        case 1 -> task1(sc);
                        case 2 -> task2Objects(sc);
                        case 3 -> task3EncryptDecrypt(sc);
                        case 4 -> task4Tags(sc);
                        case 5 -> extraExportReport(sc);
                        case 0 -> {
                            System.out.println("Exit.");
                            return;
                        }
                        default -> System.out.println("Невідомий пункт меню.");
                    }
                } catch (Exception e) {
                    System.out.println("Сталася помилка: " + e.getMessage());
                }

                System.out.println();
            }
        }
    }

    private static void printMenu() {
        System.out.println("=== I/O Streams Lab (Task 5) ===");
        System.out.println("1) Рядок з максимальною кількістю слів у файлі");
        System.out.println("2) Набір об'єктів: додати/пошук/зберегти/завантажити (Object streams)");
        System.out.println("3) Шифрування/дешифрування файлу (FilterInputStream/FilterOutputStream)");
        System.out.println("4) Частота HTML-тегів за URL (2 сортування)");
        System.out.println("5) Додаткове: Експорт звіту (txt або txt.gz)");
        System.out.println("0) Вихід");
    }

    // Task 1
    private static void task1(Scanner sc) throws IOException {
        String path = Input.readNonEmpty(sc, "Введіть шлях до текстового файлу: ");
        String best = TextFiles.findLineWithMaxWords(path);

        if (best == null) {
            System.out.println("Файл порожній або рядків не знайдено.");
        } else {
            System.out.println("Рядок з максимальною кількістю слів:");
            System.out.println(best);
        }
    }

    // Task 2 (Objects + save/load + search)
    private static void task2Objects(Scanner sc) throws IOException, ClassNotFoundException {
        while (true) {
            System.out.println("--- Objects menu ---");
            System.out.println("1) Додати Book");
            System.out.println("2) Показати всі");
            System.out.println("3) Пошук за назвою (substring)");
            System.out.println("4) Видалити за id");
            System.out.println("5) Зберегти у файл (ObjectOutputStream)");
            System.out.println("6) Завантажити з файлу (ObjectInputStream)");
            System.out.println("0) Назад");

            int c = Input.readInt(sc, "Ваш вибір: ");

            switch (c) {
                case 1 -> {
                    int id = Input.readInt(sc, "id: ");
                    String title = Input.readNonEmpty(sc, "title: ");
                    String author = Input.readNonEmpty(sc, "author: ");
                    int year = Input.readInt(sc, "year: ");

                    books.add(new Book(id, title, author, year));
                    System.out.println("Додано.");
                }
                case 2 -> {
                    if (books.isEmpty()) System.out.println("(порожньо)");
                    else books.forEach(System.out::println);
                }
                case 3 -> {
                    String q = Input.readNonEmpty(sc, "Пошук title містить: ").toLowerCase(Locale.ROOT);
                    books.stream()
                            .filter(b -> b.getTitle().toLowerCase(Locale.ROOT).contains(q))
                            .forEach(System.out::println);
                }
                case 4 -> {
                    int id = Input.readInt(sc, "id для видалення: ");
                    boolean removed = books.removeIf(b -> b.getId() == id);
                    System.out.println(removed ? "Видалено." : "Не знайдено.");
                }
                case 5 -> {
                    String path = Input.readNonEmpty(sc, "Файл для збереження (наприклад, data.bin): ");
                    storage.save(path, books);
                    System.out.println("Збережено у: " + path);
                }
                case 6 -> {
                    String path = Input.readNonEmpty(sc, "Файл для читання: ");
                    books = storage.load(path);
                    System.out.println("Завантажено. Кількість об'єктів: " + books.size());
                }
                case 0 -> {
                    return;
                }
                default -> System.out.println("Невідомий пункт.");
            }
            System.out.println();
        }
    }

    // Task 3
    private static void task3EncryptDecrypt(Scanner sc) throws IOException {
        System.out.println("--- Encrypt/Decrypt ---");
        String in = Input.readNonEmpty(sc, "Вхідний файл: ");
        String out = Input.readNonEmpty(sc, "Вихідний файл: ");
        char key = Input.readChar(sc, "Ключовий символ (1 літера/символ): ");

        System.out.println("1) Encrypt -> out");
        System.out.println("2) Decrypt -> out");
        int mode = Input.readInt(sc, "Ваш вибір: ");

        if (mode == 1) {
            try (InputStream is = new FileInputStream(in);
                 OutputStream os = new KeyEncryptOutputStream(new FileOutputStream(out), key)) {
                is.transferTo(os);
            }
            System.out.println("Зашифровано у: " + out);
        } else if (mode == 2) {
            try (InputStream is = new KeyDecryptInputStream(new FileInputStream(in), key);
                 OutputStream os = new FileOutputStream(out)) {
                is.transferTo(os);
            }
            System.out.println("Дешифровано у: " + out);
        } else {
            System.out.println("Невірний режим.");
        }
    }

    // Task 4
    private static void task4Tags(Scanner sc) throws IOException, InterruptedException {
        String url = Input.readNonEmpty(sc, "URL (наприклад, https://example.com): ");

        String html = fetch(url);
        Map<String, Integer> freq = countTags(html);

        System.out.println("--- A) Лексикографічно (зростання) ---");
        freq.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(e -> System.out.println(e.getKey() + " -> " + e.getValue()));

        System.out.println("--- B) За частотою (зростання) ---");
        freq.entrySet().stream()
                .sorted(Comparator.<Map.Entry<String, Integer>>comparingInt(Map.Entry::getValue)
                        .thenComparing(Map.Entry::getKey))
                .forEach(e -> System.out.println(e.getKey() + " -> " + e.getValue()));
    }

    private static String fetch(String url) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest req = HttpRequest.newBuilder(URI.create(url))
                .GET()
                .header("User-Agent", "Java HttpClient")
                .build();
        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        return resp.body();
    }

    private static Map<String, Integer> countTags(String html) {
        Map<String, Integer> map = new HashMap<>();
        Pattern p = Pattern.compile("<\\s*/?\\s*([a-zA-Z][a-zA-Z0-9:-]*)\\b");
        Matcher m = p.matcher(html);

        while (m.find()) {
            String tag = m.group(1).toLowerCase(Locale.ROOT);
            if (tag.equals("!doctype")) continue;
            map.put(tag, map.getOrDefault(tag, 0) + 1);
        }
        return map;
    }

    // Extra
    private static void extraExportReport(Scanner sc) throws IOException {
        String path = Input.readNonEmpty(sc, "Файл звіту (results.txt або results.txt.gz): ");
        boolean gzip = path.toLowerCase(Locale.ROOT).endsWith(".gz");

        StringBuilder sb = new StringBuilder();
        sb.append("I/O Streams Lab Report\n");
        sb.append("Generated: ").append(LocalDateTime.now()).append("\n\n");

        sb.append("Objects count: ").append(books.size()).append("\n");
        for (Book b : books) sb.append("  ").append(b).append("\n");

        sb.append("\nNote: Task1/3/4 results are printed in console during execution.\n");

        ReportExporter.exportText(path, sb.toString(), gzip);
        System.out.println("Звіт експортовано у: " + path + (gzip ? " (GZIP)" : ""));
    }
}
