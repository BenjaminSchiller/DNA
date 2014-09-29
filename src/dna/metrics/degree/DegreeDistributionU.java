package dna.metrics.degree;

import dna.graph.IElement;
import dna.graph.edges.DirectedEdge;
import dna.graph.edges.UndirectedEdge;
import dna.graph.nodes.DirectedNode;
import dna.graph.nodes.UndirectedNode;
import dna.metrics.IMetricNew;
import dna.metrics.algorithms.IBeforeUpdates;
import dna.updates.update.EdgeAddition;
import dna.updates.update.EdgeRemoval;
import dna.updates.update.NodeAddition;
import dna.updates.update.NodeRemoval;

public class DegreeDistributionU extends DegreeDistribution implements
		IBeforeUpdates {

	public DegreeDistributionU() {
		super("DegreeDistributionU", IMetricNew.MetricType.exact);
	}

	@Override
	public boolean init() {
		return this.compute();
	}

	@Override
	public boolean applyBeforeUpdate(NodeAddition na) {
		this.degree.incr(na.getNode().getDegree());
		if (na.getNode() instanceof DirectedNode) {
			this.inDegree.incr(0);
			this.outDegree.incr(0);
		}
		return true;
	}

	@Override
	public boolean applyBeforeUpdate(NodeRemoval nr) {
		if (nr.getNode() instanceof UndirectedNode) {
			UndirectedNode n = (UndirectedNode) nr.getNode();
			this.degree.decr(n.getDegree());
			for (IElement e_ : n.getEdges()) {
				int d = ((UndirectedEdge) e_).getDifferingNode(n).getDegree();
				this.degree.decr(d);
				this.degree.incr(d - 1);
			}
			this.degree.truncate();
		}
		if (nr.getNode() instanceof DirectedNode) {
			DirectedNode n = (DirectedNode) nr.getNode();
			this.degree.decr(n.getDegree());
			this.inDegree.decr(n.getInDegree());
			this.outDegree.decr(n.getOutDegree());
			for (IElement neighbor_ : n.getNeighbors()) {
				DirectedNode neighbor = (DirectedNode) neighbor_;
				this.degree.decr(neighbor.getDegree());
				this.inDegree.decr(neighbor.getInDegree());
				this.outDegree.decr(neighbor.getOutDegree());
				this.degree.incr(neighbor.getDegree() - 2);
				this.inDegree.incr(neighbor.getInDegree() - 1);
				this.outDegree.incr(neighbor.getOutDegree() - 1);
			}
			for (IElement out_ : n.getOutgoingEdges()) {
				DirectedNode out = ((DirectedEdge) out_).getDst();
				if (!n.hasNeighbor(out)) {
					this.degree.decr(out.getDegree());
					this.degree.incr(out.getDegree() - 1);
					this.inDegree.decr(out.getInDegree());
					this.inDegree.incr(out.getInDegree() - 1);
				}
			}
			for (IElement in_ : n.getIncomingEdges()) {
				DirectedNode in = ((DirectedEdge) in_).getSrc();
				if (!n.hasNeighbor(in)) {
					this.degree.decr(in.getDegree());
					this.degree.incr(in.getDegree() - 1);
					this.outDegree.decr(in.getOutDegree());
					this.outDegree.incr(in.getOutDegree() - 1);
				}
			}
			this.degree.truncate();
			this.inDegree.truncate();
			this.outDegree.truncate();
		}
		return true;
	}

	@Override
	public boolean applyBeforeUpdate(EdgeAddition ea) {
		this.degree.decr(ea.getEdge().getN1().getDegree());
		this.degree.decr(ea.getEdge().getN2().getDegree());
		this.degree.incr(ea.getEdge().getN1().getDegree() + 1);
		this.degree.incr(ea.getEdge().getN2().getDegree() + 1);
		this.degree.truncate();

		if (ea.getEdge() instanceof DirectedEdge) {
			DirectedEdge e = (DirectedEdge) ea.getEdge();
			this.inDegree.decr(e.getDst().getInDegree());
			this.outDegree.decr(e.getSrc().getOutDegree());
			this.inDegree.incr(e.getDst().getInDegree() + 1);
			this.outDegree.incr(e.getSrc().getOutDegree() + 1);
			this.inDegree.truncate();
			this.outDegree.truncate();
		}
		return true;
	}

	@Override
	public boolean applyBeforeUpdate(EdgeRemoval er) {
		this.degree.decr(er.getEdge().getN1().getDegree());
		this.degree.decr(er.getEdge().getN2().getDegree());
		this.degree.incr(er.getEdge().getN1().getDegree() - 1);
		this.degree.incr(er.getEdge().getN2().getDegree() - 1);
		this.degree.truncate();

		if (er.getEdge() instanceof DirectedEdge) {
			DirectedEdge e = (DirectedEdge) er.getEdge();
			this.inDegree.decr(e.getDst().getInDegree());
			this.outDegree.decr(e.getSrc().getOutDegree());
			this.inDegree.incr(e.getDst().getInDegree() - 1);
			this.outDegree.incr(e.getSrc().getOutDegree() - 1);
			this.inDegree.truncate();
			this.outDegree.truncate();
		}
		return true;
	}

}
