package dna.graph.generators.connectivity;

import java.util.Set;

import dna.graph.Graph;
import dna.graph.IElement;
import dna.graph.edges.Edge;
import dna.graph.generators.GraphGenerator;
import dna.graph.nodes.Node;

public abstract class ConnectedGraph extends GraphGenerator {

	protected GraphGenerator gg;

	public ConnectedGraph(String name, GraphGenerator gg) {
		super(name + gg.getNamePlain(), gg.getParameters(), gg
				.getGraphDataStructure(), gg.getTimestampInit(), gg
				.getNodesInit(), gg.getEdgesInit());
		this.gg = gg;
	}

	@Override
	public Graph generate() {
		Graph g = this.gg.generate();
		Set<Node> exclude = this.getNodesToExclude(g);
		for (Node n : exclude) {
			g.removeNode(n);
			for (IElement e_ : n.getEdges()) {
				Edge e = (Edge) e_;
				g.removeEdge(e);
				Node n2 = e.getDifferingNode(n);
				if (!exclude.contains(n2)) {
					n2.removeEdge(e);
				}
			}
		}
		g.setName(this.getNamePlain());
		return g;
	}

	protected abstract Set<Node> getNodesToExclude(Graph g);

}
