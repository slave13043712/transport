package org.aakimov.transport.api;


import org.junit.Before;
import org.junit.Test;

import java.nio.file.Paths;

import static org.junit.Assert.assertArrayEquals;

public class MappedRouteDataReaderTest {

    private RouteDataReader routeDataLoader;

    @Before
    public void setUp() {
        this.routeDataLoader = new MappedRouteDataReader();
    }

    @Test
    public void testReadReturnsCorrectDataIfRouteDataFileIsValid() {
        int[] expectedRouteData = {
            // number of routes
            2,
            // route ID followed by the number of stops and sorted stop IDs
            1, 13, 2, 3, 5, 9, 13, 34, 43, 45, 65, 71, 73, 76, 88,
            2, 6, 5, 12, 34, 55, 76, 178
        };
        assertArrayEquals(
            "Route data contents should be valid.",
            expectedRouteData,
            this.routeDataLoader.read(
                Paths.get("src/test/resources/org/aakimov/transport/api/route_data_file")
            )
        );
    }

    @Test
    public void testReadReturnsEmptyArrayIfRouteDataFileIsEmptyOrInvalid() {
        int[] expectedRouteData = new int[0];
        assertArrayEquals("Route data contents should be empty if route data file is empty.",
            expectedRouteData,
            this.routeDataLoader.read(
                Paths.get("src/test/resources/org/aakimov/transport/api/empty_route_data_file")
            )
        );
    }

    @Test
    public void testReadReturnsEmptyArrayIfRouteDataFileContainsCorruptedFirstLine() {
        int[] expectedRouteData = new int[0];
        assertArrayEquals("Route data contents should be empty if first line of the file is corrupted.",
            expectedRouteData,
            this.routeDataLoader.read(
                Paths.get("src/test/resources/org/aakimov/transport/api/route_data_file_with_corrupted_first_line")
            )
        );
    }

    @Test
    public void testReadReturnsEmptyArrayIfRouteDataFileContainsCorruptedRouteData() {
        int[] expectedRouteData = new int[0];
        assertArrayEquals("Route data contents should be empty if route data in the file is corrupted.",
            expectedRouteData,
            this.routeDataLoader.read(
                Paths.get("src/test/resources/org/aakimov/transport/api/route_data_file_with_corrupted_route_data")
            )
        );
    }

    @Test
    public void testReadReturnsEmptyArrayIfRouteDataFileDeclaresWrongNumberOfRoutes() {
        int[] expectedRouteData = new int[0];
        assertArrayEquals("Route data contents should be empty if file declares wrong number of routes.",
            expectedRouteData,
            this.routeDataLoader.read(
                Paths.get("src/test/resources/org/aakimov/transport/api/route_data_file_with_wrong_number_of_routes")
            )
        );
    }

    @Test
    public void testReadReturnsEmptyArrayIfRouteDataFileDeclaresWrongNumberOfStops() {
        int[] expectedRouteData = new int[0];
        assertArrayEquals("Route data contents should be empty if file declares wrong number of stops.",
            expectedRouteData,
            this.routeDataLoader.read(
                Paths.get("src/test/resources/org/aakimov/transport/api/route_data_file_with_wrong_number_of_stops")
            )
        );
    }
}
