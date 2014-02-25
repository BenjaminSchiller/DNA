package dna.metrics.apsp;

import java.util.LinkedList;

import dna.graph.IElement;
import dna.graph.edges.DirectedEdge;
import dna.graph.edges.UndirectedEdge;
import dna.graph.nodes.DirectedNode;
import dna.graph.nodes.Node;
import dna.graph.nodes.UndirectedNode;
import dna.updates.batch.Batch;
import dna.updates.update.Update;

public class UnweightedAllPairsShortestPathsR extends UnweightedAllPairsShortestPaths {

	public UnweightedAllPairsShortestPathsR() {
		super("UnweightedAllPairsShortestPathsR", ApplicationType.Recomputation);
	}

	@Override
	public boolean applyBeforeBatch(Batch b) {
		return false;
	}

	@Override
	public boolean applyAfterBatch(Batch b) {
		return false;
	}

	@Override
	public boolean applyBeforeUpdate(Update u) {
		return false;
	}

	@Override
	public boolean applyAfterUpdate(Update u) {
		return false;
	}

	@Override
	public boolean compute() {
		for (IElement n_ : this.g.getNodes()) {
			Node n = (Node) n_;
			boolean[] seen = new boolean[this.g.getMaxNodeIndex() + 1];
			int[] dist = new int[this.g.getMaxNodeIndex() + 1];
			LinkedList<Node> queue = new LinkedList<Node>();
			queue.add(n);
			seen[n.getIndex()] = true;
			dist[n.getIndex()] = 0;

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
							this.apsp.incr(dist[out.getIndex()]);
						}
					}
				} else if (current instanceof UndirectedNode) {
					UndirectedNode curr = (UndirectedNode) current;
					for (IElement out_ : curr.getEdges()) {
						UndirectedNode out = ((UndirectedEdge) out_)
								.getDifferingNode(curr);
						if (!seen[out.getIndex()]) {
							queue.addLast(out);
							seen[out.getIndex()] = true;
							dist[out.getIndex()] = dist[curr.getIndex()] + 1;
							this.apsp.incr(dist[out.getIndex()]);
						}
					}
				}

			}
		}

		return true;
	}
}
