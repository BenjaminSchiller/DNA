package dna.metrics.connectedComponents;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

import dna.graph.IElement;
import dna.graph.edges.UndirectedEdge;
import dna.graph.nodes.UndirectedNode;
import dna.updates.Batch;
import dna.updates.Update;

@SuppressWarnings("rawtypes")
public class CCUndirectedDynBatch extends CCUndirected {

	public CCUndirectedDynBatch() {
		super("CCUndirectedComp", ApplicationType.AfterBatch);
	}

	@Override
	public boolean applyBeforeBatch(Batch b) {
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean applyAfterBatch(Batch b) {
		int r = 0;

		Collection<UndirectedEdge> edgeRemovals = (Collection<UndirectedEdge>) b
				.getEdgeRemovals();
		for (UndirectedEdge e : edgeRemovals) {
			SpanningTreeNode n1 = this.nodesTreeElement.get(e.getNode1()
					.getIndex());
			SpanningTreeNode n2 = this.nodesTreeElement.get(e.getNode2()
					.getIndex());
			boolean neighbourFound = false;

			HashSet<UndirectedNode> reachableNodes = new HashSet<>();
			for (IElement ie : n1.getNode().getEdges()) {
				UndirectedEdge ed = (UndirectedEdge) ie;
				UndirectedNode node = ed.getDifferingNode(n1.getNode());
				reachableNodes.add(node);
			}
			for (IElement ie : n2.getNode().getEdges()) {
				UndirectedEdge ed = (UndirectedEdge) ie;
				UndirectedNode node = ed.getDifferingNode(n2.getNode());
				if (reachableNodes.contains(node)) {
					n2.setParent(this.nodesTreeElement.get(node.getIndex()));
					neighbourFound = true;
					break;
				}
			}

			if (!neighbourFound) {
				r += 1;
			}
		}

		// TODO: r thres zeigt an ab wann neuberechnet wird
		if (r > 0) {
			r = 0;
			this.reset_();
			this.compute();
		} else {
			for (UndirectedEdge e : (Collection<UndirectedEdge>) b
					.getEdgeAdditions()) {
				UndirectedNode n1 = e.getNode1();
				UndirectedNode n2 = e.getNode2();

				if (this.nodeComponentMembership.get(n1.getIndex()) != this.nodeComponentMembership
						.get(n2.getIndex())) {

					SpanningTreeNode temp = this.nodesTreeElement.get(n2
							.getIndex());
					SpanningTreeNode newParent = this.nodesTreeElement.get(n1
							.getIndex());

					while (!temp.isRoot()) {
						SpanningTreeNode newChild = temp.getParent();

						temp.setParent(newParent);
						newParent = temp;
						temp = newChild;
					}
					temp.setParent(newParent);
					temp.setRoot(false);
					updateComponentIndex(
							this.nodeComponentMembership.get(n1.getIndex()),
							this.nodesTreeElement.get(n2.getIndex()));

				}
			}

		}

		return true;
	}

	private void updateComponentIndex(int component, SpanningTreeNode dst) {
		Queue<SpanningTreeNode> q = new LinkedList<SpanningTreeNode>();
		q.add(dst);

		while (!q.isEmpty()) {
			SpanningTreeNode temp = (SpanningTreeNode) q.poll();
			for (IElement ie : temp.getNode().getEdges()) {
				UndirectedEdge ed = (UndirectedEdge) ie;
				UndirectedNode node = ed.getDifferingNode(temp.getNode());
				if (this.nodesTreeElement.get(node.getIndex()).getParent() == temp) {
					q.add(this.nodesTreeElement.get(node.getIndex()));
				}
			}
			this.nodeComponentMembership.put(temp.getNode().getIndex(),
					component);
		}

	}

	@Override
	public boolean applyBeforeUpdate(Update u) {
		return false;
	}

	@Override
	public boolean applyAfterUpdate(Update u) {
		return false;
	}

}
