# Appointment Booking System

Appointment booking system for a beauty salon (nail and hair services): clients submit requests,
professionals process them from a queue, appointments can be rescheduled, completed, cancelled and deleted.
All data is stored in the Java Collection Framework.

**Class hierarchy:** `Appointment` → `NailAppointment`, `HairAppointment`

## How to run
```bash
javac -d out src/*.java
java -cp out Main
```
Or in IntelliJ IDEA: open `src/Main.java` → Run.

## Files
| File | Purpose |
|---|---|
| `Appointment` | abstract appointment, `Comparable`, `equals/hashCode` by id |
| `NailAppointment`, `HairAppointment` | concrete appointment types |
| `Client`, `Professional` | users of the system |
| `ExtraService` | extra service inside an appointment (composition) |
| `AppointmentStatus` | PENDING, CONFIRMED, COMPLETED, CANCELLED |
| `AppointmentManager` | service layer: collections, CRUD, queue, search, statistics |
| `AppointmentComparators` | 3 comparators |
| `BookingException` | custom exception |
| `Main` | demo of all features |

## Java Collection Framework usage
| Block | Collection | Where | Why |
|---|---|---|---|
| A | `ArrayList<Appointment>` | `AppointmentManager.appointments` | main registry of appointments |
| A | `HashSet<Client>` | `Professional.clients` | N:M relationship without duplicates |
| B | `HashMap<Integer, Appointment>` | `appointmentById` | O(1) lookup of appointment by id |
| B | `HashMap<Integer, Client>`, `HashMap<Integer, Professional>` | `clients`, `professionals` | O(1) lookup of users by id |
| B | `HashMap<String, List<Appointment>>` | `appointmentsByType` | grouping by service type |
| C | `Comparable<Appointment>` | `Appointment.compareTo` | natural order: date, then id |
| C | 3 × `Comparator` | `AppointmentComparators` | by price, by client name, by type + price |
| D | `ArrayDeque<Appointment>` (Queue) | `requestQueue` | FIFO queue of booking requests |

## UML class diagram
```mermaid
classDiagram
    class Appointment {
        <<abstract>>
        -int id
        -Client client
        -Professional professional
        -LocalDateTime dateTime
        -double price
        -AppointmentStatus status
        -List~ExtraService~ extras
        +getType()* String
        +getDetails()* String
        +addExtra(String, double)
        +getTotalPrice() double
        +compareTo(Appointment) int
        +equals(Object) boolean
        +hashCode() int
    }
    class NailAppointment {
        -String coating
    }
    class HairAppointment {
        -String service
    }
    class Client {
        -int id
        -String name
        -String phone
        -List~Appointment~ appointments
    }
    class Professional {
        -int id
        -String name
        -String specialization
        -double rating
        -List~Appointment~ appointments
        -Set~Client~ clients
    }
    class ExtraService {
        -String name
        -double price
        +equals(Object) boolean
        +hashCode() int
    }
    class AppointmentStatus {
        <<enumeration>>
        PENDING
        CONFIRMED
        COMPLETED
        CANCELLED
    }
    class AppointmentManager {
        -List~Appointment~ appointments
        -Map~Integer, Appointment~ appointmentById
        -Map~Integer, Client~ clients
        -Map~Integer, Professional~ professionals
        -Map~String, List~Appointment~~ appointmentsByType
        -Queue~Appointment~ requestQueue
        +submitRequest(Appointment)
        +processNextRequest()
        +addAppointment(Appointment)
        +getById(int) Appointment
        +getClient(int) Client
        +getProfessional(int) Professional
        +reschedule(int, LocalDateTime)
        +changeStatus(int, AppointmentStatus)
        +removeAppointment(int)
        +getSorted(Comparator) List
        +printStatistics()
    }
    class AppointmentComparators {
        +BY_PRICE$
        +BY_CLIENT_NAME$
        +BY_TYPE_THEN_PRICE_DESC$
    }
    class BookingException

    Appointment <|-- NailAppointment
    Appointment <|-- HairAppointment
    Appointment ..|> Comparable
    Appointment *-- ExtraService : composition
    Appointment --> AppointmentStatus
    Client "1" o-- "*" Appointment : 1..N
    Professional "1" o-- "*" Appointment : 1..N
    Professional "*" -- "*" Client : N..M
    AppointmentManager o-- Appointment : aggregation
    AppointmentManager o-- Client
    AppointmentManager o-- Professional
    AppointmentManager ..> AppointmentComparators
    AppointmentManager ..> BookingException
```

## User roles
- **Client** submits a request → `submitRequest()` → `Queue.offer()`
- **Professional** processes requests one by one → `processNextRequest()` → `Queue.poll()`
