package org.hms.input.handlers;

import org.hms.View;
import org.hms.database.DataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
                                   h_id AS Hotel_ID,         
                                   h_name AS Hotel_Name
                               FROM
                                   housekeeping_rooms
                               JOIN
                                   room r on housekeeping_rooms.r_id = r.r_id
                                JOIN 
                                   hotel h on r.h_id = h.h_id
                               WHERE
                                   housekeeping_rooms.status = 'dirty';  
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
                                   h_id AS Hotel_ID,         
                                   h_name AS Hotel_Name
                               FROM
                                   housekeeping_rooms
                               JOIN
                                   room r on housekeeping_rooms.r_id = r.r_id
                                JOIN 
                                   hotel h on r.h_id = h.h_id
                               WHERE
                                   housekeeping_rooms.status = 'clean';  
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
    private void updateTaskStatusToCompleted(int r_id) throws Exception{
        DataSource ds = new DataSource();
        Connection connection = ds.getConnection();

        String sql = """
                  UPDATE housekeeping_rooms 
                  SET housekeeping_rooms.status = 'clean'
                  WHERE r_id = ?;
                """;

        PreparedStatement preparedStatement = connection.prepareStatement(sql);

        try {

            preparedStatement.setInt(1, r_id);

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
        String userName = view.getController().getUsername(); // Bu metod, kullanıcıdan temizlik görevlisinin ID'sini alabilir.

        // Temizlik görevlisinin görevli olduğu odaların listesini almak için sorgu yazıyoruz
        DataSource ds = new DataSource();
        Connection connection = ds.getConnection();

        // SQL sorgusu, temizlik görevlisinin görevli olduğu odaları listeliyor.
        String sql = """
                  SELECT
                       housekeeping_rooms.r_id AS Room_ID,
                       h_id AS Hotel_ID,
                       h_name AS Hotel_Name,
                       housekeeping_rooms.status AS Room_Status
                  FROM
                       housekeeping_rooms
                  JOIN
                       hotel ON room.h_id = hotel.h_id
                  JOIN
                       housekeeping_rooms ON room.r_id = housekeeping_rooms.r_id
                  WHERE
                       housekeeping_rooms.staff_id = ?;  -- Temizlik görevlisinin ID'si
                """;

        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, staffId);  // Temizlik görevlisinin ID'sini parametre olarak ekliyoruz

        try (ResultSet resultSet = preparedStatement.executeQuery()) {
            System.out.println("My Cleaning Schedule:");
            System.out.printf("%-10s %-10s %-20s %-10s%n", "Room ID", "Hotel ID", "Hotel Name", "Room Status");
            System.out.println("----------------------------------------------------");

            while (resultSet.next()) {
                int roomId = resultSet.getInt("Room_ID");
                int hotelId = resultSet.getInt("Hotel_ID");
                String hotelName = resultSet.getString("Hotel_Name");
                String roomStatus = resultSet.getString("Room_Status");

                // Temizlik görevlisinin görevli olduğu odaların bilgilerini yazdırıyoruz
                System.out.printf("%-10d %-10d %-20s %-10s%n", roomId, hotelId, hotelName, roomStatus);
            }
        } finally {
            preparedStatement.close();
            connection.close();
        }
    }

    public void handleInput() {}
}
