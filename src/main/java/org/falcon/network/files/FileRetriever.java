package org.falcon.network.files;

import java.io.*;

/**
 * this class gets the folder where all the files are stored of a web from the pages directory
 */
public class FileRetriever {

    public FileRetriever() {

    }

    /**
     * Returns a file from the files of a domain
     *
     * @param resource the name of the resource to be searched in the folder of the resource
     * @return the resource file
     * @throws FileNotFoundException if the file is not found or if the parent folder of the file is not found
     */
    public File getFile(String resource) throws FileNotFoundException {
        try {
            File pagesFolder = new File("pages");

            if (!pagesFolder.exists()) {
                pagesFolder.mkdir();
            }

            File resourceFile = new File(pagesFolder, resource);
            if (!resourceFile.exists()) {
                throw new FileNotFoundException(resource);
            }

            return resourceFile;
        } catch (NullPointerException e) {
            //todo make logger
            throw new FileNotFoundException(resource);
        }
    }
}
