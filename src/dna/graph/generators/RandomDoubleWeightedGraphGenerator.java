package dna.graph.generators;

import dna.graph.Graph;
import dna.graph.IElement;
import dna.graph.IWeighted;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.edges.Edge;
import dna.util.Rand;
import dna.util.parameters.Parameter;

public class RandomDoubleWeightedGraphGenerator extends RandomGraphGenerator {

	public RandomDoubleWeightedGraphGenerator(String name, Parameter[] params,
			GraphDataStructure gds, long timestampInit, int nodesInit,
			int edgesInit) {
		super(name, params, gds, timestampInit, nodesInit, edgesInit);
	}

	
	@Override
	public Graph generate() {
		Graph g = super.generate();
		for (IElement e : g.getEdges()) {
			((IWeighted) e).setWeight(Rand.rand.nextDouble());
		}
		return g;
	}
	
	@Override
	public boolean canGenerateEdgeType(Class<? extends Edge> edgeType) {
		return IWeighted.class.isAssignableFrom(edgeType);
	}
}
