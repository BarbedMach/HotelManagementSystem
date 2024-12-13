package org.hms.input.handlers;

import org.hms.View;
import org.hms.database.DataSource;
import org.hms.database.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class LoginPageInputHandler extends InputHandlerBase {
    private String username = null;
    private String password = null;

    public LoginPageInputHandler(View view) {
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

    private boolean checkIfAdmin(Connection connection) throws SQLException {
        String checkIfAdminQuery = """
                SELECT COUNT(*) FROM administrator
                JOIN user ON administrator.a_id = user.u_id
                WHERE user.u_name = ? AND user.u_password = ?
                """;

        PreparedStatement checkIfAdminStatement = connection.prepareStatement(checkIfAdminQuery);
        checkIfAdminStatement.setString(1, username);
        checkIfAdminStatement.setString(2, password);

        ResultSet checkIfAdminResultSet = checkIfAdminStatement.executeQuery();

        if (checkIfAdminResultSet.next()) {
            int count = checkIfAdminResultSet.getInt(1);
            return count == 1;
        }
        return false;
    }

    private boolean checkIfGuest(Connection connection) throws SQLException {
        String checkIfGuestQuery = """
                SELECT COUNT(*) FROM guest
                JOIN user ON guest.g_id = user.u_id
                WHERE user.u_name = ? AND user.u_password = ?
                """;

        PreparedStatement checkIfGuestStatement = connection.prepareStatement(checkIfGuestQuery);
        checkIfGuestStatement.setString(1, username);
        checkIfGuestStatement.setString(2, password);

        ResultSet checkIfGuestResultSet = checkIfGuestStatement.executeQuery();

        if (checkIfGuestResultSet.next()) {
            int count = checkIfGuestResultSet.getInt(1);
            return count == 1;
        }
        return false;
    }

    private boolean checkIfHousekeeper(Connection connection) throws SQLException {
        String checkIfHousekeeperQuery = """
                SELECT COUNT(*) FROM housekeeper
                JOIN user ON housekeeper.hk_id = user.u_id
                WHERE user.u_name = ? AND user.u_password = ?
                """;

        PreparedStatement checkIfHousekeeperStatement = connection.prepareStatement(checkIfHousekeeperQuery);
        checkIfHousekeeperStatement.setString(1, username);
        checkIfHousekeeperStatement.setString(2, password);

        ResultSet checkIfHousekeeperResultSet = checkIfHousekeeperStatement.executeQuery();

        if (checkIfHousekeeperResultSet.next()) {
            int count = checkIfHousekeeperResultSet.getInt(1);
            return count == 1;
        }
        return false;
    }

    private boolean checkIfReceptionist(Connection connection) throws SQLException {
        String checkIfReceptionistQuery = """
               SELECT COUNT(*) FROM receptionist
               JOIN user ON receptionist.r_id = user.u_id
               WHERE user.u_id = ? AND user.u_password = ?
               """;

        PreparedStatement checkIfReceptionistStatement = connection.prepareStatement(checkIfReceptionistQuery);
        checkIfReceptionistStatement.setString(1, username);
        checkIfReceptionistStatement.setString(2, password);

        ResultSet checkIfReceptionistResultSet = checkIfReceptionistStatement.executeQuery();

        if (checkIfReceptionistResultSet.next()) {
            int count = checkIfReceptionistResultSet.getInt(1);
            return count == 1;
        }
        return false;
    }

    private void handleLogin() throws Exception {
        if (username == null || password == null) {
            System.out.println("Username or password not entered!");
            return;
        }

        DataSource dataSource = new DataSource("root", "1234");
        Connection connection = dataSource.getConnection();

        if (checkIfAdmin(connection)) {
            view.getController().setUserType(User.ADMIN);
            return;
        }

        if (checkIfGuest(connection)) {
            view.getController().setUserType(User.GUEST);
            return;
        }

        if (checkIfHousekeeper(connection)) {
            view.getController().setUserType(User.HOUSEKEEPER);
            return;
        }

        if (checkIfReceptionist(connection)) {
            view.getController().setUserType(User.RECEPTIONIST);
            return;
        }

        DataSource.closeConnection(connection);
        throw new RuntimeException("Login failed!");
    }

    private void setViewBasedOnUser() {
        switch (view.getController().getUserType()) {
            case ADMIN -> view.display("ADMINISTRATOR");
            case GUEST -> view.display("GUEST");
            case HOUSEKEEPER -> view.display("HOUSEKEEPER");
            case RECEPTIONIST -> view.display("RECEPTIONIST");
            default -> {}
        }
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
                    try {
                        handleLogin();
                        terminated = true;
                        setViewBasedOnUser();
                    } catch (Exception e) {
                        System.out.println("Login failed!");
                    }
                }
                case 8 -> {
                    terminated = true;
                    username = null;
                    password = null;
                    view.display("LANDING");
                }
                case 9 -> System.exit(0);
            }
        }
    }
}
