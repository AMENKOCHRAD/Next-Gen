package entities;

public class ticket {
    private int idticket;
    private int idevent;
    private double prix;
    private int quantite;

    public ticket() {
    }

 public ticket(int idticket, int idevent, double prix, int quantite) {
        this.idticket = idticket;
        this.idevent = idevent;
        this.prix = prix;
        this.quantite = quantite;

 }
 public int getIdticket() {
        return idticket;

 }
 public void setIdticket(int idticket) {
        this.idticket = idticket;

 }
 public int getIdevent() {
        return idevent;
 }
 public void setIdevent(int idevent) {
        this.idevent = idevent;

 }
 public double getPrix() {
        return prix;
 }
 public void setPrix(double prix) {
        this.prix = prix;
 }
 public int getQuantite() {
        return quantite;
 }
 public void setQuantite(int quantite) {
        this.quantite = quantite;

 }

    @Override
    public String toString() {
        return "ticket{" +
                "idticket=" + idticket +
                ", idevent=" + idevent +
                ", prix=" + prix +
                ", quantite=" + quantite +
                '}';
    }
}
