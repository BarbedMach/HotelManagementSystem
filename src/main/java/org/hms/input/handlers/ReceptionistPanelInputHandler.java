package org.hms.input.handlers;

import org.hms.View;
import org.hms.database.DataSource;

import java.sql.*;
import java.util.Scanner;

public class ReceptionistPanelInputHandler extends InputHandlerBase {
    public ReceptionistPanelInputHandler(View view) {
        super(view);
    }

    private void addNewReservation() throws Exception {
        Scanner scanner = new Scanner(System.in);

        DataSource ds = new DataSource();
        Connection connection = ds.getConnection();

        String sqlInfo = """
                SELECT booking.b_id AS BookingID,
                       h_name AS HotelName,
                       u_name AS GuestName,
                       total_guests AS TotalGuests,
                       r_id AS RoomID,
                       status AS BookingStatus
                FROM booking
                LEFT JOIN reservations ON booking.b_id = reservations.b_id
                JOIN user ON user.u_id = booking.g_id
                ORDER BY h_name, u_name, total_guests, r_id
                """;

        PreparedStatement preparedStatement = connection.prepareStatement(sqlInfo);
        ResultSet resultSet = preparedStatement.executeQuery();

        StringBuilder output = new StringBuilder();
        output.append(String.format("%-10s %-20s %-20s %-15s %-10s %-15s%n",
                "Booking ID", "Hotel Name", "Guest Name", "Total Guests", "Room ID", "Booking Status"));
        output.append("-----------------------------------------------------------------------------------------\n");

        while (resultSet.next()) {
            int bookingId = resultSet.getInt("BookingID");
            String hotelName = resultSet.getString("HotelName");
            String guestName = resultSet.getString("GuestName");
            int totalGuests = resultSet.getInt("TotalGuests");
            int roomId = resultSet.getInt("RoomID");
            String bookingStatus = resultSet.getString("BookingStatus");

            output.append(String.format("%-10d %-20s %-20s %-15d %-10s %-15s%n",
                    bookingId, hotelName, guestName, totalGuests,
                    (roomId == 0 ? "N/A" : String.valueOf(roomId)), bookingStatus));
        }

        System.out.println(output);

        System.out.println("Enter the reservation details");
        System.out.print("Enter booking ID: ");
        int bookingID = Integer.parseInt(scanner.nextLine());

        System.out.print("Enter hotel name: ");
        String hotelName = scanner.nextLine();

        System.out.print("Enter room number: ");
        int roomID = Integer.parseInt(scanner.nextLine());

        String sql = """
                INSERT INTO reservations (b_id, h_id, r_id)
                SELECT ?, h_id, ?
                FROM hotel
                WHERE h_name = ?
                """;
        preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, bookingID);
        preparedStatement.setInt(2, roomID);
        preparedStatement.setString(3, hotelName);

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

        sql = """
            UPDATE booking
            SET booking.status = 'p_pending'
            WHERE b_id = ?
        """;
        PreparedStatement preparedStatement2 = connection.prepareStatement(sql);
        preparedStatement2.setInt(1, bookingID);

        rowCount = preparedStatement2.executeUpdate();
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
                SELECT h_name AS HotelName,
                       u_name AS GuestName,
                       total_guests AS TotalGuests,
                       r_id AS RoomID,
                       status AS BookingStatus
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
        DataSource ds = new DataSource();
        Connection connection = ds.getConnection();

        String sqlInfo = """
                SELECT booking.b_id AS BookingID,
                       h_name AS HotelName,
                       u_name AS GuestName,
                       total_guests AS TotalGuests,
                       r_id AS RoomID,
                       status AS BookingStatus
                FROM booking
                LEFT JOIN reservations ON booking.b_id = reservations.b_id
                JOIN user ON user.u_id = booking.g_id
                ORDER BY h_name, u_name, total_guests, r_id
                """;

        PreparedStatement preparedStatement = connection.prepareStatement(sqlInfo);
        ResultSet resultSet = preparedStatement.executeQuery();

        StringBuilder output = new StringBuilder();
        output.append(String.format("%-10s %-20s %-20s %-15s %-10s %-15s%n",
                "Booking ID", "Hotel Name", "Guest Name", "Total Guests", "Room ID", "Booking Status"));
        output.append("-----------------------------------------------------------------------------------------\n");

