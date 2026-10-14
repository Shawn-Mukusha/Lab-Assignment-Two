# Library Management System class diagram

```mermaid
classDiagram
    class Borrowable {
        <<interface>>
        +borrow(Member)
        +giveBack()
    }
    class LibraryItem {
        <<abstract>>
        -String id
        -String title
        -Loan activeLoan
        +loanPeriodDays() int
        +isAvailable() boolean
        +borrow(Member)
        +giveBack()
    }
    class Book {
        -String author
        +loanPeriodDays() int
    }
    class Dvd {
        -int durationMinutes
        +loanPeriodDays() int
    }
    class Member {
        -String id
        -String name
        -List~Loan~ loans
        +getLoans() List~Loan~
    }
    class Loan {
        -LibraryItem item
        -Member member
        -LocalDate borrowedOn
        -LocalDate dueOn
        +isOverdue(LocalDate) boolean
    }
    class Library {
        -Map~String,LibraryItem~ items
        -Map~String,Member~ members
        +borrow(String,String)
        +giveBack(String)
        +searchByTitle(String) List~LibraryItem~
        +overdueLoans(LocalDate) List~Loan~
    }
    class LibraryRepository {
        -Path directory
        +load() Library
        +save(Library)
    }
    class Csv {
        +row(String[]) String
        +parse(String) List~String~
    }
    class LibraryApp {
        +main(String[])
    }
    class ItemNotAvailableException
    class MemberLimitExceededException

    Borrowable <|.. LibraryItem
    LibraryItem <|-- Book
    LibraryItem <|-- Dvd
    Library "1" o-- "0..*" LibraryItem : stores by ID
    Library "1" o-- "0..*" Member : stores by ID
    LibraryItem "1" --> "0..1" Loan : active loan
    Member "1" --> "0..3" Loan : active loans
    Loan --> "1" LibraryItem : item
    Loan --> "1" Member : borrower
    LibraryApp ..> Library : menu operations
    LibraryApp ..> LibraryRepository : load and save
    LibraryRepository ..> Library : persist
    LibraryRepository ..> Csv : encode and parse
    LibraryItem ..> ItemNotAvailableException : throws
    LibraryItem ..> MemberLimitExceededException : throws
```

An open triangle with a solid line denotes inheritance; an open triangle with a dashed line denotes interface implementation. The member has zero to three active loans; an item has at most one. The service stores existing objects, while each Loan links a member to one item and its dates. The CSV repository and menu depend on the service rather than duplicating borrowing rules.
