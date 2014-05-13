package dna.metrics.shortestPaths;

import java.util.LinkedList;
import java.util.Queue;

import dna.graph.IElement;
import dna.graph.edges.UndirectedEdge;
import dna.graph.nodes.Node;
import dna.graph.nodes.UndirectedNode;
import dna.updates.batch.Batch;
import dna.updates.update.Update;
import dna.util.ArrayUtils;

public class UndirectedShortestPathsR extends UndirectedShortestPaths {

	public UndirectedShortestPathsR() {
		super("UndirectedShortestPathsR", ApplicationType.Recomputation);
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
		this.existingPaths = 0;

		for (IElement sUncasted : g.getNodes()) {
			UndirectedNode s = (UndirectedNode) sUncasted;
			int[] dist = ArrayUtils.init(this.g.getMaxNodeIndex() + 1, -1);
			Queue<Node> q = new LinkedList<Node>();

			dist[s.getIndex()] = 0;
			q.add(s);

			while (!q.isEmpty()) {
				Node current = q.poll();
				for (IElement eUncasted : current.getEdges()) {
					UndirectedEdge e = (UndirectedEdge) eUncasted;
					Node neighbor = e.getDifferingNode(current);
					if (dist[neighbor.getIndex()] != -1) {
						continue;
					}
					dist[neighbor.getIndex()] = dist[current.getIndex()] + 1;
					this.spl = ArrayUtils.incr(this.spl,
							dist[neighbor.getIndex()]);
					q.add(neighbor);
					this.existingPaths++;
				}
			}
		}

		this.cpl = -1;
		this.diam = this.spl.length - 1;

		return true;
	}

}
