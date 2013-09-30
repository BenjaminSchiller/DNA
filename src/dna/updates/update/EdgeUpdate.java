package dna.updates.update;

import dna.graph.edges.IEdge;

public abstract class EdgeUpdate extends Update {

	protected IEdge edge;

	public EdgeUpdate(UpdateType type, IEdge edge) {
		super(type);
		this.edge = edge;
	}

	public IEdge getEdge() {
		return this.edge;
	}

	@Override
	protected String getStringRepresentation_() {
		return this.edge.getStringRepresentation();
	}

	@Override
	protected String toString_() {
		return this.edge.toString();
	}

}
