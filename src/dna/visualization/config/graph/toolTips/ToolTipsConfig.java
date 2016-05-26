package dna.visualization.config.graph.toolTips;

import java.util.ArrayList;

import dna.visualization.config.JSON.JSONObject;

/**
 * Configuration object holding an list of GraphStyleRuleConfigs.
 * 
 * @author Rwilmes
 * 
 */
public class ToolTipsConfig {

	protected ArrayList<ToolTipConfig> toolTips;

	protected boolean enabled;

	public ToolTipsConfig(boolean enabled, ArrayList<ToolTipConfig> toolTips) {
		this.enabled = enabled;
		this.toolTips = toolTips;
	}

	public ArrayList<ToolTipConfig> getToolTips() {
		return toolTips;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public static ToolTipsConfig getFromJSONObject(JSONObject o) {
		ArrayList<ToolTipConfig> toolTips = new ArrayList<ToolTipConfig>();

		boolean enabled = true;

		if (JSONObject.getNames(o) != null) {
			for (String s : JSONObject.getNames(o)) {
				if (s.equals("Enabled")) {
					enabled = o.getBoolean(s);
				} else {
					ToolTipConfig ttCfg = ToolTipConfig.getFromJSONObject(s,
							o.getJSONObject(s));
					toolTips.add(ttCfg);
				}
			}
		}

		return new ToolTipsConfig(enabled, toolTips);
	}
}
