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
			if (u instanceof NodeAddition) {

				this.nodes++;

				this.degreeCount = ArrayUtils.incr(this.degreeCount, 0);
				this.inDegreeCount = ArrayUtils.incr(this.inDegreeCount, 0);
				this.outDegreeCount = ArrayUtils.incr(this.outDegreeCount, 0);

				// UPDATE distributions
				this.updateDistribution(this.degreeDistribution,
						this.nodes - 1, this.nodes);
				this.updateDistribution(this.inDegreeDistribution,
						this.nodes - 1, this.nodes);
				this.updateDistribution(this.outDegreeDistribution,
						this.nodes - 1, this.nodes);

				// UPDATE values
				this.degreeDistribution[0] = (double) this.degreeCount[0]
						/ this.nodes;
				this.inDegreeDistribution[0] = (double) this.inDegreeCount[0]
						/ this.nodes;
				this.outDegreeDistribution[0] = (double) this.outDegreeCount[0]
						/ this.nodes;

			} else if (u instanceof NodeRemoval) {
				this.nodes--;

				// TODO node removal update for DD (directed)

			} else if (u instanceof EdgeAddition || u instanceof EdgeRemoval) {

				int change = 1;
				if (u instanceof EdgeRemoval) {
					change = -1;
				}

				this.edges += change;

				DirectedEdge e = (DirectedEdge) ((EdgeUpdate) u).getEdge();

				// DECR src
				this.degreeCount = ArrayUtils.decr(this.degreeCount, e.getSrc()
						.getDegree());
				this.outDegreeCount = ArrayUtils.decr(this.outDegreeCount, e
						.getSrc().getOutDegree());
				// DECR dst
				this.degreeCount = ArrayUtils.decr(this.degreeCount, e.getDst()
						.getDegree());
				this.inDegreeCount = ArrayUtils.decr(this.inDegreeCount, e
						.getDst().getInDegree());

				// INCR src
				this.degreeCount = ArrayUtils.incr(this.degreeCount, e.getSrc()
						.getDegree() + change);
				this.outDegreeCount = ArrayUtils.incr(this.outDegreeCount, e
						.getSrc().getOutDegree() + change);
				// INCR dst
				this.degreeCount = ArrayUtils.incr(this.degreeCount, e.getDst()
						.getDegree() + change);
				this.inDegreeCount = ArrayUtils.incr(this.inDegreeCount, e
						.getDst().getInDegree() + change);

				// UPDATE values SRC
				this.degreeDistribution = ArrayUtils.set(
						this.degreeDistribution, e.getSrc().getDegree(),
						this.degreeCount[e.getSrc().getDegree()] / this.nodes);
				this.degreeDistribution = ArrayUtils.set(
						this.degreeDistribution, e.getSrc().getDegree()
								+ change, this.degreeCount[e.getSrc()
								.getDegree() + change]
								/ this.nodes);
				this.outDegreeDistribution = ArrayUtils.set(
						this.outDegreeDistribution, e.getSrc().getOutDegree(),
						this.outDegreeCount[e.getSrc().getOutDegree()]
								/ this.nodes);
				this.outDegreeDistribution = ArrayUtils.set(
						this.outDegreeDistribution, e.getSrc().getOutDegree()
								+ change, this.outDegreeCount[e.getSrc()
								.getOutDegree() + change]
								/ this.nodes);

				// UPDATE values DST

				this.degreeDistribution = ArrayUtils.set(
						this.degreeDistribution, e.getDst().getDegree(),
						this.degreeCount[e.getDst().getDegree()] / this.nodes);
				this.degreeDistribution = ArrayUtils.set(
						this.degreeDistribution, e.getDst().getDegree()
								+ change, this.degreeCount[e.getDst()
								.getDegree() + change]
								/ this.nodes);
				this.inDegreeDistribution = ArrayUtils.set(
						this.inDegreeDistribution, e.getDst().getInDegree(),
						this.inDegreeCount[e.getDst().getInDegree()]
								/ this.nodes);
				this.inDegreeDistribution = ArrayUtils.set(
						this.inDegreeDistribution, e.getDst().getInDegree()
								+ change, this.inDegreeCount[e.getDst()
								.getInDegree() + change]
								/ this.nodes);

				this.degreeDistribution = ArrayUtils.truncate(
						this.degreeDistribution, 0);
				this.inDegreeDistribution = ArrayUtils.truncate(
						this.inDegreeDistribution, 0);
				this.outDegreeDistribution = ArrayUtils.truncate(
						this.outDegreeDistribution, 0);

				this.degreeCount = ArrayUtils.truncate(this.degreeCount, 0);
				this.inDegreeCount = ArrayUtils.truncate(this.inDegreeCount, 0);
				this.outDegreeCount = ArrayUtils.truncate(this.outDegreeCount,
						0);

			}

			return true;

		} else if (UndirectedNode.class.isAssignableFrom(this.g
				.getGraphDatastructures().getNodeType())) {
			if (u instanceof NodeAddition) {

				this.nodes++;

				this.degreeCount = ArrayUtils.incr(this.degreeCount, 0);

				// UPDATE distributions
				this.updateDistribution(this.degreeDistribution,
						this.nodes - 1, this.nodes);

				// UPDATE values
				this.degreeDistribution[0] = (double) this.degreeCount[0]
						/ this.nodes;

			} else if (u instanceof NodeRemoval) {

				this.nodes--;

				// TODO remove node update for DD (undirected)

			} else if (u instanceof EdgeAddition || u instanceof EdgeRemoval) {

				int change = 1;
				if (u instanceof EdgeRemoval) {
					change = -1;
				}

				this.edges += change;

				UndirectedEdge e = (UndirectedEdge) ((EdgeUpdate) u).getEdge();

				// DECR node1
				this.degreeCount = ArrayUtils.decr(this.degreeCount, e
						.getNode1().getDegree());
				// DECR node2
				this.degreeCount = ArrayUtils.decr(this.degreeCount, e
						.getNode2().getDegree());

				// INCR node1
				this.degreeCount = ArrayUtils.incr(this.degreeCount, e
						.getNode1().getDegree() + change);
				// INCR node2
				this.degreeCount = ArrayUtils.incr(this.degreeCount, e
						.getNode2().getDegree() + change);

				// UPDATE values node1
				this.degreeDistribution = ArrayUtils
						.set(this.degreeDistribution, e.getNode1().getDegree(),
								this.degreeCount[e.getNode1().getDegree()]
										/ this.nodes);
				this.degreeDistribution = ArrayUtils.set(
						this.degreeDistribution, e.getNode1().getDegree()
								+ change, this.degreeCount[e.getNode1()
								.getDegree() + change]
								/ this.nodes);

				// UPDATE values node2
				this.degreeDistribution = ArrayUtils
						.set(this.degreeDistribution, e.getNode2().getDegree(),
								this.degreeCount[e.getNode2().getDegree()]
										/ this.nodes);
				this.degreeDistribution = ArrayUtils.set(
						this.degreeDistribution, e.getNode2().getDegree()
								+ change, this.degreeCount[e.getNode2()
								.getDegree() + change]
								/ this.nodes);

				this.degreeDistribution = ArrayUtils.truncate(
						this.degreeDistribution, 0);

				this.degreeCount = ArrayUtils.truncate(this.degreeCount, 0);

			}

			return true;
		}
		Log.error("DD - unsupported node type "
				+ this.g.getGraphDatastructures().getNodeType());
		return false;
	}

	private void updateDistribution(double[] distr, double oldNodes,
			double newNodes) {
		for (int i = 0; i < distr.length; i++) {
			distr[i] = distr[i] * oldNodes / newNodes;
		}
	}

	@Override
	public boolean applyAfterUpdate(Update u) {
		return false;
	}

	@Override
	public boolean cleanup() {
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
			this.cleanup();
			return true;
		} else if (UndirectedNode.class.isAssignableFrom(this.g
				.getGraphDatastructures().getNodeType())) {
			for (UndirectedNode n : (Collection<UndirectedNode>) this.g
					.getNodes()) {
				this.degreeCount = ArrayUtils.incr(this.degreeCount,
						n.getDegree());
			}
			this.cleanup();
			return true;
		}
		Log.error("DD - unsupported node type "
				+ this.g.getGraphDatastructures().getNodeType());
		return false;
	}

	@Override
	public boolean recompute() {
		return this.compute();
	}

	@Override
	protected void init_(Graph g) {
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

}
