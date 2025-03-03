package edu.pidev3A8.interfaces;

import java.util.List;

public interface ICommande <T>{
    public void addCommande(T t);
    public  void deleteCommande(T t);
    public void updateCommande(int id,T t);
    public List<T> getAllData();
}
