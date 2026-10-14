import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.NoSuchElementException;

/** Additional boundary/invariant checks beyond executing the exercise main methods. */
public final class ExerciseChecks {
    private static int checks;
    private static void check(boolean condition, String label) {
        if (!condition) throw new AssertionError(label);
        checks++; System.out.println("PASS: " + label);
    }
    @FunctionalInterface interface Action { void run() throws Exception; }
    private static void rejects(Class<? extends Exception> type, Action action, String label) throws Exception {
        try { action.run(); }
        catch (Exception exception) { check(type.isInstance(exception), label); return; }
        throw new AssertionError("Expected failure: " + label);
    }
    public static void main(String[] args) throws Exception {
        int year = java.time.Year.now().getValue();
        check(!new CarAntique.Car("A", year - 25, 0).isAntique(), "Exactly 25 years is not antique");
        check(new CarAntique.Car("A", year - 26, 0).isAntique(), "26 years is antique");
        LargestRectangle.Rectangle rectangle = new LargestRectangle.Rectangle(3, 5);
        check(rectangle.area() == 15 && rectangle.perimeter() == 16, "Rectangle area and perimeter");
        check(new CircleConstructors.Circle().getRadius() == 1, "Default circle radius is one");
        rejects(IllegalArgumentException.class, () -> new CircleConstructors.Circle(-1), "Negative circle radius rejected");
        int before = CircleCounter.Circle.getCount(); new CircleCounter.Circle(); new CircleCounter.Circle(2);
        check(CircleCounter.Circle.getCount() == before + 2, "Constructor chaining counts each circle once");
        ValidatedStudent.Student student = new ValidatedStudent.Student("Ada", 50);
        rejects(IllegalArgumentException.class, () -> student.setMarks(-1), "Negative marks rejected");
        rejects(IllegalArgumentException.class, () -> student.setMarks(101), "Marks above 100 rejected");
        check(student.getMarks() == 50, "Invalid marks do not change student state");
        StudentGrades.Student graded = new StudentGrades.Student("Ada", 0);
        int[] boundaries = {49, 50, 59, 60, 74, 75, 100};
        String[] grades = {"F", "C", "C", "B", "B", "A", "A"};
        for (int i = 0; i < boundaries.length; i++) { graded.setMarks(boundaries[i]); check(graded.getGrade().equals(grades[i]), "Grade boundary " + boundaries[i]); }
        BankAccountHistory.BankAccount account = new BankAccountHistory.BankAccount("A", "Ada", 100);
        List<String> history = account.getTransactionHistory(); account.deposit(20);
        check(history.size() == 1 && account.getTransactionHistory().size() == 2, "History is a detached snapshot");
        rejects(UnsupportedOperationException.class, () -> history.add("bad"), "History is unmodifiable");
        check(!account.withdraw(121) && account.getBalance() == 120, "Overdraw leaves bank balance unchanged");
        check(Modifier.isFinal(ImmutablePerson.class.getModifiers()), "ImmutablePerson class is final");
        check(Arrays.stream(ImmutablePerson.class.getDeclaredFields()).allMatch(field -> Modifier.isPrivate(field.getModifiers()) && Modifier.isFinal(field.getModifiers())), "ImmutablePerson fields are private and final");
        check(Arrays.stream(ImmutablePerson.class.getMethods()).noneMatch(method -> method.getName().startsWith("set")), "ImmutablePerson has no setters");
        check(Math.abs(KelvinTemperature.Temperature.fromFahrenheit(32).getCelsius()) < 0.000001, "Kelvin storage preserves Fahrenheit conversion");
        rejects(IllegalArgumentException.class, () -> KelvinTemperature.Temperature.fromCelsius(-274), "Below absolute zero rejected");
        AgeValidation.validateAge(0); AgeValidation.validateAge(120);
        check(true, "Age endpoints 0 and 120 are valid");
        rejects(AgeValidation.InvalidAgeException.class, () -> AgeValidation.validateAge(121), "Age 121 rejected with checked exception");
        InsufficientFunds.BankAccount checked = new InsufficientFunds.BankAccount("B", "Ada", 10);
        rejects(InsufficientFunds.InsufficientFundsException.class, () -> checked.withdraw(11), "Insufficient funds throws checked exception");
        check(checked.getBalance() == 10, "Failed checked withdrawal preserves balance");
        GenericStack.Stack<Integer> stack = new GenericStack.Stack<>(); stack.push(1); stack.push(2);
        check(stack.peek() == 2 && stack.pop() == 2 && stack.pop() == 1 && stack.isEmpty(), "Generic stack follows LIFO");
        rejects(java.util.EmptyStackException.class, stack::peek, "Empty generic stack rejects peek");
        check(WordFrequency.countWords("Java, JAVA! it's fun.").get("java") == 2, "Word frequency handles case and punctuation");
        Integer[] values = {3, -1, 3, 0}; GenericBubbleSort.bubbleSort(values);
        check(Arrays.equals(values, new Integer[]{-1, 0, 3, 3}), "Generic sort handles negatives and duplicates");
        Integer[] empty = {}; GenericBubbleSort.bubbleSort(empty); check(empty.length == 0, "Generic sort accepts empty array");
        CustomLinkedList.LinkedList<String> list = new CustomLinkedList.LinkedList<>();
        rejects(NoSuchElementException.class, () -> list.iterator().next(), "Empty linked-list iterator rejects next");
        list.add("A"); java.util.Iterator<String> iterator = list.iterator(); list.add("B");
        rejects(ConcurrentModificationException.class, iterator::next, "Linked-list iterator detects structural modification");
        int[] best = new int[201];
        for (int cents = 1; cents <= 200; cents++) {
            best[cents] = 1000;
            for (int coin : new int[]{1, 5, 10, 25}) if (cents >= coin) best[cents] = Math.min(best[cents], best[cents - coin] + 1);
        }
        for (int cents = 0; cents <= 200; cents++) {
            List<CoinChange.Coin> coins = CoinChange.fewestCoins(cents);
            if (coins.size() != best[cents] || coins.stream().mapToInt(CoinChange.Coin::getCents).sum() != cents) throw new AssertionError("Coin change at " + cents);
        }
        check(true, "Coin change is minimal and totals correctly for every amount 0-200");
        rejects(IllegalArgumentException.class, () -> CoinChange.fewestCoins(-1), "Negative coin amount rejected");
        System.out.println("ALL " + checks + " EXERCISE CHECKS PASSED");
    }
}
