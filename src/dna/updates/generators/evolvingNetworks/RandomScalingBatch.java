package dna.updates.generators.evolvingNetworks;

import dna.graph.IGraph;
import dna.updates.batch.Batch;
import dna.updates.generators.BatchGenerator;
import dna.updates.generators.random.RandomBatch;

public class RandomScalingBatch extends BatchGenerator {
	private double nodeGrowthFactor;
	private double nodeShrinkFactor;
	private double edgeGrowthFactor;
	private double edgeShrinkFactor;

	public RandomScalingBatch(double nodeGrowthFactor, double nodeShrinkFactor,
			double edgeGrowthFactor, double edgeShrinkFactor) {
		super("RandomScalingBatch");
		this.nodeGrowthFactor = nodeGrowthFactor;
		this.nodeShrinkFactor = nodeShrinkFactor;
		this.edgeGrowthFactor = edgeGrowthFactor;
		this.edgeShrinkFactor = edgeShrinkFactor;
	}

	public RandomScalingBatch(double growthFactor, double shrinkFactor) {
		this(growthFactor, shrinkFactor, growthFactor, shrinkFactor);
	}

	@Override
	public Batch generate(IGraph g) {
		Batch b = this.getCurrentBatch(g).generate(g);
		return b;
	}

	@Override
	public void reset() {
	}

	private RandomBatch getCurrentBatch(IGraph g) {
		int nodes = g.getNodeCount();
		int edges = g.getEdgeCount();

		return new RandomBatch((int) Math.ceil(nodes * nodeGrowthFactor),
				(int) Math.ceil(nodes * nodeShrinkFactor), 0, null,
				(int) Math.ceil(edges * edgeGrowthFactor),
				(int) Math.ceil(edges * edgeShrinkFactor), 0, null);
	}

	@Override
	public boolean isFurtherBatchPossible(IGraph g) {
		return this.getCurrentBatch(g).isFurtherBatchPossible(g);
	}
}
