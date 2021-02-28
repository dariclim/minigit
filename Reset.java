package gitlet;

import java.io.File;
import java.util.List;

/** Class containing Reset command function.
 *  @author Daric Lim
 */
public class Reset {
    /** Command function that resets the working dir to COMMITID. */
    public static void cmd(String commitId) {
        File headBranch = new File(Utils.readContentsAsString(HEAD));
        String headCommitId = Utils.readContentsAsString(headBranch);
        File headCommitFile = Utils.join(COMMITS_DIR, headCommitId);

        Commit headCommit = Utils.readObject(headCommitFile, Commit.class);
        StringHashMap headCommitBlobs = headCommit.getBlobs();

        File resetCommitFile = Utils.join(COMMITS_DIR, commitId);
        if (!resetCommitFile.exists()) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }

        Commit resetCommit = Utils.readObject(resetCommitFile, Commit.class);
        StringHashMap resetCommitBlobs = resetCommit.getBlobs();

        List<String> filesInCWD = Utils.plainFilenamesIn(CWD);
        for (String file: filesInCWD) {
            if (resetCommitBlobs.containsKey(file)
                    && !headCommitBlobs.containsKey(file)) {
                System.out.println("There is an untracked file in the way;"
                        + " delete it, or add and commit it first.");
                System.exit(0);
            }
        }

        for (String filename: headCommitBlobs.keySet()) {
            if (!resetCommitBlobs.containsKey(filename)) {
                File fileToDelete = Utils.join(CWD, filename);
                fileToDelete.delete();
            }
        }

        for (String filename: resetCommitBlobs.keySet()) {
            Checkout.checkoutFile(commitId, filename);
        }

        File[] files = STAGE_DIR.listFiles();
        for (File file: files) {
            file.delete();
        }
        Utils.writeObject(RM_TRACKER, new StringHashMap());
        Utils.writeContents(headBranch, commitId);
    }
    /** File pointer to CWD directory. */
    static final File CWD = new File(System.getProperty("user.dir"));
    /** File pointer to .gitlet directory. */
    static final File GITLET_DIR = Utils.join(CWD, ".gitlet");
    /** File pointer to Stage directory. */
    static final File STAGE_DIR = Utils.join(GITLET_DIR, "stage");
    /** File pointer to Commits directory. */
    static final File COMMITS_DIR = Utils.join(GITLET_DIR, "commits");
    /** File pointer to file tracking files to remove. */
    static final File RM_TRACKER = Utils.join(GITLET_DIR, "rmTracker");
    /** File pointer to file tracking HEAD. */
    static final File HEAD = Utils.join(GITLET_DIR, "HEAD");
}
