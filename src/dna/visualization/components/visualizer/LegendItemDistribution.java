package dna.visualization.components.visualizer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import dna.visualization.components.visualizer.MultiScalarVisualizer.SortModeDist;

/**
 * A legenditem in the legendlist representing a distribution.
 * 
 * @author Rwilmes
 * 
 */
public class LegendItemDistribution extends LegendItem {
	// defaults
	private SortModeDist DEFAULT_SORT_MODE_DIST = SortModeDist.distribution;

	// components
	private JButton toggleXAxisButton;
	private JButton sortModeButton;
	private SortModeDist sortMode;

	// constructor
	public LegendItemDistribution(LegendList parent, String name, Color color) {
		super(parent, name, color);
		this.sortMode = DEFAULT_SORT_MODE_DIST;

		this.valueLabel.setText("D=0");
		this.valueLabel.setPreferredSize(new Dimension(60, 20));
		this.buttonPanel.setPreferredSize(new Dimension(100, 20));

		// toggle y axis button
		this.toggleXAxisButton = new JButton("x1");
		this.toggleXAxisButton.setFont(this.defaultFont);
		this.toggleXAxisButton.setForeground(Color.BLACK);
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
			break;
		case cdf:
			this.sortModeButton.setText("C");
			break;
		}
		this.sortModeButton.setFont(this.defaultFont);
		this.sortModeButton.setForeground(Color.BLACK);
		this.sortModeButton.setPreferredSize(this.buttonSize);
		this.sortModeButton
				.setToolTipText("Distribution is shown as distribution. Click to change to cdf plot.");
		this.sortModeButton.setMargin(new Insets(0, 0, 0, 0));
		this.sortModeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				if (sortMode.equals(SortModeDist.distribution)) {
					sortModeButton.setText("C");
					sortMode = SortModeDist.cdf;
					sortModeButton
							.setToolTipText("Distribution is shown as cdf plot. Click to change to show it as distribution.");
				} else if (sortMode.equals(SortModeDist.cdf)) {
					sortModeButton.setText("D");
					sortMode = SortModeDist.distribution;
					sortModeButton
							.setToolTipText("Distribution is shown as distribution. Click to change to cdf plot.");
				}
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
}
