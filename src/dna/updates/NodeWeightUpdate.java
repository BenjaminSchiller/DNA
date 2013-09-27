package dna.updates;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import dna.graph.Graph;
import dna.graph.edges.Edge;
import dna.graph.nodes.Node;
import dna.graph.weights.IWeighted;
import dna.util.Config;
import dna.util.Log;

public class NodeWeightUpdate<E extends Edge, T> extends NodeUpdate<E> {

	private T weight;

	public NodeWeightUpdate(Node node, T weight) {
		super(node, UpdateType.NodeWeithUpdate);
		this.weight = weight;
	}

	public T getWeight() {
		return this.weight;
	}

	public String toString() {
		return "w(" + this.node + ") = " + this.weight;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean apply(Graph graph) {
		Log.debug("=> " + this.toString());
		((IWeighted<T>) this.node).setWeight(this.weight);
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
		
		return this.node.getStringRepresentation()
				+ Config.get("UPDATE_DELIMITER2") + bos.toString();
	}

}
