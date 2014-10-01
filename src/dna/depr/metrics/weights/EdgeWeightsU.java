package dna.depr.metrics.weights;

import dna.metrics.IMetricNew;
import dna.updates.batch.Batch;
import dna.updates.update.EdgeAddition;
import dna.updates.update.EdgeRemoval;
import dna.updates.update.EdgeWeight;
import dna.updates.update.Update;

public class EdgeWeightsU extends EdgeWeights {

	public EdgeWeightsU(double binSize) {
		super("EdgeWeightsU", ApplicationType.BeforeAndAfterUpdate,
				IMetricNew.MetricType.exact, binSize);
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
		if (u instanceof EdgeAddition) {
			this.distr.incr(this.getWeight(((EdgeAddition) u).getEdge()));
		} else if (u instanceof EdgeRemoval) {
			this.distr.decr(this.getWeight(((EdgeRemoval) u).getEdge()));
		} else if (u instanceof EdgeWeight) {
			this.distr.decr(this.getWeight(((EdgeWeight) u).getEdge()));
			this.distr.incr(this.getWeight(((EdgeWeight) u).getWeight()));
		}
		this.distr.truncate();
		return true;
	}

	@Override
	public boolean applyAfterUpdate(Update u) {
		return false;
	}

}
