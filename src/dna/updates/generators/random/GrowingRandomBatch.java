package dna.updates.generators.random;

import dna.graph.Graph;
import dna.graph.weightsNew.Weight.WeightSelection;
import dna.graph.weightsNew.Weight.WeightType;
import dna.updates.batch.Batch;
import dna.updates.generators.BatchGenerator;

public class GrowingRandomBatch extends BatchGenerator {

	private int na;
	private int nr;
	private int nw;
	private WeightType nwt;
	private WeightSelection nws;
	private int ea;
	private int er;
	private int ew;
	private WeightType ewt;
	private WeightSelection ews;

	int time = 0;

	public GrowingRandomBatch(RandomBatch rb) {
		super("GrowingRandomBatch");

		this.na = rb.getNa();
		this.nr = rb.getNr();
		this.nw = rb.getNw();
		this.nwt = rb.getNwt();
		this.nws = rb.getNws();
		this.ea = rb.getEa();
		this.er = rb.getEr();
		this.ew = rb.getEw();
		this.ewt = rb.getEwt();
		this.ews = rb.getEws();

		this.time = 1;
	}

	@Override
	public Batch generate(Graph g) {
		Batch b = this.getCurrentBatch().generate(g);
		this.time++;
		return b;
	}

	private RandomBatch getCurrentBatch() {
		return new RandomBatch(na * time, nr * time, nw * time, nwt, nws, ea
				* time, er * time, ew * time, ewt, ews);
	}

	@Override
	public void reset() {
		this.time = 1;
	}

	@Override
	public boolean isFurtherBatchPossible(Graph g) {
		return this.getCurrentBatch().isFurtherBatchPossible(g);
	}
}
