package com.arkea.sgesapi;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.util.List;
import java.util.Map;

/**
 * Classe abstraite pour les tests d'intégration avec RestAssured.
 * <p>
 * Fournit :
 *   - Configuration automatique de RestAssured (port, content-type, filtres de log)
 *   - Méthodes utilitaires : getResource, getError, getListResources, postResource, deleteResource
 * <p>
 * Usage :
 *   Les sous-classes doivent être annotées avec :
 *   {@code @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)}
 *   {@code @ActiveProfiles("dev")}
 */
public abstract class AbstractSpringBootTest {

    protected static RequestSpecification spec;

    @LocalServerPort
    protected int port;

    @BeforeEach
    public void setup() {
        RestAssured.port = port;
        spec = new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setPort(port)
                .addFilter(new ResponseLoggingFilter())
                .build();
    }

    // ── Helpers GET ─────────────────────────────────────────────────

    /**
     * GET avec vérification du code HTTP attendu (crash si différent).
     */
    protected void getError(String subpath, Map<String, String> requestParams, Integer errorCode) {
        RequestSpecification res = RestAssured.given().spec(spec);
        if (requestParams != null) {
            res.queryParams(requestParams);
        }
        res.when().get(subpath).then().statusCode(errorCode);
    }

    /**
     * GET → crash si code HTTP != specified
     */
    protected void getError(String subpath, Integer errorCode) {
        getError(subpath, null, errorCode);
    }

    /**
     * GET une ressource unique avec désérialisation en responseClass.
     */
    protected <T> T getResource(String subpath, Map<String, String> requestParams, Class<T> responseClass) {
        RequestSpecification res = RestAssured.given().spec(spec);
        if (requestParams != null) {
            res.queryParams(requestParams);
        }
        return res.when()
                .get(subpath)
                .then()
                .statusCode(200)
                .extract()
                .as(responseClass);
    }

    /**
     * GET une ressource unique sans queryParams.
     */
    protected <T> T getResource(String subpath, Class<T> responseClass) {
        return getResource(subpath, null, responseClass);
    }

    /**
     * GET une liste de ressources via jsonPath(".").
     */
    protected <T> List<T> getListResources(String subpath, Map<String, String> requestParams, Class<T> responseClass) {
        RequestSpecification res = RestAssured.given().spec(spec);
        if (requestParams != null) {
            res.queryParams(requestParams);
        }
        return res.when()
                .get(subpath)
                .then()
                .statusCode(200)
                .extract()
                .body()
                .jsonPath()
                .getList(".", responseClass);
    }

    /**
     * GET une liste sans queryParams.
     */
    protected <T> List<T> getListResources(String subpath, Class<T> responseClass) {
        return getListResources(subpath, null, responseClass);
    }

    // ── Helpers POST / DELETE ────────────────────────────────────────

    /**
     * POST une ressource.
     */
    protected ValidatableResponse postResource(String path, Object payload) {
        return RestAssured.given()
                .spec(spec)
                .body(payload)
                .when()
                .post(path)
                .then();
    }

    /**
     * DELETE une ressource avec vérification 200.
     */
    protected ValidatableResponse deleteResource(String subpath) {
        return RestAssured.given()
                .spec(spec)
                .when()
                .delete(subpath)
                .then()
                .statusCode(200);
    }
}
