package dna.updates.generators;

import dna.graph.IGraph;
import dna.updates.batch.Batch;

public interface IBatchGenerator {
	public Batch generate(IGraph g);

	public void reset();

	public boolean isFurtherBatchPossible(IGraph g);
}
