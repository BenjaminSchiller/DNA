package dna.metrics.connectedComponents;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import sun.misc.Queue;
import dna.diff.Diff;
import dna.diff.DiffNotApplicableException;
import dna.graph.Edge;
import dna.graph.Graph;
import dna.graph.Node;

public class CCUndirectedDyn extends CCUndirected {

	boolean searchSmallerComponent;

	public CCUndirectedDyn() {
		super("CCdirectedDyn()", false, true, false);
	}

	protected void init(Graph g) {
		super.init(g);
		searchSmallerComponent = false;
	}

	public void setSearchSmallerComponet(boolean searchSmallerComponent) {
		this.searchSmallerComponent = searchSmallerComponent;
	}

	@Override
	protected boolean applyBeforeDiff_(Diff d)
			throws DiffNotApplicableException {
		throw new DiffNotApplicableException("before diff");
	}

	@Override
	protected boolean applyAfterEdgeAddition_(Diff d, Edge e) {
		Node src = e.getSrc();
		Node dst = e.getDst();
		if (this.nodeComponentMembership[src.getIndex()] != this.nodeComponentMembership[dst
				.getIndex()]) {

			SpanningTreeNode temp = this.nodesTreeElement[dst.getIndex()];
			SpanningTreeNode newParent = this.nodesTreeElement[src.getIndex()];

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
			updateComponentIndex(this.nodeComponentMembership[src.getIndex()],
					this.nodesTreeElement[dst.getIndex()]);

		}

		return true;
	}

	private void updateComponentIndex(int component, SpanningTreeNode dst) {
		Queue q = new Queue();
		q.enqueue(dst);
		try {
			while (!q.isEmpty()) {
				SpanningTreeNode temp = (SpanningTreeNode) q.dequeue();
				for (SpanningTreeNode n : temp.getChildren()) {
					q.enqueue(n);

				}
				this.nodeComponentMembership[temp.getNode().getIndex()] = component;
			}

		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	@Override
	protected boolean applyAfterEdgeRemoval_(Diff d, Edge e)
			throws DiffNotApplicableException {

		Node src = e.getSrc();
		Node dst = e.getDst();

		if (this.nodeComponentMembership[src.getIndex()] == this.nodeComponentMembership[dst
				.getIndex()]) {

			SpanningTreeNode srcTreeElement = this.nodesTreeElement[src
					.getIndex()];
			SpanningTreeNode dstTreeElement = this.nodesTreeElement[dst
					.getIndex()];

			if (srcTreeElement.getChildren().contains(dstTreeElement)) {

				if (src.getNeighbors().contains(dst.getNeighbors())) {

					for (Node n : src.getNeighbors()) {
						if (dst.getNeighbors().contains(n)) {
							SpanningTreeNode newparent = nodesTreeElement[n
									.getIndex()];
							newparent.addChild(dstTreeElement);
							dstTreeElement.setParent(newparent);
						}
					}

				} else {

					boolean edgeRemovedWithoutSeperation = saveRemove(
							srcTreeElement, dstTreeElement);

					if (edgeRemovedWithoutSeperation) {

						dstTreeElement.setParent(null);
						dstTreeElement.setRoot(true);
						srcTreeElement.removeChild(dstTreeElement);
						updateComponentIndex(dst.getIndex(), dstTreeElement);
						this.componentList.add(dstTreeElement);
					}
				}
			}
		}
		return true;
	}

	private boolean saveRemovedfs(SpanningTreeNode node) {
		boolean[] visited = new boolean[this.g.getNodes().length];
		Stack<SpanningTreeNode> s = new Stack<SpanningTreeNode>();
		s.push(node);
		while (!s.isEmpty()) {
			SpanningTreeNode temp = s.pop();
			visited[temp.getNode().getIndex()] = true;
			if (temp.isRoot()) {

				return true;
			}
			for (Node n : temp.getNode().getNeighbors()) {
				if (!visited[n.getIndex()]) {

				}
			}
		}
		return false;
	}

	private boolean saveRemove(SpanningTreeNode srcTreeElement,
			SpanningTreeNode dstTreeElement) {
		try {
			Queue q = new Queue();
			dstTreeElement.setParent(null);
			q.enqueue(dstTreeElement);

			Set<Node> nodes = bfs(dstTreeElement);

			for (Node n : dstTreeElement.getNode().getNeighbors()) {
				if (!nodes.contains(n)
						&& this.nodeComponentMembership[n.getIndex()] == this.nodeComponentMembership[dstTreeElement
								.getNode().getIndex()]) {
					dstTreeElement
							.setParent(this.nodesTreeElement[n.getIndex()]);
					return true;
				}
			}

			while (!q.isEmpty()) {
				SpanningTreeNode temp = (SpanningTreeNode) q.dequeue();

				for (Node n : temp.getNode().getNeighbors()) {

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
					q.enqueue(spanningTreeNode);
				}

			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	private Set<Node> bfs(SpanningTreeNode treeElement) {
		Set<Node> nodes = new HashSet<Node>();
		try {
			Queue q = new Queue();
			q.enqueue(treeElement);
			while (!q.isEmpty()) {
				SpanningTreeNode temp = (SpanningTreeNode) q.dequeue();
				for (SpanningTreeNode n : temp.getChildren()) {
					q.enqueue(n);
					nodes.add(n.getNode());
				}

			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return nodes;

	}

	@Override
	protected boolean applyAfterDiff_(Diff d) throws DiffNotApplicableException {
		throw new DiffNotApplicableException("after diff");
	}

}
