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

	public static NonOverlappingPartition getPartition(String name, Graph g,
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
	public boolean propagate(EdgeAddition ea) {
		if (this.g.containsNode(ea.getEdge().getN1())
				&& this.g.containsNode(ea.getEdge().getN2())) {
			EdgeAddition ea_ = this.getLocalEA(ea);
			return ea_.apply(this.g);
		} else {
			return this.externalEdges.add((Edge) ea.getEdge());
		}
	}

	@Override
	public boolean propagate(EdgeRemoval er) {
		if (this.externalEdges.contains(er.getEdge())) {
			return this.externalEdges.remove(er.getEdge());
		} else {
			return this.getLocalER(er).apply(this.g);
		}
	}

	@Override
	protected Value[] getStatistics() {
		Value externalEdges = new Value("externalEdges",
				this.externalEdges.size());
		return new Value[] { externalEdges };
	}

}
