package dna.graph.generators.google;

import java.security.Policy.Parameters;

import dna.graph.Graph;
import dna.graph.generators.directed.DirectedGraphGenerator;
import dna.util.parameters.Parameter;

public class GooglePlusFromExistingGraphGenerator extends
		DirectedGraphGenerator {

	private Graph graph;

	public GooglePlusFromExistingGraphGenerator(Graph g) {
		super(g.getName(), (Parameter[]) new Parameters[0], g
				.getGraphDatastructures(), g.getTimestamp(), g.getNodeCount(),
				g.getEdgeCount());
		this.graph = g;

	}

	@Override
	public Graph generate() {
		return this.graph;
	}

}
