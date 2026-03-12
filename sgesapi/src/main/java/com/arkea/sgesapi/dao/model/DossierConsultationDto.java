package com.arkea.sgesapi.dao.model;

/**
 * DTO complet d'un dossier — affiché dans l'écran de consultation.
 * Correspond aux données générales + données prêt de la capture d'écran.
 */
public class DossierConsultationDto {

    // ── Identifiants personnes Topaze ──────────────────────────────
    private String noEmprunteur;
    private String noCoEmprunteur;

    // ── Données Générales (noms résolus via getInformationsMinimalesPersonnes)
    private String emprunteur;
    private String coEmprunteur;
    private String numeroPret;
    private String numeroContratSouscritProjet;
    private String numeroContratSouscritPret;
    private String efs;
    private String structure;
    private String codeEtat;
    private String libelleEtat;
    private String codeObjet;
    private String libelleObjet;
    private String codeNature;
    private String libelleNature;

    // ── Données Prêt ──────────────────────────────────────────────
    private Double montantPret;
    private Integer dureePret;          // en mois
    private Double tauxRemboursement;
    private Double tauxFranchise;
    private Double tauxBonification;
    private Boolean anticipation;
    private String typeAmortissement;
    private String outilInstruction;
    private Double montantDebloque;
    private Double montantDisponible;
    private Double montantRA;
    private Double encours;
    private Double teg;

    public DossierConsultationDto() {
    }

