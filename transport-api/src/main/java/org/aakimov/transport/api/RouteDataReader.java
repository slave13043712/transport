package org.aakimov.transport.api;

import java.nio.file.Path;

/**
 * Route data reader.
 *
 * @author aakimov
 */
@FunctionalInterface
public interface RouteDataReader {

    /**
     * Retrieve route data from the given location
     *
     * @param routeDataPath path to route data
     * @return route data array
     */
    public int[] read(Path routeDataPath);
}
