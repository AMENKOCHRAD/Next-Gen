package edu.pidev3a8.tests;

import edu.pidev3a8.services.CoursService;
import edu.pidev3a8.entities.Cours_type;
import edu.pidev3a8.services.Cours_typeService;

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
