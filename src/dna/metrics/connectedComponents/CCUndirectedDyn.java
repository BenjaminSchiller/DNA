package dna.metrics.connectedComponents;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import dna.graph.Graph;
import dna.graph.undirected.UndirectedEdge;
import dna.graph.undirected.UndirectedNode;
import dna.metrics.Metric;
import dna.updates.Batch;
import dna.updates.EdgeAddition;
import dna.updates.EdgeRemoval;
import dna.updates.NodeAddition;
import dna.updates.NodeRemoval;
import dna.updates.Update;

public class CCUndirectedDyn extends CCUndirected {

	boolean searchSmallerComponent;

	public CCUndirectedDyn() {
		super("CCUndirectedDyn()", ApplicationType.AfterUpdate);
	}

	public void init() {
		super.init();
		searchSmallerComponent = false;
	}

	public void setSearchSmallerComponet(boolean searchSmallerComponent) {
		this.searchSmallerComponent = searchSmallerComponent;
	}

	private void updateComponentIndex(int component, SpanningTreeNode dst) {
		Queue<SpanningTreeNode> q = new LinkedList<SpanningTreeNode>();
		q.add(dst);

		while (!q.isEmpty()) {
			SpanningTreeNode temp = (SpanningTreeNode) q.poll();
			for (SpanningTreeNode n : temp.getChildren()) {
				q.add(n);

			}
			this.nodeComponentMembership[temp.getNode().getIndex()] = component;
		}

	}

	private boolean saveRemove(SpanningTreeNode srcTreeElement,
			SpanningTreeNode dstTreeElement) {
		Queue<SpanningTreeNode> q = new LinkedList<SpanningTreeNode>();
		dstTreeElement.setParent(null);
		q.add(dstTreeElement);

		Set<UndirectedNode> nodes = bfsDYN(dstTreeElement);

		for (UndirectedEdge ed : dstTreeElement.getNode().getEdges()) {
			UndirectedNode n = ed.getNode1();
			if (n == dstTreeElement.getNode())
				n = ed.getNode2();

			if (!nodes.contains(n)
					&& this.nodeComponentMembership[n.getIndex()] == this.nodeComponentMembership[dstTreeElement
							.getNode().getIndex()]) {
				dstTreeElement.setParent(this.nodesTreeElement[n.getIndex()]);
				return true;
			}
		}

		while (!q.isEmpty()) {
			SpanningTreeNode temp = (SpanningTreeNode) q.poll();

			for (UndirectedEdge ed : temp.getNode().getEdges()) {
				UndirectedNode n = ed.getNode1();
				if (n == temp.getNode())
					n = ed.getNode2();

				if (!temp.getChildren().contains(n) || !nodes.contains(n)) {
					SpanningTreeNode child = temp;
					SpanningTreeNode parent = this.nodesTreeElement[n
							.getIndex()];
					parent.addChild(child);
					child.setParent(parent);

					while (parent.getParent() != null) {
						child.setParent(parent);
						parent.addChild(child);
						child = parent;
						parent = parent.getParent();
					}
					return true;
				}

			}

			for (SpanningTreeNode spanningTreeNode : temp.getChildren()) {
				q.add(spanningTreeNode);
			}

		}

		return false;
	}

	private Set<UndirectedNode> bfsDYN(SpanningTreeNode treeElement) {
		Set<UndirectedNode> nodes = new HashSet<UndirectedNode>();

		Queue<SpanningTreeNode> q = new LinkedList<SpanningTreeNode>();
		q.add(treeElement);
		while (!q.isEmpty()) {
			SpanningTreeNode temp = (SpanningTreeNode) q.poll();
			for (SpanningTreeNode n : temp.getChildren()) {
				q.add(n);
				nodes.add(n.getNode());
			}

		}

		return nodes;

	}

