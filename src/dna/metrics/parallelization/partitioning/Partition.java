package dna.metrics.parallelization.partitioning;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import dna.graph.Graph;
import dna.graph.nodes.Node;
import dna.metrics.Metric;
import dna.metrics.clustering.UndirectedClusteringCoefficientR;
import dna.metrics.clustering.UndirectedClusteringCoefficientU;
import dna.metrics.parallelization.collation.clustering.PartitionedUndirectedClusteringCoefficientR;
import dna.updates.update.EdgeAddition;
import dna.updates.update.EdgeRemoval;
import dna.updates.update.NodeAddition;
import dna.updates.update.NodeRemoval;

public abstract class Partition {

	protected Graph g;

	protected List<Node> nodes;

	protected Set<Node> nodeSet;

	protected Metric m;

	public Partition(Graph g, List<Node> nodes, Metric m) {
		this.g = g;
		this.nodes = nodes;
		this.m = m;
		m.setGraph(g);
		this.nodeSet = new HashSet<Node>(nodes);
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

	public abstract boolean propagate(EdgeAddition ea);

	/*
	 * ER
	 */

	public boolean shouldPropagate(EdgeRemoval er) {
		return this.g.containsNode(er.getEdge().getN1())
				|| this.g.containsNode(er.getEdge().getN2());
	}

	public abstract boolean propagate(EdgeRemoval er);
}
