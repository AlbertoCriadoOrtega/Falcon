package org.example;

import java.io.File;
import java.io.FileNotFoundException;


/**
 * this class gets the folder where all the files are stored of a web from the pages directory
 */
public class FileRetriever {

    private PageFolders pages = new PageFolders();

    public FileRetriever() {
    }

    /**
     * Returns a file from the files of a domain
     *
     * @param domain
     * @param resource
     * @return
     * @throws FileNotFoundException if the file is not found
     */
    public File getFile(String domain, String resource) throws FileNotFoundException {
        try {
            File folder = pages.getFolderFile(domain);

            if (resource.equals("/")) {
                resource = "index.html";
            }

            System.out.println(resource);

            File resourceFile = new File(folder, resource);

            if (!resourceFile.exists()) {
                throw new FileNotFoundException(resource);
            }

            return resourceFile;
        } catch (NullPointerException e) {
            throw new FileNotFoundException(domain);
        }
    }


}
