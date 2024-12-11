package org.falcon;

import com.sun.net.httpserver.HttpServer;
import org.falcon.errors.ExitErrorCodes;

import java.io.IOException;
import java.net.BindException;
import java.net.InetSocketAddress;

/**
 * This class only starts the server and lets the requestManager do its thing
 */
public class Starter {

    private final static int PORT = 8080;
    private final static int BACKLOG = 0;
    private final RequestManager requestManager = new RequestManager();

    /**
     * starts the server
     */
    public Starter() {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(PORT), BACKLOG);
            contextStart(server);
            server.start();
        } catch (BindException exception) {
            //todo make logger
            System.exit(ExitErrorCodes.BINDING_ERROR_ON_SERVER_START);
        } catch (IOException e) {
            //todo make logger
            System.exit(ExitErrorCodes.HTTP_CREATION_ERROR);
        }
    }

    private void contextStart(HttpServer server) {
        server.createContext("/", requestManager);
    }
}
