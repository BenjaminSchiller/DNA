package dna.updates.directed;

import dna.graph.Graph;
import dna.graph.Node;
import dna.graph.directed.DirectedEdge;
import dna.graph.directed.DirectedGraph;
import dna.graph.directed.DirectedGraphDatastructures;
import dna.updates.Batch;
import dna.util.parameters.IntParameter;

public class RandomDirectedEdgeAdditions extends DirectedBatchGenerator {

	private int additions;

	public RandomDirectedEdgeAdditions(int additions,
			DirectedGraphDatastructures datastructures) {
		super("randomDirectedEdgeAdditions", new IntParameter("additions",
				additions), datastructures);
		this.additions = additions;
	}

	@Override
	public Batch<DirectedEdge> generate(
			Graph<? extends Node<DirectedEdge>, DirectedEdge> graph) {
		DirectedGraph g = (DirectedGraph) graph;
		Batch<DirectedEdge> batch = new Batch<DirectedEdge>(0, 0, 0,
				this.additions, 0, 0);
		while (batch.getSize() < this.additions) {
			// TODO implement random directed edge addition
		}

		return null;
	}

}
