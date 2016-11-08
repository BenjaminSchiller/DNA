package dna.metrics.weights;

import dna.graph.Graph;
import dna.graph.IElement;
import dna.graph.edges.IEdge;
import dna.graph.weights.IWeightedEdge;
import dna.graph.weights.multi.MultiWeight;
import dna.metrics.algorithms.IBeforeEA;
import dna.metrics.algorithms.IBeforeER;
import dna.metrics.algorithms.IBeforeEW;
import dna.metrics.algorithms.IBeforeNR;
import dna.updates.batch.Batch;
import dna.updates.update.EdgeAddition;
import dna.updates.update.EdgeRemoval;
import dna.updates.update.EdgeWeight;
import dna.updates.update.NodeRemoval;

public class EdgeWeightsMultiU extends EdgeWeights implements IBeforeEA,
		IBeforeER, IBeforeEW, IBeforeNR {

	protected int index;

	protected String name;

	public EdgeWeightsMultiU(int index, double binSize) {
		this("EdgeWeightsU-" + index + "-" + binSize, index, binSize);
	}

	public EdgeWeightsMultiU(String name, int index, double binSize) {
		super(name, binSize);
		this.name = name;
		this.index = index;
	}

	@Override
	public boolean init() {
		return this.compute();
	}

	@Override
	public boolean applyBeforeUpdate(EdgeWeight ew) {
		this.distr.decr(this.getWeight(ew.getEdge()));
		this.distr.incr(this.getWeight(ew.getWeight(), this.index));
		return true;
	}

	@Override
	public boolean applyBeforeUpdate(EdgeRemoval er) {
		this.distr.decr(this.getWeight(er.getEdge(), this.index));
		return true;
	}

	@Override
	public boolean applyBeforeUpdate(EdgeAddition ea) {
		this.distr.incr(this.getWeight(ea.getEdge(), this.index));
		return true;
	}

	@Override
	public boolean applyBeforeUpdate(NodeRemoval nr) {
		for (IElement e : nr.getNode().getEdges()) {
			this.distr.decr(this.getWeight((IEdge) e), this.index);
		}
		return true;
	}

	@Override
	public boolean isApplicable(Graph g) {
		return g.getGraphDatastructures().isEdgeType(IWeightedEdge.class)
				&& g.getGraphDatastructures().isEdgeWeightType(
						MultiWeight.class);
	}

	@Override
	public boolean isApplicable(Batch b) {
		return b.getGraphDatastructures().isEdgeType(IWeightedEdge.class)
				&& b.getGraphDatastructures().isEdgeWeightType(
						MultiWeight.class);
	}
}
