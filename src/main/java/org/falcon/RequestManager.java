package org.falcon;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.falcon.files.FileRetriever;
import org.falcon.files.procesing.CssProcessor;
import org.falcon.files.procesing.JavascriptProcessor;
import org.falcon.files.procesing.PhpProcessor;

import java.io.*;
import java.net.HttpURLConnection;
import java.nio.file.Files;

/**
 * Manages the client request and sends the corresponding responses to the client
 */
public class RequestManager implements HttpHandler {

    public RequestManager() {

    }

    /**
     * Retrieves the files from their own folders and sends them through a request,
     * if the file in not found sends 404,
     * if there was an unexpected error in the file retrieving it sends a 500
     *
     * @param exchange the exchange containing the request from the
     *                 client and used to send the response
     * @throws IOException if in the sending of the response something failed
     */
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String domain = exchange.getRequestHeaders().getFirst("host");
        String resource = exchange.getRequestURI().getPath();

        try {
            FileRetriever filesRetriever = new FileRetriever();
            File resourceFile = filesRetriever.getFile(domain, resource);//consigue le archivo que desea con la peticion
            sendResponse(exchange, resourceFile);
        } catch (FileNotFoundException e) {
            //todo make logger
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_NOT_FOUND, 0);
        } catch (IOException exception) {
            //todo make logger
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_INTERNAL_ERROR, 0);
        }
    }

    /**
     * Sends a response to the client, if the response failed it sends another response but 500
     *
     * @param exchange     the request received
     * @param resourceFile the file to be send
     * @throws IOException if in the sending of the response something failed
     */
    private void sendResponse(HttpExchange exchange, File resourceFile) throws IOException {
        String mimeType = Files.probeContentType(resourceFile.toPath());
        if (mimeType == null) {
            mimeType = "application/octet-stream"; // Tipo genérico
        }

        try {
            byte[] fileBytes = readFileToBytes(resourceFile);
            exchange.getResponseHeaders().add("Content-Type", mimeType);
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, fileBytes.length);
            OutputStream os = exchange.getResponseBody();
            os.write(fileBytes);
            os.close();
        } catch (IOException exception) {
            //todo make logger
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_INTERNAL_ERROR, 0);
        }
    }

    /**
     * Returns the contents of a file into a byte Array
     *
     * @param file the file witch contents are going to be converted into a byte Array
     * @return The byte array
     * @throws IOException if something failed in the IO
     */
    private byte[] readFileToBytes(File file) throws IOException {
        FileInputStream fis = new FileInputStream(file);
        byte[] fileBytes = new byte[(int) file.length()];
        fis.read(fileBytes);
        fis.close();
        return fileBytes;
    }

    //region Work in progress

    //TODO NOT DONE STILL WORKING
    /**
     * send a file though an interpreter
     * @param file
     * @throws IOException
     */
    private String interpretFile(File file) throws IOException {

        if (isJavascriptFile(file)) {
            JavascriptProcessor processor = new JavascriptProcessor();
            processor.processFile(file);
        }

        if (isPHPFile(file)) {
            PhpProcessor processor = new PhpProcessor();
            processor.processFile(file);
        }

        if (isCSSFile(file)) {
            CssProcessor processor = new CssProcessor();
            processor.processFile(file);
        }

        return null;
    }

    private boolean isJavascriptFile(File file) {
        return file.getName().endsWith(".js");
    }

    private boolean isPHPFile(File file) {
        return file.getName().endsWith(".php");
    }

    private boolean isCSSFile(File file) {
        return file.getName().endsWith(".css");
    }

    private byte[] readStringToBytes(String string) throws IOException {
        return string.getBytes();
    }

    //endregion
}
