package dna.updates.directed;

import dna.graph.Graph;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.edges.DirectedEdge;
import dna.updates.Batch;

public class DirectedTimelineBatch extends DirectedBatchGenerator {

	private DirectedBatchGenerator[] generators;

	private int timestamp;

	public DirectedTimelineBatch(GraphDataStructure ds, DirectedBatchGenerator... generators) {
		super("directedTimelineBatch", ds);
		this.generators = generators;
		this.timestamp = 0;
	}

	@Override
	public Batch<DirectedEdge> generate(Graph graph) {
		return this.generators[this.timestamp++ % this.generators.length].generate(graph);
	}

	@Override
	public void reset() {
		this.timestamp = 0;
	}

}
