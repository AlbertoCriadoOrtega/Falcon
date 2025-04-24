package org.falcon.network;

import com.sun.net.httpserver.HttpServer;
import org.falcon.network.errors.ExitErrorCodes;
import org.falcon.network.errors.Logger;

import java.io.IOException;
import java.net.BindException;
import java.net.InetSocketAddress;
import java.time.LocalDateTime;

/**
 * This class starts the server and lets the RequestManager handle requests.
 *  @author  albertocriadoortega
 */
public class Starter {

    private final static int PORT = 8080;
    private final static int BACKLOG = 0;
    private final RequestManager requestManager = new RequestManager();

    /**
     * Starts the server on the specified port and handles incoming requests.
     * If the server fails to start, it exits with the appropriate error code.
     */
    public Starter() {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(PORT), BACKLOG);
            contextStart(server);
            server.start();
            System.out.println(LocalDateTime.now().format(Logger.formatter)+": server started correctly");
        } catch (BindException exception) {
            System.out.println(LocalDateTime.now().format(Logger.formatter)+": "+exception.getMessage());
            System.exit(ExitErrorCodes.BINDING_ERROR_ON_SERVER_START);
        } catch (IOException exception) {
            System.out.println(LocalDateTime.now().format(Logger.formatter)+": "+exception.getMessage());
            System.exit(ExitErrorCodes.HTTP_CREATION_ERROR);
        }
    }

    /**
     * Configures the server to use the RequestManager to handle requests at the root context.
     *
     * @param server the HttpServer instance to be configured
     */
    private void contextStart(HttpServer server) {
        server.createContext("/", requestManager);
    }
}
