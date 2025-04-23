package org.falcon.network.files.procesing;

import com.sun.net.httpserver.HttpExchange;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.*;

public class PhpProcessor implements FileProcessor {

    /**
     * passes the file through the php interpreter en executes its code
     *
     * @param file
     * @return
     */
    @Override
    public String processFile(File file, HttpExchange exchange) throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder("./php/php-cgi.exe");

// Set environment variables
        processBuilder.environment().put("SCRIPT_FILENAME", file.getAbsolutePath());
        processBuilder.environment().put("QUERY_STRING", exchange.getRequestURI().getQuery());
        processBuilder.environment().put("REQUEST_METHOD", exchange.getRequestMethod());
        processBuilder.environment().put("REDIRECT_STATUS", "200"); // Required for php-cgi

        Process proceso = processBuilder.start();

        // Capture standard and error output
        BufferedReader reader = new BufferedReader(new InputStreamReader(proceso.getInputStream()));
        BufferedReader errorReader = new BufferedReader(new InputStreamReader(proceso.getErrorStream()));

        StringBuilder respuesta = new StringBuilder();
        String linea;

        reader.readLine();//eliminates the php version notice
        reader.readLine();//eliminates the content type notice
        while ((linea = reader.readLine()) != null) {
            respuesta.append(linea).append("\n");
        }

        StringBuilder errorOutput = new StringBuilder();
        while ((linea = errorReader.readLine()) != null) {
            errorOutput.append(linea).append("\n");
        }

        // Log errors (if any)
        if (!errorOutput.isEmpty()) {
            System.err.println("PHP CGI Error: " + errorOutput);
        }

        return respuesta.toString();
    }
}
