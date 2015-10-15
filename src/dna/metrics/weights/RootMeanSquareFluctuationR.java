package dna.metrics.weights;

import java.util.HashMap;
import java.util.LinkedList;

import dna.graph.IElement;
import dna.graph.nodes.Node;
import dna.graph.weights.IWeightedNode;
import dna.metrics.algorithms.IRecomputation;
import dna.series.data.distr.BinnedDoubleDistr;
import dna.series.data.nodevaluelists.NodeValueList;

public class RootMeanSquareFluctuationR extends RootMeanSquareFluctuation
		implements IRecomputation {

	public RootMeanSquareFluctuationR(int steps) {
		super("RootMeanSquareFluctuationR", steps);
	}

	@Override
	public boolean recompute() {
		this.rmsf = new NodeValueList("RootMeanSquareFluctuation", 0);
		this.rmsfD = new BinnedDoubleDistr(
				"RootMeanSquareFluctuation--Distribution", 0.05);

		if (this.positions == null) {
			this.positions = new HashMap<Node, LinkedList<double[]>>(
					this.g.getNodeCount());
		}

		for (IElement n_ : this.g.getNodes()) {
			Node n = (Node) n_;
			this.update(n, ((IWeightedNode) n).getWeight());
			double rmsf = this.computeRMSF(this.positions.get(n));
			this.rmsf.setValue(n.getIndex(), rmsf);
			this.rmsfD.incr(rmsf);
		}

		return true;
	}
}
