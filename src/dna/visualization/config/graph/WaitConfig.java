package dna.visualization.config.graph;

import dna.visualization.config.JSON.JSONObject;

/**
 * Configures the wait-times of a graph-panel in milliseconds.
 * 
 * @author Rwilmes
 * 
 */
public class WaitConfig {

	protected boolean enabled;

	protected long nodeAddition;
	protected long nodeRemoval;
	protected long nodeWeightChange;

	protected long edgeAddition;
	protected long edgeRemoval;
	protected long edgeWeightChange;

	public WaitConfig(boolean enabled, long nodeAddition, long nodeRemoval,
			long nodeWeightChange, long edgeAddition, long edgeRemoval,
			long edgeWeightChange) {
		this.enabled = enabled;
		this.nodeAddition = nodeAddition;
		this.nodeRemoval = nodeRemoval;
		this.nodeWeightChange = nodeWeightChange;

		this.edgeAddition = edgeAddition;
		this.edgeRemoval = edgeRemoval;
		this.edgeWeightChange = edgeWeightChange;
	}

	/** Creates a main display config object from a given json object. **/
	public static WaitConfig getFromJSONObject(JSONObject o) {
		GraphPanelConfig defaultGraphPanelConfig = GraphPanelConfig.defaultGraphPanelConfig;

		// init values
		boolean enabled = true;
		long nodeAddition = 20;
		long nodeRemoval = 20;
		long nodeWeightChange = 10;

		long edgeAddition = 20;
		long edgeRemoval = 20;
		long edgeWeightChange = 10;

		if (defaultGraphPanelConfig != null) {
			// read default values
			WaitConfig def = defaultGraphPanelConfig.getWaitConfig();
			enabled = def.isEnabled();
			nodeAddition = def.getNodeAddition();
			nodeRemoval = def.getNodeRemoval();
			nodeWeightChange = def.getNodeWeightChange();
			edgeAddition = def.getEdgeAddition();
			edgeRemoval = def.getEdgeRemoval();
			edgeWeightChange = def.getEdgeWeightChange();
		}

		if (JSONObject.getNames(o) != null) {
			for (String s : JSONObject.getNames(o)) {
				switch (s) {
				case "EdgeAddition":
					edgeAddition = o.getLong(s);
					break;
				case "EdgeRemoval":
					edgeRemoval = o.getLong(s);
					break;
				case "EdgeWeightChange":
					edgeWeightChange = o.getLong(s);
					break;
				case "Enabled":
					enabled = o.getBoolean(s);
					break;
				case "NodeAddition":
					nodeAddition = o.getLong(s);
					break;
				case "NodeRemoval":
					nodeRemoval = o.getLong(s);
					break;
				case "NodeWeightChange":
					nodeWeightChange = o.getLong(s);
					break;
				}
			}
		}

		return new WaitConfig(enabled, nodeAddition, nodeRemoval,
				nodeWeightChange, edgeAddition, edgeRemoval, edgeWeightChange);
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public long getNodeAddition() {
		return nodeAddition;
	}

	public void setNodeAddition(long nodeAddition) {
		this.nodeAddition = nodeAddition;
	}

	public long getNodeRemoval() {
		return nodeRemoval;
	}

	public void setNodeRemoval(long nodeRemoval) {
		this.nodeRemoval = nodeRemoval;
	}

	public long getNodeWeightChange() {
		return nodeWeightChange;
	}

	public void setNodeWeightChange(long nodeWeightChange) {
		this.nodeWeightChange = nodeWeightChange;
	}

	public long getEdgeAddition() {
		return edgeAddition;
	}

	public void setEdgeAddition(long edgeAddition) {
		this.edgeAddition = edgeAddition;
	}

	public long getEdgeRemoval() {
		return edgeRemoval;
	}

	public void setEdgeRemoval(long edgeRemoval) {
		this.edgeRemoval = edgeRemoval;
	}

	public long getEdgeWeightChange() {
		return edgeWeightChange;
	}

	public void setEdgeWeightChange(long edgeWeightChange) {
		this.edgeWeightChange = edgeWeightChange;
	}

}
