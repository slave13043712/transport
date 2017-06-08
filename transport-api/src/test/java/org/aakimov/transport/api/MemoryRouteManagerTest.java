package org.aakimov.transport.api;


import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MemoryRouteManagerTest {

    private int[] routeData;

    @Before
    public void setUp() {
        this.routeData = new int[] {
            // number of routes
            2,
            // route ID followed by the number of stops and sorted stop IDs
            1, 13, 2, 3, 5, 9, 13, 34, 43, 45, 65, 71, 73, 76, 88,
            2, 6, 5, 12, 34, 55, 76, 78
        };
    }

    @Test
    public void testIsDirectRouteAvailableCorrectlyIdentifiesDirectRoute() {
        MemoryRouteManager memoryRouteManager = new MemoryRouteManager(this.routeData);
        assertTrue(memoryRouteManager.isDirectRouteAvailable(2, 43));
        assertTrue(memoryRouteManager.isDirectRouteAvailable(13, 3));
        assertTrue(memoryRouteManager.isDirectRouteAvailable(5, 55));
        assertTrue(memoryRouteManager.isDirectRouteAvailable(78, 76));
        assertTrue(memoryRouteManager.isDirectRouteAvailable(88, 76));
        assertFalse(memoryRouteManager.isDirectRouteAvailable(2, 12));
        assertFalse(memoryRouteManager.isDirectRouteAvailable(3, 55));
    }

    @Test
    public void testIsDirectRouteAvailableReturnsFalseIfRouteDataIsEmpty() {
        MemoryRouteManager memoryRouteManager = new MemoryRouteManager(new int[0]);
        assertFalse(memoryRouteManager.isDirectRouteAvailable(100, 200));
    }
}
