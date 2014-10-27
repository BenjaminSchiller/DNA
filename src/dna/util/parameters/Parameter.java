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

	public static Parameter[] combine(Parameter[] p1, Parameter[] p2) {
		if (p1 != null && p2 != null) {
			Parameter[] p = new Parameter[p1.length + p2.length];
			System.arraycopy(p1, 0, p, 0, p1.length);
			System.arraycopy(p2, 0, p, p1.length, p2.length);
			return p;
		}
		if (p1 != null) {
			return p1;
		}
		if (p2 != null) {
			return p2;
		}
		return new Parameter[0];
	}
}
