package dna.metrics.degree;

import dna.graph.Graph;
import dna.graph.IElement;
import dna.graph.edges.DirectedEdge;
import dna.graph.edges.UndirectedEdge;
import dna.graph.nodes.DirectedNode;
import dna.graph.nodes.UndirectedNode;
import dna.updates.batch.Batch;
import dna.updates.update.EdgeAddition;
import dna.updates.update.EdgeRemoval;
import dna.updates.update.EdgeUpdate;
import dna.updates.update.NodeAddition;
import dna.updates.update.NodeRemoval;
import dna.updates.update.Update;
import dna.util.Log;

public class DegreeDistributionU extends DegreeDistribution {

	public DegreeDistributionU() {
		super("DegreeDistributionU", ApplicationType.BeforeUpdate,
				MetricType.exact);
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
		if (DirectedNode.class.isAssignableFrom(this.g.getGraphDatastructures()
				.getNodeType())) {
			return this.applyBeforeUpdateDirected(u);
		} else if (UndirectedNode.class.isAssignableFrom(this.g
				.getGraphDatastructures().getNodeType())) {
			return this.applyBeforeUpdateUndirected(u);
		}
		Log.error("DD - unsupported node type "
				+ this.g.getGraphDatastructures().getNodeType());
		return false;
	}

	private boolean applyBeforeUpdateDirected(Update u) {
		if (u instanceof NodeAddition) {

			this.nodes++;
			this.degree.incr(0);
			this.degree.incrDenominator();
			this.inDegree.incr(0);
			this.inDegree.incrDenominator();
			this.outDegree.incr(0);
			this.outDegree.incrDenominator();

		} else if (u instanceof NodeRemoval) {

			DirectedNode n = (DirectedNode) ((NodeRemoval) u).getNode();

			this.nodes--;
			this.edges -= n.getDegree();
			this.degree.decr(n.getDegree());
			this.degree.decrDenominator();
			this.inDegree.decr(n.getInDegree());
			this.inDegree.decrDenominator();
			this.outDegree.decr(n.getOutDegree());
			this.outDegree.decrDenominator();

			// change counts for outgoing edges
			for (IElement outUncasted : n.getOutgoingEdges()) {
				DirectedEdge out = (DirectedEdge) outUncasted;
				if (n.hasNeighbor(out.getDst())) {
					continue;
				}
				this.degree.decr(out.getDst().getDegree());
				this.inDegree.decr(out.getDst().getInDegree());
				this.degree.incr(out.getDst().getDegree() - 1);
				this.inDegree.incr(out.getDst().getInDegree() - 1);
			}

			// / change count for incoming edges
			for (IElement inUncasted : n.getIncomingEdges()) {
				DirectedEdge in = (DirectedEdge) inUncasted;
				if (n.hasNeighbor(in.getSrc())) {
					continue;
				}
				this.degree.decr(in.getSrc().getDegree());
				this.outDegree.decr(in.getSrc().getOutDegree());
				this.degree.incr(in.getSrc().getDegree() - 1);
				this.outDegree.incr(in.getSrc().getOutDegree() - 1);
			}

			// change count for neighbors
			for (IElement neighborUncasted : n.getNeighbors()) {
				DirectedNode neighbor = (DirectedNode) neighborUncasted;
				this.degree.decr(neighbor.getDegree());
				this.inDegree.decr(neighbor.getInDegree());
				this.outDegree.decr(neighbor.getOutDegree());
				this.degree.incr(neighbor.getDegree() - 2);
				this.inDegree.incr(neighbor.getInDegree() - 1);
				this.outDegree.incr(neighbor.getOutDegree() - 1);
			}

			// TRUNCATE
			this.degree.truncate();
			this.inDegree.truncate();
			this.outDegree.truncate();

		} else if (u instanceof EdgeAddition || u instanceof EdgeRemoval) {

			DirectedEdge e = (DirectedEdge) ((EdgeUpdate) u).getEdge();

			int change = 1;
			if (u instanceof EdgeRemoval) {
				change = -1;
			}

			this.edges += change;

			// COUNT src
			this.degree.decr(e.getSrc().getDegree());
			this.outDegree.decr(e.getSrc().getOutDegree());
			this.degree.incr(e.getSrc().getDegree() + change);
			this.outDegree.incr(e.getSrc().getOutDegree() + change);

			// COUNT dst
			this.degree.decr(e.getDst().getDegree());
			this.inDegree.decr(e.getDst().getInDegree());
			this.degree.incr(e.getDst().getDegree() + change);
			this.inDegree.incr(e.getDst().getInDegree() + change);

			// TRUNCATE
			if (u instanceof EdgeRemoval) {
				this.degree.truncate();
				this.inDegree.truncate();
				this.outDegree.truncate();
			}

		}

		return true;
	}

