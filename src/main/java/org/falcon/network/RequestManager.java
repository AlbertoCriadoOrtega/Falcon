package org.falcon.network;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.falcon.network.errors.StaticFileRequestException;
import org.falcon.network.files.FileRetriever;
import org.falcon.network.files.procesing.FileProcessor;
import org.falcon.network.files.procesing.PhpProcessor;

import java.io.*;
import java.net.HttpURLConnection;
import java.nio.file.Files;

/**
 * Manages the client request and sends the corresponding responses to the client
 */
public class RequestManager implements HttpHandler {

    public RequestManager() {}

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
        String resource = exchange.getRequestURI().getPath();

        try {
            FileRetriever filesRetriever = new FileRetriever();
            File resourceFile = filesRetriever.getFile(resource); //consigue el archivo que desea con la peticion
            byte[] procesedFileBytes = processFile(resourceFile,exchange); //will send the files to the interpreter if needed, or if file is static it will check the method and decide
            sendResponse(exchange, resourceFile, procesedFileBytes);
        } catch (FileNotFoundException e) {
            //todo make logger
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_NOT_FOUND, 0);
            exchange.close();
        } catch (IOException exception) {
            //todo make logger
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_INTERNAL_ERROR, 0);
            exchange.close();
            exception.printStackTrace();
        } catch (StaticFileRequestException exception) {
            exception.printStackTrace();
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_METHOD, 0);
            exchange.close();
        }
    }

    /**
     * Sends a response to the client, if the response failed it sends another response but 500
     *
     * @param exchange     the request received
     * @param resourceFile the file to be sent
     * @throws IOException if in the sending of the response something failed
     */
    private void sendResponse(HttpExchange exchange, File resourceFile, byte[] fileByte ) throws IOException {
        String mimeType = Files.probeContentType(resourceFile.toPath());
        if (mimeType == null) {
            mimeType = "application/octet-stream"; // Tipo genérico
        }

        if (mimeType.equals("text/plain") && resourceFile.getName().endsWith(".php")) {
            mimeType = "text/html";
        }

        mimeType = mimeType + "; charset=utf-8"; // add charset

        try {
            exchange.getResponseHeaders().add("Content-Type", mimeType);
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, fileByte.length);
            OutputStream os = exchange.getResponseBody();
            os.write(fileByte);
            os.close();
        } catch (IOException exception) {
            exception.printStackTrace();
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

    /**
     * processes the file
     * @param file
     * @throws IOException
     * @throws StaticFileRequestException
     */
    private byte[] processFile(File file, HttpExchange exchange) throws IOException, StaticFileRequestException {

        if (isStaticFile(file) && !exchange.getRequestMethod().equals("GET")) {
            throw new StaticFileRequestException(file.getName());
        }

        if (isPHPFile(file)) {
            FileProcessor processor = new PhpProcessor();
            String processedFile = processor.processFile(file, exchange);
            return processedFile.getBytes();
        }

        return readFileToBytes(file);
    }

    private boolean isStaticFile(File file) {
        String name = file.getName();
        int dotIndex = name.lastIndexOf(".");
        if (dotIndex == -1 || dotIndex == name.length() - 1) {
            return false; // No extension or dot is at the end
        }

        String extension = name.substring(dotIndex + 1).toLowerCase();
        return extension.equals("html") || extension.equals("css") || extension.equals("js");
    }


    private boolean isPHPFile(File file) {
        return file.getName().endsWith(".php");
    }
}
