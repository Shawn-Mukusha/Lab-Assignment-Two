import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

/** Dependency-free checks for library rules, CSV restoration and invalid persisted data. */
public final class LibraryTests {
    private static int checks;
    private static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
        checks++;
        System.out.println("PASS: " + message);
    }
    @FunctionalInterface interface Action { void run() throws Exception; }
    private static void rejects(Class<? extends Exception> type, Action action, String message) throws Exception {
        try { action.run(); }
        catch (Exception exception) { check(type.isInstance(exception), message); return; }
        throw new AssertionError("Expected " + type.getSimpleName() + ": " + message);
    }
    public static void main(String[] args) throws Exception {
        Path directory = args.length == 0 ? Files.createTempDirectory("library-tests-") : Path.of(args[0]);
        Files.createDirectories(directory);
        Library library = new Library();
        library.addItem(new Book("B1", "Java, \"Objects\"", "José"));
        library.addItem(new Dvd("D1", "Java on Film", 90));
        library.addItem(new Book("B2", "Algorithms", "Ada"));
        library.addItem(new Book("B3", "Databases", "Ben"));
        library.registerMember(new Member("M1", "Rudo, \"Reader\""));
        library.registerMember(new Member("M2", "Tariro"));
        check(library.findItem("B1").loanPeriodDays() == 14 && library.findItem("D1").loanPeriodDays() == 7,
                "Books and DVDs use different loan periods");
        rejects(IllegalArgumentException.class, () -> new Book("", "Title", "Author"), "Blank item id rejected");
        rejects(IllegalArgumentException.class, () -> new Dvd("X", "Film", 0), "Invalid DVD duration rejected");
        rejects(IllegalArgumentException.class, () -> library.addItem(new Book("B1", "Other", "A")), "Duplicate item id rejected");
        rejects(IllegalArgumentException.class, () -> library.registerMember(new Member("M1", "Other")), "Duplicate member id rejected");
        rejects(IllegalArgumentException.class, () -> library.borrow("unknown", "B1"), "Unknown member rejected");
        rejects(IllegalArgumentException.class, () -> library.borrow("M1", "unknown"), "Unknown item rejected");
        library.borrow("M1", "B1");
        check(!library.findItem("B1").isAvailable() && library.findMember("M1").getLoans().size() == 1, "Borrow updates item and member");
        check(library.findItem("B1").getActiveLoan().getDueOn().equals(LocalDate.now().plusDays(14)), "Book due date is 14 days later");
        rejects(ItemNotAvailableException.class, () -> library.borrow("M2", "B1"), "Unavailable item rejected");
        library.borrow("M1", "D1"); library.borrow("M1", "B2");
        rejects(MemberLimitExceededException.class, () -> library.borrow("M1", "B3"), "Fourth loan rejected");
        check(library.findItem("B3").isAvailable() && library.findMember("M1").getLoans().size() == 3, "Failed borrow leaves state unchanged");
        library.giveBack("B1");
        check(library.findItem("B1").isAvailable() && library.findMember("M1").getLoans().size() == 2, "Return releases item and member slot");
        rejects(IllegalArgumentException.class, () -> library.giveBack("B1"), "Returning an available item rejected");
        library.findItem("B1").restoreLoan(library.findMember("M2"), LocalDate.of(2020, 1, 1), LocalDate.of(2020, 1, 15));
        check(library.overdueLoans(LocalDate.of(2020, 1, 15)).isEmpty(), "Due today is not overdue");
        check(library.overdueLoans(LocalDate.of(2020, 1, 16)).size() == 1, "Past-due loan found");
        check(library.searchByTitle("JAVA").size() == 2, "Stream search ignores case");
        rejects(UnsupportedOperationException.class, () -> library.getItems().clear(), "Item snapshot cannot be modified");
        rejects(UnsupportedOperationException.class, () -> library.findMember("M1").getLoans().clear(), "Member loans cannot be externally modified");
        LibraryRepository repository = new LibraryRepository(directory);
        repository.save(library);
        Library restored = repository.load();
        check(restored.getItems().size() == 4 && restored.getMembers().size() == 2 && restored.getLoans().size() == 3, "Items, members and active loans restored");
        check(restored.findItem("B1").getTitle().equals("Java, \"Objects\"") && ((Book)restored.findItem("B1")).getAuthor().equals("José"), "CSV quotes, commas and UTF-8 preserved");
        check(restored.findItem("B1").getActiveLoan().getDueOn().equals(LocalDate.of(2020, 1, 15)), "Original due date preserved after reload");
        restored.giveBack("B1"); repository.save(restored);
        check(repository.load().getLoans().size() == 2, "Returned loan stays returned after reload");
        check(Csv.parse(Csv.row("A,B", "quoted \"text\"", "")).equals(List.of("A,B", "quoted \"text\"", "")), "CSV codec round trip");
        Files.writeString(directory.resolve("loans.csv"), "itemId,memberId,borrowedOn,dueOn\nmissing,M1,2020-01-01,2020-01-15\n");
        rejects(IOException.class, repository::load, "Orphaned persisted loan rejected");
        check(Files.readString(directory.resolve("loans.csv")).contains("missing"), "Load failure does not overwrite saved data");
        System.out.println("ALL " + checks + " LIBRARY CHECKS PASSED");
    }
}
