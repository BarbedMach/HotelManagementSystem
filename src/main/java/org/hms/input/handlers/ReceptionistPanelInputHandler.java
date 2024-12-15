package org.hms.input.handlers;

import org.hms.View;
import org.hms.database.DataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;

public class ReceptionistPanelInputHandler extends InputHandlerBase {
    public ReceptionistPanelInputHandler(View view) {
        super(view);
    }

    private void addNewReservation() throws Exception {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter the reservation details");
        System.out.print("Enter hotel name: ");
        String hotelName = scanner.nextLine();

        System.out.print("Enter room number: ");
        int roomID = Integer.parseInt(scanner.nextLine());

        DataSource ds = new DataSource();
        Connection connection = ds.getConnection();

        String sql = """
                INSERT INTO reservations
                SELECT h_id, ?
                FROM hotel
                WHERE h_name = ?
                """;
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, roomID);
        preparedStatement.setString(2, hotelName);

        System.out.print("Enter y/Y to confirm, anything else to discard: ");

        String decision = scanner.nextLine();
        if (!decision.equalsIgnoreCase("Y")) {
            System.out.println("Operation discarded");
            connection.close();
            return;
        }

        int rowCount = preparedStatement.executeUpdate();
        if (rowCount == 0) {
            connection.close();
            throw new Exception("Failed to add reservation");
        }

        System.out.println("Reservation added");
        connection.close();
    }

    private void displayBookingsAndReservations() throws Exception {
        DataSource ds = new DataSource();
        Connection connection = ds.getConnection();

        String sql = """
                SELECT h_name, u_name, total_guests, r_id, status
                FROM booking
                LEFT JOIN reservations ON booking.b_id = reservations.b_id
                JOIN user ON user.u_id = booking.g_id
                ORDER BY h_name, u_name, total_guests, r_id
                """;

        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ResultSet resultSet = preparedStatement.executeQuery();

        System.out.printf("%-20s %-20s %-15s %-10s %-15s%n",
                "Hotel Name", "Guest Name", "Total Guests", "Room ID", "Booking Status");
        System.out.println("--------------------------------------------------------------------------");

        while (resultSet.next()) {
            String hotelName = resultSet.getString("HotelName");
            String guestName = resultSet.getString("GuestName");
            int totalGuests = resultSet.getInt("TotalGuests");
            int roomId = resultSet.getInt("RoomID");
            String bookingStatus = resultSet.getString("BookingStatus");

            System.out.printf("%-20s %-20s %-15d %-10s %-15s%n",
                    hotelName, guestName, totalGuests,
                    (roomId == 0 ? "N/A" : roomId), bookingStatus);
        }
        System.out.println();

        resultSet.close();
        preparedStatement.close();
        connection.close();
    }

    private void deleteReservation() throws Exception {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the reservation details");
        System.out.print("Enter hotel name: ");
        String hotelName = scanner.nextLine();

        System.out.print("Enter room number: ");
        int roomID = Integer.parseInt(scanner.nextLine());

        DataSource ds = new DataSource();
        Connection connection = ds.getConnection();

        String sql = """
                DELETE FROM reservations
                WHERE reservations.r_id = ? AND reservations.b_id = (SELECT b_id
                                                                     FROM booking
                                                                     WHERE h_name = ?)
                """;
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, roomID);
        preparedStatement.setString(2, hotelName);

        System.out.print("Enter y/Y to confirm, anything else to discard: ");

        String decision = scanner.nextLine();
        if (!decision.equalsIgnoreCase("Y")) {
            System.out.println("Operation discarded");
            connection.close();
            return;
        }

        int rowCount = preparedStatement.executeUpdate();
        if (rowCount == 0) {
            connection.close();
            throw new Exception("Failed to delete reservation");
        }

        System.out.println("Reservation deleted");
        connection.close();
    }

    private void createHousekeepingTask() throws Exception {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the task details");
        System.out.print("Enter start date YYYY-MM-DD: ");
        String startDate = scanner.nextLine();

        System.out.print("Enter end date YYYY-MM-DD: ");
        String endDate = scanner.nextLine();

        DataSource ds = new DataSource();
        Connection connection = ds.getConnection();

        String sql = """
                INSERT INTO housekeeping_schedule (t_start_date, t_end_date, status)
                VALUES (?, ?, 'waiting')
                """;
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, startDate);
        preparedStatement.setString(2, endDate);

        System.out.print("Enter y/Y to confirm, anything else to discard: ");

        String decision = scanner.nextLine();
        if (!decision.equalsIgnoreCase("Y")) {
            System.out.println("Operation discarded");
            connection.close();
            return;
        }

        int rowCount = preparedStatement.executeUpdate();
        if (rowCount == 0) {
            connection.close();
            throw new Exception("Failed to add new task");
        }

        System.out.println("New housekeeping task added");
        connection.close();
    }

    private void assignHousekeepingTask() throws Exception {

    }

    private void displayHousekeepersAndTheirStatus() throws Exception {
        DataSource ds = new DataSource();
        Connection connection = ds.getConnection();

        String sql = """
                SELECT user.u_name, COUNT(housekeeping_staff.t_id)
                FROM housekeeper
                JOIN user ON housekeeper.hk_id = user.u_id
                LEFT JOIN housekeeping_staff ON housekeeper.hk_id = housekeeping_staff.hk_id
                GROUP BY user.u_name
                ORDER BY COUNT(housekeeping_staff.t_id)
                """;
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ResultSet resultSet = preparedStatement.executeQuery();

        System.out.printf("%-20s %-10s%n", "Housekeeper", "TaskCount");
        System.out.println("----------------------------------");
        while (resultSet.next()) {
            String housekeeperName = resultSet.getString("Housekeeper");
            int taskCount = resultSet.getInt("TaskCount");
            System.out.printf("%-20s %-10d%n", housekeeperName, taskCount);
        }
        System.out.println();

        resultSet.close();
        preparedStatement.close();
        connection.close();
    }

    private void processPayment() throws Exception {

    }

    @Override
    public void handleInput() {
        boolean terminated = false;
        Scanner scanner = new Scanner(System.in);

        while (!terminated) {
            System.out.print("Enter number: ");

            while (!scanner.hasNextInt()) {
                System.out.print("Enter number: ");
                scanner.next();
            }

            int number = scanner.nextInt();

            switch (number) {
                case 1 -> {
                    try {
                        addNewReservation();
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                    view.display();
                }
                case 2 -> {
                    try {
                        displayBookingsAndReservations();
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                    view.display();
                }
                case 3 -> {
                    try {
                        deleteReservation();
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                    view.display();
                }
                case 4 -> {
                    try {
                        createHousekeepingTask();
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                    view.display();
                }
                case 5 -> {
                    try {
                        assignHousekeepingTask();
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                    view.display();
                }
                case 6 -> {
                    try {
                        displayHousekeepersAndTheirStatus();
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                    view.display();
                }
                case 7 -> {
                    try {
                        processPayment();
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                    view.display();
                }
                case 8 -> {
                    terminated = true;
                    view.display("LANDING");
                }
                case 9 -> System.exit(0);
            }
        }
    }
}
