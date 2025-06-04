package gitlet.tools;

import gitlet.tools.AllBranches;
import gitlet.tools.Commit;

import java.io.*;
import java.util.*;

public class CommitSerializer {

    private static final String SERIALIZED_TREE_FILE = "./.gitlet/commits/commit_tree.ser";

    // Serialize all reachable commits from the leaves
    public static void serializeCommitTree() {
        Set<Commit> visited = new HashSet<>();
        ArrayList<Commit> leaves = AllBranches.getLeavesCommit();

        for (Commit leaf : leaves) {
            dfsCollectCommits(leaf, visited);
        }

        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(SERIALIZED_TREE_FILE))) {
            out.writeObject(new ArrayList<>(visited));
            System.out.println("Commit tree serialized with " + visited.size() + " commits.");
        } catch (IOException e) {
            System.err.println("Failed to serialize commit tree: " + e.getMessage());
        }
    }

    // Load commit tree from disk
    public static ArrayList<Commit> deserializeCommitTree() {
        File file = new File(SERIALIZED_TREE_FILE);
        if (!file.exists()) return new ArrayList<>();

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            return (ArrayList<Commit>) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Failed to deserialize commit tree: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    // Recursively collect all parent commits
    private static void dfsCollectCommits(Commit commit, Set<Commit> visited) {
        if (commit == null || visited.contains(commit)) return;
        visited.add(commit);

        if (commit.getParents() != null) {
            for (Commit parent : commit.getParents()) {
                dfsCollectCommits(parent, visited);
            }
        }
    }
}
