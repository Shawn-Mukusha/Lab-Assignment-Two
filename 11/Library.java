import java.time.LocalDate;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/** The service owns item/member lookup and delegates borrowing to the Borrowable contract. */
public final class Library {
    private final Map<String, LibraryItem> items = new LinkedHashMap<>();
    private final Map<String, Member> members = new LinkedHashMap<>();

    public void addItem(LibraryItem item) {
        if (items.putIfAbsent(item.getId(), item) != null) throw new IllegalArgumentException("Duplicate item id");
    }
    public void registerMember(Member member) {
        if (members.putIfAbsent(member.getId(), member) != null) throw new IllegalArgumentException("Duplicate member id");
    }
    public LibraryItem findItem(String id) {
        LibraryItem item = items.get(id);
        if (item == null) throw new IllegalArgumentException("Unknown item: " + id);
        return item;
    }
    public Member findMember(String id) {
        Member member = members.get(id);
        if (member == null) throw new IllegalArgumentException("Unknown member: " + id);
        return member;
    }
    public void borrow(String memberId, String itemId)
            throws ItemNotAvailableException, MemberLimitExceededException {
        Member member = findMember(memberId);
        findItem(itemId).borrow(member);
    }
    public void giveBack(String itemId) { findItem(itemId).giveBack(); }
    public List<LibraryItem> getItems() { return List.copyOf(items.values()); }
    public List<Member> getMembers() { return List.copyOf(members.values()); }
    public List<Loan> getLoans() {
        return items.values().stream().filter(item -> !item.isAvailable())
                .map(LibraryItem::getActiveLoan).toList();
    }
    public List<Loan> overdueLoans(LocalDate today) {
        return getLoans().stream().filter(loan -> loan.isOverdue(today))
                .sorted(Comparator.comparing(Loan::getDueOn)).toList();
    }
    public List<LibraryItem> searchByTitle(String text) {
        String query = Member.requireText(text, "Search term").toLowerCase(Locale.ROOT);
        return items.values().stream()
                .filter(item -> item.getTitle().toLowerCase(Locale.ROOT).contains(query))
                .sorted(Comparator.comparing(LibraryItem::getTitle)).toList();
    }
}
