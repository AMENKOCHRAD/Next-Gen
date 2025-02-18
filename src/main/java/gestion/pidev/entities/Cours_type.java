package gestion.pidev.entities;

public class Cours_type {

    private String type_cours;
    private String description;
    private int id ;

    public Cours_type() {
    }
    public Cours_type(int id ,String type_cours, String description) {
        this.id=id;
        this.type_cours = type_cours;
        this.description = description;
    }
    public Cours_type(String type_cours, String description) {
        this.type_cours = type_cours;
        this.description = description;
    }

    public String getType_cours() {
        return type_cours;
    }

    public void setType_cours(String type_cours) {
        this.type_cours = type_cours;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Cours_type{" +
                "id='" + id + '\'' +
                "type_cours='" + type_cours + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
