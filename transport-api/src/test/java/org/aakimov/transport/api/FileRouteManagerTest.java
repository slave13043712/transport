package org.aakimov.transport.api;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class FileRouteManagerTest {

    @Test
    public void testIsDirectRouteAvailableCorrectlyIdentifiesDirectRoute() {
        FileRouteManager fileRouteManager = new FileRouteManager(
            "src/test/resources/org/aakimov/transport/api/route_data_file"
        );

        assertTrue(fileRouteManager.isDirectRouteAvailable(2, 43));
        assertTrue(fileRouteManager.isDirectRouteAvailable(13, 3));
        assertTrue(fileRouteManager.isDirectRouteAvailable(5, 55));
        assertTrue(fileRouteManager.isDirectRouteAvailable(178, 76));
        assertFalse(fileRouteManager.isDirectRouteAvailable(3, 55));
        assertFalse(fileRouteManager.isDirectRouteAvailable(2, 12));
    }
}
