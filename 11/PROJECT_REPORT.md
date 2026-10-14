# Library Management System: mini-project report

## Objective

Combine the four OOP pillars, interfaces, custom checked exceptions, maps, streams and CSV persistence in a usable command-line library application.

## Run

Requires JDK 17 or later. From this folder:

```powershell
javac --release 17 -encoding UTF-8 *.java
java LibraryApp
```

The default data directory is `data/` inside the current working directory. Supply a directory to keep a separate library:

```powershell
java LibraryApp my-library-data
```

The menu supports adding books/DVDs, registering members, borrowing, returning, listing overdue loans, searching titles and listing all items. Choose `0` to save and exit. End-of-input also saves completed operations.

## Requirements and implementation

| Manual requirement | Implementation |
|---|---|
| Private, validated Book, Member and Loan state; toString | [Book](Book.java), [Member](Member.java), [Loan](Loan.java); common item fields are encapsulated by LibraryItem |
| Abstract LibraryItem with Book and Dvd subclasses | [LibraryItem](LibraryItem.java), [Book](Book.java), [Dvd](Dvd.java) |
| Different loan periods through polymorphism | Books return 14 days; DVDs return 7 days; common borrowing code calls loanPeriodDays |
| Borrowable with borrow(Member) and giveBack | [Borrowable](Borrowable.java), implemented by LibraryItem and inherited by both concrete types |
| Custom availability and member-limit exceptions | [ItemNotAvailableException](ItemNotAvailableException.java), [MemberLimitExceededException](MemberLimitExceededException.java); maximum three active loans |
| Maps keyed by item/member ID | [Library](Library.java) owns separate maps and rejects duplicate IDs |
| CSV save on exit and reload on start | [LibraryRepository](LibraryRepository.java) stores items, members and active loans with original dates |
| Required menu, stream search, overdue list | [LibraryApp](LibraryApp.java); title search and overdue filtering use streams in Library |
| Class diagram and report | [Class diagram](CLASS_DIAGRAM.md) and this report |

## Design choices

Encapsulation protects identifiers, member loan lists, item availability and loan dates. A member's public loan getter returns an unmodifiable snapshot. LibraryItem has abstract type-specific methods, while the shared borrowing code creates a Loan and coordinates the member and item states. Book and Dvd fulfil that contract with different loan periods. Borrowable allows callers to depend on a capability.

Library handles lookup and operations, LibraryRepository handles file format and storage, and LibraryApp handles user input. This separates responsibilities and keeps the main borrowing method independent of concrete item types. Unknown IDs, duplicate IDs, invalid DVD durations, returning an available item and unsupported menu entries are reported without silently corrupting state. Both checked borrowing exceptions are caught by the menu.

The program keeps active loans rather than a historical archive. Returning an item releases the member's loan slot and clears the item's active loan. Loans store both their original borrowing date and due date; reloading does not extend a loan.

## Persistence

The repository writes UTF-8 CSV files with explicit headers:

| File | Columns |
|---|---|
| items.csv | type,id,title,detail |
| members.csv | id,name |
| loans.csv | itemId,memberId,borrowedOn,dueOn |

Book detail is its author; DVD detail is its duration in minutes. The [CSV codec](Csv.java) supports commas and quotes within fields through quoting and doubled quotes. Text must fit on one line. Loading validates headers, field counts, types, identifiers, date ordering, availability and the loan limit. Invalid saved data stops startup so the program does not replace it with an empty library.

Each file is written to a temporary file then replaced, using an atomic move where supported. The three-file save is not one atomic transaction: this educational application assumes a single running instance and normal process termination. A database transaction or a versioned snapshot would be appropriate for concurrent use and stronger crash recovery.

## Verification and actual output

[LibraryTests](LibraryTests.java) checks loan periods, validation, duplicates, missing IDs, unavailability, the three-loan limit, state preservation after failure, returning, overdue boundaries, search, immutable snapshots, quoted UTF-8 data, reloading and corrupted records. It performs 26 checks and ends with `ALL 26 LIBRARY CHECKS PASSED`.

The [assignment verification runner](../verification/Verify.ps1) also starts the menu in three separate processes: it creates two items and one member, borrows a book and saves; it reloads and returns the book; it reloads again and verifies zero active loans. This tests persistence across program runs rather than only within one Java object.

See the recorded evidence:

- [Library checks](../report/evidence/LibraryChecks.txt)
- [Initial menu session](../report/evidence/LibraryMenuFirstRun.txt)
- [Reload and return](../report/evidence/LibraryMenuReload.txt)
- [Return persisted across restart](../report/evidence/LibraryMenuReturnPersisted.txt)
- [Complete lab report with output captures](../report/LAB_REPORT.html)

## Design pattern demonstrations

[SingletonFactoryDemo](SingletonFactoryDemo.java) matches the manual's singleton configuration and email/SMS factory example. [StrategyObserverDemo](StrategyObserverDemo.java) swaps the checkout discount at runtime and publishes grade events to portal and advisor listeners. These are separate demonstrations: the mini-project uses abstraction, composition and polymorphism where needed rather than forcing all four patterns into the library service.

## Assumptions and conclusion

Books loan for 14 days and DVDs for 7 days because the manual requests different periods without prescribing values. Overdue means the due date is strictly before today, so an item due today is not overdue. Dates use the computer's local calendar. Members can hold at most three active loans across both item types.

The implementation meets the seven mini-project requirements and demonstrates the requested class relationships and persistence workflow. Source, repeatable checks, recorded outputs and the class diagram provide evidence for the submission and viva.
