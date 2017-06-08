package org.aakimov.transport.rest.controllers;

import org.aakimov.transport.api.RouteManager;
import org.aakimov.transport.rest.entities.DirectRoute;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;

/**
 * Route manager controller.
 *
 * Handles HTTP requests to route-related resources.
 *
 * @author aakimov
 */
@RestController
@RequestMapping(value = "/api")
public class RouteManagerController {

    /**
     * Route manager
     */
    private final RouteManager routeManager;

    /**
     * @param routeManager route manager
     */
    @Inject
    public RouteManagerController(
        RouteManager routeManager
    ) {
        this.routeManager = routeManager;
    }

    /**
     * Find direct route between two stops represented by given IDs
     *
     * @param departureStopId departure stop ID
     * @param arrivalStopId arrival stop ID
     * @return response object that represent direct route between two stops
     */
    @RequestMapping(
        value = "/direct",
        method = RequestMethod.GET,
        params = {"dep_sid", "arr_sid"},
        produces = {MediaType.APPLICATION_JSON_UTF8_VALUE}
    )
    @ResponseBody
    public DirectRoute findDirectRoute(
        @RequestParam(name = "dep_sid") int departureStopId,
        @RequestParam(name = "arr_sid") int arrivalStopId
    ) {
        return new DirectRoute(
            departureStopId,
            arrivalStopId,
            this.routeManager.isDirectRouteAvailable(departureStopId, arrivalStopId)
        );
    }

    /**
     * Custom exception handler for bad request scenarios.
     */
    @ResponseStatus(
        value= HttpStatus.BAD_REQUEST,
        reason="Invalid request parameters. Expected integer values for 'dep_sid' and 'arr_sid'."
    )
    @ExceptionHandler({NumberFormatException.class})
    public void handleInvalidRequestParameters() {
        // do nothing
    }
}
