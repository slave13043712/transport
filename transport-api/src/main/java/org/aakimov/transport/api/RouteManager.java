package org.aakimov.transport.api;

/**
 * Route manager.
 *
 * Can be used to find available routes to destination.
 *
 * @author aakimov
 */
public interface RouteManager { //NOSONAR

    /**
     * Check if direct route is available between given stops.
     *
     * @param departureStopId departure stop identifier
     * @param arrivalStopId arrival stop identifier
     * @return true if direct route is available or false otherwise
     */
    public boolean isDirectRouteAvailable(int departureStopId, int arrivalStopId);
}
