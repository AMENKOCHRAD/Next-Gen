package gestion.pidev.services;

import gestion.pidev.entities.Cours;
import gestion.pidev.entities.Cours_type;
import gestion.pidev.interfaces.IService;
import gestion.pidev.tools.MyConnection;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Cours_typeService implements IService<Cours_type> {
    @Override
    public void addEntity(Cours_type coursT) {
        try {
            //INSERT INTO `personne`(`id`, `nom`, `prenom`) VALUES ('[value-1]','[value-2]','[value-3]')
            String requete = "INSERT INTO cours_type(id,type_cours, description) VALUES ('" + coursT.getId() + "','" + coursT.getType_cours() + "','" + coursT.getDescription() +"')";


            Statement st = MyConnection.getInstance().getCnx().createStatement();
            st.executeUpdate(requete);
            System.out.println("type de cours ajoutéé");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

    @Override
    public void deleteEntity(Cours_type coursType) {

        try {
            String requete = "DELETE FROM cours_type WHERE id = " + coursType.getId();
            Statement st = MyConnection.getInstance().getCnx().createStatement();
            int rowsAffected = st.executeUpdate(requete);
            if (rowsAffected > 0) {
                System.out.println("type Cours supprimé avec succès !");
            } else {
                System.out.println("Aucun type cours trouvé avec cet ID.");
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la suppression du type de cours : " + e.getMessage());
        }

    }

    @Override
    public void updateEntity(int id, Cours_type coursType) {

        try {
            String requete = "UPDATE cours_type SET type_cours = '" + coursType.getType_cours() +
                    "', description = '" + coursType.getDescription() +

                    "' WHERE id = " + id;
            Statement st = MyConnection.getInstance().getCnx().createStatement();
            int rowsAffected = st.executeUpdate(requete);
            if (rowsAffected > 0) {
                System.out.println("TYPE mis à jour avec succès !");
            } else {
                System.out.println("Aucun TYPE trouvé avec cet ID.");
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la mise à jour du type : " + e.getMessage());
        }

    }

    @Override
    public List<Cours_type> getAllData() {
        List<Cours_type> result = new ArrayList<>();
        try {
            String requete = "SELECT * FROM cours_type";
            Statement st = MyConnection.getInstance().getCnx().createStatement();
            ResultSet rs= st.executeQuery(requete);
            while(rs.next()){
                Cours_type c = new Cours_type();
                c.setId(rs.getInt(1));
                c.setType_cours(rs.getString(2));
                c.setDescription(rs.getString(3));


                result.add(c);
            }
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }

        return result;

    }
}
