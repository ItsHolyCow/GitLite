package gitlet.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

import gitlet.tools.Encode;

import gitlet.tools.GitIgnoreSet;
import gitlet.tools.Commit;
import gitlet.tools.AllBranches;
import gitlet.tools.CurrentBranchName;

public class Status {
    public static void getStatus() throws IOException {
        ArrayList<String> UntackedFiles = new ArrayList<>();
        ArrayList<String> ModifiedFiles = new ArrayList<>();
        Set<String> deletedFiles = new HashSet<>();

        File Staged = new File("./.gitlet/StagingArea");
        File[] FilesStaged = Staged.listFiles();

        File[] WorkingDir = new File(System.getProperty("user.dir")).listFiles();

        File[] LatestFiles = new File("./.gitlet/latestFiles").listFiles();


        System.out.println("==========================> Staging Area <==========================");
        if (FilesStaged != null) {
            for (File file : FilesStaged) {
                System.out.println(file.getName());
            }
        }
        System.out.println("==========================> Untracked Files <==========================");
        // files that are not in the latest commit and not in the staging Area but are in the main directory
        for(File file : WorkingDir){
            GitIgnoreSet temp = new GitIgnoreSet();
            if(temp.contains(file.getName())){
                continue;
            }
            UntackedFiles.add(file.getName());
        }
        // removing files in stagin directory
        if(FilesStaged != null) {
            for (File file : FilesStaged) {
                UntackedFiles.remove(file.getName());
            }
        }
        if(LatestFiles != null) {
            for (File file : LatestFiles) {
                UntackedFiles.remove(file.getName());
            }
        }
        if(!UntackedFiles.isEmpty()) {
            for (String name : UntackedFiles) {
                System.out.println(name);
            }
        }
        System.out.println("==========================> Modified Files <==========================");
        // get all the files that are modified
        Map<String,String> latestHash = new HashMap<String,String>();
        if(LatestFiles != null) {
            for (File file : LatestFiles) {
                String hash = Encode.sha1(file);
                latestHash.put(file.getName(), hash);
            }
        }
        Map<String,String> workingHash = new HashMap<String,String>();
        GitIgnoreSet temp = new GitIgnoreSet();
        if(WorkingDir != null) {
            for (File file : WorkingDir) {
                if(temp.contains(file.getName()))continue;
                String hash = Encode.sha1(file);
                workingHash.put(file.getName(), hash);
            }
        }
        if(latestHash != null) {
            for (String key : latestHash.keySet()) {
                if (workingHash.containsKey(key)) {
                    if (!latestHash.get(key).equals(workingHash.get(key))) ModifiedFiles.add(key);
                }
            }
        }
        if(!ModifiedFiles.isEmpty()) {
            for (String name : ModifiedFiles) {
                System.out.println(name);
            }
        }
        System.out.println("==========================> Deleted Files <==========================");
        File[] userDir = new File(System.getProperty("user.dir")).listFiles();


        Commit temp_commit = null;
        ArrayList<Commit> allleaves = AllBranches.getLeavesCommit();
        for(Commit commit : allleaves){
            if(commit.getBranch_name().equals(CurrentBranchName.getBranchName()))temp_commit = commit;
        }
        while(temp_commit != null){
            ArrayList<String> names = temp_commit.getNames();
            for(String name : names){
                deletedFiles.add(name);
            }
            if(temp_commit.getParents() == null || temp_commit.getParents().isEmpty())temp_commit = null;
            else temp_commit = temp_commit.getParents().get(0);
        }
        for(File file : userDir){
            if(temp.contains(file.getName()))continue;
            deletedFiles.remove(file.getName());
        }
        if(deletedFiles!=null) {
            for (String name : deletedFiles) {
                System.out.println(name);
            }
        }
    }
}
