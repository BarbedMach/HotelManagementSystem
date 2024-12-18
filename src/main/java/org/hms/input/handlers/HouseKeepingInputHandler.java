package org.hms.input.handlers;

import org.hms.View;
import org.hms.database.DataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class HouseKeepingInputHandler extends InputHandlerBase{
    public HouseKeepingInputHandler(View view) {
        super(view);
    }

    private void displayPendingHousekeeping() throws SQLException{
        DataSource ds = new DataSource();
        Connection connection = ds.getConnection();

        String sql = """
                              SELECT
                                   housekeeping_rooms.r_id AS Room_ID,
                                   h.h_id AS Hotel_ID,
                                   h_name AS Hotel_Name
                               FROM
                                   housekeeping_rooms
                               JOIN
                                   room r on housekeeping_rooms.r_id = r.r_id
                                JOIN
                                   hotel h on r.h_id = h.h_id
                               WHERE
                                   housekeeping_rooms.status = 'dirty'
                """;


        PreparedStatement preparedStatement = connection.prepareStatement(sql);

        try (ResultSet resultSet = preparedStatement.executeQuery()) {
            System.out.println("Pending Housekeeping Tasks:");
            System.out.printf("%-10s %-10s %-20s%n", "Room ID", "Hotel ID", "Hotel Name");
            System.out.println("----------------------------------------------------");


            while (resultSet.next()) {
                int roomId = resultSet.getInt("Room_ID");
                int hotelId = resultSet.getInt("Hotel_ID");
                String hotelName = resultSet.getString("Hotel_Name");

                System.out.printf("%-10d %-10d %-20s%n", roomId, hotelId, hotelName);
            }
        } finally {
            preparedStatement.close();
            connection.close();
        }
    }

    private void displayCompletedHousekeeping()throws SQLException{
        DataSource ds = new DataSource();
        Connection connection = ds.getConnection();

        String sql = """
                              SELECT
                                   housekeeping_rooms.r_id AS Room_ID,
                                   h.h_id AS Hotel_ID,
                                   h_name AS Hotel_Name
                               FROM
                                   housekeeping_rooms
                               JOIN
                                   room r on housekeeping_rooms.r_id = r.r_id
                                JOIN
                                   hotel h on r.h_id = h.h_id
                               WHERE
                                   housekeeping_rooms.status = 'clean'
                """;


        PreparedStatement preparedStatement = connection.prepareStatement(sql);

        try (ResultSet resultSet = preparedStatement.executeQuery()) {
            System.out.println("Completed Housekeeping Tasks:");
            System.out.printf("%-10s %-10s %-20s%n", "Room ID", "Hotel ID", "Hotel Name");
            System.out.println("----------------------------------------------------");


            while (resultSet.next()) {
                int roomId = resultSet.getInt("Room_ID");
                int hotelId = resultSet.getInt("Hotel_ID");
                String hotelName = resultSet.getString("Hotel_Name");

                System.out.printf("%-10d %-10d %-20s%n", roomId, hotelId, hotelName);
            }
        } finally {
            preparedStatement.close();
            connection.close();
        }
    }
    private void updateTaskStatusToCompleted() throws Exception{
        DataSource ds = new DataSource();
        Connection connection = ds.getConnection();

        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter hotel name: ");
        String hotelName = scanner.nextLine();

        System.out.print("Enter Room ID: ");
        int r_id = Integer.parseInt(scanner.nextLine());

        String sql = """
                  UPDATE housekeeping_rooms
                  SET housekeeping_rooms.status = 'clean'
                  WHERE r_id = ? AND h_id = (
                      SELECT h_id
                      FROM hotel
                      WHERE h_name = ?
                  )
                """;

        PreparedStatement preparedStatement = connection.prepareStatement(sql);

        try {

            preparedStatement.setInt(1, r_id);
            preparedStatement.setString(2, hotelName);

            int rowsUpdated = preparedStatement.executeUpdate();

            if (rowsUpdated > 0) {
                System.out.println("Room status updated to 'clean' successfully.");
            } else {
                System.out.println("No room found with ID: " + r_id);
            }
        } finally {
            preparedStatement.close();
            connection.close();
        }
    }
    private void displayMyCleaningSchedule() throws SQLException {
        String userName = view.getController().getUsername();


        DataSource ds = new DataSource();
        Connection connection = ds.getConnection();


        String sql = """
                SELECT
                    housekeeping_rooms.r_id AS Room_ID,
                    housekeeping_rooms.h_id AS Hotel_ID,
                    hotel.h_name AS Hotel_Name,
                    housekeeping_rooms.status AS Room_Status
                FROM user
                JOIN housekeeping_staff ON user.u_id = housekeeping_staff.hk_id
                JOIN housekeeping_schedule ON housekeeping_staff.t_id = housekeeping_schedule.t_id
                JOIN housekeeping_rooms ON housekeeping_schedule.t_id = housekeeping_rooms.t_id
                JOIN hotel ON hotel.h_id = housekeeping_rooms.h_id
                WHERE user.u_name = ?
                """;

        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, userName);

        try (ResultSet resultSet = preparedStatement.executeQuery()) {
            System.out.println("My Cleaning Schedule:");
            System.out.printf("%-10s %-10s %-20s %-10s%n", "Room ID", "Hotel ID", "Hotel Name", "Room Status");
            System.out.println("----------------------------------------------------");

            while (resultSet.next()) {
                int roomId = resultSet.getInt("Room_ID");
                int hotelId = resultSet.getInt("Hotel_ID");
                String hotelName = resultSet.getString("Hotel_Name");
                String roomStatus = resultSet.getString("Room_Status");


                System.out.printf("%-10d %-10d %-20s %-10s%n", roomId, hotelId, hotelName, roomStatus);
            }
        } finally {
            preparedStatement.close();
            connection.close();
        }
    }

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
                        displayPendingHousekeeping();
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                    view.display();
                }
                case 2 -> {
                    try {
                        displayCompletedHousekeeping();
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                    view.display();
                }
                case 3 -> {
                    try {
                        updateTaskStatusToCompleted();
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                    view.display();
                }
                case 4 -> {
                    try {
                        displayMyCleaningSchedule();
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
