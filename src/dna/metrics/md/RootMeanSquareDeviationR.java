package dna.metrics.md;

import java.util.HashMap;

import dna.graph.IElement;
import dna.graph.nodes.Node;
import dna.graph.weightsNew.IWeightedNode;
import dna.graph.weightsNew.Weight;
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
				MetricType.exact);
	}

	protected HashMap<Integer, Weight> positions;

	/**
	 * 
	 * Updates the position stored for the given node with its currently
	 * assigned weight.
	 * 
	 * @param n
	 */
	protected void updatePosition(Node n) {
		this.positions.put(n.getIndex(), ((IWeightedNode) n).getWeight());
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
			this.positions = new HashMap<Integer, Weight>();
			for (IElement n : this.g.getNodes()) {
				this.updatePosition((Node) n);
			}
		} else {
			for (IElement n_ : this.g.getNodes()) {
				Node n = (Node) n_;
				Weight old = this.positions.get(n.getIndex());
				if (old != null && !old.equals(this.getWeight(n))) {
					double deviation = this
							.getDeviation(old, this.getWeight(n));
					this.rmsd += deviation;
					this.distr.incr(deviation);
					this.changes++;
				}
				this.positions.put(n.getIndex(), this.getWeight(n));
			}
			this.rmsd = Math.sqrt(this.rmsd);
		}
		return true;
	}
}
