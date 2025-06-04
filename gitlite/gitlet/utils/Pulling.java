package gitlet.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import gitlet.tools.Commit;
import gitlet.tools.AllBranches;
import gitlet.tools.GitIgnoreSet;
import gitlet.tools.CurrentBranchName;

public class Pulling {
    public static void pull(String id) throws IOException {
        ArrayList<Commit> commits = AllBranches.getLeavesCommit();
        Commit pulledCommit = null;
        for(Commit temp : commits){
            if(temp.getBranch_name().equals(CurrentBranchName.getBranchName())){
                pulledCommit = temp;
                break;
            }
        }
        // now i have to pull the snapshots of the files from that commit and till the root
        Map<String,byte[]> files = new HashMap<>();
        boolean id_found = false;
        while(pulledCommit != null){
            if(pulledCommit.getId().equals(id))id_found = true;
            if(id_found) {
                Map<String, byte[]> temp = pulledCommit.getFiles_content();
                for (String key : temp.keySet()) {
                    if (!files.containsKey(key)) files.put(key, temp.get(key));
                }
            }
            ArrayList<Commit> parents = pulledCommit.getParents();
            if (parents == null || parents.isEmpty()) pulledCommit = null;
            else pulledCommit = parents.get(0);
        }
        // now we have the map of files that we will e putting in out working directoy
        File[] workingDir = new File(System.getProperty("user.dir")).listFiles();
        for(File file : workingDir){
            GitIgnoreSet temp = new GitIgnoreSet();
            if(temp.contains(file.getName()))continue;
            file.delete();
        }
        // this will write the file in that commit
        for(String key : files.keySet()){
            Path workingPath = Path.of(System.getProperty("user.dir"));
            Path filePath = workingPath.resolve(key);
            Files.write(filePath,files.get(key),StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        }
    }
}
