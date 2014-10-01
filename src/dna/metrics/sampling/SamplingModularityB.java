package dna.metrics.sampling;

import dna.graph.Graph;
import dna.graph.nodes.Node;
import dna.metrics.algorithms.IAfterBatch;
import dna.updates.batch.Batch;
import dna.updates.update.NodeAddition;

/**
 * This metric will measure the fraction between sample and original graph. It
 * will generate two values: SamplingModularityV1 and SamplingModularityV2. SMV1
 * compares the amount of edges in the sample with the amount of edges in the
 * original graph. SMV2 compares the amount of edges between sampled and not
 * sampled nodes, with the amount of edges in the original graph.
 * 
 * @author Benedict Jahn
 * 
 */
public class SamplingModularityB extends SamplingModularity implements
		IAfterBatch {

	public SamplingModularityB(Graph graph) {
		super("SamplingModularityB", MetricType.exact, graph);
	}

	@Override
	public boolean init() {
		return this.compute();
	}

	@Override
	public boolean applyAfterBatch(Batch b) {
		edgesInSample += b.getEdgeAdditionsCount();
		for (NodeAddition na : b.getNodeAdditions()) {
			degreeSum += getDegreeFromOriginalNode((Node) na.getNode());
		}
		return true;
	}

}
