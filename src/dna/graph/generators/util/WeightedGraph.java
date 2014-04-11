package dna.graph.generators.util;

import dna.graph.Graph;
import dna.graph.IElement;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.generators.GraphGenerator;
import dna.graph.weightsNew.IWeighted;
import dna.graph.weightsNew.Weight;
import dna.graph.weightsNew.Weight.WeightSelection;
import dna.util.ArrayUtils;
import dna.util.parameters.ObjectParameter;

public class WeightedGraph extends GraphGenerator {

	private GraphGenerator gg;

	private WeightSelection nw;
	private WeightSelection ew;

	public WeightedGraph(GraphGenerator gg, WeightSelection nw,
			WeightSelection ew) {
		super("Weighted" + gg.getNamePlain(), ArrayUtils.append(
				gg.getParameters(), new ObjectParameter("NW", nw),
				new ObjectParameter("EW", ew)), gg.getGraphDataStructure(), gg
				.getTimestampInit(), gg.getNodesInit(), gg.getEdgesInit());
		this.gg = gg;
		this.nw = nw;
		this.ew = ew;
	}

	@Override
	public Graph generate() {
		Graph g = this.gg.generate();
		GraphDataStructure gds = g.getGraphDatastructures();
		g.setName(this.getName());
		if (gds.createsWeightedNodes() && this.nw != null
				&& !this.nw.equals(WeightSelection.None)) {
			for (IElement n : g.getNodes()) {
				Weight nodeWeight = gds.newNodeWeight(this.nw);
				((IWeighted) n).setWeight(nodeWeight);
			}
		}
		if (gds.createsWeightedEdges() && this.ew != null
				&& !this.ew.equals(WeightSelection.None)) {
			for (IElement e : g.getEdges()) {
				Weight edgeWeight = gds.newEdgeWeight(this.ew);
				((IWeighted) e).setWeight(edgeWeight);
			}
		}
		return g;
	}

}
