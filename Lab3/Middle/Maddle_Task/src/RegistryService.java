import java.time.*;
import java.util.*;

public class RegistryService {
    private final Map<Long, SkiPassCard> registry = new HashMap<>();
    private long seq = 1000;

    public SkiPassCard issueWeekdayTime(PassTimeWindow window) {
        return issueTime(PassCategory.WEEKDAY, window);
    }

    public SkiPassCard issueWeekendTime(PassTimeWindow window) {
        return issueTime(PassCategory.WEEKEND, window);
    }

    public SkiPassCard issueWeekdayRides(int rides) {
        return issueRides(PassCategory.WEEKDAY, rides);
    }

    public SkiPassCard issueWeekendRides(int rides) {
        return issueRides(PassCategory.WEEKEND, rides);
    }

    public SkiPassCard issueSeason() {
        long id = ++seq;
        LocalDate today = LocalDate.now();
        LocalDate start = LocalDate.of(today.getYear(), 12, 1);
        LocalDate end = LocalDate.of(today.getYear() + 1, 3, 31);

        SkiPassCard card = new SkiPassCard(
                id,
                PassKind.season(),
                start.atStartOfDay(),
                end.atTime(LocalTime.MAX),
                null
        );
        registry.put(id, card);
        return card;
    }

    public void block(long id) {
        SkiPassCard card = registry.get(id);
        if (card != null) card.block();
    }

    public SkiPassCard find(long id) {
        return registry.get(id);
    }

    public List<SkiPassCard> allCards() {
        List<SkiPassCard> list = new ArrayList<>(registry.values());
        list.sort(Comparator.comparingLong(SkiPassCard::getId));
        return list;
    }

    private SkiPassCard issueTime(PassCategory category, PassTimeWindow window) {
        long id = ++seq;
        LocalDate today = LocalDate.now();
        LocalDateTime from;
        LocalDateTime to;

        switch (window) {
            case HALF_DAY_AM -> { from = today.atTime(9, 0);  to = today.atTime(13, 0); }
            case HALF_DAY_PM -> { from = today.atTime(13, 0); to = today.atTime(17, 0); }
            case DAY         -> { from = today.atTime(9, 0);  to = today.atTime(17, 0); }
            case TWO_DAYS    -> { from = today.atTime(9, 0);  to = today.plusDays(1).atTime(17, 0); }
            case FIVE_DAYS   -> { from = today.atTime(9, 0);  to = today.plusDays(4).atTime(17, 0); }
            default -> throw new IllegalArgumentException("Неприпустиме time window: " + window);
        }

        if (category == PassCategory.WEEKEND && window == PassTimeWindow.FIVE_DAYS) {
            throw new IllegalArgumentException("Для WEEKEND недоступний FIVE_DAYS.");
        }

        SkiPassCard card = new SkiPassCard(
                id,
                PassKind.time(category, window),
                from,
                to,
                null
        );
        registry.put(id, card);
        return card;
    }

    private SkiPassCard issueRides(PassCategory category, int rides) {
        long id = ++seq;
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime to = now.plusDays(30); // умовна валідність для “поїздок”

        SkiPassCard card = new SkiPassCard(
                id,
                PassKind.rides(category, rides),
                now.minusMinutes(1),
                to,
                rides
        );
        registry.put(id, card);
        return card;
    }
}
