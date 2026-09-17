# Campus Facility & Event Resource Booking System (CampusResourceHub)

[![Java Version](https://img.shields.io/badge/Java-21%20%2F%2025%20LTS-orange.svg)](https://www.oracle.com/java/)
[![Evaluation](https://img.shields.io/badge/VITyarthi-Flipped%20Course%20Project-blue.svg)](#)
[![Build & Tests](https://img.shields.io/badge/Tests-5%2F5%20Passed-brightgreen.svg)](#)

A production-grade, object-oriented Java system engineered to coordinate university venue reservations, equipment allocation, and multi-user scheduling. The application implements thread-safe slot allocation, role-based quota management, preemption conflict resolution, and Java Streams analytics.

---

## Table of Contents
1. [Overview](#overview)
2. [Key Features](#key-features)
3. [Technologies & Tools](#technologies--tools)
4. [System Architecture & Package Layout](#system-architecture--package-layout)
5. [Steps to Install & Run](#steps-to-install--run)
6. [Instructions for Testing](#instructions-for-testing)
7. [Sample CLI Execution & Output](#sample-cli-execution--output)
8. [License & Course Attribution](#license--course-attribution)

---

## 1. Overview
Campus facilities such as auditoriums, computer laboratories, and boardrooms often experience scheduling conflicts due to decentralized booking mechanisms. **CampusResourceHub** solves this by providing:
- Strict temporal clash detection.
- Multi-threaded concurrency control eliminating race-condition double bookings.
- Role-based quota enforcement (e.g., student clubs are restricted to 3 active bookings; faculty members are granted up to 10).
- Priority escalation where faculty academic activities preempt lower-priority casual bookings.
- Analytics and reporting using Java 8+ Streams API and CSV file export.

---

## 2. Features

### Core Functional Modules
1. **User & Identity Management:**
   * Role hierarchy: `ADMIN`, `FACULTY`, `STUDENT_CLUB`.
   * Dynamic quota limit checks per user type.
2. **Resource Catalog & Allocation Engine:**
   * Support for specialized facilities: `Auditorium` (AV & stage lighting), `ComputerLab` (OS & GPU configuration), `ConferenceRoom` (smart boards & video kits).
   * Thread-safe reservation engine with per-resource lock monitors.
   * Priority-based preemption resolution.
3. **Analytics, Audit Trail & Export:**
   * Live Streams API aggregation for booked hours by venue category.
   * Full event ledger logging user actions with timestamps.
   * One-click CSV export to `data/campus_booking_report.csv`.

### Non-Functional Highlights
* **Thread Safety & Concurrency:** Eliminates race conditions through dedicated resource monitors (`ConcurrentHashMap<String, Object>`).
* **Clean Domain Architecture:** Decoupled Model-Service-Util structure with abstract base classes and interfaces (`Reservable`, `Auditable`).
* **Robust Error Handling:** Custom checked exception hierarchy (`SlotConflictException`, `QuotaExceededException`, `ResourceNotFoundException`, `UnauthorizedAccessException`).

---

## 3. Technologies & Tools
* **Programming Language:** Java 21 / 25 LTS
* **Paradigm:** Object-Oriented Programming (Polymorphism, Abstraction, Encapsulation, Interfaces)
* **Collections & Concurrency:** `ConcurrentHashMap`, `CopyOnWriteArrayList`, `AtomicInteger`, `TreeSet`, `CountDownLatch`
* **Modern APIs:** Java Streams, Lambdas, `java.time.LocalDateTime`, `Optional`
* **I/O & Persistence:** Java File I/O, Object Serialization, CSV generation
* **Testing:** Built-in Automated Unit & Concurrency Test Runner (`TestRunner.java`)
* **Environment:** Platform-independent (Windows, Linux, macOS)

---

## 4. System Architecture & Package Layout

```
CampusResourceBookingSystem/
├── src/
│   └── com/vityarthi/booking/
│       ├── Main.java                          # Interactive CLI Entry Point
│       ├── model/
│       │   ├── UserRole.java                  # Role Enum (ADMIN, FACULTY, STUDENT_CLUB)
│       │   ├── User.java                      # Abstract User Base Class
│       │   ├── AdminUser.java                 # Administrator Model
│       │   ├── FacultyUser.java               # Faculty Member Model
│       │   ├── StudentClubUser.java           # Student Club Coordinator Model
│       │   ├── CampusResource.java            # Abstract Resource Base Class
│       │   ├── Auditorium.java                # Auditorium Facility Model
│       │   ├── ComputerLab.java               # Computing Laboratory Model
│       │   ├── ConferenceRoom.java            # Boardroom / Meeting Space Model
│       │   ├── TimeSlot.java                  # Temporal Window & Overlap Logic
│       │   ├── ReservationStatus.java         # Lifecycle Enum
│       │   └── Reservation.java               # Core Reservation Entity
│       ├── service/
│       │   ├── Reservable.java                # Interface for Booking Actions
│       │   ├── Auditable.java                 # Interface for Logging & Audit
│       │   ├── UserService.java               # User Lookup & Quota Registry
│       │   ├── ResourceService.java          # Facility Catalog Management
│       │   ├── BookingService.java            # Thread-Safe Reservation Engine
│       │   └── AnalyticsService.java          # Streams Aggregations & CSV Export
│       ├── exception/
│       │   ├── BookingException.java          # Base Domain Exception
│       │   ├── SlotConflictException.java     # Temporal Clash Exception
│       │   ├── QuotaExceededException.java    # Quota Violation Exception
│       │   ├── ResourceNotFoundException.java # Missing Resource Exception
│       │   └── UnauthorizedAccessException.java
│       └── util/
│           ├── StorageManager.java            # File I/O Serialization
│           └── DemoDataSeeder.java            # Initial Realistic Campus Dataset
├── test/
│   └── com/vityarthi/booking/
│       ├── BookingServiceTest.java            # Unit Validation Suites
│       ├── ConcurrencyBookingTest.java        # Multi-Threaded Stress Test
│       └── TestRunner.java                    # Standalone Test Execution Harness
├── docs/
│   ├── diagrams/                              # UML & Architecture Diagrams
│   └── project_report.md                      # Comprehensive 15-Section Report
├── statement.md                               # VITyarthi Problem Statement File
├── build_and_run.bat                          # One-Click Windows Runner Script
└── README.md                                  # Repository Documentation
```

---

## 5. Steps to Install & Run

### Prerequisites
* JDK 17, 21, or 25 installed and available on system `PATH`.
* Verify with:
  ```bash
  javac -version
  java -version
  ```

### Quick Run (Windows)
Simply double-click `build_and_run.bat` or run:
```cmd
.\build_and_run.bat
```

### Manual Compilation & Execution
1. Navigate to the project root:
   ```bash
   cd CampusResourceBookingSystem
   ```
2. Compile the source code:
   ```bash
   javac -encoding UTF-8 -d bin src/com/vityarthi/booking/model/*.java src/com/vityarthi/booking/exception/*.java src/com/vityarthi/booking/service/*.java src/com/vityarthi/booking/util/*.java src/com/vityarthi/booking/Main.java
   ```
3. Run the interactive CLI:
   ```bash
   java -cp bin com.vityarthi.booking.Main
   ```

---

## 6. Instructions for Testing

The project includes an automated test harness validating both functional business rules and concurrent race conditions.

### Compile and Run Test Suite:
```bash
javac -encoding UTF-8 -cp bin -d bin test/com/vityarthi/booking/*.java
java -cp bin com.vityarthi.booking.TestRunner
```

### Test Coverage Summary:
* **Test 1: Valid Booking Creation** $\rightarrow$ Validates entity instantiation and active status assignment.
* **Test 2: Double Booking Conflict Detection** $\rightarrow$ Asserts that overlapping time slots trigger `SlotConflictException`.
* **Test 3: Quota Limit Enforcement** $\rightarrow$ Asserts that attempting a 4th booking on a student club account throws `QuotaExceededException`.
* **Test 4: Cancellation and Slot Re-availability** $\rightarrow$ Asserts that cancelling a booking restores the slot availability for subsequent reservations.
* **Test 5: Multi-Threaded Concurrency Race Condition** $\rightarrow$ Fires 2 simultaneous threads attempting to book the identical auditorium slot at the same millisecond using `CountDownLatch`. Asserts exactly 1 thread succeeds and 1 receives a conflict exception.

---

## 7. Sample CLI Execution & Output

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

### Live Concurrency Output:
```
--- CONCURRENCY RACE CONDITION TEST ---
Simulating 2 simultaneous threads attempting to book the SAME auditorium at the EXACT same second...
>> [Thread-1 GDSC] SUCCESS: Acquired slot -> RES-1003
>> [Thread-2 Robotics] REJECTED: CONFLICT: Slot is already reserved by Booking #RES-1003.
>> Concurrency check complete: Double-booking successfully prevented by synchronization monitor!
```

---

## 8. Course & Academic Attribution
* **Course:** Programming in Java
* **Program:** Flipped Course Evaluation
* **Institution:** VITyarthi / Vellore Institute of Technology