git cheetsheet - to help avoiding merge problems
=========
   Nico Haase <nico.haase@stud.tu-darmst...>

For my master's thesis, I'm working on [DNA](https://www.p2p.tu-darmstadt.de/research/dna/), a framework to analyze dynamic graphs. Our source code is hosted in a git project on github, where each contributer is free to fork the project. Through pull requests, one can get his work back into the main repository. For new contributers, I want to give an introduction into the work with multiple branches here. A very basic understanding of how git itself works can be found in a [tutorial by Roger Dudler](http://rogerdudler.github.io/git-guide/index.html).

1) Fetch current status from the core repository: `git clone`
------------------
git will keep a copy of the core repository, including all revisions and all branches(!), in the .git folder. This will be initialized through git clone: `git clone git@github.com:BenjaminSchiller/DNA.git DNA` will clone the upstream repository into the local folder DNA, and check out the current master

If you want to use github for a copy of your master, you can simply use the Fork button to create a copy of the whole current repository into your own account. In the clone command above, you should use your own username instead of BenjaminSchiller. After cloning, your own copy is locally refered to as the "origin" repository, and you have to connect the upstream repository to it through `git remote add upstream git@github.com:BenjaminSchiller/DNA.git`. This eases later work, as you can now simply receive the changes from the upstream repo.

2) Create a new branch
------------------
Each contributer should work on its own branch. This makes it possible to distinguish changes of each contributer, and to re-pull other contributions. Additionally, it will create less problems when creating a pull request on github later. `git checkout -b branchname` will create a new branch with the name `branchname`, based on the previously used branch. Consider using a branch name that on the one hand is a good label for your contribution, but on the other hand clearly states that the branch contains work in progress that might also get rebased (@zoranzaric recommends the prefix `tmp`) - I'll come to the problems in a minute!

3) Work on that branch
------------------
This needs no specific introduction aparts the one of Roger Dudler.
 
4) Receive changes from upstream's master branch: `git rebase`
------------------
This is the interesting part: to get the changes from the upstream repository into your own, you have to fetch it first through `git fetch upstream`. Afterwards, use `git rebase upstream/master` to put your work on top of the current masters commit. Conflicts might occur if commits overlap. The result: all commits you did since first creating the branch or sending the last pull request will be placed after the last commit in the upstream's master branch. 

Other documentations might lead you to `git pull upstream`. This is not what we want here, as a `pull` first performs a `fetch` and afterwards a `merge`. This uses both branches (the one you created and the master branch from upstream), merges them together automatically and creates a new commit with the merge results. `merge` commits do not look nice and contain more information than needed, aparts from the magic happening for the automatic merge. Sementically, they say something like "I don't care what happened upstream, I just want to glue both pieces together". A `rebase` states: "Okay, I did some work and want to integrate it into the master".

A [tutorial by John Metta](http://mettadore.com/analysis/a-simple-git-rebase-workflow-explained/) gives a more in-depth introduction and some graphics to show the difference, while [Nicola Paolucci](http://blogs.atlassian.com/2013/10/git-team-workflows-merge-or-rebase/) discusses pros and cons about `rebase` and `merge`.

Before pushing the result to github or issuing a pull request, please check the project for syntax errors and test your code. Even if the rebase algorithm should not introduce errors, they can occur every now and then...

`git push` might give you errors now. This is a security mechanism by git: the local changes you pushed the last time differ from the stuff that your local repository now contains, as rebasing rewrites the commit log and the ancestors. The last pushed commit is not the same as the last that now resides in your repository. Each commit knows about its ancestor, but rebasing somehow is like cheating as you smuggle in new commits in the middle of your history. The server cannot handle a `push` while it sees a commit on the top that does not exist in the local version you want to push. `git push --force` will solve this problem by just pushing your version and overriding everything that is on the server. This is also the reason that you want to mark your branch specifically: if someone else used your branch in the meantime for his own work and updates his copy now, he gets into the same trouble as his local copy has a different history than the one pulled from a server...

5) Pull request
------------------
If you use github for a "centralized" data hub, you can simply give your contributions back now through a pull request to the core repository. This is an easy way to notify the core developers about your changes and give them a possibility of easy integration.