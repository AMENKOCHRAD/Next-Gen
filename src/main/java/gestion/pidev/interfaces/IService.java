package gestion.pidev.interfaces;

import java.util.List;

public interface IService<T> {
    public void addEntity( T t);
    public void deleteEntity( T t);
    public void updateEntity(int id, T t);
    public List<T> getAllData();
}
