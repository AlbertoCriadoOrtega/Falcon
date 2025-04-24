package org.falcon.network.errors;

/**
 * this exception is thrown when a file that is statically managed is request via POST, PUT , DELETE methods
 * @author albertocriadoortega
 */
public class StaticFileRequestException extends RuntimeException {
    public StaticFileRequestException(String message) {
        super(message);
    }
}
