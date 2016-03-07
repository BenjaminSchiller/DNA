package dna.graph.weights;

public class TypedWeight extends Weight implements ITypedWeight {

	private String type;

	@Override
	public String getType() {
		return this.type;
	}

	public TypedWeight(String type) {
		this.type = type;
	}

	public TypedWeight() {
		this("");
	}

	public TypedWeight(WeightSelection ws) {
		this("");
	}

	@Override
	public String asString() {
		return this.type;
	}

}
