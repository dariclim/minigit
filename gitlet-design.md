# Gitlet Design Document

**Name**: Daric Lim

## Classes and Data Structures

### Commit
##### Instance Variables
* Message - contains message of commit
* Timestamp - time at which a commit was created, assigned by constructor
* Parent - the parent commit of current commit object
* Blob - the blob object that the commit is pointing to

##### Methods
* Commit constructor - constructs commit that takes parameters: message, timestamp, parent commit, and blob
* Merge commit constructor - constructs commit that takes same parameters but takes two parents 
* getMessage - Returns the message of commit
* getTimestamp - Returns the timestamp of commit
* getParent - Returns the parent of commit, if regular commit. Else, returns both parents if merge.
* getBlob - Returns the values of Blob
* isMerge - lets us know if the commit is a merge or regular commit, based on number of parents
* toString - override of toString to format string representation of commit


### Repository
##### Instance Variables
* AddingStage - the staging area for additions
* RemovingStage - the staging area for removals
* CommitBranches - the collection of commit branches, default having master branch
* Blobs - the collection of blobs 
* Head - the current pointer pointing to the commit
* The directory of the repository (final variable)

##### Methods
* Init - Create a new repository in current directory, creating an initial commit and master branch. Possibly the constructor 
* Add - adds copy of file to addition staging area, overwrites if file is already staged
* Remove - adds copy of file to removal staging area, remove file from cwd
* Commit - Removes objects from staging area, and creates new commit cloned from parent
* Log - Displays info about each commit starting from Head, ignoring second parents in merge commits
* Global-log - Displays info about all commits made, disregarding order of commits
* Find - Prints id of all commits with the given commit msg
* Status - Displays the Branches, Staged Files, Removed Files, Modifications not staged for commit, and untracked files 
* Checkout - Overwrites the existing version of file in cwd
* Branch - Creates new branch with given name, points at current head node, without switching to newly created branch
* RemoveBranch - Deletes branch with given name, without deleting commits created under branch
* Reset - Checks out all files tracked by given commit, removing tracked files not present in commit
* Merge - Merges file from given branch to current branch

### Blob, extending Serializable
##### Instance Variables
* UniqueID - unique ID of Blob for serialization
* Contents - Contents of blob

##### Methods
* Blob Constructor - creates Blob object using parameters of contents
* GetContents - returns contents of blob
* GetUniqueId - returns the uniqueID

### Main
##### Methods
* Command - Based on the String[] args provided to System.in, provides a switch case statement of commands that give commands from Repository

