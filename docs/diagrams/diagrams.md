# Design Diagrams: Campus Facility & Event Resource Booking System (CampusResourceHub)

This document contains all required architectural and UML diagrams specified in Section 4 of the VITyarthi Project Guidelines. The diagrams are rendered in standard GitHub Markdown Mermaid format.

---

## 1. System Architecture Diagram (Layered Architecture)

```mermaid
flowchart TD
    subgraph UI_Layer ["Presentation / User Interaction Layer"]
        CLI["Main Interactive CLI Console"]
        DemoRunner["Concurrency & Test Runner Harness"]
    end

    subgraph Service_Layer ["Business Logic & Coordination Layer"]
        BS["BookingService\n(Thread-Safe Monitors & Conflict Engine)"]
        US["UserService\n(User Lookup & Quota Checks)"]
        RS["ResourceService\n(Facility Catalog & Filter Query)"]
        AS["AnalyticsService\n(Java Streams Aggregator & Metrics)"]
    end

    subgraph Domain_Layer ["Object-Oriented Domain Entities"]
        UserModels["User Hierarchy\n(User, AdminUser, FacultyUser, StudentClubUser)"]
        ResourceModels["Resource Hierarchy\n(CampusResource, Auditorium, ComputerLab, ConferenceRoom)"]
        ReservationEntity["Reservation & TimeSlot (Value Object)"]
        StatusEnum["Enums: UserRole, ReservationStatus"]
    end

    subgraph Persistence_Layer ["Data & Storage Layer"]
        FileStore["StorageManager (Object Serialization)"]
        CSVStore["CSV Audit Exporter (data/*.csv)"]
        AuditTrail["In-Memory Synchronized Audit Ledger"]
    end

    CLI --> BS
    CLI --> US
    CLI --> RS
    CLI --> AS
    DemoRunner --> BS

    BS --> UserModels
    BS --> ResourceModels
    BS --> ReservationEntity
    BS --> FileStore
    BS --> AuditTrail
    AS --> CSVStore
```

---

## 2. Process Flow / Workflow Diagram (Reservation Lifecycle)

```mermaid
flowchart TD
    Start([User Initiates Slot Booking]) --> AuthCheck{User Authenticated & Exists?}
    AuthCheck -- No --> RejectUser[Throw BookingException: User Not Found]
    AuthCheck -- Yes --> ResourceCheck{Resource Exists & Operational?}
    
    ResourceCheck -- No --> RejectRes[Throw ResourceNotFoundException]
    ResourceCheck -- Yes --> QuotaCheck{Active Bookings < Role Quota?}
    
    QuotaCheck -- No --> RejectQuota[Throw QuotaExceededException]
    QuotaCheck -- Yes --> AcquireLock[Acquire Synchronized Resource Lock]
    
    AcquireLock --> ClashCheck{Overlapping Active Slots Found?}
    
    ClashCheck -- Yes --> PriorityCheck{Incoming User Priority > Existing User?}
    PriorityCheck -- Yes --> Preempt[Preempt Lower Priority Booking & Mark CANCELLED]
    PriorityCheck -- No --> RejectConflict[Throw SlotConflictException]
    
    ClashCheck -- No --> CreateRes[Generate RES-XXXX & Confirm Slot]
    Preempt --> CreateRes
    
    CreateRes --> LogAudit[Log Action into Audit Ledger]
    LogAudit --> ReleaseLock[Release Synchronized Lock]
    ReleaseLock --> ReturnSuccess([Return Confirmed Reservation to User])
    
    RejectUser --> EndFail([Booking Failed])
    RejectRes --> EndFail
    RejectQuota --> EndFail
    RejectConflict --> EndFail
```

---

## 3. UML Use Case Diagram

```mermaid
flowchart LR
    Faculty((Faculty Member))
    StudentClub((Student Club Coordinator))
    Admin((Campus Administrator))

    subgraph SystemBoundary ["Campus Resource Hub System"]
        UC1(["Browse Campus Facilities & Availability"])
        UC2(["Book Auditorium / Lab / Conference Room"])
        UC3(["View Personal Active Bookings & Quotas"])
        UC4(["Cancel Own Reservation"])
        UC5(["Preempt Lower-Priority Booking"])
        UC6(["Override & Cancel Any Reservation"])
        UC7(["Toggle Facility Maintenance Status"])
        UC8(["View System Analytics & Utilization (Streams)"])
        UC9(["Export Audit Logs to CSV"])
    end

    StudentClub --> UC1
    StudentClub --> UC2
    StudentClub --> UC3
    StudentClub --> UC4

    Faculty --> UC1
    Faculty --> UC2
    Faculty --> UC3
    Faculty --> UC4
    Faculty --> UC5

    Admin --> UC1
    Admin --> UC6
    Admin --> UC7
    Admin --> UC8
    Admin --> UC9
```

---

## 4. UML Class Diagram (Core OOP Model)

