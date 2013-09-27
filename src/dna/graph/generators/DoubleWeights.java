package dna.graph.generators;

import dna.graph.Graph;
import dna.graph.IElement;
import dna.graph.edges.Edge;
import dna.graph.nodes.Node;
import dna.graph.weights.IDoubleWeighted;
import dna.graph.weights.Weights;
import dna.graph.weights.Weights.DoubleEdgeWeightSelection;
import dna.graph.weights.Weights.DoubleNodeWeightSelection;
import dna.util.ArrayUtils;
import dna.util.parameters.ObjectParameter;

public class DoubleWeights extends GraphGenerator {

	private GraphGenerator gg;

	private DoubleNodeWeightSelection nw;

	private DoubleEdgeWeightSelection ew;

	public DoubleWeights(GraphGenerator gg, DoubleNodeWeightSelection nw,
			DoubleEdgeWeightSelection ew) {
		super(gg.getNamePlain() + "DoubleW", ArrayUtils.append(
				gg.getParameters(), new ObjectParameter("NW", nw),
				new ObjectParameter("EW", ew)), gg.getGraphDataStructure(),
				gg.timestampInit, gg.nodesInit, gg.edgesInit);
		this.gg = gg;
		this.nw = nw;
		this.ew = ew;
	}

	public DoubleWeights(GraphGenerator gg, DoubleNodeWeightSelection nw) {
		super(gg.getNamePlain() + "DoubleNW", ArrayUtils.append(
				gg.getParameters(), new ObjectParameter("NW", nw)), gg
				.getGraphDataStructure(), gg.timestampInit, gg.nodesInit,
				gg.edgesInit);
		this.gg = gg;
		this.nw = nw;
		this.ew = null;
	}

	public DoubleWeights(GraphGenerator gg, DoubleEdgeWeightSelection ew) {
		super(gg.getNamePlain() + "DoubleEW", ArrayUtils.append(
				gg.getParameters(), new ObjectParameter("EW", ew)), gg
				.getGraphDataStructure(), gg.timestampInit, gg.nodesInit,
				gg.edgesInit);
		this.gg = gg;
		this.nw = null;
		this.ew = ew;
	}

	@Override
	public Graph generate() {
		Graph g = this.gg.generate();
		g.setName(this.getName());
		if (this.nw != null && !this.nw.equals(DoubleNodeWeightSelection.None)) {
			for (IElement n : g.getNodes()) {
				((IDoubleWeighted) n).setWeight(Weights
						.getDoubleWeight(this.nw));
			}
		}
		if (this.ew != null && !this.ew.equals(DoubleEdgeWeightSelection.None)) {
			for (IElement e : g.getEdges()) {
				((IDoubleWeighted) e).setWeight(Weights
						.getDoubleWeight(this.ew));
			}
		}
		return g;
	}

	@Override
	public boolean canGenerateNodeType(Class<? extends Node> nodeType) {
		if (this.nw != null && !this.nw.equals(DoubleNodeWeightSelection.None)) {
			return super.canGenerateNodeType(nodeType)
					&& IDoubleWeighted.class.isAssignableFrom(nodeType);
		}
		return super.canGenerateNodeType(nodeType);
	}

	@Override
	public boolean canGenerateEdgeType(Class<? extends Edge> edgeType) {
		if (this.ew != null && !this.ew.equals(DoubleEdgeWeightSelection.None)) {
			return super.canGenerateEdgeType(edgeType)
					&& IDoubleWeighted.class.isAssignableFrom(edgeType);
		}
		return super.canGenerateEdgeType(edgeType);
	}

}
