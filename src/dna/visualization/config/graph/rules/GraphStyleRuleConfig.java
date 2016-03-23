package dna.visualization.config.graph.rules;

import java.util.ArrayList;

import dna.util.Config;
import dna.util.parameters.BooleanParameter;
import dna.util.parameters.DoubleParameter;
import dna.util.parameters.IntParameter;
import dna.util.parameters.LongParameter;
import dna.util.parameters.ObjectParameter;
import dna.util.parameters.Parameter;
import dna.util.parameters.StringParameter;
import dna.visualization.config.JSON.JSONArray;
import dna.visualization.config.JSON.JSONObject;

/**
 * Configuration object representing a single GraphStyleRule.<br>
 * <br>
 * 
 * The key is mandatory and identifies which type of rule will be added.<br>
 * The params-array may contain additional configuration-parameters.<br>
 * The enabled-flag decides whether the rule will be enabled or disabled upon
 * init.<br>
 * The hidden-flag decides whether the rule is visible (and therefore
 * accessible) in the rule-hotswap of the visualization.<br>
 * 
 * @author Rwilmes
 * 
 */
public class GraphStyleRuleConfig {

	protected String key;
	protected String name;
	protected Parameter[] params;

	public GraphStyleRuleConfig(String key, Parameter[] params) {
		this(key, null, Config
				.getBoolean("GRAPH_VIS_RULE_CONFIG_DEFAULT_ENABLED"), Config
				.getBoolean("GRAPH_VIS_RULE_CONFIG_DEFAULT_HIDDEN"), params);
	}

	public GraphStyleRuleConfig(String key, String name, boolean enabled,
			boolean hidden, Parameter[] params) {
		this.key = key;
		this.name = (name == null) ? key : name;
		this.params = (params == null) ? new Parameter[0] : params;

		System.out.println("INIT NEW GRAPHSTYLERULECONFIG");
		System.out.println("\tkey: " + key);
		System.out.println("\tname: " + this.name);
		System.out.println("\tenabled: " + enabled);
		System.out.println("\thidden: " + hidden);
		System.out.println("\tparams: " + params.length);
		for (Parameter p : params)
			System.out.println("\t\t" + p);

	}

	public String getKey() {
		return key;
	}

	public String getName() {
		return name;
	}

	public Parameter[] getParams() {
		return params;
	}

	public void read(String dir, String filename) {
		// TODO!
	}

	public void write(String dir, String filename) {
		// TODO!
	}

	/** Parses a GraphStyleRuleConfig from a JSONObject. **/
	public static GraphStyleRuleConfig getFromJSONObject(String key,
			JSONObject o) {
		String[] keys = JSONObject.getNames(o);

		String name = null;
		boolean enabled = Config
				.getBoolean("GRAPH_VIS_RULE_CONFIG_DEFAULT_ENABLED");
		boolean hidden = Config
				.getBoolean("GRAPH_VIS_RULE_CONFIG_DEFAULT_HIDDEN");

		ArrayList<Parameter> paramsList = new ArrayList<Parameter>();

		for (int i = 0; i < keys.length; i++) {
			String k = keys[i];

			// switch on key, detect default config fields like Name, Enabled,
			// Hidden
			switch (k) {
			case "Name":
				name = o.getString(k);
				break;
			case "Enabled":
				enabled = o.getBoolean(k);
				break;
			case "Hidden":
				hidden = o.getBoolean(k);
				break;
			default:
				if (o.get(k) instanceof JSONArray) {
					JSONArray array = o.getJSONArray(k);
					for (int j = 0; j < array.length(); j++) {
						paramsList.add(GraphStyleRuleConfig
								.getParameterFromObject(k + "_" + j,
										array.get(j)));
					}
				} else {
					paramsList.add(GraphStyleRuleConfig.getParameterFromObject(
							k, o.get(k)));
				}
			}
		}

		return new GraphStyleRuleConfig(key, name, enabled, hidden,
				paramsList.toArray(new Parameter[paramsList.size()]));
	}

	/**
	 * Matches objects to types and returns proper parameters.<br>
	 * Will return ObjectParameter if object is not clear.
	 */
	public static Parameter getParameterFromObject(String key, Object o) {
		if (o instanceof Integer)
			return new IntParameter(key, (int) o);
		if (o instanceof Double)
			return new DoubleParameter(key, (double) o);
		if (o instanceof Long)
			return new LongParameter(key, (long) o);
		if (o instanceof String)
			return new StringParameter(key, (String) o);
		if (o instanceof Boolean)
			return new BooleanParameter(key, (boolean) o);

		return new ObjectParameter(key, o);
	}
}
