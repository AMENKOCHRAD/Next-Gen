package edu.pidev3a8.tests;

import edu.pidev3a8.entities.Categorie;
import edu.pidev3a8.entities.GmailOAuth2Service;
import edu.pidev3a8.entities.Reclamation;
import edu.pidev3a8.entities.TraitementReclamation;
import edu.pidev3a8.services.EmailService;
import edu.pidev3a8.services.ReclamtionService;
import edu.pidev3a8.services.TraitementService;

import javax.mail.MessagingException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static edu.pidev3a8.entities.FiltrageTexte.filtrerTexte;

public class Main {
    public static void main(String[] args) {
        String sujet = "Problème technique";
        String description = "Le système ne répond pas";
        Categorie categorie = Categorie.TECHNIQUE; // Utilisation de l'enum Categorie
        List<String> piecesJointes = Arrays.asList("fichier1.png", "fichier2.pdf");


        Reclamation re = new Reclamation(sujet, description, categorie, piecesJointes);

        // Ajouter la réclamation à la base de données
        ReclamtionService rs = new ReclamtionService();
        //rs.addEntity(re);
        String texte = "Ceci est un texte avec des mots inappropriés comme fuck et shit.";
        String texteFiltre = filtrerTexte(texte);
        System.out.println("Texte filtré : " + texteFiltre);

        //String email ="hbibbensalem20@gmail.com";
        //String ms  = "reclamation changee";
        //EmailService.sendResetEmail(email,ms);

}}