package dna.visualization.config.graph.rules;

import java.util.ArrayList;

import dna.visualization.config.JSON.JSONObject;

/**
 * Configuration object holding an list of GraphStyleRuleConfigs.
 * 
 * @author Rwilmes
 * 
 */
public class RulesConfig {

	protected ArrayList<GraphStyleRuleConfig> rules;

	public RulesConfig() {
		this(new ArrayList<GraphStyleRuleConfig>());
	}

	public RulesConfig(ArrayList<GraphStyleRuleConfig> rules) {
		this.rules = rules;
	}

	public ArrayList<GraphStyleRuleConfig> getRules() {
		return rules;
	}

	public void read(String dir, String filename) {
		// TODO!
	}

	public void write(String dir, String filename) {
		// TODO!
	}

	public static RulesConfig getFromJSONObject(JSONObject o) {
		ArrayList<GraphStyleRuleConfig> rules = new ArrayList<GraphStyleRuleConfig>();

		if (JSONObject.getNames(o) != null) {
			for (String s : JSONObject.getNames(o)) {
				GraphStyleRuleConfig rCfg = GraphStyleRuleConfig
						.getFromJSONObject(s, o.getJSONObject(s));
				rules.add(rCfg);
			}
		}

		return new RulesConfig(rules);
	}
}
