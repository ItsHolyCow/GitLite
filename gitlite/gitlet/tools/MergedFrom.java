package gitlet.tools;

import java.io.*;
import java.util.ArrayList;
import gitlet.tools.Commit;

public class MergedFrom {
    private static final File MERGED_FROM_FILE = new File("./.gitlet/MergedFrom");
    private static ArrayList<Commit> mergedFrom = new ArrayList<>();

    // Static initializer block to load data once when the class is first accessed
    static {
        try {
            load();
        } catch (IOException | ClassNotFoundException e) {
            mergedFrom = new ArrayList<>(); // fallback to empty if anything goes wrong
        }
    }

    public static ArrayList<Commit> getMergedFrom() {
        return mergedFrom;
    }

    public static void setMergedFrom(ArrayList<Commit> merged) {
        mergedFrom = merged;
    }

    // 🔻 Save to ./.gitlet/MergedFrom
    public static void save() throws IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(MERGED_FROM_FILE))) {
            out.writeInt(mergedFrom.size());  // store number of commits
            for (Commit c : mergedFrom) {
                out.writeObject(c);  // Commit must be Serializable
            }
        }
    }

    // 🔻 Load from ./.gitlet/MergedFrom
    private static void load() throws IOException, ClassNotFoundException {
        if (!MERGED_FROM_FILE.exists()) {
            mergedFrom = new ArrayList<>();
            return;
        }

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(MERGED_FROM_FILE))) {
            int size = in.readInt();
            ArrayList<Commit> list = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                Commit commit = (Commit) in.readObject();
                list.add(commit);
            }
            mergedFrom = list;
        }
    }
}
