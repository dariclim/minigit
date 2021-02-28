package gitlet;

import java.io.File;
import java.util.List;

/** Class containing Add command function.
 *  @author Daric Lim
 */
public class Add {
    /** Command function that adds FILENAME to the stage.*/
    public static void cmd(String filename) {
        StringHashMap rmMap = Utils.readObject(RM_TRACKER, StringHashMap.class);
        if (rmMap.containsKey(filename)) {
            rmMap.remove(filename);
            Utils.writeObject(RM_TRACKER, rmMap);
            return;
        }

        File headBranch = new File(Utils.readContentsAsString(HEAD));
        String headCommitId = Utils.readContentsAsString(headBranch);
        File headCommitFile = Utils.join(COMMITS_DIR, headCommitId);
        Commit headCommit = Utils.readObject(headCommitFile, Commit.class);
        StringHashMap headCommitBlobs = headCommit.getBlobs();

        if (headCommitBlobs.containsKey(filename)) {
            String blobId = headCommit.getBlobId(filename);
            File addFile = Utils.join(CWD, filename);
            String addFileId = Utils.sha1(Utils.readContents(addFile));
            if (blobId.equals(addFileId)) {
                List<String> fileInStage = Utils.plainFilenamesIn(STAGE_DIR);
                if (fileInStage.contains(filename)) {
                    File fileToRm = Utils.join(STAGE_DIR, filename);
                    fileToRm.delete();
                }
                return;
            }
        }

        addToStage(filename);
    }

    /** Helper function to add FILENAME to stage.*/
    public static void addToStage(String filename) {
        File addFile = Utils.join(CWD, filename);
        if (!addFile.exists()) {
            System.out.println("File does not exist.");
            System.exit(0);
        }

        byte[] addFileContents = Utils.readContents(addFile);

        File copyAddFile = Utils.join(STAGE_DIR, filename);
        Utils.writeContents(copyAddFile, addFileContents);

        StringHashMap addMap = Utils.readObject(ADD_TRACKER,
                                        StringHashMap.class);

        addMap.put(filename, Utils.sha1(addFileContents));
        Utils.writeObject(ADD_TRACKER, addMap);
    }
    /** File pointer to CWD directory. */
    static final File CWD = new File(System.getProperty("user.dir"));
    /** File pointer to .gitlet directory. */
    static final File GITLET_DIR = Utils.join(CWD, ".gitlet");
    /** File pointer to Stage directory. */
    static final File STAGE_DIR = Utils.join(GITLET_DIR, "stage");
    /** File pointer to Commits directory. */
    static final File COMMITS_DIR = Utils.join(GITLET_DIR, "commits");
    /** File pointer to file tracking stage files. */
    static final File ADD_TRACKER = Utils.join(GITLET_DIR, "addTracker");
    /** File pointer to file tracking files to remove. */
    static final File RM_TRACKER = Utils.join(GITLET_DIR, "rmTracker");
    /** File pointer to file tracking HEAD. */
    static final File HEAD = Utils.join(GITLET_DIR, "HEAD");
}
