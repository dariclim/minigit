package gitlet;
import java.io.File;

/** Class containing Find command function.
 *  @author Daric Lim
 */
public class Find {
    /** Command function that finds commit based on the commit's MESSAGE. */
    public static void cmd(String message) {
        boolean foundMessage = false;
        File[] files = COMMITS_DIR.listFiles();
        for (File file: files) {
            Commit currCommit = Utils.readObject(file, Commit.class);
            if (currCommit.getMessage().equals(message)) {
                foundMessage = true;
                System.out.println(currCommit.getId());
            }
        }
        if (!foundMessage) {
            System.out.println("Found no commit with that message.");
            System.exit(0);
        }
    }
    /** File pointer to CWD directory. */
    static final File CWD = new File(System.getProperty("user.dir"));
    /** File pointer to .gitlet directory. */
    static final File GITLET_DIR = Utils.join(CWD, ".gitlet");
    /** File pointer to Commits directory. */
    static final File COMMITS_DIR = Utils.join(GITLET_DIR, "commits");
}
