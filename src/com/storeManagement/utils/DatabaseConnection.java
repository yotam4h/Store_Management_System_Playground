package com.storeManagement.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    // Singleton instance
    static Connection con = null;

    static {
        @SuppressWarnings("SpellCheckingInspection") String url = "jdbc:mysql://ifa3z.h.filess.io:3307/StoreManagementSystem_statement";
        String user = "StoreManagementSystem_statement";
        String pass = "9d05a4dffc9ff707e2c31864cb098c6eb23782a0";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(url, user, pass);
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Error connecting to database: " + e.getMessage());
        }

    }

    public static Connection getConnection() throws IllegalStateException {
        if (con == null)
            throw new IllegalStateException("Connection not available");
        return con;
    }


}
