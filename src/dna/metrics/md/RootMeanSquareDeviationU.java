package dna.metrics.md;

import dna.series.data.BinnedDistributionInt;
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
public class RootMeanSquareDeviationU<W> extends RootMeanSquareDeviation<W> {

	public RootMeanSquareDeviationU() {
		super("RootMeanSquareDeviationU", ApplicationType.BeforeBatch,
				MetricType.exact);
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean applyBeforeBatch(Batch b) {
		this.changes = 0;
		this.rmsd = 0;
		this.distr = new BinnedDistributionInt("DeviationDistribution", 0.1,
				new int[0], 0);
		for (NodeWeight u : b.getNodeWeights()) {
			double deviation = this.getDeviation(
					this.getWeight(this.g.getNode(u.getNode().getIndex())),
					(W) u.getWeight());
			this.changes++;
			this.rmsd += deviation;
			this.distr.incr((int) Math.floor(deviation / 0.1));
		}
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
		this.distr = new BinnedDistributionInt("DeviationDistribution", 0.1,
				new int[0], 0);
		return true;
	}

}
