package edu.pidev3a8.utils;

import edu.pidev3a8.entities.Utilisateur;

public class CurrentUser {
    private static CurrentUser instance;
    private Utilisateur currentUser;

    private CurrentUser() {
        // private constructor to prevent instantiation
    }

    public static CurrentUser getInstance() {
        if (instance == null) {
            instance = new CurrentUser();
        }
        return instance;
    }

    public Utilisateur getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(Utilisateur user) {
        this.currentUser = user;
    }
}