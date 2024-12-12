package org.hms.database;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {

    private static String readFile(String fileName) throws RuntimeException {
        StringBuilder fileContent = new StringBuilder();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(fileName));

            String line;
            while ((line = reader.readLine()) != null) {
                fileContent.append(line);
            }

            reader.close();
            return fileContent.toString();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void executeSQL(Connection connection, String sql) throws SQLException {
        Statement statement = connection.createStatement();

        String[] statements = sql.split(";");
        for (String stmt : statements) {
            statement.execute(stmt);
        }
    }

    public static void initializeDatabase() throws RuntimeException {
        DataSource rootDb = new DataSource("root", "1234");
        try {
            Connection connection = rootDb.getConnection();
            String sql = readFile("resources/DDL.sql");
            executeSQL(connection, sql);
            DataSource.closeConnection(connection);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
