public class HardMain {
    public static void main(String[] args) {
        RegistryService registry = new RegistryService();
        StatsService stats = new StatsService();
        TurnstileService turnstile = new TurnstileService(registry, stats);
        SkiPassView view = new SkiPassView();

        new SkiPassController(registry, turnstile, stats, view).run();
    }
}
