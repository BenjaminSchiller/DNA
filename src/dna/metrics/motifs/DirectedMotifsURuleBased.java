package dna.metrics.motifs;

import java.util.HashSet;

import dna.graph.IElement;
import dna.graph.edges.DirectedEdge;
import dna.graph.nodes.DirectedNode;
import dna.metrics.algorithms.IBeforeUpdates;
import dna.updates.update.EdgeAddition;
import dna.updates.update.EdgeRemoval;
import dna.updates.update.NodeAddition;
import dna.updates.update.NodeRemoval;

public class DirectedMotifsURuleBased extends DirectedMotifs implements IBeforeUpdates {

	private DirectedMotifsRule[][][][][] rules;

	public DirectedMotifsURuleBased() {
		super("DirectedMotifsURuleBased");
	}

	@Override
	public boolean init() {
		this.rules = new DirectedMotifsRule[2][2][2][2][2];

		DirectedMotifType m1 = DirectedMotifType.DM01;
		DirectedMotifType m2 = DirectedMotifType.DM02;
		DirectedMotifType m3 = DirectedMotifType.DM03;
		DirectedMotifType m4 = DirectedMotifType.DM04;
		DirectedMotifType m5 = DirectedMotifType.DM05;
		DirectedMotifType m6 = DirectedMotifType.DM06;
		DirectedMotifType m7 = DirectedMotifType.DM07;
		DirectedMotifType m8 = DirectedMotifType.DM08;
		DirectedMotifType m9 = DirectedMotifType.DM09;
		DirectedMotifType m10 = DirectedMotifType.DM10;
		DirectedMotifType m11 = DirectedMotifType.DM11;
		DirectedMotifType m12 = DirectedMotifType.DM12;
		DirectedMotifType m13 = DirectedMotifType.DM13;

		rules[1][0][0][0][0] = new DirectedMotifsRuleAdd(m1);
		rules[0][0][1][0][0] = new DirectedMotifsRuleAdd(m3);
		rules[0][1][0][0][0] = new DirectedMotifsRuleAdd(m3);
		rules[0][0][0][1][0] = new DirectedMotifsRuleAdd(m2);
		rules[1][1][0][0][0] = new DirectedMotifsRuleAdd(m5);
		rules[0][0][1][1][0] = new DirectedMotifsRuleAdd(m6);

		rules[1][0][0][0][1] = new DirectedMotifsRuleChange(m3, m5);
		rules[0][0][1][0][1] = new DirectedMotifsRuleChange(m1, m5);
		rules[0][1][0][0][1] = new DirectedMotifsRuleChange(m2, m6);
		rules[0][0][0][1][1] = new DirectedMotifsRuleChange(m3, m6);
		rules[1][1][0][0][1] = new DirectedMotifsRuleChange(m6, m11);
		rules[0][0][1][1][1] = new DirectedMotifsRuleChange(m5, m11);
		rules[1][0][0][1][0] = new DirectedMotifsRuleChange(m3, m4);
		rules[1][0][0][1][1] = new DirectedMotifsRuleChange(m7, m10);
		rules[1][1][0][1][0] = new DirectedMotifsRuleChange(m5, m9);
		rules[1][1][0][1][1] = new DirectedMotifsRuleChange(m10, m12);
		rules[1][0][1][0][0] = new DirectedMotifsRuleChange(m2, m4);
		rules[1][0][1][0][1] = new DirectedMotifsRuleChange(m4, m9);
		rules[1][1][1][0][0] = new DirectedMotifsRuleChange(m6, m10);
		rules[1][1][1][0][1] = new DirectedMotifsRuleChange(m8, m12);
		rules[0][1][0][1][0] = new DirectedMotifsRuleChange(m1, m4);
		rules[0][1][0][1][1] = new DirectedMotifsRuleChange(m4, m8);
		rules[0][1][1][1][0] = new DirectedMotifsRuleChange(m5, m10);
		rules[0][1][1][1][1] = new DirectedMotifsRuleChange(m9, m12);
		rules[0][1][1][0][0] = new DirectedMotifsRuleChange(m3, m7);
		rules[0][1][1][0][1] = new DirectedMotifsRuleChange(m4, m10);
		rules[1][0][1][1][0] = new DirectedMotifsRuleChange(m6, m8);
		rules[1][0][1][1][1] = new DirectedMotifsRuleChange(m10, m12);
		rules[1][1][1][1][0] = new DirectedMotifsRuleChange(m11, m12);
		rules[1][1][1][1][1] = new DirectedMotifsRuleChange(m12, m13);

		return this.compute();
	}

