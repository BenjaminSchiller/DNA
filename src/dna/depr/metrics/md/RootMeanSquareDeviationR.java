package dna.depr.metrics.md;

import java.util.HashMap;

import dna.graph.IElement;
import dna.graph.nodes.Node;
import dna.graph.weights.IWeightedNode;
import dna.graph.weights.Weight;
import dna.graph.weights.distances.EuclideanDistance;
import dna.metricsNew.IMetricNew;
import dna.updates.batch.Batch;
import dna.updates.update.Update;

/**
 * 
 * Recomputation of the RMSD. For each node, the prior position is stored in a
 * HashMap.
 * 
 * @author benni
 * 
 * @param <W>
 */
public class RootMeanSquareDeviationR extends RootMeanSquareDeviation {

	public RootMeanSquareDeviationR() {
		super("RootMeanSquareDeviationR", ApplicationType.Recomputation,
				IMetricNew.MetricType.exact);
	}

	protected HashMap<Node, Weight> positions;

	@Override
	public void reset_() {
		super.reset_();
		this.positions = null;
	}

	/**
	 * 
	 * Updates the position stored for the given node with its currently
	 * assigned weight.
	 * 
	 * @param n
	 */
	protected void updatePosition(Node n) {
		this.positions.put(n, ((IWeightedNode) n).getWeight());
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

	@Override
	public boolean compute() {
		this.changes = 0;
		this.rmsd = 0;
		this.initDistr();
		if (this.positions == null) {
			this.positions = new HashMap<Node, Weight>();
			for (IElement n : this.g.getNodes()) {
				this.updatePosition((Node) n);
			}
		} else {
			for (IElement n_ : this.g.getNodes()) {
				Node n = (Node) n_;
				Weight old = this.positions.get(n);
				if (old != null) {
					double dist = EuclideanDistance.dist(old,
							((IWeightedNode) n).getWeight());
					this.rmsd += dist * dist;
					this.distr.incr(dist);
					this.changes++;
				}
				this.updatePosition((Node) n);
			}
			this.rmsd /= this.g.getNodeCount();
			this.rmsd = Math.sqrt(this.rmsd);
		}
		return true;
	}
}
