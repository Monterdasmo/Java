package ua.task10;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ua.task10.i18n.I18n;
import ua.task10.reflection.StringMutator;

import java.util.Locale;
import java.util.Scanner;

public class Main {
    private static final Logger log = LogManager.getLogger(Main.class);

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        // за замовчуванням укр
        I18n i18n = new I18n(new Locale("uk"));

        log.info("Application started");

        while (true) {
            System.out.println();
            System.out.println(i18n.t("app.title"));
            System.out.println(i18n.t("menu.choose"));
            System.out.println(i18n.t("menu.reflection"));
            System.out.println(i18n.t("menu.logging"));
            System.out.println(i18n.t("menu.lang"));
            System.out.println(i18n.t("menu.exit"));
            System.out.print(i18n.t("input.choice"));

            int choice = readInt(sc, i18n);
            switch (choice) {
                case 1 -> reflectionTask(sc, i18n);
                case 2 -> loggingTask(i18n);
                case 3 -> chooseLanguage(sc, i18n);
                case 0 -> {
                    log.info("Application exit requested");
                    System.out.println(i18n.t("bye"));
                    return;
                }
                default -> System.out.println(i18n.t("error.invalid"));
            }
        }
    }

    private static int readInt(Scanner sc, I18n i18n) {
        while (true) {
            String s = sc.nextLine().trim();
            try {
                return Integer.parseInt(s);
            } catch (NumberFormatException e) {
                System.out.println(i18n.t("error.number"));
                System.out.print(i18n.t("input.choice"));
            }
        }
    }

    // -------------------------
    // Task 10.1 Reflection
    // -------------------------
    private static void reflectionTask(Scanner sc, I18n i18n) {
        log.info("Reflection task started");

        System.out.println();
        System.out.println(i18n.t("reflection.title"));

        // 1) String як літерал
        String literal = "OriginalLiteral";
        System.out.println(i18n.t("reflection.literal") + literal);

        // 2) String з клавіатури
        System.out.print(i18n.t("reflection.enter"));
        String input = sc.nextLine();

        // Replacement value: programmatic or keyboard
        System.out.println(i18n.t("reflection.replace.ask"));
        System.out.println(i18n.t("reflection.replace.program"));
        System.out.println(i18n.t("reflection.replace.keyboard"));
        System.out.print(i18n.t("input.choice"));
        int replChoice = readInt(sc, i18n);

        String replacement;
        if (replChoice == 1) {
            replacement = "HACKED";
        } else if (replChoice == 2) {
            System.out.print(i18n.t("reflection.enter"));
            replacement = sc.nextLine();
        } else {
            System.out.println(i18n.t("error.invalid"));
            return;
        }

        // mutate both strings
        try {
            System.out.println(i18n.t("reflection.before") + literal);
            StringMutator.mutateTo(literal, replacement);
            System.out.println(i18n.t("reflection.after") + literal);

            System.out.println(i18n.t("reflection.before") + input);
            StringMutator.mutateTo(input, replacement);
            System.out.println(i18n.t("reflection.after") + input);

            log.warn("Reflection mutation executed (this is WARN -> console + file)");

        } catch (Throwable ex) {
            // IMPORTANT: on Java 9+ might throw InaccessibleObjectException without --add-opens
            log.error("Reflection failed", ex);
            System.out.println(i18n.tf("error.reflection", ex.getClass().getSimpleName() + ": " + ex.getMessage()));
            System.out.println(i18n.t("reflection.note"));
        }
    }

    // -------------------------
    // Task 10.2 Logging (Log4j)
    // -------------------------
    private static void loggingTask(I18n i18n) {
        System.out.println();
        System.out.println(i18n.t("logging.title"));

        // DEBUG не потрапить ні в консоль, ні в файл (через ThresholdFilter)
        log.debug("DEBUG: you should NOT see this in console/file with current config");

        // INFO піде тільки у файл (logs/app.log), але НЕ в консоль
        log.info("INFO: should go to file only (console denies INFO)");

        // WARN піде і в консоль, і в файл
        log.warn("WARN: should go to both console and file");

        // ERROR піде і в консоль, і в файл (і може мати stacktrace)
        try {
            int a = 1 / 0;
            System.out.println(a);
        } catch (Exception e) {
            log.error("ERROR: demo exception (divide by zero)", e);
        }

        System.out.println(i18n.t("logging.done"));
    }

    // -------------------------
    // Task 10.3 Internationalization (ResourceBundle)
    // -------------------------
    private static void chooseLanguage(Scanner sc, I18n i18n) {
        System.out.println();
        System.out.println(i18n.tf("lang.current", i18n.getLocale().toLanguageTag()));
        System.out.println(i18n.t("lang.choose"));
        System.out.println(i18n.t("lang.uk"));
        System.out.println(i18n.t("lang.en"));
        System.out.println(i18n.t("lang.cs"));
        System.out.print(i18n.t("input.choice"));

        int choice = readInt(sc, i18n);
        switch (choice) {
            case 1 -> {
                i18n.setLocale(new Locale("uk"));
                log.info("Language switched to uk");
            }
            case 2 -> {
                i18n.setLocale(Locale.ENGLISH);
                log.info("Language switched to en");
            }
            case 3 -> {
                i18n.setLocale(new Locale("cs"));
                log.info("Language switched to cs");
            }
            default -> System.out.println(i18n.t("error.invalid"));
        }
    }
}
