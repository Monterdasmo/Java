import java.util.List;

public class BookView {
    public void printHeader(String title) {
        System.out.println("\n=== " + title + " ===");
    }

    public void printMenu() {
        System.out.println("\n--- MENU (Books) ---");
        System.out.println("1) Показати початковий масив книг");
        System.out.println("2) Список книг зазначеного автора (random)");
        System.out.println("3) Список книг зазначеного видавництва (random)");
        System.out.println("4) Список книг, виданих пізніше року (random)");
        System.out.println("5) Відсортувати книги за видавництвами (Comparator)");
        System.out.println("0) Вихід");
    }

    public void printBooks(List<Book> books) {
        if (books.isEmpty()) {
            System.out.println("Нічого не знайдено.");
            return;
        }
        for (Book b : books) {
            System.out.println(b);
        }
    }

    public void printInfo(String msg) {
        System.out.println(msg);
    }
}
