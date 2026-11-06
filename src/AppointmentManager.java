import java.time.LocalDateTime;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;


public class AppointmentManager {


    private final List<Appointment> appointments = new ArrayList<>();


    private final Map<Integer, Appointment> appointmentById = new HashMap<>();
    private final Map<Integer, Client> clients = new HashMap<>();
    private final Map<Integer, Professional> professionals = new HashMap<>();

    private final Map<String, List<Appointment>> appointmentsByType = new HashMap<>(); // группировка


    private final Queue<Appointment> requestQueue = new ArrayDeque<>();



    public void addClient(Client client) {
        if (client == null) {
            throw new BookingException("Client must not be null");
        }
        if (clients.containsKey(client.getId())) {
            throw new BookingException("Client with id " + client.getId() + " is already registered");
        }
        clients.put(client.getId(), client);
    }

    public void addProfessional(Professional professional) {
        if (professional == null) {
            throw new BookingException("Professional must not be null");
        }
        if (professionals.containsKey(professional.getId())) {
            throw new BookingException("Professional with id " + professional.getId() + " is already registered");
        }
        professionals.put(professional.getId(), professional);
    }


    public Client getClient(int id) {
        Client client = clients.get(id);
        if (client == null) {
            throw new BookingException("Client with id " + id + " not found");
        }
        return client;
    }


    public Professional getProfessional(int id) {
        Professional professional = professionals.get(id);
        if (professional == null) {
            throw new BookingException("Professional with id " + id + " not found");
        }
        return professional;
    }



    public void submitRequest(Appointment request) {
        if (request == null) {
            throw new BookingException("Request must not be null");
        }
        if (!clients.containsKey(request.getClient().getId())) {
            throw new BookingException("Client " + request.getClient().getName() + " is not registered");
        }
        requestQueue.offer(request);
        System.out.println("  + request from " + request.getClient() + " added to queue");
    }


    public void processNextRequest() {
        Appointment request = requestQueue.poll(); // берём первую заявку из очереди
        if (request == null) {
            System.out.println("  Queue is empty");
            return;
        }
        try {
            addAppointment(request);
            request.setStatus(AppointmentStatus.CONFIRMED);
            System.out.println("  ✔ confirmed: " + request);
        } catch (BookingException e) {
            request.setStatus(AppointmentStatus.CANCELLED);
            System.out.println("  ✘ rejected: " + e.getMessage());
        }
    }

    public int getQueueSize() {
        return requestQueue.size();
    }




    public void addAppointment(Appointment a) {
        if (a == null) {
            throw new BookingException("Appointment must not be null");
        }
        if (!professionals.containsKey(a.getProfessional().getId())) {
            throw new BookingException("Professional " + a.getProfessional().getName() + " is not registered");
        }
        if (appointmentById.containsKey(a.getId())) {
            throw new BookingException("Appointment #" + a.getId() + " already exists");
        }
        if (!a.getType().equals(a.getProfessional().getSpecialization())) {
            throw new BookingException(a.getProfessional().getName() + " does not do " + a.getType());
        }
        if (isBusy(a.getProfessional(), a)) {
            throw new BookingException(a.getProfessional().getName() + " is busy at this time");
        }
        appointments.add(a);
        appointmentById.put(a.getId(), a);
        appointmentsByType.computeIfAbsent(a.getType(), k -> new ArrayList<>()).add(a);
        a.getClient().getAppointments().add(a);         // 1:N
        a.getProfessional().getAppointments().add(a);   // 1:N
        a.getProfessional().getClients().add(a.getClient()); // N:M
    }

    public Appointment getById(int id) {
        Appointment a = appointmentById.get(id);
        if (a == null) {
            throw new BookingException("Appointment #" + id + " not found");
        }
        return a;
    }

    public void reschedule(int id, LocalDateTime newTime) {
        if (newTime == null) {
            throw new BookingException("New time must not be null");
        }
        Appointment a = getById(id);
        LocalDateTime oldTime = a.getDateTime();
        a.setDateTime(newTime);
        if (isBusy(a.getProfessional(), a)) {
            a.setDateTime(oldTime); // откат
            throw new BookingException(a.getProfessional().getName() + " is busy at this time");
        }
    }

    public void changeStatus(int id, AppointmentStatus status) {
        if (status == null) {
            throw new BookingException("Status must not be null");
        }
        Appointment a = getById(id);
        if (a.getStatus() == AppointmentStatus.COMPLETED || a.getStatus() == AppointmentStatus.CANCELLED) {
            throw new BookingException("Appointment #" + id + " is already " + a.getStatus());
        }
        a.setStatus(status);
    }


    public void removeAppointment(int id) {
        Appointment a = getById(id);
        appointments.remove(a);
        appointmentById.remove(id);
        appointmentsByType.get(a.getType()).remove(a);
        a.getClient().getAppointments().remove(a);
        a.getProfessional().getAppointments().remove(a);
        // N:M: убираем клиента у мастера, если у них больше нет общих записей
        boolean stillVisits = false;
        for (Appointment other : a.getProfessional().getAppointments()) {
            if (other.getClient().equals(a.getClient())) {
                stillVisits = true;
            }
        }
        if (!stillVisits) {
            a.getProfessional().getClients().remove(a.getClient());
        }
    }


    private boolean isBusy(Professional p, Appointment a) {
        for (Appointment other : p.getAppointments()) {
            if (!other.equals(a)
                    && other.getStatus() != AppointmentStatus.CANCELLED
                    && other.getDateTime().equals(a.getDateTime())) {
                return true;
            }
        }
        return false;
    }


    public List<Appointment> getByType(String type) {
        return new ArrayList<>(appointmentsByType.getOrDefault(type, new ArrayList<>())); // копия — оригинал не изменить снаружи
    }

    public List<Appointment> getByStatus(AppointmentStatus status) {
        List<Appointment> result = new ArrayList<>();
        for (Appointment a : appointments) {
            if (a.getStatus() == status) {
                result.add(a);
            }
        }
        return result;
    }

    public List<Appointment> getByPriceRange(double min, double max) {
        if (min > max) {
            throw new BookingException("Min price must not be greater than max price");
        }
        List<Appointment> result = new ArrayList<>();
        for (Appointment a : appointments) {
            if (a.getTotalPrice() >= min && a.getTotalPrice() <= max) {
                result.add(a);
            }
        }
        return result;
    }

    public List<Appointment> getSorted() {
        List<Appointment> copy = new ArrayList<>(appointments);
        Collections.sort(copy); // использует compareTo
        return copy;
    }

    public List<Appointment> getSorted(Comparator<Appointment> comparator) {
        List<Appointment> copy = new ArrayList<>(appointments);
        copy.sort(comparator);
        return copy;
    }



    public void printStatistics() {
        double revenue = 0;
        for (Appointment a : getByStatus(AppointmentStatus.COMPLETED)) {
            revenue += a.getTotalPrice();
        }
        System.out.println("  Total appointments: " + appointments.size());
        for (String type : appointmentsByType.keySet()) {
            System.out.println("  " + type + ": " + appointmentsByType.get(type).size());
        }
        for (AppointmentStatus status : AppointmentStatus.values()) {
            System.out.println("  " + status + ": " + getByStatus(status).size());
        }
        System.out.printf("  Revenue (COMPLETED): %.0f som%n", revenue);
    }
}
