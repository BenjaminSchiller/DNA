package Graph.Nodes;

import DataStructures.GraphDataStructure;
import Graph.IWeighted;
import Graph.Edges.Edge;
import Graph.Edges.UndirectedDoubleWeightedEdge;

public class UndirectedDoubleWeightedNode extends UndirectedNode implements IWeighted<Double> {
	private Double weight;
	public final static Class<? extends Edge> edgeType = UndirectedDoubleWeightedEdge.class;

	public UndirectedDoubleWeightedNode(int i, Double weight, GraphDataStructure gds) {
		super(i, gds);
		this.setWeight(weight);
	}
	
	public UndirectedDoubleWeightedNode(String str, Double weight, GraphDataStructure gds) {
		super(str, gds);
		this.setWeight(weight);
	}
	
	public UndirectedDoubleWeightedNode(int i, GraphDataStructure gds) {
		this(i, 1d, gds);
	}
	
	public UndirectedDoubleWeightedNode(String str, GraphDataStructure gds) {
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