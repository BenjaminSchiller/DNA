package dna.metrics.weights;

import java.util.HashMap;

import dna.graph.IElement;
import dna.graph.nodes.Node;
import dna.graph.weights.IWeightedNode;
import dna.graph.weights.Weight;
import dna.graph.weights.distances.EuclideanDistance;
import dna.metrics.algorithms.IRecomputation;

public class RootMeanSquareDeviationR extends RootMeanSquareDeviation implements
		IRecomputation {

	public RootMeanSquareDeviationR() {
		super("RootMeanSquareDeviationR", MetricType.exact);
	}

	protected HashMap<Node, Weight> positions;

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
	public boolean recompute() {
		this.changes = 0;
		this.rmsd = 0;
		this.initDistr();
		if (this.positions == null) {
			this.positions = new HashMap<Node, Weight>();
			for (IElement n : this.g.getNodes()) {
				this.updatePosition((Node) n);
			}
			for (int i = 0; i < this.g.getNodeCount(); i++) {
				this.distr.incr(0);
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
				} else {
					this.distr.incr(0);
				}
				this.updatePosition((Node) n);
			}
			this.rmsd /= this.g.getNodeCount();
			this.rmsd = Math.sqrt(this.rmsd);
		}
		return true;
	}

}
