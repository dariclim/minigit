package gitlet;

import java.io.File;
import java.util.List;

/** Class containing add-remote command function.
 *  @author Daric Lim
 */
public class AddRemote {
    /** Command function that saves REMOTENAME from REMOTEPATH
     *  a new remote dir into gitlet. */
    public static void cmd(String remoteName, String remotePath) {
        List<String> filesInRemote = Utils.plainFilenamesIn(REMOTE_DIR);
        if (filesInRemote.contains(remoteName)) {
            System.out.println("A remote with that name already exists.");
            System.exit(0);
        }
        remotePath = remotePath.replaceAll("/", File.separator);
        File addRemote = Utils.join(REMOTE_DIR, remoteName);
        Utils.writeContents(addRemote, remotePath);
    }
    /** File pointer to CWD directory. */
    static final File CWD = new File(System.getProperty("user.dir"));
    /** File pointer to .gitlet directory. */
    static final File GITLET_DIR = Utils.join(CWD, ".gitlet");
    /** File pointer to Remote Dirs Directory.*/
    static final File REMOTE_DIR = Utils.join(GITLET_DIR, "remotes");
}
