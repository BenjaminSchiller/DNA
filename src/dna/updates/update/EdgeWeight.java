package dna.updates.update;

import dna.graph.Graph;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.edges.DirectedEdge;
import dna.graph.edges.IEdge;
import dna.graph.edges.UndirectedEdge;
import dna.graph.weights.IWeightedEdge;
import dna.graph.weights.Weight;
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
		IEdge edge = g.getEdge(g.getNode(Integer.parseInt(temp2[0])),
				g.getNode(Integer.parseInt(temp2[1])));
		this.edge = edge;
		this.weight = gds.newEdgeWeight(temp1[1]);
	}

	@Override
	public boolean apply_(Graph g) {
		((IWeightedEdge) this.edge).setWeight(this.weight);
		return g.getEdge(this.edge.getN1(), this.edge.getN2()) == this.edge;
	}

	@Override
	public UpdateType getType() {
		return UpdateType.EW;
	}

	@Override
	protected String asString_() {
		String res;
		if (this.edge instanceof DirectedEdge) {
			DirectedEdge e = (DirectedEdge) this.edge;
			res = e.getSrc().getIndex() + Update.EdgeSeparator + e.getDst().getIndex();
		} else if (this.edge instanceof UndirectedEdge) {
			UndirectedEdge e = (UndirectedEdge) this.edge;
			res = e.getNode1().getIndex() + Update.EdgeSeparator + e.getNode2().getIndex();
		} else {
			Log.error("incompatible edge type: " + this.edge.getClass());
			return null;
		}
		
		res += Update.WeightDelimiter + this.weight;
		
		return res;
	}

	@Override
	protected String toString_() {
		return this.edge.toString();
	}

}
