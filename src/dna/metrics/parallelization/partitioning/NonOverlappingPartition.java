package dna.metrics.parallelization.partitioning;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import dna.graph.Graph;
import dna.graph.IElement;
import dna.graph.edges.Edge;
import dna.graph.nodes.Node;
import dna.metrics.Metric;
import dna.updates.update.EdgeAddition;
import dna.updates.update.EdgeRemoval;

public class NonOverlappingPartition extends Partition {

	protected Set<Edge> connections;

	public Set<Edge> getConnections() {
		return connections;
	}

	public NonOverlappingPartition(Graph g, List<Node> nodes, Metric m,
			Set<Edge> connections) {
		super(g, nodes, m);
		this.connections = connections;
	}

	public boolean addConnection(Edge e) {
		return this.connections.add(e);
	}

	public String toString() {
		return "NonOverlappingPartition: " + g.toString() + " @ "
				+ connections.size() + " connections";
	}

	public static NonOverlappingPartition getPartition(String name, Graph g,
			List<Node> nodes, Metric m) {
		Graph gp = g.getGraphDatastructures().newGraphInstance(name,
				g.getTimestamp(), nodes.size(),
				nodes.size() == 0 ? 0 : g.getEdgeCount() / nodes.size());
		Set<Edge> connections = new HashSet<Edge>();

		for (Node n : nodes) {
			Node newNode = gp.getGraphDatastructures().newNodeInstance(
					n.asString());
			gp.addNode(newNode);
		}
		for (Node n : nodes) {
			for (IElement e_ : n.getEdges()) {
				Edge e = (Edge) e_;
				if (gp.containsNode(e.getN1()) && gp.containsNode(e.getN2())) {
					Edge newEdge = gp.getGraphDatastructures().newEdgeInstance(
							e_.asString(), gp);
					gp.addEdge(newEdge);
					newEdge.connectToNodes();
				} else {
					connections.add(e);
				}
			}
		}

		return new NonOverlappingPartition(gp, nodes, clone(m), connections);
	}

	@Override
	public boolean propagate(EdgeAddition ea) {
		if (this.g.containsNode(ea.getEdge().getN1())
				&& this.g.containsNode(ea.getEdge().getN2())) {
			EdgeAddition ea_ = new EdgeAddition(this.g.getGraphDatastructures()
					.newEdgeInstance(ea.getEdge().asString(), this.g));
			return ea_.apply(this.g);
		} else {
			return this.connections.add((Edge) ea.getEdge());
		}
	}

	@Override
	public boolean propagate(EdgeRemoval er) {
		// TODO noting needed to be done?
		return er.apply(this.g);
	}

}
