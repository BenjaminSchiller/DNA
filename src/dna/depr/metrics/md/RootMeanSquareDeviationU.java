package dna.depr.metrics.md;

import dna.graph.weights.IWeightedNode;
import dna.graph.weights.distances.EuclideanDistance;
import dna.metrics.IMetric;
import dna.updates.batch.Batch;
import dna.updates.update.NodeWeight;
import dna.updates.update.Update;

/**
 * Update version of the RMSD. No additional state is required.
 * 
 * @author benni
 * 
 * @param <W>
 */
public class RootMeanSquareDeviationU extends RootMeanSquareDeviation {

	public RootMeanSquareDeviationU() {
		super("RootMeanSquareDeviationU", ApplicationType.BeforeBatch,
				IMetric.MetricType.exact);
	}

	@Override
	public boolean applyBeforeBatch(Batch b) {
		this.changes = 0;
		this.rmsd = 0;
		this.initDistr();
		for (NodeWeight u : b.getNodeWeights()) {
			double dist = EuclideanDistance.dist(
					((IWeightedNode) u.getNode()).getWeight(), u.getWeight());
			this.rmsd += dist * dist;
			this.distr.incr(dist);
			this.changes++;
		}
		this.rmsd /= this.g.getNodeCount();
		this.rmsd = Math.sqrt(this.rmsd);
		return true;
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
		this.changes = 0;
		this.rmsd = 0;
		this.initDistr();
		return true;
	}

}
