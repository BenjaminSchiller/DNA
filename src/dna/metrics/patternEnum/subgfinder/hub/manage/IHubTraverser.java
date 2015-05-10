package dna.metrics.patternEnum.subgfinder.hub.manage;

import java.util.Collection;

import dna.graph.nodes.INode;
import dna.metrics.patternEnum.datastructures.Path;

public interface IHubTraverser {
	Collection<Path> getSubgraphsForHub(INode hub, int size);
}
