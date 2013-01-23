package dynamicGraphs.metrics.degree;

import dynamicGraphs.diff.Diff;
import dynamicGraphs.graph.Edge;
import dynamicGraphs.graph.Graph;
import dynamicGraphs.graph.Node;
import dynamicGraphs.metrics.Metric;
import dynamicGraphs.util.ArrayUtils;

public class DegreeDistribution extends Metric {
	public DegreeDistribution(Graph g) {
		super(g, "DegreeDistribution", true);
		this.degrees = new int[this.g.getNodes().length];
		this.inDegrees = new int[this.g.getNodes().length];
		this.outDegrees = new int[this.g.getNodes().length];
		this.D = new int[0];
		this.DIn = new int[0];
		this.DOut = new int[0];
		this.edges = 0;
	}

	private int[] degrees;

	private int[] inDegrees;

	private int[] outDegrees;

	private int[] D;

	private int[] DIn;

	private int[] DOut;

	private int edges;

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
	protected boolean computeMetric() {
		for (Node n : this.g.getNodes()) {
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
	protected boolean applyDiffBefore(Diff d) {
		int added = 0;
		int removed = 0;

		for (Edge e : d.getAddedEdges()) {
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

		for (Edge e : d.getRemovedEdges()) {
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

		System.out.println("Diff@DD: A(" + added + "/"
				+ d.getAddedEdges().size() + ") R(" + removed + "/"
				+ d.getRemovedEdges().size() + ")");

		this.D = ArrayUtils.truncate(this.D, 0);
		this.DIn = ArrayUtils.truncate(this.DIn, 0);
		this.DOut = ArrayUtils.truncate(this.DOut, 0);

		return true;
	}

	@Override
	protected boolean applyDiffAfter(Diff d) {
		return true;
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

}
