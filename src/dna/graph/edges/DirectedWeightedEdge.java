package dna.graph.edges;

import java.util.HashMap;

import dna.graph.Graph;
import dna.graph.nodes.DirectedNode;
import dna.graph.nodes.Node;
import dna.graph.weightsNew.IWeighted;
import dna.graph.weightsNew.Weight;

public class DirectedWeightedEdge extends DirectedEdge implements IWeighted {

	protected Weight weight;

	public Weight getWeight() {
		return weight;
	}

	public void setWeight(Weight weight) {
		this.weight = weight;
	}

	public DirectedWeightedEdge(DirectedNode src, DirectedNode dst,
			Weight weight) {
		super(src, dst);
		this.weight = weight;
	}

	public DirectedWeightedEdge(String s, Graph g) {
		super(s.split(Weight.WeightDelimiter)[0], g);
		this.weight = Weight.fromString(s.split(Weight.WeightDelimiter)[1]);
	}

	public DirectedWeightedEdge(String s, Graph g,
			HashMap<Integer, Node> addedNodes) {
		super(s.split(Weight.WeightDelimiter)[0], g, addedNodes);
		this.weight = Weight.fromString(s.split(Weight.WeightDelimiter)[1]);
	}

	@Override
	public String getStringRepresentation() {
		return super.getStringRepresentation() + Weight.WeightDelimiter
				+ this.weight.asString();
	}

	public String toString() {
		return super.toString() + " [" + this.getWeight().asString() + "]";
	}

}
