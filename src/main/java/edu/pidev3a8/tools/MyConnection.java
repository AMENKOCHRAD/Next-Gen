package edu.pidev3a8.tools;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MyConnection {
    private static MyConnection instance;
    private Connection cnx;

    private MyConnection() {
        try {
            String url = "jdbc:mysql://localhost:3306/sportify";
            String user = "root";
            String password = "";
            cnx = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            System.out.println("Erreur de connexion à la base de données: " + e.getMessage());
        }
    }

    public static MyConnection getInstance() {
        if (instance == null) {
            instance = new MyConnection();
        }
        return instance;
    }

    public Connection getCnx() {
        try {
            if (cnx == null || cnx.isClosed()) {
                String url = "jdbc:mysql://localhost:3306/sportify";
                String user = "root";
                String password = "";
                cnx = DriverManager.getConnection(url, user, password);
            }
        } catch (SQLException e) {
            System.out.println("Erreur de connexion à la base de données: " + e.getMessage());
        }
        return cnx;
    }
}