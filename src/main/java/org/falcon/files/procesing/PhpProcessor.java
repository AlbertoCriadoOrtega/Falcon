package org.falcon.files.procesing;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.*;

public class PhpProcessor implements FileProcessor {

    /**
     * passes the file through the php interpreter en executes its code
     *
     * @param file
     * @return
     */
    @Override
    public File processFile(File file) throws FileNotFoundException, IOException {
        StringBuilder code = new StringBuilder();

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            while (br.ready()) {
                code.append(br.readLine());
            }
        }

        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("php");  // Replace "php" with the engine you're using
        if (engine == null) {
            throw new IOException("php not found");
        }

        try {
            StringBuilder fileOutput = new StringBuilder((String) engine.eval(code.toString()));
            System.out.println(fileOutput.toString());
        } catch (ScriptException ex) {
            throw new IOException(ex.getMessage());
        }


        return null;
    }

    public static void main(String[] args) {
        PhpProcessor pp = new PhpProcessor();
        try {
            pp.processFile(new File("test.php"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
