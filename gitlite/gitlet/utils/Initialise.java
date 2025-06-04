package gitlet.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import gitlet.tools.CurrentBranchName;

public class Initialise {
    public static void write_gitletignore(){
//        File gitIgnore = new File("./gitlet/.gitletignore");
            ArrayList<String> ignoreList = new ArrayList<>(Arrays.asList(
                    ".DS_Store",
                    "Thumbs.db",
                    "*.log",
                    ".vscode",
                    ".idea",
                    "*.iml",
                    "/bin",
                    "/build",
                    "/out",
                    ".classpath",
                    ".project",
                    ".settings",
                    "/target",
                    "/.gradle",
                    "/*.jar",
                    "/*.war",
                    "/*.ear",
                    "__pycache__",
                    "/*.pyc",
                    "*.egg-info",
                    "/dist",
                    "/.env",
                    "*.sqlite3",
                    "/node_modules",
                    "package-lock.json",
                    "npm-debug.log*",
                    ".gitlet",
                    "*.tmp",
                    "*.swp",
                    "*.bak",
                    "*.cache",
                    ".git",
                    "gitlet",
                    "lib",
                    "out"
            ));
        try {
            FileWriter writer = new FileWriter("./.gitlet/.gitletignore");
            for(String s : ignoreList){
                writer.write(s+"\n");
            }
            writer.close();
        } catch (IOException e) {
            System.out.println("Repo not initialised");
            System.exit(0);
        }

    }
    public static void init(){
        File[] files_demo = new File(System.getProperty("user.dir")).listFiles();
        for(File file : files_demo){
            if(file.getName().equals(".gitlet")){
                System.out.println("Reintalised a repo here");
                file.delete();
            }
        }
        CurrentBranchName.setBranchName("main");
        File hidden_gitlet = new File("./.gitlet");
        File commits = new File("./.gitlet/commits");
        File StagingArea = new File("./.gitlet/StagingArea");
        File Branches = new File("./.gitlet/Branches");
        Branches.mkdir();
        hidden_gitlet.mkdir();
        commits.mkdir();
        StagingArea.mkdir();
        File gitIgnore = new File("./.gitlet/.gitletignore");
        try {
            gitIgnore.createNewFile();
            write_gitletignore();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
