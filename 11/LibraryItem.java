import java.time.LocalDate;
import java.util.Objects;

/** Common state and borrowing logic; subclasses supply their own loan period. */
public abstract class LibraryItem implements Borrowable {
    private final String id;
    private final String title;
    private Loan activeLoan;

    protected LibraryItem(String id, String title) {
        this.id = Member.requireText(id, "Item id");
        this.title = Member.requireText(title, "Title");
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public boolean isAvailable() { return activeLoan == null; }
    public Loan getActiveLoan() { return activeLoan; }
    public abstract int loanPeriodDays();
    public abstract String itemType();
    public abstract String detail();

    @Override public final void borrow(Member member)
            throws ItemNotAvailableException, MemberLimitExceededException {
        LocalDate today = LocalDate.now();
        restoreLoan(member, today, today.plusDays(loanPeriodDays()));
    }

    // Also used when reloading persisted loans, preserving their original dates.
    final void restoreLoan(Member member, LocalDate borrowed, LocalDate due)
            throws ItemNotAvailableException, MemberLimitExceededException {
        Objects.requireNonNull(member, "member");
        if (!isAvailable()) throw new ItemNotAvailableException(id);
        member.checkLimit();
        Loan loan = new Loan(this, member, borrowed, due);
        member.attach(loan);
        activeLoan = loan;
    }

    @Override public final void giveBack() {
        if (isAvailable()) throw new IllegalArgumentException("Item " + id + " is not on loan");
        activeLoan.getMember().detach(activeLoan);
        activeLoan = null;
    }

    @Override public String toString() {
        return itemType() + " " + id + ": " + title + " [" + detail() + "] - "
                + (isAvailable() ? "available" : "due " + activeLoan.getDueOn());
    }
}
