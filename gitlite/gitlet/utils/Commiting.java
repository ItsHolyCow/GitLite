package gitlet.utils;

import gitlet.tools.Commit;
import gitlet.tools.AllBranches;
import gitlet.tools.CurrentBranchName;
import gitlet.tools.StagingStore;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import gitlet.tools.MergedFrom;

import gitlet.tools.CommitSerializer;

public class Commiting {

    private static void clearStage() {
        File stage = new File("./.gitlet/StagingArea");
        File[] staged_files = stage.listFiles();
        if (staged_files != null) {
            for (File file : staged_files) {
                file.delete();
            }
        }
        StagingStore.setStaged_file(new HashMap<>());
        File temp_file = new File("./.gitlet/stage.ser");
        if(temp_file.exists())temp_file.delete();
    }

    public static void commit(String message) {
        // now at commit we should store the latest snapshot of all the files in a folder it will be helpful in many things
        File latestFiles = new File("./.gitlet/latestFiles");
        if(!latestFiles.exists())latestFiles.mkdir();
        Commit newcommit = new Commit();
        newcommit.setMessage(message);

        File staged_files = new File("./.gitlet/StagingArea");
        if (!staged_files.exists()) {
            System.out.println("Initialize the repo first");
            return;
        }
        ArrayList<byte[]> files = new ArrayList<>();
        File[] Sfiles = staged_files.listFiles();
        if(Sfiles.length == 0){
            System.out.println("Nothing to Commit");
            System.exit(0);
        }
        ArrayList<String> name = newcommit.getNames();
        Map<String,byte[]> temp = newcommit.getFiles_content();

        if (Sfiles != null) {
            for (File file : Sfiles) {
                try {
                    if (name == null) name = new ArrayList<>();
                    name.add(file.getName());
                    byte[] bytes = Files.readAllBytes(Path.of(file.getPath()));
                    temp.put(file.getName(),bytes);
                    files.add(bytes);
                    Path filePath = latestFiles.toPath().resolve(file.getName());
                    Files.write(filePath,bytes);
                    // copy the file in the latest area as well if not exists if exists override it
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        newcommit.setFiles(files);
        newcommit.setNames(name);
        newcommit.setFiles_content(temp);

        ArrayList<Commit> alleaves = AllBranches.getLeavesCommit();


        if (alleaves.isEmpty()) {
            alleaves.add(newcommit);
        } else {
            boolean updated = false;
            for (int i = 0; i < alleaves.size(); i++) {
                if (alleaves.get(i).getBranch_name().equals(CurrentBranchName.getBranchName())) {
                    alleaves.set(i, newcommit);
                    updated = true;
                    break;
                }
            }
            if (!updated) {
                alleaves.add(newcommit);
            }
        }
        ArrayList<Commit> merged = MergedFrom.getMergedFrom();
        ArrayList<Commit> parent = newcommit.getParents();
        for(Commit commit : merged){
            parent.add(commit);
        }
        newcommit.setParents(parent);
        AllBranches.setLeavesCommit(alleaves);

        // After commit, serialize the full commit tree
        CommitSerializer.serializeCommitTree();

        clearStage();
    }
}
