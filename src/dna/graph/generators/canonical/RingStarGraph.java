package dna.graph.generators.canonical;

import dna.graph.IGraph;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.edges.Edge;
import dna.graph.generators.GraphGenerator;
import dna.graph.nodes.Node;
import dna.util.parameters.IntParameter;
import dna.util.parameters.Parameter;

/**
 * Graph generator for a ring topology, i.e. 1 -> 2 -> 3 -> ... -> n -> 0
 * 
 * @author Nico
 * 
 */
public class RingStarGraph extends GraphGenerator {

	public RingStarGraph(GraphDataStructure gds, int nodes) {
		super(buildName("RingStarGraph", gds),
				new Parameter[] { new IntParameter("N", nodes) }, gds, 0,
				nodes, nodes);
	}

	@Override
	public IGraph generate() {
		IGraph g = this.newGraphInstance();

		Node center = this.gds.newNodeInstance(0);
		g.addNode(center);

		Node previous = this.gds.newNodeInstance(1);
		Node first = previous;
		this.add(g, center, first);

		for (int i = 2; i < this.nodesInit; i++) {
			Node node = this.gds.newNodeInstance(i);
			this.add(g, center, previous, node);
			previous = node;
		}
		this.connect(g, previous, first);

		return g;
	}

	protected void connect(IGraph g, Node src, Node dst) {
		Edge e = this.gds.newEdgeInstance(src, dst);
		g.addEdge(e);
		e.connectToNodes();
	}

	protected void add(IGraph g, Node center, Node n) {
		g.addNode(n);
		this.connect(g, center, n);
	}

	protected void add(IGraph g, Node center, Node previous, Node n) {
		g.addNode(n);
		this.connect(g, center, n);
		this.connect(g, previous, n);
	}

}
