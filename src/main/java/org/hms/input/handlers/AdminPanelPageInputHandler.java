package org.hms.input.handlers;

import org.hms.View;
import org.hms.database.DataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.util.Scanner;

public class AdminPanelPageInputHandler extends InputHandlerBase {
    public AdminPanelPageInputHandler(View view) {
        super(view);
    }

    private void addHotel() throws SQLException {
        Scanner scanner = new Scanner(System.in);
        String hotelName, hotelZip, hotelStreet, hotelBuildingNo, hotelNo;

        System.out.println("Enter hotel details");

        System.out.print("Enter hotel name: ");
        hotelName = scanner.nextLine();

        System.out.print("Enter hotel zip: ");
        hotelZip = scanner.nextLine();

        System.out.print("Enter hotel street: ");
        hotelStreet = scanner.nextLine();

        System.out.print("Enter hotel building number: ");
        hotelBuildingNo = scanner.nextLine();

        System.out.print("Enter hotel phone number: ");
        hotelNo = scanner.nextLine();

        System.out.println("Hotel to be added");
        System.out.println("Hotel: " + hotelName + " at " + hotelStreet + " Street No:" + hotelBuildingNo + " (ZIP:" + hotelZip + ",PNO:" + hotelNo + ")");
        System.out.print("Enter y/Y to add, anything else to discard: ");

        String decision = scanner.nextLine();
        if (!decision.equalsIgnoreCase("Y")) {
            System.out.println("Operation discarded");
            return;
        }

        DataSource ds = new DataSource();
        Connection connection = ds.getConnection();

        String sql = """
                INSERT INTO hotel (h_name, h_zip, h_street, h_building_no, h_phone_no)
                VALUES (?, ?, ?, ?, ?)
                """;

        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, hotelName);
        preparedStatement.setString(2, hotelZip);
        preparedStatement.setString(3, hotelStreet);
        preparedStatement.setString(4, hotelBuildingNo);
        preparedStatement.setString(5, hotelNo);

        int rowCount = preparedStatement.executeUpdate();
        if (rowCount <= 0) {
            DataSource.closeConnection(connection);
            throw new SQLException("Failed to add hotel");
        }

