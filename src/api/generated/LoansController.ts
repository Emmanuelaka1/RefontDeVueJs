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

import { DossierConsultationDto } from "./data-contracts";
import { HttpClient, RequestParams } from "./http-client";

export class LoansController<SecurityDataType = unknown> {
  http: HttpClient<SecurityDataType>;

  constructor(http: HttpClient<SecurityDataType>) {
    this.http = http;
  }

  /**
   * No description
   *
   * @tags loans-controller
   * @name SearchLoans
   * @request GET:/api/v1/loans/{numeroPret}
   * @secure
   */
  searchLoans = (numeroPret: string, params: RequestParams = {}) =>
    this.http.request<DossierConsultationDto, any>({
      path: `/api/v1/loans/${numeroPret}`,
      method: "GET",
      secure: true,
      format: "json",
      ...params,
    });
}
