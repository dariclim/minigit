package gitlet;

import java.io.File;

/** Class containing Log command function.
 * @author Daric Lim
 */
public class Log {
    /** Command function that prints out the log. */
    public static void cmd() {
        String headPath = Utils.readContentsAsString(HEAD);
        File headBranch = new File(headPath);

        String headCommitId = Utils.readContentsAsString(headBranch);

        File headCommitFile = Utils.join(COMMITS_DIR, headCommitId);
        Commit currCommit = Utils.readObject(headCommitFile, Commit.class);

        while (currCommit.getParent() != null) {
            LogFormat.printLog(currCommit);
            String parentCommitId = currCommit.getParent();
            File parentCommitFile = Utils.join(COMMITS_DIR, parentCommitId);
            currCommit = Utils.readObject(parentCommitFile, Commit.class);
        }
        LogFormat.printLog(currCommit);
    }
    /** File pointer to CWD directory. */
    static final File CWD = new File(System.getProperty("user.dir"));
    /** File pointer to .gitlet directory. */
    static final File GITLET_DIR = Utils.join(CWD, ".gitlet");
    /** File pointer to Commits directory. */
    static final File COMMITS_DIR = Utils.join(GITLET_DIR, "commits");
    /** File pointer to file tracking HEAD. */
    static final File HEAD = Utils.join(GITLET_DIR, "HEAD");
}
