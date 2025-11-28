package com.DB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    public static void main(String[] args) {

        String url = "jdbc:mysql://localhost:3306/cinematch_db";
        String user = "root";
        String password = "password";

        try (Connection connection = DriverManager.getConnection(url, user, password)) {

            if (connection != null) {
                System.out.println("Connected to the database successfully!");
            } else {
                System.out.println("Failed to make connection!");
            }

        } catch (SQLException e) {
            System.err.println("Connection error:");
            e.printStackTrace();
        }
    }
}
