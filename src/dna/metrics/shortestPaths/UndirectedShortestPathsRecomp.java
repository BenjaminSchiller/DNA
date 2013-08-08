package dna.metrics.shortestPaths;

import java.util.LinkedList;
import java.util.Queue;

import dna.graph.undirected.UndirectedEdge;
import dna.graph.undirected.UndirectedGraph;
import dna.graph.undirected.UndirectedNode;
import dna.updates.Batch;
import dna.updates.Update;
import dna.util.ArrayUtils;

@SuppressWarnings("rawtypes")
public class UndirectedShortestPathsRecomp extends UndirectedShortestPaths {

	public UndirectedShortestPathsRecomp() {
		super("undirectedShortestPathsRecomp", ApplicationType.Recomputation);
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
		UndirectedGraph g = (UndirectedGraph) this.g;

		this.existingPaths = 0;

		for (UndirectedNode s : g.getNodes()) {
			int[] dist = ArrayUtils.init(this.g.getMaxNodeIndex() + 1, -1);
			Queue<UndirectedNode> q = new LinkedList<UndirectedNode>();

			dist[s.getIndex()] = 0;
			q.add(s);

			while (!q.isEmpty()) {
				UndirectedNode current = q.poll();
				for (UndirectedEdge e : current.getEdges()) {
					UndirectedNode neighbor = e.getDifferingNode(current);
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
