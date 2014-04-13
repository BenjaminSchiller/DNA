package dna.metrics.shortestPaths;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

import dna.graph.IElement;
import dna.graph.edges.UndirectedEdge;
import dna.graph.nodes.Node;
import dna.graph.nodes.UndirectedNode;
import dna.updates.batch.Batch;
import dna.updates.update.EdgeAddition;
import dna.updates.update.EdgeRemoval;
import dna.updates.update.NodeAddition;
import dna.updates.update.NodeRemoval;
import dna.updates.update.Update;
import dna.util.ArrayUtils;

public class UndirectedShortestPathsU extends UndirectedShortestPaths {

	public UndirectedShortestPathsU() {
		super("UndirectedShortestPathsU", ApplicationType.BeforeUpdate);
	}

	protected HashMap<Node, HashMap<Node, Node>> parents;

	protected HashMap<Node, HashMap<Node, Integer>> heights;

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
		if (u instanceof NodeAddition) {
			// TODO implement SP update node addition
			return false;
		} else if (u instanceof NodeRemoval) {
			// TODO implement SP update node removal
			return false;
		} else if (u instanceof EdgeAddition) {
			UndirectedEdge e = (UndirectedEdge) ((EdgeAddition) u).getEdge();
			UndirectedNode n1 = (UndirectedNode) e.getNode1();
			UndirectedNode n2 = (UndirectedNode) e.getNode2();

			for (IElement sUncasted : g.getNodes()) {
				Node s = (Node) sUncasted;
				HashMap<Node, Node> parent = this.parents.get(s);
				HashMap<Node, Integer> height = this.heights.get(s);

				if (n1.equals(s)) {
					this.check(n1, n2, parent, height);
					continue;
				}
				if (n2.equals(s)) {
					this.check(n2, n1, parent, height);
					continue;
				}
				if (!parent.containsKey(n1) && !parent.containsKey(n2)) {
					continue;
				}
				this.check(n1, n2, parent, height);
				this.check(n2, n1, parent, height);
			}

			this.spl = ArrayUtils.truncate(this.spl, 0);
			this.diam = spl.length - 1;

		} else if (u instanceof EdgeRemoval) {
			// TODO implement SP update edge removal
			return false;
		}

		return true;
	}

	protected void check(Node a, Node b, HashMap<Node, Node> parent,
			HashMap<Node, Integer> height) {
		int h_a = height.get(a);
		int h_b = height.get(b);
		if (h_a == Integer.MAX_VALUE || h_a + 1 >= h_b) {
			return;
		}
		if (h_b != Integer.MAX_VALUE) {
			this.spl = ArrayUtils.decr(this.spl, h_b);
		}
		if (!parent.containsKey(b)) {
			this.existingPaths++;
		}
		parent.put(b, a);
		h_b = h_a + 1;
		height.put(b, h_b);
		this.spl = ArrayUtils.incr(this.spl, h_b);
		for (IElement eUncasted : b.getEdges()) {
			UndirectedEdge e = (UndirectedEdge) eUncasted;
			UndirectedNode c = (UndirectedNode) e.getDifferingNode(b);
			this.check(b, c, parent, height);
		}
	}

	@Override
	public boolean applyAfterUpdate(Update u) {
		return false;
	}

	@Override
	public boolean compute() {
		for (IElement sUncasted : g.getNodes()) {
			Node s = (Node) sUncasted;
			HashMap<Node, Node> parent = new HashMap<Node, Node>();
			HashMap<Node, Integer> height = new HashMap<Node, Integer>();

			for (IElement tUncasted : g.getNodes()) {
				UndirectedNode t = (UndirectedNode) tUncasted;
				if (t.equals(s)) {
					height.put(s, 0);
				} else {
					height.put(t, Integer.MAX_VALUE);
				}
			}

			Queue<Node> q = new LinkedList<Node>();
			q.add(s);

			while (!q.isEmpty()) {
				Node current = q.poll();
				for (IElement eUncasted : current.getEdges()) {
					UndirectedEdge e = (UndirectedEdge) eUncasted;
					Node neighbor = e.getDifferingNode(current);
					if (height.get(neighbor) != Integer.MAX_VALUE) {
						continue;
					}
					height.put(neighbor, height.get(current) + 1);
					parent.put(neighbor, current);
					this.spl = ArrayUtils.incr(this.spl, height.get(neighbor));
					q.add(neighbor);
					this.existingPaths++;
				}
			}

			this.parents.put(s, parent);
			this.heights.put(s, height);
		}

		this.cpl = -1;
		this.diam = this.spl.length - 1;

		return true;
	}

	public void init_() {
		super.init_();
		this.parents = new HashMap<Node, HashMap<Node, Node>>();
		this.heights = new HashMap<Node, HashMap<Node, Integer>>();
	}

	public void reset_() {
		super.reset_();
		this.parents = null;
		this.heights = null;
	}

}
