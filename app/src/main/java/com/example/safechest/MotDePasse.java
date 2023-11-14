package com.example.safechest;

public class MotDePasse {
    private String nom;
    private String motdepasse;
    private int idmotdepasse;

    public MotDePasse(String name , String password, int idmotdepasse){
        this.nom = name;
        this.motdepasse = password;
        this.idmotdepasse = idmotdepasse;
    }



    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getMotdepasse() {
        return motdepasse;
    }

    public int getIdmotdepasse() {
        return idmotdepasse;
    }

    public void setIdmotdepasse(int idutilisateur) {
        this.idmotdepasse = idutilisateur;
    }

    public void setMotdepasse(String motdepasse) {this.motdepasse = motdepasse;}
}
