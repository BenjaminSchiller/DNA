package dna.depr.metrics.motifs.directedMotifs;

import java.util.HashSet;

import dna.depr.metrics.motifs.directedMotifs.exceptions.InvalidDirectedMotifException;
import dna.graph.IElement;
import dna.graph.edges.DirectedEdge;
import dna.graph.nodes.DirectedNode;
import dna.metrics.motifs.DirectedMotifs;
import dna.metricsNew.IMetricNew;
import dna.updates.batch.Batch;
import dna.updates.update.EdgeAddition;
import dna.updates.update.EdgeRemoval;
import dna.updates.update.NodeRemoval;
import dna.updates.update.Update;

@Deprecated
public class DirectedMotifsListingUsingMotifU extends DirectedMotifs {

	public DirectedMotifsListingUsingMotifU() {
		super("DirectedMotifsListingUsingMotifU",
				ApplicationType.BeforeAndAfterUpdate, IMetricNew.MetricType.exact);
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
		if (u instanceof EdgeAddition) {
			DirectedEdge e = (DirectedEdge) ((EdgeAddition) u).getEdge();
			DirectedNode a = e.getSrc();
			DirectedNode b = e.getDst();
			if (!e.getSrc().hasEdge(e.invert())) {
				this.removeCombinations(a, b,
						this.getConnectedNodesIntersection(a, b));
			} else {
				this.removeCombinations(a, b, this.getConnectedNodesUnion(a, b));
			}
		} else if (u instanceof EdgeRemoval) {
			DirectedEdge e = (DirectedEdge) ((EdgeRemoval) u).getEdge();
			DirectedNode a = e.getSrc();
			DirectedNode b = e.getDst();
			this.removeCombinations(a, b, this.getConnectedNodesUnion(a, b));
		} else if (u instanceof NodeRemoval) {
			// TODO implement node removal
		}
		return true;
	}

	@Override
	public boolean applyAfterUpdate(Update u) {
		if (u instanceof EdgeAddition) {
			DirectedEdge e = (DirectedEdge) ((EdgeAddition) u).getEdge();
			DirectedNode a = e.getSrc();
			DirectedNode b = e.getDst();
			this.addCombinations(a, b, this.getConnectedNodesUnion(a, b));
		} else if (u instanceof EdgeRemoval) {
			DirectedEdge e = (DirectedEdge) ((EdgeRemoval) u).getEdge();
			DirectedNode a = e.getSrc();
			DirectedNode b = e.getDst();
			if (!e.getSrc().hasEdge(e.invert())) {
				this.addCombinations(a, b,
						this.getConnectedNodesIntersection(a, b));
			} else {
				this.addCombinations(a, b, this.getConnectedNodesUnion(a, b));
			}
		} else if (u instanceof NodeRemoval) {
			// TODO implement node removal
		}
		return true;
	}

	protected HashSet<DirectedNode> getConnectedNodesUnion(DirectedNode a,
			DirectedNode b) {
		HashSet<DirectedNode> nodes = new HashSet<DirectedNode>(a.getInDegree()
				+ a.getOutDegree() + b.getInDegree() + b.getOutDegree());
		for (IElement in : a.getIncomingEdges()) {
			DirectedNode n = ((DirectedEdge) in).getSrc();
			if (n.getIndex() != b.getIndex()) {
				nodes.add(n);
			}
		}
		for (IElement out : a.getOutgoingEdges()) {
			DirectedNode n = ((DirectedEdge) out).getDst();
			if (n.getIndex() != b.getIndex()) {
				nodes.add(n);
			}
		}
		for (IElement in : b.getIncomingEdges()) {
			DirectedNode n = ((DirectedEdge) in).getSrc();
			if (n.getIndex() != a.getIndex()) {
				nodes.add(n);
			}
		}
		for (IElement out : b.getOutgoingEdges()) {
			DirectedNode n = ((DirectedEdge) out).getDst();
			if (n.getIndex() != a.getIndex()) {
				nodes.add(n);
			}
		}
		return nodes;
	}

	protected void addCombinations(DirectedNode a, DirectedNode b,
			HashSet<DirectedNode> cs) {
		// System.out.println("ADDING for " + a.getIndex() + " " +
		// b.getIndex());
		// System.out.println("    " + cs);
		for (DirectedNode c : cs) {
			try {
				this.motifs.incr(DirectedMotifs.getIndex(DirectedMotif.getType(
						a, b, c)));
			} catch (InvalidDirectedMotifException e) {
				e.printStackTrace();
			}
		}
	}

	protected void removeCombinations(DirectedNode a, DirectedNode b,
			HashSet<DirectedNode> cs) {
		// System.out.println("REMOVING for " + a.getIndex() + " " +
		// b.getIndex());
		// System.out.println("    " + cs);
		for (DirectedNode c : cs) {
			try {
				this.motifs.decr(DirectedMotifs.getIndex(DirectedMotif.getType(
						a, b, c)));
			} catch (InvalidDirectedMotifException e) {
				e.printStackTrace();
			}
		}
	}

	protected HashSet<DirectedNode> getConnectedNodesIntersection(
			DirectedNode a, DirectedNode b) {
		HashSet<DirectedNode> nodesA = this.getConnectedNodes(a);
		HashSet<DirectedNode> nodesB = this.getConnectedNodes(b);
		HashSet<DirectedNode> nodes = new HashSet<DirectedNode>(nodesA.size()
				+ nodesB.size());
		for (DirectedNode n : nodesA) {
			if (nodesB.contains(n)) {
				nodes.add(n);
			}
		}
		return nodes;
	}

}