        while (resultSet.next()) {
            int bookingId = resultSet.getInt("BookingID");
            String hotelName = resultSet.getString("HotelName");
            String guestName = resultSet.getString("GuestName");
            int totalGuests = resultSet.getInt("TotalGuests");
            int roomId = resultSet.getInt("RoomID");
            String bookingStatus = resultSet.getString("BookingStatus");

            output.append(String.format("%-10d %-20s %-20s %-15d %-10s %-15s%n",
                    bookingId, hotelName, guestName, totalGuests,
                    (roomId == 0 ? "N/A" : String.valueOf(roomId)), bookingStatus));
        }

        System.out.println(output);


        System.out.println("Enter the reservation details");
        System.out.print("Enter hotel name: ");
        String hotelName = scanner.nextLine();

        System.out.print("Enter room number: ");
        int roomID = Integer.parseInt(scanner.nextLine());

        System.out.print("Enter booking ID: ");
        int bookingID = Integer.parseInt(scanner.nextLine());

        String sql = """
                DELETE FROM reservations
                WHERE reservations.r_id = ? AND reservations.b_id = ? AND reservations.h_id = (SELECT h_id
                                                                     FROM booking
                                                                     JOIN hotel ON hotel.h_name = booking.h_name
                                                                     WHERE hotel.h_name = ?
                                                                     LIMIT 1)
                """;
        preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, roomID);
        preparedStatement.setInt(2, bookingID);
        preparedStatement.setString(3, hotelName);

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
        PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
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

        ResultSet generatedKeys = preparedStatement.getGeneratedKeys();

        if (rowCount == 0) {
            connection.close();
            throw new Exception("Failed to add new task");
        }

        System.out.println("New housekeeping task added");

        if (generatedKeys.next()) {
            int id = generatedKeys.getInt(1);
            System.out.println("Generated task ID: " + id);
        }

        connection.close();
    }

    private void assignHousekeepingTask() throws Exception {
        Scanner scanner = new Scanner(System.in);
        DataSource ds = new DataSource();
        Connection connection = ds.getConnection();

        String sql = """
                SELECT t_id, t_start_date, t_end_date, status
                FROM housekeeping_schedule
                """;
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ResultSet resultSet = preparedStatement.executeQuery();

        System.out.printf("%-10s %-15s %-15s %-10s%n", "Task ID", "Start Date", "End Date", "Status");
        System.out.println("-----------------------------------------------");

        while (resultSet.next()) {
            int taskId = resultSet.getInt("t_id");
            String startDate = resultSet.getString("t_start_date");
            String endDate = resultSet.getString("t_end_date");
            String status = resultSet.getString("status");

            System.out.printf("%-10d %-15s %-15s %-10s%n", taskId, startDate, endDate, status);
        }
        System.out.println();

        System.out.println("Enter assignment details");
        System.out.print("Enter task ID: ");
        int taskId = Integer.parseInt(scanner.nextLine());

        System.out.print("Enter hotel name: ");
        String hotelName = scanner.nextLine();

        System.out.print("Enter room number: ");
        int roomId = Integer.parseInt(scanner.nextLine());

        System.out.print("Enter housekeeper name: ");
        String housekeeperName = scanner.nextLine();

        System.out.println("Assignment details: Task: " + taskId + ", Hotel: " + hotelName + ", Room: " + roomId + ", Staff: " + housekeeperName);
        System.out.print("Enter y/Y to confirm, anything else to discard: ");

        String decision = scanner.nextLine();
        if (!decision.equalsIgnoreCase("Y")) {
            System.out.println("Operation discarded");
            connection.close();
            return;
        }

        connection.setAutoCommit(false);

        String hksSql = """
                INSERT INTO housekeeping_staff (hk_id, t_id)
                SELECT hk_id, ?
                FROM housekeeper
                JOIN user ON housekeeper.hk_id = user.u_id
                WHERE user.u_name = ?
                """;
        PreparedStatement hksPreparedStatement = connection.prepareStatement(hksSql);
        hksPreparedStatement.setInt(1, taskId);
        hksPreparedStatement.setString(2, housekeeperName);

        int rowCount = hksPreparedStatement.executeUpdate();
        if (rowCount == 0) {
            connection.rollback();
            throw new Exception("Failed to assign task to housekeeper");
        }

        String hkrSql = """
                INSERT INTO housekeeping_rooms (t_id, r_id, h_id, status)
                SELECT ?, ?, h.h_id, 'dirty'
                FROM hotel h
                WHERE h.h_name = ? AND EXISTS(
                    SELECT 1
                    FROM room
                    WHERE room.h_id = h.h_id AND room.r_id = ?
                )
                """;
        PreparedStatement hkrPreparedStatement = connection.prepareStatement(hkrSql);
        hkrPreparedStatement.setInt(1, taskId);
        hkrPreparedStatement.setInt(2, roomId);
        hkrPreparedStatement.setString(3, hotelName);
        hkrPreparedStatement.setInt(4, roomId);

        rowCount = hkrPreparedStatement.executeUpdate();
        if (rowCount == 0) {
            connection.rollback();
            throw new Exception("Failed to assign room to task");
        }

        connection.commit();
        connection.close();

    }

    private void displayHousekeepersAndTheirStatus() throws Exception {
        DataSource ds = new DataSource();
        Connection connection = ds.getConnection();

        String sql = """
                SELECT user.u_name AS Housekeeper,
                    COUNT(housekeeping_staff.t_id) AS TaskCount
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
        Scanner scanner = new Scanner(System.in);
        DataSource ds = new DataSource();
        Connection connection = ds.getConnection();

        System.out.print("Enter guest name: ");
        String guestName = scanner.nextLine();

        System.out.println("Enter payment details");
        String retrieveRelevantBookings = """
                SELECT b_id, total_guests, check_in_date, check_out_date, status
                FROM booking
                JOIN user ON booking.g_id = user.u_id
                WHERE user.u_name = ? AND booking.status = 'p_pending'
                """;
        PreparedStatement statement = connection.prepareStatement(retrieveRelevantBookings);
        statement.setString(1, guestName);

        ResultSet resultSet = statement.executeQuery();

        System.out.printf("%-10s %-15s %-15s %-15s %-15s%n", "Booking ID", "Total Guests", "Check-in Date", "Check-out Date", "Status");
        System.out.println("------------------------------------------------------------");

        while (resultSet.next()) {
            int bId = resultSet.getInt("b_id");
            int totalGuests = resultSet.getInt("total_guests");
            String checkInDate = resultSet.getString("check_in_date");
            String checkOutDate = resultSet.getString("check_out_date");
            String status = resultSet.getString("status");

            System.out.printf("%-10d %-15d %-15s %-15s %-15s%n", bId, totalGuests, checkInDate, checkOutDate, status);
        }
        System.out.println();

        System.out.print("Enter booking ID: ");
        int bookingId = Integer.parseInt(scanner.nextLine());

        System.out.print("Enter payment amount: ");
        double paymentAmount = Double.parseDouble(scanner.nextLine());

        connection.setAutoCommit(false);

        String insertPaymentSql = """
                INSERT INTO payments (b_id, status, amount, payment_date)
                VALUES (?, 'paid', ?, CURRENT_DATE)
                """;
        PreparedStatement insertPaymentStatement = connection.prepareStatement(insertPaymentSql);
        insertPaymentStatement.setInt(1, bookingId);
        insertPaymentStatement.setDouble(2, paymentAmount);

        int rowCount = insertPaymentStatement.executeUpdate();
        if (rowCount == 0) {
            connection.rollback();
            throw new SQLException("Failed to insert payment.");
        }

        String updateBookingStatusSql = """
                UPDATE booking
                SET status = 'booked'
                WHERE b_id = ?
                """;
        PreparedStatement updateBookingStatusStatement = connection.prepareStatement(updateBookingStatusSql);
        updateBookingStatusStatement.setInt(1, bookingId);

        rowCount = updateBookingStatusStatement.executeUpdate();
        if (rowCount == 0) {
            connection.rollback();
            throw new SQLException("Failed to update payment.");
        }

        connection.commit();
        System.out.println("Payment successfully processed and booking status updated.");
        connection.close();
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
