package Utilisateur.Pidev.Tests;

import Utilisateur.Pidev.Entites.Utilisateur;
import Utilisateur.Pidev.Services.UtilisateurService;
import Utilisateur.Pidev.Tools.MyConnection;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class mainClass {
    public static void main(String[] args) {
        MyConnection mc = MyConnection.getInstance();

//        Utilisateur u = new Utilisateur("admin6@gmail.com", "mdpadmin6"
//                , "boumiza", "badissadmin6", 92735731, "Homme", Utilisateur.Role.Admin
//                , "im.png");

      //Affichage

//        UtilisateurService us = new UtilisateurService();
//        us.addEntity(u);

        //List<Utilisateur> utilisateurs = us.getAllData2();

        // Print the retrieved data
        /* if (utilisateurs.isEmpty()) {
            System.out.println("No users found in the database.");
        } else {
            System.out.println("List of all users:");
            for (Utilisateur k : utilisateurs) {
                System.out.println(k); // Ensure Utilisateur has a toString() method for proper output
            }
        } */
    }

    }







        //Delete
        /*Utilisateur utilisateur = new Utilisateur();
        utilisateur.setId(4); // Set the ID of the user you want to delete

        // Create an instance of the service class
        UtilisateurService ps = new UtilisateurService();

        // Call the deleteEntity method to delete the user
        ps.deleteEntity(utilisateur);

        // Optional: Print all data to verify the deletion
        System.out.println(ps.getAllData());/*
         */

    //Modifier
    /* UtilisateurService us = new UtilisateurService();
        us.updateEntity(4, u);
        System.out.println(us.getAllData());  */















