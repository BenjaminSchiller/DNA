package dna.updates;

import dna.graph.Edge;
import dna.graph.Graph;
import dna.graph.Node;
import dna.graph.WeightedNode;
import dna.util.Log;

public class NodeWeightUpdate<E extends Edge> extends NodeUpdate<E> {

	private double weight;

	public NodeWeightUpdate(Node<E> node, double weight) {
		super(node, UpdateType.NodeWeithUpdate);
		this.weight = weight;
	}

	public double getWeight() {
		return this.weight;
	}

	public String toString() {
		return "w(" + this.node + ") = " + this.weight;
	}

	@Override
	public boolean apply(Graph<? extends Node<E>, ? extends E> graph) {
		Log.debug("=> " + this.toString());
		((WeightedNode) this.node).setWeight(this.weight);
		return true;
	}

}
