package org.aakimov.transport.rest.entities;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Direct route entity.
 *
 * Represents direct route between two stops.
 *
 * @author aakimov
 */
public class DirectRoute {

    /**
     * Departure stop ID
     */
    @JsonProperty("dep_sid")
    private final int departureStopId;

    /**
     * Arrival stop ID
     */
    @JsonProperty("arr_sid")
    private final int arrivalStopId;

    /**
     * Flag that shows if direct route is available
     */
    @JsonProperty("direct_bus_route")
    private final boolean directRouteAvailable;

    /**
     * @param departureStopId departure stop ID
     * @param arrivalStopId arrival stop ID
     * @param directRouteAvailable flag that shows if direct route is available
     */
    @JsonCreator
    public DirectRoute(
        @JsonProperty(value = "dep_sid", required = true) int departureStopId,
        @JsonProperty(value = "arr_sid", required = true) int arrivalStopId,
        @JsonProperty(value = "direct_bus_route", required = true) boolean directRouteAvailable
    ) {
        this.departureStopId = departureStopId;
        this.arrivalStopId = arrivalStopId;
        this.directRouteAvailable = directRouteAvailable;
    }

    /**
     * Retrieve departure stop ID
     *
     * @return departure stop ID
     */
    public int getDepartureStopId() {
        return this.departureStopId;
    }

    /**
     * Retrieve arrival stop ID
     *
     * @return arrival stop ID
     */
    public int getArrivalStopId() {
        return this.arrivalStopId;
    }

    /**
     * Check is direct route is available
     *
     * @return true if direct route is available or false otherwise
     */
    public boolean isDirectRouteAvailable() {
        return this.directRouteAvailable;
    }
}
