package dna.updates;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import dna.graph.Graph;
import dna.graph.IWeighted;
import dna.graph.edges.Edge;
import dna.util.Config;
import dna.util.Log;

public class EdgeWeightUpdate<E extends Edge, T> extends EdgeUpdate<E> {

	private T weight;

	public EdgeWeightUpdate(E edge, T weight) {
		super(edge, UpdateType.EdgeWeightUpdate);
		this.weight = weight;
	}

	public T getWeight() {
		return this.weight;
	}

	public String toString() {
		return "w(" + this.edge + ") = " + this.weight;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean apply(Graph graph) {
		Log.debug("=> " + this.toString());
		((IWeighted<T>) this.edge).setWeight(this.weight);
		return true;
	}

	@Override
	protected String getStringRepresentation_() {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos;
		try {
			oos = new ObjectOutputStream(bos);
			oos.writeObject(this.weight);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return this.edge.getStringRepresentation()
				+ Config.get("UPDATE_DELIMITER2") + bos.toString();
	}

}
