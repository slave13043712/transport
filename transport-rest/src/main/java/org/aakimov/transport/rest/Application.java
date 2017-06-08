package org.aakimov.transport.rest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring boot application.
 *
 * @author aakimov
 */
@SpringBootApplication
public class Application { //NOSONAR

    /**
     * Main application entry point.
     *
     * @param args application arguments
     */
    public static void main(String... args) {
        SpringApplication.run(Application.class, args);
    }
}