	@Override
	public boolean applyBeforeBatch(Batch b) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean applyAfterBatch(Batch b) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean applyBeforeUpdate(Update u) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean applyAfterUpdate(Update u) {
		if (u instanceof NodeAddition) {
			return applyAfterNodeAddition(u);
		} else if (u instanceof NodeRemoval) {
			return applyAfterNodeRemoval(u);
		} else if (u instanceof EdgeAddition) {
			return applyAfterEdgeAddition(u);
		} else if (u instanceof EdgeRemoval) {
			return applyAfterEdgeRemoval(u);
		}
		return false;
	}

	private boolean applyAfterEdgeRemoval(Update u) {
		UndirectedEdge e = (UndirectedEdge) ((EdgeAddition) u).getEdge();
		UndirectedNode n1 = e.getNode1();
		UndirectedNode n2 = e.getNode2();

		if (this.nodeComponentMembership[n1.getIndex()] == this.nodeComponentMembership[n2
				.getIndex()]) {

			SpanningTreeNode n1TreeElement = this.nodesTreeElement[n1
					.getIndex()];
			SpanningTreeNode n2TreeElement = this.nodesTreeElement[n2
					.getIndex()];

			if (n1TreeElement.getChildren().contains(n2TreeElement)
					|| n2TreeElement.getChildren().contains(n1TreeElement)) {

				boolean neighbourFound = false;

				for (UndirectedEdge ude : n2.getEdges()) {
					UndirectedNode n = ude.getNode1();
					if (n == n2)
						n = ude.getNode2();
					if (n1TreeElement.getParent().getNode() == n) {
						SpanningTreeNode newparent = nodesTreeElement[n
								.getIndex()];
						newparent.addChild(n2TreeElement);
						n2TreeElement.setParent(newparent);
						neighbourFound = true;
					}

					for (SpanningTreeNode stn : n1TreeElement.getChildren()) {
						if (stn.getNode() == n) {
							SpanningTreeNode newparent = nodesTreeElement[n
									.getIndex()];
							newparent.addChild(n2TreeElement);
							n2TreeElement.setParent(newparent);
							neighbourFound = true;
						}
					}
				}

				if (!neighbourFound) {

					neighbourFound = saveRemove(n1TreeElement, n2TreeElement);
				}

				if (!neighbourFound) {

					n2TreeElement.setParent(null);
					n2TreeElement.setRoot(true);
					n1TreeElement.removeChild(n2TreeElement);
					updateComponentIndex(n2.getIndex(), n2TreeElement);
					this.componentList.add(n2TreeElement);
				}
			}
		}

		return true;
	}

	private boolean applyAfterEdgeAddition(Update u) {
		UndirectedEdge e = (UndirectedEdge) ((EdgeAddition) u).getEdge();
		UndirectedNode n1 = e.getNode1();
		UndirectedNode n2 = e.getNode2();

		if (this.nodeComponentMembership[n1.getIndex()] != this.nodeComponentMembership[n2
				.getIndex()]) {

			SpanningTreeNode temp = this.nodesTreeElement[n2.getIndex()];
			SpanningTreeNode newParent = this.nodesTreeElement[n1.getIndex()];

			while (!temp.isRoot()) {
				SpanningTreeNode newChild = temp.getParent();
				temp.addChild(newChild);
				temp.removeChild(newParent);

				temp.setParent(newParent);
				newParent = temp;
				temp = newChild;
			}
			temp.removeChild(newParent);
			temp.setParent(newParent);
			temp.setRoot(false);
			updateComponentIndex(this.nodeComponentMembership[n1.getIndex()],
					this.nodesTreeElement[n2.getIndex()]);

		}

		return true;
	}

	private boolean applyAfterNodeRemoval(Update u) {
		// TODO Auto-generated method stub
		return false;
	}

	private boolean applyAfterNodeAddition(Update u) {
		UndirectedNode n = (UndirectedNode) ((NodeAddition) u).getNode();
		SpanningTreeNode stn = new SpanningTreeNode(n);
		this.componentList.add(stn);
		return true;
	}

	@Override
	public boolean compute() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void init_() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isApplicable(Graph g) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isApplicable(Batch b) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isComparableTo(Metric m) {
		// TODO Auto-generated method stub
		return false;
	}

}
