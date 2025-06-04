package gitlet.tools;

import java.io.*;

public class CurrentBranchName {
    private static final String BRANCH_FILE_PATH = ".gitlet/Branches/branch.ser";
    private static String BranchName;

    // Load branch name when class is loaded
    static {
        BranchName = loadBranchName();
    }

    public static String getBranchName() {
        return BranchName;
    }

    public static void setBranchName(String branchName) {
        BranchName = branchName;
        saveBranchName(); // Save to disk whenever updated
    }

    private static void saveBranchName() {
        try {
            File dir = new File(".gitlet/Branches");
            if (!dir.exists()) {
                dir.mkdirs(); // create folder if not present
            }
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(BRANCH_FILE_PATH));
            out.writeObject(BranchName);
            out.close();
        } catch (IOException e) {
            System.err.println("Error saving BranchName: " + e.getMessage());
        }
    }

    private static String loadBranchName() {
        try {
            File file = new File(BRANCH_FILE_PATH);
            if (file.exists()) {
                ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
                String loaded = (String) in.readObject();
                in.close();
                return loaded;
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading BranchName: " + e.getMessage());
        }
        return "main"; // Default branch name if nothing saved
    }
}
