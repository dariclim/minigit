package gitlet;

import java.io.File;

/** Class containing pull command function.
 *  @author Daric Lim
 */
public class Pull {
    /** Command function that pulls REMOTEBRANCH from REMOTENAME. */
    public static void cmd(String remoteName, String remoteBranch) {
        Fetch.cmd(remoteName, remoteBranch);
        String fetchBranchName = remoteName + File.separator + remoteBranch;
        Merge.cmd(fetchBranchName);
    }
}
