import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** Lab 11, Program 11.2: interchangeable algorithms and subscribed listeners. */
public class StrategyObserverDemo {
    interface DiscountStrategy { double apply(double price); }
    static class Checkout {
        private DiscountStrategy strategy;
        Checkout(DiscountStrategy strategy) { setStrategy(strategy); }
        void setStrategy(DiscountStrategy strategy) { this.strategy = Objects.requireNonNull(strategy); }
        double total(double price) { return strategy.apply(price); }
    }
    interface GradeListener { void onGradePublished(String course, int mark); }
    static class GradeBook {
        private final List<GradeListener> listeners = new ArrayList<>();
        void subscribe(GradeListener listener) { listeners.add(Objects.requireNonNull(listener)); }
        void publish(String course, int mark) {
            for (GradeListener listener : listeners) listener.onGradePublished(course, mark);
        }
    }
    public static void main(String[] args) {
        Checkout checkout = new Checkout(price -> price);
        System.out.println("Regular: " + checkout.total(200));
        checkout.setStrategy(price -> price * 0.90);
        System.out.println("Student: " + checkout.total(200));
        GradeBook book = new GradeBook();
        book.subscribe((course, mark) -> System.out.println("Student portal: " + course + " = " + mark));
        book.subscribe((course, mark) -> { if (mark < 50) System.out.println("Advisor alert: " + course + " failed"); });
        book.publish("OOP", 72); book.publish("Databases", 45);
    }
}
