import java.util.Comparator;


public class AppointmentComparators {


    public static final Comparator<Appointment> BY_PRICE =
            Comparator.comparingDouble(Appointment::getTotalPrice);

    public static final Comparator<Appointment> BY_CLIENT_NAME =
            Comparator.comparing(a -> a.getClient().getName());


    public static final Comparator<Appointment> BY_TYPE_THEN_PRICE_DESC =
            Comparator.comparing(Appointment::getType)
                    .thenComparing(BY_PRICE.reversed());
}
