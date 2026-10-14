public final class Book extends LibraryItem {
    private final String author;
    public Book(String id, String title, String author) {
        super(id, title);
        this.author = Member.requireText(author, "Author");
    }
    public String getAuthor() { return author; }
    @Override public int loanPeriodDays() { return 14; }
    @Override public String itemType() { return "BOOK"; }
    @Override public String detail() { return author; }
}
