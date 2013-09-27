package dna.graph.generators;

import dna.graph.Graph;
import dna.graph.IElement;
import dna.graph.edges.Edge;
import dna.graph.nodes.Node;
import dna.graph.weights.IIntWeighted;
import dna.graph.weights.Weights;
import dna.graph.weights.Weights.IntEdgeWeightSelection;
import dna.graph.weights.Weights.IntNodeWeightSelection;
import dna.util.ArrayUtils;
import dna.util.parameters.ObjectParameter;

public class IntWeights extends GraphGenerator {

	private GraphGenerator gg;

	private IntNodeWeightSelection nw;

	private IntEdgeWeightSelection ew;

	public IntWeights(GraphGenerator gg, IntNodeWeightSelection nw,
			IntEdgeWeightSelection ew) {
		super(gg.getNamePlain() + "IntW", ArrayUtils.append(gg.getParameters(),
				new ObjectParameter("NW", nw), new ObjectParameter("EW", ew)),
				gg.getGraphDataStructure(), gg.timestampInit, gg.nodesInit,
				gg.edgesInit);
		this.gg = gg;
		this.nw = nw;
		this.ew = ew;
	}

	public IntWeights(GraphGenerator gg, IntNodeWeightSelection nw) {
		super(gg.getNamePlain() + "IntNW", ArrayUtils.append(
				gg.getParameters(), new ObjectParameter("NW", nw)), gg
				.getGraphDataStructure(), gg.timestampInit, gg.nodesInit,
				gg.edgesInit);
		this.gg = gg;
		this.nw = nw;
		this.ew = null;
	}

	public IntWeights(GraphGenerator gg, IntEdgeWeightSelection ew) {
		super(gg.getNamePlain() + "IntEW", ArrayUtils.append(
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
		if (this.nw != null && !this.nw.equals(IntNodeWeightSelection.None)) {
			for (IElement n : g.getNodes()) {
				((IIntWeighted) n).setWeight(Weights.getIntWeight(this.nw));
			}
		}
		if (this.ew != null && !this.ew.equals(IntEdgeWeightSelection.None)) {
			for (IElement e : g.getEdges()) {
				((IIntWeighted) e).setWeight(Weights.getIntWeight(this.ew));
			}
		}
		return g;
	}

	@Override
	public boolean canGenerateNodeType(Class<? extends Node> nodeType) {
		if (this.nw != null && !this.nw.equals(IntNodeWeightSelection.None)) {
			return super.canGenerateNodeType(nodeType)
					&& IIntWeighted.class.isAssignableFrom(nodeType);
		}
		return super.canGenerateNodeType(nodeType);
	}

	@Override
	public boolean canGenerateEdgeType(Class<? extends Edge> edgeType) {
		if (this.ew != null && !this.ew.equals(IntEdgeWeightSelection.None)) {
			return super.canGenerateEdgeType(edgeType)
					&& IIntWeighted.class.isAssignableFrom(edgeType);
		}
		return super.canGenerateEdgeType(edgeType);
	}

}
