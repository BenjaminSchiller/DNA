package dna.updates;

import dna.graph.Edge;
import dna.graph.Graph;
import dna.graph.Node;
import dna.graph.WeightedEdge;
import dna.io.etc.Keywords;
import dna.util.Log;

public class EdgeWeightUpdate<E extends Edge> extends EdgeUpdate<E> {

	private double weight;

	public EdgeWeightUpdate(E edge, double weight) {
		super(edge, UpdateType.EdgeWeightUpdate);
		this.weight = weight;
	}

	public double getWeight() {
		return this.weight;
	}

	public String toString() {
		return "w(" + this.edge + ") = " + this.weight;
	}

	@Override
	public boolean apply(Graph<? extends Node<E>, ? extends E> graph) {
		Log.debug("=> " + this.toString());
		((WeightedEdge) this.edge).setWeight(this.weight);
		return true;
	}

	@Override
	protected String getStringRepresentation_() {
		return this.edge.getStringRepresentation() + Keywords.updateDelimiter2
				+ this.weight;
	}

}