```mermaid
classDiagram
    direction TB

    class UserRole {
        <<enumeration>>
        ADMIN
        FACULTY
        STUDENT_CLUB
        -int priorityLevel
        -int maxConcurrentBookings
        +getPriorityLevel() int
        +getMaxConcurrentBookings() int
    }

    class User {
        <<abstract>>
        -String userId
        -String fullName
        -String email
        -String department
        -UserRole role
        +getUserId() String
        +getFullName() String
        +getRole() UserRole
        +getMaxConcurrentBookings() int
        +canApproveReservations()* boolean
    }

    class AdminUser {
        +canApproveReservations() boolean
    }

    class FacultyUser {
        -String designation
        +getDesignation() String
        +canApproveReservations() boolean
    }

    class StudentClubUser {
        -String clubName
        -String facultyAdvisorName
        +getClubName() String
        +getFacultyAdvisorName() String
        +canApproveReservations() boolean
    }

    User <|-- AdminUser
    User <|-- FacultyUser
    User <|-- StudentClubUser
    User --> UserRole

    class CampusResource {
        <<abstract>>
        -String resourceId
        -String name
        -String buildingLocation
        -int capacity
        -boolean operational
        +getResourceId() String
        +getName() String
        +getCapacity() int
        +isOperational() boolean
        +getResourceType()* String
        +getHourlyCostRate()* double
    }

    class Auditorium {
        -boolean hasStageAudioVisual
        -boolean hasAirConditioning
        +getResourceType() String
        +getHourlyCostRate() double
    }

    class ComputerLab {
        -int workstationCount
        -String operatingSystem
        -boolean hasGpuAcceleration
        +getResourceType() String
        +getHourlyCostRate() double
    }

    class ConferenceRoom {
        -boolean hasVideoConferenceKit
        -boolean hasSmartBoard
        +getResourceType() String
        +getHourlyCostRate() double
    }

    CampusResource <|-- Auditorium
    CampusResource <|-- ComputerLab
    CampusResource <|-- ConferenceRoom

    class TimeSlot {
        -LocalDateTime startTime
        -LocalDateTime endTime
        +getStartTime() LocalDateTime
        +getEndTime() LocalDateTime
        +getDurationMinutes() long
        +overlapsWith(TimeSlot) boolean
        +compareTo(TimeSlot) int
    }

    class ReservationStatus {
        <<enumeration>>
        PENDING
        CONFIRMED
        REJECTED
        CANCELLED
    }

    class Reservation {
        -String reservationId
        -String userId
        -String resourceId
        -TimeSlot timeSlot
        -String purpose
        -int priorityWeight
        -ReservationStatus status
        -LocalDateTime requestedAt
        -String adminRemarks
        +isActive() boolean
    }

    Reservation --> TimeSlot
    Reservation --> ReservationStatus

    class Reservable {
        <<interface>>
        +bookResource(userId, resourceId, slot, purpose) Reservation
        +cancelReservation(reservationId, userId) boolean
        +isAvailable(resourceId, slot) boolean
    }

    class Auditable {
        <<interface>>
        +logAction(action, performedBy, details) void
        +getAuditLogs() List~String~
    }

    class BookingService {
        -UserService userService
        -ResourceService resourceService
        -Map~String, Reservation~ reservationRegistry
        -Map~String, Object~ resourceLocks
        -List~String~ auditLogs
        +bookResource(userId, resourceId, slot, purpose) Reservation
        +cancelReservation(reservationId, userId) boolean
        +isAvailable(resourceId, slot) boolean
    }

    Reservable <|.. BookingService
    Auditable <|.. BookingService
    BookingService --> Reservation
```

---

## 5. UML Sequence Diagram (Multi-Threaded Concurrent Booking Resolution)

```mermaid
sequenceDiagram
    autonumber
    actor Thread1 as Worker Thread 1 (Club GDSC)
    actor Thread2 as Worker Thread 2 (Club Robotics)
    participant BS as BookingService
    participant Lock as Resource Monitor Lock (AUD-102)
    participant Reg as ReservationRegistry

    Note over Thread1, Thread2: Both threads fire concurrently at the exact same millisecond
    Thread1->>BS: bookResource("club_gdsc", "AUD-102", Slot_09to11, "GDSC Meet")
    Thread2->>BS: bookResource("club_robotics", "AUD-102", Slot_09to11, "Robotics Workshop")

    BS->>Lock: Thread 1 acquires synchronized(lock)
    Note over BS, Lock: Thread 2 blocks on synchronized monitor!
    
    BS->>Reg: Check overlapping active slots for AUD-102
    Reg-->>BS: Empty (No clashes found)
    BS->>Reg: Store Reservation #RES-1003 (Status: CONFIRMED)
    BS-->>Thread1: Return Confirmed Reservation #RES-1003
    BS->>Lock: Thread 1 releases synchronized(lock)

    Note over BS, Lock: Thread 2 unblocks and enters synchronized critical section
    BS->>Lock: Thread 2 acquires synchronized(lock)
    BS->>Reg: Check overlapping active slots for AUD-102
    Reg-->>BS: Found clashing active booking #RES-1003
    BS->>BS: Evaluate Priority (GDSC: 1 vs Robotics: 1) -> No Preemption
    BS-->>Thread2: Throw SlotConflictException ("Slot already reserved by RES-1003")
    BS->>Lock: Thread 2 releases synchronized(lock)
```

---

## 6. Entity Relationship (ER) / Storage Schema Diagram

```mermaid
erDiagram
    USER ||--o{ RESERVATION : places
    CAMPUS_RESOURCE ||--o{ RESERVATION : "is booked for"
    USER ||--o{ AUDIT_LOG : triggers

    USER {
        string userId PK
        string fullName
        string email
        string department
        string role "ADMIN / FACULTY / STUDENT_CLUB"
        int maxConcurrentBookings
        string extraDetails "designation or clubName"
    }

    CAMPUS_RESOURCE {
        string resourceId PK
        string name
        string buildingLocation
        int capacity
        boolean operational
        string resourceType "Auditorium / ComputerLab / ConferenceRoom"
        double hourlyCostRate
    }

    RESERVATION {
        string reservationId PK
        string userId FK
        string resourceId FK
        datetime startTime
        datetime endTime
        long durationMinutes
        string status "PENDING / CONFIRMED / CANCELLED"
        string purpose
        int priorityWeight
        datetime requestedAt
        string adminRemarks
    }

    AUDIT_LOG {
        int logId PK
        datetime timestamp
        string action "BOOKING_CREATED / CANCELLED / PREEMPTION"
        string performedBy FK
        string details
    }
```