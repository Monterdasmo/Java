import java.util.*;
import java.util.stream.Collectors;

public class BookService {
    private final BookRepository repo;
    private final Random rnd = new Random();

    private final List<String> preparedAuthors = List.of(
            "Joshua Bloch", "Robert C. Martin", "Martin Fowler", "Non Existing Author"
    );
    private final List<String> preparedPublishers = List.of(
            "Addison-Wesley", "Manning", "Non Existing Publisher"
    );
    private final List<Integer> preparedYears = List.of(1990, 2000, 2010, 2020);

    public BookService(BookRepository repo) {
        this.repo = repo;
    }

    public List<Book> getAll() {
        return repo.getAll();
    }

    public String randomAuthorQuery() {
        return preparedAuthors.get(rnd.nextInt(preparedAuthors.size()));
    }

    public String randomPublisherQuery() {
        return preparedPublishers.get(rnd.nextInt(preparedPublishers.size()));
    }

    public int randomYearQuery() {
        return preparedYears.get(rnd.nextInt(preparedYears.size()));
    }

    public List<Book> byAuthor(String author) {
        return repo.getAll().stream()
                .filter(b -> b.getAuthor().equalsIgnoreCase(author))
                .collect(Collectors.toList());
    }

    public List<Book> byPublisher(String publisher) {
        return repo.getAll().stream()
                .filter(b -> b.getPublisher().equalsIgnoreCase(publisher))
                .collect(Collectors.toList());
    }

    public List<Book> publishedAfter(int year) {
        return repo.getAll().stream()
                .filter(b -> b.getYear() > year)
                .collect(Collectors.toList());
    }

    public List<Book> sortByPublisher() {
        List<Book> list = repo.getAll();
        list.sort(Comparator.comparing(Book::getPublisher, String.CASE_INSENSITIVE_ORDER)
                .thenComparing(Book::getTitle, String.CASE_INSENSITIVE_ORDER));
        return list;
    }
}
