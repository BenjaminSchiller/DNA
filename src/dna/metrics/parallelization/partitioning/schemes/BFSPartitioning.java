package dna.metrics.parallelization.partitioning.schemes;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import dna.graph.Graph;
import dna.graph.IElement;
import dna.graph.edges.Edge;
import dna.graph.nodes.Node;
import dna.util.parameters.IntParameter;

public class BFSPartitioning extends PartitioningScheme {

	protected int partitionSize;

	public BFSPartitioning(PartitioningType partitioningType, int partitionSize) {
		super("BFSPartitioning", partitioningType, new IntParameter(
				"partitionSize", partitionSize));
		this.partitionSize = partitionSize;
	}

	@Override
	public List<List<Node>> getPartitioning(Graph g) {
		HashSet<Node> visited = new HashSet<Node>(g.getNodeCount());
		List<Node> orderedNodes = new ArrayList<Node>(g.getNodeCount());

		for (IElement n_ : g.getNodes()) {
			Node n = (Node) n_;
			if (visited.contains(n)) {
				continue;
			}

			Queue<Node> queue = new LinkedList<Node>();
			queue.add(n);
			visited.add(n);
			orderedNodes.add(n);
			while (!queue.isEmpty()) {
				Node current = queue.poll();
				for (IElement e_ : current.getEdges()) {
					Edge e = (Edge) e_;
					Node n2 = e.getDifferingNode(current);
					if (!visited.contains(n2)) {
						queue.add(n2);
						visited.add(n2);
						orderedNodes.add(n2);
					}
				}
			}
		}

		List<List<Node>> partitioning = this.createNewPartitioning();
		List<Node> current = this.addNewPartition(partitioning);

		for (Node n : orderedNodes) {
			if (current.size() >= this.partitionSize) {
				current = this.addNewPartition(partitioning);
			}
			current.add(n);
		}

		return partitioning;
	}
}
