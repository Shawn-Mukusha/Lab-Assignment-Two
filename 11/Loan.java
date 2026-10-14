import java.time.LocalDate;
import java.util.Objects;

/** An immutable association between one member and one library item. */
public final class Loan {
    private final LibraryItem item;
    private final Member member;
    private final LocalDate borrowedOn;
    private final LocalDate dueOn;

    public Loan(LibraryItem item, Member member, LocalDate borrowedOn, LocalDate dueOn) {
        this.item = Objects.requireNonNull(item, "item");
        this.member = Objects.requireNonNull(member, "member");
        this.borrowedOn = Objects.requireNonNull(borrowedOn, "borrowedOn");
        this.dueOn = Objects.requireNonNull(dueOn, "dueOn");
        if (!dueOn.isAfter(borrowedOn)) throw new IllegalArgumentException("Due date must follow borrowing date");
    }

    public LibraryItem getItem() { return item; }
    public Member getMember() { return member; }
    public LocalDate getBorrowedOn() { return borrowedOn; }
    public LocalDate getDueOn() { return dueOn; }
    public boolean isOverdue(LocalDate today) { return dueOn.isBefore(today); }
    @Override public String toString() {
        return item.getId() + " [" + item.getTitle() + "] -> " + member.getName()
                + ", borrowed " + borrowedOn + ", due " + dueOn;
    }
}
