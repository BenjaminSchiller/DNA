package dna.graph.nodes;

import dna.datastructures.GraphDataStructure;
import dna.graph.IWeighted;
import dna.graph.edges.Edge;
import dna.graph.edges.UndirectedDoubleWeightedEdge;
import dna.io.etc.Keywords;

public class UndirectedDoubleWeightedNode extends UndirectedNode implements IWeighted<Double> {
	private double weight;
	public final static Class<? extends Edge> edgeType = UndirectedDoubleWeightedEdge.class;

	public UndirectedDoubleWeightedNode(int i, Double weight, GraphDataStructure gds) {
		super(i, gds);
		this.setWeight(weight);
	}

	public UndirectedDoubleWeightedNode(String str, GraphDataStructure gds) {
		super(str.split(Keywords.nodeWeightDelimiter)[0], gds);
		if (str.contains(Keywords.nodeWeightDelimiter)) {
			this.weight = Double.parseDouble(str.split(Keywords.nodeWeightDelimiter)[1]);
		} else {
			this.weight = 0;
		}
	}

	public UndirectedDoubleWeightedNode(int i, GraphDataStructure gds) {
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