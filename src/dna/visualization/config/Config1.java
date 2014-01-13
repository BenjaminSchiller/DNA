package dna.visualization.config;

/**
 * example config for specific settings that affect only chosen
 * metrics/distributions etc.
 **/
public class Config1 extends VisualizerListConfig {

	public Config1() {
		// METRIC VISUALIZER ITEMS
		MetricVisualizerItem item1 = new MetricVisualizerItem(
				"UndirectedClusteringCoefficientU.exact.globalCC");
		MetricVisualizerItem item2 = new MetricVisualizerItem(
				"UndirectedClusteringCoefficientU.exact.averageCC");

		MetricVisualizerItem item3 = new MetricVisualizerItem(
				"statistics.edges", DisplayMode.linespoint, yAxisSelection.y2,
				GraphVisibility.hidden);
		MetricVisualizerItem item4 = new MetricVisualizerItem(
				"statistics.nodes", DisplayMode.linespoint, yAxisSelection.y2,
				GraphVisibility.hidden);

		// MULTI SCALAR VISUALIZER ITEMS
		MultiScalarDistributionItem item5 = new MultiScalarDistributionItem(
				"DegreeDistributionU.exact.degreeDistribution",
				SortModeDist.cdf, xAxisSelection.x1, yAxisSelection.y1,
				DisplayMode.bars, GraphVisibility.shown);

		MultiScalarNodeValueListItem item6 = new MultiScalarNodeValueListItem(
				"UndirectedClusteringCoefficientU.exact.localCC",
				SortModeNVL.index, xAxisSelection.x2, yAxisSelection.y2,
				DisplayMode.bars, GraphVisibility.shown);

		// build config array
		ConfigItem[] entries = { item1, item2, item3, item4, item5, item6 };
		this.addConfigItems(entries);
	}
}
