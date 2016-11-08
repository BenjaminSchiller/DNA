package dna.metrics.weights.multi;

import dna.graph.Graph;
import dna.graph.weights.IWeightedEdge;
import dna.graph.weights.multi.DoubleMultiWeight;
import dna.metrics.algorithms.IBeforeNA;
import dna.metrics.algorithms.IBeforeNR;
import dna.metrics.algorithms.IBeforeNW;
import dna.metrics.weights.NodeWeights;
import dna.updates.batch.Batch;
import dna.updates.update.NodeAddition;
import dna.updates.update.NodeRemoval;
import dna.updates.update.NodeWeight;

/**
 * Computes the NodeWeights metric update-based for a weight with given index of
 * given DoubleMultiWeight.
 * 
 * @author Rwilmes
 * 
 */
public class NodeWeightsMultiU extends NodeWeights implements IBeforeNA,
		IBeforeNR, IBeforeNW {

	protected int index;
	protected String name;

	public NodeWeightsMultiU(int index, double binSize) {
		this("NodeWeightsU-" + index + "-" + binSize, index, binSize);
	}

	public NodeWeightsMultiU(String name, int index, double binSize) {
		super(name, binSize);
		this.name = name;
		this.index = index;
	}

	@Override
	public boolean init() {
		return this.compute();
	}

	@Override
	public boolean applyBeforeUpdate(NodeWeight nw) {
		this.distr.decr(this.getWeight(nw.getNode()));
		this.distr.incr(this.getWeight(nw.getWeight(), this.index));
		return true;
	}

	@Override
	public boolean applyBeforeUpdate(NodeRemoval nr) {
		this.distr.decr(this.getWeight(nr.getNode(), this.index));
		return true;
	}

	@Override
	public boolean applyBeforeUpdate(NodeAddition na) {
		this.distr.incr(this.getWeight(na.getNode(), this.index));
		return true;
	}

	@Override
	public boolean isApplicable(Graph g) {
		return g.getGraphDatastructures().isEdgeType(IWeightedEdge.class)
				&& g.getGraphDatastructures().isEdgeWeightType(
						DoubleMultiWeight.class);
	}

	@Override
	public boolean isApplicable(Batch b) {
		return b.getGraphDatastructures().isEdgeType(IWeightedEdge.class)
				&& b.getGraphDatastructures().isEdgeWeightType(
						DoubleMultiWeight.class);
	}
}
