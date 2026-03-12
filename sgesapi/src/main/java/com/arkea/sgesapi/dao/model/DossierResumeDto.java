package com.arkea.sgesapi.dao.model;

/**
 * DTO résumé d'un dossier — affiché dans les résultats de recherche.
 */
public class DossierResumeDto {
    private String numeroPret;
    private String noEmprunteur;
    private String noCoEmprunteur;
    private String emprunteur;
    private String coEmprunteur;
    private String efs;
    private String structure;
    private String codeEtat;
    private String libelleEtat;
    private String codeNature;
    private String libelleNature;
    private Double montantPret;
    private Double tauxRemboursement;

    public DossierResumeDto() {
    }

    public DossierResumeDto(String numeroPret, String noEmprunteur, String noCoEmprunteur,
                            String emprunteur, String coEmprunteur,
                            String efs, String structure, String codeEtat, String libelleEtat,
                            String codeNature, String libelleNature, Double montantPret,
                            Double tauxRemboursement) {
        this.numeroPret = numeroPret;
        this.noEmprunteur = noEmprunteur;
        this.noCoEmprunteur = noCoEmprunteur;
        this.emprunteur = emprunteur;
        this.coEmprunteur = coEmprunteur;
        this.efs = efs;
        this.structure = structure;
        this.codeEtat = codeEtat;
        this.libelleEtat = libelleEtat;
        this.codeNature = codeNature;
        this.libelleNature = libelleNature;
        this.montantPret = montantPret;
        this.tauxRemboursement = tauxRemboursement;
    }

    public String getNumeroPret() {
        return numeroPret;
    }

    public void setNumeroPret(String numeroPret) {
        this.numeroPret = numeroPret;
    }

    public String getNoEmprunteur() {
        return noEmprunteur;
    }

    public void setNoEmprunteur(String noEmprunteur) {
        this.noEmprunteur = noEmprunteur;
    }

    public String getNoCoEmprunteur() {
        return noCoEmprunteur;
    }

    public void setNoCoEmprunteur(String noCoEmprunteur) {
        this.noCoEmprunteur = noCoEmprunteur;
    }

    public String getEmprunteur() {
        return emprunteur;
    }

    public void setEmprunteur(String emprunteur) {
        this.emprunteur = emprunteur;
    }

    public String getCoEmprunteur() {
        return coEmprunteur;
    }

    public void setCoEmprunteur(String coEmprunteur) {
        this.coEmprunteur = coEmprunteur;
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

    public String getLibelleEtat() {
        return libelleEtat;
    }

    public void setLibelleEtat(String libelleEtat) {
        this.libelleEtat = libelleEtat;
    }

    public String getCodeNature() {
        return codeNature;
    }

    public void setCodeNature(String codeNature) {
        this.codeNature = codeNature;
    }

    public String getLibelleNature() {
        return libelleNature;
    }

    public void setLibelleNature(String libelleNature) {
        this.libelleNature = libelleNature;
    }

    public Double getMontantPret() {
        return montantPret;
    }

    public void setMontantPret(Double montantPret) {
        this.montantPret = montantPret;
    }

    public Double getTauxRemboursement() {
        return tauxRemboursement;
    }

    public void setTauxRemboursement(Double tauxRemboursement) {
        this.tauxRemboursement = tauxRemboursement;
    }

    // ── Builder ────────────────────────────────────────────────────
    public static DossierResumeDto.Builder builder() {
        return new DossierResumeDto.Builder();
    }

    public static class Builder {
        private String numeroPret;
        private String noEmprunteur;
        private String noCoEmprunteur;
        private String emprunteur;
        private String coEmprunteur;
        private String efs;
        private String structure;
        private String codeEtat;
        private String libelleEtat;
        private String codeNature;
        private String libelleNature;
        private Double montantPret;
        private Double tauxRemboursement;

        public Builder numeroPret(String numeroPret) {
            this.numeroPret = numeroPret;
            return this;
        }

        public Builder noEmprunteur(String noEmprunteur) {
            this.noEmprunteur = noEmprunteur;
            return this;
        }

        public Builder noCoEmprunteur(String noCoEmprunteur) {
            this.noCoEmprunteur = noCoEmprunteur;
            return this;
        }

        public Builder emprunteur(String emprunteur) {
            this.emprunteur = emprunteur;
            return this;
        }

        public Builder coEmprunteur(String coEmprunteur) {
            this.coEmprunteur = coEmprunteur;
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

        public Builder libelleEtat(String libelleEtat) {
            this.libelleEtat = libelleEtat;
            return this;
        }

        public Builder codeNature(String codeNature) {
            this.codeNature = codeNature;
            return this;
        }

        public Builder libelleNature(String libelleNature) {
            this.libelleNature = libelleNature;
            return this;
        }

        public Builder montantPret(Double montantPret) {
            this.montantPret = montantPret;
            return this;
        }

        public Builder tauxRemboursement(Double tauxRemboursement) {
            this.tauxRemboursement = tauxRemboursement;
            return this;
        }

        public DossierResumeDto build() {
            return new DossierResumeDto(numeroPret, noEmprunteur, noCoEmprunteur,
                    emprunteur, coEmprunteur, efs, structure,
                    codeEtat, libelleEtat, codeNature, libelleNature, montantPret,
                    tauxRemboursement);
        }
    }
}
