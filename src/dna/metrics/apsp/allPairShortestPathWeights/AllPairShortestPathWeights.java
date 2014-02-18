package dna.metrics.apsp.allPairShortestPathWeights;

import dna.graph.Graph;
import dna.graph.edges.DirectedIntWeightedEdge;
import dna.graph.edges.UndirectedIntWeightedEdge;
import dna.graph.nodes.Node;
import dna.metrics.Metric;
import dna.series.data.Distribution;
import dna.series.data.DistributionInt;
import dna.series.data.NodeNodeValueList;
import dna.series.data.NodeValueList;
import dna.series.data.Value;
import dna.updates.batch.Batch;
import dna.util.ArrayUtils;

public abstract class AllPairShortestPathWeights extends Metric {

	protected DistributionInt apsp;

	public AllPairShortestPathWeights(String name, ApplicationType type) {
		super(name, type, MetricType.exact);

	}

	@Override
	public void init_() {
		this.apsp = new DistributionInt("APSP");
	}

	@Override
	public void reset_() {
		this.apsp = new DistributionInt("APSP");
	}

	@Override
	public Value[] getValues() {
		this.apsp.truncate();

		Value v1 = new Value("existingPaths", this.apsp.getDenominator());
		Value v2 = new Value("possiblePaths", this.g.getNodeCount()
				* (this.g.getNodeCount() - 1));
		Value v3 = new Value("characteristicPathLength",
				this.apsp.computeAverage());
		Value v4 = new Value("diameter", this.apsp.getMax());

		return new Value[] { v1, v2, v3, v4 };
	}

	@Override
	public NodeValueList[] getNodeValueLists() {
		return new NodeValueList[] {};
	}

	@Override
	public NodeNodeValueList[] getNodeNodeValueLists() {
		return new NodeNodeValueList[] {};
	}

	@Override
	public Distribution[] getDistributions() {
		this.apsp.truncate();
		return new Distribution[] { this.apsp };
	}

	@Override
	public boolean equals(Metric m) {
		if (!(m instanceof AllPairShortestPathWeights)) {
			return false;
		}
		return ArrayUtils.equals(this.apsp.getIntValues(),
				((AllPairShortestPathWeights) m).apsp.getIntValues());
	}

	@Override
	public boolean isComparableTo(Metric m) {
		return m != null && m instanceof AllPairShortestPathWeights;
	}

	@Override
	public boolean isApplicable(Graph g) {
		return DirectedIntWeightedEdge.class.isAssignableFrom(g
				.getGraphDatastructures().getEdgeType())
				|| UndirectedIntWeightedEdge.class.isAssignableFrom(g
						.getGraphDatastructures().getEdgeType());
	}

	@Override
	public boolean isApplicable(Batch b) {
		return Node.class.isAssignableFrom(b.getGraphDatastructures()
				.getNodeType());
	}

}
