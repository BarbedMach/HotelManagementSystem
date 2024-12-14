package org.hms.input.handlers;

import org.hms.View;
import org.hms.database.DataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
            System.out.println("Add hotel process discarded");
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
                FROM hotel;
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
            System.out.println("Deletion discarded");
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

    private void addUser() throws SQLException {
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
            System.out.println("Add user process discarded");
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

    private void displayUsers() throws SQLException {
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

    private void deleteUser() throws SQLException {
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
            System.out.println("Deletion discarded");
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
                    } catch (SQLException e) {
                        System.out.println(e.getMessage());
                    }
                    view.display();
                }
                case 2 -> {
                    try {
                        displayHotels();
                    } catch (SQLException e) {
                        System.out.println(e.getMessage());
                    }
                    view.display();
                }
                case 3 -> {
                    try {
                        deleteHotel();
                    } catch (SQLException e) {
                        System.out.println(e.getMessage());
                    }
                    view.display();
                }
                case 4 -> {
                    try {
                        addUser();
                    } catch (SQLException e) {
                        System.out.println(e.getMessage());
                    }
                    view.display();
                }
                case 5 -> {
                    try {
                        displayUsers();
                    } catch (SQLException e) {
                        System.out.println(e.getMessage());
                    }
                    view.display();
                }
                case 6 -> {
                    try {
                        deleteUser();
                    } catch (SQLException e) {
                        System.out.println(e.getMessage());
                    }
                }
                case 14 -> {
                    terminated = true;
                    view.display("LANDING");
                }
                case 15 -> System.exit(0);
            }
        }
    }
}
