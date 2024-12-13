package org.hms.input.handlers;

import org.hms.View;
import org.hms.database.DataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;

public class SignupPageInputHandler extends InputHandlerBase {
    private String username = null;
    private String password = null;
    private String phoneNumber = null;

    public SignupPageInputHandler(View view) {
        super(view);
    }

    private void handleUsername() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Username: ");

        while(!scanner.hasNextLine()) {
            System.out.print("Username: ");
            scanner.nextLine();
        }

        username = scanner.nextLine();
        System.out.println("Username set to: " + username);
    }

    private void handlePassword() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Password: ");

        while(!scanner.hasNextLine()) {
            System.out.print("Password: ");
            scanner.nextLine();
        }

        password = scanner.nextLine();
    }

    private void insertNewGuest(Connection connection) throws SQLException {
        String sql = """
                INSERT INTO user (u_name, u_phone_no, u_password)
                VALUES (?, ?, ?)
                """;

        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, username);
        preparedStatement.setString(3, password);
        preparedStatement.setString(2, phoneNumber);

        int rowCount = preparedStatement.executeUpdate();
        if (rowCount <= 0) {
            throw new SQLException("Failed to insert new user");
        }

        sql = """
              INSERT INTO guest (g_id)
              SELECT u_id
              FROM user
              WHERE user.u_name = ? AND user.u_password = ?
             """;

        preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, username);
        preparedStatement.setString(2, password);

        rowCount = preparedStatement.executeUpdate();
        if (rowCount <= 0) {
            throw new SQLException("Failed to insert new guest");
        }
    }

    private void handleSignup() throws Exception {
        if (username == null || password == null || phoneNumber == null) {
            System.out.println("Please enter your name, password, and your phone number");
            return;
        }

        DataSource dataSource = new DataSource("root", "1234");
        Connection connection = dataSource.getConnection();

        insertNewGuest(connection);

        DataSource.closeConnection(connection);
    }

    private void handlePhoneNumber() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Phone Number: ");

        while(!scanner.hasNextLine()) {
            System.out.print("Phone Number: ");
            scanner.nextLine();
        }

        phoneNumber = scanner.nextLine();
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
                    handleUsername();
                }
                case 2 -> {
                    handlePassword();
                }
                case 3 -> {
                    handlePhoneNumber();
                }
                case 4 -> {
                    try {
                        handleSignup();
                        terminated = true;
                        username = null;
                        password = null;
                        phoneNumber = null;
                        view.display("LANDING");
                    } catch (Exception e) {
                        System.out.println("Sign up failed.");
                    }
                }
                case 8 -> {
                    terminated = true;
                    view.display("LANDING");
                }
                case 9 -> {
                    System.exit(0);
                }
            }
        }
    }
}
