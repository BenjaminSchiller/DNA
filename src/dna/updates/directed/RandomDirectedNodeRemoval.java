package dna.updates.directed;

import java.util.HashSet;

import dna.graph.Graph;
import dna.graph.Node;
import dna.graph.directed.DirectedEdge;
import dna.graph.directed.DirectedGraph;
import dna.graph.directed.DirectedGraphDatastructures;
import dna.graph.directed.DirectedNode;
import dna.updates.Batch;
import dna.updates.NodeRemoval;
import dna.util.parameters.IntParameter;

public class RandomDirectedNodeRemoval extends DirectedBatchGenerator {

	private int removals;

	public RandomDirectedNodeRemoval(int removals,
			DirectedGraphDatastructures datastructures) {
		super("randomDirectedNodeRemoval", new IntParameter("removals",
				removals), datastructures);
		this.removals = removals;
	}

	@Override
	public Batch<DirectedEdge> generate(
			Graph<? extends Node<DirectedEdge>, DirectedEdge> graph) {
		DirectedGraph g = (DirectedGraph) graph;
		Batch<DirectedEdge> batch = new Batch<DirectedEdge>(0, this.removals,
				0, 0, 0, 0);
		HashSet<DirectedNode> removed = new HashSet<DirectedNode>(this.removals);
		while (batch.getSize() < this.removals) {
			DirectedNode n = (DirectedNode) g.getRandomNode();
			if (removed.contains(n)) {
				continue;
			}
			removed.add(n);
			batch.add(new NodeRemoval<DirectedEdge>(n));
		}
		return batch;
	}
}
