package dna.metrics.md;

import java.util.HashMap;

import dna.graph.IElement;
import dna.graph.nodes.DirectedDoubleArrayWeightedNode;
import dna.graph.nodes.DirectedIntArrayWeightedNode;
import dna.graph.nodes.Node;
import dna.graph.nodes.UndirectedDoubleArrayWeightedNode;
import dna.graph.nodes.UndirectedIntArrayWeightedNode;
import dna.series.data.BinnedDistributionInt;
import dna.updates.batch.Batch;
import dna.updates.update.Update;
import dna.util.ArrayUtils;

/**
 * 
 * Recomputation of the RMSD. For each node, the prior position is stored in a
 * HashMap.
 * 
 * @author benni
 * 
 * @param <W>
 */
public class RootMeanSquareDeviationR<W> extends RootMeanSquareDeviation<W> {

	public RootMeanSquareDeviationR() {
		super("RootMeanSquareDeviationR", ApplicationType.Recomputation,
				MetricType.exact);
	}

	protected HashMap<Integer, W> positions;

	/**
	 * 
	 * Updates the position stored for the given node with its currently
	 * assigned weight.
	 * 
	 * @param n
	 */
	@SuppressWarnings("unchecked")
	protected void updatePosition(Node n) {
		if (n instanceof DirectedIntArrayWeightedNode) {
			this.positions.put(n.getIndex(),
					(W) ((DirectedIntArrayWeightedNode) n).getWeight());
		} else if (n instanceof DirectedDoubleArrayWeightedNode) {
			this.positions.put(n.getIndex(),
					(W) ((DirectedDoubleArrayWeightedNode) n).getWeight());
		} else if (n instanceof UndirectedIntArrayWeightedNode) {
			this.positions.put(n.getIndex(),
					(W) ((UndirectedIntArrayWeightedNode) n).getWeight());
		} else if (n instanceof UndirectedDoubleArrayWeightedNode) {
			this.positions.put(n.getIndex(),
					(W) ((UndirectedDoubleArrayWeightedNode) n).getWeight());
		}
	}

	protected boolean areEqual(W pos1, W pos2) {
		if (pos1 instanceof int[]) {
			return ArrayUtils.equals((int[]) pos1, (int[]) pos2);
		} else if (pos1 instanceof double[]) {
			return ArrayUtils.equals((double[]) pos1, (double[]) pos2);
		}
		return false;
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
			this.positions = new HashMap<Integer, W>();
			for (IElement n : this.g.getNodes()) {
				this.updatePosition((Node) n);
			}
		} else {
			for (IElement n_ : this.g.getNodes()) {
				Node n = (Node) n_;
				W old = this.positions.get(n.getIndex());
				if (old != null && !this.areEqual(old, this.getWeight(n))) {
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
