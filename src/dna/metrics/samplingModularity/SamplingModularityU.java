package dna.metrics.samplingModularity;

import dna.graph.Graph;
import dna.graph.nodes.Node;
import dna.updates.batch.Batch;
import dna.updates.update.NodeAddition;
import dna.updates.update.Update;

/**
 * @author Benedict Jahn
 * 
 */
public class SamplingModularityU extends SamplingModularity {

	/**
	 * 
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
