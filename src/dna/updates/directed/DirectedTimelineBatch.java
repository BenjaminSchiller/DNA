package dna.updates.directed;

import dna.graph.Graph;
import dna.graph.GraphDatastructures;
import dna.graph.Node;
import dna.graph.directed.DirectedEdge;
import dna.graph.directed.DirectedGraph;
import dna.graph.directed.DirectedNode;
import dna.updates.Batch;

public class DirectedTimelineBatch extends DirectedBatchGenerator {

	private DirectedBatchGenerator[] generators;

	private int timestamp;

	public DirectedTimelineBatch(
			GraphDatastructures<DirectedGraph, DirectedNode, DirectedEdge> ds,
			DirectedBatchGenerator... generators) {
		super("directedTimelineBatch", ds);
		this.generators = generators;
		this.timestamp = 0;
	}

	@Override
	public Batch<DirectedEdge> generate(
			Graph<? extends Node<DirectedEdge>, DirectedEdge> graph) {
		return this.generators[this.timestamp++ % this.generators.length]
				.generate(graph);
	}

	@Override
	public void reset() {
		this.timestamp = 0;
	}

}
