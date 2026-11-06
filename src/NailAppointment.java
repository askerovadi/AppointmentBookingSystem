import java.time.LocalDateTime;


public class NailAppointment extends Appointment {
    private final String coating; // classic, gel, acrylic

    public NailAppointment(int id, Client client, Professional professional,
                           LocalDateTime dateTime, double price, String coating) {
        super(id, client, professional, dateTime, price);
        this.coating = coating;
    }

    @Override
    public String getType() {
        return "NAIL";
    }

    @Override
    public String getDetails() {
        return "coating: " + coating;
    }
}
