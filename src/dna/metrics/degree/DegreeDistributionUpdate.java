package dna.metrics.degree;

import java.util.Collection;

import dna.graph.Graph;
import dna.graph.directed.DirectedEdge;
import dna.graph.directed.DirectedNode;
import dna.graph.undirected.UndirectedEdge;
import dna.graph.undirected.UndirectedNode;
import dna.updates.Batch;
import dna.updates.EdgeAddition;
import dna.updates.EdgeRemoval;
import dna.updates.EdgeUpdate;
import dna.updates.NodeAddition;
import dna.updates.NodeRemoval;
import dna.updates.Update;
import dna.util.ArrayUtils;
import dna.util.Log;

@SuppressWarnings("rawtypes")
public class DegreeDistributionUpdate extends DegreeDistribution {

	public DegreeDistributionUpdate() {
		super("degreeDistributionUpdate", ApplicationType.BeforeUpdate);
	}

	private int[] degreeCount;

	private int[] inDegreeCount;

	private int[] outDegreeCount;

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

			DirectedNode n = (DirectedNode) ((NodeAddition) u).getNode();

			this.nodes++;
			this.incrCounts(n, 0);
			this.updateDistributions();

		} else if (u instanceof NodeRemoval) {

			DirectedNode n = (DirectedNode) ((NodeRemoval) u).getNode();

			this.nodes--;
			this.edges -= n.getDegree();
			this.decrCounts(n, 0);

			// change counts for outgoing edges
			for (DirectedEdge out : n.getOutgoingEdges()) {
				if (n.hasNeighbor(out.getDst())) {
					continue;
				}
				this.decrDegreeCount(out.getDst(), 0);
				this.decrInDegreeCount(out.getDst(), 0);
				this.incrDegreeCount(out.getDst(), -1);
				this.incrInDegreeCount(out.getDst(), -1);
			}

			// / change count for incoming edges
			for (DirectedEdge in : n.getIncomingEdges()) {
				if (n.hasNeighbor(in.getSrc())) {
					continue;
				}
				this.decrDegreeCount(in.getSrc(), 0);
				this.decrOutDegreeCount(in.getSrc(), 0);
				this.incrDegreeCount(in.getSrc(), -1);
				this.incrOutDegreeCount(in.getSrc(), -1);
			}

			// change count for neighbors
			for (DirectedNode neighbor : n.getNeighbors()) {
				this.decrCounts(neighbor, 0);
				this.incrDegreeCount(neighbor, -2);
				this.incrInDegreeCount(neighbor, -1);
				this.incrOutDegreeCount(neighbor, -1);
			}

			this.updateDistributions();

		} else if (u instanceof EdgeAddition || u instanceof EdgeRemoval) {

			DirectedEdge e = (DirectedEdge) ((EdgeUpdate) u).getEdge();

			int change = 1;
			if (u instanceof EdgeRemoval) {
				change = -1;
			}

			this.edges += change;

			// COUNT src
			this.decrDegreeCount(e.getSrc(), 0);
			this.decrOutDegreeCount(e.getSrc(), 0);
			this.incrDegreeCount(e.getSrc(), change);
			this.incrOutDegreeCount(e.getSrc(), change);

			// COUNT dst
			this.decrDegreeCount(e.getDst(), 0);
			this.decrInDegreeCount(e.getDst(), 0);
			this.incrDegreeCount(e.getDst(), change);
			this.incrInDegreeCount(e.getDst(), change);

			// DISTRIBUTION src
			this.updateDegreeDistribution(e.getSrc(), change);
			this.updateOutDegreeDistribution(e.getSrc(), change);

			// DISTRIBUTION dst
			this.updateDegreeDistribution(e.getDst(), change);
			this.updateInDegreeDistribution(e.getDst(), change);

			// TRUNCATE
			this.truncateAll();

		}

		return true;
	}

	private boolean applyBeforeUpdateUndirected(Update u) {
		if (u instanceof NodeAddition) {

			UndirectedNode n = (UndirectedNode) ((NodeAddition) u).getNode();

			this.nodes++;
			this.incrDegreeCount(n, 0);

			// UPDATE distributions
			this.updateDistribution(this.degreeDistribution, this.degreeCount,
					this.nodes);

		} else if (u instanceof NodeRemoval) {

			UndirectedNode n = (UndirectedNode) ((NodeRemoval) u).getNode();

			this.nodes--;
			this.edges -= n.getDegree();
			this.decrDegreeCount(n, 0);

			for (UndirectedEdge e : n.getEdges()) {
				UndirectedNode node = e.getNode1();
				if (n.equals(node)) {
					node = e.getNode2();
				}
				this.decrDegreeCount(node, 0);
				this.incrDegreeCount(node, -1);
			}

			this.updateDegreeDistribution();

		} else if (u instanceof EdgeAddition || u instanceof EdgeRemoval) {

			UndirectedEdge e = (UndirectedEdge) ((EdgeUpdate) u).getEdge();

			int change = 1;
			if (u instanceof EdgeRemoval) {
				change = -1;
			}

			this.edges += change;

			// NODE1
			this.decrDegreeCount(e.getNode1(), 0);
			this.incrDegreeCount(e.getNode1(), change);

			// NODE2
			this.decrDegreeCount(e.getNode2(), 0);
			this.incrDegreeCount(e.getNode2(), change);

			// UPDATE
			this.updateDegreeDistribution(e.getNode1(), change);
			this.updateDegreeDistribution(e.getNode2(), change);

			// TRUNCATE
			this.truncateDegree();

		}

		return true;
	}

	@Override
	public boolean applyAfterUpdate(Update u) {
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean compute() {
		this.nodes = this.g.getNodeCount();
		this.edges = this.g.getEdgeCount();
		if (DirectedNode.class.isAssignableFrom(this.g.getGraphDatastructures()
				.getNodeType())) {
			for (DirectedNode n : (Collection<DirectedNode>) this.g.getNodes()) {
				this.degreeCount = ArrayUtils.incr(this.degreeCount,
						n.getDegree());
				this.inDegreeCount = ArrayUtils.incr(this.inDegreeCount,
						n.getInDegree());
				this.outDegreeCount = ArrayUtils.incr(this.outDegreeCount,
						n.getOutDegree());
			}
			this.finalizeComputation();
			return true;
		} else if (UndirectedNode.class.isAssignableFrom(this.g
				.getGraphDatastructures().getNodeType())) {
			for (UndirectedNode n : (Collection<UndirectedNode>) this.g
					.getNodes()) {
				this.degreeCount = ArrayUtils.incr(this.degreeCount,
						n.getDegree());
			}
			this.finalizeComputation();
			return true;
		}
		Log.error("DD - unsupported node type "
				+ this.g.getGraphDatastructures().getNodeType());
		return false;
	}

	public boolean finalizeComputation() {
		this.degreeDistribution = new double[this.degreeCount.length];
		this.inDegreeDistribution = new double[this.inDegreeCount.length];
		this.outDegreeDistribution = new double[this.outDegreeCount.length];

		this.fill(this.degreeCount, this.degreeDistribution, this.nodes);
		this.fill(this.inDegreeCount, this.inDegreeDistribution, this.nodes);
		this.fill(this.outDegreeCount, this.outDegreeDistribution, this.nodes);

		return true;
	}

	private void fill(int[] src, double[] dst, double divideBy) {
		for (int i = 0; i < src.length; i++) {
			dst[i] = (double) src[i] / divideBy;
		}
	}

	@Override
	protected void init_() {
		this.degreeCount = new int[0];
		this.inDegreeCount = new int[0];
		this.outDegreeCount = new int[0];
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

	private void updateDistributions() {
		this.degreeCount = ArrayUtils.truncate(this.degreeCount, 0);
		if (this.degreeCount.length != this.degreeDistribution.length) {
			this.degreeDistribution = new double[this.degreeCount.length];
		}
		this.updateDistribution(this.degreeDistribution, this.degreeCount,
				this.nodes);

		this.inDegreeCount = ArrayUtils.truncate(this.inDegreeCount, 0);
		if (this.inDegreeCount.length != this.inDegreeDistribution.length) {
			this.inDegreeDistribution = new double[this.inDegreeCount.length];
		}
		this.updateDistribution(this.inDegreeDistribution, this.inDegreeCount,
				this.nodes);

		this.outDegreeCount = ArrayUtils.truncate(this.outDegreeCount, 0);
		if (this.outDegreeCount.length != this.outDegreeDistribution.length) {
			this.outDegreeDistribution = new double[this.outDegreeCount.length];
		}
		this.updateDistribution(this.outDegreeDistribution,
				this.outDegreeCount, this.nodes);
	}

	private void updateDegreeDistribution() {
		this.degreeCount = ArrayUtils.truncate(this.degreeCount, 0);
		if (this.degreeCount.length != this.degreeDistribution.length) {
			this.degreeDistribution = new double[this.degreeCount.length];
		}
		this.updateDistribution(this.degreeDistribution, this.degreeCount,
				this.nodes);
	}

	private void updateDistribution(double[] distribution, int[] counts,
			double nodes) {
		for (int i = 0; i < counts.length; i++) {
			distribution[i] = (double) counts[i] / nodes;
		}
	}

	private void decrCounts(DirectedNode n, int offset) {
		this.decrDegreeCount(n, offset);
		this.decrInDegreeCount(n, offset);
		this.decrOutDegreeCount(n, offset);
	}

	private void decrDegreeCount(DirectedNode n, int offset) {
		this.degreeCount = ArrayUtils.decr(this.degreeCount, n.getDegree()
				+ offset);
	}

	private void decrInDegreeCount(DirectedNode n, int offset) {
		this.inDegreeCount = ArrayUtils.decr(this.inDegreeCount,
				n.getInDegree() + offset);
	}

	private void decrOutDegreeCount(DirectedNode n, int offset) {
		this.outDegreeCount = ArrayUtils.decr(this.outDegreeCount,
				n.getOutDegree() + offset);
	}

	private void incrCounts(DirectedNode n, int offset) {
		this.incrDegreeCount(n, offset);
		this.incrInDegreeCount(n, offset);
		this.incrOutDegreeCount(n, offset);
	}

	private void incrDegreeCount(DirectedNode n, int offset) {
		this.degreeCount = ArrayUtils.incr(this.degreeCount, n.getDegree()
				+ offset);
	}

	private void incrInDegreeCount(DirectedNode n, int offset) {
		this.inDegreeCount = ArrayUtils.incr(this.inDegreeCount,
				n.getInDegree() + offset);
	}

	private void incrOutDegreeCount(DirectedNode n, int offset) {
		this.outDegreeCount = ArrayUtils.incr(this.outDegreeCount,
				n.getOutDegree() + offset);
	}

	private void updateDegreeDistribution(DirectedNode n, int offset) {
		this.degreeDistribution = ArrayUtils.set(this.degreeDistribution,
				n.getDegree(), this.degreeCount[n.getDegree()] / this.nodes, 0);
		this.degreeDistribution = ArrayUtils.set(this.degreeDistribution,
				n.getDegree() + offset,
				this.degreeCount[n.getDegree() + offset] / this.nodes, 0);
	}

	private void updateInDegreeDistribution(DirectedNode n, int offset) {
		this.inDegreeDistribution = ArrayUtils.set(this.inDegreeDistribution,
				n.getInDegree() + offset, this.inDegreeCount[n.getInDegree()
						+ offset]
						/ this.nodes, 0);
		this.inDegreeDistribution = ArrayUtils.set(this.inDegreeDistribution,
				n.getInDegree(), this.inDegreeCount[n.getInDegree()]
						/ this.nodes, 0);
	}

	private void updateOutDegreeDistribution(DirectedNode n, int offset) {
		this.outDegreeDistribution = ArrayUtils.set(this.outDegreeDistribution,
				n.getOutDegree() + offset, this.outDegreeCount[n.getOutDegree()
						+ offset]
						/ this.nodes, 0);
		this.outDegreeDistribution = ArrayUtils.set(this.outDegreeDistribution,
				n.getOutDegree(), this.outDegreeCount[n.getOutDegree()]
						/ this.nodes, 0);
	}

	private void decrDegreeCount(UndirectedNode n, int offset) {
		this.degreeCount = ArrayUtils.decr(this.degreeCount, n.getDegree()
				+ offset);
	}

	private void incrDegreeCount(UndirectedNode n, int offset) {
		this.degreeCount = ArrayUtils.incr(this.degreeCount, n.getDegree()
				+ offset);
	}

	private void updateDegreeDistribution(UndirectedNode n, int offset) {
		this.degreeDistribution = ArrayUtils.set(this.degreeDistribution,
				n.getDegree(), this.degreeCount[n.getDegree()] / this.nodes, 0);
		this.degreeDistribution = ArrayUtils.set(this.degreeDistribution,
				n.getDegree() + offset,
				this.degreeCount[n.getDegree() + offset] / this.nodes, 0);
	}

	private void truncateAll() {
		this.truncateDegree();
		this.inDegreeDistribution = ArrayUtils.truncate(
				this.inDegreeDistribution, 0);
		this.inDegreeCount = ArrayUtils.truncate(this.inDegreeCount, 0);
		this.outDegreeDistribution = ArrayUtils.truncate(
				this.outDegreeDistribution, 0);
		this.outDegreeCount = ArrayUtils.truncate(this.outDegreeCount, 0);
	}

	private void truncateDegree() {
		this.degreeDistribution = ArrayUtils.truncate(this.degreeDistribution,
				0);
		this.degreeCount = ArrayUtils.truncate(this.degreeCount, 0);
	}

}
