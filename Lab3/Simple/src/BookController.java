import java.util.List;
import java.util.Scanner;

public class BookController {
    private final BookService service;
    private final BookView view;

    public BookController(BookService service, BookView view) {
        this.service = service;
        this.view = view;
    }

    public void run() {
        view.printHeader("Початковий масив книг");
        view.printBooks(service.getAll());

        try (Scanner sc = new Scanner(System.in)) {
            while (true) {
                view.printMenu();
                int choice = BookInput.readInt(sc, "Ваш вибір: ");

                switch (choice) {
                    case 1 -> {
                        view.printHeader("Початковий масив книг");
                        view.printBooks(service.getAll());
                    }
                    case 2 -> {
                        String author = service.randomAuthorQuery();
                        view.printHeader("Пошук за автором (random): " + author);
                        List<Book> res = service.byAuthor(author);
                        view.printBooks(res);
                    }
                    case 3 -> {
                        String pub = service.randomPublisherQuery();
                        view.printHeader("Пошук за видавництвом (random): " + pub);
                        List<Book> res = service.byPublisher(pub);
                        view.printBooks(res);
                    }
                    case 4 -> {
                        int year = service.randomYearQuery();
                        view.printHeader("Книги після року (random): " + year);
                        List<Book> res = service.publishedAfter(year);
                        view.printBooks(res);
                    }
                    case 5 -> {
                        view.printHeader("Сортування за видавництвом (Comparator)");
                        view.printBooks(service.sortByPublisher());
                    }
                    case 0 -> {
                        view.printInfo("Вихід.");
                        return;
                    }
                    default -> view.printInfo("Невідомий пункт меню.");
                }
            }
        }
    }
}
