package dna.metrics.apsp.allPairShortestPathWeights;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;

import dna.graph.IElement;
import dna.graph.edges.DirectedEdge;
import dna.graph.edges.DirectedIntWeightedEdge;
import dna.graph.edges.Edge;
import dna.graph.edges.UndirectedEdge;
import dna.graph.edges.UndirectedIntWeightedEdge;
import dna.graph.nodes.DirectedNode;
import dna.graph.nodes.Node;
import dna.graph.nodes.UndirectedNode;
import dna.metrics.apsp.QueueElement;
import dna.updates.batch.Batch;
import dna.updates.update.EdgeAddition;
import dna.updates.update.EdgeRemoval;
import dna.updates.update.EdgeWeight;
import dna.updates.update.NodeAddition;
import dna.updates.update.NodeRemoval;
import dna.updates.update.Update;

/**
 * 
 * @author Jan
 * 
 *         APSP for positive int weights
 */
public class AllPairShortestPathWeightsU extends AllPairShortestPathWeights {

	public AllPairShortestPathWeightsU() {
		super("AllPairShortestPathWeightsU", ApplicationType.AfterUpdate);
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
			if (DirectedNode.class.isAssignableFrom(this.g
					.getGraphDatastructures().getNodeType())) {
				return applyAfterDirectedEdgeAddition(u);
			} else {
				return applyAfterUndirectedEdgeAddition(u);
			}
		} else if (u instanceof EdgeRemoval) {
			if (DirectedNode.class.isAssignableFrom(this.g
					.getGraphDatastructures().getNodeType())) {
				return applyAfterDirectedEdgeRemoval(u);
			} else {
				return applyAfterUndirectedEdgeRemoval(u);
			}
		} else if (u instanceof EdgeWeight) {
			if (DirectedNode.class.isAssignableFrom(this.g
					.getGraphDatastructures().getNodeType())) {
				return applyAfterDirectedEdgeWeight(u);
			} else {
				return applyAfterUndirectedEdgeWeight(u);
			}
		}
		return false;
	}

	private boolean applyAfterUndirectedEdgeWeight(Update u) {
		UndirectedIntWeightedEdge e = (UndirectedIntWeightedEdge) ((EdgeWeight) u)
				.getEdge();
		UndirectedNode n1 = e.getNode1();
		UndirectedNode n2 = e.getNode2();

		for (IElement ie : g.getNodes()) {
			UndirectedNode s = (UndirectedNode) ie;
			HashMap<Node, Node> parent = parents.get(s);
			HashMap<Node, Integer> height = heights.get(s);

			UndirectedNode src;
			UndirectedNode dst;

			if (height.get(n1) > height.get(n2)) {
				src = n2;
				dst = n1;
			} else {
				src = n1;
				dst = n2;
			}

			if (height.get(src) + e.getWeight() >= height.get(dst)
					|| height.get(src) + e.getWeight() < 0) {
				if (parent.get(dst).equals(src)) {
					undirectedDelete(s, e);
				}
			}
			undirectedAdd(e, s);
			dists.truncate();
		}
		return true;
	}

	private boolean applyAfterDirectedEdgeWeight(Update u) {
		DirectedIntWeightedEdge e = (DirectedIntWeightedEdge) ((EdgeWeight) u)
				.getEdge();

		for (IElement ie : g.getNodes()) {
			DirectedNode s = (DirectedNode) ie;
			HashMap<Node, Node> parent = parents.get(s);
			HashMap<Node, Integer> height = heights.get(s);

			DirectedNode src = e.getSrc();
			DirectedNode dst = e.getDst();
			if (height.get(src) + e.getWeight() >= height.get(dst)
					|| height.get(src) + e.getWeight() < 0) {
				if (parent.containsKey(dst) && parent.get(dst).equals(src)) {
					directedDelete(s, e);
				}
			}
			directedAdd(e, s);
			dists.truncate();
		}
		return true;
	}

	private boolean applyAfterDirectedEdgeRemoval(Update u) {
		DirectedEdge e = (DirectedEdge) ((EdgeRemoval) u).getEdge();

		// check all trees if the deleted edge is in the tree
		for (IElement ie : g.getNodes()) {
			DirectedNode r = (DirectedNode) ie;
			directedDelete(r, e);
			dists.truncate();

		}
		return true;
	}

	private void directedDelete(DirectedNode r, DirectedEdge e) {
		HashMap<Node, Node> parent = this.parents.get(r);
		HashMap<Node, Integer> height = this.heights.get(r);
		DirectedNode src = e.getSrc();
		DirectedNode dst = e.getDst();
		// if the source or dst or edge is not in tree do nothing
		if (height.get(src) == Integer.MAX_VALUE
				|| height.get(dst) == Integer.MAX_VALUE || dst.equals(r)
				|| !parent.get(dst).equals(src)) {
			return;
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

			int key = ((Integer) qE.distance);

			// find the new shortest path
			int dist = Integer.MAX_VALUE;

			ArrayList<DirectedNode> minSettled = new ArrayList<DirectedNode>();
			ArrayList<DirectedNode> min = new ArrayList<DirectedNode>();
			for (IElement iEgde : w.getIncomingEdges()) {
				DirectedIntWeightedEdge edge = (DirectedIntWeightedEdge) iEgde;
				DirectedNode z = edge.getSrc();

				if (changed.contains(z) || height.get(z) == Integer.MAX_VALUE) {
					continue;
				}
				if (height.get(z) + edge.getWeight() < dist) {
					min.clear();
					minSettled.clear();
					min.add(z);
					if (!uncertain.contains(z))
						minSettled.add(z);
					dist = height.get(z) + edge.getWeight();
					continue;
				}
				if (height.get(z) + edge.getWeight() == dist) {
					min.add(z);
					if (!uncertain.contains(z))
						minSettled.add(z);
					continue;
				}
			}

			boolean noPossibleNeighbour = (key >= 10000d && dist > 10000d)
					|| (min.isEmpty() && (!uncertain.contains(w) || (key == dist)));

			// no neighbour found
			if (noPossibleNeighbour) {
				dists.decr(height.get(w));
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
					if (height.get(w) != Integer.MAX_VALUE)
						dists.decr(height.get(w));
					dists.incr(dist);
					height.put(w, dist);
					for (IElement iEgde : w.getOutgoingEdges()) {
						DirectedIntWeightedEdge edge = (DirectedIntWeightedEdge) iEgde;
						DirectedNode z = edge.getDst();

						if (height.get(z) > dist + edge.getWeight()) {
							q.remove(new QueueElement<DirectedNode>(z, dist
									+ edge.getWeight()));
							q.add(new QueueElement<DirectedNode>(z, dist
									+ edge.getWeight()));

						}
					}
				} else {
					changed.add(w);

					q.add(new QueueElement<DirectedNode>(w, dist));
					uncertain.remove(w);
					for (IElement iEgde : w.getOutgoingEdges()) {
						DirectedEdge edge = (DirectedEdge) iEgde;
						DirectedNode z = edge.getDst();
						if (parent.get(z) == w) {
							parent.remove(z);
							uncertain.add(z);
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
			if (height.get(w) != Integer.MAX_VALUE)
				dists.decr(height.get(w));
			dists.incr(dist);
			height.put(w, dist);
			for (IElement iEgde : w.getOutgoingEdges()) {
				DirectedIntWeightedEdge edge = (DirectedIntWeightedEdge) iEgde;
				DirectedNode z = edge.getDst();

				if (height.get(z) > dist + edge.getWeight()) {
					q.remove(new QueueElement<DirectedNode>(z, dist
							+ edge.getWeight()));
					q.add(new QueueElement<DirectedNode>(z, dist
							+ edge.getWeight()));

				}
			}
		}
	}

	private boolean applyAfterUndirectedEdgeRemoval(Update u) {
		UndirectedEdge e = (UndirectedEdge) ((EdgeRemoval) u).getEdge();
		// check all trees if the deleted edge is in the tree
		for (IElement ie : g.getNodes()) {
			Node r = (Node) ie;
			undirectedDelete(r, e);
			dists.truncate();
		}
		return true;
	}

	private void undirectedDelete(Node r, UndirectedEdge e) {
		HashMap<Node, Node> parent = this.parents.get(r);
		HashMap<Node, Integer> height = this.heights.get(r);
		Node n1 = e.getNode1();
		Node n2 = e.getNode2();
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
				|| height.get(dst) == Integer.MAX_VALUE || height.get(dst) == 0
				|| parent.get(dst) != src) {
			return;
		}

		// Queues and data structure for tree change
		HashSet<Node> uncertain = new HashSet<Node>();
		HashSet<Node> changed = new HashSet<Node>();

		PriorityQueue<QueueElement<Node>> q = new PriorityQueue<QueueElement<Node>>();

		q.add(new QueueElement<Node>(dst, height.get(dst)));

		uncertain.add(dst);
		parent.remove(dst);

		while (!q.isEmpty()) {
			QueueElement<Node> qE = q.poll();
			Node w = qE.e;

			int key = qE.distance;

			// find the new shortest path
			int dist = Integer.MAX_VALUE;

			ArrayList<Node> minSettled = new ArrayList<Node>();
			ArrayList<Node> min = new ArrayList<Node>();
			for (IElement iEdge : w.getEdges()) {
				UndirectedIntWeightedEdge edge = (UndirectedIntWeightedEdge) iEdge;
				Node z = edge.getDifferingNode(w);
				if (parent.get(w) == z || changed.contains(z)
						|| height.get(z) == Integer.MAX_VALUE) {
					continue;
				}
				if (height.get(z) + edge.getWeight() < dist) {
					min.clear();
					minSettled.clear();
					min.add(z);
					if (!uncertain.contains(z))
						minSettled.add(z);
					dist = height.get(z) + 1;
					continue;
				}
				if (height.get(z) + edge.getWeight() == dist) {
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
				dists.decr(height.get(w));
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
					if (height.get(w) != Integer.MAX_VALUE)
						dists.decr(height.get(w));
					dists.incr(dist);
					height.put(w, dist);
					for (IElement iEdge : w.getEdges()) {
						UndirectedIntWeightedEdge ed = (UndirectedIntWeightedEdge) iEdge;
						Node z = ed.getDifferingNode(w);
						if (height.get(z) > dist + 1) {
							q.remove(new QueueElement<Node>(z, dist
									+ ed.getWeight()));
							q.add(new QueueElement<Node>(z, dist
									+ ed.getWeight()));
						}
					}
				} else {
					changed.add(w);
					q.add(new QueueElement<Node>(w, dist));
					uncertain.remove(w);
					for (IElement iEdge : w.getEdges()) {
						UndirectedEdge ed = (UndirectedEdge) iEdge;
						Node z = ed.getDifferingNode(w);
						if (parent.get(z) == w) {
							parent.remove(z);
							uncertain.add(z);

							q.add(new QueueElement<Node>(z, height.get(z)));
						}
					}
				}
				continue;
			}
			if (dist > key) {
				q.add(new QueueElement<Node>(w, dist));
				continue;
			}
			if (minSettled.isEmpty()) {
				parent.put(w, min.get(0));
			} else {
				parent.put(w, minSettled.get(0));
			}
			changed.remove(w);
			if (height.get(w) != Integer.MAX_VALUE)
				dists.decr(height.get(w));
			dists.incr(dist);
			height.put(w, dist);
			for (IElement iEdge : w.getEdges()) {
				UndirectedIntWeightedEdge ed = (UndirectedIntWeightedEdge) iEdge;
				Node z = ed.getDifferingNode(w);
				if (height.get(z) > dist + 1) {
					q.remove(new QueueElement<Node>(z, dist + ed.getWeight()));
					q.add(new QueueElement<Node>(z, dist + ed.getWeight()));
				}
			}
		}
	}

	private boolean applyAfterNodeRemoval(Update u) {
		Node n = (Node) ((NodeRemoval) u).getNode();

		HashSet<Edge> edges = new HashSet<Edge>();

		g.addNode(n);
		for (IElement ie : n.getEdges()) {
			Edge e = (Edge) ie;
			edges.add(e);
			e.connectToNodes();
		}

		for (Edge de : edges) {
			de.disconnectFromNodes();
			applyAfterUpdate(new EdgeRemoval(de));
		}
		g.removeNode(n);
		this.heights.remove(n);
		this.parents.remove(n);
		for (IElement ie : this.g.getNodes()) {
			Node r = (Node) ie;
			this.heights.get(r).remove(n);
			this.parents.get(r).remove(n);
		}
		dists.truncate();
		return true;
	}

	private boolean applyAfterNodeAddition(Update u) {
		Node n = (Node) ((NodeAddition) u).getNode();

		this.parents.put(n, new HashMap<Node, Node>());
		this.heights.put(n, new HashMap<Node, Integer>());

		for (IElement ie : this.g.getNodes()) {
			Node r = (Node) ie;

			if (r != n) {
				this.heights.get(r).put(n, Integer.MAX_VALUE);
				this.heights.get(n).put(r, Integer.MAX_VALUE);
			} else {
				this.heights.get(r).put(n, 0);
			}

		}

		return true;
	}

	private boolean applyAfterUndirectedEdgeAddition(Update u) {
		UndirectedIntWeightedEdge e = (UndirectedIntWeightedEdge) ((EdgeAddition) u)
				.getEdge();

		for (IElement ie : g.getNodes()) {
			UndirectedNode s = (UndirectedNode) ie;
			undirectedAdd(e, s);
			dists.truncate();

		}
		return true;
	}

	private boolean undirectedAdd(UndirectedIntWeightedEdge e, UndirectedNode s) {
		HashMap<Node, Node> parent = parents.get(s);
		HashMap<Node, Integer> height = heights.get(s);

		UndirectedNode n1 = e.getNode1();
		UndirectedNode n2 = e.getNode2();

		if (height.get(n1) > height.get(n2)) {
			n1 = e.getNode2();
			n2 = e.getNode1();
		}

		if (height.get(n1) + e.getWeight() >= height.get(n2)
				|| height.get(n1) + e.getWeight() < 0) {
			return true;
		}
		if (height.get(n2) != Integer.MAX_VALUE)
			dists.decr(height.get(n2));
		dists.incr(height.get(n1) + e.getWeight());
		height.put(n2, height.get(n1) + e.getWeight());
		parent.put(n2, n1);
		PriorityQueue<UndirectedNode> q = new PriorityQueue<>();
		q.add((UndirectedNode) n2);
		while (!q.isEmpty()) {
			UndirectedNode current = q.poll();

			if (height.get(current) == Integer.MAX_VALUE) {
				break;
			}

			for (IElement edge : current.getEdges()) {
				UndirectedIntWeightedEdge d = (UndirectedIntWeightedEdge) edge;

				UndirectedNode neighbor = d.getDifferingNode(current);

				int alt = height.get(current) + d.getWeight();

				if (alt < height.get(neighbor)) {
					if (height.get(neighbor) != Integer.MAX_VALUE)
						dists.decr(height.get(neighbor));
					dists.incr(alt);
					height.put(neighbor, alt);
					parent.put(neighbor, current);
					if (q.contains(neighbor)) {
						q.remove(neighbor);
					}
					q.add(neighbor);
				}
			}
		}
		return true;
	}

	private boolean applyAfterDirectedEdgeAddition(Update u) {
		DirectedIntWeightedEdge e = (DirectedIntWeightedEdge) ((EdgeAddition) u)
				.getEdge();

		for (IElement ie : g.getNodes()) {
			DirectedNode s = (DirectedNode) ie;
			directedAdd(e, s);
			dists.truncate();
		}
		return true;
	}

	private boolean directedAdd(DirectedIntWeightedEdge e, DirectedNode s) {
		HashMap<Node, Node> parent = parents.get(s);
		HashMap<Node, Integer> height = heights.get(s);

		DirectedNode src = e.getSrc();
		DirectedNode dst = e.getDst();
		if (height.get(src) + e.getWeight() >= height.get(dst)
				|| height.get(src) + e.getWeight() < 0) {
			return true;
		}
		if (height.get(dst) != Integer.MAX_VALUE)
			dists.decr(height.get(dst));
		height.put(dst, height.get(src) + e.getWeight());
		dists.incr(height.get(src) + e.getWeight());
		parent.put(dst, src);
		PriorityQueue<QueueElement<DirectedNode>> q = new PriorityQueue<QueueElement<DirectedNode>>();
		QueueElement<DirectedNode> queueElement = new QueueElement<DirectedNode>(
				dst, height.get(dst));
		q.add(queueElement);
		while (!q.isEmpty()) {
			QueueElement<DirectedNode> c = q.poll();
			DirectedNode current = c.e;

			if (height.get(current) == Integer.MAX_VALUE) {
				break;
			}

			for (IElement iEdge : current.getOutgoingEdges()) {
				DirectedIntWeightedEdge d = (DirectedIntWeightedEdge) iEdge;
				DirectedNode neighbor = d.getDst();

				int alt = height.get(current) + d.getWeight();

				if (alt < height.get(neighbor)) {
					if (height.get(neighbor) != Integer.MAX_VALUE)
						dists.decr(height.get(neighbor));
					dists.incr(alt);
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
		return true;
	}

}
