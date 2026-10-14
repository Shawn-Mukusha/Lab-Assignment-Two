import java.util.Locale;

/** Lab 11, Program 11.1: one configuration instance and interface-based creation. */
public class SingletonFactoryDemo {
    static final class AppConfig {
        private static final AppConfig INSTANCE = new AppConfig();
        private final String institution = "Example University";
        private AppConfig() { }
        public static AppConfig getInstance() { return INSTANCE; }
        public String getInstitution() { return institution; }
    }
    interface Notification { void send(String message); }
    static class EmailNotification implements Notification {
        @Override public void send(String message) { System.out.println("Email: " + message); }
    }
    static class SmsNotification implements Notification {
        @Override public void send(String message) { System.out.println("SMS: " + message); }
    }
    static class NotificationFactory {
        static Notification create(String type) {
            return switch (type.toLowerCase(Locale.ROOT)) {
                case "email" -> new EmailNotification();
                case "sms" -> new SmsNotification();
                default -> throw new IllegalArgumentException("Unknown type: " + type);
            };
        }
    }
    public static void main(String[] args) {
        AppConfig a = AppConfig.getInstance(), b = AppConfig.getInstance();
        System.out.println("Same instance? " + (a == b) + " -> " + a.getInstitution());
        NotificationFactory.create("email").send("Lab 11 is open");
        NotificationFactory.create("SMS").send("Lab 11 is open");
    }
}
