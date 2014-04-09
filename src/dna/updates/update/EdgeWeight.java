package dna.updates.update;

import dna.graph.Graph;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.edges.DirectedEdge;
import dna.graph.edges.Edge;
import dna.graph.edges.IEdge;
import dna.graph.edges.UndirectedEdge;
import dna.graph.weightsNew.IWeightedEdge;
import dna.graph.weightsNew.Weight;
import dna.util.Log;

public class EdgeWeight extends EdgeUpdate {

	protected Weight weight;

	public Weight getWeight() {
		return this.weight;
	}

	public EdgeWeight(IWeightedEdge edge, Weight weight) {
		super(edge);
		this.weight = weight;
	}

	public EdgeWeight(String str, GraphDataStructure gds, Graph g) {
		super(null);
		String[] temp1 = str.split(Update.WeightDelimiter);
		String[] temp2 = temp1[0].split(Update.EdgeSeparator);
		IEdge edge = g.getEdge(gds.newEdgeInstance(
				g.getNode(Integer.parseInt(temp2[0])),
				g.getNode(Integer.parseInt(temp2[1]))));
		this.edge = edge;
		this.weight = Weight.fromString(temp1[1]);
	}

	@Override
	public boolean apply_(Graph g) {
		((IWeightedEdge) this.edge).setWeight(this.weight);
		return g.getEdge((Edge) this.edge) == this.edge;
	}

	@Override
	public UpdateType getType() {
		return UpdateType.EDGE_WEIGHT;
	}

	@Override
	protected String asString_() {
		if (this.edge instanceof DirectedEdge) {
			DirectedEdge e = (DirectedEdge) this.edge;
			return e.getSrc() + Update.EdgeSeparator + e.getDst();
		} else if (this.edge instanceof UndirectedEdge) {
			UndirectedEdge e = (UndirectedEdge) this.edge;
			return e.getNode1() + Update.EdgeSeparator + e.getNode2();
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
