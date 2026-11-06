import java.util.Objects;


public class ExtraService {
    private final String name;
    private final double price;

    public ExtraService(String name, double price) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Extra service name must not be empty");
        }
        if (price < 0) {
            throw new IllegalArgumentException("Price must not be negative");
        }
        this.name = name;
        this.price = price;
    }

    public String getName() { return name; }
    public double getPrice() { return price; }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ExtraService)) return false;
        return name.equals(((ExtraService) o).name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return name;
    }
}
