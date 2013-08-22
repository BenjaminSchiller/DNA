package Graph.Edges;

import Utils.Keywords;
import Graph.Graph;
import Graph.IWeighted;
import Graph.Nodes.Node;

public class UndirectedDoubleWeightedEdge extends UndirectedEdge implements IWeighted<Double> {
	private double weight;
	
	public UndirectedDoubleWeightedEdge(Node src, Node dst, Double weight) {
		super(src, dst);
		this.setWeight(weight);
	}

	public UndirectedDoubleWeightedEdge(String str, Graph g) {
		super(str.split(Keywords.edgeWeightDelimiter)[0], g);
		if (str.contains(Keywords.edgeWeightDelimiter)) {
			this.weight = Double.parseDouble(str
					.split(Keywords.edgeWeightDelimiter)[1]);
		} else {
			this.weight = 0;
		}
	}	
	
	public UndirectedDoubleWeightedEdge(Node src, Node dst) {
		this(src, dst, 1d);
	}
	
	@Override
	public void setWeight(Double newWeight) {
		this.weight = newWeight;
	}

	@Override
	public Double getWeight() {
		return this.weight;
	}
	
	public String getStringRepresentation() {
		return super.getStringRepresentation() + Keywords.edgeWeightDelimiter
				+ this.weight;
	}

}