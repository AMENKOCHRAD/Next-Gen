package edu.pidev3A8.interfaces;

import java.util.List;

public interface IProduit <T>{
    public void addProduit(T t);
    public  void deleteProduit(T t);
    public void updateProduit(int id,T t);
    public List<T> getAllData();

}
