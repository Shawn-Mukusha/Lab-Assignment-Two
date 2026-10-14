import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** CSV persistence is separate from business logic and the menu. */
public final class LibraryRepository {
    private final Path directory;
    public LibraryRepository(Path directory) { this.directory = directory; }

    private List<List<String>> read(String filename, String header, int columns) throws IOException {
        Path path = directory.resolve(filename);
        if (!Files.exists(path)) return List.of();
        List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
        if (lines.isEmpty() || !lines.get(0).equals(header)) throw new IOException("Invalid header in " + filename);
        List<List<String>> rows = new ArrayList<>();
        for (int i = 1; i < lines.size(); i++) {
            try {
                List<String> row = Csv.parse(lines.get(i));
                if (row.size() != columns) throw new IllegalArgumentException("Wrong column count");
                rows.add(row);
            } catch (IllegalArgumentException exception) {
                throw new IOException(filename + " row " + (i + 1) + ": " + exception.getMessage(), exception);
            }
        }
        return rows;
    }

    public Library load() throws IOException {
        Library library = new Library();
        try {
            for (List<String> row : read("items.csv", "type,id,title,detail", 4)) {
                LibraryItem item = switch (row.get(0)) {
                    case "BOOK" -> new Book(row.get(1), row.get(2), row.get(3));
                    case "DVD" -> new Dvd(row.get(1), row.get(2), Integer.parseInt(row.get(3)));
                    default -> throw new IllegalArgumentException("Unknown item type: " + row.get(0));
                };
                library.addItem(item);
            }
            for (List<String> row : read("members.csv", "id,name", 2)) {
                library.registerMember(new Member(row.get(0), row.get(1)));
            }
            for (List<String> row : read("loans.csv", "itemId,memberId,borrowedOn,dueOn", 4)) {
                library.findItem(row.get(0)).restoreLoan(library.findMember(row.get(1)),
                        LocalDate.parse(row.get(2)), LocalDate.parse(row.get(3)));
            }
        } catch (IllegalArgumentException | java.time.DateTimeException
                | ItemNotAvailableException | MemberLimitExceededException exception) {
            throw new IOException("Invalid saved library: " + exception.getMessage(), exception);
        }
        return library;
    }

    public void save(Library library) throws IOException {
        Files.createDirectories(directory);
        List<String> items = new ArrayList<>(List.of("type,id,title,detail"));
        for (LibraryItem item : library.getItems()) items.add(Csv.row(item.itemType(), item.getId(), item.getTitle(), item.detail()));
        List<String> members = new ArrayList<>(List.of("id,name"));
        for (Member member : library.getMembers()) members.add(Csv.row(member.getId(), member.getName()));
        List<String> loans = new ArrayList<>(List.of("itemId,memberId,borrowedOn,dueOn"));
        for (Loan loan : library.getLoans()) loans.add(Csv.row(loan.getItem().getId(), loan.getMember().getId(),
                loan.getBorrowedOn().toString(), loan.getDueOn().toString()));
        write("items.csv", items); write("members.csv", members); write("loans.csv", loans);
    }

    private void write(String filename, List<String> lines) throws IOException {
        Path temporary = Files.createTempFile(directory, filename, ".tmp");
        try {
            Files.write(temporary, lines, StandardCharsets.UTF_8);
            try {
                Files.move(temporary, directory.resolve(filename), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException exception) {
                Files.move(temporary, directory.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
            }
        } finally { Files.deleteIfExists(temporary); }
    }
}