	private boolean applyBeforeUpdateUndirected(Update u) {
		if (u instanceof NodeAddition) {

			this.nodes++;
			this.degree.incr(0);

			// UPDATE distributions
			this.degree.incrDenominator();

			// TRUNCATE
			this.degree.truncate();

		} else if (u instanceof NodeRemoval) {

			UndirectedNode n = (UndirectedNode) ((NodeRemoval) u).getNode();

			this.nodes--;
			this.edges -= n.getDegree();
			this.degree.decr(n.getDegree());

			for (IElement eUncasted : n.getEdges()) {
				UndirectedEdge e = (UndirectedEdge) eUncasted;
				UndirectedNode node = e.getNode1();
				if (n.equals(node)) {
					node = e.getNode2();
				}
				this.degree.decr(node.getDegree());
				this.degree.incr(node.getDegree() - 1);
			}

		} else if (u instanceof EdgeAddition || u instanceof EdgeRemoval) {

			UndirectedEdge e = (UndirectedEdge) ((EdgeUpdate) u).getEdge();

			int change = 1;
			if (u instanceof EdgeRemoval) {
				change = -1;
			}

			this.edges += change;

			// NODE1
			this.degree.decr(e.getNode1().getDegree());
			this.degree.incr(e.getNode1().getDegree() + change);

			// NODE2
			this.degree.decr(e.getNode2().getDegree());
			this.degree.incr(e.getNode2().getDegree() + change);

			// TRUNCATE
			if (u instanceof EdgeRemoval) {
				this.degree.truncate();
			}

		}

		return true;
	}

	@Override
	public boolean applyAfterUpdate(Update u) {
		return false;
	}

	@Override
	public boolean compute() {
		this.nodes = this.g.getNodeCount();
		this.edges = this.g.getEdgeCount();
		if (DirectedNode.class.isAssignableFrom(this.g.getGraphDatastructures()
				.getNodeType())) {
			for (IElement nUncasted : this.g.getNodes()) {
				DirectedNode n = (DirectedNode) nUncasted;
				this.degree.incr(n.getDegree());
				this.inDegree.incr(n.getInDegree());
				this.outDegree.incr(n.getOutDegree());
			}
			this.finalizeComputation();
			return true;
		} else if (UndirectedNode.class.isAssignableFrom(this.g
				.getGraphDatastructures().getNodeType())) {
			for (IElement nUncasted : this.g.getNodes()) {
				UndirectedNode n = (UndirectedNode) nUncasted;
				this.degree.incr(n.getDegree());
			}
			this.finalizeComputation();
			return true;
		}
		Log.error("DD - unsupported node type "
				+ this.g.getGraphDatastructures().getNodeType());
		return false;
	}

	public boolean finalizeComputation() {
		return true;
	}

	@Override
	public boolean isApplicable(Graph g) {
		return DirectedNode.class.isAssignableFrom(g.getGraphDatastructures()
				.getNodeType())
				|| UndirectedNode.class.isAssignableFrom(g
						.getGraphDatastructures().getNodeType());
	}

	@Override
	public boolean isApplicable(Batch b) {
		return DirectedNode.class.isAssignableFrom(b.getGraphDatastructures()
				.getNodeType())
				|| UndirectedNode.class.isAssignableFrom(b
						.getGraphDatastructures().getNodeType());
	}

	// private void decrCounts(DirectedNode n, int offset) {
	// this.decrDegreeCount(n, offset);
	// this.decrInDegreeCount(n, offset);
	// this.decrOutDegreeCount(n, offset);
	// }
	//
	// private void decrDegreeCount(DirectedNode n, int offset) {
	// this.degreeCount = ArrayUtils.decr(this.degreeCount, n.getDegree()
	// + offset);
	// }
	//
	// private void decrInDegreeCount(DirectedNode n, int offset) {
	// this.inDegreeCount = ArrayUtils.decr(this.inDegreeCount,
	// n.getInDegree() + offset);
	// }
	//
	// private void decrOutDegreeCount(DirectedNode n, int offset) {
	// this.outDegreeCount = ArrayUtils.decr(this.outDegreeCount,
	// n.getOutDegree() + offset);
	// }

