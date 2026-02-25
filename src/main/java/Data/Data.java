package Data;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
public class Data {
    public static void addFile(String path) {
        File file = new File(path);
        try {
            // Check if file exists, if not create it
            if (!file.exists()) {
                if (file.createNewFile()) {
                    System.out.println("File created: " + file.getName());
                } else {
                    System.out.println("Failed to create file.");
                }
            } else {
                System.out.println("File already exists.");
            }

        } catch (IOException e) {
            System.err.println("Error handling file: " + e.getMessage());
        }

    }

    public static void writeFile(String path, String data) {
        File file = new File(path);
        if (!file.exists()) {
            addFile(path);
        }
        try (FileWriter fw = new FileWriter(file, true)) {
            fw.write(data);
        } catch (IOException e) {
            System.err.println("Error handling file: " + e.getMessage());
        }
    }
}
