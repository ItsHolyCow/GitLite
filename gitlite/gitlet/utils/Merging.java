package gitlet.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import gitlet.tools.AllBranches;
import gitlet.tools.Commit;
import gitlet.tools.MergedFrom;
import gitlet.tools.Encode;

public class Merging {
    public static void merge(String mergerName) throws IOException {
        // 1. first we will be checking if the branch exists or is not equal to the current branch name
        if(mergerName.equals(gitlet.tools.CurrentBranchName.getBranchName())) System.out.println(" You cant merge into this branc");
        ArrayList<Commit> commits = AllBranches.getLeavesCommit();
        if(commits == null || commits.isEmpty()) System.out.println("Cant merge right now");
        Commit temp = null;
        for(Commit commit : commits){
            if(commit.getBranch_name().equals(mergerName)){
                temp = commit;
            }
        }
        if(temp == null){
            System.out.println("Cant merge this branch");
            System.exit(0);
        }
        // 2. now we have the commit that we will be merging from now we will be pushing it into the branched from array or class so that we can serailase and deserailze it for the next commit
        ArrayList<Commit> temp_mergedFrom = MergedFrom.getMergedFrom();
        temp_mergedFrom.add(temp);
        MergedFrom.setMergedFrom(temp_mergedFrom);
        MergedFrom.save();
        // 3. now we will be bringing all the files from that branch last commit to the current working Direcotry also if the files are same and there is no conflict then aslo we will not do anything
        File[] workingDir = new File(System.getProperty("user.dir")).listFiles();
        Map<String,byte[]> FilesFromMerged = new HashMap<>();
        while(temp!=null){
            Map<String,byte[]> FileContent = temp.getFiles_content();
            for(String name : FileContent.keySet()) {
                if(!FilesFromMerged.containsKey(name))FilesFromMerged.put(name,FileContent.get(name));
            }
            if(temp.getParents() == null || temp.getParents().isEmpty())temp = null;
            else temp = temp.getParents().get(0);
        }
        for(String name : FilesFromMerged.keySet()){
            boolean isPresent = false;
            for(File file : workingDir){
                if(file.getName().equals(name)){
                    isPresent = true;
                    String hash_FromMerger = Encode.sha1(FilesFromMerged.get(name));
                    byte[] cont = Files.readAllBytes(Path.of(file.getPath()));
                    String hash_working = Encode.sha1(cont);
                    if(hash_working.equals(hash_FromMerger))continue;
                    try(BufferedWriter bw = new BufferedWriter(new FileWriter(file,true))){
                        bw.newLine();
                        bw.write("============================== Content From ============================== " + mergerName);
                        bw.newLine();
                        bw.write(new String(FilesFromMerged.get(name)));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            if(isPresent == false){
                File newfile = new File(System.getProperty("user.dir") + "/" + name);
                newfile.createNewFile();
                byte[] content = FilesFromMerged.get(name);
                try(BufferedWriter bw = new BufferedWriter(new FileWriter(newfile))){
                    bw.write(new String(content));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
