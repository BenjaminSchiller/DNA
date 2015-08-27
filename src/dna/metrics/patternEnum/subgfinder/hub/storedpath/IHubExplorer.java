package dna.metrics.patternEnum.subgfinder.hub.storedpath;

import java.util.Collection;

import dna.graph.edges.IEdge;
import dna.metrics.patternEnum.datastructures.Path;

public interface IHubExplorer {
	void addPaths(Collection<Path> paths, StoredPathVertex spv);
	void addPaths(Collection<Path> paths, StoredPathVertex spv, boolean sorted);
	void addPath(IEdge actEdge, Path path, StoredPathVertex spv, int maxSize);
	void removePath(IEdge edgeToRemove, StoredPathVertex spv);
}
