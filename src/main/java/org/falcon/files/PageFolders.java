package org.falcon.files;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import org.falcon.errors.ExitErrorCodes;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

/**
 * this works like a database key-value, it keeps the entries in a json file, after every update, insert and delete it writes the file
 * <p>
 * The entry Key will be a domain and the Value will be the folder path
 * <p>
 * EXAMPLE:
 * <p>
 * {@code { "domain":"test.com", "folder_path":"storage/pages/test" }}
 */
public class PageFolders {

    private final File JSON_FILE = new File("storage/records.json");
    private final HashMap<String, String> folders;

    public PageFolders() {
        folders = getStoredJsonRecords();
    }

    /**
     * Gets the stored values in the json
     * <p>
     * if the file is not found it will be created
     *
     * @return {@link HashMap} with all the value, if the file was missing it will return an empty hash map
     */
    private HashMap<String, String> getStoredJsonRecords() {
        HashMap<String, String> records = new HashMap<>();

        try (FileReader fileReader = new FileReader(JSON_FILE); JsonReader jsonReader = Json.createReader(fileReader)) {
            JsonArray page = jsonReader.readArray();

            page.forEach(entry -> {
                JsonObject record = (JsonObject) entry;
                records.put(record.getString("domain"), record.getString("folder_path"));
            });

        } catch (FileNotFoundException e) {
            //todo make logger
            makeJson();
            return records; //empty hashmap
        } catch (IOException e) {
            //todo make logger
            System.exit(ExitErrorCodes.JSON_FILE_READING_ERROR);
        }

        return records;
    }

    /**
     * Creates the json file for the entries
     */
    private void makeJson() {
        try {
            JSON_FILE.createNewFile();
        } catch (IOException ex) {
            //todo make logger
            System.exit(ExitErrorCodes.JSON_FILE_CREATING_ERROR);
        }
    }

    //TODO MAKE CRUD METHODS

    /**
     * returns the file with the files of the web
     *
     * @param key the domain of the web
     * @return the folder with all the web files, null if
     * @throws NullPointerException if the key is not existing or if the value associated to the key is null
     */
    public File getFolderFile(String key) throws NullPointerException {
        return new File(folders.get(key));
    }

//    public File updateJsonEntry(String key, String newValue) {
//        return new File(folders.get(key));
//    }
//
//    public File deleteJsonEntry(String key) {
//        return new File(folders.get(key));
//    }
//
//    public File newFolderEntry(String key, String newValue) {
//        return new File(folders.get(key));
//    }

}
