package dna.metrics.shortestPaths;

import java.util.HashMap;
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
import dna.util.ArrayUtils;

public class UndirectedShortestPathsUpdate extends UndirectedShortestPaths {

	public UndirectedShortestPathsUpdate() {
		super("undirectedShortestPathsUpdate", ApplicationType.BeforeUpdate);
	}

	protected HashMap<UndirectedNode, HashMap<UndirectedNode, UndirectedNode>> parents;

	protected HashMap<UndirectedNode, HashMap<UndirectedNode, Integer>> heights;

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
				UndirectedNode s = (UndirectedNode) sUncasted;
				HashMap<UndirectedNode, UndirectedNode> parent = this.parents
						.get(s);
				HashMap<UndirectedNode, Integer> height = this.heights.get(s);

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

	protected void check(UndirectedNode a, UndirectedNode b,
			HashMap<UndirectedNode, UndirectedNode> parent,
			HashMap<UndirectedNode, Integer> height) {
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
			UndirectedNode c = e.getDifferingNode(b);
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
			UndirectedNode s = (UndirectedNode) sUncasted;
			HashMap<UndirectedNode, UndirectedNode> parent = new HashMap<UndirectedNode, UndirectedNode>();
			HashMap<UndirectedNode, Integer> height = new HashMap<UndirectedNode, Integer>();

			for (IElement tUncasted : g.getNodes()) {
				UndirectedNode t = (UndirectedNode) tUncasted;
				if (t.equals(s)) {
					height.put(s, 0);
				} else {
					height.put(t, Integer.MAX_VALUE);
				}
			}

			Queue<UndirectedNode> q = new LinkedList<UndirectedNode>();
			q.add(s);

			while (!q.isEmpty()) {
				UndirectedNode current = q.poll();
				for (IElement eUncasted : current.getEdges()) {
					UndirectedEdge e = (UndirectedEdge) eUncasted;
					UndirectedNode neighbor = e.getDifferingNode(current);
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
		this.parents = new HashMap<UndirectedNode, HashMap<UndirectedNode, UndirectedNode>>();
		this.heights = new HashMap<UndirectedNode, HashMap<UndirectedNode, Integer>>();
	}

	public void reset_() {
		super.reset_();
		this.parents = null;
		this.heights = null;
	}

}