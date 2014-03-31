package dna.updates.update;

import dna.graph.Graph;
import dna.graph.edges.Edge;
import dna.graph.edges.IWeightedEdge;
import dna.graph.weights.IWeighted;
import dna.util.Config;

public class EdgeWeight extends EdgeUpdate {

	private Object weight;

	public EdgeWeight(IWeightedEdge edge, Object weight) {
		super(UpdateType.EDGE_WEIGHT, edge);
		this.weight = weight;
	}

	@Override
	public boolean apply_(Graph g) {
		((IWeighted) this.edge).setWeight(this.weight);
		return g.getEdge((Edge) this.edge) == this.edge;
	}

	@Override
	protected String getStringRepresentation_() {
		return super.getStringRepresentation_()
				+ Config.get("UPDATE_DELIMITER2") + this.weight;
	}

	@Override
	protected String toString_() {
		return super.toString_() + " [" + this.weight + "]";
	}

	public Object getWeight() {
		return this.weight;
	}

}
