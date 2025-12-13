import java.util.HashMap;
import java.util.Map;

public class StatsService {
    private long allowedTotal = 0;
    private long deniedTotal = 0;

    private final Map<PassKind, Long> allowedByKind = new HashMap<>();
    private final Map<PassKind, Long> deniedByKind = new HashMap<>();

    public void recordAllowed(PassKind kind) {
        allowedTotal++;
        allowedByKind.put(kind, allowedByKind.getOrDefault(kind, 0L) + 1);
    }

    public void recordDenied(PassKind kind) {
        deniedTotal++;
        deniedByKind.put(kind, deniedByKind.getOrDefault(kind, 0L) + 1);
    }

    public long getAllowedTotal() { return allowedTotal; }
    public long getDeniedTotal() { return deniedTotal; }

    public Map<PassKind, Long> getAllowedByKind() { return allowedByKind; }
    public Map<PassKind, Long> getDeniedByKind() { return deniedByKind; }
}
