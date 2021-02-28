package gitlet;

import java.io.File;
import java.util.HashMap;
import java.util.List;

/** Class containing Merge command function.
 *  @author Daric Lim
 */
public class Merge {
    /** Command function that merges head branch with BRANCHNAME. */
    public static void cmd(String branchName) {
        mergeConflict = false;
        File givenBranch = Utils.join(BRANCHES_DIR, branchName);
        if (!givenBranch.exists()) {
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        }
        if (stageFilesPresent()) {
            System.out.println("You have uncommitted changes.");
            System.exit(0);
        }
        String givenCommitId = Utils.readContentsAsString(givenBranch);
        Commit givenCommit = getCommit(givenCommitId);

        File headBranch = new File(Utils.readContentsAsString(HEAD));
        String headCommitId = Utils.readContentsAsString(headBranch);
        Commit headCommit = getCommit(headCommitId);

        checkErrors(headCommit, givenBranch);

        String splitPointCommitId = findSplitPoint(headCommitId, givenCommitId);
        checkSplitPoint(splitPointCommitId, headCommit, givenCommit);

        File splitPointCommitFile = Utils.join(COMMITS_DIR, splitPointCommitId);
        Commit splitPointCommit = Utils.readObject(splitPointCommitFile,
                                                          Commit.class);
        mergeForSplitBlobs(splitPointCommit, headCommit, givenCommit);
        mergeForNotSplitBlobs(splitPointCommit, headCommit, givenCommit);

        String message = "Merged " + branchName + " into "
                             + headBranch.getName() + ".";
        MergeCommitCommand.cmd(message, headCommitId, givenCommitId);
        if (mergeConflict) {
            System.out.println("Encountered a merge conflict.");
        }
    }

    /** Returns commit based on COMMITID. */
    public static Commit getCommit(String commitId) {
        File commitFile = Utils.join(COMMITS_DIR, commitId);
        Commit commit = Utils.readObject(commitFile, Commit.class);
        return commit;
    }

    /** Checks for any errors based on GIVENBRANCH and HEADCOMMIT. */
    public static void checkErrors(Commit headCommit, File givenBranch) {
        File headBranch = new File(Utils.readContentsAsString(HEAD));
        List<String> filesInCWD = Utils.plainFilenamesIn(CWD);
        StringHashMap headCommitBlobs = headCommit.getBlobs();
        for (String fileInCWD: filesInCWD) {
            if (!headCommitBlobs.containsKey(fileInCWD)) {
                System.out.println("There is an untracked file in the way; "
                        + "delete it, or add and commit it first.");
                System.exit(0);
            }
        }
        if (headBranch.equals(givenBranch)) {
            System.out.println("Cannot merge a branch with itself.");
            System.exit(0);
        }
    }

    /** Checks for any file changes for files not tracked by SPLITCOMMIT,
     *  but are either present in HEADCOMMIT, GIVENCOMMIT, or both. */
    public static void mergeForNotSplitBlobs(Commit splitCommit,
                                             Commit headCommit,
                                             Commit givenCommit) {
        for (String blobKey: givenCommit.getBlobs().keySet()) {
            if (splitCommit.getBlobId(blobKey) == null
                    && headCommit.getBlobId(blobKey) == null) {
                Checkout.checkoutFile(givenCommit.getId(), blobKey);
                Add.addToStage(blobKey);
            }

            if (splitCommit.getBlobId(blobKey) == null
                    && headCommit.getBlobId(blobKey) != null
                    && headCommit.getBlobId(blobKey)
                    != givenCommit.getBlobId(blobKey)) {
                mergeConflict = true;
                modifyFile(blobKey, headCommit.getBlobId(blobKey),
                        givenCommit.getBlobId(blobKey));
            }
        }
    }

    /** Checks for any file changes for files tracked by SPLITCOMMIT,
     *  and are either present in HEADCOMMIT, GIVENCOMMIT, or both. */
    public static void mergeForSplitBlobs(Commit splitCommit,
                                          Commit headCommit,
                                          Commit givenCommit) {
        for (String splitBlobKey: splitCommit.getBlobs().keySet()) {
            String splitBlobId = splitCommit.getBlobId(splitBlobKey);
            String headBlobId = headCommit.getBlobId(splitBlobKey);
            String givenBlobId = givenCommit.getBlobId(splitBlobKey);
            if (givenBlobId == null && headBlobId == null) {
                continue;
            } else if (givenBlobId == null && headBlobId != null) {
                if (splitBlobId.equals(headBlobId)) {
                    Rm.cmd(splitBlobKey);
                } else {
                    mergeConflict = true;
                    modifyFile(splitBlobKey, headBlobId, givenBlobId);
                }
            } else if (givenBlobId != null && headBlobId == null) {
                if (splitBlobId.equals(givenBlobId)) {
                    continue;
                } else {
                    mergeConflict = true;
                    modifyFile(splitBlobKey, headBlobId, givenBlobId);
                }
            } else if (splitBlobId.equals(headBlobId)
                    && !splitBlobId.equals(givenBlobId)) {
                Checkout.checkoutFile(givenCommit.getId(), splitBlobKey);
                Add.addToStage(splitBlobKey);
            } else if (!splitBlobId.equals(headBlobId)
                    && !splitBlobId.equals(givenBlobId)
                    && (!headBlobId.equals(givenBlobId))) {
                mergeConflict = true;
                modifyFile(splitBlobKey, headBlobId, givenBlobId);
            }
        }
    }

