package dna.visualization.components.visualizer;

import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

import dna.visualization.MainDisplay;
import dna.visualization.config.VisualizerListConfig.yAxisSelection;
import dna.visualization.config.components.LabelVisualizerConfig;
import dna.visualization.config.components.MetricVisualizerConfig;
import dna.visualization.config.components.MultiScalarVisualizerConfig;

/**
 * An item in the legendlist.
 * 
 * @author Rwilmes
 * 
 */
@SuppressWarnings("serial")
public class LegendItem extends JPanel {
	// general options
	protected LegendItem thisItem;
	protected LegendList parent;
	protected Color color;
	protected yAxisSelection yAxis;

	// text panel containing the name and value
	protected JLabel nameLabel;
	protected JLabel valueLabel;

	// buttons
	protected JButton toggleYAxisButton;
	protected JButton removeButton;
	protected JButton showHideButton;
	protected JButton displayModeButton;

	// optionspanel containing several buttons and options
	protected JPanel buttonPanel;

	// configuration
	protected Dimension size;
	protected Dimension buttonSize;
	protected Dimension buttonPanelSize;
	protected Dimension nameLabelSize;
	protected Dimension valueLabelSize;
	protected Font valueFont;
	protected Color valueFontColor;

	// constructor
	public LegendItem(LegendList parent, String name, Color color) {
		// init default parameters and variables
		this.parent = parent;
		this.thisItem = this;
		this.setName(name);
		this.color = color;

		// read config values
		if (parent.parent.parent instanceof MetricVisualizer) {
			MetricVisualizerConfig config = ((MetricVisualizer) parent.parent.parent).config;
			this.buttonPanelSize = config.getLegendItemButtonPanelSize();
			this.buttonSize = config.getLegendItemButtonSize();
			this.size = config.getLegendItemSize();
			this.nameLabelSize = config.getLegendItemNameLabelSize();
			this.valueLabelSize = config.getLegendItemValueLabelSize();
			this.valueFont = config.getLegendItemValueFont();
			this.valueFontColor = config.getLegendItemValueFontColor();
		} else if(parent.parent.parent instanceof MultiScalarVisualizer) {
			MultiScalarVisualizerConfig config = ((MultiScalarVisualizer) parent.parent.parent).config;
			this.buttonPanelSize = config.getLegendItemButtonPanelSize();
			this.buttonSize = config.getLegendItemButtonSize();
			this.size = config.getLegendItemSize();
			this.nameLabelSize = config.getLegendItemNameLabelSize();
			this.valueLabelSize = config.getLegendItemValueLabelSize();
			this.valueFont = config.getLegendItemValueFont();
			this.valueFontColor = config.getLegendItemValueFontColor();
		} else if(parent.parent.parent instanceof LabelVisualizer) {
//			LabelVisualizerConfig config = ((LabelVisualizer) parent.parent.parent).config;
			MetricVisualizerConfig config = MainDisplay.DefaultConfig.getMetricVisualizerConfigs()[0];
			this.buttonPanelSize = config.getLegendItemButtonPanelSize();
			this.buttonSize = config.getLegendItemButtonSize();
			this.size = config.getLegendItemSize();
			this.nameLabelSize = config.getLegendItemNameLabelSize();
			this.valueLabelSize = config.getLegendItemValueLabelSize();
			this.valueFont = config.getLegendItemValueFont();
			this.valueFontColor = config.getLegendItemValueFontColor();
		}
		this.setPreferredSize(this.size);
		this.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		this.setLayout(new GridBagLayout());
		GridBagConstraints legendItemConstraints = new GridBagConstraints();
		this.yAxis = yAxisSelection.y1;

		// only show suffix b from the name, with name = a.b
		int i = name.length() - 1;
		while (i > 0 && name.charAt(i) != '.') {
			i--;
		}
		String nameSuffix = name.substring(i + 1);

		// name label
		this.nameLabel = new JLabel(nameSuffix);
		this.nameLabel.setFont(MainDisplay.config.getDefaultFont());
		this.nameLabel.setForeground(color);
		this.nameLabel.setPreferredSize(this.nameLabelSize);

		// value label
		this.valueLabel = new JLabel("V=" + 0.0);
		this.valueLabel.setFont(this.valueFont);
		this.valueLabel.setForeground(this.valueFontColor);
		this.valueLabel.setPreferredSize(this.valueLabelSize);
		this.valueLabel.setToolTipText("V=0.0");

		// remove button
		this.removeButton = new JButton("-");
		this.removeButton.setFont(MainDisplay.config.getDefaultFont());
		this.removeButton.setForeground(MainDisplay.config
				.getDefaultFontColor());
		this.removeButton.setPreferredSize(this.buttonSize);
		this.removeButton
				.setToolTipText("Removes this value from list and plot.");
		this.removeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				thisItem.parent.removeItem(thisItem);
			}
		});
		this.removeButton.setMargin(new Insets(0, 0, 0, 0));
		this.removeButton.setFont(new Font(this.removeButton.getFont()
				.getName(), this.removeButton.getFont().getStyle(), 17));

		// toggle y axis button
		this.toggleYAxisButton = new JButton("y1");
		this.toggleYAxisButton.setFont(MainDisplay.config.getDefaultFont());
		this.toggleYAxisButton.setForeground(MainDisplay.config
				.getDefaultFontColor());
		this.toggleYAxisButton.setPreferredSize(this.buttonSize);
		this.toggleYAxisButton.setMargin(new Insets(0, 0, 0, 0));
		this.toggleYAxisButton
				.setToolTipText("Currently plotted on left y-axis (y1). Click to change to right y-axis");
		this.toggleYAxisButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				if (thisItem.toggleYAxisButton.getText().equals("y2")) {
					thisItem.toggleYAxisButton.setText("y1");
					thisItem.toggleYAxisButton
							.setToolTipText("Currently plotted on left y-axis (y1). Click to change to right y-axis");
				} else {
					thisItem.toggleYAxisButton.setText("y2");
					thisItem.toggleYAxisButton
							.setToolTipText("Currently plotted on right y-axis (y2). Click to change to left y-axis");
				}
				thisItem.parent.toggleYAxis(thisItem);
			}
		});

		// show/hide button
		this.showHideButton = new JButton("S");
		this.showHideButton.setFont(MainDisplay.config.getDefaultFont());
		this.showHideButton.setForeground(MainDisplay.config
				.getDefaultFontColor());
		this.showHideButton.setPreferredSize(this.buttonSize);
		this.showHideButton.setMargin(new Insets(0, 0, 0, 0));
		this.showHideButton.setToolTipText("Hides this value in the chart");
		this.showHideButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				if (thisItem.showHideButton.getText().equals("H")) {
					thisItem.showHideButton.setText("S");
					thisItem.showHideButton.setForeground(MainDisplay.config
							.getDefaultFontColor());
					thisItem.showHideButton
							.setToolTipText("Hides this value in the chart");
				} else {
					thisItem.showHideButton.setText("H");
					thisItem.showHideButton.setForeground(Color.RED);
					thisItem.showHideButton
							.setToolTipText("Shows this value in the chart");
				}
				thisItem.parent.toggleVisiblity(thisItem);
			}
		});

		// bar/linespoint button
		this.displayModeButton = new JButton("L");
		this.displayModeButton.setFont(MainDisplay.config.getDefaultFont());
		this.displayModeButton.setForeground(MainDisplay.config
				.getDefaultFontColor());
		this.displayModeButton.setPreferredSize(this.buttonSize);
		this.displayModeButton.setMargin(new Insets(0, 0, 0, 0));
		this.displayModeButton
				.setToolTipText("Currently shown as linespoint. Click to change to bars.");
		this.displayModeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				if (thisItem.displayModeButton.getText().equals("B")) {
					thisItem.displayModeButton.setText("L");
					thisItem.displayModeButton
							.setToolTipText("Currently shown as linespoint. Click to change to bars.");
				} else {
					thisItem.displayModeButton.setText("B");
					thisItem.displayModeButton
							.setToolTipText("Currently shown as bars. Click to change to linespoint.");
				}
				thisItem.parent.toggleDisplayMode(thisItem);
			}
		});

		// button panel
		this.buttonPanel = new JPanel();
		this.buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 0, 0));
		this.buttonPanel
				.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		this.buttonPanel.setPreferredSize(this.buttonPanelSize);
		this.buttonPanel.add(this.removeButton);
		this.buttonPanel.add(this.showHideButton);
		this.buttonPanel.add(this.displayModeButton);
		this.buttonPanel.add(this.toggleYAxisButton);

		// add components
		legendItemConstraints.fill = GridBagConstraints.HORIZONTAL;
		legendItemConstraints.insets = new Insets(0, 0, 0, 0);

		// add namelabel
		legendItemConstraints.gridx = 0;
		legendItemConstraints.gridy = 0;
		legendItemConstraints.gridwidth = 2;
		this.add(this.nameLabel, legendItemConstraints);

		// add valuelabel
		this.valueLabel.setText("");
		legendItemConstraints.gridwidth = 1;
		legendItemConstraints.gridy = 1;
		legendItemConstraints.weightx = 0.5;
		this.add(this.valueLabel, legendItemConstraints);

		// add buttonpanel
		legendItemConstraints.gridx = 1;
		this.add(this.buttonPanel, legendItemConstraints);
	}

	/** sets the name **/
	public void setNameLabel(String name) {
		this.nameLabel.setText(name);
	}

	/** sets the color **/
	public void setColor(Color color) {
		this.nameLabel.setForeground(color);
	}

	/** returns the color **/
	public Color getColor() {
		return this.color;
	}

	/** sets the ShowHide button **/
	public void setShowHideButton(boolean visible) {
		if (visible) {
			this.showHideButton.setText("S");
			this.showHideButton.setForeground(MainDisplay.config
					.getDefaultFontColor());
			this.showHideButton.setToolTipText("Hides this value in the chart");
		} else {
			this.showHideButton.setText("H");
			this.showHideButton.setForeground(Color.RED);
			this.showHideButton.setToolTipText("Shows this value in the chart");
		}
	}

	/** sets the DisplayModeButton **/
	public void setDisplayModeButton(boolean displayAsLinespoint) {
		if (displayAsLinespoint) {
			this.displayModeButton.setText("L");
			this.displayModeButton
					.setToolTipText("Currently shown as linespoint. Click to change to bars.");
		} else {
			this.displayModeButton.setText("B");
			this.displayModeButton
					.setToolTipText("Currently shown as bars. Click to change to linespoint.");
		}
	}

	/** sets the y axis button **/
	public void setYAxisButton(boolean showOnY1) {
		if (showOnY1) {
			this.toggleYAxisButton.setText("y1");
			this.toggleYAxisButton
					.setToolTipText("Currently plotted on left y-axis (y1). Click to change to right y-axis");
		} else {
			this.toggleYAxisButton.setText("y2");
			this.toggleYAxisButton
					.setToolTipText("Currently plotted on right y-axis (y2). Click to change to left y-axis");
		}
	}
}
