public class SimpleMain {
    public static void main(String[] args) {
        BookRepository repo = new BookRepository();
        BookService service = new BookService(repo);
        BookView view = new BookView();
        BookController controller = new BookController(service, view);

        controller.run();
    }
}
