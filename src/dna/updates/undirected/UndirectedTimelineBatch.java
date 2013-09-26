package dna.updates.undirected;

import dna.graph.Graph;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.edges.UndirectedEdge;
import dna.updates.Batch;

public class UndirectedTimelineBatch extends UndirectedBatchGenerator {

	private UndirectedBatchGenerator[] generators;

	private int timestamp;

	public UndirectedTimelineBatch(

	GraphDataStructure ds, UndirectedBatchGenerator... generators) {
		super("undirectedTimelineBatch", ds);
		this.generators = generators;
		this.timestamp = 0;
	}

	@Override
	public Batch<UndirectedEdge> generate(Graph graph) {
		return this.generators[this.timestamp++ % this.generators.length].generate(graph);
	}

	@Override
	public void reset() {
		this.timestamp = 0;
	}

}
