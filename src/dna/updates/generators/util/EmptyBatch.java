package dna.updates.generators.util;

import dna.graph.Graph;
import dna.updates.batch.Batch;
import dna.updates.generators.BatchGenerator;

public class EmptyBatch extends BatchGenerator {

	public EmptyBatch() {
		super("EmptyBatch");
	}

	@Override
	public Batch generate(Graph g) {
		return new Batch(g.getGraphDatastructures(), g.getTimestamp(),
				g.getTimestamp() + 1);
	}

	@Override
	public void reset() {
	}

	@Override
	public boolean isFurtherBatchPossible(Graph g) {
		return true;
	}

}
