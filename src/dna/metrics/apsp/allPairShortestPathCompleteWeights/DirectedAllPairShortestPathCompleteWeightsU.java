package dna.metrics.apsp.allPairShortestPathCompleteWeights;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;

import dna.graph.IElement;
import dna.graph.edges.DirectedDoubleWeightedEdge;
import dna.graph.edges.DirectedEdge;
import dna.graph.nodes.DirectedNode;
import dna.metrics.apsp.QueueElement;
import dna.updates.batch.Batch;
import dna.updates.update.EdgeAddition;
import dna.updates.update.EdgeRemoval;
import dna.updates.update.NodeAddition;
import dna.updates.update.NodeRemoval;
import dna.updates.update.Update;

public class DirectedAllPairShortestPathCompleteWeightsU extends
		DirectedAllPairShortestPathCompleteWeights {

	public DirectedAllPairShortestPathCompleteWeightsU() {
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
		DirectedEdge e = (DirectedEdge) ((EdgeRemoval) u).getEdge();
		DirectedNode src = e.getSrc();
		DirectedNode dst = e.getDst();

		// check all trees if the deleted edge is in the tree
		for (IElement ie : g.getNodes()) {
			DirectedNode r = (DirectedNode) ie;
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
				for (IElement iEdge : w.getIncomingEdges()) {
					DirectedDoubleWeightedEdge ed = (DirectedDoubleWeightedEdge) iEdge;
					DirectedNode z = ed.getSrc();
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
						for (IElement iEdge : w.getOutgoingEdges()) {
							DirectedDoubleWeightedEdge ed = (DirectedDoubleWeightedEdge) iEdge;
							DirectedNode z = ed.getSrc();
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
				for (IElement iEdge : w.getOutgoingEdges()) {
					DirectedDoubleWeightedEdge edge = (DirectedDoubleWeightedEdge) iEdge;
					DirectedNode z = edge.getSrc();
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

	// private boolean applyAfterEdgeRemoval1(Update u) {
	// DirectedEdgeWeighted e = (DirectedEdgeWeighted) ((EdgeRemoval) u)
	// .getEdge();
	//
	// DirectedGraph g = (DirectedGraph) this.g;
	//
	// for (DirectedNode s : g.getNodes()) {
	// HashMap<DirectedNode, DirectedNode> parent = parents.get(s);
	// HashMap<DirectedNode, Double> height = heights.get(s);
	//
	// DirectedNode src = e.getSrc();
	// DirectedNode dst = e.getDst();
	// if (parent.get(dst) != src) {
	// continue;
	// }
	// PriorityQueue<QueueElement<DirectedNode>> q = new PriorityQueue<>();
	// HashSet<DirectedNode> uncertain = new HashSet<>();
	// HashSet<DirectedNode> touched = new HashSet<>();
	//
	// q.add(new QueueElement(dst, height.get(dst)));
	// uncertain.add(dst);
	// touched.add(dst);
	// while (!q.isEmpty()) {
	//
	// QueueElement<DirectedNode> c = q.poll();
	// DirectedNode w = c.e;
	//
	// // all childs of node w need to be checked
	// for (DirectedEdge ed : w.getOutgoingEdges()) {
	// DirectedNode z = ed.getDst();
	// if (parent.get(z) == w && !touched.contains(z)) {
	// q.add(new QueueElement((DirectedNode) z, height.get(z)));
	// uncertain.add(z);
	// touched.add(z);
	// }
	// }
	//
	// // find the new shortest path
	// double dist = Double.MAX_VALUE;
	// ArrayList<DirectedNode> min = new ArrayList<DirectedNode>();
	// for (DirectedEdge edge : w.getIncomingEdges()) {
	// DirectedNode z = edge.getSrc();
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
	// if (dist == Double.MAX_VALUE
	// || dist >= (Double) (g.getNodeCount() - 1d)) {
	// height.put(w, Double.MAX_VALUE);
	// parent.remove(w);
	// uncertain.remove(w);
	// continue;
	// }
	//
	// // connect to the highest uncertain node
	// boolean found = false;
	// for (DirectedNode mNode : min) {
	// if ((!uncertain.contains(mNode))
	// && (height.get(mNode) + 1 == height.get(w) || height
	// .get(mNode) == height.get(w))) {
	// uncertain.remove(w);
	// height.put(w, height.get(mNode) + 1d);
	// parent.put(w, mNode);
	// found = true;
	// break;
	// }
	// }
	//
	// // else connect to another node
	// if (!found) {
	// q.add(new QueueElement<DirectedNode>(w, (height.get(min
	// .get(0)) + 1d)));
	// height.put(w, height.get(min.get(0)) + 1d);
	// parent.put(w, min.get(0));
	// }
	//
	// }
	//
	// }
	//
	// return true;
	// }

	private boolean applyAfterNodeAddition(Update u) {
		DirectedNode n = (DirectedNode) ((NodeAddition) u).getNode();

		this.parents.put(n, new HashMap<DirectedNode, DirectedNode>());
		this.heights.put(n, new HashMap<DirectedNode, Double>());

		for (IElement ie : this.g.getNodes()) {
			DirectedNode r = (DirectedNode) ie;

			if (r != n) {
				this.heights.get(r).put(n, Double.MAX_VALUE);
				this.heights.get(n).put(r, Double.MAX_VALUE);
			} else {
				this.heights.get(r).put(n, 0d);
			}

		}
		return true;
	}

	private boolean applyAfterNodeRemoval(Update u) {
		DirectedNode n = (DirectedNode) ((NodeRemoval) u).getNode();
		this.heights.remove(n);
		this.parents.remove(n);

		for (IElement ie : n.getEdges()) {
			applyAfterEdgeRemoval(new EdgeRemoval(
					(DirectedDoubleWeightedEdge) ie));
		}

		for (IElement ie : this.g.getNodes()) {
			DirectedNode r = (DirectedNode) ie;
			this.heights.get(r).remove(n);
			this.parents.get(r).remove(n);
		}
		return true;
	}

	private boolean applyAfterEdgeAddition(Update u) {
		DirectedDoubleWeightedEdge e = (DirectedDoubleWeightedEdge) ((EdgeAddition) u)
				.getEdge();

		for (IElement ie : g.getNodes()) {
			DirectedNode s = (DirectedNode) ie;
			HashMap<DirectedNode, DirectedNode> parent = parents.get(s);
			HashMap<DirectedNode, Double> height = heights.get(s);

			DirectedNode src = e.getSrc();
			DirectedNode dst = e.getDst();
			if (height.get(src) + e.getWeight() >= height.get(dst)) {
				continue;
			}

			height.put(dst, height.get(src) + e.getWeight());
			parent.put(dst, src);
			PriorityQueue<QueueElement<DirectedNode>> q = new PriorityQueue<QueueElement<DirectedNode>>();
			q.add(new QueueElement(dst, height.get(dst)));
			while (!q.isEmpty()) {
				QueueElement<DirectedNode> c = q.poll();
				DirectedNode current = c.e;

				if (height.get(current) == Double.MAX_VALUE) {
					break;
				}

				for (IElement iEdge : current.getOutgoingEdges()) {
					DirectedDoubleWeightedEdge d = (DirectedDoubleWeightedEdge) iEdge;
					DirectedNode neighbor = d.getDst();

					double alt = height.get(current) + d.getWeight();

					if (alt < height.get(neighbor)) {
						height.put(neighbor, alt);
						parent.put(neighbor, current);
						QueueElement<DirectedNode> temp = new QueueElement<DirectedNode>(
								neighbor, height.get(neighbor));
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
