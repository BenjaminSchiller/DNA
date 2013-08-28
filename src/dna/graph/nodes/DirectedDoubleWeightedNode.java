package dna.graph.nodes;

import dna.graph.IWeighted;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.edges.DirectedDoubleWeightedEdge;
import dna.graph.edges.Edge;
import dna.io.etc.Keywords;

public class DirectedDoubleWeightedNode extends DirectedNode implements IWeighted<Double> {
	private double weight;
	public final static Class<? extends Edge> edgeType = DirectedDoubleWeightedEdge.class;

	public DirectedDoubleWeightedNode(int i, Double weight, GraphDataStructure gds) {
		super(i, gds);
		this.setWeight(weight);
	}

	public DirectedDoubleWeightedNode(String str, GraphDataStructure gds) {
		super(str.split(Keywords.nodeWeightDelimiter)[0], gds);
		if (str.contains(Keywords.nodeWeightDelimiter)) {
			this.weight = Double.parseDouble(str.split(Keywords.nodeWeightDelimiter)[1]);
		} else {
			this.weight = 0;
		}
	}

	public DirectedDoubleWeightedNode(int i, GraphDataStructure gds) {
		this(i, 1d, gds);
	}

	@Override
	public void setWeight(Double newWeight) {
		this.weight = newWeight;
	}

	@Override
	public Double getWeight() {
		return this.weight;
	}

	@Override
	public String getStringRepresentation() {
		return super.getStringRepresentation() + Keywords.nodeWeightDelimiter + this.weight;
	}
}
