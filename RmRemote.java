package gitlet;

import java.io.File;

/** Class containing rm-remote command function.
 *  @author Daric Lim
 */
public class RmRemote {
    /** Command function that removes REMOTENAME from remote dir. */
    public static void cmd(String remoteName) {
        File remoteFile = Utils.join(REMOTE_DIR, remoteName);
        if (!remoteFile.exists()) {
            System.out.println("A remote with that name does not exist.");
            System.exit(0);
        }
        remoteFile.delete();
    }
    /** File pointer to CWD directory. */
    static final File CWD = new File(System.getProperty("user.dir"));
    /** File pointer to .gitlet directory. */
    static final File GITLET_DIR = Utils.join(CWD, ".gitlet");
    /** File pointer to Remote Dirs Directory.*/
    static final File REMOTE_DIR = Utils.join(GITLET_DIR, "remotes");
}
