package dna.metrics.degree;

import java.util.Collection;

import dna.graph.Graph;
import dna.graph.directed.DirectedNode;
import dna.graph.undirected.UndirectedNode;
import dna.updates.Batch;
import dna.updates.Update;
import dna.util.ArrayUtils;
import dna.util.Log;

@SuppressWarnings("rawtypes")
public class DegreeDistributionRecomp extends DegreeDistribution {

	public DegreeDistributionRecomp() {
		super("degreeDistributionRecomp", ApplicationType.Recomputation);
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
			for (DirectedNode n : (Collection<DirectedNode>) this.g.getNodes()) {
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
			for (UndirectedNode n : (Collection<UndirectedNode>) this.g
					.getNodes()) {
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
	public boolean recompute() {
		return this.compute();
	}

	@Override
	protected void init_(Graph g) {
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

}
