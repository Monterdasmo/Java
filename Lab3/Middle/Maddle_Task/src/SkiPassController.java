import java.util.Scanner;

public class SkiPassController {
    private final RegistryService registry;
    private final TurnstileService turnstile;
    private final StatsService stats;
    private final SkiPassView view;

    public SkiPassController(RegistryService registry, TurnstileService turnstile, StatsService stats, SkiPassView view) {
        this.registry = registry;
        this.turnstile = turnstile;
        this.stats = stats;
        this.view = view;
    }

    public void run() {
        try (Scanner sc = new Scanner(System.in)) {
            while (true) {
                view.printMenu();
                int c = HardInput.readInt(sc, "Ваш вибір: ");

                try {
                    switch (c) {
                        case 1 -> view.printIssued(registry.issueWeekdayTime(askTimeWindow(sc, true)));
                        case 2 -> view.printIssued(registry.issueWeekendTime(askTimeWindow(sc, false)));
                        case 3 -> view.printIssued(registry.issueWeekdayRides(askRides(sc)));
                        case 4 -> view.printIssued(registry.issueWeekendRides(askRides(sc)));
                        case 5 -> view.printIssued(registry.issueSeason());
                        case 6 -> {
                            long id = HardInput.readLong(sc, "ID картки для блокування: ");
                            registry.block(id);
                            view.printInfo("ОК. Якщо картка існує — заблокована.");
                        }
                        case 7 -> {
                            long id = HardInput.readLong(sc, "ID картки для проходу: ");
                            view.printTurnstileResult(turnstile.swipe(id));
                        }
                        case 8 -> view.printCards(registry.allCards());
                        case 9 -> {
                            view.printInfo("ALLOWED total: " + stats.getAllowedTotal());
                            view.printInfo("DENIED total: " + stats.getDeniedTotal());
                        }
                        case 10 -> {
                            view.printMap("ALLOWED by kind:", stats.getAllowedByKind());
                            view.printMap("DENIED by kind:", stats.getDeniedByKind());
                        }
                        case 0 -> {
                            view.printInfo("Вихід.");
                            return;
                        }
                        default -> view.printInfo("Невідомий пункт меню.");
                    }
                } catch (Exception e) {
                    view.printInfo("Помилка: " + e.getMessage());
                }
            }
        }
    }

    private PassTimeWindow askTimeWindow(Scanner sc, boolean allowFiveDays) {
        System.out.println("Оберіть термін дії (по часу):");
        System.out.println("1) HALF_DAY_AM (09-13)");
        System.out.println("2) HALF_DAY_PM (13-17)");
        System.out.println("3) DAY");
        System.out.println("4) TWO_DAYS");
        if (allowFiveDays) System.out.println("5) FIVE_DAYS");

        int v = HardInput.readInt(sc, "Ваш вибір: ");
        return switch (v) {
            case 1 -> PassTimeWindow.HALF_DAY_AM;
            case 2 -> PassTimeWindow.HALF_DAY_PM;
            case 3 -> PassTimeWindow.DAY;
            case 4 -> PassTimeWindow.TWO_DAYS;
            case 5 -> {
                if (!allowFiveDays) throw new IllegalArgumentException("Для WEEKEND FIVE_DAYS недоступний.");
                yield PassTimeWindow.FIVE_DAYS;
            }
            default -> throw new IllegalArgumentException("Невірний вибір.");
        };
    }

    private int askRides(Scanner sc) {
        System.out.println("Оберіть кількість підйомів:");
        System.out.println("1) 10");
        System.out.println("2) 20");
        System.out.println("3) 50");
        System.out.println("4) 100");

        int v = HardInput.readInt(sc, "Ваш вибір: ");
        return switch (v) {
            case 1 -> 10;
            case 2 -> 20;
            case 3 -> 50;
            case 4 -> 100;
            default -> throw new IllegalArgumentException("Невірний вибір.");
        };
    }
}