	// private void incrCounts(DirectedNode n, int offset) {
	// this.incrDegreeCount(n, offset);
	// this.incrInDegreeCount(n, offset);
	// this.incrOutDegreeCount(n, offset);
	// }
	//
	// private void incrDegreeCount(DirectedNode n, int offset) {
	// this.degreeCount = ArrayUtils.incr(this.degreeCount, n.getDegree()
	// + offset);
	// }
	//
	// private void incrInDegreeCount(DirectedNode n, int offset) {
	// this.inDegreeCount = ArrayUtils.incr(this.inDegreeCount,
	// n.getInDegree() + offset);
	// }
	//
	// private void incrOutDegreeCount(DirectedNode n, int offset) {
	// this.outDegreeCount = ArrayUtils.incr(this.outDegreeCount,
	// n.getOutDegree() + offset);
	// }

	// private void updateDegreeDistribution(DirectedNode n, int offset) {
	// this.degreeDistribution = ArrayUtils.set(this.degreeDistribution,
	// n.getDegree(), this.degreeCount[n.getDegree()] / this.nodes, 0);
	// this.degreeDistribution = ArrayUtils.set(this.degreeDistribution,
	// n.getDegree() + offset,
	// this.degreeCount[n.getDegree() + offset] / this.nodes, 0);
	// }
	//
	// private void updateInDegreeDistribution(DirectedNode n, int offset) {
	// this.inDegreeDistribution = ArrayUtils.set(this.inDegreeDistribution,
	// n.getInDegree() + offset, this.inDegreeCount[n.getInDegree()
	// + offset]
	// / this.nodes, 0);
	// this.inDegreeDistribution = ArrayUtils.set(this.inDegreeDistribution,
	// n.getInDegree(), this.inDegreeCount[n.getInDegree()]
	// / this.nodes, 0);
	// }
	//
	// private void updateOutDegreeDistribution(DirectedNode n, int offset) {
	// this.outDegreeDistribution = ArrayUtils.set(this.outDegreeDistribution,
	// n.getOutDegree() + offset, this.outDegreeCount[n.getOutDegree()
	// + offset]
	// / this.nodes, 0);
	// this.outDegreeDistribution = ArrayUtils.set(this.outDegreeDistribution,
	// n.getOutDegree(), this.outDegreeCount[n.getOutDegree()]
	// / this.nodes, 0);
	// }

	// private void decrDegreeCount(UndirectedNode n, int offset) {
	// this.degreeCount = ArrayUtils.decr(this.degreeCount, n.getDegree()
	// + offset);
	// }
	//
	// private void incrDegreeCount(UndirectedNode n, int offset) {
	// this.degreeCount = ArrayUtils.incr(this.degreeCount, n.getDegree()
	// + offset);
	// }

	// private void updateDegreeDistribution(UndirectedNode n, int offset) {
	// this.degreeDistribution = ArrayUtils.set(this.degreeDistribution,
	// n.getDegree(), this.degreeCount[n.getDegree()] / this.nodes, 0);
	// this.degreeDistribution = ArrayUtils.set(this.degreeDistribution,
	// n.getDegree() + offset,
	// this.degreeCount[n.getDegree() + offset] / this.nodes, 0);
	// }
	//
	// private void truncateAll() {
	// this.truncateDegree();
	// this.inDegreeDistribution = ArrayUtils.truncate(
	// this.inDegreeDistribution, 0);
	// this.inDegreeCount = ArrayUtils.truncate(this.inDegreeCount, 0);
	// this.outDegreeDistribution = ArrayUtils.truncate(
	// this.outDegreeDistribution, 0);
	// this.outDegreeCount = ArrayUtils.truncate(this.outDegreeCount, 0);
	// }

	// private void truncateDegree() {
	// this.degreeDistribution = ArrayUtils.truncate(this.degreeDistribution,
	// 0);
	// this.degreeCount = ArrayUtils.truncate(this.degreeCount, 0);
	// }

}
