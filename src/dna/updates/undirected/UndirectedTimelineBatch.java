package dna.updates.undirected;

import dna.graph.Graph;
import dna.graph.GraphDatastructures;
import dna.graph.Node;
import dna.graph.undirected.UndirectedEdge;
import dna.graph.undirected.UndirectedGraph;
import dna.graph.undirected.UndirectedNode;
import dna.updates.Batch;

public class UndirectedTimelineBatch extends UndirectedBatchGenerator {

	private UndirectedBatchGenerator[] generators;

	private int timestamp;

	public UndirectedTimelineBatch(
			UndirectedBatchGenerator[] generators,
			GraphDatastructures<UndirectedGraph, UndirectedNode, UndirectedEdge> ds) {
		super("undirectedTimelineBatch", ds);
		this.generators = generators;
		this.timestamp = 0;
	}

	@Override
	public Batch<UndirectedEdge> generate(
			Graph<? extends Node<UndirectedEdge>, UndirectedEdge> graph) {
		return this.generators[this.timestamp++ % this.generators.length]
				.generate(graph);
	}

	@Override
	public void reset() {
		this.timestamp = 0;
	}

}
