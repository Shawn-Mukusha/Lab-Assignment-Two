import java.util.ArrayList;
import java.util.List;

public final class Member {
    public static final int MAX_LOANS = 3;
    private final String id;
    private final String name;
    private final List<Loan> loans = new ArrayList<>();

    public Member(String id, String name) {
        this.id = requireText(id, "Member id");
        this.name = requireText(name, "Member name");
    }

    static String requireText(String value, String label) {
        if (value == null || value.isBlank() || value.contains("\n") || value.contains("\r")) {
            throw new IllegalArgumentException(label + " must be nonblank and on one line");
        }
        return value.strip();
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public List<Loan> getLoans() { return List.copyOf(loans); }

    void checkLimit() throws MemberLimitExceededException {
        if (loans.size() >= MAX_LOANS) throw new MemberLimitExceededException(id);
    }

    void attach(Loan loan) { loans.add(loan); }
    void detach(Loan loan) { loans.remove(loan); }

    @Override public String toString() { return id + ": " + name + " (" + loans.size() + "/3 loans)"; }
}
