import java.util.*;

public class BookRepository {
    private final List<Book> books;

    public BookRepository() {
        this.books = new ArrayList<>(seedBooks());
    }

    public List<Book> getAll() {
        return new ArrayList<>(books);
    }

    private List<Book> seedBooks() {
        List<Book> list = new ArrayList<>();
        list.add(new Book("Clean Code", "Robert C. Martin", "Prentice Hall", 2008, 464, 35.50));
        list.add(new Book("Effective Java", "Joshua Bloch", "Addison-Wesley", 2018, 416, 45.00));
        list.add(new Book("Java Concurrency in Practice", "Brian Goetz", "Addison-Wesley", 2006, 432, 42.00));
        list.add(new Book("Head First Java", "Kathy Sierra", "O'Reilly", 2005, 720, 30.00));
        list.add(new Book("Design Patterns", "Erich Gamma", "Addison-Wesley", 1994, 395, 50.00));
        list.add(new Book("Refactoring", "Martin Fowler", "Addison-Wesley", 2018, 448, 47.00));
        list.add(new Book("The Pragmatic Programmer", "Andrew Hunt", "Addison-Wesley", 1999, 352, 40.00));
        list.add(new Book("Spring in Action", "Craig Walls", "Manning", 2018, 520, 44.00));
        list.add(new Book("Algorithms", "Robert Sedgewick", "Addison-Wesley", 2011, 976, 55.00));
        list.add(new Book("Microservices Patterns", "Chris Richardson", "Manning", 2018, 520, 49.00));
        return list;
    }
}
