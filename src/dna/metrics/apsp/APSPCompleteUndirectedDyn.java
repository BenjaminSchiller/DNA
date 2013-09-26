package dna.metrics.apsp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;

import dna.graph.undirected.UndirectedEdge;
import dna.graph.undirected.UndirectedGraph;
import dna.graph.undirected.UndirectedNode;
import dna.updates.Batch;
import dna.updates.EdgeAddition;
import dna.updates.EdgeRemoval;
import dna.updates.NodeAddition;
import dna.updates.NodeRemoval;
import dna.updates.Update;

@SuppressWarnings("rawtypes")
public class APSPCompleteUndirectedDyn extends APSPCompleteUndirected {

	public APSPCompleteUndirectedDyn() {
		super("APSP Complete DYN", ApplicationType.AfterUpdate);

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
		UndirectedGraph g = (UndirectedGraph) this.g;
		UndirectedEdge e = (UndirectedEdge) ((EdgeRemoval) u).getEdge();
		UndirectedNode n1 = e.getNode1();
		UndirectedNode n2 = e.getNode2();

		// check all trees if the deleted edge is in the tree
		for (UndirectedNode r : g.getNodes()) {
			HashMap<UndirectedNode, UndirectedNode> parent = this.parentsOut
					.get(r);
			HashMap<UndirectedNode, Integer> height = this.heightsOut.get(r);

			UndirectedNode src;
			UndirectedNode dst;

			if (height.get(n1) > height.get(n2)) {
				src = n2;
				dst = n1;
			} else {
				src = n1;
				dst = n2;
			}

			// if the source or dst or edge is not in tree do nothing
			if (height.get(src) == Integer.MAX_VALUE
					|| height.get(dst) == Integer.MAX_VALUE
					|| height.get(dst) == 0 || parent.get(dst) != src) {
				continue;
			}

			// Queues and data structure for tree change
			HashSet<UndirectedNode> uncertain = new HashSet<UndirectedNode>();
			HashSet<UndirectedNode> changed = new HashSet<UndirectedNode>();

			Queue<UndirectedNode>[] qLevel = new LinkedList[g.getNodeCount()];
			// g.getNodes().size()- lowestHeight
			for (int i = 0; i < qLevel.length; i++) {
				qLevel[i] = new LinkedList<UndirectedNode>();
			}

			PriorityQueue<QueueElement<UndirectedNode>> q = new PriorityQueue<QueueElement<UndirectedNode>>();

			q.add(new QueueElement<UndirectedNode>(dst, height.get(dst)
					.doubleValue()));

			uncertain.add(dst);
			parent.remove(dst);

			while (!q.isEmpty()) {
				QueueElement<UndirectedNode> qE = q.poll();
				UndirectedNode w = qE.e;
				// if (r.getIndex() == 842)
				// System.out.println("hey");
				// ;

				int key = ((Double) qE.distance).intValue();

				// find the new shortest path
				int dist = Integer.MAX_VALUE;

				ArrayList<UndirectedNode> minSettled = new ArrayList<UndirectedNode>();
				ArrayList<UndirectedNode> min = new ArrayList<UndirectedNode>();
				for (UndirectedEdge edge : w.getEdges()) {
					UndirectedNode z = edge.getDifferingNode(w);
					if (parent.get(w) == z || changed.contains(z)
							|| height.get(z) == Integer.MAX_VALUE) {
						continue;
					}
					if (height.get(z) + 1 < dist) {
						min.clear();
						minSettled.clear();
						min.add(z);
						if (!uncertain.contains(z))
							minSettled.add(z);
						dist = height.get(z) + 1;
						continue;
					}
					if (height.get(z) + 1 == dist) {
						min.add(z);
						if (!uncertain.contains(z))
							minSettled.add(z);
						continue;
					}
				}
				boolean noPossibleNeighbour = (key >= g.getNodeCount() && dist > g
						.getNodeCount())
						|| (min.isEmpty() && (!uncertain.contains(w) || (key == dist)));

				// no neighbour found
				if (noPossibleNeighbour) {
					height.put(w, Integer.MAX_VALUE);
					parent.remove(w);
					continue;
				}
				if (uncertain.contains(w)) {
					if (key == dist) {
						if (minSettled.isEmpty()) {
							parent.put(w, min.get(0));
						} else {
							parent.put(w, minSettled.get(0));
						}
					} else {
						changed.add(w);
						q.add(new QueueElement<UndirectedNode>(w,
								((Integer) dist).doubleValue()));
						uncertain.remove(w);
						for (UndirectedEdge ed : w.getEdges()) {
							UndirectedNode z = ed.getDifferingNode(w);
							if (parent.get(z) == w) {
								parent.remove(z);
								uncertain.add(z);
								if (key > height.get(z))
									System.out.println("fuck");
								q.add(new QueueElement<UndirectedNode>(z,
										height.get(z).doubleValue()));
							}
						}
					}
					continue;
				}
				if (dist > key) {
					q.add(new QueueElement<UndirectedNode>(w, ((Integer) dist)
							.doubleValue()));
					continue;
				}
				if (minSettled.isEmpty()) {
					parent.put(w, min.get(0));
				} else {
					parent.put(w, minSettled.get(0));
				}
				changed.remove(w);
				height.put(w, dist);
				for (UndirectedEdge ed : w.getEdges()) {
					UndirectedNode z = ed.getDifferingNode(w);
					if (height.get(z) > dist + 1) {
						q.remove(new QueueElement<UndirectedNode>(z,
								((Integer) (dist + 1)).doubleValue()));
						q.add(new QueueElement<UndirectedNode>(z,
								((Integer) (dist + 1)).doubleValue()));
					}
				}
			}
		}
		return true;
	}

