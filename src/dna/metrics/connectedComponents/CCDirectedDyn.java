package dna.metrics.connectedComponents;

import java.util.ArrayList;
import java.util.List;

import sun.misc.Queue;
import dna.diff.Diff;
import dna.diff.DiffNotApplicableException;
import dna.graph.Edge;
import dna.graph.Node;

public class CCDirectedDyn extends CCDirected {

	public CCDirectedDyn() {
		super("CCDirectedDyn", false, true, false);
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

			if (this.reachableNodesFromComponet.get(
					this.nodeComponentMembership[dst.getIndex()]).contains(src)) {
				newSpanningTree(dst);
			} else {
				this.reachableNodesFromComponet.get(
						this.nodeComponentMembership[src.getIndex()]).add(dst);
			}
		} else {
			this.reachableNodesFromComponet.get(
					this.nodeComponentMembership[src.getIndex()]).add(dst);
		}
		return true;
	}

	private boolean dstCompConnectedToSrcComp(Node src, Node dst) {
		for (Node n : this.reachableNodesFromComponet
				.get(this.nodeComponentMembership[dst.getIndex()])) {
			if (this.nodeComponentMembership[n.getIndex()] == this.nodeComponentMembership[src
					.getIndex()]) {
				return true;
			}
		}
		return false;
	}

	private void newSpanningTree(Node node) {
		try {
			this.compCounter++;
			List<Node> reachables = new ArrayList<Node>();
			int comp = node.getIndex();
			Queue q = new Queue();
			q.enqueue(new SpanningTreeNode(node));
			boolean[] discoverd = new boolean[this.g.getNodes().length];
			boolean[] visited = new boolean[this.g.getNodes().length];
			discoverd[node.getIndex()] = true;
			while (!q.isEmpty()) {
				SpanningTreeNode temp = (SpanningTreeNode) q.dequeue();
				for (Node n : temp.getNode().getOut()) {

					if (!discoverd[n.getIndex()]) {
						discoverd[n.getIndex()] = true;
						SpanningTreeNode newChild = new SpanningTreeNode(n);
						newChild.setParent(temp);
						temp.addChild(newChild);
						q.enqueue(newChild);
						reachables.add(n);

					} else if (!visited[n.getIndex()]) {
						visited[n.getIndex()] = true;
						this.nodeComponentMembership[temp.getNode().getIndex()] = comp;
						this.nodesTreeElement[temp.getNode().getIndex()] = temp;
						reachables.remove(n);
					}
				}
			}

			reachableNodesFromComponet.put(comp, reachables);

		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	@Override
	protected boolean applyAfterEdgeRemoval_(Diff d, Edge e) {
		Node src = e.getSrc();
		Node dst = e.getDst();
		if (this.nodeComponentMembership[src.getIndex()] == this.nodeComponentMembership[dst
				.getIndex()]) {

			SpanningTreeNode srcTreeElement = this.nodesTreeElement[src
					.getIndex()];
			SpanningTreeNode dstTreeElement = this.nodesTreeElement[dst
					.getIndex()];
			if (srcTreeElement.getChildren() != null) {
				if (srcTreeElement.getChildren().contains(dstTreeElement)) {

					newSpanningTree(dstTreeElement.getNode());
				}
			}
		}
		return true;
	}

	@Override
	protected boolean applyAfterDiff_(Diff d) throws DiffNotApplicableException {
		throw new DiffNotApplicableException("after diff");
	}

}
