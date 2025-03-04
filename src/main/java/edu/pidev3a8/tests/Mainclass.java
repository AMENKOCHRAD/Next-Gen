package edu.pidev3a8.tests;

import edu.pidev3a8.entities.CodeBarre;
import edu.pidev3a8.entities.Nutrition;
import edu.pidev3a8.services.CodeBarreService;
import edu.pidev3a8.services.NutritionService;
import edu.pidev3a8.tools.MyConnection;

class MainClass {
    public static void main(String[] args) {
        MyConnection mc = MyConnection.getInstance();

        // Créer une nouvelle nutrition
        Nutrition n = new Nutrition(70.0, 175.0, "femme"); // IMC sera calculé automatiquement
        NutritionService ns = new NutritionService();

        // Ajouter la nutrition
        ns.addNutrition(n);
        System.out.println("Nutrition ajoutée avec succès ! IMC calculé : " + n.getImc());

        // Supprimer une nutrition
        Nutrition nToDelete = new Nutrition();
        nToDelete.setId_nut(1); // Supprimer la nutrition avec l'ID 1
        ns.deleteNutrition(nToDelete);
        System.out.println("Nutrition supprimée avec succès !");

        // Mettre à jour une nutrition
        Nutrition updatedNutrition = new Nutrition(75.0, 180.0, "homme"); // IMC sera recalculé
        ns.updateNutrition(2, updatedNutrition);
        System.out.println("Nutrition mise à jour avec succès ! Nouvel IMC : " + updatedNutrition.getImc());

        // Afficher toutes les nutritions
        System.out.println("Liste des nutritions : " + ns.getAllData());

        // Créer un nouveau code-barre
        CodeBarre c = new CodeBarre("eau", "test", "marwa", 1);
        CodeBarreService cs = new CodeBarreService();

        // Ajouter le code-barre
        cs.addCodeBarre(c);
        System.out.println("Code-barre ajouté avec succès !");

        // Supprimer un code-barre
        CodeBarre cToDelete = new CodeBarre();
        cToDelete.setId_Code(3); // Supprimer le code-barre avec l'ID 3
        cs.deleteCodeBarre(cToDelete);
        System.out.println("Code-barre supprimé avec succès !");

        // Mettre à jour un code-barre
        CodeBarre updateCodeBarre = new CodeBarre("eau", "nesrine", "marwa", 1);
        cs.updateCodeBarre(4, updateCodeBarre);
        System.out.println("Code-barre mis à jour avec succès !");
    }
}