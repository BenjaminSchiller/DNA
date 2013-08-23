package Graph.Edges;

import Utils.Keywords;
import Graph.IWeighted;
import Graph.ReadableGraph;
import Graph.Nodes.Node;

public class DirectedDoubleWeightedEdge extends DirectedEdge implements IWeighted<Double> {
	private double weight;
	
	public DirectedDoubleWeightedEdge(Node src, Node dst, Double weight) {
		super(src, dst);
		this.setWeight(weight);
	}
	
	public DirectedDoubleWeightedEdge(String str, ReadableGraph g) {
		super(str.split(Keywords.edgeWeightDelimiter)[0], g);
		if (str.contains(Keywords.edgeWeightDelimiter)) {
			this.weight = Double.parseDouble(str
					.split(Keywords.edgeWeightDelimiter)[1]);
		} else {
			this.weight = 0;
		}
		this.setWeight(weight);
	}	
	
	public DirectedDoubleWeightedEdge(Node src, Node dst) {
		this(src, dst, 1d);
	}
	
	public String getStringRepresentation() {
		return super.getStringRepresentation() + Keywords.edgeWeightDelimiter
				+ this.weight;
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
