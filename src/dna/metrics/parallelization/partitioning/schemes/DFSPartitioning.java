package dna.metrics.parallelization.partitioning.schemes;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import dna.graph.IGraph;
import dna.graph.IElement;
import dna.graph.edges.Edge;
import dna.graph.nodes.Node;

public class DFSPartitioning extends PartitioningScheme {

	public DFSPartitioning(PartitioningType partitioningType, int partitionCount) {
		super("DFSPartitioning", partitioningType, partitionCount);
	}

	@Override
	public List<List<Node>> getPartitioning(IGraph g) {

		// int costPerBatch = (int) Math.ceil((double) g.getNodeCount()
		// / (double) this.partitionCount);
		// SamplingAlgorithm dfs = new DFS(g, new RandomSelection(),
		// costPerBatch,
		// g.getNodeCount(), SamplingStop.Visiting, WalkingType.AllEdges);
		//
		// Graph g2 = (new EmptyGraph(g.getGraphDatastructures())).generate();
		//
		// List<List<Node>> partitioning = this.createNewPartitioning();
		// for (int i = 0; i < this.partitionCount; i++) {
		// List<Node> current = this.addNewPartition(partitioning);
		// Batch b = dfs.generate(g2);
		// b.apply(g2);
		// for (NodeAddition na : b.getNodeAdditions()) {
		// current.add((Node) na.getNode());
		// }
		// }

		List<Node> orderedNodes = new ArrayList<Node>(g.getNodeCount());
		HashSet<Node> seen = new HashSet<Node>(g.getNodeCount());

		for (IElement n_ : g.getNodes()) {
			Node n = (Node) n_;
			if (seen.contains(n)) {
				continue;
			}
			LinkedList<Node> queue = new LinkedList<Node>();
			queue.addFirst(n);
			while (!queue.isEmpty()) {
				Node current = queue.pollFirst();
				orderedNodes.add(current);
				seen.add(current);
				for (IElement e_ : current.getEdges()) {
					Edge e = (Edge) e_;
					Node n2 = e.getDifferingNode(current);
					if (!seen.contains(n2)) {
						seen.add(n2);
						queue.addFirst(n2);
					}
				}
			}
		}

		int partitionSize = (int) Math.ceil((double) g.getNodeCount()
				/ (double) this.partitionCount);

		List<List<Node>> partitioning = this.createNewPartitioning();
		List<Node> current = this.addNewPartition(partitioning);

		for (Node n : orderedNodes) {
			if (current.size() >= partitionSize) {
				current = this.addNewPartition(partitioning);
			}
			current.add(n);
		}
		return partitioning;
	}
}
