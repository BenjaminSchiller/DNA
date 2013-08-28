package dna.updates.undirected;

import dna.graph.datastructures.GraphDataStructure;

public class RandomUndirectedBatch extends UndirectedBatchCombinator {

	public RandomUndirectedBatch(int nodeAdditions, int nodeRemovals, int edgeAdditions, int edgeRemovals,
			GraphDataStructure ds) {
		super("randomUndirectedBatch", new RandomUndirectedNodeAdditions(nodeAdditions, ds),
				new RandomUndirectedNodeRemoval(nodeRemovals, ds),
				new RandomUndirectedEdgeAdditions(edgeAdditions, ds),
				new RandomUndirectedEdgeRemoval(edgeRemovals, ds), ds);
	}

}
