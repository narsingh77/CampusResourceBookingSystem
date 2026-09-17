# Problem Statement & Project Scope: Campus Facility & Event Resource Booking System (CampusResourceHub)

## 1. Problem Statement
In modern collegiate environments, coordinating the allocation of physical spaces and equipment—such as grand auditoriums, computing laboratories, and departmental seminar halls—is plagued by fragmented, manual communication channels (paper requisitions, emails, or isolated spreadsheets). This results in:
* **Schedule Collisions & Double Bookings:** Multiple faculty members or student organizations reserving the same venue at the same time.
* **Lack of Priority Resolution:** High-priority academic events being obstructed by informal or low-priority student practice slots without clear escalation mechanisms.
* **Quota Violations & Resource Monopolization:** Clubs hoarding venues indefinitely without automated enforcement of allocation limits.
* **Absence of Real-Time Auditing:** Inability of campus administration to track resource utilization, energy costs, or operational status.

This project delivers **CampusResourceHub**, an enterprise-grade, object-oriented Java system providing automated slot validation, thread-safe concurrent booking conflict resolution, role-based quota management, and Streams-based utilization analytics.

---

## 2. Scope of the Project
The scope of **CampusResourceHub** encompasses:
* **Centralized Registry:** Standardized cataloging of campus physical spaces (Auditoriums, Specialized Computer Labs, Conference Rooms) with specific attributes (seating capacity, AV capabilities, GPU nodes, operational flags).
* **Role-Based Privilege & Quota Enforcement:** Tiered access control distinguishing between `Campus Administrator`, `Faculty Member`, and `Student Club Coordinator`.
* **Thread-Safe Reservation Engine:** Concurrency-protected scheduling preventing simultaneous race conditions using fine-grained synchronization locks per venue.
* **Dynamic Priority Escalation & Preemption:** Algorithmic resolution of slot contention where high-priority faculty academic events automatically preempt lower-priority casual bookings.
* **Data Aggregation & Reporting:** Real-time metrics on venue utilization hours, status distribution, and automated CSV audit trail exports using Java 8+ Streams and Lambda expressions.
* **File I/O Persistence:** Autonomous serialization and persistence of reservations across application restarts.

---

## 3. Target Users
The system serves three primary campus demographics:
1. **University Faculty:**
   * Require rapid booking of lecture halls, auditoriums, and labs for guest seminars, academic conferences, and research evaluations with higher priority and generous booking quotas.
2. **Student Club Heads & Event Organizers:**
   * Require scheduled reservations for hackathons, club meetings, cultural rehearsals, and technical workshops subject to faculty advisor endorsement and quota constraints (e.g., maximum 3 active bookings).
3. **Campus Administrators & Estate Managers:**
   * Require complete oversight of all campus infrastructure, ability to mark venues for maintenance, audit all cancellation/approval trails, and analyze utilization metrics to optimize capital expenditures.

---

## 4. High-Level Features
* **Modular Multi-Entity OOP Model:** Full inheritance hierarchy with abstract base entities (`User`, `CampusResource`) and concrete subclasses (`Auditorium`, `ComputerLab`, `ConferenceRoom`).
* **TimeSlot Overlap Validation Engine:** High-precision temporal collision detection using `java.time.LocalDateTime`.
* **Atomic Concurrency Protection:** Synchronized monitor locks per physical resource ensuring zero race condition double-bookings in multi-threaded environments.
* **Custom Domain Exception Framework:** Explicit error handling using specialized exceptions (`SlotConflictException`, `QuotaExceededException`, `ResourceNotFoundException`, `UnauthorizedAccessException`).
* **Streams API Analytics & CSV Exporter:** Live aggregation of facility usage hours and automated disk export of reservation records.
* **Audit Trail Logger:** Non-repudiation event ledger recording all booking creations, cancellations, and preemption interventions with timestamps.