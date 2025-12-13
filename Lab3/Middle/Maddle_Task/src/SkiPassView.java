import java.util.List;
import java.util.Map;

public class SkiPassView {

    public void printMenu() {
        System.out.println("\n--- MENU (Ski-Pass) ---");
        System.out.println("1) Випустити (робочі дні, по часу)");
        System.out.println("2) Випустити (вихідні, по часу)");
        System.out.println("3) Випустити (робочі дні, по підйомах)");
        System.out.println("4) Випустити (вихідні, по підйомах)");
        System.out.println("5) Випустити сезонний абонемент");
        System.out.println("6) Заблокувати картку");
        System.out.println("7) Провести через турнікет");
        System.out.println("8) Показати всі картки");
        System.out.println("9) Статистика (загальна)");
        System.out.println("10) Статистика (по типах)");
        System.out.println("0) Вихід");
    }

    public void printIssued(SkiPassCard card) {
        System.out.println("Випущено: " + card);
    }

    public void printTurnstileResult(TurnstileService.TurnstileResult res) {
        System.out.println(res.allowed() ? "[ALLOW] " + res.message() : "[DENY] " + res.message());
    }

    public void printCards(List<SkiPassCard> cards) {
        if (cards.isEmpty()) {
            System.out.println("(реєстр порожній)");
            return;
        }
        for (SkiPassCard c : cards) {
            System.out.println(c);
        }
    }

    public void printInfo(String s) {
        System.out.println(s);
    }

    public <K> void printMap(String title, Map<K, Long> map) {
        System.out.println("\n" + title);
        if (map.isEmpty()) {
            System.out.println("(немає даних)");
            return;
        }
        map.forEach((k, v) -> System.out.println(k + " -> " + v));
    }
}
