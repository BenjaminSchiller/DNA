package dna.util.parameters;

public abstract class Parameter {

	protected String name;

	public Parameter(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public abstract String getValue();

	public String toString() {
		return this.getName() + " = " + this.getValue();
	}
}
