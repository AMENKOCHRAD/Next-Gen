package edu.pidev3a8.interfaces;

import java.util.List;

public interface ITraitement <T>{
    public void addEntityTraitement (T t);
    public void deleteEntityTraitement (T t);
    public void updateEntityTraitement (int id_traitement,T t);
    public List<T> getAllDataTraitement();

}