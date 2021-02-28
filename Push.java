package gitlet;

import java.io.File;

/** Class containing push command function.
 *  @author Daric Lim
 */
public class Push {
    /** Command function that pushes current local branch's commits to
     *  end of REMOTEBRANCH at the given REMOTENAME. */
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

        String headPath = Utils.readContentsAsString(HEAD);
        File headBranch = new File(headPath);
        String headCommitId = Utils.readContentsAsString(headBranch);
        File headCommitFile = Utils.join(COMMITS_DIR, headCommitId);
        Commit headCommit = Utils.readObject(headCommitFile, Commit.class);

        File remoteBranchDir = Utils.join(_remoteDir, "branches");
        File remoteBranchFile = Utils.join(remoteBranchDir, _remoteBranch);
        if (!remoteBranchFile.exists()) {
            Utils.writeContents(remoteBranchFile, headCommitId);
            addCommitsNewBranch(headCommit);
        } else {
            String remoteCommitId = Utils.readContentsAsString(
                    remoteBranchFile);
            if (!findRemoteCommit(headCommit, remoteCommitId)) {
                System.out.println("Please pull down "
                        + "remote changes before pushing.");
            }
            Utils.writeContents(remoteBranchFile, headCommitId);
        }
    }

    /** Returns true if COMMIT id matches REMOTECOMMITID,
     *  and then append the commit. */
    public static boolean findRemoteCommit(Commit commit,
                                           String remoteCommitId) {
        if (remoteCommitId.equals(commit.getId())) {
            return true;
        } else if (commit.getParent() != null) {
            File parentCommitFile = Utils.join(COMMITS_DIR, commit.getParent());
            Commit parentCommit = Utils.readObject(parentCommitFile,
                                                      Commit.class);
            boolean found = findRemoteCommit(parentCommit, remoteCommitId);
            if (found) {
                appendCommit(commit);
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /** Adds actual COMMIT file along with blobs into remote repo. */
    public static void appendCommit(Commit commit) {
        File commitsDir = Utils.join(_remoteDir, "commits");
        File newCommitFile = Utils.join(commitsDir, commit.getId());
        Utils.writeObject(newCommitFile, commit);

        File blobDir = Utils.join(_remoteDir, "blobs");
        for (String filename: commit.getBlobs().keySet()) {
            String blobId = commit.getBlobId(filename);
            File blob = Utils.join(BLOBS_DIR, blobId);
            File addBlob = Utils.join(blobDir, blobId);
            Utils.writeContents(addBlob, Utils.readContents(blob));
        }
    }

    /** Adds COMMIT chain to newly created branch. */
    public static void addCommitsNewBranch(Commit commit) {
        if (commit.getParent() == null) {
            appendCommit(commit);
        } else {
            File parentFile = Utils.join(COMMITS_DIR, commit.getParent());
            Commit parentCommit = Utils.readObject(parentFile,
                                                Commit.class);
            addCommitsNewBranch(parentCommit);
            appendCommit(commit);
        }
    }

    /** File pointer to CWD directory. */
    static final File CWD = new File(System.getProperty("user.dir"));
    /** File pointer to .gitlet directory. */
    static final File GITLET_DIR = Utils.join(CWD, ".gitlet");
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
