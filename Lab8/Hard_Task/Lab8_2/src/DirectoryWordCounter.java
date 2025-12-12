    import java.io.BufferedReader;
    import java.io.BufferedWriter;
    import java.io.IOException;
    import java.nio.charset.StandardCharsets;
    import java.nio.file.*;
    import java.util.*;
    import java.util.concurrent.ConcurrentHashMap;
    
    public class DirectoryWordCounter {
    
        private static final class DirectoryProcessor implements Runnable {
            private final Path dir;
            private final char letterLower;
            private final Map<Path, Integer> results; // thread-safe
            private final Path outputFileAbs;         // абсолютний шлях до output-файлу (для виключення)
    
            DirectoryProcessor(Path dir, char letterLower, Map<Path, Integer> results, Path outputFileAbs) {
                this.dir = dir;
                this.letterLower = letterLower;
                this.results = results;
                this.outputFileAbs = outputFileAbs;
            }
    
            @Override
            public void run() {
                List<Thread> children = new ArrayList<>();
    
                try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
                    for (Path p : stream) {
                        if (Files.isDirectory(p)) {
                            Thread t = new Thread(new DirectoryProcessor(p, letterLower, results, outputFileAbs));
                            t.start();
                            children.add(t);
                            continue;
                        }
    
                        if (!Files.isRegularFile(p)) continue;
    
                        // ВИКЛЮЧАЄМО файл результатів зі сканування
                        Path pAbs = p.toAbsolutePath().normalize();
                        if (pAbs.equals(outputFileAbs)) {
                            continue;
                        }
    
                        if (isTextFile(p)) {
                            int cnt = countWordsStartingWith(p, letterLower);
                            results.put(p, cnt);
                        }
                    }
                } catch (IOException e) {
                    System.err.println("Cannot read dir: " + dir + " -> " + e.getMessage());
                }
    
                for (Thread t : children) {
                    try {
                        t.join();
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
            }
    
            private static boolean isTextFile(Path p) {
                String name = p.getFileName().toString().toLowerCase(Locale.ROOT);
                return name.endsWith(".txt");
                // За потреби можна розширити:
                // return name.endsWith(".txt") || name.endsWith(".md") || name.endsWith(".csv") || name.endsWith(".log");
            }
    
            private static int countWordsStartingWith(Path file, char letterLower) {
                int count = 0;
                try (BufferedReader br = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        // Слова: послідовності букв/цифр (підтримує \p{L} — всі алфавіти)
                        String[] tokens = line.split("[^\\p{L}\\p{Nd}]+");
                        for (String w : tokens) {
                            if (w.isEmpty()) continue;
                            char first = Character.toLowerCase(w.charAt(0));
                            if (first == letterLower) count++;
                        }
                    }
                } catch (IOException e) {
                    System.err.println("Cannot read file: " + file + " -> " + e.getMessage());
                }
                return count;
            }
        }
    
        public static void main(String[] args) throws Exception {
            if (args.length < 2) {
                System.err.println("Usage: java DirectoryWordCounter <directory> <letter> [outputFile]");
                System.exit(2);
            }
    
            Path root = Paths.get(args[0]);
            if (!Files.isDirectory(root)) {
                System.err.println("Not a directory: " + root.toAbsolutePath());
                System.exit(2);
            }
    
            char letterLower = Character.toLowerCase(args[1].charAt(0));
    
            Path out = (args.length >= 3) ? Paths.get(args[2]) : Paths.get("word_count_results.txt");
            // Нормалізуємо до абсолютного шляху, щоб коректно порівнювати
            Path outAbs = out.toAbsolutePath().normalize();
    
            Map<Path, Integer> results = new ConcurrentHashMap<>();
    
            long t0 = System.nanoTime();
            new DirectoryProcessor(root, letterLower, results, outAbs).run(); // старт з головного потоку
            long t1 = System.nanoTime();
    
            // Запис у файл (відсортуємо для стабільного порядку)
            List<Path> files = new ArrayList<>(results.keySet());
            files.sort(Comparator.comparing(Path::toString));
    
            try (BufferedWriter bw = Files.newBufferedWriter(outAbs, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
                for (Path f : files) {
                    bw.write(f.toAbsolutePath().normalize() + " -> " + results.get(f));
                    bw.newLine();
                }
            }
    
            // Вивід вмісту створеного файлу в консоль
            System.out.println("RESULT FILE: " + outAbs);
            System.out.println("-----");
            Files.lines(outAbs, StandardCharsets.UTF_8).forEach(System.out::println);
            System.out.println("-----");
            System.out.printf(Locale.US, "TIME %.2fms%n", (t1 - t0) / 1_000_000.0);
        }
    }
