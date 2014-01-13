package dna.visualization.config;

/**
 * example config for general settings that affect all
 * metrics/runtimes/statistics etc.
 **/
public class Config2 extends VisualizerListConfig {
	public Config2() {

		// add all metrics with this values
		MetricVisualizerItem generalMetricConfig = new MetricVisualizerItem(
				null, DisplayMode.linespoint, yAxisSelection.y1,
				GraphVisibility.shown);
		this.setAllMetrics(generalMetricConfig);

		// add all general statistics with this config
		MetricVisualizerItem generalStatisticsConfig = new MetricVisualizerItem(
				null, DisplayMode.linespoint, yAxisSelection.y2,
				GraphVisibility.hidden);
		this.setAllStatistics(generalStatisticsConfig);

		// add all distributions with this config
		MultiScalarDistributionItem generalDistributionConfig = new MultiScalarDistributionItem(
				null, SortModeDist.distribution, xAxisSelection.x1,
				yAxisSelection.y1, DisplayMode.bars, GraphVisibility.shown);
		this.setAllDistributions(generalDistributionConfig);

		// add all nodevaluelists with this config
		MultiScalarNodeValueListItem generalNodeValueListConfig = new MultiScalarNodeValueListItem(
				null, SortModeNVL.ascending, xAxisSelection.x2,
				yAxisSelection.y2, DisplayMode.linespoint,
				GraphVisibility.shown);
		this.setAllNodeValueLists(generalNodeValueListConfig);
	}
}
