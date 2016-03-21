package dna.parallel.partitioning;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import dna.graph.IElement;
import dna.graph.IGraph;
import dna.graph.edges.Edge;
import dna.graph.nodes.Node;

public class BFSPartitioning extends Partitioning {

	public BFSPartitioning() {
		super("BFSPartitioning");
	}

	@Override
	protected List<Node>[] partition(IGraph g, int partitionCount) {
		HashSet<Node> visited = new HashSet<Node>(g.getNodeCount());
		List<Node> sorted = new ArrayList<Node>(g.getNodeCount());

		for (IElement n_ : g.getNodes()) {
			Node n = (Node) n_;
			if (visited.contains(n)) {
				continue;
			}

			Queue<Node> queue = new LinkedList<Node>();
			queue.add(n);
			visited.add(n);
			sorted.add(n);
			while (!queue.isEmpty()) {
				Node current = queue.poll();
				for (IElement e_ : current.getEdges()) {
					Edge e = (Edge) e_;
					Node n2 = e.getDifferingNode(current);
					if (!visited.contains(n2)) {
						queue.add(n2);
						visited.add(n2);
						sorted.add(n2);
					}
				}
			}
		}

		return this.split(g, sorted, partitionCount);
	}

}
