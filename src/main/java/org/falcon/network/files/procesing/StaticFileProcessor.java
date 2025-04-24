package org.falcon.network.files.procesing;

import com.sun.net.httpserver.HttpExchange;

import java.io.*;

/**
 * Processes static files by reading their content and returning it as a string.
 * Implements the {@link FileProcessor} interface to handle file processing for static resources.
 *
 *  @author albertocriadoortega
 */
public class StaticFileProcessor implements FileProcessor {

    /**
     * Processes a static file by reading its content and returning it as a string.
     *
     * @param file the file to be processed
     * @param exchange the HttpExchange object containing the request and response information
     * @return the content of the file as a string
     * @throws IOException if an error occurs while reading the file
     */
    @Override
    public String processFile(File file, HttpExchange exchange) throws IOException {
        return readFileToString(file);
    }

    /**
     * Reads the content of a file and returns it as a string.
     *
     * @param file the file to be read
     * @return the content of the file as a string
     * @throws IOException if an error occurs while reading the file
     */
    private String readFileToString(File file) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            stringBuilder.append(line);
            stringBuilder.append("\n"); // Add line break to maintain the file's structure
        }
        bufferedReader.close();
        return stringBuilder.toString();
    }
}
