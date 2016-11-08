package dna.graph.weights.network;

import dna.graph.weights.Weight;

/**
 * Represents the different types a node can have inside a network-graph.
 * 
 * @author Rwilmes
 * 
 */
public class NetworkWeight extends Weight {

	public enum ElementType {
		HOST, PORT, PROT, CONNECTION, UNKNOWN
	};

	private ElementType weight;

	// constructor
	public NetworkWeight(ElementType weight) {
		this.weight = weight;
	}

	public NetworkWeight(String str) {
		if (str.equals("HOST"))
			this.weight = ElementType.HOST;
		if (str.equals("PORT"))
			this.weight = ElementType.PORT;
		if (str.equals("PROT"))
			this.weight = ElementType.PROT;
		if (str.equals("CONNECTION"))
			this.weight = ElementType.CONNECTION;
	}

	public NetworkWeight(WeightSelection ws) {
		this.weight = ElementType.UNKNOWN;
	}

	public ElementType getWeight() {
		return weight;
	}

	public void setWeight(ElementType weight) {
		this.weight = weight;
	}

	@Override
	public String asString() {
		return this.weight.toString();
	}

}
