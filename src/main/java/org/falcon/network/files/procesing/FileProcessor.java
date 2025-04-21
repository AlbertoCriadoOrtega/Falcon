package org.falcon.network.files.procesing;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public interface FileProcessor {

    File processFile(File file) throws FileNotFoundException, IOException;
}
