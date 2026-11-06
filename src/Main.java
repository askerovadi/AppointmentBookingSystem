import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Main {

    public static void main(String[] args) {
        AppointmentManager manager = new AppointmentManager();
        LocalDate day = LocalDate.of(2026, 12, 1);

        // 1. Регистрация мастеров и клиентов
        title("1. Registration");
        Professional aigerim = new Professional(1, "Aigerim", "NAIL", 4.9);
        Professional arman = new Professional(2, "Arman", "HAIR", 4.7);
        manager.addProfessional(aigerim);
        manager.addProfessional(arman);

        Client alina = new Client(1, "Alina", "+996700000001");
        Client timur = new Client(2, "Timur", "+996700000002");
        Client sara = new Client(3, "Sara", "+996700000003");
        manager.addClient(alina);
        manager.addClient(timur);
        manager.addClient(sara);
        System.out.println("  Professionals: " + aigerim + ", " + arman);
        System.out.println("  Clients: " + alina + ", " + timur + ", " + sara);

        // 2. Клиенты подают заявки в очередь
        title("2. CLIENT role: submit requests (Queue.offer)");
        NailAppointment a1 = new NailAppointment(101, alina, aigerim, at(day, 10), 8000, "gel");
        a1.addExtra("nail design", 1500); // композиция
        manager.submitRequest(a1);
        manager.submitRequest(new HairAppointment(102, timur, arman, at(day, 11), 5000, "haircut"));
        manager.submitRequest(new NailAppointment(103, sara, aigerim, at(day, 12), 6000, "classic"));
        manager.submitRequest(new HairAppointment(104, alina, arman, at(day, 14), 15000, "coloring"));
        manager.submitRequest(new NailAppointment(105, timur, aigerim, at(day, 10), 7000, "acrylic")); // время занято
        manager.submitRequest(new NailAppointment(106, sara, arman, at(day, 16), 6000, "gel"));      // не та специализация
        System.out.println("  Queue size: " + manager.getQueueSize());

        // 3. Мастер обрабатывает заявки по очереди
        title("3. PROFESSIONAL role: process requests (Queue.poll)");
        while (manager.getQueueSize() > 0) {
            manager.processNextRequest();
        }

        // 4. Поиск и фильтры
        title("4. READ: search and filters");
        System.out.println("  getById(101): " + manager.getById(101));
        System.out.println("  getClient(2): " + manager.getClient(2));
        System.out.println("  getProfessional(1): " + manager.getProfessional(1));
        System.out.println("  NAIL appointments:");
        print(manager.getByType("NAIL"));
        System.out.println("  Price from 6000 to 10000:");
        print(manager.getByPriceRange(6000, 10000));
        System.out.println("  Alina's appointments (1:N):");
        print(alina.getAppointments());
        System.out.println("  Aigerim's clients (N:M): " + aigerim.getClients());

        // 5. Обновление
        title("5. UPDATE: reschedule and change status");
        manager.reschedule(103, at(day, 13));
        manager.changeStatus(102, AppointmentStatus.COMPLETED);
        manager.changeStatus(101, AppointmentStatus.COMPLETED);
        print(manager.getSorted());

        // 6. Удаление
        title("6. DELETE: remove from all collections");
        System.out.println("  Before: " + manager.getSorted().size() + " appointments, Alina has "
                + alina.getAppointments().size());
        manager.removeAppointment(104);
        System.out.println("  After:  " + manager.getSorted().size() + " appointments, Alina has "
                + alina.getAppointments().size());

        // 7. Сортировка
        title("7. Sorting");
        System.out.println("  Natural order (by date, compareTo):");
        print(manager.getSorted());
        System.out.println("  By price:");
        print(manager.getSorted(AppointmentComparators.BY_PRICE));
        System.out.println("  By client name:");
        print(manager.getSorted(AppointmentComparators.BY_CLIENT_NAME));
        System.out.println("  By type, then price (desc):");
        print(manager.getSorted(AppointmentComparators.BY_TYPE_THEN_PRICE_DESC));

        // 8. Проверка equals и hashCode
        title("8. equals / hashCode");
        Client copy = new Client(1, "Alina copy", "000");
        Set<Client> set = new HashSet<>();
        set.add(alina);
        set.add(copy);
        System.out.println("  alina.equals(copy): " + alina.equals(copy));
        System.out.println("  same hashCode: " + (alina.hashCode() == copy.hashCode()));
        System.out.println("  HashSet size: " + set.size());

        // 9. Обработка ошибок
        title("9. Exception handling");
        try {
            manager.getById(999);
        } catch (BookingException e) {
            System.out.println("  ✘ " + e.getMessage());
        }
        try {
            manager.changeStatus(102, AppointmentStatus.CANCELLED);
        } catch (BookingException e) {
            System.out.println("  ✘ " + e.getMessage());
        }
        try {
            manager.addClient(copy);
        } catch (BookingException e) {
            System.out.println("  ✘ " + e.getMessage());
        }
        try {
            new HairAppointment(200, sara, arman, at(day, 9), -100, "styling");
        } catch (IllegalArgumentException e) {
            System.out.println("  ✘ " + e.getMessage());
        }
        try {
            manager.submitRequest(null);
        } catch (BookingException e) {
            System.out.println("  ✘ " + e.getMessage());
        }
        try {
            manager.getClient(50);
        } catch (BookingException e) {
            System.out.println("  ✘ " + e.getMessage());
        }
        manager.processNextRequest(); // пустая очередь

        // 10. Статистика
        title("10. Statistics");
        manager.printStatistics();
    }

    private static LocalDateTime at(LocalDate day, int hour) {
        return day.atTime(hour, 0);
    }

    private static void title(String text) {
        System.out.println();
        System.out.println("=== " + text + " ===");
    }

    private static void print(List<Appointment> list) {
        for (Appointment a : list) {
            System.out.println("    " + a);
        }
    }
}
