package dna.metrics.weights;

import dna.graph.IElement;
import dna.graph.edges.IEdge;
import dna.metrics.algorithms.IBeforeEA;
import dna.metrics.algorithms.IBeforeER;
import dna.metrics.algorithms.IBeforeEW;
import dna.metrics.algorithms.IBeforeNR;
import dna.updates.update.EdgeAddition;
import dna.updates.update.EdgeRemoval;
import dna.updates.update.EdgeWeight;
import dna.updates.update.NodeRemoval;

public class EdgeWeightsU extends EdgeWeights implements IBeforeEA, IBeforeER,
		IBeforeEW, IBeforeNR {

	public EdgeWeightsU(double binSize) {
		super("EdgeWeightsU", MetricType.exact, binSize);
	}

	@Override
	public boolean init() {
		return this.compute();
	}

	@Override
	public boolean applyBeforeUpdate(EdgeWeight ew) {
		this.distr.decr(this.getWeight(ew.getEdge()));
		this.distr.incr(this.getWeight(ew.getWeight()));
		return true;
	}

	@Override
	public boolean applyBeforeUpdate(EdgeRemoval er) {
		this.distr.decr(this.getWeight(er.getEdge()));
		return true;
	}

	@Override
	public boolean applyBeforeUpdate(EdgeAddition ea) {
		this.distr.incr(this.getWeight(ea.getEdge()));
		return true;
	}

	@Override
	public boolean applyBeforeUpdate(NodeRemoval nr) {
		for (IElement e : nr.getNode().getEdges()) {
			this.distr.decr(this.getWeight((IEdge) e));
		}
		return true;
	}
}
