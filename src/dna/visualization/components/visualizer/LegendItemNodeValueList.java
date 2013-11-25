package dna.visualization.components.visualizer;

import java.awt.Color;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import dna.util.Config;
import dna.visualization.GuiOptions;
import dna.visualization.components.visualizer.MultiScalarVisualizer.SortModeNVL;
import dna.visualization.components.visualizer.Visualizer.xAxisSelection;

/**
 * A legenditem in the legendlist representing a nodevaluelist.
 * 
 * @author Rwilmes
 * 
 */
@SuppressWarnings("serial")
public class LegendItemNodeValueList extends LegendItem {
	// components
	private SortModeNVL sortMode;
	private xAxisSelection xAxis;

	private JButton toggleXAxisButton;
	private JButton sortModeButton;

	// constructor
	public LegendItemNodeValueList(LegendList parent, String name, Color color) {
		// init
		super(parent, name, color);
		this.valueLabel
				.setPreferredSize(GuiOptions.legendItemNvlValueLabelSize);
		this.buttonPanel
				.setPreferredSize(GuiOptions.legendItemNvlButtonPanelSize);

		// load defaults
		this.sortMode = Config.getSortModeNVL("GUI_SORT_MODE_NVL");
		this.xAxis = Config.getXAxisSelection("GUI_NVL_X_AXIS");
		this.yAxis = Config.getYAxisSelection("GUI_NVL_Y_AXIS");

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
		this.toggleXAxisButton.setFont(GuiOptions.defaultFont);
		this.toggleXAxisButton.setForeground(GuiOptions.defaultFontColor);
		this.toggleXAxisButton
				.setPreferredSize(GuiOptions.legendItemButtonSize);
		this.toggleXAxisButton.setMargin(new Insets(0, 0, 0, 0));
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
		// add toggle x axis button
		this.buttonPanel.add(this.toggleXAxisButton);

		// sort options button
		this.sortModeButton = new JButton();
		switch (this.sortMode) {
		case ascending:
			this.sortModeButton.setText("A");
			this.sortModeButton
					.setToolTipText("NodeValueList is sorted in ascending order. Click to change to descending order.");
			break;
		case descending:
			this.sortModeButton.setText("D");
			sortModeButton
					.setToolTipText("NodeValueList is sorted in descending order. Click to change to sort by index.");
			break;
		case index:
			this.sortModeButton.setText("I");
			sortModeButton
					.setToolTipText("NodeValueList is sorted by index. Click to change to ascending order.");
			break;
		}
		this.sortModeButton.setFont(GuiOptions.defaultFont);
		this.sortModeButton.setForeground(GuiOptions.defaultFontColor);
		this.sortModeButton.setPreferredSize(GuiOptions.legendItemButtonSize);
		this.sortModeButton.setMargin(new Insets(0, 0, 0, 0));
		this.sortModeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				if (sortMode.equals(SortModeNVL.ascending)) {
					sortModeButton.setText("D");
					sortMode = SortModeNVL.descending;
					sortModeButton
							.setToolTipText("NodeValueList is sorted in descending order. Click to change to sort by index.");
				} else if (sortMode.equals(SortModeNVL.descending)) {
					sortModeButton.setText("I");
					sortMode = SortModeNVL.index;
					sortModeButton
							.setToolTipText("NodeValueList is sorted by index. Click to change to ascending order.");
				} else if (sortMode.equals(SortModeNVL.index)) {
					sortModeButton.setText("A");
					sortMode = SortModeNVL.ascending;
					sortModeButton
							.setToolTipText("NodeValueList is sorted in ascending order. Click to change to descending order.");
				}
			}
		});
		// add sort options button
		this.buttonPanel.add(this.sortModeButton);
	}

	/** returns the sortmode **/
	public SortModeNVL getSortMode() {
		return this.sortMode;
	}
}
