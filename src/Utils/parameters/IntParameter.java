package Utils.parameters;

public class IntParameter extends Parameter {

	private int value;

	public IntParameter(String name, int value) {
		super(name);
		this.value = value;
	}

	@Override
	public String getValue() {
		return Integer.toString(this.value);
	}

	public int getIntValue() {
		return this.value;
	}

}
