package Utils.parameters;

public class DoubleParameter extends Parameter {

	private double value;

	public DoubleParameter(String name, double value) {
		super(name);
		this.value = value;
	}

	@Override
	public String getValue() {
		return Double.toString(this.value);
	}

	public double returnDoubleValue() {
		return this.value;
	}

}
