package dna.metrics.parallelization.partitioning;

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

	public static OverlappingPartition getPartition(String name, Graph g,
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
	public boolean propagate(EdgeAddition ea) {
		Node n1 = ea.getEdge().getN1();
		Node n2 = ea.getEdge().getN2();
		GraphDataStructure gds = g.getGraphDatastructures();
		if (g.containsNode(n1) && g.containsNode(n2)) {
			EdgeAddition ea_ = this.getLocalEA(ea);
			// Edge newEdge = gds.newEdgeInstance(ea.getEdge().asString(), g);
			// EdgeAddition ea_ = new EdgeAddition(newEdge);
			if (auxiliaryNodes.contains(n1) || auxiliaryNodes.contains(n2)) {
				auxiliaryEdges.add((Edge) ea_.getEdge());
			}
			return ea_.apply(g);
		} else if (g.containsNode(n1) || g.containsNode(n2)) {
			Node newNode_, newNode = null;
			if (g.containsNode(n1)) {
				newNode = gds.newNodeInstance(n2.asString());
				newNode_ = n2;
			} else {
				newNode = gds.newNodeInstance(n1.asString());
				newNode_ = n1;
			}

			NodeAddition na_ = new NodeAddition(newNode);
			na_.apply(g);
			auxiliaryNodes.add(newNode);

			for (IElement e_ : newNode_.getEdges()) {
				Edge e = (Edge) e_;
				if (g.containsNode(e.getDifferingNode(newNode_))) {
					Edge newEdge = gds.newEdgeInstance(e.asString(), g);
					EdgeAddition ea_ = new EdgeAddition(newEdge);
					ea_.apply(g);
					auxiliaryEdges.add(newEdge);
				}
			}

			// Node newOverlap =
			// this.g.getGraphDatastructures().newNodeInstance(
			// ea.getEdge().getN1().asString());
			// this.g.addNode(newOverlap);
			// this.auxiliaryNodes.add(newOverlap);
			// for (IElement e_ : ea.getEdge().getN1().getEdges()) {
			// Edge e = (Edge) e_;
			// if (g.containsNode(e.getDifferingNode(ea.getEdge().getN1()))) {
			// Edge newEdge = this.g.getGraphDatastructures()
			// .newEdgeInstance(e.asString(), this.g);
			// this.g.addEdge(newEdge);
			// newEdge.connectToNodes();
			// this.auxiliaryEdges.add(newEdge);
			// }
			// }
			return true;
		} else {
			throw new IllegalStateException(
					"cannot propage EA in in case no node exists in graph in "
							+ this.toString());
		}

		// if (this.g.containsNode(ea.getEdge().getN1())
		// && this.g.containsNode(ea.getEdge().getN2())) {
		// EdgeAddition ea_ = new EdgeAddition(this.g.getGraphDatastructures()
		// .newEdgeInstance(ea.getEdge().asString(), this.g));
		// return ea_.apply(this.g);
		// } else if (!this.g.containsNode(ea.getEdge().getN1())) {
		// Node newOverlap = this.g.getGraphDatastructures().newNodeInstance(
		// ea.getEdge().getN1().asString());
		// this.g.addNode(newOverlap);
		// this.auxiliaryNodes.add(newOverlap);
		// for (IElement e_ : ea.getEdge().getN1().getEdges()) {
		// Edge e = (Edge) e_;
		// if (g.containsNode(e.getDifferingNode(ea.getEdge().getN1()))) {
		// Edge newEdge = this.g.getGraphDatastructures()
		// .newEdgeInstance(e.asString(), this.g);
		// this.g.addEdge(newEdge);
		// newEdge.connectToNodes();
		// this.auxiliaryEdges.add(newEdge);
		// }
		// }
		// return true;
		//
		// } else if (!this.g.containsNode(ea.getEdge().getN2())) {
		// Node newOverlap = this.g.getGraphDatastructures().newNodeInstance(
		// ea.getEdge().getN2().asString());
		// this.g.addNode(newOverlap);
		// this.auxiliaryNodes.add(newOverlap);
		// for (IElement e_ : ea.getEdge().getN2().getEdges()) {
		// Edge e = (Edge) e_;
		// if (g.containsNode(e.getDifferingNode(ea.getEdge().getN2()))) {
		// Edge newEdge = this.g.getGraphDatastructures()
		// .newEdgeInstance(e.asString(), this.g);
		// this.g.addEdge(newEdge);
		// newEdge.connectToNodes();
		// this.auxiliaryEdges.add(newEdge);
		// }
		// }
		// return true;
		// } else {
		// throw new IllegalStateException(
		// "cannot propage EA in in case no node exists in graph in "
		// + this.toString());
		// }
	}

	@Override
	public boolean propagate(EdgeRemoval er) {
		// TODO remove node that is in overlap!
		return this.getLocalER(er).apply(g);
	}

	@Override
	protected Value[] getStatistics() {
		Value auxiliaryNodes = new Value("auxiliaryNodes",
				this.auxiliaryNodes.size());
		Value auxiliaryEdges = new Value("auxiliaryEdges",
				this.auxiliaryEdges.size());
		return new Value[] { auxiliaryNodes, auxiliaryEdges };
	}

}
