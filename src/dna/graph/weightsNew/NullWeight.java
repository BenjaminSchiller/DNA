package dna.graph.weightsNew;

public class NullWeight extends Weight {

	public NullWeight(String s) {
	}

	public NullWeight(WeightSelection ws) {
	}

	@Override
	protected String asString_() {
		return "NULL";
	}

	@Override
	public WeightType getWeightType() {
		// TODO Auto-generated method stub
		return null;
	}

}
