package org.aakimov.transport.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.Cleaner;//NOSONAR
import sun.nio.ch.DirectBuffer;//NOSONAR

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;

/**
 * Implementation of route data reader that performs fast mapped data read from the given file.
 *
 * Route data is returned in form of the integer array that has the following structure:
 *
 * [route count][route ID1][stop count of route ID1][stop ID1][stop ID2]...[route ID2][stop count of route ID2]...
 *
 * Stop ID segments of the array are sorted in the natural order.
 * Resulting array has a maximum memory footprint of 36 + 4 * 1002 * N bytes (where N is a number of routes)
 *
 * @author aakimov
 */
public class MappedRouteDataReader implements RouteDataReader {

    /**
     * Logger instance
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(RouteDataReader.class);

    /**
     * Exception message for corrupted route data file
     */
    private static final String MESSAGE_UNEXPECTED_SYMBOL = "Route data file is corrupted. Unexpected symbol.";

    /**
     * {@inheritDoc}
     */
    @Override
    public int[] read(Path routeDataPath) {
        try (
            FileChannel fileChannel = (FileChannel)Files.newByteChannel(routeDataPath, StandardOpenOption.READ)
        ) {
            LOGGER.info("Route data file '{}' processing started.", routeDataPath);

            /**
             * Map the entire file directly into memory outside the heap and make everything super fast
             * (much faster than hipster's Files.lines etc ;)
             * This approach has a file size limitation.
             * But the task requirements (100000 routes with 1000 stops per route) do not violate it .
             */
            long bufferLength = fileChannel.size();
            MappedByteBuffer mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, bufferLength);
            mappedByteBuffer.load();

            int declaredRouteCount = this.processFirstLine(mappedByteBuffer);
            if (declaredRouteCount == 0) {
                throw new FileFormatException("Route data file is empty.");
            }

            int[] routeData = this.processRouteLines(mappedByteBuffer, declaredRouteCount);

            // explicitly clean the mapped memory region
            Cleaner cleaner = ((DirectBuffer)mappedByteBuffer).cleaner();
            cleaner.clean();

            LOGGER.info("Route data file '{}' processing finished.", routeDataPath);
            return routeData;
        } catch (FileFormatException|IOException exception) {
            LOGGER.error("Route data file '{}' cannot be processed. Skipping.", routeDataPath, exception);
            return new int[0];
        }
    }

    /**
     * Process first line to retrieve the number of routes
     *
     * @param byteBuffer target byte buffer
     * @return declared number of routes
     */
    private int processFirstLine(ByteBuffer byteBuffer) {
        int routeCount = 0;
        while (byteBuffer.hasRemaining()) {
            byte currentByte = byteBuffer.get();
            if (currentByte >= '0' && currentByte <= '9') {
                // accumulate current digit
                routeCount = routeCount * 10 + (currentByte & 0xF);
            } else if (currentByte == '\n') {
                break;
            } else if (currentByte != '\r') {
                // ignore carriage return
                throw new FileFormatException(MappedRouteDataReader.MESSAGE_UNEXPECTED_SYMBOL);
            }
        }

        return routeCount;
    }

    /**
     * Process route data lines
     *
     * @param byteBuffer target byte buffer
     * @param routeCount expected number of routes
     * @return route data array
     */
    private int[] processRouteLines(ByteBuffer byteBuffer, int routeCount) {
        int routeDataIndex = 0;
        // 1002: route ID + stop count + 1000 stops
        int[] routeData = new int[routeCount * 1002 + 1];
        routeData[routeDataIndex] = routeCount;
        routeDataIndex++;
        for (int routeIndex = 0; routeIndex < routeCount; routeIndex++) {
            routeData[routeDataIndex] = this.processRouteId(byteBuffer);
            int stopCount = this.processRouteStops(byteBuffer, routeData, routeDataIndex + 2);
            routeData[routeDataIndex + 1] = stopCount;
            routeDataIndex += stopCount + 2;
        }

        final int routeDataAllowedLength = 100000 * 1002 + 1;
        // do not waste memory if it is not needed
        if (routeDataIndex < routeDataAllowedLength / 2) {
            routeData = Arrays.copyOf(routeData, routeDataIndex);
        }

        return routeData;
    }

    /**
     * Process route identifier
     *
     * @param byteBuffer target byte buffer
     * @return parsed route ID
     */
    private int processRouteId(ByteBuffer byteBuffer) {
        int routeId = 0;
        boolean isPreviousByteDigit = false;
        while (byteBuffer.hasRemaining()) {
            byte currentByte = byteBuffer.get();
            if (currentByte >= '0' && currentByte <= '9') {
                // accumulate current digit
                routeId = routeId * 10 + (currentByte & 0xF);
                isPreviousByteDigit = true;
            } else if (currentByte == ' ' && isPreviousByteDigit) {
                break;
            } else {
                throw new FileFormatException(MappedRouteDataReader.MESSAGE_UNEXPECTED_SYMBOL);
            }
        }

        return routeId;
    }

    /**
     * Process route stops
     *
     * @param byteBuffer target byte buffer
     * @param routeData route data array to store processed stop IDs
     * @param firstStopIndex index of the first stop ID in the data array
     * @return number of stops that were processed
     */
    private int processRouteStops(ByteBuffer byteBuffer, int[] routeData, final int firstStopIndex) {
        int routeDataIndex = firstStopIndex;
        int currentStopId = 0;
        int stopCount = 0;
        boolean isPreviousByteDigit = false;
        while (byteBuffer.hasRemaining()) {
            byte currentByte = byteBuffer.get();

            if (currentByte >= '0' && currentByte <= '9') {
                // accumulate current digit
                currentStopId = currentStopId * 10 + (currentByte & 0xF);
                isPreviousByteDigit = true;
            } else if (currentByte == ' ' && isPreviousByteDigit) {
                routeData[routeDataIndex] = currentStopId;
                currentStopId = 0;
                routeDataIndex++;
                stopCount++;
                isPreviousByteDigit = false;
            } else if (currentByte == '\n') {
                break;
            } else if (currentByte != '\r') {
                // ignore carriage return
                throw new FileFormatException(MappedRouteDataReader.MESSAGE_UNEXPECTED_SYMBOL);
            }
        }

        // handle last stop ID in the line
        if (isPreviousByteDigit) {
            routeData[routeDataIndex] = currentStopId;
            stopCount++;
        }

        if (stopCount < 2) {
            throw new FileFormatException("Route does not contain at least 2 stops.");
        }

        // sort all the stops of the route
        Arrays.sort(routeData, firstStopIndex, firstStopIndex + stopCount);

        return stopCount;
    }
}
