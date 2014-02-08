package dna.visualization.components.statsdisplay;

import java.util.HashSet;

import dna.visualization.config.components.StatsDisplayConfig.RunTimeConfig;

public class RunTimeStatsGroup extends StatsGroup {

	// names
	private HashSet<String> names;

	// poliy
	private boolean showDefinedValuesOnly;

	public RunTimeStatsGroup(RunTimeConfig config) {
		super(config.getName());
		this.showDefinedValuesOnly = config.isAllShown();
		this.names = new HashSet<String>();
		for (String name : config.getNames())
			this.names.add(name);
	}

	/** add values to the panel **/
	@Override
	public void addValue(String name, double value) {
		if (showDefinedValuesOnly) {
			if (this.names.contains(name)) {
				super.addValue(name, value);
			}
		} else {
			if (!this.names.contains(name)) {
				super.addValue(name, value);
			}
		}
	}
}
