package dna.visualization.config.graph.toolTips;

import java.util.ArrayList;

import dna.util.parameters.Parameter;
import dna.visualization.config.JSON.JSONArray;
import dna.visualization.config.JSON.JSONObject;
import dna.visualization.config.graph.rules.GraphStyleRuleConfig;

/**
 * Configuration object representing a single ToolTip
 * 
 * @author Rwilmes
 * 
 */
public class ToolTipConfig {

	protected String key;
	protected String name;
	protected int index;
	protected Parameter[] params;

	public ToolTipConfig(String key, String name, int index, Parameter[] params) {
		this.key = key;
		this.name = (name == null) ? key : name;
		this.index = index;
		this.params = (params == null) ? new Parameter[0] : params;
	}

	public String getKey() {
		return key;
	}

	public String getName() {
		return name;
	}

	public int getIndex() {
		return index;
	}

	public Parameter[] getParams() {
		return params;
	}

	/** Parses a GraphStyleRuleConfig from a JSONObject. **/
	public static ToolTipConfig getFromJSONObject(String key, JSONObject o) {
		String[] keys = JSONObject.getNames(o);

		String name = null;
		int index = -1;

		ArrayList<Parameter> paramsList = new ArrayList<Parameter>();

		if (keys != null) {
			for (int i = 0; i < keys.length; i++) {
				String k = keys[i];

				// switch on key, detect default config fields like Name
				switch (k) {
				case "Name":
					name = o.getString(k);
					break;
				case "Index":
					index = o.getInt(k);
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
						paramsList.add(GraphStyleRuleConfig
								.getParameterFromObject(k, o.get(k)));
					}
				}
			}
		}

		return new ToolTipConfig(key, name, index,
				paramsList.toArray(new Parameter[paramsList.size()]));
	}
}
