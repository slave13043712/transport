package org.aakimov.transport.generator;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.LinkedHashSet;
import java.util.Random;
import java.util.Set;

/**
 * Route data generator.
 *
 * Can be used to generate route data file of any size.
 *
 * @author aakimov
 */
public class RouteDataGenerator {

    /**
     * Random instance to generate stop IDs
     */
    private final Random random;

    public RouteDataGenerator() {
        this.random = new Random();
    }

    /**
     * Generate temporary route data file.
     *
     * @param routeCount number of routes
     * @param stopCount number of stops
     * @param stopsPerRoute maximum number of stops in one route
     * @return generated file path
     * @throws IOException if file cannot be created
     */
    public Path generate(int routeCount, int stopCount, int stopsPerRoute) throws IOException {
        Path filePath = Files.createTempFile("routes", ".tmp");
        try(
            BufferedWriter writer = Files.newBufferedWriter(filePath, StandardCharsets.UTF_8, StandardOpenOption.WRITE)
        ) {
            writer.write(String.valueOf(routeCount));
            writer.newLine();
            for (int routeId = 0; routeId < routeCount; routeId++) {
                writer.write(String.valueOf(routeId));
                for (Integer stopId : this.generateStopsForRoute(stopCount, stopsPerRoute)) {
                    writer.write(" ");
                    writer.write(stopId.toString());
                }
                writer.newLine();
            }
        }

        return filePath;
    }

    /**
     * Generate set of stops.
     *
     * @param stopCount number of stops
     * @param stopsPerRoute maximum number of stops in one route
     * @return set of stop IDs
     */
    private Set<Integer> generateStopsForRoute(int stopCount, int stopsPerRoute) {
        Set<Integer> stops = new LinkedHashSet<>();
        for (int index = 0; index < stopsPerRoute; index++) {
            stops.add(this.random.nextInt(stopCount));
        }

        return stops;
    }

}
