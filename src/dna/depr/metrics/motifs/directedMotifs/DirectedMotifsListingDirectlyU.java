package dna.depr.metrics.motifs.directedMotifs;

import java.util.HashSet;

import dna.depr.metrics.motifs.directedMotifs.exceptions.DirectedMotifInvalidEdgeAdditionException;
import dna.depr.metrics.motifs.directedMotifs.exceptions.DirectedMotifInvalidEdgeRemovalException;
import dna.depr.metrics.motifs.directedMotifs.exceptions.DirectedMotifSplittingException;
import dna.depr.metrics.motifs.directedMotifs.exceptions.InvalidDirectedMotifException;
import dna.depr.metrics.motifsNew.DirectedMotifs;
import dna.graph.IElement;
import dna.graph.edges.DirectedEdge;
import dna.graph.nodes.DirectedNode;
import dna.metrics.IMetricNew;
import dna.updates.batch.Batch;
import dna.updates.update.EdgeAddition;
import dna.updates.update.EdgeRemoval;
import dna.updates.update.NodeRemoval;
import dna.updates.update.Update;

@Deprecated
public class DirectedMotifsListingDirectlyU extends DirectedMotifs {

	public DirectedMotifsListingDirectlyU() {
		super("DirectedMotifsListingDirectlyU",
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
				this.transformCombinations(a, b,
						this.getConnectedNodesIntersection(a, b), u);
			} else {
				this.transformCombinations(a, b,
						this.getConnectedNodesUnion(a, b), u);
			}
		} else if (u instanceof EdgeRemoval) {
			DirectedEdge e = (DirectedEdge) ((EdgeRemoval) u).getEdge();
			DirectedNode a = e.getSrc();
			DirectedNode b = e.getDst();
			if (!e.getSrc().hasEdge(e.invert())) {
				HashSet<DirectedNode> a_ = this.getConnectedNodes(a);
				HashSet<DirectedNode> b_ = this.getConnectedNodes(b);
				this.removeCombinations(a, b,
						this.getConnectedNodesExcluding(a, b_, b));
				this.removeCombinations(a, b,
						this.getConnectedNodesExcluding(b, a_, a));
				this.transformCombinations(a, b,
						this.getConnectedNodesIntersection(a, b), u);
			} else {
				this.transformCombinations(a, b,
						this.getConnectedNodesUnion(a, b), u);
			}
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
			if (!e.getSrc().hasEdge(e.invert())) {
				HashSet<DirectedNode> a_ = this.getConnectedNodes(a);
				HashSet<DirectedNode> b_ = this.getConnectedNodes(b);
				this.addCombinations(a, b,
						this.getConnectedNodesExcluding(a, b_, b));
				this.addCombinations(a, b,
						this.getConnectedNodesExcluding(b, a_, a));
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

	protected HashSet<DirectedNode> getConnectedNodesExcluding(
			DirectedNode node, HashSet<DirectedNode> excludeSet,
			DirectedNode excludeNode) {
		HashSet<DirectedNode> nodes = new HashSet<DirectedNode>(
				node.getInDegree() + node.getOutDegree());
		for (IElement in : node.getIncomingEdges()) {
			DirectedNode src = ((DirectedEdge) in).getSrc();
			if (!excludeSet.contains(src)
					&& excludeNode.getIndex() != src.getIndex()) {
				nodes.add(src);
			}
		}
		for (IElement out : node.getOutgoingEdges()) {
			DirectedNode dst = ((DirectedEdge) out).getDst();
			if (!excludeSet.contains(dst)
					&& excludeNode.getIndex() != dst.getIndex()) {
				nodes.add(dst);
			}
		}
		return nodes;
	}

	protected void transformCombinations(DirectedNode a, DirectedNode b,
			HashSet<DirectedNode> cs, Update u) {
		for (DirectedNode c : cs) {
			try {
				DirectedMotif m = DirectedMotif.getMotif(a, b, c);
				this.motifs.decr(DirectedMotifs.getIndex(m.getType()));
				if (u instanceof EdgeAddition) {
					m.addEdge((DirectedEdge) ((EdgeAddition) u).getEdge());
				} else {
					m.removeEdge((DirectedEdge) ((EdgeRemoval) u).getEdge());
				}
				this.motifs.incr(DirectedMotifs.getIndex(m.getType()));
			} catch (InvalidDirectedMotifException e1) {
				e1.printStackTrace();
			} catch (DirectedMotifInvalidEdgeAdditionException e1) {
				e1.printStackTrace();
			} catch (DirectedMotifSplittingException e) {
				e.printStackTrace();
			} catch (DirectedMotifInvalidEdgeRemovalException e) {
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
