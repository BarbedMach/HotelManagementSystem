package org.hms.input.handlers;

import org.hms.View;
import org.hms.database.DataSource;

import java.sql.PreparedStatement;
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
        ds.getConnection();

        String sql = """
                INSERT INTO hotel (h_name, h_zip, h_street, h_building_no, h_phone_no)
                VALUES (?, ?, ?, ?, ?)
                """;

        PreparedStatement preparedStatement = ds.getConnection().prepareStatement(sql);
        preparedStatement.setString(1, hotelName);
        preparedStatement.setString(2, hotelZip);
        preparedStatement.setString(3, hotelStreet);
        preparedStatement.setString(4, hotelBuildingNo);
        preparedStatement.setString(5, hotelNo);

        int rowCount = preparedStatement.executeUpdate();
        if (rowCount <= 0) {
            throw new SQLException("Failed to add hotel");
        }

        System.out.println("Hotel added");
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
                        view.display();
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
