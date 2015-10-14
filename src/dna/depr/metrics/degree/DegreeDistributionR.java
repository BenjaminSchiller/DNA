package dna.depr.metrics.degree;

import dna.graph.Graph;
import dna.graph.IElement;
import dna.graph.nodes.DirectedNode;
import dna.graph.nodes.UndirectedNode;
import dna.metrics.IMetric;
import dna.series.data.distr.IntDistr;
import dna.updates.batch.Batch;
import dna.updates.update.Update;
import dna.util.Log;

public class DegreeDistributionR extends DegreeDistribution {

	public DegreeDistributionR() {
		super("DegreeDistributionR", ApplicationType.Recomputation,
				IMetric.MetricType.exact);
	}

	@Override
	public boolean applyBeforeBatch(Batch b) {
		return false;
	}

	@Override
	public boolean applyAfterBatch(Batch b) {
		return false;
	}

	@Override
	public boolean applyBeforeUpdate(Update u) {
		return false;
	}

	@Override
	public boolean applyAfterUpdate(Update u) {
		return false;
	}

	@Override
	public boolean compute() {
		this.degree = new IntDistr(degreeName, Long.valueOf(this.g
				.getNodeCount()), new long[0]);
		this.inDegree = new IntDistr(inDegreeName, Long.valueOf(this.g
				.getNodeCount()), new long[0]);
		this.outDegree = new IntDistr(outDegreeName, Long.valueOf(this.g
				.getNodeCount()), new long[0]);
		this.nodes = this.g.getNodeCount();
		this.edges = this.g.getEdgeCount();
		if (DirectedNode.class.isAssignableFrom(this.g.getGraphDatastructures()
				.getNodeType())) {
			for (IElement nUncasted : this.g.getNodes()) {
				DirectedNode n = (DirectedNode) nUncasted;
				this.degree.incr(n.getDegree());
				this.inDegree.incr(n.getInDegree());
				this.outDegree.incr(n.getOutDegree());
			}
			return true;
		} else if (UndirectedNode.class.isAssignableFrom(this.g
				.getGraphDatastructures().getNodeType())) {
			for (IElement nUncasted : this.g.getNodes()) {
				UndirectedNode n = (UndirectedNode) nUncasted;
				this.degree.incr(n.getDegree());
			}
			return true;
		}
		Log.error("DD - unsupported node type "
				+ this.g.getGraphDatastructures().getNodeType());
		return false;
	}

	@Override
	public boolean isApplicable(Graph g) {
		return DirectedNode.class.isAssignableFrom(g.getGraphDatastructures()
				.getNodeType())
				|| UndirectedNode.class.isAssignableFrom(g
						.getGraphDatastructures().getNodeType());
	}

	@Override
	public boolean isApplicable(Batch b) {
		return DirectedNode.class.isAssignableFrom(b.getGraphDatastructures()
				.getNodeType())
				|| UndirectedNode.class.isAssignableFrom(b
						.getGraphDatastructures().getNodeType());
	}

}
