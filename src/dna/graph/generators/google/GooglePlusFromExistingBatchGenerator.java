package dna.graph.generators.google;

import dna.graph.Graph;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.edges.DirectedEdge;
import dna.updates.Batch;
import dna.updates.directed.DirectedBatchGenerator;

public class GooglePlusFromExistingBatchGenerator extends
		DirectedBatchGenerator {

	private int counter;
	private Batch<DirectedEdge>[] batches;

	public GooglePlusFromExistingBatchGenerator(String name,
			GraphDataStructure datastructures, Batch<DirectedEdge>[] b) {
		super(name, datastructures);
		this.batches = b;
		this.counter = 0;
	}

	@Override
	public Batch<DirectedEdge> generate(Graph graph) {
		Batch<DirectedEdge> b = this.batches[counter];
		counter++;
		return b;

	}

	@Override
	public void reset() {
	}

}
