import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalTime;
import java.util.concurrent.atomic.AtomicReference;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

public class HotReloadRunner {

    private static final String TARGET_CLASS = "TestModule";
    private static final String TARGET_SOURCE = "TestModule.java";

    public static void main(String[] args) throws Exception {
        // 1. Папка з .class цього раннера (типово: ...\Hard_Task\out\production\Hard_Task)
        Path classesDir = getClassesDir();

        // 2. Піднімаємося до кореня проекту: ...\Hard_Task
        Path projectRoot = classesDir.getParent().getParent().getParent();

        // 3. Папка src: ...\Hard_Task\src
        Path srcDir = projectRoot.resolve("src");

        // 4. Абсолютний шлях до TestModule.java: ...\Hard_Task\src\TestModule.java
        Path sourceFile = srcDir.resolve(TARGET_SOURCE).toAbsolutePath();

        // 5. Папка для компільованих класів: ...\Hard_Task\hot-classes
        Path outDir = projectRoot.resolve("hot-classes").toAbsolutePath();
        Files.createDirectories(outDir);

        System.out.println(ts() + " projectRoot = " + projectRoot);
        System.out.println(ts() + " srcDir      = " + srcDir);
        System.out.println(ts() + " sourceFile  = " + sourceFile);
        System.out.println(ts() + " classesOut  = " + outDir);

        // Якщо TestModule.java немає в src – створимо стартову версію
        ensureTestModuleExists(sourceFile);

        // Перша компіляція + створення інстансу
        compileOrThrow(sourceFile, outDir);
        AtomicReference<Object> currentInstance = new AtomicReference<>(newInstance(outDir));
        System.out.println(ts() + " Started. Current: " + currentInstance.get());
        System.out.println(ts() + " Edit and save: " + sourceFile);

        // WatchService: слідкуємо саме за src (де лежить TestModule.java)
        WatchService watchService = FileSystems.getDefault().newWatchService();
        srcDir.register(watchService, ENTRY_CREATE, ENTRY_MODIFY);

        long[] lastReloadTs = {0L};

        Thread watcher = new Thread(() -> {
            while (true) {
                try {
                    WatchKey key = watchService.take();
                    for (WatchEvent<?> ev : key.pollEvents()) {
                        Object ctx = ev.context();
                        if (!(ctx instanceof Path changed)) continue;

                        // Нас цікавить тільки TestModule.java у src
                        if (TARGET_SOURCE.equalsIgnoreCase(changed.getFileName().toString())) {
                            long now = System.currentTimeMillis();
                            if (now - lastReloadTs[0] < 250) continue; // debounce
                            lastReloadTs[0] = now;

                            sleepSilently(150);

                            Exception last = null;
                            boolean ok = false;
                            for (int i = 0; i < 4; i++) {
                                try {
                                    compileOrThrow(sourceFile, outDir);
                                    Object obj = newInstance(outDir);
                                    currentInstance.set(obj);
                                    System.out.println(ts() + " Reloaded: " + obj);
                                    ok = true;
                                    break;
                                } catch (Exception e) {
                                    last = e;
                                    sleepSilently(200);
                                }
                            }
                            if (!ok) {
                                System.out.println(ts() + " Reload FAILED: " +
                                        (last != null ? last.getMessage() : "unknown"));
                            }
                        }
                    }
                    key.reset();
                } catch (Exception e) {
                    System.out.println(ts() + " Watcher error: " + e.getMessage());
                }
            }
        }, "hot-reload-watcher");

        watcher.setDaemon(true);
        watcher.start();

        // Програма "висить" та періодично показує поточний стан
        while (true) {
            System.out.println(ts() + " Current: " + currentInstance.get());
            Thread.sleep(1000);
        }
    }

    /**
     * Знаходимо папку, де лежить HotReloadRunner.class:
     * типово: ...\Hard_Task\out\production\Hard_Task
     */
    private static Path getClassesDir() throws URISyntaxException {
        return Paths.get(
                HotReloadRunner.class
                        .getProtectionDomain()
                        .getCodeSource()
                        .getLocation()
                        .toURI()
        );
    }

    private static void ensureTestModuleExists(Path sourceFile) throws IOException {
        if (Files.exists(sourceFile)) return;

        String starter =
                "public class TestModule {\n" +
                        "    @Override\n" +
                        "    public String toString() {\n" +
                        "        return \"TestModule, version 1!\";\n" +
                        "    }\n" +
                        "}\n";

        Files.createDirectories(sourceFile.getParent());
        Files.writeString(sourceFile, starter, StandardCharsets.UTF_8, StandardOpenOption.CREATE_NEW);
        System.out.println(ts() + " Created starter: " + sourceFile);
    }

    private static void compileOrThrow(Path javaFile, Path outDir) {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            throw new IllegalStateException(
                    "JavaCompiler недоступний. Запускайте під JDK (Project SDK = JDK), не під JRE."
            );
        }

        int code = compiler.run(
                null, null, null,
                "-encoding", "UTF-8",
                "-d", outDir.toString(),
                javaFile.toString()
        );

        if (code != 0) {
            throw new IllegalStateException("Компіляція провалилась, код=" + code);
        }

        Path classFile = outDir.resolve(TARGET_CLASS + ".class");
        if (!Files.exists(classFile)) {
            throw new IllegalStateException("Не знайдено class-файл: " + classFile);
        }
    }

    private static Object newInstance(Path outDir) throws Exception {
        ByteClassLoader cl = new ByteClassLoader(outDir);
        Class<?> clazz = cl.loadClass(TARGET_CLASS);
        return clazz.getDeclaredConstructor().newInstance();
    }

    private static String ts() {
        return "[" + LocalTime.now().withNano(0) + "]";
    }

    private static void sleepSilently(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException ignored) {}
    }

    /**
     * Власний ClassLoader, який вантажить TestModule.class з hot-classes
     * і НЕ делегує system class loader-у (щоб не брати стару версію з out/production).
     */
    static class ByteClassLoader extends ClassLoader {
        private final Path classesDir;

        ByteClassLoader(Path classesDir) {
            // parent = bootstrap class loader (тільки JDK-класи),
            // щоб уникнути підхоплення TestModule із системного classpath
            super(null);
            this.classesDir = classesDir;
        }

        @Override
        protected Class<?> findClass(String name) throws ClassNotFoundException {
            try {
                Path classFile = classesDir.resolve(name + ".class");
                byte[] bytes = Files.readAllBytes(classFile);
                return defineClass(name, bytes, 0, bytes.length);
            } catch (Exception e) {
                throw new ClassNotFoundException("Не вдалося завантажити клас: " + name, e);
            }
        }
    }
}
