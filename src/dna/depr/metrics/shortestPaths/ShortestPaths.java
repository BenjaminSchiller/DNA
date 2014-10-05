package dna.depr.metrics.shortestPaths;

import dna.depr.metrics.MetricOld;
import dna.metrics.IMetric;
import dna.series.data.Distribution;
import dna.series.data.NodeNodeValueList;
import dna.series.data.NodeValueList;
import dna.series.data.Value;
import dna.util.ArrayUtils;
import dna.util.DataUtils;

public abstract class ShortestPaths extends MetricOld {

	public ShortestPaths(String name, ApplicationType type) {
		super(name, type, IMetric.MetricType.exact);
	}

	protected double cpl;

	protected double diam;

	protected long existingPaths;

	protected long[] spl;

	@Override
	public void init_() {
		this.cpl = 0;
		this.diam = 0;
		this.existingPaths = 0;
		this.spl = new long[0];
	}

	@Override
	public void reset_() {
		this.cpl = 0;
		this.diam = 0;
		this.existingPaths = 0;
		this.spl = null;
	}

	@Override
	public Value[] getValues() {
		double possible = this.g.getNodeCount() * (this.g.getNodeCount() - 1);
		Value cpl = new Value("cpl", this.cpl);
		Value diam = new Value("diam", this.diam);
		Value conn = new Value("conn", (double) this.existingPaths / possible);
		Value poss = new Value("possiblePaths", possible);
		Value ex = new Value("existingPaths", this.existingPaths);
		return new Value[] { cpl, diam, conn, poss, ex };
	}

	@Override
	public Distribution[] getDistributions() {
		double possible = this.g.getNodeCount() * (this.g.getNodeCount() - 1);
		double[] v = new double[this.spl.length];
		for (int i = 0; i < this.spl.length; i++) {
			v[i] = (double) this.spl[i] / possible;
		}
		Distribution spl = new Distribution("spl", v);
		return new Distribution[] { spl };
	}

	@Override
	public NodeValueList[] getNodeValueLists() {
		return new NodeValueList[0];
	}

	@Override
	public NodeNodeValueList[] getNodeNodeValueLists() {
		return new NodeNodeValueList[] {};
	}

	@Override
	public boolean equals(MetricOld m) {
		if (m == null || !(m instanceof ShortestPaths)) {
			return false;
		}
		ShortestPaths sp = (ShortestPaths) m;
		boolean success = true;
		success &= DataUtils.equals(this.cpl, sp.cpl, "cpl");
		success &= DataUtils.equals(this.diam, sp.diam, "diam");
		success &= DataUtils.equals(this.existingPaths, sp.existingPaths,
				"existingPaths");
		success &= ArrayUtils.equals(this.spl, sp.spl, "spl");
		return success;
	}

}
