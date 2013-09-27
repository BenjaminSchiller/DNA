package dna.graph.generators;

import dna.graph.Graph;
import dna.graph.IElement;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.edges.Edge;
import dna.graph.nodes.Node;
import dna.graph.weights.IWeighted;
import dna.graph.weights.Weights;
import dna.graph.weights.Weights.DoubleEdgeWeightSelection;
import dna.graph.weights.Weights.DoubleNodeWeightSelection;
import dna.util.parameters.ObjectParameter;
import dna.util.parameters.Parameter;

public class RandomDoubleWeightedGraphGenerator extends RandomGraphGenerator {

	private DoubleNodeWeightSelection nw;

	private DoubleEdgeWeightSelection ew;

	public RandomDoubleWeightedGraphGenerator(GraphDataStructure gds,
			int nodes, int edges, DoubleNodeWeightSelection nw,
			DoubleEdgeWeightSelection ew) {
		super("RandomDoubleWeightedGraph", new Parameter[] {
				new ObjectParameter("NW", nw), new ObjectParameter("EW", ew) },
				gds, nodes, edges);
		this.nw = nw;
		this.ew = ew;
	}

	@Override
	public Graph generate() {
		Graph g = super.generate();
		if (this.nw != null && !this.nw.equals(DoubleNodeWeightSelection.None)) {
			for (IElement n : g.getNodes()) {
				((IWeighted) n).setWeight(Weights.getDoubleWeight(this.nw));
			}
		}
		if (this.ew != null && !this.ew.equals(DoubleEdgeWeightSelection.None)) {
			for (IElement e : g.getEdges()) {
				((IWeighted) e).setWeight(Weights.getDoubleWeight(this.ew));
			}
		}
		return g;
	}

	@Override
	public boolean canGenerateNodeType(Class<? extends Node> nodeType) {
		if (this.nw != null && !this.nw.equals(DoubleNodeWeightSelection.None)) {
			return IWeighted.class.isAssignableFrom(nodeType);
		}
		return super.canGenerateNodeType(nodeType);
	}

	@Override
	public boolean canGenerateEdgeType(Class<? extends Edge> edgeType) {
		if (this.ew != null && !this.ew.equals(DoubleEdgeWeightSelection.None)) {
			return IWeighted.class.isAssignableFrom(edgeType);
		}
		return super.canGenerateEdgeType(edgeType);
	}
}
