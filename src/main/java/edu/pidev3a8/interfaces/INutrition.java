package edu.pidev3a8.interfaces;

import java.util.List;

public interface INutrition <T>{
    public void addNutrition (T t);
    public void deleteNutrition(T t);
    public void updateNutrition(int id_nut , T t);
    public List<T> getAllData();
}
