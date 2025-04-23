package org.falcon.network.files.procesing;

import com.sun.net.httpserver.HttpExchange;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public interface FileProcessor {

    String processFile(File file, HttpExchange exchange) throws FileNotFoundException, IOException;
}
