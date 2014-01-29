package dna.updates.generators.random;

import java.util.ArrayList;

import dna.graph.Graph;
import dna.graph.weights.Weights.EdgeWeightSelection;
import dna.graph.weights.Weights.NodeWeightSelection;
import dna.updates.batch.Batch;
import dna.updates.generators.BatchGenerator;
import dna.util.parameters.IntParameter;
import dna.util.parameters.ObjectParameter;

public class RandomBatch extends BatchGenerator {

	private ArrayList<BatchGenerator> bgs;

	private int na = 0;

	private int nr = 0;

	private int nw = 0;

	private NodeWeightSelection nws = NodeWeightSelection.D_Rand;

	private int ea = 0;

	private int er = 0;

	private int ew = 0;

	private EdgeWeightSelection ews = EdgeWeightSelection.D_Rand;

	public RandomBatch(int na, int nr, int nw, NodeWeightSelection nws, int ea,
			int er, int ew, EdgeWeightSelection ews) {
		super("RandomBatch", new IntParameter("NA", na), new IntParameter("NR",
				nr), new IntParameter("NW", nw),
				new ObjectParameter("NWS", nws), new IntParameter("EA", ea),
				new IntParameter("ER", er), new IntParameter("EW", ew),
				new ObjectParameter("EWS", ews));

		this.bgs = new ArrayList<BatchGenerator>(6);

		this.na = na;
		this.nr = nr;
		this.nw = nw;
		this.nws = nws;
		this.ea = ea;
		this.er = er;
		this.ew = ew;
		this.ews = ews;

		this.init();
	}

	public RandomBatch(int na, int nr, int nw, NodeWeightSelection nws, int ea,
			int er) {
		super("RandomBatch", new IntParameter("NA", na), new IntParameter("NR",
				nr), new IntParameter("NW", nw),
				new ObjectParameter("NWS", nws), new IntParameter("EA", ea),
				new IntParameter("ER", er));

		this.bgs = new ArrayList<BatchGenerator>(6);

		this.na = na;
		this.nr = nr;
		this.nw = nw;
		this.nws = nws;
		this.ea = ea;
		this.er = er;

		this.init();
	}

	public RandomBatch(int na, int nr, int ea, int er, int ew,
			EdgeWeightSelection ews) {
		super("RandomBatch", new IntParameter("NA", na), new IntParameter("NR",
				nr), new IntParameter("EA", ea), new IntParameter("ER", er),
				new IntParameter("EW", ew), new ObjectParameter("EWS", ews));

		this.bgs = new ArrayList<BatchGenerator>(6);

		this.na = na;
		this.nr = nr;
		this.ea = ea;
		this.er = er;
		this.ew = ew;
		this.ews = ews;

		this.init();
	}

	public RandomBatch(int na, int nr, int ea, int er) {
		super("RandomBatch", new IntParameter("NA", na), new IntParameter("NR",
				nr), new IntParameter("EA", ea), new IntParameter("ER", er));

		this.bgs = new ArrayList<BatchGenerator>(6);

		this.na = na;
		this.nr = nr;
		this.ea = ea;
		this.er = er;

		this.init();
	}

	private void init() {
		if (na > 0) {
			this.bgs.add(new RandomNodeAdditions(na));
		}
		if (nr > 0) {
			this.bgs.add(new RandomNodeRemovals(nr));
		}
		if (nw > 0) {
			this.bgs.add(new RandomNodeWeights(this.nws, this.nw));
		}
		if (ea > 0) {
			this.bgs.add(new RandomEdgeAdditions(ea));
		}
		if (er > 0) {
			this.bgs.add(new RandomEdgeRemovals(er));
		}
		if (ew > 0) {
			this.bgs.add(new RandomEdgeWeights(this.ews, this.ew));
		}
	}

	@Override
	public Batch generate(Graph g) {
		Batch b = new Batch(g.getGraphDatastructures(), g.getTimestamp(),
				g.getTimestamp() + 1, this.na, this.nr, this.nw, this.ea,
				this.er, this.ew);

		for (BatchGenerator bg : this.bgs) {
			b.addAll(bg.generate(g).getAllUpdates());
		}

		return b;
	}

	@Override
	public void reset() {
	}

	@Override
	public boolean isFurtherBatchPossible(Graph g) {
		for (BatchGenerator bg : this.bgs) {
			if (bg.isFurtherBatchPossible(g)) {
				return true;
			}
		}
		return false;
	}

}
