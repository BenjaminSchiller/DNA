package dna.metrics.weights;

import dna.metrics.algorithms.IBeforeNA;
import dna.metrics.algorithms.IBeforeNR;
import dna.metrics.algorithms.IBeforeNW;
import dna.updates.update.NodeAddition;
import dna.updates.update.NodeRemoval;
import dna.updates.update.NodeWeight;

public class NodeWeightsU extends NodeWeights implements IBeforeNA, IBeforeNR,
		IBeforeNW {

	public NodeWeightsU(double binSize) {
		super("NodeWeightsU", binSize);
	}

	@Override
	public boolean init() {
		return this.compute();
	}

	@Override
	public boolean applyBeforeUpdate(NodeWeight nw) {
		this.distr.decr(this.getWeight(nw.getNode()));
		this.distr.incr(this.getWeight(nw.getWeight()));
		return true;
	}

	@Override
	public boolean applyBeforeUpdate(NodeRemoval nr) {
		this.distr.decr(this.getWeight(nr.getNode()));
		return true;
	}

	@Override
	public boolean applyBeforeUpdate(NodeAddition na) {
		this.distr.incr(this.getWeight(na.getNode()));
		return true;
	}

}
