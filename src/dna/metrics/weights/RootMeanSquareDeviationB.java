package dna.metrics.weights;

import dna.graph.weights.IWeightedNode;
import dna.graph.weights.distances.EuclideanDistance;
import dna.metrics.algorithms.IBeforeBatch;
import dna.updates.batch.Batch;
import dna.updates.update.NodeWeight;

public class RootMeanSquareDeviationB extends RootMeanSquareDeviation implements
		IBeforeBatch {

	public RootMeanSquareDeviationB() {
		super("RootMeanSquareDeviationB");
	}

	@Override
	public boolean init() {
		this.changes = 0;
		this.rmsd = 0;
		this.initDistr();
		for (int i = 0; i < this.g.getNodeCount(); i++) {
			this.distr.incr(0.0);
		}
		return true;
	}

	@Override
	public boolean applyBeforeBatch(Batch b) {
		this.changes = 0;
		this.rmsd = 0;
		this.initDistr();

		int nodesAfter = this.g.getNodeCount() + b.getNodeAdditionsCount()
				- b.getNodeRemovalsCount();
		int unchangedNodes = nodesAfter - b.getNodeWeightsCount();

		for (int i = 0; i < unchangedNodes; i++) {
			this.distr.incr(0.0);
		}
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

}
