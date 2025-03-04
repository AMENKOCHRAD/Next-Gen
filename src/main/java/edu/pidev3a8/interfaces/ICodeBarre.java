package edu.pidev3a8.interfaces;

import java.util.List;

public interface ICodeBarre <T> {
    void addCodeBarre(T t);
    void deleteCodeBarre(T t);
    void updateCodeBarre(int id_Code, T t);
    List<T> getAllData();
}
