package gitlet;

import java.io.File;
import java.io.Serializable;

/** Represents a Commit object.
 *  @author Daric Lim
 */
public class Commit implements Serializable {
    /** Constructor for Commit object. Takes in MESSAGE about commit.
     *  Stores TIMESTAMP representing the date of Commit's creation.
     *  Stores ID's of parent Commit objects, PARENT1 and PARENT2.
     *  Takes in FILESADDED and FILESRM and modifies BLOBS map
     *  to reflect the new changes.
     * */
    public Commit(String message, String parent1,
                  String parent2, String timestamp,
                  StringHashMap filesAdded, StringHashMap filesRm) {
        _message = message;
        _parent1 = parent1;
        _parent2 = parent2;
        _timestamp = timestamp;
        this.blobs = new StringHashMap();
        this.isMerge = true;

        if (parent1 != null) {
            File commits = Utils.join(".gitlet", "commits");
            File parentFile = Utils.join(commits, parent1);
            Commit parentCommit = Utils.readObject(parentFile, Commit.class);
            this.blobs.putAll(parentCommit.getBlobs());
        }

        if (filesAdded != null) {
            addFiles(filesAdded);
        }
        if (filesRm != null) {
            rmFiles(filesRm);
        }

        _id = makeId();
    }

    /** Constructor for Commit object. Takes in MESSAGE about commit.
     *  Stores TIMESTAMP representing the date of Commit's creation.
     *  Stores ID's of parent Commit object, PARENT.
     *  Takes in FILESADDED and FILESRM and modifies BLOBS map
     *  to reflect the new changes.
     * */
    public Commit(String message, String parent,
                  String timestamp, StringHashMap filesAdded,
                  StringHashMap filesRm) {
        _message = message;
        _parent = parent;
        _timestamp = timestamp;
        this.blobs = new StringHashMap();
        this.isMerge = false;

        if (parent != null) {
            File commits = Utils.join(".gitlet", "commits");
            File parentFile = Utils.join(commits, parent);
            Commit parentCommit = Utils.readObject(parentFile, Commit.class);
            this.blobs.putAll(parentCommit.getBlobs());
        }

        if (filesAdded != null) {
            addFiles(filesAdded);
        }
        if (filesRm != null) {
            rmFiles(filesRm);
        }

        _id = makeId();
    }

    /** Function to add files in FILESADDED to BLOBS hashmap. */
    public void addFiles(StringHashMap filesAdded) {
        for (String key: filesAdded.keySet()) {
            blobs.put(key, filesAdded.get(key));
        }
    }

    /** Function to remove files in FILESRM to BLOBS hashmap. */
    public void rmFiles(StringHashMap filesRm) {
        for (String key : filesRm.keySet()) {
            if (!blobs.containsKey(key)) {
                Utils.error("File not contained in commit");
            }
            blobs.remove(key);
        }
    }

    /** Serializes Commit into SHA1 ID.
     * @return String of SHA1 ID.
     * */
    public String makeId() {
        return Utils.sha1(Utils.serialize(this));
    }

    /** Returns Commit's BLOBS hashmap. */
    public StringHashMap getBlobs() {
        return blobs;
    }

    /** Returns true if BLOBS contains FILENAME. */
    public boolean containsFilename(String filename) {
        return blobs.containsKey(filename);
    }

    /** Returns Blob's ID based on Blob KEY. */
    public String getBlobId(String key) {
        return blobs.get(key);
    }

    /** Returns true if BLOB contains Blob KEY. */
    public boolean hasKey(String key) {
        return blobs.containsKey(key);
    }

    /** Returns Commit's message. */
    public String getMessage() {
        return _message;
    }

    /** Returns Commit's parent, or parent1 if merge commit. */
    public String getParent() {
        if (isMerge) {
            return _parent1;
        }
        return _parent;
    }

    /** Returns Commit's parent1 iff merge commit. */
    public String getParent1() {
        if (!isMerge) {
            System.out.println("Not a merge commit.");
            System.exit(0);
        }
        return _parent1;
    }

    /** Returns Commit's parent2 iff merge commit. */
    public String getParent2() {
        if (!isMerge) {
            System.out.println("Not a merge commit.");
            System.exit(0);
        }
        return _parent2;
    }

    /** Returns Commit's ID. */
    public String getId() {
        return _id;
    }

    /** Returns Commit's timestamp. */
    public String getTimestamp() {
        return _timestamp;
    }

    /** Returns true if Commit is a merge Commit. */
    public boolean isMerge() {
        return isMerge;
    }

    /** Message of Commit. */
    private String _message;
    /** Timestamp of Commit. */
    private String _timestamp;
    /** SHA1 ID of Commit. */
    private String _id;
    /** Parent (or Parent1) of Commit. */
    private String _parent;
    /** HashMap of Blobs in Commit. */
    private StringHashMap blobs;
    /** Whether Commit is a merge commit. */
    private boolean isMerge;
    /** Parent1 of Commit if merge commit. */
    private String _parent1;
    /** Parent2 of Commit if merge commit. */
    private String _parent2;
    /** serial version UID for uniformity purposes. */
    private static final long serialVersionUID = 1084937595;
}
