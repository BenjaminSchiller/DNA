package dna.metrics.apsp.allPairShortestPathComplete;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;

import dna.graph.IElement;
import dna.graph.edges.DirectedEdge;
import dna.graph.nodes.DirectedNode;
import dna.metrics.apsp.QueueElement;
import dna.updates.batch.Batch;
import dna.updates.update.EdgeAddition;
import dna.updates.update.EdgeRemoval;
import dna.updates.update.NodeAddition;
import dna.updates.update.NodeRemoval;
import dna.updates.update.Update;

public class DirectedAllPairShortestPathCompleteU extends
		DirectedAllPairShortestPathComplete {

	public DirectedAllPairShortestPathCompleteU() {
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

			PriorityQueue<QueueElement<DirectedNode>> q = new PriorityQueue<QueueElement<DirectedNode>>();

			q.add(new QueueElement<DirectedNode>(dst, height.get(dst)
					.doubleValue()));

			uncertain.add(dst);
			parent.remove(dst);

			while (!q.isEmpty()) {
				QueueElement<DirectedNode> qE = q.poll();
				DirectedNode w = qE.e;

				int key = ((Double) qE.distance).intValue();

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
							DirectedNode z = edge.getDst();
							if (parent.get(z) == w) {
								parent.remove(z);
								uncertain.add(z);

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
					DirectedNode z = edge.getDst();
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

			if (!parent.containsKey(src)) {
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
			DirectedNode c = edge.getDst();
			this.check(b, c, parent, height);
		}
	}

	private boolean applyAfterNodeRemoval(Update u) {
		DirectedNode n = (DirectedNode) ((NodeRemoval) u).getNode();

		HashSet<DirectedEdge> edges = new HashSet<DirectedEdge>();

		g.addNode(n);
		for (IElement ie : n.getEdges()) {
			DirectedEdge e = (DirectedEdge) ie;
			edges.add(e);
			e.connectToNodes();
		}

		for (DirectedEdge de : edges) {
			de.disconnectFromNodes();
			applyAfterEdgeRemoval(new EdgeRemoval(de));
		}
		g.removeNode(n);
		this.heightsOut.remove(n);
		this.parentsOut.remove(n);
		for (IElement ie : this.g.getNodes()) {
			DirectedNode r = (DirectedNode) ie;
			this.heightsOut.get(r).remove(n);
			this.parentsOut.get(r).remove(n);
		}
		return true;
	}

	private boolean applyAfterNodeAddition(Update u) {
		DirectedNode n = (DirectedNode) ((NodeAddition) u).getNode();

		this.parentsOut.put(n, new HashMap<DirectedNode, DirectedNode>());
		this.heightsOut.put(n, new HashMap<DirectedNode, Integer>());

		for (IElement ie : this.g.getNodes()) {
			DirectedNode r = (DirectedNode) ie;

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
