package dna.graph.weights.intW;

import dna.graph.weights.ITypedWeight;
import dna.graph.weights.Weight;

public class TypedInt3dWeight extends Int3dWeight implements ITypedWeight {

	private String type;

	@Override
	public String getType() {
		return type;
	}

	public TypedInt3dWeight(String type, int x, int y, int z) {
		super(x, y, z);
		this.type = type;
	}

	public TypedInt3dWeight() {
		super();
		this.type = "";
	}

	public TypedInt3dWeight(String str) {
		super(str.split(Weight.WeightSeparator, 2)[1]);
		this.type = str.split(Weight.WeightSeparator, 2)[0];
	}

	public TypedInt3dWeight(WeightSelection ws) {
		super(ws);
		this.type = "";
	}

	@Override
	public String asString() {
		return this.type + Weight.WeightSeparator + super.asString();
	}

}
