package org.falcon.network;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.falcon.network.errors.Logger;
import org.falcon.network.errors.StaticFileRequestException;
import org.falcon.network.files.FileRetriever;
import org.falcon.network.files.procesing.FileProcessor;
import org.falcon.network.files.procesing.MimeTypeDetector;
import org.falcon.network.files.procesing.PhpProcessor;
import org.falcon.network.files.procesing.StaticFileProcessor;

import java.io.*;
import java.net.HttpURLConnection;
import java.time.LocalDateTime;

/**
 * Manages the client request and sends the corresponding responses to the client.
 * @author albertocriadoortega
 */
public class RequestManager implements HttpHandler {

    private final FileRetriever filesRetriever = new FileRetriever();

    public RequestManager() {}

    /**
     * Retrieves the files from their own folders and sends them through a request.
     * If the file is not found, sends a 404.
     * If there was an unexpected error in file retrieval, sends a 500.
     *
     * @param exchange the exchange containing the request from the
     *                 client and used to send the response
     * @throws IOException if in the sending of the response something failed
     */
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String resource = exchange.getRequestURI().getPath();
        System.out.println(LocalDateTime.now().format(Logger.formatter) + ": IP " + exchange.getRemoteAddress() + " requested " + resource);

        try {
            // Retrieve and process the requested file
            File resourceFile = filesRetriever.getFile(resource);
            byte[] processedFileBytes = processFile(resourceFile, exchange);

            // Set the response headers and send the file
            sendFileResponse(exchange, resourceFile, processedFileBytes);
        } catch (FileNotFoundException e) {
            handleError(exchange, HttpURLConnection.HTTP_NOT_FOUND, "File not found: " + resource);
        } catch (IOException e) {
            handleError(exchange, HttpURLConnection.HTTP_INTERNAL_ERROR, "IOException: " + e.getMessage());
        } catch (StaticFileRequestException e) {
            handleError(exchange, HttpURLConnection.HTTP_BAD_METHOD, "Static file request via POST/PUT/DELETE is not allowed.");
        } finally {
            exchange.close();
        }
    }

    /**
     * Sends the file response to the client with appropriate headers.
     *
     * @param exchange the HTTP exchange
     * @param resourceFile the file being sent
     * @param processedFileBytes the processed file content
     * @throws IOException if an error occurs during response sending
     */
    private void sendFileResponse(HttpExchange exchange, File resourceFile, byte[] processedFileBytes) throws IOException {
        exchange.getResponseHeaders().add("Content-Type", MimeTypeDetector.detectMimeType(resourceFile));
        exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, processedFileBytes.length);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(processedFileBytes);
        }
    }

    /**
     * Handles error responses by logging the error and sending the appropriate HTTP response.
     *
     * @param exchange the HTTP exchange
     * @param statusCode the HTTP status code to send
     * @param message the error message to log
     * @throws IOException if an error occurs during error response sending
     */
    private void handleError(HttpExchange exchange, int statusCode, String message) throws IOException {
        System.out.println(LocalDateTime.now().format(Logger.formatter) + ": " + message);
        exchange.sendResponseHeaders(statusCode, 0);
    }

    /**
     * Sends a response to the client.
     * If sending the response fails, sends another response with status 500.
     *
     * @param exchange     the request received
     * @param fileToBytes  the byte array containing the file data
     * @throws IOException if in the sending of the response something failed
     */
    private void sendResponse(HttpExchange exchange, byte[] fileToBytes ) throws IOException {
        try {
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, fileToBytes.length);
            OutputStream os = exchange.getResponseBody();
            os.write(fileToBytes);
            os.close();
        } catch (IOException exception) {
            System.out.println(LocalDateTime.now().format(Logger.formatter)+": IOException: "+exception.getMessage());
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_INTERNAL_ERROR, 0);
        }
    }

    /**
     * Returns the contents of a file as a byte array.
     *
     * @param file the file whose contents are going to be converted into a byte array
     * @return the byte array with the file's contents
     * @throws IOException if something failed in the IO process
     */
    private byte[] readFileToBytes(File file) throws IOException {
        FileInputStream fis = new FileInputStream(file);
        byte[] fileBytes = new byte[(int) file.length()];
        fis.read(fileBytes);
        fis.close();
        return fileBytes;
    }

    /**
     * Processes the requested file and returns its content as a byte array.
     * If the file is static, only GET is allowed.
     * PHP files are processed accordingly.
     *
     * @param file     the file to be processed
     * @param exchange the request information
     * @return the processed file content as byte array
     * @throws IOException if an IO error occurs
     * @throws StaticFileRequestException if the request method is not allowed for static files
     */
    private byte[] processFile(File file, HttpExchange exchange) throws IOException, StaticFileRequestException {

        if (isStaticFile(file) && !exchange.getRequestMethod().equals("GET")) {
            throw new StaticFileRequestException(file.getName());
        } else if (isStaticFile(file)) {
            FileProcessor processor = new StaticFileProcessor();
            String processedFile = processor.processFile(file, exchange);
            return processedFile.getBytes();
        }

        if (isPHPFile(file)) {
            FileProcessor processor = new PhpProcessor();
            String processedFile = processor.processFile(file, exchange);
            return processedFile.getBytes();
        }

        return readFileToBytes(file);
    }

    /**
     * Checks if a file is a static resource.
     *
     * @param file the file to check
     * @return true if the file is a static file (html, css, js), false otherwise
     */
    private boolean isStaticFile(File file) {
        String name = file.getName();
        int dotIndex = name.lastIndexOf(".");
        if (dotIndex == -1 || dotIndex == name.length() - 1) {
            return false; // No extension or dot is at the end
        }

        String extension = name.substring(dotIndex + 1).toLowerCase();
        return extension.equals("html") || extension.equals("css") || extension.equals("js");
    }

    /**
     * Checks if a file is a PHP file.
     *
     * @param file the file to check
     * @return true if the file has a .php extension, false otherwise
     */
    private boolean isPHPFile(File file) {
        return file.getName().endsWith(".php");
    }
}
