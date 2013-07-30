package dna.metrics.connectedComponents;

import java.util.Collection;

import dna.graph.undirected.UndirectedEdge;
import dna.graph.undirected.UndirectedNode;
import dna.updates.Batch;
import dna.updates.Update;

@SuppressWarnings("rawtypes")
public class CCUndirectedDynBatch extends CCUndirected {

	public CCUndirectedDynBatch() {
		super("CCdirectedComp", ApplicationType.AfterBatch);
	}

	@Override
	public boolean applyBeforeBatch(Batch b) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean applyAfterBatch(Batch b) {
		int r = 0;
		boolean[][] a = new boolean[this.g.getNodes().size()][this.g.getNodes()
				.size()];
		for (UndirectedEdge e : (Collection<UndirectedEdge>) b
				.getEdgeRemovals()) {
			UndirectedNode src = e.getNode1();
			UndirectedNode dst = e.getNode2();
			SpanningTreeNode dstTreeElement = this.nodesTreeElement[dst
					.getIndex()];
			SpanningTreeNode srcTreeElement = this.nodesTreeElement[src
					.getIndex()];
			if (srcTreeElement.getChildren().contains(dstTreeElement)) {

				boolean foundNeighbour = false;
				for (UndirectedEdge edge : src.getEdges()) {
					UndirectedNode n = edge.getNode1();
					if (src == n)
						n = edge.getNode2();
					a[src.getIndex()][n.getIndex()] = true;
				}
				for (UndirectedEdge edge : dst.getEdges()) {
					UndirectedNode n = edge.getNode1();
					if (src == n)
						n = edge.getNode2();
					if (a[src.getIndex()][n.getIndex()]) {
						foundNeighbour = true;
						break;
					}
				}

				if (!foundNeighbour) {
					r += 1;
				}
			}
		}
		// TODO: r thres zeigt an ab wann neuberechnet wird
		if (r > 0) {
			r = 0;
			this.reset_();
			this.compute();
		} else {
			int f = 0;
			for (UndirectedEdge e : (Collection<UndirectedEdge>) b
					.getEdgeAdditions()) {
				UndirectedNode src = e.getNode1();
				UndirectedNode dst = e.getNode2();
				SpanningTreeNode dstTreeElement = this.nodesTreeElement[dst
						.getIndex()];
				SpanningTreeNode srcTreeElement = this.nodesTreeElement[src
						.getIndex()];

				if (this.nodeComponentMembership[src.getIndex()] != this.nodeComponentMembership[dst
						.getIndex()]) {
					f += 1;
				}
			}

			if (f > 100) {
				this.reset_();
				this.compute();
			}
		}

		return true;
	}

	@Override
	public boolean applyBeforeUpdate(Update u) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean applyAfterUpdate(Update u) {
		// TODO Auto-generated method stub
		return false;
	}

}
