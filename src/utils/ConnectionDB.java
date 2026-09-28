package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionDB {
    private static String DRIVER = "org.postgresql.Driver";
    private static String URL = "jdbc:postgresql://localhost:5432/store_product" ;
    private static String USERNAME = "postgres" ;
    private static String PASSWORD = "12345678" ;

    public static Connection getConnection() {
        try {
            Class.forName(DRIVER);

            Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            return conn;
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
