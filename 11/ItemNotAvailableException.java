public class ItemNotAvailableException extends Exception {
    private static final long serialVersionUID = 1L;
    public ItemNotAvailableException(String id) { super("Item " + id + " is already on loan"); }
}
