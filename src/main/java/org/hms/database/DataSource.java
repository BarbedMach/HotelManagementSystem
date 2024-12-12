package org.hms.database;

import com.mysql.cj.jdbc.MysqlDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class DataSource {
    private MysqlDataSource dataSource;

    public DataSource(String username, String password) {
        try {
            dataSource = new MysqlDataSource();
            dataSource.setUrl("jdbc:mysql://localhost:3306/hotel_management_system");
            dataSource.setUser(username);
            dataSource.setPassword(password);
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize MySQL datasource", e);
        }
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public static void closeConnection(Connection connection) throws RuntimeException {
        try {
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to close connection", e);
        }
    }
}
