package edu.pidev3a8.entities;

public class CodeBarre {
    private int id_Code;
    private String nom_produit;
    private String ingredients;
    private String marque;
    private int fk_utilisateur;

    public CodeBarre() {
    }

    public CodeBarre(int id_Code, String nom_produit, String ingredients, String marque, int fk_utilisateur) {
        this.id_Code = id_Code;
        this.nom_produit = nom_produit;
        this.ingredients = ingredients;
        this.marque = marque;
        this.fk_utilisateur = fk_utilisateur;
    }

    public CodeBarre(String nom_produit, String ingredients, String marque, int fk_utilisateur) {
        this.nom_produit = nom_produit;
        this.ingredients = ingredients;
        this.marque = marque;
        this.fk_utilisateur = fk_utilisateur;
    }

    public int getId_Code() { return id_Code; }
    public void setId_Code(int id_Code) { this.id_Code = id_Code; }

    public String getNom_produit() { return nom_produit; }
    public void setNom_produit(String nom_produit) { this.nom_produit = nom_produit; }

    public String getIngredients() { return ingredients; }
    public void setIngredients(String ingredients) { this.ingredients = ingredients; }

    public String getMarque() { return marque; }
    public void setMarque(String marque) { this.marque = marque; }

    public int getFk_utilisateur() { return fk_utilisateur; }
    public void setFk_utilisateur(int fk_utilisateur) { this.fk_utilisateur = fk_utilisateur; }

    @Override
    public String toString() {
        return "CodeBarre{" +
                "id_Code=" + id_Code +
                ", nom_produit='" + nom_produit + '\'' +
                ", ingredients='" + ingredients + '\'' +
                ", marque='" + marque + '\'' +
                ", fk_utilisateur=" + fk_utilisateur +
                '}';
    }
}