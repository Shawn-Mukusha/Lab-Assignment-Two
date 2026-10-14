public final class Dvd extends LibraryItem {
    private final int durationMinutes;
    public Dvd(String id, String title, int durationMinutes) {
        super(id, title);
        if (durationMinutes <= 0) throw new IllegalArgumentException("DVD duration must be positive");
        this.durationMinutes = durationMinutes;
    }
    public int getDurationMinutes() { return durationMinutes; }
    @Override public int loanPeriodDays() { return 7; }
    @Override public String itemType() { return "DVD"; }
    @Override public String detail() { return Integer.toString(durationMinutes); }
}
