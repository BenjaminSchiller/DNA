package dna.metrics.connectedComponents;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

import dna.graph.IElement;
import dna.graph.edges.UndirectedEdge;
import dna.graph.nodes.UndirectedNode;
import dna.updates.Batch;
import dna.updates.EdgeAddition;
import dna.updates.EdgeRemoval;
import dna.updates.NodeAddition;
import dna.updates.NodeRemoval;
import dna.updates.Update;

@SuppressWarnings("rawtypes")
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

	private boolean applyAfterEdgeRemoval(Update u) {
		UndirectedEdge e = (UndirectedEdge) ((EdgeRemoval) u).getEdge();
		UndirectedNode n1 = e.getNode1();
		UndirectedNode n2 = e.getNode2();

		if (this.nodeComponentMembership.get(n1.getIndex()) == this.nodeComponentMembership
				.get(n2.getIndex())) {

			SpanningTreeNode n1TreeElement = this.nodesTreeElement.get(n1
					.getIndex());
			SpanningTreeNode n2TreeElement = this.nodesTreeElement.get(n2
					.getIndex());

			if (n1TreeElement.getParent() == n2TreeElement) {
				checkEdgeRemoval(n2TreeElement, n1TreeElement);
			} else if (n2TreeElement.getParent() == n1TreeElement) {
				checkEdgeRemoval(n1TreeElement, n2TreeElement);

			}
		}

		return true;
	}

	private void checkEdgeRemoval(SpanningTreeNode n1, SpanningTreeNode n2) {
		boolean neighbourFound = false;

		// check for direct neighbour
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

			neighbourFound = saveRemove(n1, n2);
		}

		if (!neighbourFound) {

			n2.setParent(null);
			n2.setRoot(true);
			updateComponentIndex(n2.getNode().getIndex(), n2);
			this.componentList.add(n2);
		}
	}

	private boolean saveRemove(SpanningTreeNode n1, SpanningTreeNode n2) {

		Queue<SpanningTreeNode> q = new LinkedList<SpanningTreeNode>();
		n2.setParent(null);
		q.add(n2);

		HashSet<SpanningTreeNode> seenNodes = new HashSet<>();

		HashSet<SpanningTreeNode> reachableNodes = new HashSet<>();
		while (!q.isEmpty()) {
			SpanningTreeNode temp = (SpanningTreeNode) q.poll();
			seenNodes.add(temp);
			for (IElement ie : temp.getNode().getEdges()) {
				UndirectedEdge ed = (UndirectedEdge) ie;
				UndirectedNode n = ed.getDifferingNode(temp.getNode());
				if (this.nodesTreeElement.get(n.getIndex()).getParent() == temp) {
					q.add(this.nodesTreeElement.get(n.getIndex()));
					if (reachableNodes.contains(this.nodesTreeElement.get(n
							.getIndex()))) {
						reachableNodes.remove(this.nodesTreeElement.get(n
								.getIndex()));
					}
					continue;
				}

				if (!seenNodes
						.contains(this.nodesTreeElement.get(n.getIndex()))) {
					reachableNodes.add(this.nodesTreeElement.get(n.getIndex()));
				}
			}
		}

		if (reachableNodes.isEmpty()) {
			return false;
		} else {
			SpanningTreeNode connection = reachableNodes.iterator().next();
			for (IElement ie : connection.getNode().getEdges()) {
				UndirectedEdge ed = (UndirectedEdge) ie;
				UndirectedNode node = ed.getDifferingNode(connection.getNode());
				if (seenNodes.contains(this.nodesTreeElement.get(node
						.getIndex()))) {
					restructureTree(connection, node, seenNodes);
					break;
				}
			}
			return true;
		}

	}

	private void restructureTree(SpanningTreeNode connection,
			UndirectedNode node, HashSet<SpanningTreeNode> seenNodes) {
		SpanningTreeNode spn = this.nodesTreeElement.get(node.getIndex());
		spn.setParent(connection);

		Queue<SpanningTreeNode> q = new LinkedList<SpanningTreeNode>();
		q.add(spn);

		while (!seenNodes.isEmpty() && q.isEmpty()) {
			SpanningTreeNode n = q.poll();
			for (IElement ie : n.getNode().getEdges()) {
				UndirectedEdge edge = (UndirectedEdge) ie;
				UndirectedNode node1 = edge.getDifferingNode(spn.getNode());
				SpanningTreeNode n1 = this.nodesTreeElement.get(node1
						.getIndex());
				if (seenNodes.contains(n1)) {
					seenNodes.remove(n1);
					n1.setParent(n);
					q.add(n1);
				}
			}
		}

	}

	private boolean applyAfterEdgeAddition(Update u) {
		UndirectedEdge e = (UndirectedEdge) ((EdgeAddition) u).getEdge();
		UndirectedNode n1 = e.getNode1();
		UndirectedNode n2 = e.getNode2();

		if (this.nodeComponentMembership.get(n1.getIndex()) != this.nodeComponentMembership
				.get(n2.getIndex())) {

			SpanningTreeNode temp = this.nodesTreeElement.get(n2.getIndex());
			SpanningTreeNode newParent = this.nodesTreeElement.get(n1
					.getIndex());

			while (!temp.isRoot()) {
				SpanningTreeNode newChild = temp.getParent();
				// temp.addChild(newChild);
				// temp.removeChild(newParent);

				temp.setParent(newParent);
				newParent = temp;
				temp = newChild;
			}
			// temp.removeChild(newParent);
			temp.setParent(newParent);
			temp.setRoot(false);
			updateComponentIndex(
					this.nodeComponentMembership.get(n1.getIndex()),
					this.nodesTreeElement.get(n2.getIndex()));

		}

		return true;
	}

	private boolean applyAfterNodeRemoval(Update u) {
		UndirectedNode n = (UndirectedNode) ((NodeRemoval) u).getNode();

		for (IElement ie : n.getEdges()) {
			UndirectedEdge e = (UndirectedEdge) ie;
			@SuppressWarnings("unchecked")
			Update up = (Update) new EdgeRemoval(e);
			applyAfterEdgeRemoval(up);
		}
		this.nodeComponentMembership.remove(n.getIndex());
		this.nodesTreeElement.remove(n.getIndex());
		return true;
	}

	private boolean applyAfterNodeAddition(Update u) {
		UndirectedNode n = (UndirectedNode) ((NodeAddition) u).getNode();
		SpanningTreeNode stn = new SpanningTreeNode(n);
		this.componentList.add(stn);
		return true;
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
}
