package dna.util.parameters;

import dna.util.ArrayUtils;
import dna.util.Log;

public class ParameterList {
	private String name;

	private Parameter[] parameters;

	public ParameterList(String name, Parameter... parameters) {
		this.name = name;
		if (parameters != null) {
			this.parameters = parameters;
		} else {
			this.parameters = new Parameter[0];
		}
	}

	public ParameterList(String name, Parameter[] p1, Parameter... p2) {
		this(name, Parameter.combine(p1, p2));
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

	public static Parameter[] parse(Parameter[] parameters, String[] values) {
		if (parameters.length != values.length) {
			Log.error("parameters and values do not have the same length");
			return null;
		}
		Parameter[] p = new Parameter[parameters.length];
		for (int i = 0; i < parameters.length; i++) {
			if (parameters[i] instanceof DoubleParameter) {
				p[i] = new DoubleParameter(parameters[i].getName(),
						Double.parseDouble(values[i]));
			} else if (parameters[i] instanceof IntParameter) {
				p[i] = new IntParameter(parameters[i].getName(),
						Integer.parseInt(values[i]));
			} else if (parameters[i] instanceof LongParameter) {
				p[i] = new LongParameter(parameters[i].getName(),
						Long.parseLong(values[i]));
			} else if (parameters[i] instanceof ObjectParameter) {
				Log.error("cannot create an instance of an object parameter from a string for parameter "
						+ parameters[i].getName());
				return null;
			} else if (parameters[i] instanceof StringParameter) {
				p[i] = new StringParameter(parameters[i].getName(), values[i]);
			} else {
				Log.error("unknown parameter type: " + parameters[i].getClass());
				return null;
			}
		}
		return p;
	}
}