	private boolean applyAfterEdgeRemoval1(Update u) {

		UndirectedGraph g = (UndirectedGraph) this.g;

		UndirectedEdge e = (UndirectedEdge) ((EdgeRemoval) u).getEdge();
		UndirectedNode n1 = e.getNode1();
		UndirectedNode n2 = e.getNode2();

		// check all trees if the deleted edge is in the tree
		for (UndirectedNode r : g.getNodes()) {

			HashMap<UndirectedNode, UndirectedNode> parent = this.parentsOut
					.get(r);
			HashMap<UndirectedNode, Integer> height = this.heightsOut.get(r);

			// if the source or dst or edge is not in tree do nothing
			if (height.get(n1) == Integer.MAX_VALUE
					|| height.get(n2) == Integer.MAX_VALUE) {
				continue;
			}

			if (height.get(n1) > height.get(n2)) {
				n1 = e.getNode2();
				n2 = e.getNode1();
			}

			// Queues and data structure for tree change
			HashSet<UndirectedNode> uncertain = new HashSet<UndirectedNode>();
			HashSet<UndirectedNode> touched = new HashSet<UndirectedNode>();
			Queue<UndirectedNode>[] qLevel = new LinkedList[g.getNodeCount()];
			for (int i = 0; i < qLevel.length; i++) {
				qLevel[i] = new LinkedList<UndirectedNode>();
			}

			// set data structure for dst Node
			qLevel[height.get(n2)].add(n2);
			uncertain.add(n2);
			touched.add(n2);

			for (int i = 0; i < qLevel.length; i++) {
				while (!qLevel[i].isEmpty()) {
					UndirectedNode w = qLevel[i].poll();

					// all child's of node w need to be checked
					// find the new shortest path
					int dist = Integer.MAX_VALUE;
					ArrayList<UndirectedNode> min = new ArrayList<UndirectedNode>();
					for (UndirectedEdge ed : w.getEdges()) {
						UndirectedNode z = ed.getDifferingNode(w);
						if (parent.get(z) == w && !touched.contains(z)) {
							qLevel[i + 1].add(z);
							uncertain.add(z);
							touched.add(z);
						}
						if (height.get(z) < dist) {
							min.clear();
							min.add(z);
							dist = height.get(z);
							continue;
						}
						if (height.get(z) == dist) {
							min.add(z);
							continue;
						}
					}

					// if their is no connection to the three, remove node form
					// data set
					if (dist == Integer.MAX_VALUE
							|| dist >= g.getNodeCount() - 1) {
						height.put(w, Integer.MAX_VALUE);
						parent.remove(w);
						uncertain.remove(w);
						continue;
					}

					// connect to the highest uncertain node
					boolean found = false;
					for (UndirectedNode mNode : min) {
						if ((!uncertain.contains(mNode))
								&& (height.get(mNode) + 1 == i || height
										.get(mNode) == i)) {
							uncertain.remove(w);
							height.put(w, height.get(mNode) + 1);
							parent.put(w, mNode);
							found = true;
							break;
						}
					}

					// else connect to another node
					if (!found) {
						qLevel[height.get(min.get(0)) + 1].add(w);
						height.put(w, height.get(min.get(0)) + 1);
						parent.put(w, min.get(0));
					}

				}
			}

		}

		return true;
	}

	private boolean applyAfterEdgeAddition(Update u) {

		UndirectedGraph g = (UndirectedGraph) this.g;

		UndirectedEdge e = (UndirectedEdge) ((EdgeAddition) u).getEdge();
		UndirectedNode n1 = e.getNode1();
		UndirectedNode n2 = e.getNode2();

		for (UndirectedNode s : g.getNodes()) {
			HashMap<UndirectedNode, UndirectedNode> parent = this.parentsOut
					.get(s);
			HashMap<UndirectedNode, Integer> height = this.heightsOut.get(s);

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
		parent.put(b, a);
		h_b = h_a + 1;
		height.put(b, h_b);
		for (UndirectedEdge e : b.getEdges()) {
			UndirectedNode c = e.getDifferingNode(b);
			this.check(b, c, parent, height);
		}
	}

	private boolean applyAfterNodeRemoval(Update u) {
		return false;
	}

	private boolean applyAfterNodeAddition(Update u) {
		return false;
	}

}
