package org.falcon.network.files.procesing;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

import java.io.*;

/**
 * Processes PHP files by passing them through the PHP interpreter and executing the PHP code.
 * Implements the {@link FileProcessor} interface to handle PHP file processing.
 * @author  albertocriadoortega
 */
public class PhpProcessor implements FileProcessor {

    /**
     * Processes a PHP file by executing it through the PHP interpreter and returning the output.
     *
     * @param file the PHP file to be processed
     * @param exchange the HttpExchange object containing the request and response information
     * @return the output from the PHP interpreter as a string
     * @throws IOException if an error occurs during the execution of the PHP file
     */
    @Override
    public String processFile(File file, HttpExchange exchange) throws IOException {
        return interpretPhpFile(file, exchange);
    }

    /**
     * Executes the PHP file using the PHP interpreter and returns the output as a string.
     * This method sets the necessary environment variables for PHP-CGI and captures both the standard and error output.
     *
     * @param file the PHP file to be executed
     * @param exchange the HttpExchange object containing the request and response information
     * @return the output from the PHP interpreter as a string
     * @throws IOException if an error occurs during the PHP file execution
     */
    private static String interpretPhpFile(File file, HttpExchange exchange) throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder("./php/php-cgi.exe");
        setEnviromentVariables(file, exchange, processBuilder);


        Process process = processBuilder.start();

        // Write the POST data to Php's standard input
        OutputStream bodyDataStream = process.getOutputStream();
        InputStream requestBodyStream = exchange.getRequestBody();
        requestBodyStream.transferTo(bodyDataStream);
        bodyDataStream.close();

        // Capture the output from PHP (both standard and error)
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));

        StringBuilder respuesta = new StringBuilder();
        String linea;
        reader.readLine(); // Removes the PHP version notice
        reader.readLine(); // Removes the content type notice
        while ((linea = reader.readLine()) != null) {
            respuesta.append(linea).append("\n");
        }

        StringBuilder errorOutput = new StringBuilder();
        while ((linea = errorReader.readLine()) != null) {
            errorOutput.append(linea).append("\n");
        }

        // Log errors (if any)
        if (!errorOutput.isEmpty()) {
            System.err.println("Php Error: " + errorOutput);
        }

        return respuesta.toString();
    }

    /**
     * Sets the necessary environment variables for the Php interpreter to execute the script.
     *
     * @param file the Php file to be executed
     * @param exchange the HttpExchange object containing the request and response information
     * @param processBuilder the ProcessBuilder instance used to start the PHP interpreter
     */
    private static void setEnviromentVariables(File file, HttpExchange exchange, ProcessBuilder processBuilder) {
        processBuilder.environment().put("SCRIPT_FILENAME", file.getAbsolutePath());
        processBuilder.environment().put("QUERY_STRING", exchange.getRequestURI().getQuery() != null ? exchange.getRequestURI().getQuery() : "");
        processBuilder.environment().put("REQUEST_METHOD", exchange.getRequestMethod());
        processBuilder.environment().put("REDIRECT_STATUS", "200"); // Required by php-cgi

        Headers headers = exchange.getRequestHeaders();
        String contentType = headers.getFirst("Content-Type");
        String contentLength = headers.getFirst("Content-Length");

        if (contentType != null) {
            processBuilder.environment().put("CONTENT_TYPE", contentType);
        }
        if (contentLength != null) {
            processBuilder.environment().put("CONTENT_LENGTH", contentLength);
        }
    }
}
