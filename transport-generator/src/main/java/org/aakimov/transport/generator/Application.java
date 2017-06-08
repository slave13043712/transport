package org.aakimov.transport.generator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Console application for route data generation.
 *
 * This class uses native java's logger to avoid unnecessary dependencies on third-party libraries
 * in order to be simple and small ;)
 *
 * @author aakimov
 */
public class Application {

    /**
     * Logger instance
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(Application.class.getCanonicalName());

    /**
     * Instantiation of this class is not supported.
     */
    private Application() {
        throw new UnsupportedOperationException("This class should not be instantiated.");
    }

    /**
     * Main entry point of the application.
     *
     * @param args application arguments
     */
    public static void main(String... args) {
        int routeCount = 100000;
        int stopCount = 1000000;
        int stopsPerRoute = 1000;

        LOGGER.info("Possible arguments: <route_count> <stop_count> <stops_per_route>");

        if (args.length >= 3) {
            try {
                routeCount = Math.abs(Integer.parseInt(args[0]));
                stopCount = Math.abs(Integer.parseInt(args[1]));
                stopsPerRoute = Math.abs(Integer.parseInt(args[2]));
            } catch (NumberFormatException exception) {
                LOGGER.warn(
                    "Provided arguments are invalid. Expected 3 positive integers. Falling back to default settings.",
                    exception
                );
            }
        }

        LOGGER.info(
            "Generating route data for {} routes, {} stops and {} stops per route...",
            routeCount,
            stopCount,
            stopsPerRoute
        );

        try {
            RouteDataGenerator routeDataGenerator = new RouteDataGenerator();
            Path filePath = routeDataGenerator.generate(routeCount, stopCount, stopsPerRoute);
            LOGGER.info("Generated file: {}", filePath);
        } catch (IOException exception) {
            LOGGER.error(
                "Unable to generate route data file.",
                exception
            );
            System.exit(1);
        }
    }
}
