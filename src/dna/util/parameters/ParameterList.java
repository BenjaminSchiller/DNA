package dna.util.parameters;

public class ParameterList {
	private String name;

	private Parameter[] params;

	public ParameterList(String name, Parameter[] params) {
		this.name = name;
		this.params = params;
	}

	public String getName() {
		StringBuffer buff = new StringBuffer(this.name);
		for (Parameter p : this.params) {
			buff.append("-" + p.getValue());
		}
		return buff.toString();
	}

	public String getDescription() {
		StringBuffer buff = new StringBuffer(this.name);
		for (Parameter p : this.params) {
			buff.append(" " + p.getName() + "=" + p.getValue());
		}
		return buff.toString();
	}
}
