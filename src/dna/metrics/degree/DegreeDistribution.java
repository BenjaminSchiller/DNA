package dna.metrics.degree;

import dna.diff.Diff;
import dna.diff.DiffNotApplicableException;
import dna.graph.old.OldEdge;
import dna.graph.old.OldGraph;
import dna.graph.old.OldNode;
import dna.metrics.Metric;
import dna.series.data.Distribution;
import dna.series.data.Value;
import dna.util.ArrayUtils;

public class DegreeDistribution extends Metric {
	public DegreeDistribution() {
		super("degreeDistribution", true, false, false);
	}

	private int[] degrees;

	private int[] inDegrees;

	private int[] outDegrees;

	private int[] D;

	private int[] DIn;

	private int[] DOut;

	private int edges;

	@Override
	protected void init(OldGraph g) {
		this.degrees = new int[this.g.getNodes().length];
		this.inDegrees = new int[this.g.getNodes().length];
		this.outDegrees = new int[this.g.getNodes().length];
		this.D = new int[0];
		this.DIn = new int[0];
		this.DOut = new int[0];
		this.edges = 0;
	}

	public int[] getDegrees() {
		return this.degrees;
	}

	public int[] getInDegrees() {
		return this.inDegrees;
	}

	public int[] getOutDegrees() {
		return this.outDegrees;
	}

	public int[] getD() {
		return this.D;
	}

	public int[] getDIn() {
		return this.DIn;
	}

	public int[] getDOut() {
		return this.DOut;
	}

	public int getEdges() {
		return this.edges;
	}

	@Override
	protected boolean compute_() {
		for (OldNode n : this.g.getNodes()) {
			this.inDegrees[n.getIndex()] = n.getIn().size();
			this.outDegrees[n.getIndex()] = n.getOut().size();
			this.degrees[n.getIndex()] = n.getIn().size() + n.getOut().size();
			this.DIn = ArrayUtils.incr(this.DIn, this.inDegrees[n.getIndex()]);
			this.DOut = ArrayUtils.incr(this.DOut,
					this.outDegrees[n.getIndex()]);
			this.D = ArrayUtils.incr(this.D, this.degrees[n.getIndex()]);
		}
		this.edges = this.g.getEdges().size();

		return true;
	}

	@Override
	protected boolean applyBeforeDiff_(Diff d) {
		int added = 0;
		int removed = 0;

		for (OldEdge e : d.getAddedEdges()) {
			if (this.g.containsEdge(e)) {
				continue;
			}
			added++;

			int v = e.getSrc().getIndex();
			int w = e.getDst().getIndex();

			this.D = ArrayUtils.decr(this.D, this.degrees[v]);
			this.DOut = ArrayUtils.decr(this.DOut, this.outDegrees[v]);
			this.D = ArrayUtils.decr(this.D, this.degrees[w]);
			this.DIn = ArrayUtils.decr(this.DIn, this.inDegrees[w]);

			this.degrees = ArrayUtils.incr(this.degrees, v);
			this.outDegrees = ArrayUtils.incr(this.outDegrees, v);
			this.degrees = ArrayUtils.incr(this.degrees, w);
			this.inDegrees = ArrayUtils.incr(this.inDegrees, w);

			this.D = ArrayUtils.incr(this.D, this.degrees[v]);
			this.DOut = ArrayUtils.incr(this.DOut, this.outDegrees[v]);
			this.D = ArrayUtils.incr(this.D, this.degrees[w]);
			this.DIn = ArrayUtils.incr(this.DIn, this.inDegrees[w]);
		}
		this.edges += added;

		for (OldEdge e : d.getRemovedEdges()) {
			if (!this.g.containsEdge(e)) {
				continue;
			}
			removed++;

			int v = e.getSrc().getIndex();
			int w = e.getDst().getIndex();

			this.D = ArrayUtils.decr(this.D, this.degrees[v]);
			this.DOut = ArrayUtils.decr(this.DOut, this.outDegrees[v]);
			this.D = ArrayUtils.decr(this.D, this.degrees[w]);
			this.DIn = ArrayUtils.decr(this.DIn, this.inDegrees[w]);

			this.degrees = ArrayUtils.decr(this.degrees, v);
			this.outDegrees = ArrayUtils.decr(this.outDegrees, v);
			this.degrees = ArrayUtils.decr(this.degrees, w);
			this.inDegrees = ArrayUtils.decr(this.inDegrees, w);

			this.D = ArrayUtils.incr(this.D, this.degrees[v]);
			this.DOut = ArrayUtils.incr(this.DOut, this.outDegrees[v]);
			this.D = ArrayUtils.incr(this.D, this.degrees[w]);
			this.DIn = ArrayUtils.incr(this.DIn, this.inDegrees[w]);
		}
		this.edges -= removed;

		this.D = ArrayUtils.truncate(this.D, 0);
		this.DIn = ArrayUtils.truncate(this.DIn, 0);
		this.DOut = ArrayUtils.truncate(this.DOut, 0);

		return true;
	}

