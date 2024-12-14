
package org.hms.input.handlers;

import org.hms.View;
import org.hms.database.DataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class GuestPanelInputHandler extends InputHandlerBase {
    public GuestPanelInputHandler(View view) {
        super(view);}

    private void addNewBooking() throws SQLException {
        Scanner scanner = new Scanner(System.in);
        int totalGuest;
        String checkInDate,checkOutDate,hotelName ,userName;

        System.out.println("Enter booking details");

        System.out.println("Enter Hotel Name");
        hotelName = scanner.nextLine();

        System.out.println("Enter Check-In Date");
        checkInDate = scanner.nextLine();

        System.out.println("Enter Check-Out Date");
        checkOutDate = scanner.nextLine();

        userName = view.getController().getUsername();

        System.out.println("Enter Total Guest Count");
        totalGuest = Integer.parseInt(scanner.nextLine());
        System.out.println("Booking to be created");
        System.out.println("Dear " + userName + "," + "your booking has been created for " + totalGuest + "people");

        System.out.print("Enter y/Y to add, anything else to discard: ");
        String decision = scanner.nextLine();
        if (!decision.equalsIgnoreCase("Y")) {
            System.out.println("Add booking process discarded");
            return;
        }
        DataSource ds = new DataSource();
        Connection connection = ds.getConnection();


        String sql = """
                INSERT INTO booking (g_id, h_name, total_guests, check_in_date, check_out_date, status)
                SELECT u_id, ?, ?, ?, ?, ?
                FROM user
                WHERE u_name = ?;
                     """;


        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, hotelName);
        preparedStatement.setInt(2, totalGuest);
        preparedStatement.setString(3, checkInDate);
        preparedStatement.setString(4, checkOutDate);
        preparedStatement.setString(5, "r_pending");
        preparedStatement.setString(6, userName);


        int rowCount = preparedStatement.executeUpdate();
        if (rowCount <= 0) {
            DataSource.closeConnection(connection);
            throw new SQLException("Failed to add booking");
        }

        System.out.println("Booking added");
        DataSource.closeConnection(connection);
    }
    private void displayAvailableRooms() throws SQLException {
        DataSource ds = new DataSource();
        Connection connection = ds.getConnection();

        String sql = """
                            SELECT
                                roomtype.r_type AS Room_Type,
                                roomtype.capacity AS Capacity,
                                hotel.h_name AS Hotel_Name
                            FROM
                                room
                            JOIN
                                roomtype ON room.r_type = roomtype.r_type
                            JOIN
                                hotel ON room.h_id = hotel.h_id
                            WHERE
                                room.r_status = 'available';
                """;
        PreparedStatement preparedStatement = connection.prepareStatement(sql);

        try (ResultSet resultSet = preparedStatement.executeQuery()) {
            System.out.println("Room List:");
            System.out.printf("%-20s %-10s %-20s%n", "Room Type", "Capacity", "Hotel Name");
            System.out.println("----------------------------------------------------");

            while (resultSet.next()) {
                String type = resultSet.getString("Room Type");
                String capacity = resultSet.getString("Capacity");
                String name = resultSet.getString("Hotel Name");

                System.out.printf("%-20s %-10s %-20s%n", type, capacity, name);
            }
        } finally {
            System.out.println();
            preparedStatement.close();
            connection.close();
        }
    }
    private void displayMyBooking()throws SQLException{
        DataSource ds = new DataSource();
        Connection connection = ds.getConnection();

        String sql = """
                 SELECT
                                    booking.b_id AS Booking_ID,
                                    hotel.h_name AS Hotel_Name,
                                    booking.total_guests AS Total_Guests,
                                    booking.check_in_date AS Check_In_Date,
                                    booking.check_out_date AS Check_Out_Date,
                                    booking.status AS Status
                                FROM
                                    booking
                                JOIN
                                    hotel ON booking.h_name = hotel.h_name
                                JOIN
                                    user ON booking.g_id = user.u_id
                                WHERE
                                    user.u_name = ?;
                         
                """;
        PreparedStatement preparedStatement = connection.prepareStatement(sql);

        try (ResultSet resultSet = preparedStatement.executeQuery()) {
            System.out.println("My Bookings::");
            System.out.printf("%-10s %-20s %-15s %-15s %-15s %-10s%n",
                    "ID", "Hotel Name", "Guests", "Check-In", "Check-Out", "Status");
            System.out.println("----------------------------------------------------");

            while (resultSet.next()) {
                int bookingId = resultSet.getInt("Booking_ID");
                String hotelName = resultSet.getString("Hotel_Name");
                int totalGuests = resultSet.getInt("Total_Guests");
                String checkInDate = resultSet.getString("Check_In_Date");
                String checkOutDate = resultSet.getString("Check_Out_Date");
                String status = resultSet.getString("Status");

                System.out.printf("%-10d %-20s %-15d %-15s %-15s %-10s%n",
                        bookingId, hotelName, totalGuests, checkInDate, checkOutDate, status);
            }
        } finally {
            System.out.println();
            preparedStatement.close();
            connection.close();
        }
    }
    private void cancelBookingForGuest()throws SQLException{
        Scanner scanner = new Scanner(System.in);


        System.out.println("Enter the Booking ID to cancel:");
        int bookingId = Integer.parseInt(scanner.nextLine());

        DataSource ds = new DataSource();
        Connection connection = ds.getConnection();

        String sqlCheck = """
            SELECT b_id, status 
            FROM booking 
            WHERE b_id = ? AND g_id = (
                SELECT u_id FROM user WHERE u_name = ?
            );
        """;

        PreparedStatement preparedStatementCheck = connection.prepareStatement(sqlCheck);
        preparedStatementCheck.setInt(1, bookingId);
        preparedStatementCheck.setString(2, view.getController().getUsername());

        ResultSet resultSet = preparedStatementCheck.executeQuery();

        if (!resultSet.next()) {
            System.out.println("No booking found with the provided ID for the current user.");
            preparedStatementCheck.close();
            connection.close();
            return;
        }

        String status = resultSet.getString("status");


        if (!"r_pending".equals(status)) {
            System.out.println("Only pending bookings can be canceled.");
            preparedStatementCheck.close();
            connection.close();
            return;
        }


        String sqlCancel = "DELETE FROM booking WHERE b_id = ?";

        PreparedStatement preparedStatementCancel = connection.prepareStatement(sqlCancel);
        preparedStatementCancel.setInt(1, bookingId);

        int rowsAffected = preparedStatementCancel.executeUpdate();

        if (rowsAffected > 0) {
            System.out.println("Booking successfully canceled.");
        } else {
            System.out.println("Failed to cancel the booking. Please try again.");
        }

        preparedStatementCheck.close();
        preparedStatementCancel.close();
        connection.close();
    }
    public void handleInput() {
    Scanner scanner = new Scanner(System.in);
        String choice = scanner.nextLine();
        try {
            switch (choice) {
                case "1":
                    addNewBooking();
                    break;
                case "2":
                    displayAvailableRooms();
                    break;
                case "3":
                    displayMyBooking();
                    break;
                case "4":
                    cancelBookingForGuest();
                    break;
                case "5":
                    System.out.println("Exiting Guest Panel...");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } catch (SQLException e) {
            System.err.println("An error occurred while processing your request: " + e.getMessage());
        }
    }

}


