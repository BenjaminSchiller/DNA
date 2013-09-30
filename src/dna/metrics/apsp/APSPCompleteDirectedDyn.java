package dna.metrics.apsp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;

import dna.graph.IElement;
import dna.graph.edges.DirectedEdge;
import dna.graph.nodes.DirectedNode;
import dna.updates.Batch;
import dna.updates.EdgeAddition;
import dna.updates.EdgeRemoval;
import dna.updates.NodeAddition;
import dna.updates.NodeRemoval;
import dna.updates.Update;

@SuppressWarnings("rawtypes")
public class APSPCompleteDirectedDyn extends APSPCompleteDirected {

	public APSPCompleteDirectedDyn() {
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
		DirectedEdge e = (DirectedEdge) ((EdgeRemoval) u).getEdge();
		DirectedNode src = e.getSrc();
		DirectedNode dst = e.getDst();

		// check all trees if the deleted edge is in the tree
		for (IElement ie : g.getNodes()) {
			DirectedNode r = (DirectedNode) ie;
			HashMap<DirectedNode, DirectedNode> parent = this.parentsOut.get(r);
			HashMap<DirectedNode, Integer> height = this.heightsOut.get(r);

			// if the source or dst or edge is not in tree do nothing
			if (height.get(src) == Integer.MAX_VALUE
					|| height.get(dst) == Integer.MAX_VALUE
					|| height.get(dst) == 0 || parent.get(dst) != src) {
				continue;
			}

			// Queues and data structure for tree change
			HashSet<DirectedNode> uncertain = new HashSet<DirectedNode>();
			HashSet<DirectedNode> changed = new HashSet<DirectedNode>();

			Queue<DirectedNode>[] qLevel = new LinkedList[g.getNodeCount()];
			// g.getNodes().size()- lowestHeight
			for (int i = 0; i < qLevel.length; i++) {
				qLevel[i] = new LinkedList<DirectedNode>();
			}

			PriorityQueue<QueueElement<DirectedNode>> q = new PriorityQueue<QueueElement<DirectedNode>>();

			q.add(new QueueElement<DirectedNode>(dst, height.get(dst)
					.doubleValue()));

			uncertain.add(dst);
			parent.remove(dst);

			while (!q.isEmpty()) {
				QueueElement<DirectedNode> qE = q.poll();
				DirectedNode w = qE.e;
				// if (r.getIndex() == 842)
				// System.out.println("hey");
				// ;

				int key = ((Double) qE.distance).intValue();

				// find the new shortest path
				int dist = Integer.MAX_VALUE;

				ArrayList<DirectedNode> minSettled = new ArrayList<DirectedNode>();
				ArrayList<DirectedNode> min = new ArrayList<DirectedNode>();
				for (IElement iEgde : w.getIncomingEdges()) {
					DirectedEdge edge = (DirectedEdge) iEgde;
					DirectedNode z = edge.getSrc();
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
						q.add(new QueueElement<DirectedNode>(w,
								((Integer) dist).doubleValue()));
						uncertain.remove(w);
						for (IElement iEgde : w.getOutgoingEdges()) {
							DirectedEdge edge = (DirectedEdge) iEgde;
							DirectedNode z = edge.getSrc();
							if (parent.get(z) == w) {
								parent.remove(z);
								uncertain.add(z);
								if (key > height.get(z))
									System.out.println("fuck");
								q.add(new QueueElement<DirectedNode>(z, height
										.get(z).doubleValue()));
							}
						}
					}
					continue;
				}
				if (dist > key) {
					q.add(new QueueElement<DirectedNode>(w, ((Integer) dist)
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
				for (IElement iEgde : w.getOutgoingEdges()) {
					DirectedEdge edge = (DirectedEdge) iEgde;
					DirectedNode z = edge.getSrc();
					if (height.get(z) > dist + 1) {
						q.remove(new QueueElement<DirectedNode>(z,
								((Integer) (dist + 1)).doubleValue()));
						q.add(new QueueElement<DirectedNode>(z,
								((Integer) (dist + 1)).doubleValue()));
					}
				}
			}
		}
		return true;
	}

	// private boolean applyAfterEdgeRemoval1(Update u) {
	// DirectedGraph g = (DirectedGraph) this.g;
	// DirectedEdge e = (DirectedEdge) ((EdgeRemoval) u).getEdge();
	// DirectedNode src = e.getSrc();
	// DirectedNode dst = e.getDst();
	//
	// // check all trees if the deleted edge is in the tree
	// for (DirectedNode r : g.getNodes()) {
	// HashMap<DirectedNode, DirectedNode> parent = this.parentsOut.get(r);
	// HashMap<DirectedNode, Integer> height = this.heightsOut.get(r);
	//
	// // if the source or dst or edge is not in tree do nothing
	// if (height.get(src) == Integer.MAX_VALUE
	// || height.get(dst) == Integer.MAX_VALUE
	// || height.get(dst) == 0 || parent.get(dst) != src) {
	// continue;
	// }
	//
	// // Queues and data structure for tree change
	// HashSet<DirectedNode> uncertain = new HashSet<DirectedNode>();
	// HashSet<DirectedNode> touched = new HashSet<DirectedNode>();
	//
	// int lowestHeight = height.get(dst);
	// Queue<DirectedNode>[] qLevel = new LinkedList[g.getNodeCount()];
	// // g.getNodes().size()- lowestHeight
	// for (int i = 0; i < qLevel.length; i++) {
	// qLevel[i] = new LinkedList<DirectedNode>();
	// }
	//
	// // set data structure for dst Node
	// qLevel[0].add(dst);
	// uncertain.add(dst);
	// parent.remove(dst);
	//
	// int maxHeight = height.get(dst);
	// for (int i = lowestHeight; i < qLevel.length; i++) {
	// while (!qLevel[i].isEmpty()) {
	// DirectedNode w = qLevel[i].poll();
	// this.z�hl++;
	//
	// // find the new shortest path
	// int dist = Integer.MAX_VALUE;
	// int realMin = Integer.MAX_VALUE;
	// ArrayList<DirectedNode> min = new ArrayList<DirectedNode>();
	// for (DirectedEdge edge : w.getIncomingEdges()) {
	// DirectedNode z = edge.getSrc();
	// if (height.get(z) != Integer.MAX_VALUE)
	// realMin = Math.min(realMin, height.get(z) + 1);
	//
	// if (height.get(z) == Integer.MAX_VALUE
	// || !uncertain.contains(z)) {
	// continue;
	// }
	//
	// if (height.get(z) + 1 < dist) {
	// min.clear();
	// min.add(z);
	// dist = height.get(z) + 1;
	// continue;
	// }
	// if (height.get(z) + 1 == dist) {
	// min.add(z);
	// continue;
	// }
	// }
	//
	// // if their is no connection to the three, remove node
	// // form
	// // data set
	// //
	// if (dist == Integer.MAX_VALUE
	// || dist >= g.getNodeCount() - lowestHeight - 1
	// || dist >= 16) {
	// height.put(w, Integer.MAX_VALUE);
	// parent.remove(w);
	// uncertain.remove(w);
	// for (DirectedEdge ed : w.getOutgoingEdges()) {
	// DirectedNode z = ed.getDst();
	// if (parent.get(z) == w && !touched.contains(z)) {
	// qLevel[i + 1].add(z);
	// uncertain.add(z);
	// touched.add(z);
	// maxHeight = Math.max(maxHeight, i + 1);
	// }
	// }
	// continue;
	// }
	//
	// // connect to the highest uncertain node
	// boolean found = false;
	// for (DirectedNode mNode : min) {
	// if (uncertain.contains(mNode)) {
	// continue;
	// }
	// if (height.get(mNode) + 1 == i + lowestHeight) {
	// uncertain.remove(w);
	// height.put(w, height.get(mNode) + 1);
	// parent.put(w, mNode);
	// found = true;
	// break;
	// }
	//
	// }
	//
	// // else connect to another node
	// if (!found) {
	// for (DirectedEdge ed : w.getOutgoingEdges()) {
	// DirectedNode z = ed.getDst();
	// if (parent.get(z) == w && !touched.contains(z)) {
	// qLevel[i + 1].add(z);
	// uncertain.add(z);
	// touched.add(z);
	// }
	// }
	// qLevel[height.get(min.get(0)) + 1 - lowestHeight]
	// .add(w);
	// height.put(w, height.get(min.get(0)) + 1);
	// parent.put(w, min.get(0));
	// maxHeight = Math.max(maxHeight, height.get(min.get(0))
	// + 1 - lowestHeight);
	// }
	// }
	// }
	// }
	// return true;
	// }

	private boolean applyAfterEdgeAddition(Update u) {

		DirectedEdge e = (DirectedEdge) ((EdgeAddition) u).getEdge();
		DirectedNode src = e.getSrc();
		DirectedNode dst = e.getDst();

		for (IElement ie : g.getNodes()) {
			DirectedNode s = (DirectedNode) ie;
			HashMap<DirectedNode, DirectedNode> parent = this.parentsOut.get(s);
			HashMap<DirectedNode, Integer> height = this.heightsOut.get(s);

			if (src.equals(s)) {
				this.check(src, dst, parent, height);
				continue;
			}

			if (!parent.containsKey(src) && !parent.containsKey(dst)) {
				continue;
			}

			this.check(src, dst, parent, height);
		}
		return true;
	}

	protected void check(DirectedNode a, DirectedNode b,
			HashMap<DirectedNode, DirectedNode> parent,
			HashMap<DirectedNode, Integer> height) {
		int h_a = height.get(a);
		int h_b = height.get(b);
		if (h_a == Integer.MAX_VALUE || h_a + 1 >= h_b) {
			return;
		}

		parent.put(b, a);
		h_b = h_a + 1;
		height.put(b, h_b);
		for (IElement iEdge : b.getOutgoingEdges()) {
			DirectedEdge edge = (DirectedEdge) iEdge;
			DirectedNode c = edge.getSrc();
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
