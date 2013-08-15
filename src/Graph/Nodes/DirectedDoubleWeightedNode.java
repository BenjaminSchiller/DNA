package Graph.Nodes;

import DataStructures.GraphDataStructure;
import Graph.IWeighted;
import Graph.Edges.DirectedDoubleWeightedEdge;
import Graph.Edges.Edge;

public class DirectedDoubleWeightedNode extends DirectedNode implements IWeighted<Double> {
	private Double weight;
	public final static Class<? extends Edge> edgeType = DirectedDoubleWeightedEdge.class;

	public DirectedDoubleWeightedNode(int i, Double weight, GraphDataStructure gds) {
		super(i, gds);
		this.setWeight(weight);
	}
	
	public DirectedDoubleWeightedNode(String str, Double weight, GraphDataStructure gds) {
		super(str, gds);
		this.setWeight(weight);
	}
	
	public DirectedDoubleWeightedNode(int i, GraphDataStructure gds) {
		this(i, 1d, gds);
	}
	
	public DirectedDoubleWeightedNode(String str, GraphDataStructure gds) {
		this(str, 1d, gds);
	}
	
	@Override
	public void setWeight(Double newWeight) {
		this.weight = newWeight;
	}

	@Override
	public Double getWeight() {
		return this.weight;
	}
}
