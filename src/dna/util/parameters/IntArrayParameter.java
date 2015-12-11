package dna.util.parameters;

public class IntArrayParameter extends Parameter {

	private int[] value;

	public IntArrayParameter(String name, int[] value) {
		super(name);
		this.value = value;
	}

	@Override
	public String getValue() {
		StringBuffer buff = new StringBuffer();
		buff.append(this.value[0]);
		for (int i = 1; i < this.value.length; i++) {
			buff.append("-" + this.value[i]);
		}
		return buff.toString();
	}

	public int[] getIntArrayValue() {
		return this.value;
	}

}
