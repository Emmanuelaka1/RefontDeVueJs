package com.arkea.sgesapi.dao.model;

/**
 * Critères de recherche de dossiers — objet interne au DAO.
 */
public class RechercheCriteria {
    private String nomEmprunteur;
    private String prenomEmprunteur;
    private String numeroPret;
    private String efs;
    private String structure;
    private String codeEtat;
    private String codeNature;
    private int page;
    private int taille;

    public RechercheCriteria() {
    }

    public RechercheCriteria(String nomEmprunteur, String prenomEmprunteur, String numeroPret,
                             String efs, String structure, String codeEtat, String codeNature,
                             int page, int taille) {
        this.nomEmprunteur = nomEmprunteur;
        this.prenomEmprunteur = prenomEmprunteur;
        this.numeroPret = numeroPret;
        this.efs = efs;
        this.structure = structure;
        this.codeEtat = codeEtat;
        this.codeNature = codeNature;
        this.page = page;
        this.taille = taille;
    }

    public String getNomEmprunteur() {
        return nomEmprunteur;
    }

    public void setNomEmprunteur(String nomEmprunteur) {
        this.nomEmprunteur = nomEmprunteur;
    }

    public String getPrenomEmprunteur() {
        return prenomEmprunteur;
    }

    public void setPrenomEmprunteur(String prenomEmprunteur) {
        this.prenomEmprunteur = prenomEmprunteur;
    }

    public String getNumeroPret() {
        return numeroPret;
    }

    public void setNumeroPret(String numeroPret) {
        this.numeroPret = numeroPret;
    }

    public String getEfs() {
        return efs;
    }

    public void setEfs(String efs) {
        this.efs = efs;
    }

    public String getStructure() {
        return structure;
    }

    public void setStructure(String structure) {
        this.structure = structure;
    }

    public String getCodeEtat() {
        return codeEtat;
    }

    public void setCodeEtat(String codeEtat) {
        this.codeEtat = codeEtat;
    }

    public String getCodeNature() {
        return codeNature;
    }

    public void setCodeNature(String codeNature) {
        this.codeNature = codeNature;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getTaille() {
        return taille;
    }

    public void setTaille(int taille) {
        this.taille = taille;
    }

    // ── Builder ────────────────────────────────────────────────────
    public static RechercheCriteria.Builder builder() {
        return new RechercheCriteria.Builder();
    }

    public static class Builder {
        private String nomEmprunteur;
        private String prenomEmprunteur;
        private String numeroPret;
        private String efs;
        private String structure;
        private String codeEtat;
        private String codeNature;
        private int page;
        private int taille;

        public Builder nomEmprunteur(String nomEmprunteur) {
            this.nomEmprunteur = nomEmprunteur;
            return this;
        }

        public Builder prenomEmprunteur(String prenomEmprunteur) {
            this.prenomEmprunteur = prenomEmprunteur;
            return this;
        }

        public Builder numeroPret(String numeroPret) {
            this.numeroPret = numeroPret;
            return this;
        }

        public Builder efs(String efs) {
            this.efs = efs;
            return this;
        }

        public Builder structure(String structure) {
            this.structure = structure;
            return this;
        }

        public Builder codeEtat(String codeEtat) {
            this.codeEtat = codeEtat;
            return this;
        }

        public Builder codeNature(String codeNature) {
            this.codeNature = codeNature;
            return this;
        }

        public Builder page(int page) {
            this.page = page;
            return this;
        }

        public Builder taille(int taille) {
            this.taille = taille;
            return this;
        }

        public RechercheCriteria build() {
            return new RechercheCriteria(nomEmprunteur, prenomEmprunteur, numeroPret, efs,
                    structure, codeEtat, codeNature, page, taille);
        }
    }

    @Override
    public String toString() {
        return "RechercheCriteria{" +
                "nomEmprunteur='" + nomEmprunteur + '\'' +
                ", prenomEmprunteur='" + prenomEmprunteur + '\'' +
                ", numeroPret='" + numeroPret + '\'' +
                ", efs='" + efs + '\'' +
                ", structure='" + structure + '\'' +
                ", codeEtat='" + codeEtat + '\'' +
                ", codeNature='" + codeNature + '\'' +
                ", page=" + page +
                ", taille=" + taille +
                '}';
    }
}
