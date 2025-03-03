package edu.pidev3A8.tests;

import edu.pidev3A8.entities.*;
import edu.pidev3A8.services.Commandeservice;
import edu.pidev3A8.services.Produitservice;
import edu.pidev3A8.tools.MyConnection;

import java.util.Calendar;
import java.util.Date;

public class Mainclass {
    public static void main(String[] args){
        MyConnection mc =MyConnection.getInstance();
       // Produit p = new Produit("pull", "pull", 12.3, Etat.NEUF, "jshgyfgd", Status.NON_VENDU);

       // Produit p1 = new Produit("pullll","pull",12.2,Etat.NEUF,"jshddgyfgd",Status.NON_VENDU);
        Produitservice ps = new Produitservice();
        //ps.addProduit(p);
        //ps.addProduit(p1);
        // p.setId_produit(16);

        //ps.deleteProduit(p);
        //Produit updatedProduit = new Produit("sweat", "vetement", 15.5, Etat.NEUF, "hhgvvvvv", Status.VENDU);
        // ps.updateProduit(14, updatedProduit);
        // Calendar calendar = Calendar.getInstance();
        //calendar.set(2024, Calendar.DECEMBER, 10);

//Convertir en java.sql.Date (format YYYY-MM-DD compatible MySQL)
        // Date sqlDate = new Date(calendar.getTimeInMillis());

        //Commande c = new Commande(17, 2, 6, 3.6, sqlDate, StatutCommande.ANNULEE);

        //Commandeservice cs = new Commandeservice();
        //cs.addCommande(c);
        // Commande updatecommande = new Commande(16, 2, 5, 7.5, new Date(), StatutCommande.EN_ROUTE);

        //Commandeservice cs = new Commandeservice();

        //cs.updateCommande(12, updatecommande);
        // c.setId_commande(13);
        //cs.deleteCommande(c.getId_commande());


        //System.out.println(ps.getAllData());
    }
}
