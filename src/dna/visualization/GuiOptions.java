package dna.visualization;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.border.TitledBorder;

import dna.util.Config;
import dna.visualization.config.VisualizerListConfig.DisplayMode;
import dna.visualization.config.VisualizerListConfig.GraphVisibility;
import dna.visualization.config.VisualizerListConfig.SortModeDist;
import dna.visualization.config.VisualizerListConfig.SortModeNVL;
import dna.visualization.config.VisualizerListConfig.xAxisSelection;
import dna.visualization.config.VisualizerListConfig.yAxisSelection;

public class GuiOptions {
	/** GENERAL SETTINGS **/
	public static final String defaultDir = Config.get("GUI_DEFAULT_DIR");
	public static final String dateFormat = Config.get("GUI_DATE_FORMAT");
	public static final String defaultLogDir = Config
			.get("GUI_DEFAULT_LOG_DIR");

	/** SIZES **/
	// main display
	public static final Dimension mainDisplaySize = new Dimension(1680, 800);
	public static final Dimension mainDisplayButtonSize = new Dimension(90, 30);
	public static final Dimension logoSize = new Dimension(270, 160);

	// stats display
	public static final Dimension statsDisplaySize = new Dimension(300, 420);
	public static final Dimension statsDisplaySettingsPanelSize = new Dimension(
			285, 150);
	public static final Dimension statsDisplayButtonSize = new Dimension(20, 20);

	// visualizer
	public static final Dimension visualizerDefaultSize = new Dimension(670,
			410);
	public static final Dimension visualizerDefaultMenuBarSize = new Dimension(
			655, 50);
	public static final Dimension visualizerDefaultLegendSize = new Dimension(
			190, 330);
	public static final Dimension visualizerDefaultChartSize = new Dimension(
			450, 320);
	public static final int visualizerDefaultTraceLength = Config
			.getInt("GUI_TRACE_LENGTH");
	public static final String visualizerDefaultX1AxisTitle = "Timestamp";
	public static final String visualizerDefaultX2AxisTitle = "Timestamp";
	public static final String visualizerDefaultY1AxisTitle = "y1";
	public static final String visualizerDefaultY2AxisTitle = "y2";
	public static final String metricVisualizerXAxisType = Config
			.get("GUI_X_AXIS_TYPE");
	public static final String metricVisualizerXAxisFormat = Config
			.get("GUI_X_AXIS_FORMAT");
	public static final int metricVisualizerBatchBufferSize = Config
			.getInt("GUI_BATCH_BUFFER_SIZE");
	public static final double metricVisualizerXAxisOffset = Config
			.getDouble("GUI_METRIC_VISUALIZER_X_OFFSET");
	public static final double multiScalarVisualizerXAxisOffset = Config
			.getDouble("GUI_MULTISCALAR_VISUALIZER_X_OFFSET");

	// metric visualizer defaults
	public static final String metricVisualizerDefaultTitle = "Metric Visualizer";
	public static final DisplayMode metricVisualizerDefaultDisplayMode = DisplayMode.linespoint;
	public static final yAxisSelection metricVisualizerDefaultYAxisSelection = yAxisSelection.y1;
	public static final GraphVisibility metricVisualizerDefaultGraphVisibility = GraphVisibility.shown;

	// multi scalar visualizer defaults
	public static final DisplayMode multiScalarVisualizerDefaultDistributionDisplayMode = DisplayMode.bars;
	public static final SortModeDist multiScalarVisualizerDefaultDistributionSortMode = Config
			.getSortModeDist("GUI_SORT_MODE_DIST");
	public static final xAxisSelection multiScalarVisualizerDefaultDistXAxisSelection = Config
			.getXAxisSelection("GUI_DIST_X_AXIS");
	public static final yAxisSelection multiScalarVisualizerDefaultDistYAxisSelection = Config
			.getYAxisSelection("GUI_DIST_Y_AXIS");

	public static final DisplayMode multiScalarVisualizerDefaultNodeValueListDisplayMode = DisplayMode.linespoint;
	public static final SortModeNVL multiScalarVisualizerDefaultNodeValueListSortMode = Config
			.getSortModeNVL("GUI_SORT_MODE_NVL");
	public static final xAxisSelection multiScalarVisualizerDefaultNVLXAxisSelection = Config
			.getXAxisSelection("GUI_NVL_X_AXIS");
	public static final yAxisSelection multiScalarVisualizerDefaultNVLYAxisSelection = Config
			.getYAxisSelection("GUI_NVL_Y_AXIS");

	public static final GraphVisibility multiScalarVisualizerDefaultGraphVisibility = GraphVisibility.shown;

	// legend item
	public static final Dimension legendItemItemSize = new Dimension(165, 40);
	public static final Dimension legendItemButtonSize = new Dimension(20, 20);
	public static final Dimension legendItemNameLabelSize = new Dimension(160,
			16);

	// legend item distribution
	public static final Dimension legendItemDistValueLabelSize = new Dimension(
			40, 20);
	public static final Dimension legendItemDistButtonPanelSize = new Dimension(
			120, 20);

	// legend item nodevaluelist
	public static final Dimension legendItemNvlValueLabelSize = new Dimension(
			40, 20);
	public static final Dimension legendItemNvlButtonPanelSize = new Dimension(
			120, 20);

	// menu bar
	public static final Dimension menuBarCoordsPanelSize = new Dimension(145,
			45);
	public static final Dimension menuBarXOptionsPanelSize = new Dimension(65,
			45);
	public static final Dimension menuBarYOptionsPanelSize = new Dimension(
			65, 45);
	public static final Dimension menuBarYRightOptionsPanelSize = new Dimension(
			65, 45);
	public static final Dimension menuBarIntervalPanelSize = new Dimension(220,
			45);

	// LogDisplay
	public static final String logDefaultTitle = "LogDisplay";
	public static final long logDefaultUpdateInterval = 300;
	public static final Dimension logDefaultTextFieldSize = new Dimension(380,
			80);
	public static final boolean logDefaultShowInfo = true;
	public static final boolean logDefaultShowWarning = true;
	public static final boolean logDefaultShowError = true;
	public static final boolean logDefaultShowDebug = false;

	/** FONTS AND BORDERS **/
	public static final Font defaultFont = Config.getFont("GUI_DEFAULT_FONT");
	public static final Color defaultFontColor = Config
			.getColor("GUI_DEFAULT_FONT_COLOR");
	public static final Font defaultFontBorders = Config
			.getFont("GUI_DEFAULT_BORDER_FONT");
	public static final Color defaultFontBordersColor = Config
			.getColor("GUI_DEFAULT_BORDER_FONT_COLOR");
	public static final Font menuBarCoordsFont = Config
			.getFont("GUI_COORDS_FONT");
	public static final Color menuBarCoordsFontColor = Config
			.getColor("GUI_COORDS_FONT_COLOR");
	public static final TitledBorder menuBarItemBorder = BorderFactory
			.createTitledBorder("");
	public static final Font legendItemValueFont = Config
			.getFont("GUI_VALUE_FONT");
	public static final Color legendItemValueFontColor = Config
			.getColor("GUI_VALUE_FONT_COLOR");
}
