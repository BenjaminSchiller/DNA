package dna.metrics.streaM_k;

import dna.graph.IGraph;
import dna.metrics.IMetric;
import dna.metrics.Metric;
import dna.series.data.Value;
import dna.series.data.distr.Distr;
import dna.series.data.nodevaluelists.NodeNodeValueList;
import dna.series.data.nodevaluelists.NodeValueList;
import dna.updates.batch.Batch;
import dna.util.ArrayUtils;
import dna.util.parameters.IntArrayParameter;

public abstract class AdjacencyMatrix_k extends Metric {

	protected int[] nodes;

	protected int key;

	public AdjacencyMatrix_k(String name, int[] nodes) {
		super(name, MetricType.exact, new IntArrayParameter("nodes", nodes));
		this.nodes = nodes;
	}

	@Override
	public Value[] getValues() {
		Value key = new Value("key", this.key);
		return new Value[] { key };
	}

	@Override
	public Distr<?, ?>[] getDistributions() {
		return new Distr[0];
	}

	@Override
	public NodeValueList[] getNodeValueLists() {
		return new NodeValueList[0];
	}

	@Override
	public NodeNodeValueList[] getNodeNodeValueLists() {
		return new NodeNodeValueList[0];
	}

	@Override
	public boolean isComparableTo(IMetric m) {
		if (m == null || !(m instanceof AdjacencyMatrix_k)) {
			return false;
		}
		AdjacencyMatrix_k m_ = (AdjacencyMatrix_k) m;
		return ArrayUtils.equals(m_.nodes, this.nodes);
	}

	@Override
	public boolean equals(IMetric m) {
		if (m == null || !(m instanceof AdjacencyMatrix_k)) {
			return false;
		}
		AdjacencyMatrix_k m_ = (AdjacencyMatrix_k) m;
		if (!ArrayUtils.equals(m_.nodes, this.nodes)) {
			return false;
		}
		return m_.key == this.key;
	}

	@Override
	public boolean isApplicable(IGraph g) {
		return !g.isDirected();
	}

	@Override
	public boolean isApplicable(Batch b) {
		return true;
	}

}
