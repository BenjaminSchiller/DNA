package dna.updates.generators;

import dna.graph.Graph;
import dna.updates.batch.Batch;

public interface IBatchGenerator {
	public Batch generate(Graph g);

	public void reset();

	public boolean isFurtherBatchPossible(Graph g);
}
