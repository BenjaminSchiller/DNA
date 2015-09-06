package dna.updates.update;

import dna.graph.IGraph;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.edges.DirectedEdge;
import dna.graph.edges.Edge;
import dna.graph.edges.IEdge;
import dna.graph.edges.UndirectedEdge;
import dna.util.Log;

public class EdgeRemoval extends EdgeUpdate {

	public EdgeRemoval(IEdge edge) {
		super(edge);
	}

	public EdgeRemoval(String str, GraphDataStructure gds, IGraph g) {
		super(
				g.getEdge(g.getNode(Integer.parseInt(str
						.split(Update.EdgeSeparator)[0])), g.getNode(Integer
						.parseInt(str.split(Update.EdgeSeparator)[1]))));
	}

	public EdgeRemoval(int index1, int index2, GraphDataStructure gds, IGraph g) {
		super(g.getEdge(g.getNode(index1), g.getNode(index2)));
	}

	@Override
	public boolean apply_(IGraph g) {
		boolean success = g.removeEdge((Edge) this.edge);
		success &= this.edge.disconnectFromNodes();
		return success;
	}

	@Override
	public UpdateType getType() {
		return UpdateType.ER;
	}

	@Override
	protected String asString_() {
		if (this.edge instanceof DirectedEdge) {
			DirectedEdge e = (DirectedEdge) this.edge;
			return e.getSrc().getIndex() + Update.EdgeSeparator
					+ e.getDst().getIndex();
		} else if (this.edge instanceof UndirectedEdge) {
			UndirectedEdge e = (UndirectedEdge) this.edge;
			return e.getNode1().getIndex() + Update.EdgeSeparator
					+ e.getNode2().getIndex();
		} else {
			Log.error("incompatible edge type: " + this.edge.getClass());
			return null;
		}
	}

	@Override
	protected String toString_() {
		return this.edge.toString();
	}

}
