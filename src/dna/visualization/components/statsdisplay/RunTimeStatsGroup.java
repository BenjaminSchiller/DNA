package dna.visualization.components.statsdisplay;

import java.util.HashSet;

import dna.visualization.config.components.StatsDisplayConfig.RunTimeConfig;

/**
 * Extends the StatsGroup class for the use with runtimes. A
 * showDefinedValuesOnly policy define which runtimes will be shown or hidden.
 * 
 * Note: When showDefinedValuesOnly is set true, all runtimes defined in the
 * HashSet names, will be shown, but no others. When showDefinedValuesOnly is
 * false, all runtimes will be shown, except those defined in the HashSet names.
 * 
 * @author Rwilmes
 */
public class RunTimeStatsGroup extends StatsGroup {

	// names
	private HashSet<String> names;

	// policy
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
