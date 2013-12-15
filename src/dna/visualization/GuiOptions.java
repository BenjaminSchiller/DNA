package dna.visualization;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.border.TitledBorder;

import dna.util.Config;

public class GuiOptions {
	/** GENERAL SETTINGS **/
	public static final String defaultDir = Config.get("GUI_DEFAULT_DIR");
	public static final String dateFormat = Config.get("GUI_DATE_FORMAT");

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
	public static final String metricVisualizerXAxisType = Config
			.get("GUI_X_AXIS_TYPE");
	public static final String metricVisualizerXAxisFormat = Config
			.get("GUI_X_AXIS_FORMAT");

	// legend item
	public static final Dimension legendItemItemSize = new Dimension(165, 40);
	public static final Dimension legendItemButtonSize = new Dimension(20, 20);
	public static final Dimension legendItemNameLabelSize = new Dimension(160,
			16);

	// legend item distribution
	public static final Dimension legendItemDistValueLabelSize = new Dimension(
			60, 20);
	public static final Dimension legendItemDistButtonPanelSize = new Dimension(
			100, 20);

	// legend item nodevaluelist
	public static final Dimension legendItemNvlValueLabelSize = new Dimension(
			60, 20);
	public static final Dimension legendItemNvlButtonPanelSize = new Dimension(
			100, 20);

	// menu bar
	public static final Dimension menuBarCoordsPanelSize = new Dimension(145,
			45);
	public static final Dimension menuBarXOptionsPanelSize = new Dimension(65,
			45);
	public static final Dimension menuBarYLeftOptionsPanelSize = new Dimension(
			65, 45);
	public static final Dimension menuBarYRightOptionsPanelSize = new Dimension(
			65, 45);
	public static final Dimension menuBarIntervalPanelSize = new Dimension(220,
			45);

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
