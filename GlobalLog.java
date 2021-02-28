package gitlet;

import java.io.File;

/** Class containing global-log command function.
 * @author Daric Lim
 */
public class GlobalLog {
    /** Command function that prints out global log. */
    public static void cmd() {
        File[] files = COMMITS_DIR.listFiles();
        for (File file: files) {
            Commit currCommit = Utils.readObject(file, Commit.class);
            LogFormat.printLog(currCommit);
        }
    }
    /** File pointer to CWD directory. */
    static final File CWD = new File(System.getProperty("user.dir"));
    /** File pointer to .gitlet directory. */
    static final File GITLET_DIR = Utils.join(CWD, ".gitlet");
    /** File pointer to Commits directory. */
    static final File COMMITS_DIR = Utils.join(GITLET_DIR, "commits");
    /** File pointer to Blobs directory. */
}
