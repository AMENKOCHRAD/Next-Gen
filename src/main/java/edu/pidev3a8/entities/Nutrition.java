package edu.pidev3a8.entities;

public class Nutrition {
    private int id_nut;
    private Double poids;
    private Double taille;
    private String sexe;
    private Double imc;
    private int id_utilisateur;

    public Nutrition() {
    }

    public void setId_utilisateur(int id_utilisateur) {
        this.id_utilisateur = id_utilisateur;
    }

    public int getId_utilisateur() {
        return id_utilisateur;
    }

    public void setImc(Double imc) {
        this.imc = imc;
    }

    public Nutrition(int id_nut, Double poids, Double taille, String sexe , int id_utilisateur) {
        this.id_nut = id_nut;
        this.poids = poids;
        this.taille = taille;
        this.sexe = sexe;
        this.imc = calculerIMC(); // Calcul de l'IMC
        this.id_utilisateur =id_utilisateur;
    }

    public Nutrition(Double poids, Double taille, String sexe , int id_utilisateur) {
        this.poids = poids;
        this.taille = taille;
        this.sexe = sexe;
        this.imc = calculerIMC(); // Calcul de l'IMC
        this.id_utilisateur=id_utilisateur;
    }

    public int getId_nut() {
        return id_nut;
    }

    public String getSexe() {
        return sexe;
    }

    public Double getTaille() {
        return taille;
    }

    public Double getPoids() {
        return poids;
    }

    public Double getImc() {
        return imc;
    }

    public void setId_nut(int id_nut) {
        this.id_nut = id_nut;
    }

    public void setSexe(String sexe) {
        this.sexe = sexe;
    }

    public void setPoids(Double poids) {
        this.poids = poids;
        this.imc = calculerIMC(); // Recalculer l'IMC si le poids change
    }

    public void setTaille(Double taille) {
        this.taille = taille;
        this.imc = calculerIMC(); // Recalculer l'IMC si la taille change
    }

    private Double calculerIMC() {
        if (taille == null || taille == 0 || poids == null) {
            return 0.0; // Retourne 0 si la taille ou le poids est invalide
        }
        // Convertir la taille en mètres (car elle est généralement en cm dans la base de données)
        Double tailleEnMetres = taille / 100;
        // Calculer l'IMC
        return poids / (tailleEnMetres * tailleEnMetres);
    }

    @Override
    public String toString() {
        return "Nutrition{" +
                "id_nut=" + id_nut +
                ", poids=" + poids +
                ", taille=" + taille +
                ", sexe='" + sexe + '\'' +
                ", imc=" + imc +
                ",id_utilisateur="+id_utilisateur+
                '}';
    }
}
