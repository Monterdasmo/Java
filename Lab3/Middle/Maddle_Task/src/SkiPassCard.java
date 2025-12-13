import java.time.LocalDateTime;

public class SkiPassCard {
    private final long id;
    private final PassKind kind;
    private final LocalDateTime validFrom;
    private final LocalDateTime validTo;
    private boolean blocked;
    private Integer ridesRemaining; // null for TIME/SEASON

    public SkiPassCard(long id, PassKind kind, LocalDateTime validFrom, LocalDateTime validTo, Integer ridesRemaining) {
        this.id = id;
        this.kind = kind;
        this.validFrom = validFrom;
        this.validTo = validTo;
        this.ridesRemaining = ridesRemaining;
        this.blocked = false;
    }

    public long getId() { return id; }
    public PassKind getKind() { return kind; }
    public LocalDateTime getValidFrom() { return validFrom; }
    public LocalDateTime getValidTo() { return validTo; }
    public boolean isBlocked() { return blocked; }
    public Integer getRidesRemaining() { return ridesRemaining; }

    public void block() { this.blocked = true; }

    public boolean isExpired(LocalDateTime now) {
        return now.isBefore(validFrom) || now.isAfter(validTo);
    }

    public boolean hasRides() {
        return ridesRemaining == null || ridesRemaining > 0;
    }

    public void consumeRideIfNeeded() {
        if (ridesRemaining != null && ridesRemaining > 0) {
            ridesRemaining--;
        }
    }

    @Override
    public String toString() {
        return "SkiPassCard{" +
                "id=" + id +
                ", kind=" + kind +
                ", validFrom=" + validFrom +
                ", validTo=" + validTo +
                ", blocked=" + blocked +
                ", ridesRemaining=" + ridesRemaining +
                '}';
    }
}
