#!/bin/bash
 
set -e
 
function mkdirectory {
	mkdir $1
	git add $1
}

function legacy {
	git mv src/$1 legacy/$1
}

echo "First step: move Bennis code out of the way"

mkdirectory legacy
mkdirectory legacy/dna
mkdirectory legacy/dna/graph
mkdirectory legacy/dna/graph/edges
mkdirectory legacy/dna/graph/nodes
mkdirectory legacy/dna/graph/directed
mkdirectory legacy/dna/graph/undirected
mkdirectory legacy/dna/io
mkdirectory legacy/dna/io/etc
 
git mv src/dna/graph/directed/* legacy/dna/graph/directed/
git mv src/dna/graph/undirected/* legacy/dna/graph/undirected/
legacy dna/graph/GraphDatastructures.java 
legacy dna/graph/GraphGenerator.java
legacy dna/graph/GraphImpl.java
legacy dna/graph/WeightedEdge.java
legacy dna/graph/WeightedNode.java

legacy dna/graph/Graph.java
legacy dna/graph/edges/
legacy dna/graph/nodes/
legacy dna/io/Reader.java
legacy dna/io/Writer.java
legacy dna/io/GraphReader.java
legacy dna/io/GraphWriter.java
legacy dna/io/etc/Keywords.java
git mv .gitignore legacy/

git commit -m "Moved Bennis stuff to legacy section"

test -f .classpath && mv .classpath{,.hold}
test -f .project && mv .project{,.hold}

test -f lib/mockito-all-1.9.5.jar && rm lib/mockito-all-1.9.5.jar

echo " "
echo "Second step: add experimental repo"
git remote rm experimental
git remote add -f experimental git@github.com:NicoHaase/DNAExperiment.git

echo " "
echo "Third step: cherry picking"

for singleCommit in $(git rev-list --remotes=experimental --reverse --abbrev-commit)
do
#	if [ "$singleCommit" == "1265051" ]
#	then
		# Ignore initial commit
		# continue
#	fi
	
	echo "Running     git cherry-pick $singleCommit"
	git cherry-pick $singleCommit
done

mv .classpath{.hold,}
mv .project{.hold,}

echo "Manual steps:"
echo " - add JUnit and Mockito to the build path"
echo " - change the project to be an AspectJ project"