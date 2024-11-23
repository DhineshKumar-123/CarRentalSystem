package org.carrental.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnUtil {

    private static Connection connection;

    public static Connection getConnection() {
        if (connection == null) {
            try {
                String connectionString = DBPropertyUtil.getPropertyString("db.properties");
                connection = DriverManager.getConnection(connectionString);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return connection;
    }
}
