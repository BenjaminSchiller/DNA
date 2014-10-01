package dna.depr.metrics.weights;

import dna.metrics.IMetricNew;
import dna.updates.batch.Batch;
import dna.updates.update.NodeAddition;
import dna.updates.update.NodeRemoval;
import dna.updates.update.NodeWeight;
import dna.updates.update.Update;

public class NodeWeightsU extends NodeWeights {

	public NodeWeightsU(double binSize) {
		super("NodeWeightsU", ApplicationType.BeforeUpdate, IMetricNew.MetricType.exact,
				binSize);
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
		if (u instanceof NodeAddition) {
			this.distr.incr(this.getWeight(((NodeAddition) u).getNode()));
		} else if (u instanceof NodeRemoval) {
			this.distr.decr(this.getWeight(((NodeRemoval) u).getNode()));
		} else if (u instanceof NodeWeight) {
			this.distr.decr(this.getWeight(((NodeWeight) u).getNode()));
			this.distr.incr(this.getWeight(((NodeWeight) u).getWeight()));
		}
		return true;
	}

	@Override
	public boolean applyAfterUpdate(Update u) {
		return false;
	}

}
