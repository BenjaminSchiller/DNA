package dna.metrics.parallelization.partitioning;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import dna.graph.Graph;
import dna.graph.edges.Edge;
import dna.graph.nodes.Node;
import dna.metrics.Metric;
import dna.metrics.clustering.UndirectedClusteringCoefficientR;
import dna.metrics.clustering.UndirectedClusteringCoefficientU;
import dna.metrics.parallelization.collation.clustering.PartitionedUndirectedClusteringCoefficientR;
import dna.series.data.Value;
import dna.updates.update.EdgeAddition;
import dna.updates.update.EdgeRemoval;
import dna.updates.update.NodeAddition;
import dna.updates.update.NodeRemoval;
import dna.util.Timer;

/**
 * 
 * This abstrsct class represents one partition of a graph, i.e., a subgraph
 * based on a set of nodes and the partitioning type (overlapping or
 * non-overlapping).
 * 
 * Each partition provides the induced sub-graph (new objects), the initial set
 * of nodes (objects from the original graph), as well as a metric that should
 * be computed on the partition.
 * 
 * @author benni
 *
 */
public abstract class Partition {

	protected Graph g;

	protected List<Node> nodes;
	protected Set<Node> nodeSet;

	protected Metric m;

	protected Timer t;

	public Timer getTimer() {
		return this.t;
	}

	public Partition(Graph g, List<Node> nodes, Metric m) {
		this.g = g;
		this.nodes = nodes;
		this.m = m;
		m.setGraph(g);
		this.nodeSet = new HashSet<Node>(nodes);
		this.t = new Timer();
	}

	public Graph getGraph() {
		return this.g;
	}

	public List<Node> getNodes() {
		return this.nodes;
	}

	public Metric getMetric() {
		return this.m;
	}

	public Value[] getValues() {
		Value nodes = new Value("nodes", this.g.getNodeCount());
		Value edges = new Value("edges", this.g.getEdgeCount());
		Value duration = new Value("duration",
				(double) this.t.getDutation() / 1000000.0);
		Value[] general = new Value[] { nodes, edges, duration };

		Value[] stats = this.getStatistics();

		Value[] values = new Value[stats.length + general.length];
		System.arraycopy(general, 0, values, 0, general.length);
		System.arraycopy(stats, 0, values, general.length, stats.length);

		return values;
	}

	protected abstract Value[] getStatistics();

	public boolean isResponsibleFor(Node n) {
		return this.nodeSet.contains(n);
	}

	public static Metric clone(Metric m) {
		if (m instanceof UndirectedClusteringCoefficientR) {
			return (Metric) ((UndirectedClusteringCoefficientR) m).clone();
		} else if (m instanceof PartitionedUndirectedClusteringCoefficientR) {
			return (Metric) ((PartitionedUndirectedClusteringCoefficientR) m)
					.clone();
		} else if (m instanceof UndirectedClusteringCoefficientU) {
			return (Metric) ((UndirectedClusteringCoefficientU) m).clone();
		}
		throw new IllegalArgumentException("uncloneable metric: "
				+ m.getDescription());
	}

	/*
	 * NA
	 */

	public boolean propagate(NodeAddition na) {
		boolean success = true;
		NodeAddition na_ = new NodeAddition(this.g.getGraphDatastructures()
				.newNodeInstance(na.getNode().asString()));
		success &= this.nodes.add((Node) na_.getNode());
		success &= this.nodeSet.add((Node) na_.getNode());
		success &= na_.apply(this.g);
		return success;
	}

	/*
	 * NR
	 */

	public boolean propagate(NodeRemoval nr) {
		boolean success = true;
		success &= this.nodes.remove((Node) nr.getNode());
		success &= this.nodeSet.remove((Node) nr.getNode());
		success &= nr.apply(this.g);
		return success;
	}

	/*
	 * EA
	 */

	public boolean shouldPropagate(EdgeAddition ea) {
		return this.g.containsNode(ea.getEdge().getN1())
				|| this.g.containsNode(ea.getEdge().getN2());
	}

	public boolean shouldApply(EdgeAddition ea) {
		return this.g.containsNode(ea.getEdge().getN1())
				&& this.g.containsNode(ea.getEdge().getN2());
	}

	public abstract boolean propagate(EdgeAddition ea);

	/*
	 * ER
	 */

	public boolean shouldPropagate(EdgeRemoval er) {
		return this.g.containsNode(er.getEdge().getN1())
				|| this.g.containsNode(er.getEdge().getN2());
	}

	public boolean shouldApply(EdgeRemoval er) {
		return this.g.containsNode(er.getEdge().getN1())
				&& this.g.containsNode(er.getEdge().getN2());
	}

	public abstract boolean propagate(EdgeRemoval er);

	/*
	 * UPDATES
	 */

	protected EdgeAddition localEA = null;
	protected EdgeRemoval localER = null;

	/**
	 * resets the local updates for EA. this method must be called after an
	 * update has been fully processed. otherwise, the stored (old) updates are
	 * returned instead of copying a new one.
	 */
	public void clearLocalEA() {
		this.localEA = null;
	}

	/**
	 * resets the local updates for ER. this method must be called after an
	 * update has been fully processed. otherwise, the stored (old) updates are
	 * returned instead of copying a new one.
	 */
	public void clearLocalER() {
		this.localER = null;
	}

	/**
	 * 
	 * in case a new one is requested, it is generated for the local graph using
	 * its nodes and gds. the local update has to be reset after each update.
	 * 
	 * @param ea
	 *            update applied to the global (complete) graph
	 * @return update for the local graph based on the input update from the
	 *         global graph
	 */
	public EdgeAddition getLocalEA(EdgeAddition ea) {
		if (localEA != null) {
			return localEA;
		}
		Node n1 = this.g.getNode(ea.getEdge().getN1().getIndex());
		Node n2 = this.g.getNode(ea.getEdge().getN2().getIndex());
		Edge e = this.g.getGraphDatastructures().newEdgeInstance(n1, n2);
		localEA = new EdgeAddition(e);
		return localEA;
	}

	/**
	 * 
	 * in case a new one is requested, it is generated for the local graph using
	 * its nodes and gds. the local update has to be reset after each update.
	 * 
	 * @param er
	 *            update applied to the global (complete) graph
	 * @return update for the local graph based on the input update from the
	 *         global graph
	 */
	public EdgeRemoval getLocalER(EdgeRemoval er) {
		if (localER != null) {
			return localER;
		}
		Edge e = this.g.getEdge(er.getEdge().getN1(), er.getEdge().getN2());
		localER = new EdgeRemoval(e);
		return localER;
	}

}
