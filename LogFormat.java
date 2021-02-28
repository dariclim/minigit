package gitlet;

/** Class representing format for log command function.
 *  @author Daric Lim
 */
public class LogFormat {
    /** Print the log format for CURRCOMMIT. */
    public static void printLog(Commit currCommit) {
        if (currCommit.isMerge()) {
            System.out.printf("===\ncommit " + currCommit.getId()
                    + "\nMerge: " + abbrevId(currCommit.getParent1())
                    + " " + abbrevId(currCommit.getParent2())
                    + "\nDate: " + currCommit.getTimestamp()
                    + "\n" + currCommit.getMessage() + "\n\n");
        } else {
            System.out.printf("===\ncommit " + currCommit.getId()
                    + "\nDate: " + currCommit.getTimestamp()
                    + "\n" + currCommit.getMessage()
                    + "\n\n");
        }
    }
    /** Returns the abbreviated Id for COMMITID. */
    public static String abbrevId(String commitId) {
        return commitId.substring(0, 7);
    }
}
