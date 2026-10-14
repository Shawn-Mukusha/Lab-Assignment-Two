import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public final class LibraryApp {
    private LibraryApp() { }
    private static String ask(Scanner scanner, String prompt) {
        System.out.print(prompt + ": ");
        if (!scanner.hasNextLine()) throw new IllegalArgumentException("Input ended during operation");
        return scanner.nextLine().strip();
    }

    private static void list(List<?> values) {
        if (values.isEmpty()) System.out.println("No matching records.");
        else values.forEach(System.out::println);
    }

    public static void main(String[] args) {
        LibraryRepository repository = new LibraryRepository(Path.of(args.length > 0 ? args[0] : "data"));
        Library library;
        try { library = repository.load(); }
        catch (IOException exception) {
            System.err.println("Cannot load library; existing data will not be overwritten: " + exception.getMessage());
            return;
        }
        System.out.println("Loaded " + library.getItems().size() + " items, " + library.getMembers().size()
                + " members, " + library.getLoans().size() + " active loans.");
        try (Scanner scanner = new Scanner(System.in)) {
            boolean running = true;
            while (running) {
                System.out.println("\n1 Add item | 2 Register member | 3 Borrow | 4 Return | 5 Overdue | 6 Search | 7 List items | 0 Save and exit");
                if (!scanner.hasNextLine()) break;
                String choice = scanner.nextLine().strip();
                try {
                    switch (choice) {
                        case "1" -> {
                            String type = ask(scanner, "Type (BOOK/DVD)");
                            String id = ask(scanner, "Item id"), title = ask(scanner, "Title");
                            LibraryItem item = switch (type.toUpperCase(java.util.Locale.ROOT)) {
                                case "BOOK" -> new Book(id, title, ask(scanner, "Author"));
                                case "DVD" -> new Dvd(id, title, Integer.parseInt(ask(scanner, "Duration in minutes")));
                                default -> throw new IllegalArgumentException("Type must be BOOK or DVD");
                            };
                            library.addItem(item); System.out.println("Added " + item);
                        }
                        case "2" -> {
                            Member member = new Member(ask(scanner, "Member id"), ask(scanner, "Name"));
                            library.registerMember(member); System.out.println("Registered " + member);
                        }
                        case "3" -> {
                            String memberId = ask(scanner, "Member id"), itemId = ask(scanner, "Item id");
                            library.borrow(memberId, itemId);
                            System.out.println("Borrowed: " + library.findItem(itemId).getActiveLoan());
                        }
                        case "4" -> { library.giveBack(ask(scanner, "Item id")); System.out.println("Item returned."); }
                        case "5" -> list(library.overdueLoans(LocalDate.now()));
                        case "6" -> list(library.searchByTitle(ask(scanner, "Title search")));
                        case "7" -> list(library.getItems());
                        case "0" -> running = false;
                        default -> System.out.println("Choose 0 through 7.");
                    }
                } catch (IllegalArgumentException | ItemNotAvailableException | MemberLimitExceededException exception) {
                    System.out.println("Operation rejected: " + exception.getMessage());
                }
            }
        }
        try { repository.save(library); System.out.println("Library saved."); }
        catch (IOException exception) { System.err.println("Save failed: " + exception.getMessage()); }
    }
}
