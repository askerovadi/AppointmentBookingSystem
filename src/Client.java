import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class Client {
    private final int id;
    private final String name;
    private final String phone;
    private final List<Appointment> appointments = new ArrayList<>();

    public Client(int id, String name, String phone) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Client name must not be empty");
        }
        this.id = id;
        this.name = name;
        this.phone = phone;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getPhone() { return phone; }
    public List<Appointment> getAppointments() { return appointments; }

    // equals и hashCode — по уникальному ключу id
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Client)) return false;
        return id == ((Client) o).id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return name;
    }
}
