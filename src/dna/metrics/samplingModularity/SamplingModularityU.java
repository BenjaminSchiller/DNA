package dna.metrics.samplingModularity;

import dna.graph.Graph;
import dna.graph.nodes.Node;
import dna.updates.batch.Batch;
import dna.updates.update.NodeAddition;
import dna.updates.update.Update;

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
public class SamplingModularityU extends SamplingModularity {

	/**
	 * Creates an instance of the sampling modularity metric, which gets updated
	 * after every batch.
	 * 
	 * @param fullGraph
	 *            the original full graph
	 */
	public SamplingModularityU(Graph fullGraph) {
		super("SamplingModularityU", ApplicationType.AfterBatch,
				MetricType.exact, fullGraph);
	}

	@Override
	public boolean applyBeforeBatch(Batch b) {
		return false;
	}

	@Override
	public boolean applyAfterBatch(Batch b) {
		edgesInSample += b.getEdgeAdditionsCount();
		Iterable<NodeAddition> iter = b.getNodeAdditions();
		for (NodeAddition na : iter) {
			degreeSum += getDegreeFromOriginalNode((Node) na.getNode());
		}
		return true;
	}

	@Override
	public boolean applyBeforeUpdate(Update u) {
		return false;
	}

	@Override
	public boolean applyAfterUpdate(Update u) {
		return false;
	}

}
