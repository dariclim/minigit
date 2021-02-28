package gitlet;

import java.io.File;
import java.util.List;

/** Class containing Checkout command function.
 *  @author Daric Lim
 */
public class Checkout {
    /** Checkouts FILENAME from the head commit, ignoring DASH. */
    public static void cmd(String dash, String filename) {
        if (!dash.equals("--")) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        }
        String headPath = Utils.readContentsAsString(HEAD);
        File headBranch = new File(headPath);

        String headCommitId = Utils.readContentsAsString(headBranch);
        checkoutFile(headCommitId, filename);
    }

    /** Checkouts FILENAME from the given COMMITID, ignoring DASH. */
    public static void cmd(String commitId, String dash, String filename) {
        if (!dash.equals("--")) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        }
        checkoutFile(commitId, filename);
    }

    /** Checkouts BRANCHNAME from the branch dir. */
    public static void cmd(String branchName) {
        File checkoutBranch = Utils.join(BRANCHES_DIR, branchName);
        File headBranch = new File(Utils.readContentsAsString(HEAD));

        if (!checkoutBranch.exists()) {
            System.out.println("No such branch exists.");
            System.exit(0);
        }
        if (checkoutBranch.equals(headBranch)) {
            System.out.println("No need to checkout the current branch.");
            System.exit(0);
        }

        String checkoutCommitId = Utils.readContentsAsString(checkoutBranch);
        File commitFile = Utils.join(COMMITS_DIR, checkoutCommitId);
        Commit commit = Utils.readObject(commitFile, Commit.class);
        StringHashMap commitBlobs = commit.getBlobs();

        String headCommitId = Utils.readContentsAsString(headBranch);
        File headCommitFile = Utils.join(COMMITS_DIR, headCommitId);
        Commit headCommit = Utils.readObject(headCommitFile, Commit.class);
        StringHashMap headCommitBlobs = headCommit.getBlobs();

        List<String> filesInCWD = Utils.plainFilenamesIn(CWD);
        for (String file: filesInCWD) {
            if (commitBlobs.containsKey(file)
                    && !headCommitBlobs.containsKey(file)) {
                System.out.println("There is an untracked file in the way; "
                        + "delete it, or add and commit it first");
                System.exit(0);
            }
        }

        for (String file: filesInCWD) {
            if (!commitBlobs.containsKey(file)) {
                File fileToDelete = Utils.join(CWD, file);
                fileToDelete.delete();
            }
        }

        for (String blobFilename: commitBlobs.keySet()) {
            File blob = Utils.join(BLOBS_DIR, commit.getBlobId(blobFilename));
            byte[] blobContents = Utils.readContents(blob);
            File fileInCWD = Utils.join(CWD, blobFilename);
            Utils.writeContents(fileInCWD, blobContents);
        }

        Utils.writeContents(HEAD, checkoutBranch.getPath());
    }

    /** Helper function that checkouts FILENAME from COMMITID. */
    public static void checkoutFile(String commitId, String filename) {
        File commitFile = Utils.join(COMMITS_DIR, commitId);
        boolean foundAbbrev = false;
        if (!commitFile.exists()) {
            List<String> filesInCommits = Utils.plainFilenamesIn(COMMITS_DIR);
            for (String filenameInCommit: filesInCommits) {
                if (filenameInCommit.substring(0,
                        commitId.length()).matches(commitId)) {
                    commitFile = Utils.join(COMMITS_DIR, filenameInCommit);
                    foundAbbrev = true;
                    break;
                }
            }
            if (!foundAbbrev) {
                System.out.println("No commit with that id exists.");
                System.exit(0);
            }
        }
        Commit commit = Utils.readObject(commitFile, Commit.class);
        String blobId = commit.getBlobId(filename);
        if (blobId == null) {
            System.out.println("File does not exist in that commit.");
            System.exit(0);
        }

        File blob = Utils.join(BLOBS_DIR, blobId);

        byte[] blobContent = Utils.readContents(blob);
        File fileToRewrite = Utils.join(CWD, filename);
        Utils.writeContents(fileToRewrite, blobContent);
    }

    /** File pointer to CWD directory. */
    static final File CWD = new File(System.getProperty("user.dir"));
    /** File pointer to .gitlet directory. */
    static final File GITLET_DIR = Utils.join(CWD, ".gitlet");
    /** File pointer to Commits directory. */
    static final File COMMITS_DIR = Utils.join(GITLET_DIR, "commits");
    /** File pointer to Blobs directory. */
    static final File BLOBS_DIR = Utils.join(GITLET_DIR, "blobs");
    /** File pointer to Branches directory. */
    static final File BRANCHES_DIR = Utils.join(GITLET_DIR, "branches");
    /** File pointer to file tracking HEAD. */
    static final File HEAD = Utils.join(GITLET_DIR, "HEAD");
}
