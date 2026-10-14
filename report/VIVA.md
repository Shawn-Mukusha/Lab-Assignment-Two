# Viva preparation

1. **Class and object:** A class defines a type's fields and operations. An object is a runtime instance, normally allocated on the heap. Class metadata is managed by the JVM; local reference variables can refer to heap objects. Assignment copies a reference, not the object.

2. **Multiple inheritance:** Java permits one direct superclass, which avoids conflicting inherited class state and implementations. A class can implement multiple interfaces; conflicting unrelated default methods must be resolved by an override.

3. **Overloading and overriding:** Overloading supplies the same method name with different parameter lists, as in the four print methods. Overriding supplies a subclass implementation of an inherited instance method, as in animal sounds. Overloads are selected at compile time; overridden implementations dispatch at runtime.

4. **No superclass default constructor:** An omitted constructor call implies super(). If the superclass only has Animal(String), super() cannot be resolved. The subclass must explicitly call super(name).

5. **Abstract class or interface:** Use an abstract class for closely related objects sharing state, constructors or implementation, such as LibraryItem. Use an interface for a capability or contract shared across unrelated types, such as Flyable or Borrowable. Interfaces can supply defaults, but do not supply instance fields or constructors.

6. **equals and hashCode:** Equal objects must have equal hash codes. HashSet and HashMap use a hash to locate candidates before comparing equality. The Book ISBN example overrides both, so duplicate ISBNs are recognised. Different objects may share a hash code without being equal.

7. **Checked and unchecked exceptions:** Checked exceptions must be caught or declared, such as IOException and InvalidAgeException. Unchecked exceptions extend RuntimeException, such as IllegalArgumentException and NullPointerException. Checked exceptions suit recoverable conditions callers must acknowledge; unchecked exceptions commonly report invalid arguments or programming errors.

8. **Primitive generics and erasure:** Type parameters represent reference types, so Stack<Integer> is valid while Stack<int> is not. int[] is not an Integer[]. Erasure removes most generic type arguments from runtime representation and inserts casts or bridge methods where needed. Generic checking primarily occurs at compile time.

9. **final:** A final variable cannot be reassigned after initialisation. A final reference can still point to a mutable object. A final method cannot be overridden; a final class cannot be subclassed. ImmutablePerson combines a final class with private final immutable/value fields and no setters.

10. **Dynamic dispatch:** An overridden instance method is selected by the actual object's class even through a superclass reference. Fields are selected by the declared reference type; static methods are hidden rather than overridden and are also selected by type. The field/method dispatch exercise prints parent for the field and child for the method.

11. **Functional interfaces and lambdas:** A functional interface has one abstract method, such as Predicate.test. A lambda provides an implementation for that method; default/static methods do not add abstract methods. removeIf accepts a Predicate and checkout accepts a DiscountStrategy.

12. **Resource cleanup:** try-with-resources closes AutoCloseable resources automatically, including when execution fails. Multiple resources close in reverse acquisition order, and cleanup failures can become suppressed exceptions. A manually written finally block requires explicit cleanup and careful exception handling. finally still runs during a normal pending return, but cannot guarantee execution after forced process termination.
