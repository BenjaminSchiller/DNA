package dna.graph.edges;

import java.util.HashMap;

import dna.graph.IGraph;
import dna.graph.nodes.DirectedNode;
import dna.graph.nodes.Node;
import dna.graph.weights.IWeightedEdge;
import dna.graph.weights.Weight;

public class DirectedWeightedEdge extends DirectedEdge implements IWeightedEdge {

	protected Weight weight;

	public DirectedWeightedEdge(DirectedNode src, DirectedNode dst,
			Weight weight) {
		super(src, dst);
		this.weight = weight;
	}

	public DirectedWeightedEdge(String s, IGraph g) {
		super(s.split(Weight.WeightDelimiter)[0], g);
		this.weight = g.getGraphDatastructures().newEdgeWeight(
				s.split(Weight.WeightDelimiter)[1]);
	}

	public DirectedWeightedEdge(String s, IGraph g,
			HashMap<Integer, Node> addedNodes) {
		super(s.split(Weight.WeightDelimiter)[0], g, addedNodes);
		this.weight = g.getGraphDatastructures().newEdgeWeight(
				s.split(Weight.WeightDelimiter)[1]);
	}

	public Weight getWeight() {
		return weight;
	}

	public void setWeight(Weight weight) {
		this.weight = weight;
	}

	@Override
	public String asString() {
		return super.asString() + Weight.WeightDelimiter
				+ this.weight.asString();
	}

	public String toString() {
		return super.toString() + " [" + this.getWeight().asString() + "]";
	}

}
