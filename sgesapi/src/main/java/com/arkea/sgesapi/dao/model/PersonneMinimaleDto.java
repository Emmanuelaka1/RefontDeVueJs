package com.arkea.sgesapi.dao.model;

/**
 * DTO pour les informations minimales d'une personne (PP ou PM)
 * retournées par getInformationsMinimalesPersonnes de Topaze.
 */
public class PersonneMinimaleDto {

    private String identifiant;
    private String nom;
    private String prenom;
    private String typePersonne; // "PP" ou "PM"

    public PersonneMinimaleDto() {
    }

    public PersonneMinimaleDto(String identifiant, String nom, String prenom, String typePersonne) {
        this.identifiant = identifiant;
        this.nom = nom;
        this.prenom = prenom;
        this.typePersonne = typePersonne;
    }

    /**
     * Retourne le libellé complet : "NOM Prénom"
     */
    public String getLibelleComplet() {
        if (prenom != null && !prenom.isBlank()) {
            return nom + " " + prenom;
        }
        return nom;
    }

    public String getIdentifiant() {
        return identifiant;
    }

    public void setIdentifiant(String identifiant) {
        this.identifiant = identifiant;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getTypePersonne() {
        return typePersonne;
    }

    public void setTypePersonne(String typePersonne) {
        this.typePersonne = typePersonne;
    }

    @Override
    public String toString() {
        return "PersonneMinimaleDto{" +
                "identifiant='" + identifiant + '\'' +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", typePersonne='" + typePersonne + '\'' +
                '}';
    }
}
