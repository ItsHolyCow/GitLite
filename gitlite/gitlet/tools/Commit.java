package gitlet.tools;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import gitlet.tools.CurrentBranchName;
import gitlet.tools.AllBranches;
public class Commit implements Serializable{
    private String Message;


    private String id;
    private String branch_name;
    // file array
    private ArrayList<byte[]> files = new ArrayList<byte[]>();
    private ArrayList<Commit> parents = new ArrayList<Commit>();
    private ArrayList<String> names = new ArrayList<>();
    private Map<String,byte[]> files_content= new HashMap<String,byte[]>();

    public Map<String,byte[]> getFiles_content() {
        return files_content;
    }

    public void setFiles_content(Map<String,byte[]> files_content) {
        this.files_content = files_content;
    }


    public ArrayList<String> getNames() {
        return names;
    }

    public void setNames(ArrayList<String> names) {
        this.names = names;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBranch_name() {
        return branch_name;
    }

    public void setBranch_name(String branch_name) {
        this.branch_name = branch_name;
    }

    public ArrayList<byte[]> getFiles() {
        return files;
    }

    public void setFiles(ArrayList<byte[]> files) {
        this.files = files;
    }

    public ArrayList<Commit> getParents() {
        return parents;
    }

    public void setParents(ArrayList<Commit> parents) {
        this.parents = parents;
    }

    // parents array

    // initialising parameters

    public Commit() {
        this.id = UUID.randomUUID().toString();
        this.branch_name = CurrentBranchName.getBranchName();
        ArrayList<Commit> allLeaves = AllBranches.getLeavesCommit();
        this.files = null;
        if(allLeaves.isEmpty()){
            this.parents = null;
        }
        else {
            for (Commit commits : allLeaves) {
                if (commits.getBranch_name().equals(CurrentBranchName.getBranchName())) {
                    parents.add(commits);
                    break;
                }
            }
        }
    }
}