package com.arkea.sgesapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Application principale — sgesapi (SaphirGestion API)
 * Backend Java Spring Boot avec DAO Thrift Topaze et API REST générée OpenAPI.
 */
@SpringBootApplication
public class SgesapiApplication {

    public static void main(String[] args) {
        SpringApplication.run(SgesapiApplication.class, args);
    }
}
