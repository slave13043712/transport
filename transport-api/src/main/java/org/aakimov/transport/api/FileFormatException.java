package org.aakimov.transport.api;

/**
 * Route data file format exception.
 *
 * Can be thrown if route data file is invalid.
 *
 * @author aakimov
 */
class FileFormatException extends RuntimeException {

    public FileFormatException() {
        super();
    }

    /**
     * @param message exception message
     */
    public FileFormatException(String message) {
        super(message);
    }

    /**
     * @param message exception message
     * @param cause cause of the exception
     */
    public FileFormatException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param cause cause of the exception
     */
    public FileFormatException(Throwable cause) {
        super(cause);
    }
}
