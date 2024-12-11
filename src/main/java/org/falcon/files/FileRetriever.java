package org.falcon.files;

import org.falcon.errors.ExitErrorCodes;

import java.io.*;

/**
 * this class gets the folder where all the files are stored of a web from the pages directory
 */
public class FileRetriever {

    private final PageFolders pages = new PageFolders();

    public FileRetriever() {
    }

    /**
     * Returns a file from the files of a domain
     *
     * @param domain   the name of the domain, this is a key in a hashmap
     * @param resource the name of the resource to be searched in the folder of the resource
     * @return the resource file
     * @throws FileNotFoundException if the file is not found or if the parent folder of the file is not found
     */
    public File getFile(String domain, String resource) throws FileNotFoundException {
        try {
            File folder = pages.getFolderFile(domain);

            String index;
            try {
                index = getIndexFile(folder);//the file name from where the root page starts to works
            } catch (IOException e) {
                //todo make logger
                System.exit(ExitErrorCodes.FILE_RETRIEVING_ERROR);
                throw new FileNotFoundException(domain); //for the sake of compilation, the sys exit should kill the jvm, if not BIG PROBLEMS
            }

            if (resource.equals("/")) {
                resource = index;
            }

            File resourceFile = new File(folder, resource);
            if (!resourceFile.exists()) {
                throw new FileNotFoundException(resource);
            }

            return resourceFile;
        } catch (NullPointerException e) {
            //todo make logger
            throw new FileNotFoundException(domain);
        }
    }

    /**
     * Searches in the .conf file in the folder of the webpage and gets the name of the index file
     *
     * @return the name of the index file
     */
    private String getIndexFile(File folder) throws FileNotFoundException, IOException {
        final File configFile = new File(folder, WebFolderSettings.CONFIG_FILE_NAME);

        String indexName = null;
        try (BufferedReader br = new BufferedReader(new FileReader(configFile))) {
            while (br.ready()) {
                String line = br.readLine();

                if (line.contains(WebFolderSettings.STARTER_FILE_SETTING)) {
                    indexName = line.trim().split(":")[1];
                }
            }
        }

        return indexName;
    }


}
