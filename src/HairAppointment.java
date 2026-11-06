import java.time.LocalDateTime;


public class HairAppointment extends Appointment {
    private final String service; // haircut, coloring, styling

    public HairAppointment(int id, Client client, Professional professional,
                           LocalDateTime dateTime, double price, String service) {
        super(id, client, professional, dateTime, price);
        this.service = service;
    }

    @Override
    public String getType() {
        return "HAIR";
    }

    @Override
    public String getDetails() {
        return "service: " + service;
    }
}
