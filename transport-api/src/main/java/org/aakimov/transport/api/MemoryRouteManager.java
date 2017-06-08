package org.aakimov.transport.api;

import javax.inject.Named;
import javax.inject.Singleton;
import java.util.Arrays;

/**
 * Route data manager that uses in-memory route data to provide route information.
 *
 * @author aakimov
 */
@Named
@Singleton
public class MemoryRouteManager implements RouteManager {

    /**
     * Route data with sorted stop IDs segments
     */
    private final int[] routeData;

    /**
     * @param routeData route data
     */
    public MemoryRouteManager(
        int[] routeData
    ) {
        this.routeData = routeData;
    }

    /**
     * Check if direct route is available between given stops.
     *
     * This method assumes that stop ID segments of the route data array are sorted in natural order.
     * This allows it to use efficient binary search to find arrival and departure stop IDs
     * instead of looping through all the stops to check if match exists.
     * Once one of the stop IDs is found the other is searched using only remaining portion of the stop list.
     * <b>If stop segments are not sorted, the results are undefined.</b>
     *
     * @param departureStopId departure stop identifier
     * @param arrivalStopId arrival stop identifier
     * @return true if direct route is available or false otherwise
     */
    @Override
    public boolean isDirectRouteAvailable(int departureStopId, int arrivalStopId) {
        if (this.routeData.length == 0 ) {
            return false;
        }

        int routeCount = this.routeData[0];
        int currentRouteDataIndex = 1;
        int nextRouteDataIndex;

        for (int routeIndex = 0; routeIndex < routeCount; routeIndex++) {
            // Route ID (routeData[currentRouteDataIndex]) could be handled/returned in real life ;)
            int stopCount = this.routeData[currentRouteDataIndex + 1];
            int firstStopIndex = currentRouteDataIndex + 2;
            nextRouteDataIndex = firstStopIndex + stopCount;

            int arrivalIndex = Arrays.binarySearch(this.routeData, firstStopIndex, nextRouteDataIndex, arrivalStopId);
            if (arrivalIndex > 0) {
                // search only one part of the stop list (not the entire one)
                int departureIndex = (arrivalStopId > departureStopId)
                    ? Arrays.binarySearch(this.routeData, firstStopIndex, arrivalIndex, departureStopId)
                    : Arrays.binarySearch(this.routeData, arrivalIndex, nextRouteDataIndex, departureStopId);
                if (departureIndex > 0) {
                    return true;
                }
            }

            currentRouteDataIndex = nextRouteDataIndex;
        }
        return false;
    }
}
