package gitlet;
import gitlet.utils.Initialise;
import gitlet.utils.Status;
import gitlet.utils.Add;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Currency;

import gitlet.tools.Commit;
import gitlet.tools.AllBranches;
import gitlet.tools.CurrentBranchName;
import gitlet.utils.Commiting;
import gitlet.utils.Logging;
import gitlet.utils.Checkout;
import gitlet.utils.Pulling;
import gitlet.utils.Merging;
import gitlet.utils.Pushing;

public class Main {
    public static void main(String[] args) throws IOException {
        if(args.length == 0) System.out.println("Please Enter a valid command");
        String inp = args[0];
        switch (inp){
            case "init":
                Initialise.init();
                break;
            case "status":
                Status.getStatus();
                break;
            case "add":
                if (args.length != 2) {
                    System.out.println("Bad Argument: Filename missing for 'add' command");
                    return;
                }
                Add.add(args[1]);
                break;
            case "branch":
                if(args.length == 1){
                    // we will be displaying all the branches here and currently active branch also
                    ArrayList<Commit> allLeaves = AllBranches.getLeavesCommit();
                    if(allLeaves.isEmpty()) System.out.println("main*");
                    int k = 0;
                    for(Commit commits : allLeaves){
                        if(CurrentBranchName.getBranchName().equals(commits.getBranch_name())) {
                            System.out.println(commits.getBranch_name() + "*");
                            k = 1;
                        }
                        else System.out.println(commits.getBranch_name());
                    }
                    if(k == 0) System.out.println(CurrentBranchName.getBranchName());
                }
                else if(args.length>2){
                    System.out.println("Bad argumnent ");
                }
                else{
                    String Bname = args[1];
                    // check if the name exists
                    ArrayList<Commit> allLeaves = AllBranches.getLeavesCommit();
                    for(Commit commits : allLeaves){
                        if(Bname.equals(commits.getBranch_name())) {
                            System.out.println("branch with this name exists");
                            System.exit(0);
                        }
                    }
                    CurrentBranchName.setBranchName(Bname);
                }
                break;
            case "commit":
                if(args.length == 1){
                    System.out.println("Please Enter a Message");
                    System.exit(0);
                }
                else{
                    String message = "";
                    for(int i = 1;i< args.length;i++){
                        message += args[i];
                    }
                    Commiting.commit(message);
                }
                break;
            case "log":
                Logging.log();
                break;
            case "checkout":
                Checkout.checkout(args[1]);
                break;
            case "pull":
                // we will be needing a commit id as second argumnet here
                if(args.length != 2) System.out.println("Pleases Enter a valid commit id");
                Pulling.pull(args[1]);
                break;
            case "merge":
                if(args.length != 2) System.out.println(" Please Enter a valid branch name ");
                Merging.merge(args[1]);
                break;
            case "push":
                String repoId = args[1];
                Pushing.push(repoId);
                break;
            default:
                System.out.println("Please Enter a valid command");
        }
    }
}
