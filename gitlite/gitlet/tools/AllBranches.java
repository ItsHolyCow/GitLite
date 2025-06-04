package gitlet.tools;

import gitlet.tools.Commit;

import java.io.*;
import java.util.ArrayList;

public class AllBranches {
    private static final String LEAVES_FILE_PATH = ".gitlet/Branches/leaves.ser";
    private static ArrayList<Commit> LeavesCommit;

    static {
        LeavesCommit = loadLeaves();
    }

    public static ArrayList<Commit> getLeavesCommit() {
        return LeavesCommit;
    }

    public static void setLeavesCommit(ArrayList<Commit> leavesCommit) {
        LeavesCommit = leavesCommit;
        saveLeaves(); // Save to file after update
    }

    private static void saveLeaves() {
        try {
            File dir = new File(".gitlet/Branches");
            if (!dir.exists()) {
                dir.mkdirs(); // create directory if it doesn't exist
            }
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(LEAVES_FILE_PATH));
            out.writeObject(LeavesCommit);
            out.close();
        } catch (IOException e) {
            System.err.println("Error saving LeavesCommit: " + e.getMessage());
        }
    }

    private static ArrayList<Commit> loadLeaves() {
        try {
            File file = new File(LEAVES_FILE_PATH);
            if (file.exists()) {
                ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
                ArrayList<Commit> loaded = (ArrayList<Commit>) in.readObject();
                in.close();
                return loaded;
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading LeavesCommit: " + e.getMessage());
        }
        return new ArrayList<>(); // return empty list if no saved data
    }
}
