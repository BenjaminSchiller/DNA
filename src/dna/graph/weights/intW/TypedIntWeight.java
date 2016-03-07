package dna.graph.weights.intW;

import dna.graph.weights.ITypedWeight;
import dna.graph.weights.Weight;

public class TypedIntWeight extends IntWeight implements ITypedWeight {

	private String type;

	@Override
	public String getType() {
		return this.type;
	}

	public TypedIntWeight(String type, int weight) {
		super(weight);
		this.type = type;
	}

	public TypedIntWeight() {
		super();
		this.type = "";
	}

	public TypedIntWeight(String str) {
		super(str.split(Weight.WeightSeparator, 2)[1]);
		this.type = str.split(Weight.WeightSeparator, 2)[0];
	}

	public TypedIntWeight(WeightSelection ws) {
		super(ws);
		this.type = "";
	}

	@Override
	public String asString() {
		return this.type + Weight.WeightSeparator + super.asString();
	}

}
