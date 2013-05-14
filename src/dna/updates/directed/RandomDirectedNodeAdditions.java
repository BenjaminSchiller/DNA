package dna.updates.directed;

import dna.graph.Graph;
import dna.graph.Node;
import dna.graph.directed.DirectedEdge;
import dna.graph.directed.DirectedGraph;
import dna.graph.directed.DirectedGraphDatastructures;
import dna.graph.directed.DirectedNode;
import dna.updates.Batch;
import dna.updates.NodeAddition;
import dna.util.parameters.IntParameter;

public class RandomDirectedNodeAdditions extends DirectedBatchGenerator {

	private int additions;

	public RandomDirectedNodeAdditions(int additions,
			DirectedGraphDatastructures datastructures) {
		super("randomDirectedNodeAdditions", new IntParameter("additions",
				additions), datastructures);
		this.additions = additions;
	}

	@Override
	public Batch<DirectedEdge> generate(
			Graph<? extends Node<DirectedEdge>, DirectedEdge> graph) {
		DirectedGraph g = (DirectedGraph) graph;
		Batch<DirectedEdge> batch = new Batch<DirectedEdge>(this.additions, 0,
				0, 0, 0, 0);
		int index = graph.getNodeCount();
		while (batch.getSize() < this.additions) {
			DirectedNode n = (DirectedNode) this.datastructures
					.newNodeInstance(index);
			if (!g.containsNode(n)) {
				batch.add(new NodeAddition<DirectedEdge>(this.datastructures
						.newNodeInstance(index)));
			}
			index++;
		}
		return batch;
	}

}