	@Override
	protected boolean applyAfterDiff_(Diff d) throws DiffNotApplicableException {
		throw new DiffNotApplicableException("after diff");
	}

	@Override
	protected boolean applyAfterEdgeAddition_(Diff d, OldEdge e)
			throws DiffNotApplicableException {
		throw new DiffNotApplicableException("edge addition");
	}

	@Override
	protected boolean applyAfterEdgeRemoval_(Diff d, OldEdge e)
			throws DiffNotApplicableException {
		throw new DiffNotApplicableException("edge removal");
	}

	@Override
	public boolean equals(Metric m) {
		if (!(m instanceof DegreeDistribution)) {
			return false;
		}
		DegreeDistribution dd = (DegreeDistribution) m;
		if (!ArrayUtils.equals(this.D, dd.getD())) {
			System.out.println("diff @ D");
			return false;
		}
		if (!ArrayUtils.equals(this.DIn, dd.getDIn())) {
			System.out.println("diff @ DIn");
			return false;
		}
		if (!ArrayUtils.equals(this.DOut, dd.getDOut())) {
			System.out.println("diff @ DOut");
			return false;
		}
		if (!ArrayUtils.equals(this.degrees, dd.getDegrees())) {
			System.out.println("diff @ degrees");
			return false;
		}
		if (!ArrayUtils.equals(this.inDegrees, dd.getInDegrees())) {
			System.out.println("diff @ inDegrees");
			return false;
		}
		if (!ArrayUtils.equals(this.outDegrees, dd.getOutDegrees())) {
			System.out.println("diff @ outDegrees");
			return false;
		}
		if (this.edges != dd.getEdges()) {
			System.out.println("diff @ edges");
			return false;
		}
		return true;
	}

	@Override
	public boolean cleanupApplication() {
		return true;
	}

	@Override
	public void reset_() {
		this.D = null;
		this.DIn = null;
		this.DOut = null;
		this.edges = 0;
		this.degrees = null;
		this.inDegrees = null;
		this.outDegrees = null;
	}

	@Override
	protected Value[] getValues() {
		Value v1 = new Value("edges", this.edges);
		return new Value[] { v1 };
	}

	@Override
	protected Distribution[] getDistributions() {
		Distribution d1 = new Distribution("degree", this.makeDistribution(
				this.D, this.getNodes()));
		Distribution d2 = new Distribution("inDegree", this.makeDistribution(
				this.DIn, this.getNodes()));
		Distribution d3 = new Distribution("outDegree", this.makeDistribution(
				this.DOut, this.getNodes()));
		return new Distribution[] { d1, d2, d3 };
	}

	private double[] makeDistribution(int[] values, int total) {
		double[] distribution = new double[values.length];
		for (int i = 0; i < values.length; i++) {
			distribution[i] = (double) values[i] / (double) total;
		}
		return distribution;
	}

}
