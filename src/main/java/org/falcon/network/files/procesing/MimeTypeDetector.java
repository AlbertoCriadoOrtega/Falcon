package org.falcon.network.files.procesing;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Utility class for detecting the MIME type of a file.
 *
 *  @author  albertocriadoortega
 */
public class MimeTypeDetector {

    /**
     * Detects the MIME type of given file.
     *
     * @param file the file to detect the MIME type for
     * @return the MIME type with charset=utf-8
     * @throws IOException if an error occurs during detection
     */
    public static String detectMimeType(final File file) throws IOException {
        String mimeType = Files.probeContentType(file.toPath());

        if (mimeType == null) {
            mimeType = "application/octet-stream"; // Default MIME type
        }

        if (mimeType.equals("text/plain") && file.getName().endsWith(".php")) {
            mimeType = "text/html"; // Override for PHP files
        }

        return mimeType + "; charset=utf-8";
    }
}
