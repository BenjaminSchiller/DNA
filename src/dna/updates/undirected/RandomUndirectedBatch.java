package dna.updates.undirected;

import dna.graph.GraphDatastructures;
import dna.graph.undirected.UndirectedEdge;
import dna.graph.undirected.UndirectedGraph;
import dna.graph.undirected.UndirectedGraphDatastructures;
import dna.graph.undirected.UndirectedNode;

public class RandomUndirectedBatch extends UndirectedBatchCombinator {

	public RandomUndirectedBatch(
			int nodeAdditions,
			int nodeRemovals,
			int edgeAdditions,
			int edgeRemovals,
			GraphDatastructures<UndirectedGraph, UndirectedNode, UndirectedEdge> ds) {
		super("randomUndirectedBatch", new RandomUndirectedNodeAdditions(
				nodeAdditions, (UndirectedGraphDatastructures) ds),
				new RandomUndirectedNodeRemoval(nodeRemovals,
						(UndirectedGraphDatastructures) ds),
				new RandomUndirectedEdgeAdditions(edgeAdditions,
						(UndirectedGraphDatastructures) ds),
				new RandomUndirectedEdgeRemoval(edgeRemovals,
						(UndirectedGraphDatastructures) ds), ds);
	}

}
