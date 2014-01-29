package dna.updates.generators.util;

import dna.graph.Graph;
import dna.updates.batch.Batch;
import dna.updates.generators.BatchGenerator;
import dna.util.ArrayUtils;
import dna.util.parameters.IntParameter;

public class Timestamped extends BatchGenerator {

	private BatchGenerator bg;

	private int add;

	public Timestamped(BatchGenerator bg, int add) {
		super("Timestamped" + bg.getNamePlain(), ArrayUtils.append(
				bg.getParameters(), new IntParameter("ADD", add)));
		this.bg = bg;
		this.add = add;
	}

	@Override
	public Batch generate(Graph g) {
		Batch b = this.bg.generate(g);
		b.setTo(b.getFrom() + this.add);
		return b;
	}

	@Override
	public void reset() {
	}

	@Override
	public boolean isFurtherBatchPossible(Graph g) {
		return this.bg.isFurtherBatchPossible(g);
	}

}
