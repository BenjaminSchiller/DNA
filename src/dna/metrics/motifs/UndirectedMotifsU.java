package dna.metrics.motifs;

import java.util.HashSet;

import dna.graph.IElement;
import dna.graph.edges.UndirectedEdge;
import dna.graph.nodes.UndirectedNode;
import dna.metrics.algorithms.IBeforeUpdates;
import dna.updates.update.EdgeAddition;
import dna.updates.update.EdgeRemoval;
import dna.updates.update.NodeAddition;
import dna.updates.update.NodeRemoval;
import dna.updates.update.Update;

public class UndirectedMotifsU extends UndirectedMotifs implements
		IBeforeUpdates {

	public UndirectedMotifsU() {
		super("UndirectedMotifsU");
	}

	@Override
	public boolean init() {
		return this.compute();
	}

	@Override
	public boolean applyBeforeUpdate(NodeAddition na) {
		return true;
	}

	@Override
	public boolean applyBeforeUpdate(NodeRemoval nr) {

		UndirectedNode a, b, c, d;

		a = (UndirectedNode) nr.getNode();

		UndirectedNode[] neighborsA = this.getNeighborsSorted(a);

		// b,c,d are neighbors of a
		for (int b_ = 0; b_ < neighborsA.length; b_++) {
			b = neighborsA[b_];
			for (int c_ = b_ + 1; c_ < neighborsA.length; c_++) {
				c = neighborsA[c_];
				for (int d_ = c_ + 1; d_ < neighborsA.length; d_++) {
					d = neighborsA[d_];
					boolean bc = this.connected(b, c);
					boolean bd = this.connected(b, d);
					boolean cd = this.connected(c, d);
					int sum = (bc ? 1 : 0) + (bd ? 1 : 0) + (cd ? 1 : 0);
					switch (sum) {
					case 0:
						this.decr(UndirectedMotifType.UM2);
						break;
					case 1:
						this.decr(UndirectedMotifType.UM4);
						break;
					case 2:
						this.decr(UndirectedMotifType.UM5);
						break;
					case 3:
						this.decr(UndirectedMotifType.UM6);
						break;
					}
				}
			}
		}

		// b,c are neighbors of a
		for (int b_ = 0; b_ < neighborsA.length; b_++) {
			b = neighborsA[b_];
			for (int c_ = b_ + 1; c_ < neighborsA.length; c_++) {
				c = neighborsA[c_];

				// d is neighbor of b but not c
				for (IElement d_ : b.getEdges()) {
					d = this.get(d_, b);
					if (a.equals(d) || c.equals(d)) {
						continue;
					}
					if (this.connected(d, c) || this.connected(a, d)) {
						continue;
					}
					if (!this.connected(b, c)) {
						this.decr(UndirectedMotifType.UM1);
					} else {
						this.decr(UndirectedMotifType.UM4);
					}
				}

				// d is neighbor of c
				for (IElement d_ : c.getEdges()) {
					d = this.get(d_, c);
					if (a.equals(d) || b.equals(d)) {
						continue;
					}
					if (this.connected(a, d)) {
						continue;
					}
					if (!this.connected(d, b)) {
						// d not connected to b
						if (!this.connected(b, c)) {
							this.decr(UndirectedMotifType.UM1);
						} else {
							this.decr(UndirectedMotifType.UM4);
						}
					} else {
						// d connected to b
						if (!this.connected(b, c)) {
							this.decr(UndirectedMotifType.UM3);
						} else {
							this.decr(UndirectedMotifType.UM5);
						}
					}
				}
			}
		}

		// only b is neighbor of a
		for (int b_ = 0; b_ < neighborsA.length; b_++) {
			b = neighborsA[b_];
			UndirectedNode[] neighborsB = this.getNeighborsSorted(b);

			// c is neighbor of b
			for (int c_ = 0; c_ < neighborsB.length; c_++) {
				c = neighborsB[c_];
				if (a.equals(c) || this.connected(a, c)) {
					continue;
				}

				// d is connected to b but not c
				for (int d_ = c_ + 1; d_ < neighborsB.length; d_++) {
					d = neighborsB[d_];
					if (a.equals(d) || c.equals(d)) {
						continue;
					}
					if (this.connected(a, d)) {
						continue;
					}
					if (!this.connected(d, c)) {
						// d is not connected to c
						this.decr(UndirectedMotifType.UM2);
					} else {
						// d is connected to c
						this.decr(UndirectedMotifType.UM4);
					}
				}

				// d is connected to c but not b
				for (IElement d_ : c.getEdges()) {
					d = this.get(d_, c);
					if (a.equals(d) || b.equals(d)) {
						continue;
					}
					if (this.connected(d, b) || this.connected(a, d)) {
						continue;
					}
					this.decr(UndirectedMotifType.UM1);
				}
			}
		}

		return true;
	}

	private UndirectedNode get(IElement e, UndirectedNode n) {
		return (UndirectedNode) ((UndirectedEdge) e).getDifferingNode(n);
	}

	private boolean connected(UndirectedNode a, UndirectedNode b) {
		return a.hasEdge(a, b);
	}

	@Override
	public boolean applyBeforeUpdate(EdgeAddition ea) {
		return this.processEdge(ea);
	}

	@Override
	public boolean applyBeforeUpdate(EdgeRemoval er) {
		return this.processEdge(er);
	}

	private boolean processEdge(Update u) {
		UndirectedEdge e = null;
		if (u instanceof EdgeAddition) {
			e = (UndirectedEdge) ((EdgeAddition) u).getEdge();
		} else {
			e = (UndirectedEdge) ((EdgeRemoval) u).getEdge();
		}
		UndirectedNode a = e.getNode1();
		UndirectedNode b = e.getNode2();

		HashSet<UndirectedNode> ab = new HashSet<UndirectedNode>();
		HashSet<UndirectedNode> aOnly = new HashSet<UndirectedNode>();
		HashSet<UndirectedNode> bOnly = new HashSet<UndirectedNode>();

		for (IElement e_a : a.getEdges()) {
			UndirectedNode n = (UndirectedNode) ((UndirectedEdge) e_a)
					.getDifferingNode(a);
			if (n.getIndex() == b.getIndex()) {
				continue;
			}
			if (!n.hasEdge(n, b)) {
				aOnly.add(n);
			} else {
				ab.add(n);
			}
		}
		for (IElement e_b : b.getEdges()) {
			UndirectedNode n = (UndirectedNode) ((UndirectedEdge) e_b)
					.getDifferingNode(b);
			if (n.getIndex() == a.getIndex()) {
				continue;
			}
			if (!n.hasEdge(n, a)) {
				bOnly.add(n);
			}
		}

		this.addMotifs56(ab, u);

		this.addMotifs13(aOnly, bOnly, u);

		this.addMotif1(a, b, aOnly, u);
		this.addMotif1(a, b, bOnly, u);

		this.addMotif4(a, b, ab, u);

		this.addMotifs24(aOnly, u);
		this.addMotifs24(bOnly, u);

		this.addMotifs45(ab, aOnly, u);
		this.addMotifs45(ab, bOnly, u);

		return true;
	}

	private void addMotifs56(HashSet<UndirectedNode> ab, Update u) {
		for (UndirectedNode c : ab) {
			for (UndirectedNode d : ab) {
				if (c.getIndex() >= d.getIndex()) {
					continue;
				}
				if (c.hasEdge(c, d)) {
					this.changeMotif(UndirectedMotifType.UM5,
							UndirectedMotifType.UM6, u);
				} else {
					this.changeMotif(UndirectedMotifType.UM3,
							UndirectedMotifType.UM5, u);
				}
			}
		}
	}

	private void addMotifs13(HashSet<UndirectedNode> aOnly,
			HashSet<UndirectedNode> bOnly, Update u) {
		for (UndirectedNode c : aOnly) {
			for (UndirectedNode d : bOnly) {
				if (c.hasEdge(c, d)) {
					this.changeMotif(UndirectedMotifType.UM1,
							UndirectedMotifType.UM3, u);
				} else {
					this.changeMotif(UndirectedMotifType.UM1, u);
				}
			}
		}
	}

	private void addMotif1(UndirectedNode a, UndirectedNode b,
			HashSet<UndirectedNode> only, Update u) {
		for (UndirectedNode c : only) {
			for (IElement e_c : c.getEdges()) {
				UndirectedNode d = (UndirectedNode) ((UndirectedEdge) e_c)
						.getDifferingNode(c);
				if (d.hasEdge(d, a)) {
					continue;
				}
				if (d.hasEdge(d, b)) {
					continue;
				}
				if (d.getIndex() == a.getIndex()) {
					continue;
				}
				if (d.getIndex() == b.getIndex()) {
					continue;
				}
				this.changeMotif(UndirectedMotifType.UM1, u);
			}
		}
	}

	private void addMotif4(UndirectedNode a, UndirectedNode b,
			HashSet<UndirectedNode> ab, Update u) {
		for (UndirectedNode c : ab) {
			for (IElement e_c : c.getEdges()) {
				UndirectedNode d = (UndirectedNode) ((UndirectedEdge) e_c)
						.getDifferingNode(c);
				if (d.hasEdge(d, a)) {
					continue;
				}
				if (d.hasEdge(d, b)) {
					continue;
				}
				if (d.getIndex() == a.getIndex()) {
					continue;
				}
				if (d.getIndex() == b.getIndex()) {
					continue;
				}
				this.changeMotif(UndirectedMotifType.UM2,
						UndirectedMotifType.UM4, u);
			}
		}
	}

	private void addMotifs24(HashSet<UndirectedNode> only, Update u) {
		for (UndirectedNode c : only) {
			for (UndirectedNode d : only) {
				if (c.getIndex() >= d.getIndex()) {
					continue;
				}
				if (c.hasEdge(c, d)) {
					this.changeMotif(UndirectedMotifType.UM4, u);
				} else {
					this.changeMotif(UndirectedMotifType.UM2, u);
				}
			}
		}
	}

	private void addMotifs45(HashSet<UndirectedNode> ab,
			HashSet<UndirectedNode> only, Update u) {
		for (UndirectedNode c : ab) {
			for (UndirectedNode d : only) {
				if (c.hasEdge(c, d)) {
					this.changeMotif(UndirectedMotifType.UM4,
							UndirectedMotifType.UM5, u);
				} else {
					this.changeMotif(UndirectedMotifType.UM1,
							UndirectedMotifType.UM4, u);
				}
			}
		}
	}

	private void changeMotif(UndirectedMotifType t1, UndirectedMotifType t2,
			Update u) {
		if (u instanceof EdgeAddition) {
			this.motifs.decr(UndirectedMotifs.getIndex(t1));
			this.motifs.incr(UndirectedMotifs.getIndex(t2));
		} else if (u instanceof EdgeRemoval) {
			this.motifs.decr(UndirectedMotifs.getIndex(t2));
			this.motifs.incr(UndirectedMotifs.getIndex(t1));
		}
	}

	private void changeMotif(UndirectedMotifType type, Update u) {
		if (u instanceof EdgeAddition) {
			this.motifs.incr(UndirectedMotifs.getIndex(type));
		} else if (u instanceof EdgeRemoval) {
			this.motifs.decr(UndirectedMotifs.getIndex(type));
		}
	}

}
