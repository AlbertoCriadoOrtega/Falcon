package org.falcon.network.files.procesing;

import com.sun.net.httpserver.HttpExchange;

import java.io.File;
import java.io.IOException;

/**
 * Interface for processing files based on the request.
 *
 *  @author  albertocriadoortega
 */
public interface FileProcessor {

    /**
     * Processes a file based on the client request.
     *
     * @param file the file to be processed
     * @param exchange the HTTP exchange containing the request data
     * @return the processed file content as a string
     * @throws IOException if an error occurs during file processing
     */
    String processFile(File file, HttpExchange exchange) throws IOException;
}
