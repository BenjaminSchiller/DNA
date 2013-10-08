package dna.metrics.apsp.allPairShortestPathCompleteWeights;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;

import dna.graph.IElement;
import dna.graph.edges.UndirectedDoubleWeightedEdge;
import dna.graph.edges.UndirectedEdge;
import dna.graph.nodes.UndirectedNode;
import dna.metrics.apsp.QueueElement;
import dna.updates.batch.Batch;
import dna.updates.update.EdgeAddition;
import dna.updates.update.EdgeRemoval;
import dna.updates.update.NodeAddition;
import dna.updates.update.NodeRemoval;
import dna.updates.update.Update;

public class APSPCompleteUndirectedWithWeightsDyn extends
		APSPCompleteUndirectedWithWeights {

	public APSPCompleteUndirectedWithWeightsDyn() {
		super("APSP Undirected wiht Weights Dyn", ApplicationType.AfterUpdate);
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
		UndirectedNode n1 = e.getNode1();
		UndirectedNode n2 = e.getNode2();

		// check all trees if the deleted edge is in the tree
		for (IElement ie : g.getNodes()) {
			UndirectedNode r = (UndirectedNode) ie;
			HashMap<UndirectedNode, UndirectedNode> parent = this.parents
					.get(r);
			HashMap<UndirectedNode, Double> height = this.heights.get(r);

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

			PriorityQueue<QueueElement<UndirectedNode>> q = new PriorityQueue<QueueElement<UndirectedNode>>();

			q.add(new QueueElement<UndirectedNode>(dst, height.get(dst)));

			uncertain.add(dst);
			parent.remove(dst);

			while (!q.isEmpty()) {
				QueueElement<UndirectedNode> qE = q.poll();
				UndirectedNode w = qE.e;
				// if (r.getIndex() == 842)
				// System.out.println("hey");
				// ;

				double key = qE.distance;

				// find the new shortest path
				double dist = Double.MAX_VALUE;

				ArrayList<UndirectedNode> minSettled = new ArrayList<UndirectedNode>();
				ArrayList<UndirectedNode> min = new ArrayList<UndirectedNode>();
				for (IElement iEdge : w.getEdges()) {
					UndirectedEdge edge = (UndirectedEdge) iEdge;
					UndirectedNode z = edge.getDifferingNode(w);
					UndirectedDoubleWeightedEdge ed = (UndirectedDoubleWeightedEdge) edge;
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
						q.add(new QueueElement<UndirectedNode>(w, dist));
						uncertain.remove(w);
						for (IElement iEdge : w.getEdges()) {
							UndirectedEdge edge = (UndirectedEdge) iEdge;
							UndirectedNode z = edge.getDifferingNode(w);
							if (parent.get(z) == w) {
								parent.remove(z);
								uncertain.add(z);
								if (key > height.get(z))
									System.out.println("fuck");
								q.add(new QueueElement<UndirectedNode>(z,
										height.get(z)));
							}
						}
					}
					continue;
				}
				if (dist > key) {
					q.add(new QueueElement<UndirectedNode>(w, dist));
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
					UndirectedDoubleWeightedEdge edge = (UndirectedDoubleWeightedEdge) iEdge;
					UndirectedNode z = edge.getDifferingNode(w);
					if (height.get(z) > dist + 1) {
						q.remove(new QueueElement<UndirectedNode>(z, dist
								+ edge.getWeight()));
						q.add(new QueueElement<UndirectedNode>(z, dist
								+ edge.getWeight()));
					}
				}
			}
		}
		return true;
	}

	private boolean applyAfterNodeAddition(Update u) {
		UndirectedNode n = (UndirectedNode) ((NodeAddition) u).getNode();

		this.parents.put(n, new HashMap<UndirectedNode, UndirectedNode>());
		this.heights.put(n, new HashMap<UndirectedNode, Double>());

		for (IElement ie : this.g.getNodes()) {
			UndirectedNode r = (UndirectedNode) ie;

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
		UndirectedNode n = (UndirectedNode) ((NodeRemoval) u).getNode();
		this.heights.remove(n);
		this.parents.remove(n);

		for (IElement ie : n.getEdges()) {
			applyAfterEdgeRemoval(new EdgeRemoval(
					(UndirectedDoubleWeightedEdge) ie));
		}

		for (IElement ie : this.g.getNodes()) {
			UndirectedNode r = (UndirectedNode) ie;
			this.heights.get(r).remove(n);
			this.parents.get(r).remove(n);
		}
		return true;
	}

	private boolean applyAfterEdgeAddition(Update u) {
		UndirectedDoubleWeightedEdge e = (UndirectedDoubleWeightedEdge) ((EdgeAddition) u)
				.getEdge();

		for (IElement ie : g.getNodes()) {
			UndirectedNode s = (UndirectedNode) ie;
			HashMap<UndirectedNode, UndirectedNode> parent = parents.get(s);
			HashMap<UndirectedNode, Double> height = heights.get(s);

			UndirectedNode n1 = e.getNode1();
			UndirectedNode n2 = e.getNode2();

			if (height.get(n1) > height.get(n2)) {
				n1 = e.getNode2();
				n2 = e.getNode1();
			}

			if (height.get(n1) + e.getWeight() >= height.get(n2)) {
				continue;
			}

			height.put(n2, height.get(n1) + e.getWeight());
			parent.put(n2, n1);
			PriorityQueue<UndirectedNode> q = new PriorityQueue<>();
			q.add((UndirectedNode) n2);
			while (!q.isEmpty()) {
				UndirectedNode current = q.poll();

				if (height.get(current) == Double.MAX_VALUE) {
					break;
				}

				for (IElement edge : current.getEdges()) {
					UndirectedDoubleWeightedEdge d = (UndirectedDoubleWeightedEdge) edge;

					UndirectedNode neighbor = d.getDifferingNode(current);

					double alt = height.get(current) + d.getWeight();

					if (alt < height.get(neighbor)) {
						height.put(neighbor, alt);
						parent.put(neighbor, current);
						if (q.contains(neighbor)) {
							q.remove(neighbor);
						}
						q.add(neighbor);
					}
				}
			}

		}
		return true;
	}

}
