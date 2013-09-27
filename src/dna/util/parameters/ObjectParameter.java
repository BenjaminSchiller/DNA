package dna.util.parameters;

public class ObjectParameter extends Parameter {

	private Object value;

	public ObjectParameter(String name, Object value) {
		super(name);
		this.value = value;
	}

	@Override
	public String getValue() {
		if(this.value == null){
			return "null";
		}
		return this.value.toString();
	}

	public Object returnObjectValue() {
		return this.value;
	}

}
