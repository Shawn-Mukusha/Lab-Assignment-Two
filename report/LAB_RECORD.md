# Java OOP lab record

## Submission contents

All five exercises for each of Labs 1–10 are in the matching numbered folders. The additional [Lab 11](../11/) contains the manual's pattern demonstrations and the Library Management System. Each source comment identifies the original exercise number; the [README mapping](../README.md) links every exercise in order.

The [full HTML lab report](LAB_REPORT.html) contains the objective, source, actual output and conclusion for each exercise. Its PNG images are rendered console output from real executions rather than desktop screenshots. Raw transcripts are preserved alongside them in [evidence](evidence/).

## Objectives and conclusions

| Lab | Objective | Conclusion from the exercised programs |
|---|---|---|
| 1 | Model state, behaviour, references and composition | Three cars keep independent state; antique checks use a strict age comparison; rectangle aliasing changes the shared object; a null address cannot be dereferenced. |
| 2 | Initialise objects and distinguish instance from class state | Constructor chaining gives a default radius; a static counter is shared; employee copies get new IDs; static and instance blocks run in the predicted order; named factories state the temperature unit. |
| 3 | Enforce encapsulation and valid state | Mark setters reject invalid values; grades are derived; account history is an unmodifiable snapshot; ImmutablePerson itself is final with final fields and no setters; kelvin is hidden behind conversion methods. |
| 4 | Reuse and specialise superclass behaviour | Person code is shared by students and teachers; multilevel constructors run parent first; an explicit super(name) corrects the missing-default-constructor example; composed stacks expose only stack operations. |
| 5 | Compare overloading, overriding and runtime dispatch | Overloads accept all requested types; animal and payroll loops dispatch polymorphically; fields use declared types; payment processing demonstrates both pattern matching and interface dispatch. |
| 6 | Use abstract classes and interface contracts | Shapes and accounts supply different implementations of abstract operations; Bird and Aeroplane share Flyable; Comparable and Comparator provide different orderings; conflicting default methods are resolved explicitly. |
| 7 | Handle exceptions without leaving invalid state | Division handles numeric input, non-numeric input and zero; array and age validation handle invalid boundaries; rejected withdrawals preserve balance; finally executes before return. |
| 8 | Use generics, lists, sets and maps | Ten integers sum to 55; sets remove duplicate names; stacks follow LIFO; word frequencies sort in descending order; generic bubble sort works with Integer and String arrays. |
| 9 | Use nested types, enums, records and Object contracts | Enums restrict choices; records compare by value; ISBN equality removes duplicate books; a nested-node linked list supports for-each and valid iterator failures; minimum coin change is validated for amounts 0–200. |
| 10 | Read/write files and process collections with lambdas and streams | Five input lines round-trip; an explicit predicate removes evens; text counts are reported; above-average names sort alphabetically; CSV learners group into three saved band reports. |
| 11 | Combine OOP and persistence in a mini-project | The library supports the required menu, item-type loan periods, maximum three loans, returns, overdue searches and CSV reloads; separate examples demonstrate the four design patterns. |

## Prediction exercises

- Lab 1 Exercise 4: r1.width becomes 9.0 because r1 and r2 point to the same rectangle. [Actual output](evidence/01-04-RectangleAliasing.txt).
- Lab 2 Exercise 4: static block 1, static block 2, instance block 1, instance block 2 and constructor appear for the first object; only the instance blocks and constructor repeat for the second. [Actual output](evidence/02-04-InitialisationOrder.txt).
- Lab 4 Exercise 3: Vehicle, Car, then ElectricCar constructors run. [Actual output](evidence/04-03-VehicleInheritance.txt).
- Lab 7 Exercise 5: finally prints before the returned value is displayed. [Actual output](evidence/07-05-FinallyReturn.txt).

## Verification

The [verification script](../verification/Verify.ps1) compiles all Java source with `--release 17 -Xlint:all`, runs every exercise with suitable input, and checks the requested observable results. It also verifies division failures, short input without a partial write, missing files, all three band reports, underlying exercise invariants, the 26 mini-project checks, and persistence across three separate menu sessions.

- [Exercise boundary and invariant results](evidence/ExerciseBoundaryChecks.txt)
- [Library checks](evidence/LibraryChecks.txt)
- [Library first run](evidence/LibraryMenuFirstRun.txt)
- [Library reload and return](evidence/LibraryMenuReload.txt)
- [Library return still persisted](evidence/LibraryMenuReturnPersisted.txt)
- [Machine-readable verification summary](evidence/verification-summary.json)

Tests verify selected requirements and boundaries; they do not imply exhaustive correctness for every possible input. The mini-project assumes one running process and normal saving. Per-file replacement protects individual CSV files, but the three-file save is not a database transaction.

## Assumptions

The manual does not prescribe grade cutoffs or exact loan periods. Lab 3 uses A >=75, B >=60, C >=50, otherwise F. Lab 10 uses GPA bands A >=3.5, B >=3.0, otherwise C. Books loan for 14 days and DVDs for 7 days. A loan due today is not yet overdue. Text character counts include line endings and count Unicode code points.

## Overall conclusion

The exercises cover the four OOP pillars and the supporting Java features. The library mini-project combines those features into a working application with validated state, observable failure paths and persistent data. [Viva preparation](VIVA.md) covers the manual's oral questions.
