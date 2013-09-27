package dna.util.parameters;

public class StringParameter extends Parameter {

	private String value;

	public StringParameter(String name, String value) {
		super(name);
		this.value = value;
	}

	@Override
	public String getValue() {
		return this.value;
	}

	public String getStringValue() {
		return this.value;
	}

}
