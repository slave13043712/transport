package org.aakimov.transport.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Naive implementation of the RouteManager that retrieves route data from the file per request.
 *
 * @see RouteManager
 * @author aakimov
 */
@Named
@Singleton
public class FileRouteManager implements RouteManager {

    /**
     * Class logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(FileRouteManager.class);

    /**
     * Route data file name
     */
    private final String fileName;

    /**
     * @param fileName route data file name
     */
    @Inject
    public FileRouteManager(
        String fileName
    ) {
        this.fileName = fileName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isDirectRouteAvailable(
        int departureStopId,
        int arrivalStopId
    ) {
        try (Stream<String> routeDataStream = Files.lines(Paths.get(this.fileName))) {
            Predicate<String> isDepartureAndArrivalStopPresent = new RouteDataPredicate(
                departureStopId,
                arrivalStopId,
                this.fileName
            );

            /*
             * Skip the number of routes.
             * Who cares anyway if this number is incorrect ;)?
             * It is redundant and is probably supposed to be used for consistency check
             * but this check is useless because the route data itself may be still valid
             * (moreover the consistency of the route data line is not guaranteed at all).
             */
            return routeDataStream
                .skip(1)
                .anyMatch(isDepartureAndArrivalStopPresent);
        } catch (IOException exception) {
            FileRouteManager.LOGGER.error(
                "Route data file '{}' is not available or invalid.",
                this.fileName,
                exception
            );
            return false;
        }
    }

    /**
     * Predicate that can be used to check if given string contains departure and arrival stop IDs.
     *
     * This one uses Scanner to scan the line.
     * It could also use a pre-compiled Regex pattern (not the slow String.matches() method ;).
     * But the regex pattern approach is not significantly faster.
     */
    private static class RouteDataPredicate implements Predicate<String> {

        /**
         * Logger instance
         */
        private static final Logger LOGGER = LoggerFactory.getLogger(RouteDataPredicate.class);

        /**
         * Departure stop
         */
        private final int departureStopId;

        /**
         * Arrival stop
         */
        private final int arrivalStopId;

        /**
         * Target file name for logging purposes
         */
        private final String fileName;

        /**
         * @param departureStopId departure stop
         * @param arrivalStopId arrival stop
         * @param fileName target file name
         */
        public RouteDataPredicate(
            int departureStopId,
            int arrivalStopId,
            String fileName
        ) {
            this.departureStopId = departureStopId;
            this.arrivalStopId = arrivalStopId;
            this.fileName = fileName;
        }

        /**
         * Check if given string contains departure and arrival stop IDs.
         *
         * @param routeData route data
         * @return true if route data represents a direct route and false otherwise
         */
        @Override
        public boolean test(String routeData) {
            boolean isDepartureFound = false;
            boolean isArrivalFound = false;
            Scanner routeDataScanner = new Scanner(routeData);
            if (!routeDataScanner.hasNextInt()) {
                // no route ID
                routeDataScanner.close();
                RouteDataPredicate.LOGGER.warn(
                    "Route data file '{}' contains corrupted entries that were skipped.",
                    this.fileName
                );
                return false;
            }
            // skip route ID for now
            routeDataScanner.nextInt();
            while (routeDataScanner.hasNextInt() && (!isDepartureFound || !isArrivalFound)) {
                int currentStopId = routeDataScanner.nextInt();
                if (currentStopId == this.departureStopId) {
                    isDepartureFound = true;
                }
                if (currentStopId == this.arrivalStopId) {
                    isArrivalFound = true;
                }
            }
            routeDataScanner.close();

            return isDepartureFound && isArrivalFound;
        }
    }
}
