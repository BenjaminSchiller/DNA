package dna.visualization.components.visualizer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;

import dna.visualization.components.BoundsPopupMenuListener;
import dna.visualization.components.visualizer.MultiScalarVisualizer.SortMode;

/**
 * A legenditem in the legendlist representing a nodevaluelist.
 * 
 * @author Rwilmes
 * 
 */
public class LegendItemNodeValueList extends LegendItem {

	private String[] sortOptions = { "-sort by index", "-sort ascending",
			"-sort descending" };
	private SortMode sortMode;
	
	private JButton toggleXAxisButton;

	// constructor
	public LegendItemNodeValueList(LegendList parent, String name, Color color) {
		super(parent, name, color);
		this.sortMode = SortMode.index;

		this.valueLabel.setPreferredSize(new Dimension(60, 20));
		this.buttonPanel.setPreferredSize(new Dimension(100, 20));
		
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
		
		
		
		this.buttonPanel.add(sortOptionsBox);
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
			if (m.equals("-sort by index")) {
				this.sortMode = SortMode.index;
			}
			if (m.equals("-sort ascending")) {
				this.sortMode = SortMode.ascending;
			}
			if (m.equals("-sort descending")) {
				this.sortMode = SortMode.descending;
			}
		}
	}

	/** returns the sortmode **/
	public SortMode getSortMode() {
		return this.sortMode;
	}
}
