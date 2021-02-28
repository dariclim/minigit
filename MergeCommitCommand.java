package gitlet;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/** Class containing Commit Command but modified for merge commits.
 *  @author Daric Lim
 */
public class MergeCommitCommand {
    /** Command function for making merge commits
     *  from parent commits PARENT1 and PARENT2.
     *  Like regular commit, takes in commit's MESSAGE. */
    public static void cmd(String message, String parent1, String parent2) {
        if (message.length() == 0) {
            System.out.println("Please enter a commit message.");
            System.exit(0);
        }

        String headPath = Utils.readContentsAsString(HEAD);

        File headBranch = new File(headPath);
        Date dateNow = new Date();
        String formatString = "EEE MMM d HH:mm:ss yyyy Z";
        SimpleDateFormat dateFormat = new SimpleDateFormat(formatString);
        String timestamp = dateFormat.format(dateNow);

        StringHashMap addToBlob = new StringHashMap();

        StringHashMap addMap = Utils.readObject(ADD_TRACKER,
                                       StringHashMap.class);
        for (String key: addMap.keySet()) {
            File fileToAddBlob = Utils.join(STAGE_DIR, key);
            byte[] blob = Utils.readContents(fileToAddBlob);
            String blobId = Utils.sha1(blob);
            File newBlob = Utils.join(BLOBS_DIR, blobId);
            Utils.writeContents(newBlob, blob);
            addToBlob.put(key, blobId);
        }

        StringHashMap rmToBlob = Utils.readObject(RM_TRACKER,
                                        StringHashMap.class);

        Commit newCommit = new Commit(message, parent1, parent2,
                                timestamp, addToBlob, rmToBlob);

        File savedNewCommit = Utils.join(COMMITS_DIR, newCommit.getId());
        Utils.writeObject(savedNewCommit, newCommit);

        Utils.writeContents(headBranch, newCommit.getId());

        File[] files = STAGE_DIR.listFiles();
        for (File file: files) {
            file.delete();
        }

        Utils.writeObject(ADD_TRACKER, new StringHashMap());
        Utils.writeObject(RM_TRACKER, new StringHashMap());
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
    /** File pointer to file tracking stage files. */
    static final File ADD_TRACKER = Utils.join(GITLET_DIR, "addTracker");
    /** File pointer to file tracking files to remove. */
    static final File RM_TRACKER = Utils.join(GITLET_DIR, "rmTracker");
    /** File pointer to file tracking HEAD. */
    static final File HEAD = Utils.join(GITLET_DIR, "HEAD");
}
