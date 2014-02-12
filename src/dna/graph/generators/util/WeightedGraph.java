package dna.graph.generators.util;

import dna.graph.Graph;
import dna.graph.IElement;
import dna.graph.edges.Edge;
import dna.graph.generators.GraphGenerator;
import dna.graph.nodes.Node;
import dna.graph.weights.IDoubleWeighted;
import dna.graph.weights.IIntWeighted;
import dna.graph.weights.IWeighted;
import dna.graph.weights.Weights;
import dna.graph.weights.Weights.EdgeWeightSelection;
import dna.graph.weights.Weights.NodeWeightSelection;
import dna.util.ArrayUtils;
import dna.util.parameters.ObjectParameter;

public class WeightedGraph extends GraphGenerator {

	private GraphGenerator gg;

	private NodeWeightSelection nw;

	private EdgeWeightSelection ew;

	public WeightedGraph(GraphGenerator gg, NodeWeightSelection nw,
			EdgeWeightSelection ew) {
		super("Weighted" + gg.getNamePlain(), ArrayUtils.append(
				gg.getParameters(), new ObjectParameter("NW", nw),
				new ObjectParameter("EW", ew)), gg.getGraphDataStructure(), gg
				.getTimestampInit(), gg.getNodesInit(), gg.getEdgesInit());
		this.gg = gg;
		this.nw = nw;
		this.ew = ew;
	}

	public WeightedGraph(GraphGenerator gg, NodeWeightSelection nw) {
		super("Weighted" + gg.getNamePlain(), ArrayUtils.append(
				gg.getParameters(), new ObjectParameter("NW", nw)), gg
				.getGraphDataStructure(), gg.getTimestampInit(), gg
				.getNodesInit(), gg.getEdgesInit());
		this.gg = gg;
		this.nw = nw;
		this.ew = null;
	}

	public WeightedGraph(GraphGenerator gg, EdgeWeightSelection ew) {
		super("Weighted" + gg.getNamePlain(), ArrayUtils.append(
				gg.getParameters(), new ObjectParameter("EW", ew)), gg
				.getGraphDataStructure(), gg.getTimestampInit(), gg
				.getNodesInit(), gg.getEdgesInit());
		this.gg = gg;
		this.nw = null;
		this.ew = ew;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Graph generate() {
		Graph g = this.gg.generate();
		g.setName(this.getName());
		if (this.nw != null && !this.nw.equals(NodeWeightSelection.None)) {
			for (IElement n : g.getNodes()) {
				((IWeighted) n).setWeight(Weights.getWeight(this.nw));
			}
		}
		if (this.ew != null && !this.ew.equals(EdgeWeightSelection.None)) {
			for (IElement e : g.getEdges()) {
				((IWeighted) e).setWeight(Weights.getWeight(this.ew));
			}
		}
		return g;
	}

	@Override
	public boolean canGenerateNodeType(Class<? extends Node> nodeType) {
		if (this.nw != null && !this.nw.equals(NodeWeightSelection.None)) {
			return super.canGenerateNodeType(nodeType)
					&& IWeighted.class.isAssignableFrom(nodeType)
					&& ((this.nw.toString().startsWith(
							Weights.DoubleWeightPrefix) && IDoubleWeighted.class
							.isAssignableFrom(nodeType)) || (this.nw.toString()
							.startsWith(Weights.IntWeightPrefix) && IIntWeighted.class
							.isAssignableFrom(nodeType)));
		}
		return super.canGenerateNodeType(nodeType);
	}

	@Override
	public boolean canGenerateEdgeType(Class<? extends Edge> edgeType) {
		if (this.ew != null && !this.ew.equals(EdgeWeightSelection.None)) {
			return super.canGenerateEdgeType(edgeType)
					&& IWeighted.class.isAssignableFrom(edgeType)
					&& ((this.ew.toString().startsWith(
							Weights.DoubleWeightPrefix) && IDoubleWeighted.class
							.isAssignableFrom(edgeType)) || (this.ew.toString()
							.startsWith(Weights.IntWeightPrefix) && IIntWeighted.class
							.isAssignableFrom(edgeType)));
		}
		return super.canGenerateEdgeType(edgeType);
	}

}
