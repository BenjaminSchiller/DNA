#!/bin/bash
 
function mkdirectory {
	mkdir $1
	git add $1
}

function legacy {
	git mv src/$1 legacy/$1
}

function newversion {
    git mv experimental/src/$1 src/$1
}
 
echo "Importing stuff from experimental repo into DNA repo"
git remote rm experimental
git remote add -f experimental git@github.com:NicoHaase/DNAExperiment.git
git merge -s ours --no-commit experimental/master
git read-tree --prefix=experimental/ -u experimental/master
git commit -m "Merge experimental branch into ours"

echo ""
echo "Second step: replace former classes with experimental ones"

git rm experimental/src/dna/util/parameters/*
git rm experimental/src/dna/util/Log.java
git rm experimental/src/dna/util/Rand.java

git mv experimental/src/dna/datastructures/ src/dna/
git mv experimental/src/dna/tests/ src/dna/
git mv experimental/src/dna/examples/ src/dna/
git mv experimental/src/dna/factories/ src/dna/
 
git mv experimental/src/dna/graph/Element.java src/dna/graph/
git mv experimental/src/dna/graph/IElement.java src/dna/graph/
git mv experimental/src/dna/graph/IWeighted.java src/dna/graph/
 
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

git commit -m "Moved Bennis stuff to legacy section"

echo " "
echo "Finished moving old stuff, moving new stuff new"

newversion dna/graph/Graph.java
newversion dna/graph/edges/
newversion dna/graph/nodes/
newversion dna/io/Reader.java
newversion dna/io/Writer.java
newversion dna/io/GraphReader.java
newversion dna/io/GraphWriter.java
newversion dna/io/etc/Keywords.java
 
rmdir experimental/src/dna/graph
rmdir experimental/src/dna/io/etc
rmdir experimental/src/dna/io/

git mv experimental/lib/mockit* lib/
 
git commit -m "Integretated experimental stuff into core"

echo "Manual steps:"
echo " - add JUnit and Mockito to the build path"
echo " - change the project to be an AspectJ project"