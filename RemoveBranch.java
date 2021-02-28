package gitlet;

import java.io.File;

/** Class containing rm-branch command function.
 *  @author Daric Lim
 */
public class RemoveBranch {
    /** Command function that removes BRANCHNAME. */
    public static void cmd(String branchName) {
        File branchToDelete = Utils.join(BRANCHES_DIR, branchName);
        if (!branchToDelete.exists()) {
            System.out.println("A branch with that name does not exists.");
            System.exit(0);
        }

        File headBranch = new File(Utils.readContentsAsString(HEAD));
        if (headBranch.equals(branchToDelete)) {
            System.out.println("Cannot remove the current branch");
            System.exit(0);
        }

        branchToDelete.delete();
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
