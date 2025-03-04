package edu.pidev3a8.entities;

import java.time.LocalDateTime;

public class TraitementReclamation {
    public enum StatutTraitement {
        EN_COURS,
        RESOLU,
        REJETE
    }

    // Ajouter l'énumération Priorite
    public enum Priorite {
        HAUTE,
        MOYENNE,
        BASSE
    }

    private int id_traitement;
    private int reclamationId; // Clé étrangère vers la table Reclamation
    private int adminId; // Clé étrangère vers la table User (Admin ou Coach)
    private LocalDateTime datePriseEnCharge;
    private LocalDateTime dateResolution; // Ajouté
    private StatutTraitement statut; // Utilisation d'un enum
    private String commentaire;
    private Priorite priorite; // Modifier le type de priorite pour utiliser l'enum
    private String typeTraitement; // Ajouté

    // Constructeurs
    public TraitementReclamation() {
    }

    public TraitementReclamation(int id_traitement, int reclamationId, int adminId, LocalDateTime datePriseEnCharge, LocalDateTime dateResolution, StatutTraitement statut, String commentaire, Priorite priorite, String typeTraitement) {
        this.id_traitement = id_traitement;
        this.reclamationId = reclamationId;
        this.adminId = adminId;
        this.datePriseEnCharge = datePriseEnCharge;
        this.dateResolution = dateResolution;
        this.statut = statut;
        this.commentaire = commentaire;
        this.priorite = priorite;
        this.typeTraitement = typeTraitement;
    }

    public TraitementReclamation(int reclamationId, String typeTraitement, Priorite priorite, String commentaire, StatutTraitement statut, LocalDateTime dateResolution, LocalDateTime datePriseEnCharge, int adminId) {
        this.reclamationId = reclamationId;
        this.typeTraitement = typeTraitement;
        this.priorite = priorite;
        this.commentaire = commentaire;
        this.statut = statut;
        this.dateResolution = dateResolution;
        this.datePriseEnCharge = datePriseEnCharge;
        this.adminId = adminId;
    }

    // Getters et Setters
    public int getId_traitement() {
        return id_traitement;
    }

    public void setId_traitement(int id_traitement) {
        this.id_traitement = id_traitement;
    }

    public int getId() {
        return id_traitement;
    }

    public int getReclamationId() {
        return reclamationId;
    }

    public int getAdminId() {
        return adminId;
    }

    public LocalDateTime getDatePriseEnCharge() {
        return datePriseEnCharge;
    }

    public LocalDateTime getDateResolution() {
        return dateResolution;
    }

    public StatutTraitement getStatut() {
        return statut;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public Priorite getPriorite() {
        return priorite;
    }

    public String getTypeTraitement() {
        return typeTraitement;
    }

    public void setId(int id_traitement) {
        this.id_traitement = id_traitement;
    }

    public void setReclamationId(int reclamationId) {
        this.reclamationId = reclamationId;
    }

    public void setAdminId(int adminId) {
        this.adminId = adminId;
    }

    public void setDatePriseEnCharge(LocalDateTime datePriseEnCharge) {
        this.datePriseEnCharge = datePriseEnCharge;
    }

    public void setDateResolution(LocalDateTime dateResolution) {
        this.dateResolution = dateResolution;
    }

    public void setStatut(StatutTraitement statut) {
        this.statut = statut;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }

    public void setPriorite(Priorite priorite) {
        this.priorite = priorite;
    }

    public void setTypeTraitement(String typeTraitement) {
        this.typeTraitement = typeTraitement;
    }

    @Override
    public String toString() {
        return "TraitementReclamation{" +
                "id=" + id_traitement +
                ", reclamationId=" + reclamationId +
                ", adminId=" + adminId +
                ", datePriseEnCharge=" + datePriseEnCharge +
                ", dateResolution=" + dateResolution +
                ", statut=" + statut +
                ", commentaire='" + commentaire + '\'' +
                ", priorite=" + priorite +
                ", typeTraitement='" + typeTraitement + '\'' +
                '}';
    }
}