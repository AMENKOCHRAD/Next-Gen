package edu.pidev3a8.tools;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MyConnection {
    private String url = "jdbc:mysql://localhost:3306/dbSportify";
    private String login = "root";
    private String pwd = "";
    private Connection cnx;
    private static MyConnection instance;

    // Constructeur privé pour empêcher l'instanciation directe
    private MyConnection() {
        establishConnection();
    }

    // Méthode pour établir une nouvelle connexion
    private void establishConnection() {
        try {
            cnx = DriverManager.getConnection(url, login, pwd);
            System.out.println("Connection established!");
        } catch (SQLException e) {
            System.out.println("Error, connection not established: " + e.getMessage());
        }
    }

    // Méthode pour obtenir la connexion
    public Connection getCnx() {
        try {
            // Vérifie si la connexion est fermée ou invalide
            if (cnx == null || cnx.isClosed() || !cnx.isValid(2)) {
                System.out.println("Connection is closed or invalid. Reconnecting...");
                establishConnection(); // Rétablit la connexion
            }
        } catch (SQLException e) {
            System.out.println("Error while checking connection validity: " + e.getMessage());
            establishConnection(); // Rétablit la connexion en cas d'erreur
        }
        return cnx;
    }

    // Méthode pour obtenir l'instance singleton
    public static MyConnection getInstance() {
        if (instance == null) {
            instance = new MyConnection();
        }
        return instance;
    }
}