	public DirectedMotifsRule getRule(DirectedNode a, DirectedNode b,
			DirectedNode c) {
		int ac = a.hasEdge(a, c) ? 1 : 0;
		int ca = a.hasEdge(c, a) ? 1 : 0;
		int bc = b.hasEdge(b, c) ? 1 : 0;
		int cb = b.hasEdge(c, b) ? 1 : 0;
		int ba = a.hasEdge(b, a) ? 1 : 0;
		return this.rules[ac][ca][bc][cb][ba];
	}

	private HashSet<DirectedNode> getN(DirectedNode a, DirectedNode b) {
		HashSet<DirectedNode> union = new HashSet<DirectedNode>(a.getDegree()
				+ b.getDegree());
		for (IElement e : a.getOutgoingEdges()) {
			union.add(((DirectedEdge) e).getDst());
		}
		for (IElement e : a.getIncomingEdges()) {
			union.add(((DirectedEdge) e).getSrc());
		}
		for (IElement e : b.getOutgoingEdges()) {
			union.add(((DirectedEdge) e).getDst());
		}
		for (IElement e : b.getIncomingEdges()) {
			union.add(((DirectedEdge) e).getSrc());
		}
		union.remove(a);
		union.remove(b);
		return union;
	}

	@Override
	public boolean applyBeforeUpdate(NodeAddition na) {
		return true;
	}

	@Override
	public boolean applyBeforeUpdate(NodeRemoval nr) {
		DirectedNode a = (DirectedNode) nr.getNode();
		HashSet<DirectedNode> a_ = this.getConnectedNodes(a);
		for (DirectedNode b : a_) {
			boolean ab = a.hasEdge(a, b);
			boolean ba = a.hasEdge(b, a);

			for (DirectedNode c : a_) {
				if (b.getIndex() <= c.getIndex()) {
					continue;
				}
				boolean ac = a.hasEdge(a, c);
				boolean ca = a.hasEdge(c, a);
				boolean bc = b.hasEdge(b, c);
				boolean cb = b.hasEdge(c, b);
				this.decr(this.getType(ab, ba, ac, ca, bc, cb));
			}

			HashSet<DirectedNode> b_ = this.getConnectedNodes(b);
			for (DirectedNode c : b_) {
				if (a_.contains(c) || c.getIndex() == a.getIndex()) {
					continue;
				}
				boolean ac = a.hasEdge(a, c);
				boolean ca = a.hasEdge(c, a);
				boolean bc = b.hasEdge(b, c);
				boolean cb = b.hasEdge(c, b);
				this.decr(this.getType(ab, ba, ac, ca, bc, cb));
			}
		}
		return true;
	}

	@Override
	public boolean applyBeforeUpdate(EdgeAddition ea) {
		DirectedEdge e = (DirectedEdge) ea.getEdge();
		DirectedNode a = e.getSrc();
		DirectedNode b = e.getDst();

		for (DirectedNode c : this.getN(a, b)) {
			this.getRule(a, b, c).execute(this.motifs, true);
		}

		return true;
	}

	@Override
	public boolean applyBeforeUpdate(EdgeRemoval er) {
		DirectedEdge e = (DirectedEdge) er.getEdge();
		DirectedNode a = e.getSrc();
		DirectedNode b = e.getDst();

		for (DirectedNode c : this.getN(a, b)) {
			this.getRule(a, b, c).execute(this.motifs, false);
		}

		return true;
	}

}
