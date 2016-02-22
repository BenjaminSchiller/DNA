package dna.parallel.partitioning;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import dna.graph.Graph;
import dna.graph.IElement;
import dna.graph.edges.Edge;
import dna.graph.nodes.Node;

public class DFSPartitioning extends Partitioning {

	public DFSPartitioning() {
		super("DFSPartitioning");
	}

	@Override
	protected List<Node>[] partition(Graph g, int partitionCount) {
		List<Node> sorted = new ArrayList<Node>(g.getNodeCount());
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
				sorted.add(current);
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

		return this.split(g, sorted, partitionCount);
	}

}
