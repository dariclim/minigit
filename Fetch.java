package gitlet;

import java.io.File;

/** Class containing fetch command function.
 *  @author Daric Lim
 */
public class Fetch {
    /** Command function that fetches REMOTEBRANCH from REMOTENAME. */
    public static void cmd(String remoteName, String remoteBranch) {
        File remotePath = Utils.join(REMOTE_DIR, remoteName);
        if (!remotePath.exists()) {
            System.out.println("Remote directory not found.");
            System.exit(0);
        }
        String remotePathStr = Utils.readContentsAsString(remotePath);
        File remoteDir = new File(remotePathStr);
        if (!remoteDir.exists()) {
            System.out.println("Remote directory not found.");
            System.exit(0);
        }
        _remoteDir = remoteDir;
        _remoteBranch = remoteBranch;

        File remoteBranchDir = Utils.join(_remoteDir, "branches");
        File remoteBranchFile = Utils.join(remoteBranchDir, _remoteBranch);
        if (!remoteBranchFile.exists()) {
            System.out.println("That remote does not have that branch.");
            System.exit(0);
        }

        File remoteInDir = Utils.join(BRANCHES_DIR, remoteName);
        if (!remoteInDir.exists()) {
            remoteInDir.mkdir();
        }

        String fetchBranchName = remoteName + File.separator + remoteBranch;

        File fetchBranchFile = Utils.join(BRANCHES_DIR, fetchBranchName);
        if (!fetchBranchFile.exists()) {
            String remoteCommitId = Utils.readContentsAsString(
                    remoteBranchFile);
            Utils.writeContents(fetchBranchFile, remoteCommitId);
            addCommitsNewBranch(remoteCommitId);
        } else {
            String remoteCommitId = Utils.readContentsAsString(
                    remoteBranchFile);
            String currFetchHead = Utils.readContentsAsString(
                    fetchBranchFile);
            findRemoteCommit(currFetchHead, remoteCommitId);
            Utils.writeContents(fetchBranchFile, remoteCommitId);
        }
    }

    /** Repeats recursion until FETCHID is identical to REMOTEID,
     *  then appends the commit onto the local branch. */
    public static void findRemoteCommit(String fetchId, String remoteId) {
        if (!remoteId.equals(fetchId)) {
            File remoteCommitDir = Utils.join(_remoteDir, "commits");
            File commitFile = Utils.join(remoteCommitDir, remoteId);
            Commit commit = Utils.readObject(commitFile, Commit.class);
            findRemoteCommit(fetchId, commit.getParent());
            appendCommit(remoteId);
        }
    }

    /** Adds COMMITID's commit chain to newly created branch. */
    public static void addCommitsNewBranch(String commitId) {
        File remoteCommitDir = Utils.join(_remoteDir, "commits");
        File remoteCommitFile = Utils.join(remoteCommitDir, commitId);
        Commit commit = Utils.readObject(remoteCommitFile, Commit.class);
        if (commit.getParent() == null) {
            appendCommit(commitId);
        } else {
            addCommitsNewBranch(commit.getParent());
            appendCommit(commitId);
        }
    }

    /** Adds actual COMMITID file along with blobs into remote repo. */
    public static void appendCommit(String commitId) {
        File remoteCommitDir = Utils.join(_remoteDir, "commits");
        File remoteCommitFile = Utils.join(remoteCommitDir, commitId);
        Commit commit = Utils.readObject(remoteCommitFile, Commit.class);

        File newCommitFile = Utils.join(COMMITS_DIR, commitId);
        Utils.writeObject(newCommitFile, commit);

        File remoteBlobDir = Utils.join(_remoteDir, "blobs");

        for (String filename: commit.getBlobs().keySet()) {
            String blobId = commit.getBlobId(filename);
            File blob = Utils.join(remoteBlobDir, blobId);
            File addBlob = Utils.join(BLOBS_DIR, blobId);
            Utils.writeContents(addBlob, Utils.readContents(blob));
        }
    }

    /** File pointer to CWD directory. */
    static final File CWD = new File(System.getProperty("user.dir"));
    /** File pointer to .gitlet directory. */
    static final File GITLET_DIR = Utils.join(CWD, ".gitlet");
    /** File pointer to Branches directory. */
    static final File BRANCHES_DIR = Utils.join(GITLET_DIR, "branches");
    /** File pointer to Blobs directory. */
    static final File BLOBS_DIR = Utils.join(GITLET_DIR, "blobs");
    /** File pointer to Commits directory. */
    static final File COMMITS_DIR = Utils.join(GITLET_DIR, "commits");
    /** File pointer to file tracking HEAD. */
    static final File HEAD = Utils.join(GITLET_DIR, "HEAD");
    /** File pointer to Remote Dirs Directory.*/
    static final File REMOTE_DIR = Utils.join(GITLET_DIR, "remotes");
    /** File pointer to remote branch.*/
    private static String _remoteBranch;
    /** File pointer to remote directory.*/
    private static File _remoteDir;
}
