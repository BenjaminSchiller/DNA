package dna.updates.generators.util;

import dna.graph.IGraph;
import dna.updates.batch.Batch;
import dna.updates.generators.BatchGenerator;

public class EmptyBatch extends BatchGenerator {

	public EmptyBatch() {
		super("EmptyBatch");
	}

	@Override
	public Batch generate(IGraph g) {
		return new Batch(g.getGraphDatastructures(), g.getTimestamp(),
				g.getTimestamp() + 1);
	}

	@Override
	public void reset() {
	}

	@Override
	public boolean isFurtherBatchPossible(IGraph g) {
		return true;
	}

}
