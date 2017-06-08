package org.aakimov.transport.rest;

import org.aakimov.transport.api.MappedRouteDataReader;
import org.aakimov.transport.api.MemoryRouteManager;
import org.aakimov.transport.api.RouteManager;
import org.springframework.boot.ApplicationArguments;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Paths;

/**
 * Spring application configuration.
 *
 * @author aakimov
 */
@Configuration
public class AppConfig {

    @Bean
    public RouteManager getRouteManager(
        ApplicationArguments arguments
    ) {
        if (arguments.getNonOptionArgs().isEmpty()) {
            throw new IllegalArgumentException("Route data file is not provided.");
        }

        int[] routeData = new MappedRouteDataReader().read(
            Paths.get(arguments.getNonOptionArgs().get(0))
        );

        return new MemoryRouteManager(
            routeData
        );
    }
}
