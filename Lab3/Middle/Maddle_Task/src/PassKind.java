import java.util.Objects;

public final class PassKind {
    private final PassCategory category;
    private final PassMode mode;
    private final PassTimeWindow window; // TIME/SEASON
    private final Integer ridesTotal;     // RIDES

    private PassKind(PassCategory category, PassMode mode, PassTimeWindow window, Integer ridesTotal) {
        this.category = category;
        this.mode = mode;
        this.window = window;
        this.ridesTotal = ridesTotal;
    }

    public static PassKind time(PassCategory category, PassTimeWindow window) {
        return new PassKind(category, PassMode.TIME, window, null);
    }

    public static PassKind rides(PassCategory category, int rides) {
        return new PassKind(category, PassMode.RIDES, null, rides);
    }

    public static PassKind season() {
        return new PassKind(PassCategory.SEASON, PassMode.TIME, PassTimeWindow.SEASON, null);
    }

    public PassCategory getCategory() { return category; }
    public PassMode getMode() { return mode; }
    public PassTimeWindow getWindow() { return window; }
    public Integer getRidesTotal() { return ridesTotal; }

    @Override
    public String toString() {
        if (category == PassCategory.SEASON) return "SEASON_PASS";
        if (mode == PassMode.RIDES) return category + "_RIDES_" + ridesTotal;
        return category + "_TIME_" + window;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof PassKind k)) return false;
        return category == k.category && mode == k.mode &&
                window == k.window && Objects.equals(ridesTotal, k.ridesTotal);
    }

    @Override
    public int hashCode() {
        return Objects.hash(category, mode, window, ridesTotal);
    }
}
