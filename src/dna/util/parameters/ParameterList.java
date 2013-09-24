package dna.util.parameters;

public class ParameterList {
	private String name;

	private Parameter[] parameters;

	public ParameterList(String name, Parameter[] parameters) {
		this.name = name;
		if (parameters != null) {
			this.parameters = parameters;
		} else {
			this.parameters = new Parameter[0];
		}
	}

	public String getName() {
		StringBuffer buff = new StringBuffer(this.name);
		for (Parameter p : this.parameters) {
			buff.append("-" + p.getValue());
		}
		return buff.toString();
	}

	public String getDescription() {
		StringBuffer buff = new StringBuffer(this.name);
		for (Parameter p : this.parameters) {
			buff.append(" " + p.getName() + "=" + p.getValue());
		}
		return buff.toString();
	}

	public Parameter[] getParameters() {
		return this.parameters;
	}

	public static Parameter[] getParameters(ParameterList[] lists) {
		int length = 0;
		for (ParameterList pl : lists) {
			length += pl.getParameters().length;
		}
		Parameter[] params = new Parameter[length];
		int index = 0;
		for (ParameterList pl : lists) {
			for (Parameter p : pl.getParameters()) {
				params[index++] = p;
			}
		}
		return params;
	}
}
