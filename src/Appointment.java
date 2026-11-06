import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public abstract class Appointment implements Comparable<Appointment> {
    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("dd.MM HH:mm");

    private final int id;
    private final Client client;
    private final Professional professional;
    private LocalDateTime dateTime;
    private final double price;
    private AppointmentStatus status = AppointmentStatus.PENDING;
    private final List<ExtraService> extras = new ArrayList<>(); // композиция

    public Appointment(int id, Client client, Professional professional,
                       LocalDateTime dateTime, double price) {
        if (client == null || professional == null || dateTime == null) {
            throw new IllegalArgumentException("Client, professional and date are required");
        }
        if (price <= 0) {
            throw new IllegalArgumentException("Price must be greater than 0");
        }
        this.id = id;
        this.client = client;
        this.professional = professional;
        this.dateTime = dateTime;
        this.price = price;
    }

    /** Тип услуги: "NAIL" или "HAIR" — задаёт подкласс. */
    public abstract String getType();

    /** Детали услуги — свои у каждого подкласса. */
    public abstract String getDetails();

    /** Композиция: ExtraService создаётся внутри записи. */
    public void addExtra(String name, double price) {
        extras.add(new ExtraService(name, price));
    }

    /** Итоговая цена = базовая + доп. услуги. */
    public double getTotalPrice() {
        double total = price;
        for (ExtraService extra : extras) {
            total += extra.getPrice();
        }
        return total;
    }

    public int getId() { return id; }
    public Client getClient() { return client; }
    public Professional getProfessional() { return professional; }
    public LocalDateTime getDateTime() { return dateTime; }
    public void setDateTime(LocalDateTime dateTime) { this.dateTime = dateTime; }
    public AppointmentStatus getStatus() { return status; }
    public void setStatus(AppointmentStatus status) { this.status = status; }

    @Override
    public int compareTo(Appointment other) {
        int result = dateTime.compareTo(other.dateTime);
        if (result != 0) {
            return result;
        }
        return Integer.compare(id, other.id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Appointment)) return false;
        return id == ((Appointment) o).id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("#%d %s %s | %-6s -> %-7s | %s | %.0f som | %s",
                id, getType(), dateTime.format(FORMAT), client.getName(),
                professional.getName(), getDetails(), getTotalPrice(), status);
    }
}
