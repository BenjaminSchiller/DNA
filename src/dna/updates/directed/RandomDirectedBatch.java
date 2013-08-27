package dna.updates.directed;

import dna.datastructures.GraphDataStructure;
import dna.graph.edges.DirectedEdge;
import dna.graph.nodes.DirectedNode;

public class RandomDirectedBatch extends DirectedBatchCombinator {

	public RandomDirectedBatch(int nodeAdditions, int nodeRemovals, int edgeAdditions, int edgeRemovals,
			GraphDataStructure ds) {
		super("randomDirectedBatch", new RandomDirectedNodeAdditions(nodeAdditions, ds), new RandomDirectedNodeRemoval(
				nodeRemovals, ds), new RandomDirectedEdgeAdditions(edgeAdditions, ds), new RandomDirectedEdgeRemoval(
				edgeRemovals, ds), ds);
	}
}
