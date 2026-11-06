import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class Professional {
    private final int id;
    private final String name;
    private final String specialization; // "NAIL" или "HAIR"
    private final double rating;
    private final List<Appointment> appointments = new ArrayList<>();
    private final Set<Client> clients = new HashSet<>(); // Set — без дубликатов

    public Professional(int id, String name, String specialization, double rating) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Professional name must not be empty");
        }
        if (rating < 0 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 0 and 5");
        }
        this.id = id;
        this.name = name;
        this.specialization = specialization;
        this.rating = rating;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getSpecialization() { return specialization; }
    public double getRating() { return rating; }
    public List<Appointment> getAppointments() { return appointments; }
    public Set<Client> getClients() { return clients; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Professional)) return false;
        return id == ((Professional) o).id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return name + " (" + specialization + ", rating " + rating + ")";
    }
}
