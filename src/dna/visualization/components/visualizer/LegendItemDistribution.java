package dna.visualization.components.visualizer;

import java.awt.Color;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import dna.util.Config;
import dna.visualization.MainDisplay;
import dna.visualization.config.VisualizerListConfig.SortModeDist;
import dna.visualization.config.VisualizerListConfig.xAxisSelection;
import dna.visualization.config.components.MultiScalarVisualizerConfig;

/**
 * A legenditem in the legendlist representing a distribution.
 * 
 * @author Rwilmes
 * 
 */
@SuppressWarnings("serial")
public class LegendItemDistribution extends LegendItem {
	// components
	private SortModeDist sortMode;
	private xAxisSelection xAxis;

	private JButton toggleXAxisButton;
	private JButton sortModeButton;

	// constructor
	public LegendItemDistribution(LegendList parent, String name, Color color) {
		// init
		super(parent, name, color);
		this.valueLabel.setText("D=0");

		// load defaults
		if(parent.parent.parent instanceof MultiScalarVisualizer) {
			MultiScalarVisualizerConfig config = ((MultiScalarVisualizer) parent.parent.parent).config;
			this.sortMode = config.getListConfig().getAllDistributionsConfig().getSortMode();
			this.xAxis = config.getListConfig().getAllDistributionsConfig().getXAxis();
			this.yAxis = config.getListConfig().getAllDistributionsConfig().getYAxis();
		}

		// adjust toggle y axis button
		switch (this.yAxis) {
		case y1:
			this.toggleYAxisButton.setText("y1");
			this.toggleYAxisButton
					.setToolTipText("Currently plotted on left y-axis (y1). Click to change to right y-axis");
			break;
		case y2:
			this.toggleYAxisButton.setText("y2");
			thisItem.toggleYAxisButton
					.setToolTipText("Currently plotted on right y-axis (y2). Click to change to left y-axis");
			break;
		}

		// toggle y axis button
		this.toggleXAxisButton = new JButton("x1");
		// toggle x axis button
		this.toggleXAxisButton = new JButton();
		switch (this.xAxis) {
		case x1:
			this.toggleXAxisButton.setText("x1");
			this.toggleXAxisButton
					.setToolTipText("Currently plotted on x-axis 1. Click to change to x-axis 2");
			break;
		case x2:
			this.toggleXAxisButton.setText("x2");
			toggleXAxisButton
					.setToolTipText("Currently plotted on x-axis 2. Click to change to x-axis 1");
			break;
		}
		this.toggleXAxisButton.setFont(MainDisplay.config.getDefaultFont());
		this.toggleXAxisButton.setForeground(MainDisplay.config
				.getDefaultFontColor());
		this.toggleXAxisButton.setPreferredSize(this.buttonSize);
		this.toggleXAxisButton.setMargin(new Insets(0, 0, 0, 0));
		this.toggleXAxisButton
				.setToolTipText("Currently plotted on x-axis 1. Click to change to x-axis 2");
		this.toggleXAxisButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				if (toggleXAxisButton.getText().equals("x1")) {
					toggleXAxisButton.setText("x2");
					toggleXAxisButton
							.setToolTipText("Currently plotted on x-axis 2. Click to change to x-axis 1");
				} else {
					toggleXAxisButton.setText("x1");
					toggleXAxisButton
							.setToolTipText("Currently plotted on x-axis 1. Click to change to x-axis 2");
				}
				thisItem.parent.toggleXAxis(thisItem);
			}
		});
		this.buttonPanel.add(this.toggleXAxisButton);

		// sort options button
		this.sortModeButton = new JButton();
		switch (this.sortMode) {
		case distribution:
			this.sortModeButton.setText("D");
			this.sortModeButton
					.setToolTipText("Distribution is shown as distribution. Click to change to cdf plot.");
			break;
		case cdf:
			this.sortModeButton.setText("C");
			sortModeButton
					.setToolTipText("Distribution is shown as cdf plot. Click to change to distribution.");
			break;
		}
		this.sortModeButton.setFont(MainDisplay.config.getDefaultFont());
		this.sortModeButton.setForeground(MainDisplay.config
				.getDefaultFontColor());
		this.sortModeButton.setPreferredSize(this.buttonSize);
		this.sortModeButton.setMargin(new Insets(0, 0, 0, 0));
		this.sortModeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				if (sortMode.equals(SortModeDist.distribution)) {
					sortModeButton.setText("C");
					sortMode = SortModeDist.cdf;
					sortModeButton
							.setToolTipText("Distribution is shown as cdf plot. Click to change to distribution.");
				} else if (sortMode.equals(SortModeDist.cdf)) {
					sortModeButton.setText("D");
					sortMode = SortModeDist.distribution;
					sortModeButton
							.setToolTipText("Distribution is shown as distribution. Click to change to cdf plot.");
				}
				thisItem.parent.sortItem(thisItem, sortMode);
			}
		});

		// add sort options button
		this.buttonPanel.add(this.sortModeButton);
	}

	/** sets the denominator of an item **/
	public void setDenominator(long denominator) {
		this.valueLabel.setText("D=" + denominator);
		this.valueLabel.setToolTipText("D=" + denominator);
	}

	/** returns the sortmode **/
	public SortModeDist getSortMode() {
		return this.sortMode;
	}

	/** sets the x axis button **/
	public void setXAxisButton(boolean showOnX1) {
		if (showOnX1) {
			this.toggleXAxisButton.setText("x1");
			this.toggleXAxisButton
					.setToolTipText("Currently plotted on x-axis 1. Click to change to x-axis 2");
		} else {
			this.toggleXAxisButton.setText("x2");
			toggleXAxisButton
					.setToolTipText("Currently plotted on x-axis 2. Click to change to x-axis 1");
		}
	}
}
