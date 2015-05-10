package dna.metrics.patternEnum.subgfinder;

import dna.graph.edges.IEdge;
import dna.metrics.patternEnum.patterncounter.IPatternCounter;
import dna.metrics.patternEnum.subgfinder.hub.manage.HubManager;

public interface ITraverseCounter {
	
	public static enum EdgeAction {
		added,
		removed
	}
	
	void countSubgraphsForEdge(IEdge actEdge_, int maxSize_, HubManager hubManager_,
			EdgeAction edgeAction, IPatternCounter motifCounter_);
}
