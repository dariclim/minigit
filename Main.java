package gitlet;

/** Driver class for Gitlet, the tiny stupid version-control system.
 *  @author Daric Lim
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND> .... */
    public static void main(String... args) {
        checkArgs(args);
        switchCase(args);
    }

    /** Switch case, reads ARGS and initiates command. */
    public static void switchCase(String... args) {
        switch (args[0]) {
        case "init":
            Init.cmd();
            break;
        case "add":
            Add.cmd(args[1]);
            break;
        case "commit":
            CommitCommand.cmd(args[1]);
            break;
        case "rm":
            Rm.cmd(args[1]);
            break;
        case "log":
            Log.cmd();
            break;
        case "global-log":
            GlobalLog.cmd();
            break;
        case "checkout":
            checkout(args);
            break;
        case "find":
            Find.cmd(args[1]);
            break;
        case "status":
            Status.cmd();
            break;
        case "branch":
            Branch.cmd(args[1]);
            break;
        case "rm-branch":
            RemoveBranch.cmd(args[1]);
            break;
        case "reset":
            Reset.cmd(args[1]);
            break;
        case "merge":
            Merge.cmd(args[1]);
            break;
        case "add-remote":
            AddRemote.cmd(args[1], args[2]);
            break;
        case "rm-remote":
            RmRemote.cmd(args[1]);
            break;
        case "push":
            Push.cmd(args[1], args[2]);
            break;
        case "fetch":
            Fetch.cmd(args[1], args[2]);
            break;
        case "pull":
            Pull.cmd(args[1], args[2]);
            break;
        default:
            unknownCmd();
        }
    }

    /** Prints error if input unknown command. */
    public static void unknownCmd() {
        System.out.println("No command with that name exists.");
        System.exit(0);
    }

    /** Checkout command using ARGS as parameters. */
    public static void checkout(String... args) {
        if (args.length == 2) {
            Checkout.cmd(args[1]);
        } else if (args.length == 3) {
            Checkout.cmd(args[1], args[2]);
        } else if (args.length == 4) {
            Checkout.cmd(args[1], args[2], args[3]);
        }
    }

    /** Check if ARGS is not empty. */
    public static void checkArgs(String... args) {
        if (args.length == 0) {
            System.out.println("Please enter a command");
            System.exit(0);
        }
    }
}
