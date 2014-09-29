package dna.depr.metrics.md;

import java.util.HashMap;
import java.util.LinkedList;

import dna.graph.IElement;
import dna.graph.nodes.Node;
import dna.graph.weights.IWeightedNode;
import dna.graph.weights.distances.EuclideanDistance;
import dna.metrics.IMetricNew;
import dna.updates.batch.Batch;
import dna.updates.update.Update;

public class RootMeanSquareFluctuationR extends RootMeanSquareFluctuation {

	public RootMeanSquareFluctuationR(int steps) {
		super("RootMeanSquareFluctuationR", ApplicationType.Recomputation,
				IMetricNew.MetricType.exact, steps);
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
		if (this.positions == null) {
			this.positions = new HashMap<Node, LinkedList<double[]>>(
					this.g.getNodeCount());
		}

		for (IElement n_ : this.g.getNodes()) {
			Node n = (Node) n_;
			LinkedList<double[]> positions = this.update(n,
					((IWeightedNode) n).getWeight());
			double rmsf = this.computeRMSF(this.positions.get(n));
			this.rmsf.setValue(n.getIndex(), rmsf);
			this.rmsfD.incr(rmsf);
		}

		return true;
	}

}
