package dna.parallel.partition;

import java.util.List;

import dna.graph.IGraph;
import dna.graph.nodes.Node;
import dna.updates.batch.Batch;

public abstract class Partition {

	public static final String mainNodeType = "compute";

	public static enum PartitionType {
		NodeCut, Separated, Overlapping, Complete
	}

	public IGraph g;

	public Partition(IGraph g) {
		this.g = g;
	}

	protected static IGraph getInitialGraph(IGraph g, List<Node> nodes,
			String name) {
		IGraph g_ = g.getGraphDatastructures().newGraphInstance(name,
				g.getTimestamp(), nodes.size(), 0);
		for (Node n : nodes) {
			// TODO not handling weighted nodes yet!
			g_.addNode(g_.getGraphDatastructures().newNodeInstance(
					n.getIndex() + "@" + mainNodeType));
		}
		return g_;
	}

	protected static IGraph[] getInitialGraphs(IGraph g, List<Node>[] nodess) {
		IGraph[] graphs = new IGraph[nodess.length];
		for (int i = 0; i < graphs.length; i++) {
			graphs[i] = getInitialGraph(g, nodess[i], "partition" + i);
		}
		return graphs;
	}

	protected static Batch[] getEmptyBatches(Batch b, int partitionCount) {
		Batch[] batches = new Batch[partitionCount];
		for (int i = 0; i < batches.length; i++) {
			batches[i] = new Batch(b.getGraphDatastructures(), b.getFrom(),
					b.getTo());
		}
		return batches;
	}
}
