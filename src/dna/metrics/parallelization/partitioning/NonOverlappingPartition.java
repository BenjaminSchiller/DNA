package dna.metrics.parallelization.partitioning;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import dna.graph.Graph;
import dna.graph.IElement;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.edges.Edge;
import dna.graph.nodes.Node;
import dna.metrics.Metric;
import dna.series.data.Value;
import dna.updates.update.EdgeAddition;
import dna.updates.update.EdgeRemoval;
import dna.updates.update.NodeAddition;
import dna.updates.update.NodeRemoval;

/**
 * 
 * This class extends the basic Partition class and implements a non-overlapping
 * partitioning type. For a given list of nodes, the subgraph induced by these
 * nodes is created.
 * 
 * In addition to that subgraph, the set of external edges is maintained, i.e.,
 * the edges between the given nodes and nodes in other partitions.
 * 
 * @author benni
 *
 */
public class NonOverlappingPartition extends Partition {

	protected Set<Edge> externalEdges;

	public Set<Edge> getExternalEdges() {
		return externalEdges;
	}

	public NonOverlappingPartition(Graph g, List<Node> nodes, Metric m,
			Set<Edge> connections) {
		super(g, nodes, m);
		this.externalEdges = connections;
	}

	public String toString() {
		return "NonOverlappingPartition: " + g.toString() + " @ "
				+ externalEdges.size();
	}

	public static NonOverlappingPartition[] getPartitions(Graph g,
			List<List<Node>> nodesList, Metric m,
			HashMap<Node, Partition> partitionMap) {
		NonOverlappingPartition[] p = new NonOverlappingPartition[nodesList
				.size()];
		int index = 0;
		for (List<Node> nodes : nodesList) {
			p[index] = getPartition(getName(index), g, nodes, m);
			for (Node node : nodes) {
				partitionMap.put(node, p[index]);
			}
			index++;
		}
		return p;
	}

	protected static NonOverlappingPartition getPartition(String name, Graph g,
			List<Node> nodes, Metric m) {
		Graph gp = g.getGraphDatastructures().newGraphInstance(name,
				g.getTimestamp(), nodes.size(),
				nodes.size() == 0 ? 0 : g.getEdgeCount() / nodes.size());
		GraphDataStructure gds = gp.getGraphDatastructures();
		Set<Edge> externalEdges = new HashSet<Edge>();

		// add main nodes
		for (Node n : nodes) {
			gp.addNode(gds.newNodeInstance(n.asString()));
		}

		// add edges
		for (Node n : nodes) {
			for (IElement e_ : n.getEdges()) {
				Edge e = (Edge) e_;
				if (gp.containsNode(e.getN1()) && gp.containsNode(e.getN2())) {
					Edge newEdge = gds.newEdgeInstance(e_.asString(), gp);
					gp.addEdge(newEdge);
					newEdge.connectToNodes();
				} else {
					externalEdges.add(e);
				}
			}
		}

		return new NonOverlappingPartition(gp, nodes, clone(m), externalEdges);
	}

	@Override
	protected Value[] getStatistics() {
		Value externalEdges = new Value("externalEdges",
				this.externalEdges.size());
		return new Value[] { externalEdges };
	}

	/*
	 * NA
	 */

	@Override
	public boolean propagate(NodeAddition globalNA) {
		Node n = this.g.getGraphDatastructures().newNodeInstance(
				globalNA.getNode().asString());
		NodeAddition na = new NodeAddition(n);
		boolean success = true;

		success &= this.apply(na);
		success &= this.nodes.add(n);
		success &= this.nodeSet.add(n);

		return success;
	}

	/*
	 * NR
	 */

	@Override
	public boolean shouldPropagate(NodeRemoval globalNR) {
		Node n = (Node) globalNR.getNode();
		if (this.g.containsNode(n)) {
			return true;
		}
		for (Edge e : this.externalEdges) {
			if (e.isConnectedTo(n)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean propagate(NodeRemoval globalNR) {
		boolean success = true;
		Node n = (Node) globalNR.getNode();

		if (this.g.containsNode(n)) {
			n = g.getNode(n.getIndex());
			NodeRemoval nr = new NodeRemoval(n);
			success &= this.apply(nr);
			success &= this.nodes.remove(n);
			success &= this.nodeSet.remove(n);
		}

		success &= removeConnectedEdges(this.externalEdges, n);

		return success;
	}

	/*
	 * EA
	 */

	@Override
	public boolean shouldPropagate(EdgeAddition globalEA) {
		return this.g.containsNode(globalEA.getEdge().getN1())
				|| this.g.containsNode(globalEA.getEdge().getN2());
	}

	@Override
	public boolean propagate(EdgeAddition globalEA) {
		if (!this.g.containsNode(globalEA.getEdge().getN1())
				|| !this.g.containsNode(globalEA.getEdge().getN2())) {
			return this.externalEdges.add((Edge) globalEA.getEdge());
		}

		Node n1 = this.g.getNode(globalEA.getEdge().getN1().getIndex());
		Node n2 = this.g.getNode(globalEA.getEdge().getN2().getIndex());
		Edge e = this.g.getGraphDatastructures().newEdgeInstance(n1, n2);
		EdgeAddition ea = new EdgeAddition(e);
		return this.apply(ea);
	}

	/*
	 * ER
	 */

	@Override
	public boolean shouldPropagate(EdgeRemoval globalER) {
		return this.g.containsNode(globalER.getEdge().getN1())
				|| this.g.containsNode(globalER.getEdge().getN2());
	}

	@Override
	public boolean propagate(EdgeRemoval globalER) {
		if (this.externalEdges.contains(globalER.getEdge())) {
			return this.externalEdges.remove(globalER.getEdge());
		}

		Edge e = g.getEdge(globalER.getEdge().getN1(), globalER.getEdge()
				.getN2());
		EdgeRemoval er = new EdgeRemoval(e);
		return this.apply(er);
	}

}
