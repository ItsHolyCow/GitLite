package gitlet.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.*;
import java.nio.charset.StandardCharsets;

import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bson.types.ObjectId;
import gitlet.tools.Commit;
import gitlet.tools.AllBranches;

public class Pushing {
    public static void push(String repoId) {
        File gitlet = new File("./.gitlet");
        if (!gitlet.exists()) {
            System.out.println("❌ Please initialize a repo here first.");
            return;
        }

        File gitletCommits = new File("./.gitlet/commits/commit_tree.ser");
        if (!gitletCommits.exists()) {
            System.out.println("❌ Please add a commit here first.");
            return;
        }

        // Get the leaves (branch heads) from AllBranches
        ArrayList<Commit> branches = AllBranches.getLeavesCommit();
        if (branches == null || branches.isEmpty()) {
            System.out.println("❌ No branches found to push.");
            return;
        }

        // Organize commits by branch name, preserving order (newest to oldest)
        LinkedHashMap<String, LinkedList<HashMap<String, Object>>> branchCommits = new LinkedHashMap<>();

        // Traverse each branch starting from the leaf
        for (Commit leaf : branches) {
            String branchName = leaf.getBranch_name();
            LinkedList<HashMap<String, Object>> commitsList = new LinkedList<>();

            // Traverse from leaf to root
            Commit current = leaf;
            while (current != null) {
                // Prepare commit details
                HashMap<String, Object> commitDetails = new HashMap<>();
                commitDetails.put("commitId", current.getId()); // Assumes Commit has getId()
                commitDetails.put("message", current.getMessage()); // Assumes Commit has getMessage()

                // Get files and their contents as Map<String, byte[]>
                Map<String, byte[]> fileContents = current.getFiles_content(); // Returns Map<String, byte[]>

                // Convert byte[] contents to strings
                Map<String, String> files = new HashMap<>();
                if (fileContents != null) {
                    for (Map.Entry<String, byte[]> entry : fileContents.entrySet()) {
                        String fileName = entry.getKey();
                        byte[] contentBytes = entry.getValue();
                        // Convert byte[] to String (assuming UTF-8 encoding)
                        String contentString = (contentBytes != null) ? new String(contentBytes, StandardCharsets.UTF_8) : "";
                        files.put(fileName, contentString);
                    }
                }
                commitDetails.put("files", files);

                // Add commit to the list (newest commits first)
                commitsList.addFirst(commitDetails);

                // Move to the parent (assumes single parent for simplicity; adjust if multiple parents)
                ArrayList<Commit> parents = current.getParents();
                current = (parents != null && !parents.isEmpty()) ? parents.get(0) : null;
            }

            // Add the branch's commit list to the map
            branchCommits.put(branchName, commitsList);
        }

        // MongoDB Atlas URI
        String atlasUri = "mongodb+srv://Sehez3010:SHER1005@zoomclone.fswdm.mongodb.net/GitletClone?retryWrites=true&w=majority&appName=ZoomClone";

        try (MongoClient client = MongoClients.create(atlasUri)) {
            MongoDatabase db = client.getDatabase("GitletClone");
            MongoCollection<Document> collection = db.getCollection("repos");

            ObjectId repoObjectId = new ObjectId(repoId);

            // Check if repo exists
            boolean exists = collection.find(new Document("_id", repoObjectId)).iterator().hasNext();
            if (!exists) {
                System.out.println("❌ Repo ID does not exist in MongoDB.");
                return;
            }

            // Convert the branchCommits map to JSON
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(branchCommits);

            // Prepare BSON document
            Document commitFileDoc = new Document()
                    .append("filename", "commit_tree.json")
                    .append("contentType", "application/json")
                    .append("data", json);

            // Update the repo document with commitFile
            collection.updateOne(
                    new Document("_id", repoObjectId),
                    new Document("$set", new Document("commitFile", commitFileDoc))
            );

            System.out.println("✅ Commit JSON successfully pushed to MongoDB.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}