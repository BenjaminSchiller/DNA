package dna.metrics.apsp;

import java.util.HashSet;

import dna.graph.nodes.Node;
import dna.updates.batch.Batch;
import dna.updates.update.Update;
import dna.util.parameters.IntParameter;

public class UnweightedKSingleSourceShortestPaths extends
		UnweightedAllPairsShortestPaths {

	protected int k;

	public UnweightedKSingleSourceShortestPaths(int k) {
		super("UnweightedKSingleSourceShortestPaths",
				ApplicationType.Recomputation, MetricType.heuristic,
				new IntParameter("k", k));
		this.k = k;
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
		HashSet<Node> nodes = new HashSet<Node>();
		while (nodes.size() < this.k) {
			Node n = this.g.getRandomNode();
			if (nodes.contains(n.getIndex())) {
				continue;
			}
			nodes.add(n);
			this.compute(n);
		}
		return true;
	}

}
