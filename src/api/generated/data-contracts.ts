/* eslint-disable */
/* tslint:disable */
// @ts-nocheck
/*
 * ---------------------------------------------------------------
 * ## THIS FILE WAS GENERATED VIA SWAGGER-TYPESCRIPT-API        ##
 * ##                                                           ##
 * ## AUTHOR: acacode                                           ##
 * ## SOURCE: https://github.com/acacode/swagger-typescript-api ##
 * ---------------------------------------------------------------
 */

export interface LoginRequest {
  username?: string;
  password?: string;
}

export interface DossierConsultationDto {
  noEmprunteur?: string;
  noCoEmprunteur?: string;
  emprunteur?: string;
  coEmprunteur?: string;
  numeroPret?: string;
  numeroContratSouscritProjet?: string;
  numeroContratSouscritPret?: string;
  efs?: string;
  structure?: string;
  codeEtat?: string;
  libelleEtat?: string;
  codeObjet?: string;
  libelleObjet?: string;
  codeNature?: string;
  libelleNature?: string;
  /** @format double */
  montantPret?: number;
  /** @format int32 */
  dureePret?: number;
  /** @format double */
  tauxRemboursement?: number;
  /** @format double */
  tauxFranchise?: number;
  /** @format double */
  tauxBonification?: number;
  anticipation?: boolean;
  typeAmortissement?: string;
  outilInstruction?: string;
  /** @format double */
  montantDebloque?: number;
  /** @format double */
  montantDisponible?: number;
  /** @format double */
  montantRA?: number;
  /** @format double */
  encours?: number;
  /** @format double */
  teg?: number;
}
