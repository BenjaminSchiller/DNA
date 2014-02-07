package dna.graph.edges;

import dna.graph.Element;

public abstract class Edge extends Element implements IEdge {
	public int hashCode() {
		return this.getHashString().hashCode();
	}
}
