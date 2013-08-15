package Graph.Edges;

import Graph.Graph;
import Graph.IWeighted;
import Graph.Nodes.Node;

public class UndirectedDoubleWeightedEdge extends UndirectedEdge implements IWeighted<Double> {
	private double weight;
	
	public UndirectedDoubleWeightedEdge(Node src, Node dst, Double weight) {
		super(src, dst);
		this.setWeight(weight);
	}

	public UndirectedDoubleWeightedEdge(String str, Graph g, Double weight) {
		super(str, g);
		this.setWeight(weight);
	}	
	
	public UndirectedDoubleWeightedEdge(Node src, Node dst) {
		this(src, dst, 1d);
	}

	public UndirectedDoubleWeightedEdge(String str, Graph g) {
		this(str, g, 1d);
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