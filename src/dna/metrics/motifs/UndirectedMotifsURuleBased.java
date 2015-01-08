package dna.metrics.motifs;

import java.util.HashSet;

import dna.graph.IElement;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.edges.UndirectedEdge;
import dna.graph.nodes.UndirectedNode;
import dna.metrics.algorithms.IAfterER;
import dna.metrics.algorithms.IBeforeEA;
import dna.metrics.algorithms.IBeforeNA;
import dna.metrics.algorithms.IBeforeNR;
import dna.updates.update.EdgeAddition;
import dna.updates.update.EdgeRemoval;
import dna.updates.update.NodeAddition;
import dna.updates.update.NodeRemoval;
import dna.updates.update.Update;

public class UndirectedMotifsURuleBased extends UndirectedMotifs implements
		IBeforeNA, IBeforeNR, IBeforeEA, IAfterER {

	private UndirectedMotifsRule[][][][][] rules;

	public UndirectedMotifsURuleBased() {
		super("UndirectedMotifsURuleBased");
	}

	@Override
	public boolean init() {
		UndirectedMotifType m1 = UndirectedMotifType.UM1;
		UndirectedMotifType m2 = UndirectedMotifType.UM2;
		UndirectedMotifType m3 = UndirectedMotifType.UM3;
		UndirectedMotifType m4 = UndirectedMotifType.UM4;
		UndirectedMotifType m5 = UndirectedMotifType.UM5;
		UndirectedMotifType m6 = UndirectedMotifType.UM6;

		this.rules = new UndirectedMotifsRule[2][2][2][2][2];

		this.rules[1][0][0][1][0] = new UndirectedMotifsRuleAdd(m1);
		this.rules[0][1][1][0][0] = new UndirectedMotifsRuleAdd(m1);
		this.rules[1][0][0][0][1] = new UndirectedMotifsRuleAdd(m1);
		this.rules[0][1][0][0][1] = new UndirectedMotifsRuleAdd(m1);
		this.rules[0][0][1][0][1] = new UndirectedMotifsRuleAdd(m1);
		this.rules[0][0][0][1][1] = new UndirectedMotifsRuleAdd(m1);
		this.rules[1][1][0][0][0] = new UndirectedMotifsRuleAdd(m2);
		this.rules[0][0][1][1][0] = new UndirectedMotifsRuleAdd(m2);
		this.rules[1][1][0][0][1] = new UndirectedMotifsRuleAdd(m4);
		this.rules[0][0][1][1][1] = new UndirectedMotifsRuleAdd(m4);

		this.rules[1][0][0][1][1] = new UndirectedMotifsRuleChange(m1, m3);
		this.rules[0][1][1][0][1] = new UndirectedMotifsRuleChange(m1, m3);
		this.rules[1][1][1][0][0] = new UndirectedMotifsRuleChange(m1, m4);
		this.rules[1][1][0][1][0] = new UndirectedMotifsRuleChange(m1, m4);
		this.rules[1][0][1][1][0] = new UndirectedMotifsRuleChange(m1, m4);
		this.rules[0][1][1][1][0] = new UndirectedMotifsRuleChange(m1, m4);
		this.rules[1][0][1][0][1] = new UndirectedMotifsRuleChange(m2, m4);
		this.rules[0][1][0][1][1] = new UndirectedMotifsRuleChange(m2, m4);
		this.rules[1][1][1][1][0] = new UndirectedMotifsRuleChange(m3, m5);
		this.rules[1][1][1][0][1] = new UndirectedMotifsRuleChange(m4, m5);
		this.rules[1][1][0][1][1] = new UndirectedMotifsRuleChange(m4, m5);
		this.rules[1][0][1][1][1] = new UndirectedMotifsRuleChange(m4, m5);
		this.rules[0][1][1][1][1] = new UndirectedMotifsRuleChange(m4, m5);
		this.rules[1][1][1][1][1] = new UndirectedMotifsRuleChange(m5, m6);

		return this.compute();
	}

	private UndirectedMotifsRule getRule(UndirectedNode a, UndirectedNode b,
			UndirectedNode c, UndirectedNode d) {
		int ac = a.hasEdge(a, c) ? 1 : 0;
		int ad = a.hasEdge(a, d) ? 1 : 0;
		int bc = b.hasEdge(b, c) ? 1 : 0;
		int bd = b.hasEdge(b, d) ? 1 : 0;
		int cd = c.hasEdge(c, d) ? 1 : 0;
		return this.rules[ac][ad][bc][bd][cd];
	}

	private HashSet<UndirectedEdge> getN(UndirectedNode a, UndirectedNode b) {
		HashSet<UndirectedEdge> n = new HashSet<UndirectedEdge>();

		GraphDataStructure gds = this.g.getGraphDatastructures();

		for (IElement c_ : a.getEdges()) {
			UndirectedNode c = (UndirectedNode) ((UndirectedEdge) c_)
					.getDifferingNode(a);
			for (IElement d_ : a.getEdges()) {
				UndirectedNode d = (UndirectedNode) ((UndirectedEdge) d_)
						.getDifferingNode(a);
				if (!d.equals(c)) {
					n.add((UndirectedEdge) gds.newEdgeInstance(c, d));
				}
			}
			for (IElement d_ : b.getEdges()) {
				UndirectedNode d = (UndirectedNode) ((UndirectedEdge) d_)
						.getDifferingNode(b);
				if (!d.equals(c)) {
					n.add((UndirectedEdge) gds.newEdgeInstance(c, d));
				}
			}
			for (IElement d_ : c.getEdges()) {
				UndirectedNode d = (UndirectedNode) ((UndirectedEdge) d_)
						.getDifferingNode(c);
				if (!d.equals(a) && !d.equals(b)) {
					n.add((UndirectedEdge) gds.newEdgeInstance(c, d));
				}
			}
		}

		for (IElement c_ : b.getEdges()) {
			UndirectedNode c = (UndirectedNode) ((UndirectedEdge) c_)
					.getDifferingNode(b);
			for (IElement d_ : b.getEdges()) {
				UndirectedNode d = (UndirectedNode) ((UndirectedEdge) d_)
						.getDifferingNode(b);
				if (!d.equals(c)) {
					n.add((UndirectedEdge) gds.newEdgeInstance(c, d));
				}
			}
			for (IElement d_ : c.getEdges()) {
				UndirectedNode d = (UndirectedNode) ((UndirectedEdge) d_)
						.getDifferingNode(c);
				if (!d.equals(a) && !d.equals(b)) {
					n.add((UndirectedEdge) gds.newEdgeInstance(c, d));
				}
			}
		}

		return n;
	}

	@Override
	public boolean applyBeforeUpdate(EdgeAddition ea) {
		UndirectedNode a = (UndirectedNode) ea.getEdge().getN1();
		UndirectedNode b = (UndirectedNode) ea.getEdge().getN2();

		HashSet<UndirectedEdge> N = this.getN(a, b);
		for (UndirectedEdge cd : N) {
			UndirectedNode c = cd.getNode1();
			UndirectedNode d = cd.getNode2();
			this.getRule(a, b, c, d).execute(this.motifs, true);
		}

		return true;
	}

	@Override
	public boolean applyAfterUpdate(EdgeRemoval er) {
		UndirectedNode a = (UndirectedNode) er.getEdge().getN1();
		UndirectedNode b = (UndirectedNode) er.getEdge().getN2();

		HashSet<UndirectedEdge> N = this.getN(a, b);
		for (UndirectedEdge cd : N) {
			UndirectedNode c = cd.getNode1();
			UndirectedNode d = cd.getNode2();
			this.getRule(a, b, c, d).execute(this.motifs, false);
		}

		return true;
		// return this.processEdge(er);
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

		this.addMotifs56(a, b, ab, u);

		this.addMotifs13(a, b, aOnly, bOnly, u);

		this.addMotif1(a, b, aOnly, u);
		this.addMotif1(a, b, bOnly, u);

		this.addMotif4(a, b, ab, u);

		this.addMotifs24(a, b, aOnly, u);
		this.addMotifs24(a, b, bOnly, u);

		this.addMotifs45(a, b, ab, aOnly, u);
		this.addMotifs45(a, b, ab, bOnly, u);

		return true;
	}

	private void addMotifs56(UndirectedNode a, UndirectedNode b,
			HashSet<UndirectedNode> ab, Update u) {
		for (UndirectedNode c : ab) {
			for (UndirectedNode d : ab) {
				if (c.getIndex() >= d.getIndex()) {
					continue;
				}
				if (c.hasEdge(c, d)) {
					this.changeMotif(a, b, c, d, UndirectedMotifType.UM5,
							UndirectedMotifType.UM6, u);
				} else {
					this.changeMotif(a, b, c, d, UndirectedMotifType.UM3,
							UndirectedMotifType.UM5, u);
				}
			}
		}
	}

	private void addMotifs13(UndirectedNode a, UndirectedNode b,
			HashSet<UndirectedNode> aOnly, HashSet<UndirectedNode> bOnly,
			Update u) {
		for (UndirectedNode c : aOnly) {
			for (UndirectedNode d : bOnly) {
				if (c.hasEdge(c, d)) {
					this.changeMotif(a, b, c, d, UndirectedMotifType.UM1,
							UndirectedMotifType.UM3, u);
				} else {
					this.changeMotif(a, b, c, d, UndirectedMotifType.UM1, u);
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
				this.changeMotif(a, b, c, d, UndirectedMotifType.UM1, u);
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
				this.changeMotif(a, b, c, d, UndirectedMotifType.UM2,
						UndirectedMotifType.UM4, u);
			}
		}
	}

	private void addMotifs24(UndirectedNode a, UndirectedNode b,
			HashSet<UndirectedNode> only, Update u) {
		for (UndirectedNode c : only) {
			for (UndirectedNode d : only) {
				if (c.getIndex() >= d.getIndex()) {
					continue;
				}
				if (c.hasEdge(c, d)) {
					this.changeMotif(a, b, c, d, UndirectedMotifType.UM4, u);
				} else {
					this.changeMotif(a, b, c, d, UndirectedMotifType.UM2, u);
				}
			}
		}
	}

	private void addMotifs45(UndirectedNode a, UndirectedNode b,
			HashSet<UndirectedNode> ab, HashSet<UndirectedNode> only, Update u) {
		for (UndirectedNode c : ab) {
			for (UndirectedNode d : only) {
				if (c.hasEdge(c, d)) {
					this.changeMotif(a, b, c, d, UndirectedMotifType.UM4,
							UndirectedMotifType.UM5, u);
				} else {
					this.changeMotif(a, b, c, d, UndirectedMotifType.UM1,
							UndirectedMotifType.UM4, u);
				}
			}
		}
	}

	private void changeMotif(UndirectedNode a, UndirectedNode b,
			UndirectedNode c, UndirectedNode d, UndirectedMotifType t1,
			UndirectedMotifType t2, Update u) {
		UndirectedMotifsRuleChange r = (UndirectedMotifsRuleChange) this
				.getRule(a, b, c, d);
		// System.out.println(a.getIndex() + " " + b.getIndex() + " "
		// + c.getIndex() + " " + d.getIndex() + " --- " + (r.m1 == t1)
		// + " / " + (r.m2 == t2));

		if (u instanceof EdgeAddition) {
			this.motifs.decr(UndirectedMotifs.getIndex(t1));
			this.motifs.incr(UndirectedMotifs.getIndex(t2));
		} else if (u instanceof EdgeRemoval) {
			this.motifs.decr(UndirectedMotifs.getIndex(t2));
			this.motifs.incr(UndirectedMotifs.getIndex(t1));
		}
	}

	private void changeMotif(UndirectedNode a, UndirectedNode b,
			UndirectedNode c, UndirectedNode d, UndirectedMotifType type,
			Update u) {
		UndirectedMotifsRuleAdd r = (UndirectedMotifsRuleAdd) this.getRule(a,
				b, c, d);
		// System.out.println(a.getIndex() + " " + b.getIndex() + " "
		// + c.getIndex() + " " + d.getIndex() + " --- " + (type == r.m));

		if (u instanceof EdgeAddition) {
			this.motifs.incr(UndirectedMotifs.getIndex(type));
		} else if (u instanceof EdgeRemoval) {
			this.motifs.decr(UndirectedMotifs.getIndex(type));
		}
	}

}