    /** Checks if splitpoint SPLITID equals HEAD commit or GIVEN commit. */
    public static void checkSplitPoint(String splitId,
                                       Commit head,
                                       Commit given) {
        StringHashMap headCommitBlobs = head.getBlobs();
        if (splitId.equals(given.getId())) {
            System.out.println("Given branch is an ancestor "
                    + "of the current branch.");
            System.exit(0);
        }
        if (splitId.equals(head.getId())) {
            StringHashMap givenCommitBlobs = given.getBlobs();
            for (String filename: givenCommitBlobs.keySet()) {
                Checkout.checkoutFile(given.getId(), filename);
            }
            for (String fileToDelete: headCommitBlobs.keySet()) {
                if (!givenCommitBlobs.containsKey(fileToDelete)) {
                    Rm.cmd(fileToDelete);
                }
            }
            System.out.println("Current branch fast-forwarded");
            System.exit(0);
        }
    }


    /** Modify conflicted file FILENAME adding contents from
     *  HEADBLOBID and GIVENBLOBID. */
    public static void modifyFile(String filename,
                                  String headBlobId,
                                  String givenBlobId) {
        String fileStart = "<<<<<<< HEAD\n";
        String headGivenDivider = "=======\n";
        String fileEnd = ">>>>>>>\n";

        if (headBlobId == null) {
            File givenBlob = Utils.join(BLOBS_DIR, givenBlobId);
            byte[] givenContent = Utils.readContents(givenBlob);
            String headContent = "";

            File fileToRewrite = Utils.join(CWD, filename);
            Utils.writeContents(fileToRewrite, fileStart, headContent,
                    headGivenDivider, givenContent, fileEnd);
        } else if (givenBlobId == null) {
            File headBlob = Utils.join(BLOBS_DIR, headBlobId);
            byte[] headContent = Utils.readContents(headBlob);
            String givenContent = "";

            File fileToRewrite = Utils.join(CWD, filename);
            Utils.writeContents(fileToRewrite, fileStart, headContent,
                    headGivenDivider, givenContent, fileEnd);
        } else {
            File givenBlob = Utils.join(BLOBS_DIR, givenBlobId);
            byte[] givenContent = Utils.readContents(givenBlob);
            File headBlob = Utils.join(BLOBS_DIR, headBlobId);
            byte[] headContent = Utils.readContents(headBlob);

            File fileToRewrite = Utils.join(CWD, filename);

            Utils.writeContents(fileToRewrite, fileStart, headContent,
                    headGivenDivider, givenContent, fileEnd);
        }
        Add.addToStage(filename);
    }

    /** Returns true if there are files in stage. */
    public static boolean stageFilesPresent() {
        List<String> filesInStage = Utils.plainFilenamesIn(STAGE_DIR);
        if (!filesInStage.isEmpty()) {
            return true;
        }
        StringHashMap rmMap = Utils.readObject(RM_TRACKER,
                                     StringHashMap.class);
        if (!rmMap.isEmpty()) {
            return true;
        }
        return false;
    }

    /** Find the latest common ancestor between
     *  HEADCOMMITID and GIVENCOMMITID.
     *  @return the splitPoint in String Id. */
    public static String findSplitPoint(String headCommitId,
                                        String givenCommitId) {
        HashMap<String, Integer> headMap = new HashMap<>();
        HashMap<String, Integer> givenMap = new HashMap<>();

        branchHashMap(headMap, headCommitId, 0);
        branchHashMap(givenMap, givenCommitId, 0);

        String currClosest = "";
        int currDist = 100;

        for (String commitKey: headMap.keySet()) {
            if (givenMap.containsKey(commitKey)
                    && headMap.get(commitKey) < currDist) {
                currClosest = commitKey;
                currDist = headMap.get(commitKey);
            }
        }
        return currClosest;
    }

    /** Helper function, making a MAP for COMMITID
     * and take in a DISTFROMHEAD. */
    public static void branchHashMap(HashMap<String, Integer> map,
                                     String commitId, int distFromHead) {
        if (map.containsKey(commitId)) {
            if (map.get(commitId) < distFromHead) {
                return;
            }
        }
        map.put(commitId, distFromHead);

        File commitFile = Utils.join(COMMITS_DIR, commitId);
        Commit commit = Utils.readObject(commitFile, Commit.class);
        if (commit.isMerge()) {
            branchHashMap(map, commit.getParent1(), distFromHead + 1);
            branchHashMap(map, commit.getParent2(), distFromHead + 1);
        } else if (commit.getParent() != null) {
            branchHashMap(map, commit.getParent(), distFromHead + 1);
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
    /** File pointer to Blobs directory. */
    static final File BLOBS_DIR = Utils.join(GITLET_DIR, "blobs");
    /** File pointer to Branches directory. */
    static final File BRANCHES_DIR = Utils.join(GITLET_DIR, "branches");
    /** File pointer to file tracking files to remove. */
    static final File RM_TRACKER = Utils.join(GITLET_DIR, "rmTracker");
    /** File pointer to file tracking HEAD. */
    static final File HEAD = Utils.join(GITLET_DIR, "HEAD");
    /** Indicates whether current merge attempt produces conflict. */
    private static boolean mergeConflict = false;
}
