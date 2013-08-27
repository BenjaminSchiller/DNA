package dna.updates;

import dna.graph.Graph;
import dna.graph.edges.DirectedEdge;
import dna.graph.edges.Edge;
import dna.graph.edges.UndirectedEdge;
import dna.util.Log;

public class EdgeAddition<E extends Edge> extends EdgeUpdate<E> {

	public EdgeAddition(E edge) {
		super(edge, UpdateType.EdgeAddition);
	}

	public String toString() {
		return "add " + this.edge;
	}

	@Override
	public boolean apply(Graph graph) {
		Log.debug("=> " + this.toString());
		if (this.edge instanceof DirectedEdge) {
			boolean success = graph.addEdge((E) this.edge);
			DirectedEdge e = (DirectedEdge) this.edge;
			success &= e.getSrc().addEdge(e);
			success &= e.getDst().addEdge(e);
			return success;
		} else if (this.edge instanceof UndirectedEdge) {
			boolean success = graph.addEdge((E) this.edge);
			UndirectedEdge e = (UndirectedEdge) this.edge;
			success &= e.getNode1().addEdge(e);
			success &= e.getNode2().addEdge(e);
			return success;
		}
		Log.error("EdgeAddition - unkown edge type " + this.edge.getClass());
		return false;
	}

	@Override
	protected String getStringRepresentation_() {
		return this.edge.getStringRepresentation();
	}

}