        System.out.println("Hotel added");
        DataSource.closeConnection(connection);
    }

    private void displayHotels() throws SQLException {
        DataSource ds = new DataSource();
        Connection connection = ds.getConnection();

        String sql = """
                SELECT h_name, h_zip, h_street, h_building_no, h_phone_no
                FROM hotel
                """;
        PreparedStatement preparedStatement = connection.prepareStatement(sql);

        try (ResultSet resultSet = preparedStatement.executeQuery()) {
            System.out.println("Hotel List:");
            System.out.printf("%-20s %-10s %-20s %-15s %-15s%n", "Name", "ZIP", "Street", "Building No", "Phone No");

            while (resultSet.next()) {
                String name = resultSet.getString("h_name");
                String zip = resultSet.getString("h_zip");
                String street = resultSet.getString("h_street");
                String buildingNo = resultSet.getString("h_building_no");
                String phoneNo = resultSet.getString("h_phone_no");

                System.out.printf("%-20s %-10s %-20s %-15s %-15s%n", name, zip, street, buildingNo, phoneNo);
            }
        } finally {
            System.out.println();
            preparedStatement.close();
            connection.close();
        }
    }

    private void deleteHotel() throws SQLException {
        Scanner scanner = new Scanner(System.in);
        String hotelName;

        System.out.print("Enter hotel name to delete: ");
        hotelName = scanner.nextLine();

        DataSource ds = new DataSource();
        Connection connection = ds.getConnection();

        String sql = """
                DELETE FROM hotel
                WHERE h_name = ?
                """;
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, hotelName);

        System.out.println("Hotel to be deleted: " + hotelName);
        System.out.print("Enter y/Y to add, anything else to discard: ");

        String decision = scanner.nextLine();
        if (!decision.equalsIgnoreCase("Y")) {
            System.out.println("Operation discarded");
            connection.close();
            return;
        }

        int rowCount = preparedStatement.executeUpdate();
        if (rowCount <= 0) {
            DataSource.closeConnection(connection);
            throw new SQLException("Failed to delete hotel");
        }

        System.out.println("Hotel deleted");
        DataSource.closeConnection(connection);
    }

    private void addUser() throws Exception {
        Scanner scanner = new Scanner(System.in);
        String type, name, password, phoneNo;

        System.out.println("Enter user details");

        System.out.print("Enter user type(R:receptionist, H:housekeeper): ");
        type = scanner.nextLine();

        System.out.print("Enter username: ");
        name = scanner.nextLine();

        System.out.print("Enter password: ");
        password = scanner.nextLine();

        System.out.print("Enter phone number: ");
        phoneNo = scanner.nextLine();

        System.out.println("User to be added");
        System.out.println("Type: " + type + " Name: " + name + " Password: " + password + " Phone No: " + phoneNo);
        System.out.print("Enter y/Y to add, anything else to discard: ");

        String decision = scanner.nextLine();
        if (!decision.equalsIgnoreCase("Y")) {
            System.out.println("Operation discarded");
            return;
        }

        DataSource ds = new DataSource();
        Connection connection = ds.getConnection();

        String addUserSql = """
                INSERT INTO user (u_name, u_phone_no, u_password)
                VALUES (?, ?, ?)
                """;
        PreparedStatement preparedStatement = connection.prepareStatement(addUserSql);
        preparedStatement.setString(1, name);
        preparedStatement.setString(2, phoneNo);
        preparedStatement.setString(3, password);

        int rowCount = preparedStatement.executeUpdate();
        if (rowCount <= 0) {
            DataSource.closeConnection(connection);
            throw new SQLException("Failed to add user");
        }

        String addReceptionistSql = """
                INSERT INTO receptionist
                SELECT u_id
                FROM user
                WHERE u_name = ? AND u_password = ?
                """;

        String addHousekeeperSql = """
                INSERT INTO housekeeper
                SELECT u_id
                FROM user
                WHERE u_name = ? AND u_password = ?
                """;

        if (type.equalsIgnoreCase("R")) {
            PreparedStatement addReceptionistStatement = connection.prepareStatement(addReceptionistSql);
            addReceptionistStatement.setString(1, name);
            addReceptionistStatement.setString(2, password);

            rowCount = addReceptionistStatement.executeUpdate();
            if (rowCount <= 0) {
                DataSource.closeConnection(connection);
                throw new SQLException("Failed to add receptionist");
            }
        } else if (type.equalsIgnoreCase("H")) {
            PreparedStatement addHousekeeperStatement = connection.prepareStatement(addHousekeeperSql);
            addHousekeeperStatement.setString(1, name);
            addHousekeeperStatement.setString(2, password);

            rowCount = addHousekeeperStatement.executeUpdate();
            if (rowCount <= 0) {
                DataSource.closeConnection(connection);
                throw new SQLException("Failed to add housekeeper");
            }
        } else {
            DataSource.closeConnection(connection);
            throw new SQLException("Invalid user type");
        }

        System.out.println("User added");
        connection.close();
    }

    private void displayUsers() throws Exception {
        DataSource ds = new DataSource();
        Connection connection = ds.getConnection();

        String guests = """
                SELECT u_name, u_phone_no
                FROM user
                JOIN guest ON user.u_id = guest.g_id
                """;
        PreparedStatement guestStatement = connection.prepareStatement(guests);
        ResultSet guestResultSet = guestStatement.executeQuery();

        System.out.println("Guests:");
        System.out.printf("%-20s %-15s%n", "Name", "Phone Number");
        System.out.println("------------------------------------");
        while (guestResultSet.next()) {
            String name = guestResultSet.getString("u_name");
            String phone = guestResultSet.getString("u_phone_no");
            System.out.printf("%-20s %-15s%n", name, phone);
        }
        guestResultSet.close();
        System.out.println();

        String receptionists = """
                SELECT u_name, u_phone_no, u_password
                FROM user
                JOIN receptionist ON user.u_id = receptionist.r_id
                """;
        PreparedStatement receptionistStatement = connection.prepareStatement(receptionists);
        ResultSet receptionistResultSet = receptionistStatement.executeQuery();

        System.out.println("Receptionists:");
        System.out.printf("%-20s %-15s %-20s%n", "Name", "Phone Number", "Password");
        System.out.println("------------------------------------------------------------");
        while (receptionistResultSet.next()) {
            String name = receptionistResultSet.getString("u_name");
            String phone = receptionistResultSet.getString("u_phone_no");
            String password = receptionistResultSet.getString("u_password");
            System.out.printf("%-20s %-15s %-20s%n", name, phone, password);
        }
        receptionistResultSet.close();
        System.out.println();

        String housekeepers = """
                SELECT u_name, u_phone_no, u_password
                FROM user
                JOIN housekeeper ON user.u_id = housekeeper.hk_id
                """;
        PreparedStatement housekeeperStatement = connection.prepareStatement(housekeepers);
        ResultSet housekeeperResultSet = housekeeperStatement.executeQuery();

        System.out.println("Housekeepers:");
        System.out.printf("%-20s %-15s %-20s%n", "Name", "Phone Number", "Password");
        System.out.println("------------------------------------------------------------");
        while (housekeeperResultSet.next()) {
            String name = housekeeperResultSet.getString("u_name");
            String phone = housekeeperResultSet.getString("u_phone_no");
            String password = housekeeperResultSet.getString("u_password");
            System.out.printf("%-20s %-15s %-20s%n", name, phone, password);
        }
        housekeeperResultSet.close();
        System.out.println();

        connection.close();
    }

    private void deleteUser() throws Exception {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter username to delete: ");
        String userName = scanner.nextLine();

        DataSource ds = new DataSource();
        Connection connection = ds.getConnection();

        String sql = """
                DELETE FROM user
                WHERE u_name = ? AND NOT EXISTS (
                       SELECT 1
                       FROM administrator
                       WHERE user.u_id = administrator.a_id
                );
                """;
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, userName);

        System.out.println("User to be deleted: " + userName);
        System.out.print("Enter y/Y to add, anything else to discard: ");

        String decision = scanner.nextLine();
        if (!decision.equalsIgnoreCase("Y")) {
            System.out.println("Operation discarded");
            connection.close();
            return;
        }

        int rowCount = preparedStatement.executeUpdate();
        if (rowCount <= 0) {
            DataSource.closeConnection(connection);
            throw new SQLException("Failed to delete user");
        }

        System.out.println("User deleted");
        DataSource.closeConnection(connection);
    }

    private void addRoomType() throws Exception {
        String roomType;
        int capacity;

        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter room type details");
        System.out.print("Enter room type: ");
        roomType = scanner.nextLine();

        System.out.print("Enter capacity: ");
        capacity = Integer.parseInt(scanner.nextLine());

        DataSource ds = new DataSource();
        Connection connection = ds.getConnection();

        System.out.println("Room type to add: " + roomType + ", capacity: " + capacity);
        System.out.print("Enter y/Y to add, anything else to discard: ");

        String decision = scanner.nextLine();
        if (!decision.equalsIgnoreCase("Y")) {
            System.out.println("Operation discarded");
            connection.close();
            return;
        }

        String sql = """
                INSERT INTO roomtype (r_type, capacity)
                VALUES (?, ?)
                """;
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, roomType);
        preparedStatement.setInt(2, capacity);

        int rowCount = preparedStatement.executeUpdate();
        if (rowCount <= 0) {
            DataSource.closeConnection(connection);
            throw new SQLException("Failed to add room type");
        }

        System.out.println("Room type added");
    }

    private void displayRoomTypes() throws Exception {
        DataSource ds = new DataSource();
        Connection connection = ds.getConnection();

        String sql = """
                SELECT r_type, capacity
                FROM roomtype
                """;
        PreparedStatement preparedStatement = connection.prepareStatement(sql);

        ResultSet resultSet = preparedStatement.executeQuery();
        System.out.println("Room Types and Capacities:");
        System.out.println("---------------------------");
        while (resultSet.next()) {
            String roomType = resultSet.getString("r_type");
            int capacity = resultSet.getInt("capacity");
            System.out.printf("Room Type: %s, Capacity: %d%n", roomType, capacity);
        }
        System.out.println();
        resultSet.close();
        preparedStatement.close();
        connection.close();
    }

    private void deleteRoomType() throws SQLException {
        String roomType;
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter room type to delete: ");
        roomType = scanner.nextLine();

        DataSource ds = new DataSource();
        Connection connection = ds.getConnection();

        String sql = """
                DELETE FROM roomtype
                WHERE r_type = ?
                """;
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, roomType);

        int rowCount = preparedStatement.executeUpdate();
        if (rowCount <= 0) {
            DataSource.closeConnection(connection);
            throw new SQLException("Failed to delete room type");
        }

        System.out.println("Room type deleted");
        connection.close();
    }

    private void addRoom() throws SQLException {
        String hotelName, roomType;
        int roomId;

        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter room details");
        System.out.print("Enter hotel name: ");
        hotelName = scanner.nextLine();

        System.out.print("Enter room number: ");
        roomId = Integer.parseInt(scanner.nextLine());

        System.out.print("Enter room type: ");
        roomType = scanner.nextLine();

        DataSource ds = new DataSource();
        Connection connection = ds.getConnection();

        String sql = """
                INSERT INTO room (r_id, h_id, r_type, r_status)
                SELECT ?, h_id, ?, ?
                FROM hotel
                WHERE hotel.h_name = ?
                """;
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, roomId);
        preparedStatement.setString(2, roomType);
        preparedStatement.setString(3, "available");
        preparedStatement.setString(4, hotelName);

        System.out.print("Enter y/Y to confirm, anything else to discard: ");

        String decision = scanner.nextLine();
        if (!decision.equalsIgnoreCase("Y")) {
            System.out.println("Operation discarded");
            connection.close();
            return;
        }

        int rowCount = preparedStatement.executeUpdate();
        if (rowCount <= 0) {
            DataSource.closeConnection(connection);
            throw new SQLException("Failed to add room");
        }

        System.out.println("Room added");
        connection.close();
    }

    public void displayRooms() throws SQLException {
        DataSource ds = new DataSource();
        Connection connection = ds.getConnection();

        String sql = """
                SELECT hotel.h_name, room.r_id, room.r_type, roomtype.capacity, room.r_status
                FROM room
                JOIN roomtype ON room.r_type = roomtype.r_type
                JOIN hotel ON hotel.h_id = room.h_id
                """;
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ResultSet resultSet = preparedStatement.executeQuery();

        System.out.println("----------------------------------------------------------------------------------");
        System.out.printf("%-20s %-10s %-15s %-10s %-15s%n", "Hotel Name", "Room ID", "Room Type", "Capacity", "Status");
        System.out.println("----------------------------------------------------------------------------------");

        while (resultSet.next()) {
            String hotelName = resultSet.getString("h_name");
            int roomId = resultSet.getInt("r_id");
            String roomType = resultSet.getString("r_type");
            int capacity = resultSet.getInt("capacity");
            String roomStatus = resultSet.getString("r_status");

            System.out.printf("%-20s %-10d %-15s %-10d %-15s%n", hotelName, roomId, roomType, capacity, roomStatus);
        }
        System.out.println();
        resultSet.close();
        connection.close();
    }

    private void deleteRoom() throws SQLException {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter room details to delete");
        System.out.print("Enter hotel name: ");
        String hotelName = scanner.nextLine();

        System.out.print("Enter room number: ");
        int roomId = Integer.parseInt(scanner.nextLine());

        DataSource ds = new DataSource();
        Connection connection = ds.getConnection();

        String sql = """
                DELETE r
                FROM room r
                JOIN hotel h ON r.h_id = h.h_id
                WHERE h.h_name = ? AND r.r_id = ?
                """;

        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, hotelName);
        preparedStatement.setInt(2, roomId);

        System.out.print("Enter y/Y to confirm, anything else to discard: ");

        String decision = scanner.nextLine();
        if (!decision.equalsIgnoreCase("Y")) {
            System.out.println("Operation discarded");
            connection.close();
            return;
        }

        int rowCount = preparedStatement.executeUpdate();
        if (rowCount <= 0) {
            DataSource.closeConnection(connection);
            throw new SQLException("Failed to delete room");
        }

        System.out.println("Room deleted");
        connection.close();
    }

    private void editRoomStatus() throws SQLException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter room details to edit");
        System.out.print("Enter hotel name: ");
        String hotelName = scanner.nextLine();

        System.out.print("Enter room number: ");
        int roomId = Integer.parseInt(scanner.nextLine());

        System.out.print("Enter new room status(A:available, R:reservation_pending, P:payment_pending, B:booked): ");
        String status = scanner.nextLine();

        switch (status) {
            case "A" -> status = "available";
            case "R" -> status = "r_pending";
            case "P" -> status = "p_pending";
            case "B" -> status = "booked";
            default -> {
                System.out.println("Invalid status.");
                return;
            }
        }

        DataSource ds = new DataSource();
        Connection connection = ds.getConnection();

        String sql = """
                UPDATE room r
                JOIN hotel h ON r.h_id = h.h_id
                SET r.r_status = ?
                WHERE h.h_name = ? AND r.r_id = ?;
                """;
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, status);
        preparedStatement.setString(2, hotelName);
        preparedStatement.setInt(3, roomId);

        System.out.print("Enter y/Y to confirm, anything else to discard: ");
        String decision = scanner.nextLine();
        if (!decision.equalsIgnoreCase("Y")) {
            System.out.println("Operation discarded");
            connection.close();
            return;
        }

        int rowCount = preparedStatement.executeUpdate();
        if (rowCount <= 0) {
            DataSource.closeConnection(connection);
            throw new SQLException("Failed to edit room");
        }

        System.out.println("Room edited");
        connection.close();
    }

    private void displayAllBookingRecords() throws Exception {
        DataSource ds = new DataSource();
        Connection connection = ds.getConnection();

        String sql = """
                SELECT h_name, user.u_name, total_guests, check_in_date, check_out_date, status
                FROM booking
                JOIN user ON u_id = booking.g_id
                """;
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ResultSet resultSet = preparedStatement.executeQuery();

        System.out.printf("%-20s %-20s %-15s %-15s %-15s %-10s%n",
                "Hotel Name", "User Name", "Total Guests", "Check-in Date", "Check-out Date", "Status");
        System.out.println("---------------------------------------------------------------------------------------------");

        while (resultSet.next()) {
            String hotelName = resultSet.getString("h_name");
            String userName = resultSet.getString("u_name");
            int totalGuests = resultSet.getInt("total_guests");
            Date checkInDate = resultSet.getDate("check_in_date");
            Date checkOutDate = resultSet.getDate("check_out_date");
            String status = resultSet.getString("status");

            System.out.printf("%-20s %-20s %-15d %-15s %-15s %-10s%n",
                    hotelName, userName, totalGuests, checkInDate, checkOutDate, status);
        }
        System.out.println();

        resultSet.close();
        preparedStatement.close();
        connection.close();
    }

    private void viewMostBookedRoomTypes() throws Exception {
        DataSource ds = new DataSource();
        Connection connection = ds.getConnection();

        String sql = """
                SELECT room.r_type AS RoomType, COUNT(*) AS BookingCount
                FROM reservations
                JOIN room ON reservations.r_id = room.r_id
                GROUP BY room.r_type
                ORDER BY COUNT(*) DESC
                LIMIT 5
                """;

        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ResultSet resultSet = preparedStatement.executeQuery();

        System.out.printf("%-20s %-15s%n", "Room Type", "Booking Count");
        System.out.println("---------------------------------------");

        while (resultSet.next()) {
            String roomType = resultSet.getString("RoomType");
            int bookingCount = resultSet.getInt("BookingCount");

            System.out.printf("%-20s %-15d%n", roomType, bookingCount);
        }
        System.out.println();

        resultSet.close();
        preparedStatement.close();
        connection.close();
    }

    private void displayAllHousekeepingRecords() throws Exception {
        DataSource ds = new DataSource();
        Connection connection = ds.getConnection();

        String sql = """
                SELECT t_start_date AS Start,
                       t_end_date AS End,
                       status AS Status,
                       h_name AS Hotel,
                       housekeeping_rooms.r_id AS Room,
                       u_name AS Staff
                FROM housekeeping_schedule
                LEFT JOIN housekeeping_rooms ON housekeeping_schedule.t_id = housekeeping_rooms.t_id
                LEFT JOIN housekeeping_staff ON housekeeping_schedule.t_id = housekeeping_staff.t_id
                JOIN user ON housekeeping_staff.hk_id = user.u_id
                JOIN room ON room.r_id = housekeeping_rooms.r_id
                JOIN hotel ON hotel.h_id = room.h_id
                """;
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ResultSet resultSet = preparedStatement.executeQuery();

        System.out.printf("%-15s %-15s %-10s %-20s %-10s %-20s%n",
                "Start", "End", "Status", "Hotel", "Room", "Staff");
        System.out.println("-------------------------------------------------------------------------------------------");

        while (resultSet.next()) {
            String start = resultSet.getString("Start");
            String end = resultSet.getString("End");
            String status = resultSet.getString("Status");
            String hotel = resultSet.getString("Hotel");
            int room = resultSet.getInt("Room");
            String staff = resultSet.getString("Staff");

            System.out.printf("%-15s %-15s %-10s %-20s %-10d %-20s%n",
                    start, end, status, hotel, room, staff);
        }
        System.out.println();

        resultSet.close();
        preparedStatement.close();
        connection.close();
    }

    private void generateRevenueReport() throws Exception {
        DataSource ds = new DataSource();
        Connection connection = ds.getConnection();

        String sql = """
                SELECT hotel.h_name, SUM(payments.amount), COUNT(DISTINCT booking.b_id)
                FROM payments
                JOIN booking ON payments.b_id = booking.b_id
                JOIN hotel ON booking.h_name = hotel.h_name
                GROUP BY hotel.h_name
                ORDER BY SUM(payments.amount) DESC
                """;
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ResultSet resultSet = preparedStatement.executeQuery();

        System.out.printf("%-25s %-20s %-15s%n", "Hotel Name", "Total Revenue", "Total Bookings");
        System.out.println("------------------------------------------------------------------");

        while (resultSet.next()) {
            String hotelName = resultSet.getString("HotelName");
            double totalRevenue = resultSet.getDouble("TotalRevenue");
            int totalBookings = resultSet.getInt("TotalBookings");

            System.out.printf("%-25s %-20.2f %-15d%n", hotelName, totalRevenue, totalBookings);
        }
        System.out.println();

        resultSet.close();
        preparedStatement.close();
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
                        addHotel();
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                    view.display();
                }
                case 2 -> {
                    try {
                        displayHotels();
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                    view.display();
                }
                case 3 -> {
                    try {
                        deleteHotel();
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                    view.display();
                }
                case 4 -> {
                    try {
                        addUser();
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                    view.display();
                }
                case 5 -> {
                    try {
                        displayUsers();
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                    view.display();
                }
                case 6 -> {
                    try {
                        deleteUser();
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                    view.display();
                }
                case 7 -> {
                    try {
                        addRoom();
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                    view.display();
                }
                case 8 -> {
                    try {
                        displayRooms();
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                    view.display();
                }
                case 9 -> {
                    try {
                        deleteRoom();
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                    view.display();
                }
                case 10 -> {
                    try {
                        editRoomStatus();
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                    view.display();
                }
                case 11 -> {
                    try {
                        addRoomType();
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                    view.display();
                }
                case 12 -> {
                    try {
                        displayRoomTypes();
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                    view.display();
                }
                case 13 -> {
                    try {
                        deleteRoomType();
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                    view.display();
                }
                case 14 -> {
                    try {
                        displayAllBookingRecords();
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                    view.display();
                }
                case 15 -> {
                    try {
                        viewMostBookedRoomTypes();
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                    view.display();
                }
                case 16 -> {
                    try {
                        displayAllHousekeepingRecords();
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                    view.display();
                }
                case 17 -> {
                    try {
                        generateRevenueReport();
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                    view.display();
                }
                case 90 -> {
                    terminated = true;
                    view.display("LANDING");
                }
                case 99 -> System.exit(0);
            }
        }
    }
}
