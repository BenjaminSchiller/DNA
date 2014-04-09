package dna.updates.update;

import dna.graph.edges.IEdge;

public abstract class EdgeUpdate extends Update {

	protected IEdge edge;

	public IEdge getEdge() {
		return this.edge;
	}

	public EdgeUpdate(IEdge edge) {
		this.edge = edge;
	}

	@Override
	protected int hashCode_() {
		return this.edge.hashCode();
	}

}
