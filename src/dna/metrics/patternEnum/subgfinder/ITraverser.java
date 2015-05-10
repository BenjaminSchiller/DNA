package dna.metrics.patternEnum.subgfinder;

import java.util.Collection;

import dna.graph.edges.IEdge;
import dna.metrics.patternEnum.datastructures.Path;
import dna.metrics.patternEnum.subgfinder.hub.manage.HubManager;

public interface ITraverser {
	
	public static enum EdgeAction {
		added,
		removed
	}
	
	
	/**
	 * Returns all subgraphs with {@code maxSize} nodes that contains {@code actEdge}.
	 * 
	 * @param actEdge
	 * @param maxSize
	 * @param hubManager
	 * @param edgeAction
	 * @return
	 */
	Collection<Path> getSubgraphsForEdge(IEdge actEdge, int maxSize, HubManager hubManager,
			EdgeAction edgeAction);
}
