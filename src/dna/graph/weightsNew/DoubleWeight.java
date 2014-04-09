package dna.graph.weightsNew;

/**
 * 
 * weight holding a single double value.
 * 
 * @author benni
 * 
 */
public class DoubleWeight extends Weight {

	private double weight;

	public double getWeight() {
		return this.weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public DoubleWeight(double weight) {
		this.weight = weight;
	}

	public DoubleWeight() {
		this(0);
	}

	public DoubleWeight(String str) {
		this.weight = Double.parseDouble(str);
	}

	@Override
	protected String asString_() {
		return Double.toString(this.weight);
	}

	@Override
	public WeightType getWeightType() {
		return WeightType.D;
	}

}
