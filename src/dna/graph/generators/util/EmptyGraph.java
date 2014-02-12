package dna.graph.generators.util;

import dna.graph.Graph;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.generators.GraphGenerator;

public class EmptyGraph extends GraphGenerator {

	public EmptyGraph(GraphDataStructure gds, int nodesInit, int edgesInit) {
		super("EmptyGraph", null, gds, 0, nodesInit, edgesInit);
	}

	public EmptyGraph(GraphDataStructure gds) {
		this(gds, 0, 0);
	}

	@Override
	public Graph generate() {
		return this.newGraphInstance();
	}

}
