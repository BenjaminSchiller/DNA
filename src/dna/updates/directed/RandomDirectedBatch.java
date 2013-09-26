package dna.updates.directed;

import dna.graph.GraphDatastructures;
import dna.graph.directed.DirectedEdge;
import dna.graph.directed.DirectedGraph;
import dna.graph.directed.DirectedGraphDatastructures;
import dna.graph.directed.DirectedNode;

public class RandomDirectedBatch extends DirectedBatchCombinator {

	public RandomDirectedBatch(int nodeAdditions, int nodeRemovals,
			int edgeAdditions, int edgeRemovals,
			GraphDatastructures<DirectedGraph, DirectedNode, DirectedEdge> ds) {
		super("randmDirectedBatch", new RandomDirectedNodeAdditions(
				nodeAdditions, (DirectedGraphDatastructures) ds),
				new RandomDirectedNodeRemoval(nodeRemovals,
						(DirectedGraphDatastructures) ds),
				new RandomDirectedEdgeAdditions(edgeAdditions,
						(DirectedGraphDatastructures) ds),
				new RandomDirectedEdgeRemoval(edgeRemovals,
						(DirectedGraphDatastructures) ds), ds);
	}

}
