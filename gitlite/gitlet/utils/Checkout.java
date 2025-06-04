package gitlet.utils;
import gitlet.tools.CurrentBranchName;
import gitlet.tools.AllBranches;
import gitlet.tools.Commit;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;

public class Checkout {
    private static void cleanFiles() throws IOException {
        File[] files = new File(System.getProperty("user.dir")).listFiles();
        for(File file : files){
            byte[] bytes = Files.readAllBytes(Path.of("./.gitlet/.gitletIgnore"));
            String cont = new String(bytes);
            String[] lines = cont.split("\\R");
            for(String line : lines){
                if(line.equals(file.getName())){
                    break;
                }
            }
            file.delete();
        }
    }
    public static void checkout(String Bname) throws IOException {
        CurrentBranchName.setBranchName(Bname);
        // first thing that we will be doing here is we will be trying to get the commit of new branch
        ArrayList<Commit> arr = AllBranches.getLeavesCommit();
        Commit temp = null;
        for(Commit commit : arr){
            if(commit.getBranch_name().equals(Bname)){
                temp = commit;
                break;
            }
        }
        // for now we have got the commit that we needed
        // we will be Traversing back in the commit till the root inorder to get all the files in that commit
        // we will just copy those files from the latest files only

        // before all this we will have to clean our working directory
        cleanFiles();
        Set<String> filesCreated = new HashSet<>();
        String userHome = System.getProperty("user.dir");

        while (temp != null) {
            ArrayList<String> fileNames = temp.getNames();
            for (String name : fileNames) {
                if (filesCreated.contains(name)) continue;
                filesCreated.add(name);

                // Source file in .gitlet/latestFiles
                File sourceFile = new File("./.gitlet/latestFiles/" + name);

                // Destination file in user's home directory
                File destFile = new File(userHome + "/" + name);
                // Ensure the parent directory exists
                destFile.getParentFile().mkdirs();

                // Copy file contents
                Files.copy(sourceFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }

            if (temp.getParents() == null || temp.getParents().isEmpty())
                temp = null;
            else
                temp = temp.getParents().get(0);
        }
    }
}
