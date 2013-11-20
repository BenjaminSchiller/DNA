package dna.metrics.apsp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;

import dna.graph.directed.DirectedEdge;
import dna.graph.directed.DirectedEdgeWeighted;
import dna.graph.directed.DirectedGraph;
import dna.graph.directed.DirectedGraphAlAl;
import dna.graph.directed.DirectedNode;
import dna.graph.directed.DirectedNodeAlWeighted;
import dna.updates.Batch;
import dna.updates.EdgeAddition;
import dna.updates.EdgeRemoval;
import dna.updates.NodeAddition;
import dna.updates.NodeRemoval;
import dna.updates.Update;

@SuppressWarnings("rawtypes")
public class APSPCompleteDirectedWithWeightsDyn extends
		APSPCompleteDirectedWithWeights {

	public APSPCompleteDirectedWithWeightsDyn() {
		super("APSP Directed wiht Weights Dyn", ApplicationType.AfterUpdate);
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
		DirectedGraph g = (DirectedGraph) this.g;
		DirectedEdge e = (DirectedEdge) ((EdgeRemoval) u).getEdge();
		DirectedNode src = e.getSrc();
		DirectedNode dst = e.getDst();

		// check all trees if the deleted edge is in the tree
		for (DirectedNode r : g.getNodes()) {
			HashMap<DirectedNode, DirectedNode> parent = this.parents.get(r);
			HashMap<DirectedNode, Double> height = this.heights.get(r);

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

			q.add(new QueueElement<DirectedNode>(dst, height.get(dst)));

			uncertain.add(dst);
			parent.remove(dst);

			while (!q.isEmpty()) {
				QueueElement<DirectedNode> qE = q.poll();
				DirectedNode w = qE.e;
				// if (r.getIndex() == 842)
				// System.out.println("hey");
				// ;

				double key = qE.distance;

				// find the new shortest path
				double dist = Double.MAX_VALUE;

				ArrayList<DirectedNode> minSettled = new ArrayList<DirectedNode>();
				ArrayList<DirectedNode> min = new ArrayList<DirectedNode>();
				for (DirectedEdge edge : w.getIncomingEdges()) {
					DirectedNode z = edge.getSrc();
					DirectedEdgeWeighted ed = (DirectedEdgeWeighted) edge;
					if (parent.get(w) == z || changed.contains(z)
							|| height.get(z) == Integer.MAX_VALUE) {
						continue;
					}
					if (height.get(z) + ed.getWeight() < dist) {
						min.clear();
						minSettled.clear();
						min.add(z);
						if (!uncertain.contains(z))
							minSettled.add(z);
						dist = height.get(z) + ed.getWeight();
						continue;
					}
					if (height.get(z) + ed.getWeight() == dist) {
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
					height.put(w, Double.MAX_VALUE);
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
						q.add(new QueueElement<DirectedNode>(w, dist));
						uncertain.remove(w);
						for (DirectedEdge ed : w.getOutgoingEdges()) {
							DirectedNode z = ed.getDst();
							if (parent.get(z) == w) {
								parent.remove(z);
								uncertain.add(z);
								if (key > height.get(z))
									System.out.println("fuck");
								q.add(new QueueElement<DirectedNode>(z, height
										.get(z)));
							}
						}
					}
					continue;
				}
				if (dist > key) {
					q.add(new QueueElement<DirectedNode>(w, dist));
					continue;
				}
				if (minSettled.isEmpty()) {
					parent.put(w, min.get(0));
				} else {
					parent.put(w, minSettled.get(0));
				}
				changed.remove(w);
				height.put(w, dist);
				for (DirectedEdge ed : w.getOutgoingEdges()) {
					DirectedNode z = ed.getDst();
					DirectedEdgeWeighted edge = (DirectedEdgeWeighted) ed;
					if (height.get(z) > dist + 1) {
						q.remove(new QueueElement<DirectedNode>(z, dist
								+ edge.getWeight()));
						q.add(new QueueElement<DirectedNode>(z, dist
								+ edge.getWeight()));
					}
				}
			}
		}
		return true;
	}

	private boolean applyAfterEdgeRemoval1(Update u) {
		DirectedEdgeWeighted e = (DirectedEdgeWeighted) ((EdgeRemoval) u)
				.getEdge();

		DirectedGraph g = (DirectedGraph) this.g;

		for (DirectedNode s : g.getNodes()) {
			HashMap<DirectedNode, DirectedNode> parent = parents.get(s);
			HashMap<DirectedNode, Double> height = heights.get(s);

			DirectedNode src = e.getSrc();
			DirectedNode dst = e.getDst();
			if (parent.get(dst) != src) {
				continue;
			}
			PriorityQueue<QueueElement<DirectedNode>> q = new PriorityQueue<>();
			HashSet<DirectedNode> uncertain = new HashSet<>();
			HashSet<DirectedNode> touched = new HashSet<>();

			q.add(new QueueElement(dst, height.get(dst)));
			uncertain.add(dst);
			touched.add(dst);
			while (!q.isEmpty()) {

				QueueElement<DirectedNode> c = q.poll();
				DirectedNode w = c.e;

				// all childs of node w need to be checked
				for (DirectedEdge ed : w.getOutgoingEdges()) {
					DirectedNode z = ed.getDst();
					if (parent.get(z) == w && !touched.contains(z)) {
						q.add(new QueueElement((DirectedNode) z, height.get(z)));
						uncertain.add(z);
						touched.add(z);
					}
				}

				// find the new shortest path
				double dist = Double.MAX_VALUE;
				ArrayList<DirectedNode> min = new ArrayList<DirectedNode>();
				for (DirectedEdge edge : w.getIncomingEdges()) {
					DirectedNode z = edge.getSrc();
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
				if (dist == Double.MAX_VALUE
						|| dist >= (Double) (g.getNodeCount() - 1d)) {
					height.put(w, Double.MAX_VALUE);
					parent.remove(w);
					uncertain.remove(w);
					continue;
				}

				// connect to the highest uncertain node
				boolean found = false;
				for (DirectedNode mNode : min) {
					if ((!uncertain.contains(mNode))
							&& (height.get(mNode) + 1 == height.get(w) || height
									.get(mNode) == height.get(w))) {
						uncertain.remove(w);
						height.put(w, height.get(mNode) + 1d);
						parent.put(w, mNode);
						found = true;
						break;
					}
				}

				// else connect to another node
				if (!found) {
					q.add(new QueueElement<DirectedNode>(w, (height.get(min
							.get(0)) + 1d)));
					height.put(w, height.get(min.get(0)) + 1d);
					parent.put(w, min.get(0));
				}

			}

		}

		return true;
	}

	private boolean applyAfterNodeAddition(Update u) {
		return false;
	}

	private boolean applyAfterNodeRemoval(Update u) {
		return false;
	}

	private boolean applyAfterEdgeAddition(Update u) {
		DirectedEdgeWeighted e = (DirectedEdgeWeighted) ((EdgeAddition) u)
				.getEdge();

		DirectedGraphAlAl g = (DirectedGraphAlAl) this.g;

		for (DirectedNode s : g.getNodes()) {
			HashMap<DirectedNode, DirectedNode> parent = parents.get(s);
			HashMap<DirectedNode, Double> height = heights.get(s);

			DirectedNode src = e.getSrc();
			DirectedNode dst = e.getDst();
			if (height.get(src) + e.getWeight() >= height.get(dst)) {
				continue;
			}

			height.put(dst, height.get(src) + e.getWeight());
			parent.put(dst, src);
			PriorityQueue<QueueElement<DirectedNodeAlWeighted>> q = new PriorityQueue<>();
			q.add(new QueueElement((DirectedNodeAlWeighted) dst, height
					.get(dst)));
			while (!q.isEmpty()) {
				QueueElement<DirectedNodeAlWeighted> c = q.poll();
				DirectedNodeAlWeighted current = c.e;

				if (height.get(current) == Double.MAX_VALUE) {
					break;
				}

				for (DirectedEdge edge : current.getOutgoingEdges()) {
					DirectedEdgeWeighted d = (DirectedEdgeWeighted) edge;

					DirectedNode neighbor = d.getDst();

					double alt = height.get(current) + d.getWeight();

					if (alt < height.get(neighbor)) {
						height.put(neighbor, alt);
						parent.put(neighbor, current);
						QueueElement<DirectedNodeAlWeighted> temp = new QueueElement<DirectedNodeAlWeighted>(
								(DirectedNodeAlWeighted) neighbor,
								height.get(neighbor));
						if (q.contains(temp)) {
							q.remove(temp);
						}
						q.add(temp);
					}
				}
			}

		}
		return true;
	}
}
