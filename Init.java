package gitlet;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/** Class containing Init command function.
 *  @author Daric Lim
 */
public class Init {
    /** Command function that initializes a new .gitlet directory. */
    public static void cmd() {
        if (GITLET_DIR.exists()) {
            System.out.println("A Gitlet version-control system "
                    + "already exists in the current directory.");
            System.exit(0);
        }

        GITLET_DIR.mkdir();
        STAGE_DIR.mkdir();
        COMMITS_DIR.mkdir();
        BLOBS_DIR.mkdir();
        BRANCHES_DIR.mkdir();
        REMOTE_DIR.mkdir();

        StringHashMap addMap = new StringHashMap();
        Utils.writeObject(ADD_TRACKER, addMap);

        StringHashMap rmMap = new StringHashMap();
        Utils.writeObject(RM_TRACKER, rmMap);

        Date dateInit = new Date(0);
        String formatString = "EEE MMM d HH:mm:ss yyyy Z";
        SimpleDateFormat dateFormat = new SimpleDateFormat(formatString);
        String initTimestamp = dateFormat.format(dateInit);

        Commit initCommit = new Commit("initial commit", null,
                                    initTimestamp, null, null);

        File masterBranch = Utils.join(BRANCHES_DIR, "master");

        Utils.writeContents(masterBranch, initCommit.getId());

        Utils.writeContents(HEAD, masterBranch.getPath());

        File savedInitCommit = Utils.join(COMMITS_DIR, initCommit.getId());
        Utils.writeObject(savedInitCommit, initCommit);
    }
    /** File pointer to CWD directory. */
    static final File CWD = new File(System.getProperty("user.dir"));
    /** File pointer to .gitlet directory. */
    static final File GITLET_DIR = Utils.join(CWD, ".gitlet");
    /** File pointer to Stage directory. */
    static final File STAGE_DIR = Utils.join(GITLET_DIR, "stage");
    /** File pointer to Commits directory. */
    static final File COMMITS_DIR = Utils.join(GITLET_DIR, "commits");
    /** File pointer to Blobs directory. */
    static final File BLOBS_DIR = Utils.join(GITLET_DIR, "blobs");
    /** File pointer to Branches directory. */
    static final File BRANCHES_DIR = Utils.join(GITLET_DIR, "branches");
    /** File pointer to file tracking stage files. */
    static final File ADD_TRACKER = Utils.join(GITLET_DIR, "addTracker");
    /** File pointer to file tracking files to remove. */
    static final File RM_TRACKER = Utils.join(GITLET_DIR, "rmTracker");
    /** File pointer to file tracking HEAD. */
    static final File HEAD = Utils.join(GITLET_DIR, "HEAD");
    /** File pointer to Remote Dirs Directory.*/
    static final File REMOTE_DIR = Utils.join(GITLET_DIR, "remotes");
}
