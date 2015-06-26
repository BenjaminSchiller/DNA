package dna.graph.generators.canonical;

import dna.graph.Graph;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.edges.Edge;
import dna.graph.generators.GraphGenerator;
import dna.graph.nodes.Node;
import dna.util.parameters.IntParameter;
import dna.util.parameters.Parameter;

public class StarGraph extends GraphGenerator {

	private int nodes;

	public StarGraph(GraphDataStructure gds, int nodes) {
		super("StarGraph",
				new Parameter[] { new IntParameter("Nodes", nodes) }, gds, 0,
				nodes, nodes - 1);
		this.nodes = nodes;
	}

	@Override
	public Graph generate() {
		Graph g = this.newGraphInstance();

		Node center = this.gds.newNodeInstance(0);
		g.addNode(center);
		for (int i = 1; i < this.nodes; i++) {
			Node n = this.gds.newNodeInstance(i);
			g.addNode(n);
			Edge e = this.gds.newEdgeInstance(center, n);
			g.addEdge(e);
			e.connectToNodes();
		}
		return g;
	}

}
