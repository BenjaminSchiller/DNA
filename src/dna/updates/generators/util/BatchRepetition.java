package dna.updates.generators.util;

import dna.graph.IGraph;
import dna.updates.batch.Batch;
import dna.updates.generators.BatchGenerator;

public class BatchRepetition extends BatchGenerator {

	private BatchRepetitionWrapper[] bgs;

	private int index;

	private int total;

	public BatchRepetition(int count, BatchGenerator... bgs) {
		this(getWrappers(bgs, count));
	}

	private static BatchRepetitionWrapper[] getWrappers(BatchGenerator[] bgs,
			int count) {
		BatchRepetitionWrapper[] wrapper = new BatchRepetitionWrapper[bgs.length];
		for (int i = 0; i < bgs.length; i++) {
			wrapper[i] = new BatchRepetitionWrapper(bgs[i], count);
		}
		return wrapper;
	}

	public BatchRepetition(BatchRepetitionWrapper... bgs) {
		super("BatchRepetition");
		this.bgs = bgs;
		this.index = 0;
		this.total = 0;
		for (BatchRepetitionWrapper bg : this.bgs) {
			this.total += bg.getCount();
		}
	}

	@Override
	public Batch generate(IGraph g) {
		Batch b = this.getNext().generate(g);
		this.index = (index + 1) % this.total;
		return b;
	}

	protected BatchGenerator getNext() {
		int sum = 0;
		for (BatchRepetitionWrapper bg : this.bgs) {
			sum += bg.getCount();
			if (sum > this.index) {
				return bg.getBg();
			}
		}
		return null;
	}

	@Override
	public void reset() {
		this.index = 0;
	}

	@Override
	public boolean isFurtherBatchPossible(IGraph g) {
		return this.getNext().isFurtherBatchPossible(g);
	}

	public static class BatchRepetitionWrapper {
		private BatchGenerator bg;

		private int count;

		public BatchRepetitionWrapper(BatchGenerator bg, int count) {
			this.bg = bg;
			this.count = count;
		}

		public BatchGenerator getBg() {
			return bg;
		}

		public int getCount() {
			return count;
		}
	}

}
