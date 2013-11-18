package dna.visualization.components.visualizer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;

import dna.visualization.components.BoundsPopupMenuListener;
import dna.visualization.components.visualizer.MultiScalarVisualizer.SortModeDist;

/**
 * A legenditem in the legendlist representing a distribution.
 * 
 * @author Rwilmes
 * 
 */
public class LegendItemDistribution extends LegendItem {

	private JButton toggleXAxisButton;

	private String[] sortOptions = { "-plot distribution", "-plot cdf" };
	private SortModeDist sortMode;

	private LegendItemDistribution thisItem;

	// constructor
	public LegendItemDistribution(LegendList parent, String name, Color color) {
		super(parent, name, color);
		this.sortMode = SortModeDist.distribution;
		thisItem = this;

		this.valueLabel.setText("D=0");
		this.valueLabel.setPreferredSize(new Dimension(60, 20));
		this.buttonPanel.setPreferredSize(new Dimension(100, 20));

		JButton xAxisButton = new JButton();
		xAxisButton.setText("x1");
		xAxisButton.setMargin(new Insets(0, 0, 0, 0));
		xAxisButton.setFont(defaultFont);
		xAxisButton.setPreferredSize(new Dimension(20, 20));

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
		final JComboBox<String> sortOptionsBox = new JComboBox<String>(
				this.sortOptions);
		sortOptionsBox.setFont(this.defaultFont);
		sortOptionsBox.setPreferredSize((new Dimension(20, 20)));
		sortOptionsBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				selectSortOptions(sortOptionsBox.getSelectedIndex());
			}
		});
		BoundsPopupMenuListener listener = new BoundsPopupMenuListener(true,
				false);
		sortOptionsBox.addPopupMenuListener(listener);

		this.buttonPanel.add(sortOptionsBox);

	}

	/** sets the denominator of an item **/
	public void setDenominator(long denominator) {
		this.valueLabel.setText("D=" + denominator);
		this.valueLabel.setToolTipText("D=" + denominator);
	}

	/** called by the sortoption dropdown menu to update the sort options **/
	public void selectSortOptions(int selectionIndex) {
		String m = "";
		try {
			m = this.sortOptions[selectionIndex];
		} catch (IndexOutOfBoundsException e) {
			e.printStackTrace();
		}
		if (!m.equals("")) {
			if (m.equals("-plot distribution")) {
				this.sortMode = SortModeDist.distribution;
			}
			if (m.equals("-plot cdf")) {
				this.sortMode = SortModeDist.cdf;
			}
		}
	}

	/** returns the sortmode **/
	public SortModeDist getSortMode() {
		return this.sortMode;
	}
}
