package dna.graph.generators;

import dna.graph.Graph;
import dna.graph.datastructures.GraphDataStructure;

public class EmptyGraphGenerator extends GraphGenerator {

	public EmptyGraphGenerator(GraphDataStructure gds, int nodesInit,
			int edgesInit) {
		super("EmptyGraph", null, gds, 0, nodesInit, edgesInit);
	}

	public EmptyGraphGenerator(GraphDataStructure gds) {
		this(gds, 0, 0);
	}

	@Override
	public Graph generate() {
		return this.newGraphInstance();
	}

}
