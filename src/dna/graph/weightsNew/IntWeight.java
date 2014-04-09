package dna.graph.weightsNew;

/**
 * 
 * weight holding a single int value.
 * 
 * @author benni
 * 
 */
public class IntWeight extends Weight {

	private int weight;

	public int getWeight() {
		return this.weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	public IntWeight(int weight) {
		this.weight = weight;
	}

	public IntWeight() {
		this(0);
	}

	public IntWeight(String str) {
		this.weight = Integer.parseInt(str);
	}

	@Override
	protected String asString_() {
		return Integer.toString(this.weight);
	}

	@Override
	public WeightType getWeightType() {
		return WeightType.I;
	}

}
