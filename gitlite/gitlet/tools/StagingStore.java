package gitlet.tools;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import gitlet.tools.Encode;

public class StagingStore {
    private static final String STAGE_FILE_PATH = ".gitlet/stage.ser";

    private static Map<String, String> staged_file;

    // Load from disk on class load
    static {
        staged_file = loadStage();
    }

    public static Map<String, String> getStaged_file() {
        return staged_file;
    }
    public static void setStaged_file(Map<String,String> temp){
        staged_file = temp;
    }
    public static void setStaged_file_file(String fileName) throws IOException {
        Path workingDir = Paths.get(System.getProperty("user.dir"));
        Path source = workingDir.resolve(fileName);
        byte[] content = Files.readAllBytes(source);
        String id = Encode.sha1(content);
        staged_file.put(fileName, id);
        saveStage(); // Save after modifying
    }

    public static boolean isstaged(String fileName) {
        System.out.println(staged_file);
        return staged_file.containsKey(fileName);
    }

    private static void saveStage() {
        try {
            // Ensure .gitlet directory exists
            Files.createDirectories(Paths.get(".gitlet"));
            // File file = new File("./.gitlet");
            // if(file!=null) exists
            // else notexitst
            // simple version of that
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(STAGE_FILE_PATH));
            out.writeObject(staged_file);
            out.close();
        } catch (IOException e) {
            System.err.println("Error saving staged_file: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private static Map<String, String> loadStage() {
        try {
            File f = new File(STAGE_FILE_PATH);
            if (f.exists()) {
                ObjectInputStream in = new ObjectInputStream(new FileInputStream(f));
                Map<String, String> loaded = (Map<String, String>) in.readObject();
                in.close();
                return loaded;
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading staged_file: ");
        }
        return new HashMap<>();
    }
}
