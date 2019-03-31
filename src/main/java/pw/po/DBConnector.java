package pw.po;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

class DBConnector {

    private static final String dbPath = "jdbc:mysql://localhost:3306/world?serverTimezone=UTC&useSSL=false&allowPublicKeyRetrieval=true";
    private static final String user = "root";
    private static final String password = "admin";

    static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(dbPath, user, password);
    }
}