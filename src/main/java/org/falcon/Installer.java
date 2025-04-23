package org.falcon;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Class for when the first usage starts, it creates necessary folders and download necessary tools
 */
public class Installer {

    public Installer() {
        createPagesFolder();

        if (!new File("php").exists()){
            downloadAndDecompressPHP();
        }
    }

    public void createPagesFolder() {
        File pagesFolder = new File("pages");

        if (!pagesFolder.exists()) {
            pagesFolder.mkdir();
        }
    }

    public void downloadAndDecompressPHP() {
        String fileURL = "https://windows.php.net/downloads/releases/php-8.4.6-nts-Win32-vs17-x64.zip";
        String saveDir = "php"; // Folder where PHP will be downloaded and extracted

        try {
            // Create the folder if it doesn't exist
            Files.createDirectories(Paths.get(saveDir));

            // Download the ZIP file
            String zipFilePath = saveDir + File.separator + "php.zip";
            downloadFile(fileURL, zipFilePath);

            // Extract the ZIP file
            unzip(zipFilePath, saveDir);

            // Optionally delete the ZIP file after extraction
            Files.deleteIfExists(Paths.get(zipFilePath));

            System.out.println("PHP downloaded and extracted successfully to: " + saveDir);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void downloadFile(String fileURL, String savePath) throws IOException {
        URL url = new URL(fileURL);
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
        int responseCode = httpConn.getResponseCode();

        // Check HTTP response code
        if (responseCode == HttpURLConnection.HTTP_OK) {
            InputStream inputStream = httpConn.getInputStream();
            FileOutputStream outputStream = new FileOutputStream(savePath);

            byte[] buffer = new byte[4096];
            int bytesRead;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            outputStream.close();
            inputStream.close();
            System.out.println("Downloaded: " + savePath);
        } else {
            System.out.println("No file to download. Server replied HTTP code: " + responseCode);
        }
        httpConn.disconnect();
    }

    private void unzip(String zipFilePath, String destDirectory) throws IOException {
        File destDir = new File(destDirectory);
        byte[] buffer = new byte[1024];

        ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFilePath));
        ZipEntry entry = zipIn.getNextEntry();

        while (entry != null) {
            File newFile = new File(destDir, entry.getName());

            // Create directories for subfolders
            if (entry.isDirectory()) {
                newFile.mkdirs();
            } else {
                new File(newFile.getParent()).mkdirs();

                FileOutputStream fos = new FileOutputStream(newFile);
                int len;
                while ((len = zipIn.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
                fos.close();
            }
            zipIn.closeEntry();
            entry = zipIn.getNextEntry();
        }
        zipIn.close();
        System.out.println("Extraction complete.");
    }
}
