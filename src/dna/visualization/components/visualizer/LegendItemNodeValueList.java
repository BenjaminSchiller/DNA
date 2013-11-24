package dna.visualization.components.visualizer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import dna.visualization.components.visualizer.MultiScalarVisualizer.SortMode;

/**
 * A legenditem in the legendlist representing a nodevaluelist.
 * 
 * @author Rwilmes
 * 
 */
public class LegendItemNodeValueList extends LegendItem {
	// default
	private SortMode DEFAULT_SORT_MODE = SortMode.ascending;

	// components
	private SortMode sortMode;
	private JButton toggleXAxisButton;
	private JButton sortModeButton;

	// constructor
	public LegendItemNodeValueList(LegendList parent, String name, Color color) {
		super(parent, name, color);
		// default sort-mode settings:
		this.sortMode = DEFAULT_SORT_MODE;

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
		// add toggle x axis button
		this.buttonPanel.add(this.toggleXAxisButton);

		// sort options button
		this.sortModeButton = new JButton();
		switch (this.sortMode) {
		case ascending:
			this.sortModeButton.setText("A");
			break;
		case descending:
			this.sortModeButton.setText("D");
			break;
		case index:
			this.sortModeButton.setText("I");
			break;
		}
		this.sortModeButton.setFont(this.defaultFont);
		this.sortModeButton.setForeground(Color.BLACK);
		this.sortModeButton.setPreferredSize(this.buttonSize);
		this.sortModeButton
				.setToolTipText("NodeValueList is sorted in ascending order. Click to change to descending order.");
		this.sortModeButton.setMargin(new Insets(0, 0, 0, 0));
		this.sortModeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				if (sortMode.equals(SortMode.ascending)) {
					sortModeButton.setText("D");
					sortMode = SortMode.descending;
					sortModeButton
							.setToolTipText("NodeValueList is sorted in descending order. Click to change to sort by index.");
				} else if (sortMode.equals(SortMode.descending)) {
					sortModeButton.setText("I");
					sortMode = SortMode.index;
					sortModeButton
							.setToolTipText("NodeValueList is sorted by index. Click to change to ascending order.");
				} else if (sortMode.equals(SortMode.index)) {
					sortModeButton.setText("A");
					sortMode = sortMode.ascending;
					sortModeButton
							.setToolTipText("NodeValueList is sorted in ascending order. Click to change to descending order.");
				}
			}
		});
		// add sort options button
		this.buttonPanel.add(this.sortModeButton);
	}

	/** returns the sortmode **/
	public SortMode getSortMode() {
		return this.sortMode;
	}
}
