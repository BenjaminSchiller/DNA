package dna.graph.weights.intW;

import dna.graph.weights.ITypedWeight;
import dna.graph.weights.Weight;

public class TypedInt2dWeight extends Int2dWeight implements ITypedWeight {

	private String type;

	@Override
	public String getType() {
		return this.type;
	}

	public TypedInt2dWeight(String type, int x, int y) {
		super(x, y);
		this.type = type;
	}

	public TypedInt2dWeight() {
		super();
		this.type = "";
	}

	public TypedInt2dWeight(String str) {
		super(str.split(Weight.WeightSeparator, 2)[1]);
		this.type = str.split(Weight.WeightSeparator, 2)[0];
	}

	public TypedInt2dWeight(WeightSelection ws) {
		super(ws);
		this.type = "";
	}

	@Override
	public String asString() {
		return this.type + Weight.WeightSeparator + super.asString();
	}

}
