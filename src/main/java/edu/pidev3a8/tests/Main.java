package edu.pidev3a8.tests;

import edu.pidev3a8.entities.Categorie;
import edu.pidev3a8.entities.Reclamation;
import edu.pidev3a8.entities.TraitementReclamation;
import edu.pidev3a8.services.ReclamtionService;
import edu.pidev3a8.services.TraitementService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

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


        int idReclamationAModifier = 9; // ID de la réclamation à mettre à jour (à adapter selon votre base de données)
        String nouveauSujet = "ye rabi";
        String nouvelleDescription = "Le système fonctionne correctement";
        Categorie nouvelleCategorie = Categorie.SERVICE_CLIENT; // Nouvelle catégorie
        List<String> nouvellesPiecesJointes = Arrays.asList("fichier3.jpg"); // Nouvelles pièces jointes

        // Créer un objet Reclamation avec les nouvelles valeurs
        Reclamation reclamationModifiee = new Reclamation(
                idReclamationAModifier, // ID de la réclamation à mettre à jour
                nouveauSujet,
                nouvelleDescription,
                "RESOLUE", // Nouveau statut
                null, // La date sera mise à jour automatiquement dans la méthode updateEntity
                1, // ID de l'utilisateur (à adapter selon votre logique)
                nouvelleCategorie,
                nouvellesPiecesJointes
        );

        // Mettre à jour la réclamation dans la base de données
        rs.updateEntity(idReclamationAModifier, reclamationModifiee);
        //System.out.println(rs.getAllData());


        Reclamation reclamationToDelete = new Reclamation();
        //reclamationToDelete.setId(20); // ID de la réclamation à supprimer

// Supprimer la réclamation
        rs.deleteEntity(reclamationToDelete);




        TraitementService traitementService = new TraitementService();

// Ajouter un traitement
        TraitementReclamation traitement = new TraitementReclamation();
        traitement.setReclamationId(9);
        traitement.setAdminId(8);
        traitement.setDatePriseEnCharge(LocalDateTime.now());
        traitement.setStatut(TraitementReclamation.StatutTraitement.EN_COURS);
        traitement.setCommentaire("Réclamation en cours de traitement.");
        traitementService.addEntityTraitement(traitement);

        // Mettre à jour un traitement

        traitement.setStatut(TraitementReclamation.StatutTraitement.RESOLU);
        traitement.setCommentaire("Réclamation résolue.");
        traitementService.updateEntityTraitement(4, traitement);


       // traitement.setId(5); // ID de la réclamation à supprimer
        //traitementService.deleteEntityTraitement(traitement);

        // Récupérer tous les traitements
        List<TraitementReclamation> traitements = traitementService.getAllDataTraitement();
        for (TraitementReclamation t : traitements) {
           // System.out.println(t);
        }














    }
}
