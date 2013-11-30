package dna.metrics.apsp.allPairShortestPath;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;

import dna.graph.IElement;
import dna.graph.edges.DirectedEdge;
import dna.graph.edges.Edge;
import dna.graph.edges.UndirectedEdge;
import dna.graph.nodes.DirectedNode;
import dna.graph.nodes.Node;
import dna.graph.nodes.UndirectedNode;
import dna.metrics.apsp.QueueElement;
import dna.updates.batch.Batch;
import dna.updates.update.EdgeAddition;
import dna.updates.update.EdgeRemoval;
import dna.updates.update.NodeAddition;
import dna.updates.update.NodeRemoval;
import dna.updates.update.Update;

public class AllPairShortestPathU extends AllPairShortestPath {

	public AllPairShortestPathU() {
		super("AllPairShortestPathU", ApplicationType.AfterUpdate);

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
		}
		return false;
	}

	private boolean applyAfterDirectedEdgeRemoval(Update u) {
		DirectedEdge e = (DirectedEdge) ((EdgeRemoval) u).getEdge();
		DirectedNode src = e.getSrc();
		DirectedNode dst = e.getDst();

		// check all trees if the deleted edge is in the tree
		for (IElement ie : g.getNodes()) {
			DirectedNode r = (DirectedNode) ie;
			HashMap<Node, Node> parent = this.parents.get(r);
			HashMap<Node, Integer> height = this.heights.get(r);

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

				int key = qE.distance;

				// find the new shortest path
				int dist = Integer.MAX_VALUE;

				ArrayList<DirectedNode> minSettled = new ArrayList<DirectedNode>();
				ArrayList<DirectedNode> min = new ArrayList<DirectedNode>();
				for (IElement iEgde : w.getIncomingEdges()) {
					DirectedEdge edge = (DirectedEdge) iEgde;
					DirectedNode z = edge.getSrc();
					if (changed.contains(z)
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
					dists.decr(height.get(w));
					sum -= height.get(w);
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
				sum = sum + dist - height.get(w);
				height.put(w, dist);
				for (IElement iEgde : w.getOutgoingEdges()) {
					DirectedEdge edge = (DirectedEdge) iEgde;
					DirectedNode z = edge.getDst();
					if (height.get(z) > dist + 1) {
						q.remove(new QueueElement<DirectedNode>(z, dist + 1));
						q.add(new QueueElement<DirectedNode>(z, dist + 1));
					}
				}
			}
		}
		return true;
	}

	private boolean applyAfterUndirectedEdgeRemoval(Update u) {
		UndirectedEdge e = (UndirectedEdge) ((EdgeRemoval) u).getEdge();
		Node n1 = e.getNode1();
		Node n2 = e.getNode2();

		// check all trees if the deleted edge is in the tree
		for (IElement ie : g.getNodes()) {
			Node r = (Node) ie;
			HashMap<Node, Node> parent = this.parents.get(r);
			HashMap<Node, Integer> height = this.heights.get(r);

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
					dists.decr(height.get(w));
					height.put(w, Integer.MAX_VALUE);
					sum -= height.get(w);
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
				sum = sum + dist - height.get(w);
				height.put(w, dist);
				for (IElement iEdge : w.getEdges()) {
					UndirectedEdge ed = (UndirectedEdge) iEdge;
					Node z = ed.getDifferingNode(w);
					if (height.get(z) > dist + 1) {
						q.remove(new QueueElement<Node>(z, dist + 1));
						q.add(new QueueElement<Node>(z, dist + 1));
					}
				}
			}
		}
		return true;
	}

	private boolean applyAfterDirectedEdgeAddition(Update u) {

		DirectedEdge e = (DirectedEdge) ((EdgeAddition) u).getEdge();
		DirectedNode src = e.getSrc();
		DirectedNode dst = e.getDst();

		for (IElement ie : g.getNodes()) {
			DirectedNode s = (DirectedNode) ie;
			HashMap<Node, Node> parent = this.parents.get(s);
			HashMap<Node, Integer> height = this.heights.get(s);

			if (src.equals(s)) {
				this.check(src, dst, parent, height);
				continue;
			}

			if (!parent.containsKey(src)) {
				continue;
			}
			this.check(src, dst, parent, height);

		}
		return true;
	}

	private boolean applyAfterUndirectedEdgeAddition(Update u) {

		UndirectedEdge e = (UndirectedEdge) ((EdgeAddition) u).getEdge();
		UndirectedNode n1 = e.getNode1();
		UndirectedNode n2 = e.getNode2();

		for (IElement ie : g.getNodes()) {
			Node s = (Node) ie;
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
		return true;
	}

	protected void check(UndirectedNode a, UndirectedNode b,
			HashMap<Node, Node> parent, HashMap<Node, Integer> height) {
		int h_a = height.get(a);
		int h_b = height.get(b);
		if (h_a == Integer.MAX_VALUE || h_a + 1 >= h_b) {
			return;
		}
		parent.put(b, a);
		h_b = h_a + 1;
		if (height.get(b) != Integer.MAX_VALUE)
			dists.decr(height.get(b));
		dists.incr(h_b);
		sum = sum + h_b - height.get(b);
		height.put(b, h_b);
		for (IElement iEdge : b.getEdges()) {
			UndirectedEdge e = (UndirectedEdge) iEdge;
			UndirectedNode c = e.getDifferingNode(b);
			this.check(b, c, parent, height);
		}
	}

	protected void check(DirectedNode a, DirectedNode b,
			HashMap<Node, Node> parent, HashMap<Node, Integer> height) {
		int h_a = height.get(a);
		int h_b = height.get(b);
		if (h_a == Integer.MAX_VALUE || h_a + 1 >= h_b) {
			return;
		}
		parent.put(b, a);
		h_b = h_a + 1;
		if (height.get(b) != Integer.MAX_VALUE)
			dists.decr(height.get(b));
		dists.incr(h_b);
		sum = sum + h_b - height.get(b);
		height.put(b, h_b);
		for (IElement iEdge : b.getOutgoingEdges()) {
			DirectedEdge edge = (DirectedEdge) iEdge;
			DirectedNode c = edge.getDst();
			this.check(b, c, parent, height);
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
}