    public DossierConsultationDto(String noEmprunteur, String noCoEmprunteur,
                                   String emprunteur, String coEmprunteur, String numeroPret,
                                   String numeroContratSouscritProjet, String numeroContratSouscritPret,
                                   String efs, String structure, String codeEtat, String libelleEtat,
                                   String codeObjet, String libelleObjet, String codeNature,
                                   String libelleNature, Double montantPret, Integer dureePret,
                                   Double tauxRemboursement, Double tauxFranchise,
                                   Double tauxBonification, Boolean anticipation,
                                   String typeAmortissement, String outilInstruction,
                                   Double montantDebloque, Double montantDisponible,
                                   Double montantRA, Double encours, Double teg) {
        this.noEmprunteur = noEmprunteur;
        this.noCoEmprunteur = noCoEmprunteur;
        this.emprunteur = emprunteur;
        this.coEmprunteur = coEmprunteur;
        this.numeroPret = numeroPret;
        this.numeroContratSouscritProjet = numeroContratSouscritProjet;
        this.numeroContratSouscritPret = numeroContratSouscritPret;
        this.efs = efs;
        this.structure = structure;
        this.codeEtat = codeEtat;
        this.libelleEtat = libelleEtat;
        this.codeObjet = codeObjet;
        this.libelleObjet = libelleObjet;
        this.codeNature = codeNature;
        this.libelleNature = libelleNature;
        this.montantPret = montantPret;
        this.dureePret = dureePret;
        this.tauxRemboursement = tauxRemboursement;
        this.tauxFranchise = tauxFranchise;
        this.tauxBonification = tauxBonification;
        this.anticipation = anticipation;
        this.typeAmortissement = typeAmortissement;
        this.outilInstruction = outilInstruction;
        this.montantDebloque = montantDebloque;
        this.montantDisponible = montantDisponible;
        this.montantRA = montantRA;
        this.encours = encours;
        this.teg = teg;
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

    public String getNumeroPret() {
        return numeroPret;
    }

    public void setNumeroPret(String numeroPret) {
        this.numeroPret = numeroPret;
    }

    public String getNumeroContratSouscritProjet() {
        return numeroContratSouscritProjet;
    }

    public void setNumeroContratSouscritProjet(String numeroContratSouscritProjet) {
        this.numeroContratSouscritProjet = numeroContratSouscritProjet;
    }

    public String getNumeroContratSouscritPret() {
        return numeroContratSouscritPret;
    }

    public void setNumeroContratSouscritPret(String numeroContratSouscritPret) {
        this.numeroContratSouscritPret = numeroContratSouscritPret;
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

    public String getCodeObjet() {
        return codeObjet;
    }

    public void setCodeObjet(String codeObjet) {
        this.codeObjet = codeObjet;
    }

    public String getLibelleObjet() {
        return libelleObjet;
    }

    public void setLibelleObjet(String libelleObjet) {
        this.libelleObjet = libelleObjet;
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

    public Integer getDureePret() {
        return dureePret;
    }

    public void setDureePret(Integer dureePret) {
        this.dureePret = dureePret;
    }

    public Double getTauxRemboursement() {
        return tauxRemboursement;
    }

    public void setTauxRemboursement(Double tauxRemboursement) {
        this.tauxRemboursement = tauxRemboursement;
    }

    public Double getTauxFranchise() {
        return tauxFranchise;
    }

    public void setTauxFranchise(Double tauxFranchise) {
        this.tauxFranchise = tauxFranchise;
    }

    public Double getTauxBonification() {
        return tauxBonification;
    }

    public void setTauxBonification(Double tauxBonification) {
        this.tauxBonification = tauxBonification;
    }

    public Boolean getAnticipation() {
        return anticipation;
    }

    public void setAnticipation(Boolean anticipation) {
        this.anticipation = anticipation;
    }

    public String getTypeAmortissement() {
        return typeAmortissement;
    }

    public void setTypeAmortissement(String typeAmortissement) {
        this.typeAmortissement = typeAmortissement;
    }

    public String getOutilInstruction() {
        return outilInstruction;
    }

    public void setOutilInstruction(String outilInstruction) {
        this.outilInstruction = outilInstruction;
    }

    public Double getMontantDebloque() {
        return montantDebloque;
    }

    public void setMontantDebloque(Double montantDebloque) {
        this.montantDebloque = montantDebloque;
    }

    public Double getMontantDisponible() {
        return montantDisponible;
    }

    public void setMontantDisponible(Double montantDisponible) {
        this.montantDisponible = montantDisponible;
    }

    public Double getMontantRA() {
        return montantRA;
    }

    public void setMontantRA(Double montantRA) {
        this.montantRA = montantRA;
    }

    public Double getEncours() {
        return encours;
    }

    public void setEncours(Double encours) {
        this.encours = encours;
    }

    public Double getTeg() {
        return teg;
    }

    public void setTeg(Double teg) {
        this.teg = teg;
    }

    // ── Builder ────────────────────────────────────────────────────
    public static DossierConsultationDto.Builder builder() {
        return new DossierConsultationDto.Builder();
    }

    public static class Builder {
        private String noEmprunteur;
        private String noCoEmprunteur;
        private String emprunteur;
        private String coEmprunteur;
        private String numeroPret;
        private String numeroContratSouscritProjet;
        private String numeroContratSouscritPret;
        private String efs;
        private String structure;
        private String codeEtat;
        private String libelleEtat;
        private String codeObjet;
        private String libelleObjet;
        private String codeNature;
        private String libelleNature;
        private Double montantPret;
        private Integer dureePret;
        private Double tauxRemboursement;
        private Double tauxFranchise;
        private Double tauxBonification;
        private Boolean anticipation;
        private String typeAmortissement;
        private String outilInstruction;
        private Double montantDebloque;
        private Double montantDisponible;
        private Double montantRA;
        private Double encours;
        private Double teg;

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

        public Builder numeroPret(String numeroPret) {
            this.numeroPret = numeroPret;
            return this;
        }

        public Builder numeroContratSouscritProjet(String numeroContratSouscritProjet) {
            this.numeroContratSouscritProjet = numeroContratSouscritProjet;
            return this;
        }

        public Builder numeroContratSouscritPret(String numeroContratSouscritPret) {
            this.numeroContratSouscritPret = numeroContratSouscritPret;
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

        public Builder codeObjet(String codeObjet) {
            this.codeObjet = codeObjet;
            return this;
        }

        public Builder libelleObjet(String libelleObjet) {
            this.libelleObjet = libelleObjet;
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

        public Builder dureePret(Integer dureePret) {
            this.dureePret = dureePret;
            return this;
        }

        public Builder tauxRemboursement(Double tauxRemboursement) {
            this.tauxRemboursement = tauxRemboursement;
            return this;
        }

        public Builder tauxFranchise(Double tauxFranchise) {
            this.tauxFranchise = tauxFranchise;
            return this;
        }

        public Builder tauxBonification(Double tauxBonification) {
            this.tauxBonification = tauxBonification;
            return this;
        }

        public Builder anticipation(Boolean anticipation) {
            this.anticipation = anticipation;
            return this;
        }

        public Builder typeAmortissement(String typeAmortissement) {
            this.typeAmortissement = typeAmortissement;
            return this;
        }

        public Builder outilInstruction(String outilInstruction) {
            this.outilInstruction = outilInstruction;
            return this;
        }

        public Builder montantDebloque(Double montantDebloque) {
            this.montantDebloque = montantDebloque;
            return this;
        }

        public Builder montantDisponible(Double montantDisponible) {
            this.montantDisponible = montantDisponible;
            return this;
        }

        public Builder montantRA(Double montantRA) {
            this.montantRA = montantRA;
            return this;
        }

        public Builder encours(Double encours) {
            this.encours = encours;
            return this;
        }

        public Builder teg(Double teg) {
            this.teg = teg;
            return this;
        }

        public DossierConsultationDto build() {
            return new DossierConsultationDto(noEmprunteur, noCoEmprunteur,
                    emprunteur, coEmprunteur, numeroPret,
                    numeroContratSouscritProjet, numeroContratSouscritPret, efs, structure,
                    codeEtat, libelleEtat, codeObjet, libelleObjet, codeNature, libelleNature,
                    montantPret, dureePret, tauxRemboursement, tauxFranchise, tauxBonification,
                    anticipation, typeAmortissement, outilInstruction, montantDebloque,
                    montantDisponible, montantRA, encours, teg);
        }
    }
}
