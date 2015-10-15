package dna.metrics.parallelization.partitioning;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import dna.graph.Graph;
import dna.graph.edges.Edge;
import dna.graph.nodes.Node;
import dna.metrics.Metric;
import dna.metrics.algorithms.IAfterEA;
import dna.metrics.algorithms.IAfterER;
import dna.metrics.algorithms.IAfterNA;
import dna.metrics.algorithms.IAfterNR;
import dna.metrics.algorithms.IBeforeEA;
import dna.metrics.algorithms.IBeforeER;
import dna.metrics.algorithms.IBeforeNA;
import dna.metrics.algorithms.IBeforeNR;
import dna.metrics.algorithms.IDynamicAlgorithm;
import dna.metrics.algorithms.IRecomputation;
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

	public Partition() {
	}

	public Partition(Graph g, List<Node> nodes, Metric m) {
		this.init(g, nodes, m);
	}

	public void init(Graph g, List<Node> nodes, Metric m) {
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
		Value duration = new Value("duration", (double) this.t.getDutation());
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
	 * HELPERS
	 */

	protected static String getName(int index) {
		return "p" + index;
	}

	protected static boolean removeConnectedEdges(Set<Edge> edges, Node node) {
		List<Edge> toRemove = new LinkedList<Edge>();
		for (Edge e : edges) {
			if (e.isConnectedTo(node)) {
				toRemove.add(e);
			}
		}
		boolean success = true;
		for (Edge e : toRemove) {
			success &= edges.remove(e);
		}
		return success;
	}

	/*
	 * INIT & RECOMPUTE & RESET
	 */

	public boolean init() {
		boolean success = true;

		if (this.m instanceof IDynamicAlgorithm) {
			success &= ((IDynamicAlgorithm) this.m).init();
		}

		return success;
	}

	public boolean recompute() {
		boolean success = true;

		if (this.m instanceof IRecomputation) {
			success &= ((IRecomputation) this.m).recompute();
		}

		return success;
	}

	public boolean reset() {
		return this.m.reset();
	}

	/*
	 * NA
	 */

	public abstract boolean propagate(NodeAddition globalNA);

	protected boolean apply(NodeAddition na) {
		boolean success = true;
		if (m instanceof IBeforeNA) {
			success &= ((IBeforeNA) m).applyBeforeUpdate(na);
		}
		success &= na.apply(g);
		if (m instanceof IAfterNA) {
			success &= ((IAfterNA) m).applyAfterUpdate(na);
		}
		return success;
	}

	/*
	 * NR
	 */

	public abstract boolean shouldPropagate(NodeRemoval globalNR);

	public abstract boolean propagate(NodeRemoval globalNR);

	protected boolean apply(NodeRemoval nr) {
		boolean success = true;
		if (this.m instanceof IBeforeNR) {
			success &= ((IBeforeNR) m).applyBeforeUpdate(nr);
		}
		success &= nr.apply(this.g);
		if (this.m instanceof IAfterNR) {
			success &= ((IAfterNR) m).applyAfterUpdate(nr);
		}
		return success;
	}

	/*
	 * EA
	 */

	public abstract boolean shouldPropagate(EdgeAddition globalEA);

	public abstract boolean propagate(EdgeAddition globalEA);

	protected boolean apply(EdgeAddition ea) {
		boolean success = true;
		if (m instanceof IBeforeEA) {
			success &= ((IBeforeEA) m).applyBeforeUpdate(ea);
		}
		success &= ea.apply(g);
		if (m instanceof IAfterEA) {
			success &= ((IAfterEA) m).applyAfterUpdate(ea);
		}
		return success;
	}

	/*
	 * ER
	 */

	public abstract boolean shouldPropagate(EdgeRemoval globalER);

	public abstract boolean propagate(EdgeRemoval globalER);

	protected boolean apply(EdgeRemoval er) {
		boolean success = true;
		if (m instanceof IBeforeER) {
			success &= ((IBeforeER) m).applyBeforeUpdate(er);
		}
		success &= er.apply(g);
		if (m instanceof IAfterER) {
			success &= ((IAfterER) m).applyAfterUpdate(er);
		}
		return success;
	}

}
