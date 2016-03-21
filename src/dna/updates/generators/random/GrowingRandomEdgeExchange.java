package dna.updates.generators.random;

import dna.graph.IGraph;
import dna.updates.batch.Batch;
import dna.updates.generators.BatchGenerator;
import dna.util.parameters.IntParameter;

public class GrowingRandomEdgeExchange extends BatchGenerator {

	private int time;

	private int edges;

	private int maxFails;

	public GrowingRandomEdgeExchange(int edges, int maxFails) {
		super("GrowingRandomEdgeExchange", new IntParameter("EDGES", edges),
				new IntParameter("MAX_FAILS", maxFails));
		this.edges = edges;
		this.maxFails = maxFails;
		this.time = 1;
	}

	@Override
	public Batch generate(IGraph g) {
		Batch b = this.getCurrentBatch().generate(g);
		this.time++;
		return b;
	}

	private RandomEdgeExchange getCurrentBatch() {
		return new RandomEdgeExchange(this.time * this.edges, this.maxFails);
	}

	@Override
	public void reset() {
		this.time = 1;
	}

	@Override
	public boolean isFurtherBatchPossible(IGraph g) {
		return this.getCurrentBatch().isFurtherBatchPossible(g);
	}

}
