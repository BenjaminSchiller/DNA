package dna.util.parameters;

import dna.util.ArrayUtils;

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

	public String getNamePlain() {
		return this.name;
	}

	public Parameter[] getParameters() {
		return this.parameters;
	}

	protected static String combineNames(ParameterList... pls) {
		StringBuffer buff = new StringBuffer("COMB");
		for (ParameterList pl : pls) {
			buff.append("_" + pl.getNamePlain());
		}
		return buff.toString();
	}

	protected static Parameter[] combineParameters(ParameterList... pls) {
		Parameter[] p = new Parameter[0];
		for (ParameterList pl : pls) {
			p = ArrayUtils.append(p, pl.getParameters());
		}
		return p;
	}
}
