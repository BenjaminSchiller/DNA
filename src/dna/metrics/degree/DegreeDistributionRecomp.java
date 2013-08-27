package dna.metrics.degree;

import java.util.Collection;

import dna.graph.Graph;
import dna.graph.IElement;
import dna.graph.nodes.DirectedNode;
import dna.graph.nodes.UndirectedNode;
import dna.series.data.NodeValueList;
import dna.updates.Batch;
import dna.updates.Update;
import dna.util.ArrayUtils;
import dna.util.Log;

@SuppressWarnings("rawtypes")
public class DegreeDistributionRecomp extends DegreeDistribution {

	public DegreeDistributionRecomp() {
		super("degreeDistributionRecomp", ApplicationType.Recomputation,
				MetricType.exact);
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

	@SuppressWarnings("unchecked")
	@Override
	public boolean compute() {
		this.degreeDistribution = new double[0];
		this.inDegreeDistribution = new double[0];
		this.outDegreeDistribution = new double[0];
		this.nodes = this.g.getNodeCount();
		this.edges = this.g.getEdgeCount();
		if (DirectedNode.class.isAssignableFrom(this.g.getGraphDatastructures()
				.getNodeType())) {
			for (IElement nUncasted : this.g.getNodes()) {
				DirectedNode n = (DirectedNode) nUncasted;
				this.degreeDistribution = ArrayUtils.incr(
						this.degreeDistribution, n.getDegree());
				this.inDegreeDistribution = ArrayUtils.incr(
						this.inDegreeDistribution, n.getInDegree());
				this.outDegreeDistribution = ArrayUtils.incr(
						this.outDegreeDistribution, n.getOutDegree());
			}
			ArrayUtils.divide(this.degreeDistribution, this.nodes);
			ArrayUtils.divide(this.inDegreeDistribution, this.nodes);
			ArrayUtils.divide(this.outDegreeDistribution, this.nodes);
			return true;
		} else if (UndirectedNode.class.isAssignableFrom(this.g
				.getGraphDatastructures().getNodeType())) {
			for (IElement nUncasted : this.g.getNodes()) {
				UndirectedNode n = (UndirectedNode) nUncasted;
				this.degreeDistribution = ArrayUtils.incr(
						this.degreeDistribution, n.getDegree());
			}
			ArrayUtils.divide(this.degreeDistribution, this.nodes);
			return true;
		}
		Log.error("DD - unsupported node type "
				+ this.g.getGraphDatastructures().getNodeType());
		return false;
	}

	@Override
	protected void init_() {
		this.degreeDistribution = new double[0];
		this.inDegreeDistribution = new double[0];
		this.outDegreeDistribution = new double[0];
		this.nodes = 0;
		this.edges = 0;
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

	@Override
	protected NodeValueList[] getNodeValueLists() {
		// TODO Auto-generated method stub
		return null;
	}

}
