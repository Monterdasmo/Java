import java.time.LocalDateTime;
import java.util.Random;

public class TurnstileService {
    private final RegistryService registry;
    private final StatsService stats;
    private final Random rnd = new Random();

    public TurnstileService(RegistryService registry, StatsService stats) {
        this.registry = registry;
        this.stats = stats;
    }

    public TurnstileResult swipe(long cardId) {
        // 5%: “дані не вдалося зчитати”
        if (rnd.nextInt(100) < 5) {
            stats.recordDenied(unknownKind());
            return new TurnstileResult(false, "Дані зчитати не вдалося (read error).");
        }

        SkiPassCard card = registry.find(cardId);
        if (card == null) {
            stats.recordDenied(unknownKind());
            return new TurnstileResult(false, "Картку не знайдено у реєстрі.");
        }

        LocalDateTime now = LocalDateTime.now();

        if (card.isBlocked()) {
            stats.recordDenied(card.getKind());
            return new TurnstileResult(false, "Прохід заборонено: картка заблокована.");
        }

        if (card.isExpired(now)) {
            stats.recordDenied(card.getKind());
            return new TurnstileResult(false, "Прохід заборонено: картка прострочена/ще не активна.");
        }

        if (!card.hasRides()) {
            stats.recordDenied(card.getKind());
            return new TurnstileResult(false, "Прохід заборонено: немає доступних поїздок.");
        }

        card.consumeRideIfNeeded();
        stats.recordAllowed(card.getKind());
        return new TurnstileResult(true, "Прохід дозволено.");
    }

    private PassKind unknownKind() {
        // технічний “заглушковий” тип для статистики, коли тип реальний невідомий
        return PassKind.rides(PassCategory.WEEKDAY, 0);
    }

    public record TurnstileResult(boolean allowed, String message) {}
}
