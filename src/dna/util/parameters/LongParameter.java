package dna.util.parameters;

public class LongParameter extends Parameter {

	private long value;

	public LongParameter(String name, long value) {
		super(name);
		this.value = value;
	}

	@Override
	public String getValue() {
		return Long.toString(this.value);
	}

	public long getLongValue() {
		return this.value;
	}

}
