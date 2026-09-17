# VITyarthi Flipped Course Evaluation: Project Report

---

# CAMPUS FACILITY & EVENT RESOURCE BOOKING SYSTEM
### An Enterprise Object-Oriented Java Architecture for University Venue Coordination and Concurrency Control

**Course Title:** Programming in Java  
**Course Code:** CSE1007 / JAVA201  
**Project Category:** Flipped Course Evaluation (Build Your Own Project)  
**Submission Date:** September 2026  
**Institution:** Vellore Institute of Technology (VIT) / VITyarthi  

---

## Table of Contents
1. [Cover Page / Metadata](#1-cover-page)
2. [Introduction](#2-introduction)
3. [Problem Statement](#3-problem-statement)
4. [Functional Requirements](#4-functional-requirements)
5. [Non-Functional Requirements](#5-non-functional-requirements)
6. [System Architecture](#6-system-architecture)
7. [Design Diagrams](#7-design-diagrams)
   - 7.1 System Architecture Diagram
   - 7.2 Process Flow / Workflow Diagram
   - 7.3 UML Use Case Diagram
   - 7.4 UML Class Diagram
   - 7.5 UML Sequence Diagram (Concurrency Resolution)
   - 7.6 Entity Relationship (ER) Diagram
8. [Design Decisions & Rationale](#8-design-decisions--rationale)
9. [Implementation Details](#9-implementation-details)
10. [Screenshots & Execution Results](#10-screenshots--execution-results)
11. [Testing Approach & Test Matrix](#11-testing-approach--test-matrix)
12. [Challenges Faced & Solutions](#12-challenges-faced--solutions)
13. [Learnings & Key Takeaways](#13-learnings--key-takeaways)
14. [Future Enhancements](#14-future-enhancements)
15. [References](#15-references)

---

## 1. Cover Page
* **Project Name:** CampusResourceHub (Campus Facility & Resource Booking System)
* **Author / Candidate:** Student Submission
* **Subject:** Programming in Java
* **Target Audience:** University Administration, Departmental Faculty, Student Club Coordinators
* **Repository:** GitHub Ready Repository with `README.md`, `statement.md`, `src/`, `test/`, and `docs/`

---

## 2. Introduction
In university ecosystems, physical infrastructure represents one of the most critical and capital-intensive assets. From 800-seat grand auditoriums and specialized GPU artificial intelligence laboratories to executive conference boardrooms, optimal scheduling is essential to support dynamic academic lectures, technical symposia, hackathons, and administrative governance.

Traditionally, universities manage these reservations via fragmented channels: ad-hoc email chains, physical sign-up registers, or uncoordinated spreadsheets. These manual approaches inevitably collapse under high demand, leading to double-bookings, conflicts between faculty and student clubs, lack of accountability, and unrecorded resource hoarding.

**CampusResourceHub** is an enterprise-grade software system developed in Java. It leverages core and advanced Java paradigms—including Object-Oriented Design (Inheritance, Polymorphism, Abstraction, Encapsulation), the Java Collections Framework, Multi-Threaded Synchronization, Custom Domain Exceptions, Java 8+ Streams API, and File I/O Serialization—to deliver an automated, conflict-free, and audited campus resource reservation system.

---

## 3. Problem Statement
The operational inefficiencies of legacy campus reservation methods can be summarized into four key challenges:
1. **Concurrency Race Conditions:** Simultaneous requests for the same venue at the same time slot frequently lead to double bookings.
2. **Priority Disparity:** Informal student club practice sessions can unintentionally block mission-critical faculty academic symposia due to a lack of an automated priority preemption mechanism.
3. **Unregulated Quotas:** Without strict systemic checks, active clubs may monopolize prime venues weeks in advance, exhausting availability for peer organizations.
4. **Audit and Analytics Vacuum:** Campus administrators lack real-time visibility into venue utilization rates, maintenance downtimes, and historical audit logs.

---

## 4. Functional Requirements

The system provides three major functional modules:

### Module 1: User Identity & Role-Based Access Control (RBAC)
* **Tiered User Model:** Provides an abstract `User` base with concrete implementations: `AdminUser`, `FacultyUser`, and `StudentClubUser`.
* **Dynamic Booking Quota Enforcement:** Imposes algorithmic limits on active reservations (e.g., maximum 3 concurrent bookings for student clubs, 10 for faculty, and unlimited for campus administrators).
* **Identity Context Switching:** Enables switching between faculty, student, and administrator roles during runtime to verify permission scopes.

### Module 2: Resource Catalog & Thread-Safe Reservation Engine
* **Specialized Venue Modeling:** Models campus spaces via an abstract `CampusResource` base with concrete subclasses: `Auditorium` (AV & stage acoustics), `ComputerLab` (OS platforms & GPU acceleration), and `ConferenceRoom` (smart boards & video conferencing kits).
* **Temporal Overlap Collision Detection:** Precision time-window validation using `TimeSlot` and `LocalDateTime` to prevent overlapping reservations.
* **Synchronized Concurrency Lock:** Implements per-resource monitor locks (`ConcurrentHashMap<String, Object>`) ensuring mutual exclusion when concurrent threads vie for the same venue.
* **Algorithmic Preemption:** Allows high-priority faculty requests to automatically preempt lower-priority student club bookings, transitioning preempted bookings to `CANCELLED` status with audit trail notes.

### Module 3: Reporting, Analytics & CSV Export
* **Streams API Aggregation:** Calculates real-time utilization metrics (total booked hours per facility type and booking status distributions) using Java 8+ Streams, grouping collectors, and lambdas.
* **Audit Trail Ledger:** Synchronized in-memory transaction ledger recording all booking creations, cancellations, and preemption interventions with millisecond timestamps.
* **CSV Disk Export:** Produces formatted reports (`data/campus_booking_report.csv`) for administrative records.

---

## 5. Non-Functional Requirements
1. **Performance:** Sub-10ms conflict detection and slot reservation leveraging in-memory `ConcurrentHashMap` indexing and high-efficiency temporal comparison logic.
2. **Reliability & Thread Safety:** Zero double-bookings under concurrent multi-threaded workloads, verified using `CountDownLatch` multi-thread stress tests.
3. **Maintainability & Modularity:** Adherence to SOLID principles; distinct separation of Model, Service, Utility, Exception, and Presentation layers across 10+ decoupled Java classes.
4. **Error Handling & Fault Tolerance:** A clear checked exception hierarchy (`SlotConflictException`, `QuotaExceededException`, `ResourceNotFoundException`, `UnauthorizedAccessException`) guaranteeing explicit error messages rather than unhandled crashes.
5. **Usability:** A clean, intuitive interactive Console CLI featuring automated demo seeding, formatted tabular displays, and feedback prompts.

---

## 6. System Architecture

The application adopts a **4-Tier Layered Architecture**:

1. **Presentation / Interface Layer:** Handles command-line input, renders formatted status summaries, and facilitates interactive demo navigation (`Main.java`, `TestRunner.java`).
2. **Service / Business Logic Layer:** Enforces domain rules, concurrency synchronization, priority scheduling, quota limits, and Streams-based analytics (`BookingService`, `UserService`, `ResourceService`, `AnalyticsService`).
3. **Domain / Model Layer:** Defines domain entities, value objects, and inheritance hierarchies (`User`, `CampusResource`, `TimeSlot`, `Reservation`, `UserRole`, `ReservationStatus`).
4. **Persistence & Utility Layer:** Provides file I/O serialization (`StorageManager`), CSV generation, and dataset bootstrapping (`DemoDataSeeder`).

---

## 7. Design Diagrams

### 7.1 System Architecture Diagram
```
+-------------------------------------------------------------------------+
|                       Presentation Layer (CLI / Tests)                  |
|                 Main.java  |  TestRunner.java  |  Interactive Menu      |
+-------------------------------------------------------------------------+
                                    |
                                    v
+-------------------------------------------------------------------------+
|                          Service Layer (Business Logic)                 |
|   BookingService (Locks/Clashes)  |  UserService  |  ResourceService    |
|   AnalyticsService (Streams API)  |  Reservable   |  Auditable          |
+-------------------------------------------------------------------------+
                                    |
                                    v
+-------------------------------------------------------------------------+
|                            Domain Model Layer                           |
|   User Hierarchy: AdminUser, FacultyUser, StudentClubUser               |
|   Resource Hierarchy: Auditorium, ComputerLab, ConferenceRoom           |
|   Value Objects: TimeSlot, Reservation, UserRole, ReservationStatus     |
+-------------------------------------------------------------------------+
                                    |
                                    v
+-------------------------------------------------------------------------+
|                       Persistence & Storage Layer                       |
|   StorageManager (Serialization) | CSV Exporter | Audit Trail Ledger    |
+-------------------------------------------------------------------------+
```

### 7.2 Process Flow / Workflow Diagram
1. User submits Resource ID, Start Date/Time, Duration, and Purpose.
2. System checks user authenticity and active booking quota.
3. System verifies resource operational status.
4. System enters `synchronized(resourceLock)` monitor.
5. Checks for overlapping confirmed slots:
   - If overlap exists and incoming user is Faculty while incumbent is Student Club: **Preempt** existing booking, mark CANCELLED, and allocate slot.
   - If overlap exists and no priority advantage: Throw **`SlotConflictException`**.
   - If slot is free: Confirm booking and generate unique ID (`RES-XXXX`).
6. Appends entry to synchronized audit ledger.
7. Releases monitor lock and returns confirmed reservation.

### 7.3 UML Use Case Diagram
* **Actors:**
  * `Faculty Member`: Browse venues, book slots, view own quotas, cancel own reservations, preempt lower-priority club slots.
  * `Student Club Coordinator`: Browse venues, book slots within quota (max 3), view own bookings, cancel own bookings.
  * `Campus Administrator`: View all venues, toggle maintenance status, cancel any booking, view Streams analytics, export CSV audit reports.

### 7.4 UML Class Diagram Hierarchy
* **Abstract Class `User`**: Subclasses: `AdminUser`, `FacultyUser`, `StudentClubUser`. Composed with `UserRole`.
* **Abstract Class `CampusResource`**: Subclasses: `Auditorium`, `ComputerLab`, `ConferenceRoom`.
* **Interfaces**:
  * `Reservable`: Methods `bookResource()`, `cancelReservation()`, `isAvailable()`.
  * `Auditable`: Methods `logAction()`, `getAuditLogs()`.
* **Entity `Reservation`**: Contains `TimeSlot`, `ReservationStatus`, `priorityWeight`.
* **Core Engine `BookingService`**: Implements `Reservable`, `Auditable`. Manages `ConcurrentHashMap<String, Reservation>` and `ConcurrentHashMap<String, Object>`.

### 7.5 UML Sequence Diagram (Concurrent Race Condition Resolution)
```
Thread 1 (Club GDSC)             BookingService               Thread 2 (Club Robotics)
       |                                |                                |
       |--- bookResource(AUD-102) ----->|                                |
       |                                |-- (Acquires Resource Lock)     |
       |                                |<--- bookResource(AUD-102) -----|
       |                                |     (Blocks on Monitor)        |
       |                                |                                |
       |    [Slot Validated & Saved]    |                                |
       |<-- Returns Confirmed RES-1003 -|                                |
       |    [Releases Resource Lock]    |                                |
       |                                |-- (Thread 2 Enters Lock)       |
       |                                |-- [Detects Overlap with 1003]  |
       |                                |-- [Evaluates Equal Priority]   |
       |                                |--- Throws SlotConflictException|
       |                                |    [Releases Resource Lock]    |
       v                                v                                v
```

### 7.6 Entity Relationship (ER) Schema
* `USER` (1) <----> (M) `RESERVATION`
* `CAMPUS_RESOURCE` (1) <----> (M) `RESERVATION`
* `USER` (1) <----> (M) `AUDIT_LOG`

---

## 8. Design Decisions & Rationale

| Architectural Decision | Chosen Strategy | Technical Rationale |
| :--- | :--- | :--- |
| **Concurrency Protection** | Fine-grained per-resource monitor locks | Avoids coarse global system locks; two users booking *different* rooms proceed concurrently without latency. |
| **Temporal Representation** | `java.time.LocalDateTime` & `TimeSlot` | Modern immutable Java Date/Time API eliminates legacy mutable `java.util.Date` bugs; implements `Comparable` for natural chronological sorting. |
| **Interface Segregation** | `Reservable` and `Auditable` | Decouples reservation execution from compliance logging, allowing modular testing and mocking. |
| **Exception Hierarchy** | Checked Domain Exceptions (`BookingException`) | Forces client code and CLI controllers to handle business failure cases explicitly (e.g., quota exceeded vs. slot conflict). |
| **Reporting Engine** | Java 8+ Streams API (`groupingBy`, `summingDouble`) | Demonstrates functional programming paradigms in Java, generating metrics dynamically without boilerplate iterative loops. |

---

## 9. Implementation Details

### Core Classes Implemented:
1. `model/UserRole.java`: Enum containing priority weights and quota constants.
2. `model/User.java`: Abstract class providing encapsulation for identity attributes.
3. `model/AdminUser.java`, `FacultyUser.java`, `StudentClubUser.java`: Concrete user implementations.
4. `model/CampusResource.java`: Abstract class enforcing polymorphic cost and classification contracts.
5. `model/Auditorium.java`, `ComputerLab.java`, `ConferenceRoom.java`: Concrete resource entities with specialized hardware attributes.
6. `model/TimeSlot.java`: Value object encapsulating overlap logic:
   ```java
   public boolean overlapsWith(TimeSlot other) {
       return this.startTime.isBefore(other.endTime) && this.endTime.isAfter(other.startTime);
   }
   ```
7. `model/ReservationStatus.java`: State machine enum (`PENDING`, `CONFIRMED`, `REJECTED`, `CANCELLED`).
8. `model/Reservation.java`: Immutable reservation entity with tracking metadata.
9. `exception/BookingException.java` and specialized subclasses (`SlotConflictException`, etc.).
10. `service/BookingService.java`: Thread-safe reservation engine with preemption logic.
11. `service/AnalyticsService.java`: Java Streams aggregation and CSV export pipeline.
12. `util/StorageManager.java`: Java Object Serialization persistence engine.
13. `util/DemoDataSeeder.java`: Realistic campus bootstrapping engine.
14. `Main.java`: 10-option interactive menu console application.

---

## 10. Screenshots & Execution Results

### 10.1 Interactive CLI Menu
```
===============================================================================
     CAMPUS FACILITY & RESOURCE BOOKING SYSTEM (CampusResourceHub)            
     Flipped Course Evaluation Project | Programming in Java                  
===============================================================================

>>> Current Active User: [STUDENT_CLUB] Rohan Mehta (club_gdsc) - Dept: CSE
-------------------------------------------------------------------------------
1. Browse All Campus Facilities & Resources
2. Book a Resource / Facility Slot
3. View My Active Reservations & Quota
4. Cancel a Reservation
5. Switch User Account (Faculty / Club / Admin)
6. View System Analytics & Utilization (Streams API)
7. Export Audit & Reservation Report to CSV
8. View Live Audit Log Trail
9. Run Concurrency Stress Demonstration (Live Race Condition Test)
0. Exit
-------------------------------------------------------------------------------
```

### 10.2 Resource Catalog View
```
--- CAMPUS RESOURCES CATALOG ---
* [AUD-101] Sir Visvesvaraya Grand Auditorium   | Location: Main Tech Block     | Capacity:  800 | Rate: Rs.1200.0/hr
* [AUD-102] Kalam Mini Auditorium               | Location: Academic Block 2    | Capacity:  250 | Rate: Rs.1200.0/hr
* [LAB-CS-204] General Software Engineering Lab | Location: IT Tower 2nd Floor  | Capacity:   75 | Rate: Rs.650.0/hr
* [CONF-DEAN-1] Senate Boardroom                | Location: Administrative Wing | Capacity:   30 | Rate: Rs.350.0/hr
* [LAB-CS-301] Advanced AI & Cloud Computing Lab| Location: IT Tower 3rd Floor  | Capacity:   60 | Rate: Rs.650.0/hr
```

### 10.3 Streams API Analytics Output
```
--- SYSTEM ANALYTICS & UTILIZATION (STREAMS API) ---
Reservations Breakdown by Status:
  * CONFIRMED    : 2 bookings

Booked Hours by Facility Category:
  * Computer Lab : 3.00 total hours
  * Auditorium   : 2.00 total hours
```

### 10.4 Live Multi-Threaded Concurrency Test Run
```
--- CONCURRENCY RACE CONDITION TEST ---
Simulating 2 simultaneous threads attempting to book the SAME auditorium at the EXACT same second...
>> [Thread-1 GDSC] SUCCESS: Acquired slot -> RES-1003
>> [Thread-2 Robotics] REJECTED: CONFLICT: Slot is already reserved by Booking #RES-1003 (User: club_gdsc).
>> Concurrency check complete: Double-booking successfully prevented by synchronization monitor!
```

---

## 11. Testing Approach & Test Matrix

An automated test harness (`TestRunner.java`) was implemented to test critical boundary conditions and concurrency without third-party dependencies:

| Test ID | Test Scenario | Input / Precondition | Expected Behavior | Actual Result | Status |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **TC-01** | Valid Booking Creation | Faculty `fac_cse_01`, `AUD-101`, free slot | Returns confirmed `Reservation` entity | Booking confirmed (`RES-1001`) | **PASS** |
| **TC-02** | Temporal Slot Collision | 2 overlapping requests for `LAB-1` | Throws `SlotConflictException` | Exception caught & verified | **PASS** |
| **TC-03** | Quota Limit Enforcement | Student Club attempts 4th active booking | Throws `QuotaExceededException` | Exception caught & verified | **PASS** |
| **TC-04** | Cancellation & Slot Release | Cancel active booking, re-book slot | Cancel succeeds; slot returns `isAvailable=true` | Slot re-booked successfully | **PASS** |
| **TC-05** | Multi-Threaded Race Condition | 2 concurrent threads on `AUD-RACE` via `CountDownLatch` | Exactly 1 succeeds, exactly 1 fails with conflict | 1 Confirmed, 1 Rejected | **PASS** |

### Test Runner Output:
```
*************************************************************
  STARTING AUTOMATED TEST RUNNER: CampusResourceHub         
*************************************************************
=======================================================
 RUNNING UNIT TESTS: BookingServiceTest
=======================================================
Test 1: Valid Booking Creation ... PASSED
Test 2: Double Booking Conflict Detection ... PASSED (Correctly caught SlotConflictException)
Test 3: Quota Limit Enforcement (Max 3 for Clubs) ... PASSED (Correctly caught QuotaExceededException)
Test 4: Cancellation and Slot Re-availability ... PASSED
>> BookingServiceTest: ALL 4 SUITES PASSED (100% OK)

=======================================================
 RUNNING CONCURRENCY TEST: Multi-Threaded Contention
=======================================================
Test: 2 Threads simultaneously booking exact same slot ... PASSED (Successes: 1, Conflicts: 1)
>> ConcurrencyBookingTest: PASSED (Thread-Safe Isolation Confirmed)

=======================================================
  ALL TEST SUITES EXECUTED SUCCESSFULLY! (5/5 PASSED)  
=======================================================
```

---

## 12. Challenges Faced & Solutions
1. **Race Conditions During High-Traffic Slot Release:**
   * *Problem:* When two student clubs attempted to book an auditorium simultaneously, both read the slot as free before either could commit, leading to double allocation.
   * *Solution:* Implemented fine-grained lock striping using `ConcurrentHashMap<String, Object>` where threads synchronize exclusively on the target resource ID.
2. **Dynamic Preemption Without Deadlocks:**
   * *Problem:* Faculty members needed the capability to override lower-priority bookings without locking other system operations.
   * *Solution:* Designed priority levels in `UserRole`. Within the synchronized lock, the system safely marks the conflicting reservation as `CANCELLED` and logs the preemption before confirming the new booking.
3. **Zero-Dependency Academic Portability:**
   * *Problem:* Using heavy build tools (Gradle/Maven) or external databases (MySQL) often causes setup failures during academic evaluations.
   * *Solution:* Built the entire persistence and test runner using standard Java SE (JDK 17+), enabling compilation and execution on any standard Java environment with `javac` and `java`.

---

## 13. Learnings & Key Takeaways
* **Object-Oriented Design Principles:** Practical experience applying inheritance hierarchies and polymorphism to model physical campus assets.
* **Thread Safety & Java Concurrency:** Deepened understanding of intrinsic locks, `synchronized` blocks, thread-safe collections (`CopyOnWriteArrayList`, `ConcurrentHashMap`), and synchronization barriers (`CountDownLatch`).
* **Declarative Processing with Java Streams:** Streamlined data extraction and reporting using `.filter()`, `.map()`, `.collect()`, and `Collectors.groupingBy()`.
* **Clean Code & Exception Handling:** Enforced separation of concerns by routing validation failures through custom checked exceptions rather than generic error codes.

---

## 14. Future Enhancements
* **Graphical User Interface (GUI):** Transitioning from the console CLI to a desktop JavaFX or web-based Spring Boot interface.
* **Database Integration:** Replacing Java file serialization with an enterprise relational database (PostgreSQL or SQLite via JDBC).
* **Automated Email/SMS Notifications:** Adding background daemon threads to dispatch email confirmations and preemption alerts to users.
* **Smart IoT Room Access Integration:** Generating dynamic QR codes or RFID access tokens upon booking approval.

---

## 15. References
1. Bloch, Joshua. *Effective Java (3rd Edition)*. Addison-Wesley Professional, 2018.
2. Schildt, Herbert. *Java: The Complete Reference (Twelfth Edition)*. McGraw-Hill Education, 2021.
3. Oracle Java Documentation: *Java Platform, Standard Edition & Concurrency Utilities API Specification*.
4. Gamma, E., Helm, R., Johnson, R., & Vlissides, J. *Design Patterns: Elements of Reusable Object-Oriented Software*. Addison-Wesley, 1994.
5. VITyarthi Course Syllabus: *Programming in Java (Flipped Course Evaluation Guidelines)*.