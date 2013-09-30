package dna.updates.generators;

import dna.graph.Graph;
import dna.updates.batch.Batch;

public class BatchCombinator extends BatchGenerator {

	private BatchGenerator[] bgs;

	public BatchCombinator(BatchGenerator... bgs) {
		super("Combinator");
		this.bgs = bgs;
	}

	@Override
	public Batch generate(Graph g) {
		int addN = 0;
		int remN = 0;
		int weightN = 0;
		int addE = 0;
		int remE = 0;
		int weightE = 0;
		long to = Long.MIN_VALUE;

		Batch[] batches = new Batch[this.bgs.length];
		for (int i = 0; i < this.bgs.length; i++) {
			batches[i] = this.bgs[i].generate(g);
			addN += batches[i].getNodeAdditionsCount();
			remN += batches[i].getNodeRemovalsCount();
			weightN += batches[i].getNodeWeightsCount();
			addE += batches[i].getEdgeAdditionsCount();
			remE += batches[i].getEdgeRemovalsCount();
			weightE += batches[i].getEdgeWeightsCount();
			to = Math.max(to, batches[i].getTo());
		}

		Batch b = new Batch(g.getGraphDatastructures(), g.getTimestamp(), to,
				addN, remN, weightN, addE, remE, weightE);
		for (Batch batch : batches) {
			b.addAll(batch.getAllUpdates());
		}

		return b;
	}

	@Override
	public void reset() {
	}

}
