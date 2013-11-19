package dna.metrics.apsp.allPairShortestPathComplete;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;

import dna.graph.IElement;
import dna.graph.edges.UndirectedEdge;
import dna.graph.nodes.Node;
import dna.metrics.apsp.QueueElement;
import dna.updates.batch.Batch;
import dna.updates.update.EdgeAddition;
import dna.updates.update.EdgeRemoval;
import dna.updates.update.NodeAddition;
import dna.updates.update.NodeRemoval;
import dna.updates.update.Update;

public class UndirectedAllPairShortestPathCompleteU extends
		AllPairShortestPathComplete {

	public UndirectedAllPairShortestPathCompleteU() {
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
		UndirectedEdge e = (UndirectedEdge) ((EdgeRemoval) u).getEdge();
		Node n1 = e.getNode1();
		Node n2 = e.getNode2();

		// check all trees if the deleted edge is in the tree
		for (IElement ie : g.getNodes()) {
			Node r = (Node) ie;
			HashMap<Node, Node> parent = this.parentsOut.get(r);
			HashMap<Node, Integer> height = this.heightsOut.get(r);

			Node src;
			Node dst;

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
			HashSet<Node> uncertain = new HashSet<Node>();
			HashSet<Node> changed = new HashSet<Node>();

			PriorityQueue<QueueElement<Node>> q = new PriorityQueue<QueueElement<Node>>();

			q.add(new QueueElement<Node>(dst, height.get(dst).doubleValue()));

			uncertain.add(dst);
			parent.remove(dst);

			while (!q.isEmpty()) {
				QueueElement<Node> qE = q.poll();
				Node w = qE.e;

				int key = ((Double) qE.distance).intValue();

				// find the new shortest path
				int dist = Integer.MAX_VALUE;

				ArrayList<Node> minSettled = new ArrayList<Node>();
				ArrayList<Node> min = new ArrayList<Node>();
				for (IElement iEdge : w.getEdges()) {
					UndirectedEdge edge = (UndirectedEdge) iEdge;
					Node z = edge.getDifferingNode(w);
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
						q.add(new QueueElement<Node>(w, ((Integer) dist)
								.doubleValue()));
						uncertain.remove(w);
						for (IElement iEdge : w.getEdges()) {
							UndirectedEdge ed = (UndirectedEdge) iEdge;
							Node z = ed.getDifferingNode(w);
							if (parent.get(z) == w) {
								parent.remove(z);
								uncertain.add(z);

								q.add(new QueueElement<Node>(z, height.get(z)
										.doubleValue()));
							}
						}
					}
					continue;
				}
				if (dist > key) {
					q.add(new QueueElement<Node>(w, ((Integer) dist)
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
				for (IElement iEdge : w.getEdges()) {
					UndirectedEdge ed = (UndirectedEdge) iEdge;
					Node z = ed.getDifferingNode(w);
					if (height.get(z) > dist + 1) {
						q.remove(new QueueElement<Node>(z,
								((Integer) (dist + 1)).doubleValue()));
						q.add(new QueueElement<Node>(z, ((Integer) (dist + 1))
								.doubleValue()));
					}
				}
			}
		}
		return true;
	}

	// private boolean applyAfterEdgeRemoval1(Update u) {
	//
	// UndirectedGraph g = (UndirectedGraph) this.g;
	//
	// UndirectedEdge e = (UndirectedEdge) ((EdgeRemoval) u).getEdge();
	// Node n1 = e.getNode1();
	// Node n2 = e.getNode2();
	//
	// // check all trees if the deleted edge is in the tree
	// for (Node r : g.getNodes()) {
	//
	// HashMap<Node, Node> parent = this.parentsOut
	// .get(r);
	// HashMap<Node, Integer> height = this.heightsOut.get(r);
	//
	// // if the source or dst or edge is not in tree do nothing
	// if (height.get(n1) == Integer.MAX_VALUE
	// || height.get(n2) == Integer.MAX_VALUE) {
	// continue;
	// }
	//
	// if (height.get(n1) > height.get(n2)) {
	// n1 = e.getNode2();
	// n2 = e.getNode1();
	// }
	//
	// // Queues and data structure for tree change
	// HashSet<Node> uncertain = new HashSet<Node>();
	// HashSet<Node> touched = new HashSet<Node>();
	// Queue<Node>[] qLevel = new LinkedList[g.getNodeCount()];
	// for (int i = 0; i < qLevel.length; i++) {
	// qLevel[i] = new LinkedList<Node>();
	// }
	//
	// // set data structure for dst Node
	// qLevel[height.get(n2)].add(n2);
	// uncertain.add(n2);
	// touched.add(n2);
	//
	// for (int i = 0; i < qLevel.length; i++) {
	// while (!qLevel[i].isEmpty()) {
	// Node w = qLevel[i].poll();
	//
	// // all child's of node w need to be checked
	// // find the new shortest path
	// int dist = Integer.MAX_VALUE;
	// ArrayList<Node> min = new ArrayList<Node>();
	// for (UndirectedEdge ed : w.getEdges()) {
	// Node z = ed.getDifferingNode(w);
	// if (parent.get(z) == w && !touched.contains(z)) {
	// qLevel[i + 1].add(z);
	// uncertain.add(z);
	// touched.add(z);
	// }
	// if (height.get(z) < dist) {
	// min.clear();
	// min.add(z);
	// dist = height.get(z);
	// continue;
	// }
	// if (height.get(z) == dist) {
	// min.add(z);
	// continue;
	// }
	// }
	//
	// // if their is no connection to the three, remove node form
	// // data set
	// if (dist == Integer.MAX_VALUE
	// || dist >= g.getNodeCount() - 1) {
	// height.put(w, Integer.MAX_VALUE);
	// parent.remove(w);
	// uncertain.remove(w);
	// continue;
	// }
	//
	// // connect to the highest uncertain node
	// boolean found = false;
	// for (Node mNode : min) {
	// if ((!uncertain.contains(mNode))
	// && (height.get(mNode) + 1 == i || height
	// .get(mNode) == i)) {
	// uncertain.remove(w);
	// height.put(w, height.get(mNode) + 1);
	// parent.put(w, mNode);
	// found = true;
	// break;
	// }
	// }
	//
	// // else connect to another node
	// if (!found) {
	// qLevel[height.get(min.get(0)) + 1].add(w);
	// height.put(w, height.get(min.get(0)) + 1);
	// parent.put(w, min.get(0));
	// }
	//
	// }
	// }
	//
	// }
	//
	// return true;
	// }

	private boolean applyAfterEdgeAddition(Update u) {

		UndirectedEdge e = (UndirectedEdge) ((EdgeAddition) u).getEdge();
		Node n1 = e.getNode1();
		Node n2 = e.getNode2();

		for (IElement ie : g.getNodes()) {
			Node s = (Node) ie;
			HashMap<Node, Node> parent = this.parentsOut.get(s);
			HashMap<Node, Integer> height = this.heightsOut.get(s);

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

	protected void check(Node a, Node b, HashMap<Node, Node> parent,
			HashMap<Node, Integer> height) {
		int h_a = height.get(a);
		int h_b = height.get(b);
		if (h_a == Integer.MAX_VALUE || h_a + 1 >= h_b) {
			return;
		}
		parent.put(b, a);
		h_b = h_a + 1;
		height.put(b, h_b);
		for (IElement iEdge : b.getEdges()) {
			UndirectedEdge e = (UndirectedEdge) iEdge;
			Node c = e.getDifferingNode(b);
			this.check(b, c, parent, height);
		}
	}

	private boolean applyAfterNodeRemoval(Update u) {
		Node n = (Node) ((NodeRemoval) u).getNode();
		this.heightsOut.remove(n);
		this.parentsOut.remove(n);

		for (IElement ie : n.getEdges()) {
			applyAfterEdgeRemoval(new EdgeRemoval((UndirectedEdge) ie));
		}

		for (IElement ie : this.g.getNodes()) {
			Node r = (Node) ie;
			this.heightsOut.get(r).remove(n);
			this.parentsOut.get(r).remove(n);
		}
		return true;
	}

	private boolean applyAfterNodeAddition(Update u) {
		Node n = (Node) ((NodeAddition) u).getNode();

		this.parentsOut.put(n, new HashMap<Node, Node>());
		this.heightsOut.put(n, new HashMap<Node, Integer>());

		for (IElement ie : this.g.getNodes()) {
			Node r = (Node) ie;

			if (r != n) {
				this.heightsOut.get(r).put(n, Integer.MAX_VALUE);
				this.heightsOut.get(n).put(r, Integer.MAX_VALUE);
			} else {
				this.heightsOut.get(r).put(n, 0);
			}

		}

		return true;
	}
}
