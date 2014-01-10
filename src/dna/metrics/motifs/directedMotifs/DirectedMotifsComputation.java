package dna.metrics.motifs.directedMotifs;

import java.util.HashSet;

import dna.graph.IElement;
import dna.graph.edges.DirectedEdge;
import dna.graph.nodes.DirectedNode;
import dna.metrics.motifs.directedMotifs.exceptions.DirectedMotifInvalidEdgeAdditionException;
import dna.metrics.motifs.directedMotifs.exceptions.DirectedMotifInvalidEdgeRemovalException;
import dna.metrics.motifs.directedMotifs.exceptions.DirectedMotifSplittingException;
import dna.metrics.motifs.directedMotifs.exceptions.InvalidDirectedMotifException;
import dna.updates.update.EdgeAddition;
import dna.updates.update.EdgeRemoval;
import dna.updates.update.Update;

public abstract class DirectedMotifsComputation extends DirectedMotifs {

	public DirectedMotifsComputation(String name, ApplicationType type,
			MetricType metricType) {
		super(name, type, metricType);
	}

	@Override
	public boolean compute() {
		for (IElement element : this.g.getNodes()) {
			DirectedNode a = (DirectedNode) element;
			HashSet<DirectedNode> a_ = this.getConnectedNodes(a);
			for (DirectedNode b : a_) {
				HashSet<DirectedNode> b_ = this.getConnectedNodes(b);
				for (DirectedNode c : b_) {
					if (c.getIndex() > a.getIndex() && !a_.contains(c)) {
						try {
							// System.out.println("COMP: add "
							// + DirectedMotif.getMotif(a, b, c));
							this.motifs.incr(DirectedMotifs
									.getIndex(DirectedMotif.getType(a, b, c)));
						} catch (InvalidDirectedMotifException e) {
							e.printStackTrace();
						}
					}
				}
				if (b.getIndex() > a.getIndex()) {
					for (DirectedNode c : b_) {
						if (c.getIndex() > b.getIndex() && a_.contains(c)) {
							try {
								// System.out.println("COMP: add "
								// + DirectedMotif.getMotif(a, b, c));
								this.motifs.incr(DirectedMotifs
										.getIndex(DirectedMotif
												.getType(a, b, c)));
							} catch (InvalidDirectedMotifException e) {
								e.printStackTrace();
							}
						}
					}
				}
			}
		}
		return true;
	}

	protected HashSet<DirectedNode> getConnectedNodes(DirectedNode node) {
		HashSet<DirectedNode> nodes = new HashSet<DirectedNode>(
				node.getInDegree() + node.getOutDegree());
		for (IElement in : node.getIncomingEdges()) {
			nodes.add(((DirectedEdge) in).getSrc());
		}
		for (IElement out : node.getOutgoingEdges()) {
			nodes.add(((DirectedEdge) out).getDst());
		}
		return nodes;
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

}
