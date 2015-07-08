package dna.util.parameters;

public class BooleanParameter extends Parameter {

	private boolean value;

	public BooleanParameter(String name, boolean value) {
		super(name);
		this.value = value;
	}

	@Override
	public String getValue() {
		return Boolean.toString(this.value);
	}

	public boolean getBooleanValue() {
		return this.value;
	}

}
