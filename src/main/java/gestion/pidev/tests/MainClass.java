package gestion.pidev.tests;

import gestion.pidev.entities.Cours;
import gestion.pidev.entities.Cours_type;
import gestion.pidev.services.CoursService;
import gestion.pidev.services.Cours_typeService;
import gestion.pidev.tools.MyConnection;

public class MainClass {

    public static void main(String[] args) {

       // Cours c = new Cours("arts_martiaux","lutte","13/06/25","coach@gmail.com");
      //  Cours c1 = new Cours(11,"arts_martiaux","mma","13/06/25","entraineur@gmail.com");
       // Cours c2 = new Cours("zumba","danse_bresillienne","25/06/25","danseuse@gmail.com");
        Cours_type ct = new Cours_type("b"," b ");
        CoursService cs = new CoursService();
        Cours_typeService cst = new Cours_typeService();
        //cst.addEntity(ct);
        //cst.deleteEntity(ct);
        //cst.updateEntity(15,ct);
        System.out.println(cst.getAllData());
      // cs.addEntity(c2);
       System.out.println(cs.getAllData());
        //cs.deleteEntity(c1);
       // cs.updateEntity(9,c2);


    }
}
