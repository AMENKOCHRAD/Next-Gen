package Utilisateur.Pidev.Interfaces;

import Utilisateur.Pidev.Entites.Utilisateur;

import java.util.List;


public interface IService<T>
{
    public void addEntity(T t);
    public void deleteEntity(T t);
    public void updateEntity(int id,T t);
    public List<T> getAllData();
    public List<T> getAllData2();
    Utilisateur authenticateUser(String email, String password);
}
