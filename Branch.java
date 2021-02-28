package gitlet;

import java.io.File;

/** Class containing Branch command function.
 *  @author Daric Lim
 */
public class Branch {
    /** Command function that creates new branch BRANCHNAME in branch dir. */
    public static void cmd(String branchName) {
        File branchFile = Utils.join(BRANCHES_DIR, branchName);
        if (branchFile.exists()) {
            System.out.println("A branch with that name already exists.");
            System.exit(0);
        }
        String headPath = Utils.readContentsAsString(HEAD);
        File headBranch = new File(headPath);

        String headCommitId = Utils.readContentsAsString(headBranch);
        Utils.writeContents(branchFile, headCommitId);
    }
    /** File pointer to CWD directory. */
    static final File CWD = new File(System.getProperty("user.dir"));
    /** File pointer to .gitlet directory. */
    static final File GITLET_DIR = Utils.join(CWD, ".gitlet");
    /** File pointer to Branches directory. */
    static final File BRANCHES_DIR = Utils.join(GITLET_DIR, "branches");
    /** File pointer to file tracking HEAD. */
    static final File HEAD = Utils.join(GITLET_DIR, "HEAD");
}
