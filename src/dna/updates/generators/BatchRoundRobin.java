package dna.updates.generators;

import dna.graph.Graph;
import dna.updates.batch.Batch;
import dna.util.parameters.Parameter;

public class BatchRoundRobin extends BatchGenerator {

	private BatchGenerator[] bgs;

	int index = 0;

	public BatchRoundRobin(BatchGenerator... bgs) {
		super("BatchRoundRobin");
		this.bgs = bgs;
	}

	@Override
	public Batch generate(Graph g) {
		Batch b = this.bgs[this.index].generate(g);
		this.index = (this.index + 1) % this.bgs.length;
		return b;
	}

	@Override
	public void reset() {
		this.index = 0;
	}

}
