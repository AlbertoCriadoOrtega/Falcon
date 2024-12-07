package org.example;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.example.bugManegement.ExitErrorCodes;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.file.Files;

public class RequestManager {

    public static void main(String[] args) {

        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(80), 0);
            manageRequest(server);
            server.start();
        } catch (IOException e) {
            //todo make logger
            System.exit(ExitErrorCodes.HTTP_CREATION_ERROR);
        }
    }

    /**
     * manages the http requests
     *
     * @param server the server that will handle the request
     */
    private static void manageRequest(HttpServer server) {
        server.createContext("/", new HttpHandler() {
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
                    exchange.sendResponseHeaders(404, 0);
                } catch (IOException exception) {
                    //todo make logger
                    exchange.sendResponseHeaders(500, 0);
                }
            }
        });
    }

    /**
     * @param exchange
     * @param resourceFile
     * @throws IOException
     */
    private static void sendResponse(HttpExchange exchange, File resourceFile) throws IOException {
        String mimeType = Files.probeContentType(resourceFile.toPath());
        if (mimeType == null) {
            mimeType = "application/octet-stream"; // Tipo genérico
        }

        byte[] fileBytes = readFileToBytes(resourceFile);
        exchange.getResponseHeaders().add("Content-Type", mimeType);
        exchange.sendResponseHeaders(200, fileBytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(fileBytes);
        os.close();
    }

    /**
     * @param file
     * @return
     * @throws IOException
     */
    private static byte[] readFileToBytes(File file) throws IOException {
        FileInputStream fis = new FileInputStream(file);
        byte[] fileBytes = new byte[(int) file.length()];
        fis.read(fileBytes);
        fis.close();
        return fileBytes;
    }
}
