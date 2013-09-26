package dna.metrics.apsp;

import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;

import dna.graph.directed.DirectedEdge;
import dna.graph.directed.DirectedGraph;
import dna.graph.directed.DirectedNode;
import dna.updates.Batch;
import dna.updates.EdgeAddition;
import dna.updates.EdgeRemoval;
import dna.updates.NodeAddition;
import dna.updates.NodeRemoval;
import dna.updates.Update;

@SuppressWarnings("rawtypes")
public class APSPWidthDdirectedDyn extends APSPWitdhDdirected {

	public APSPWidthDdirectedDyn() {
		super("APSP Directed Dyn", ApplicationType.AfterUpdate);
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
		DirectedGraph g = (DirectedGraph) this.g;
		for (DirectedNode n : g.getNodes()) {
			HashMap<DirectedNode, Integer> heightIN = this.heightsIn.get(n);
			HashMap<DirectedNode, DirectedNode> parentIN = this.parentsIn
					.get(n);
			HashMap<DirectedNode, Integer> heightOut = this.heightsIn.get(n);
			HashMap<DirectedNode, DirectedNode> parentOut = this.parentsIn
					.get(n);
			if (parentIN.get(e.getDst()) == e.getSrc()) {

				continue;
			}

			HashSet<DirectedNode> uncertain = new HashSet<DirectedNode>();
			PriorityQueue<DirectedNode> q = new PriorityQueue<DirectedNode>();
			q.add(e.getDst());
			if (parentOut.get(e.getSrc()) == e.getDst()) {
				uncertain.add(e.getDst());
				DirectedNode node = q.poll();
				if (heightOut.get(node) > 0) {

				}
				if (uncertain.contains(node)) {
					if (heightOut.get(node) == 0) {
						settle(node);
					} else {
						make_changed(node);
						for (DirectedEdge edge : node.getOutgoingEdges()) {
							if (parentOut.get(edge.getDst()) == node) {
								uncertain.add(e.getDst());
							}
						}
					}
				} else {
					settle(node);
				}

			}
		}
		return true;
	}

	private void make_changed(DirectedNode node) {
		// TODO Auto-generated method stub

	}

	private void settle(DirectedNode node) {
		// TODO Auto-generated method stub

	}

	private boolean applyAfterEdgeAddition(Update u) {
		DirectedEdge e = (DirectedEdge) ((EdgeAddition) u).getEdge();
		DirectedGraph g = (DirectedGraph) this.g;

		this.heightsIn.remove(e.getSrc());
		this.heightsOut.remove(e.getSrc());

		buildTrees(g, e.getSrc());
		return true;
	}

	private boolean applyAfterNodeRemoval(Update u) {
		DirectedNode n = (DirectedNode) ((NodeRemoval) u).getNode();
		return false;
	}

	private boolean applyAfterNodeAddition(Update u) {
		DirectedNode n = (DirectedNode) ((NodeAddition) u).getNode();
		return false;
	}

}
