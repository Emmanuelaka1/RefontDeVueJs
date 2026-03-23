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

import { LoginRequest } from "./data-contracts";
import { ContentType, HttpClient, RequestParams } from "./http-client";

export class Authentification<SecurityDataType = unknown> {
  http: HttpClient<SecurityDataType>;

  constructor(http: HttpClient<SecurityDataType>) {
    this.http = http;
  }

  /**
   * @description Retourne un token JWT valide
   *
   * @tags Authentification
   * @name Login
   * @summary Authentification
   * @request POST:/api/v1/auth/login
   * @secure
   */
  login = (data: LoginRequest, params: RequestParams = {}) =>
    this.http.request<Record<string, string>, any>({
      path: `/api/v1/auth/login`,
      method: "POST",
      body: data,
      secure: true,
      type: ContentType.Json,
      ...params,
    });
}
