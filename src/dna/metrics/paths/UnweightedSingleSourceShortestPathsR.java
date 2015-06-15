package dna.metrics.paths;

import java.util.LinkedList;

import dna.graph.IElement;
import dna.graph.edges.DirectedEdge;
import dna.graph.edges.UndirectedEdge;
import dna.graph.nodes.DirectedNode;
import dna.graph.nodes.Node;
import dna.graph.nodes.UndirectedNode;
import dna.metrics.algorithms.IRecomputation;
import dna.series.data.distributions.DistributionLong;

public class UnweightedSingleSourceShortestPathsR extends
		UnweightedSingleSourceShortestPaths implements IRecomputation {

	public UnweightedSingleSourceShortestPathsR(int sourceIndex) {
		super("UnweightedSingleSourceShortestPathsR", sourceIndex);
	}

	@Override
	public boolean recompute() {
		this.sssp = new DistributionLong("SSSP");

		if (!this.g.containsNode(this.g.getGraphDatastructures()
				.newNodeInstance(0))) {
			return true;
		}

		boolean[] seen = new boolean[this.g.getMaxNodeIndex() + 1];
		int[] dist = new int[this.g.getMaxNodeIndex() + 1];
		LinkedList<Node> queue = new LinkedList<Node>();
		Node source = this.g.getNode(this.sourceIndex);
		queue.add(source);
		seen[source.getIndex()] = true;
		dist[source.getIndex()] = 0;

		while (!queue.isEmpty()) {
			Node current = queue.pop();

			if (current instanceof DirectedNode) {
				DirectedNode curr = (DirectedNode) current;
				for (IElement out_ : curr.getOutgoingEdges()) {
					DirectedNode out = ((DirectedEdge) out_).getDst();
					if (!seen[out.getIndex()]) {
						queue.addLast(out);
						seen[out.getIndex()] = true;
						dist[out.getIndex()] = dist[curr.getIndex()] + 1;
						this.sssp.incr(dist[out.getIndex()]);
					}
				}
			} else if (current instanceof UndirectedNode) {
				UndirectedNode curr = (UndirectedNode) current;
				for (IElement out_ : curr.getEdges()) {
					UndirectedNode out = (UndirectedNode) ((UndirectedEdge) out_)
							.getDifferingNode(curr);
					if (!seen[out.getIndex()]) {
						queue.addLast(out);
						seen[out.getIndex()] = true;
						dist[out.getIndex()] = dist[curr.getIndex()] + 1;
						this.sssp.incr(dist[out.getIndex()]);
					}
				}
			}

		}
		return true;
	}

}
