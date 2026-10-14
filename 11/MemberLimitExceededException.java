public class MemberLimitExceededException extends Exception {
    private static final long serialVersionUID = 1L;
    public MemberLimitExceededException(String id) { super("Member " + id + " has reached the limit of 3 loans"); }
}
