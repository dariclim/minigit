package gitlet;

import java.io.File;

/** Class containing Rm function.
 *  @author Daric Lim
 */
public class Rm {
    /** Command function that removes FILENAME from the working dir. */
    public static void cmd(String filename) {
        boolean inStage = false;
        boolean inHeadCommit = false;

        StringHashMap addMap = Utils.readObject(ADD_TRACKER,
                                       StringHashMap.class);

        if (addMap.containsKey(filename)) {
            File fileToRm = Utils.join(STAGE_DIR, filename);
            fileToRm.delete();
            inStage = true;
        }
        addMap.remove(filename);
        Utils.writeObject(ADD_TRACKER, addMap);

        String headPath = Utils.readContentsAsString(HEAD);
        File headBranch = new File(headPath);
        String headCommitId = Utils.readContentsAsString(headBranch);

        File headCommitFile = Utils.join(COMMITS_DIR, headCommitId);
        Commit headCommit = Utils.readObject(headCommitFile, Commit.class);
        if (headCommit.hasKey(filename)) {

            StringHashMap rmMap = Utils.readObject(RM_TRACKER,
                                         StringHashMap.class);
            rmMap.put(filename, filename);
            Utils.writeObject(RM_TRACKER, rmMap);
            File fileToRemove = new File(filename);
            fileToRemove.delete();
            inHeadCommit = true;
        }

        if (!inHeadCommit && !inStage) {
            System.out.println("No reason to remove the file.");
            System.exit(0);
        }
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
