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
 * This class extends the basic partition and implements the overlapping type of
 * partition, i.e., all (1-hop) neighbors of the given nodes are added and the
 * edges induced accordingly.
 * 
 * In addition to the subgraph, this partition maintains the set of auxiliary
 * nodes (the 1-hop neighbors of the initial nodes) as well as the auxiliary
 * edges (edges to and between the 1-hop neighbors).
 * 
 * @author benni
 *
 */
public class OverlappingPartition extends Partition {

	protected Set<Node> auxiliaryNodes;
	protected Set<Edge> auxiliaryEdges;

	public Set<Node> getAuxiliaryNodes() {
		return this.auxiliaryNodes;
	}

	public OverlappingPartition(Graph g, List<Node> nodes, Metric m,
			Set<Node> auxiliaryNodes, Set<Edge> auxiliaryEdges) {
		super(g, nodes, m);
		this.auxiliaryNodes = auxiliaryNodes;
		this.auxiliaryEdges = auxiliaryEdges;
	}

	public boolean addOverlap(Node n) {
		return this.auxiliaryNodes.add(n);
	}

	public String toString() {
		return "OverlappingPartition: " + g.toString() + " @ "
				+ auxiliaryNodes.size() + " / " + auxiliaryEdges.size();
	}

	public static OverlappingPartition[] getPartitions(Graph g,
			List<List<Node>> nodesList, Metric m,
			HashMap<Node, Partition> partitionMap) {
		OverlappingPartition[] p = new OverlappingPartition[nodesList.size()];
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

	protected static OverlappingPartition getPartition(String name, Graph g,
			List<Node> nodes, Metric m) {
		Graph gp = g.getGraphDatastructures().newGraphInstance(name,
				g.getTimestamp(), nodes.size(),
				nodes.size() == 0 ? 0 : g.getEdgeCount() / nodes.size());
		GraphDataStructure gds = gp.getGraphDatastructures();
		Set<Node> auxiliaryNodes = new HashSet<Node>();
		Set<Edge> auxiliaryEdges = new HashSet<Edge>();

		// add main nodes
		for (Node n : nodes) {
			gp.addNode(gds.newNodeInstance(n.asString()));
		}

		// add auxiliary nodes (and direct edges, including auxiliary edges)
		for (Node n : nodes) {
			for (IElement e_ : n.getEdges()) {
				Edge e = (Edge) e_;
				Node n2 = e.getDifferingNode(n);
				if (!gp.containsNode(n2)) {
					Node newNode = gds.newNodeInstance(n2.asString());
					gp.addNode(newNode);
					auxiliaryNodes.add(newNode);
				}
				if (!gp.containsEdge(e)) {
					Edge newEdge = gds.newEdgeInstance(e.asString(), gp);
					gp.addEdge(newEdge);
					newEdge.connectToNodes();
					if (auxiliaryNodes.contains(n2)) {
						auxiliaryEdges.add(newEdge);
					}
				}
			}
		}

		// add auxiliary edges between auxiliary nodes
		for (Node n : nodes) {
			for (IElement e_ : n.getEdges()) {
				for (IElement e__ : ((Edge) e_).getDifferingNode(n).getEdges()) {
					Edge e = (Edge) e__;
					if (!gp.containsEdge(e) && gp.containsNode(e.getN1())
							&& gp.containsNode(e.getN2())) {
						Edge newEdge = gds.newEdgeInstance(e.asString(), gp);
						gp.addEdge(newEdge);
						newEdge.connectToNodes();
						auxiliaryEdges.add(newEdge);
					}
				}
			}
		}

		return new OverlappingPartition(gp, nodes, clone(m), auxiliaryNodes,
				auxiliaryEdges);
	}

	@Override
	protected Value[] getStatistics() {
		Value auxiliaryNodes = new Value("auxiliaryNodes",
				this.auxiliaryNodes.size());
		Value auxiliaryEdges = new Value("auxiliaryEdges",
				this.auxiliaryEdges.size());
		return new Value[] { auxiliaryNodes, auxiliaryEdges };
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
		return this.g.containsNode((Node) globalNR.getNode());
	}

	@Override
	public boolean propagate(NodeRemoval globalNR) {
		boolean success = true;

		Node n = g.getNode(globalNR.getNode().getIndex());
		NodeRemoval nr = new NodeRemoval(n);

		if (this.nodeSet.contains(n)) {
			success &= this.nodes.remove(n);
			success &= this.nodeSet.remove(n);
		}

		success &= this.apply(nr);

		if (this.auxiliaryNodes.contains(n)) {
			success &= this.auxiliaryNodes.remove(n);
		}

		success &= removeConnectedEdges(this.auxiliaryEdges, n);

		return success;
	}

	/*
	 * EA
	 */

	@Override
	public boolean shouldPropagate(EdgeAddition globalEA) {
		return (g.containsNode(globalEA.getEdge().getN1()) && g
				.containsNode(globalEA.getEdge().getN2()))
				|| nodeSet.contains(globalEA.getEdge().getN1())
				|| nodeSet.contains(globalEA.getEdge().getN2());
	}

	@Override
	public boolean propagate(EdgeAddition globalEA) {
		Node n1 = globalEA.getEdge().getN1();
		Node n2 = globalEA.getEdge().getN2();
		GraphDataStructure gds = g.getGraphDatastructures();
		if (this.g.containsNode(globalEA.getEdge().getN1())
				&& this.g.containsNode(globalEA.getEdge().getN2())) {
			boolean success = true;

			Edge e = gds.newEdgeInstance(globalEA.getEdge().asString(), this.g);
			if (this.auxiliaryNodes.contains(globalEA.getEdge().getN1())
					&& this.auxiliaryNodes.contains(globalEA.getEdge().getN2())) {
				this.auxiliaryEdges.add(e);
			}

			EdgeAddition ea = new EdgeAddition(e);
			success &= this.apply(ea);

			return success;
		} else {
			boolean success = true;

			Node newNode, globalNode = null;
			if (this.nodeSet.contains(n1)) {
				newNode = gds.newNodeInstance(n2.asString());
				globalNode = n2;
			} else if (this.nodeSet.contains(n2)) {
				newNode = gds.newNodeInstance(n1.asString());
				globalNode = n1;
			} else {
				throw new IllegalStateException("neither " + n1 + " nor " + n2
						+ " are contained in node set");
			}

			NodeAddition na = new NodeAddition(newNode);
			success &= this.apply(na);
			this.auxiliaryNodes.add(newNode);

			for (IElement edge_ : globalNode.getEdges()) {
				Edge edge = (Edge) edge_;
				if (!g.containsNode(edge.getN1())
						|| !g.containsNode(edge.getN2())) {
					continue;
				}
				Edge newEdge = gds.newEdgeInstance(edge.asString(), g);
				EdgeAddition ea = new EdgeAddition(newEdge);
				this.apply(ea);
				this.auxiliaryEdges.add(newEdge);
			}

			return success;
		}
	}

	/*
	 * ER
	 */

	@Override
	public boolean shouldPropagate(EdgeRemoval globalER) {
		return this.g.containsNode(globalER.getEdge().getN1())
				&& this.g.containsNode(globalER.getEdge().getN2());
	}

	@Override
	public boolean propagate(EdgeRemoval globalER) {
		Node n1 = g.getNode(globalER.getEdge().getN1().getIndex());
		Node n2 = g.getNode(globalER.getEdge().getN2().getIndex());
		// GraphDataStructure gds = g.getGraphDatastructures();

		boolean success = true;

		Edge e = g.getEdge(n1, n2);
		EdgeRemoval er = new EdgeRemoval(e);
		success &= this.apply(er);

		// TODO remove auxiliary nodes which only have auxiliary edges

		return success;
	}

}
