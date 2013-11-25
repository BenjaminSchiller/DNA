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
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

import dna.visualization.GuiOptions;
import dna.visualization.components.visualizer.Visualizer.yAxisSelection;

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
	protected JPanel textPanel;
	protected JLabel nameLabel;
	protected JLabel valueLabel;

	// buttons
	protected JButton toggleYAxisButton;
	private JButton removeButton;
	private JButton showHideButton;

	// optionspanel containing several buttons and options
	protected JPanel buttonPanel;

	// constructor
	public LegendItem(LegendList parent, String name, Color color) {
		// init default parameters and variables
		this.parent = parent;
		this.thisItem = this;
		this.setName(name);
		this.color = color;
		this.setPreferredSize(GuiOptions.legendItemItemSize);
		this.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		this.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		this.yAxis = yAxisSelection.y1;

		// init set-, text- and button panels
		this.textPanel = new JPanel();
		this.textPanel.setPreferredSize(new Dimension(120, 30));
		this.textPanel
				.setLayout(new BoxLayout(this.textPanel, BoxLayout.Y_AXIS));

		// config set-, text- and button panels
		this.textPanel
				.setLayout(new BoxLayout(this.textPanel, BoxLayout.Y_AXIS));

		// only show suffix b from the name, with name = a.b
		int i = name.length() - 1;
		while (i > 0 && name.charAt(i) != '.') {
			i--;
		}
		String nameSuffix = name.substring(i + 1);

		// name label
		this.nameLabel = new JLabel(nameSuffix);
		this.nameLabel.setFont(GuiOptions.defaultFont);
		this.nameLabel.setForeground(color);
		this.textPanel.add(this.nameLabel);
		// value label
		this.valueLabel = new JLabel("V=" + 0.0);
		this.valueLabel.setFont(GuiOptions.legendItemValueFont);
		this.valueLabel.setForeground(GuiOptions.legendItemValueFontColor);
		this.valueLabel.setPreferredSize(new Dimension(80, 20));
		this.valueLabel.setToolTipText("V=0.0");
		this.textPanel.add(this.valueLabel);

		// remove button
		this.removeButton = new JButton("-");
		this.removeButton.setFont(GuiOptions.defaultFont);
		this.removeButton.setForeground(GuiOptions.defaultFontColor);
		this.removeButton.setPreferredSize(GuiOptions.legendItemButtonSize);
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
		this.toggleYAxisButton.setFont(GuiOptions.defaultFont);
		this.toggleYAxisButton.setForeground(GuiOptions.defaultFontColor);
		this.toggleYAxisButton.setPreferredSize(GuiOptions.legendItemButtonSize);
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
		this.showHideButton = new JButton("H");
		this.showHideButton.setFont(GuiOptions.defaultFont);
		this.showHideButton.setForeground(GuiOptions.defaultFontColor);
		this.showHideButton.setPreferredSize(GuiOptions.legendItemButtonSize);
		this.showHideButton.setMargin(new Insets(0, 0, 0, 0));
		this.showHideButton.setToolTipText("Hides this value in the chart");
		this.showHideButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				if (thisItem.showHideButton.getText().equals("H")) {
					thisItem.showHideButton.setText("S");
					thisItem.showHideButton.setForeground(Color.RED);
					thisItem.showHideButton
							.setToolTipText("Shows this value in the chart");
				} else {
					thisItem.showHideButton.setText("H");
					thisItem.showHideButton.setForeground(GuiOptions.defaultFontColor);
					thisItem.showHideButton
							.setToolTipText("Hides this value in the chart");
				}
				thisItem.parent.toggleVisiblity(thisItem);
			}
		});

		// button panel
		this.buttonPanel = new JPanel();
		this.buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 0, 0));
		this.buttonPanel
				.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		this.buttonPanel.setPreferredSize(new Dimension(80, 20));
		this.buttonPanel.add(this.removeButton);
		this.buttonPanel.add(this.showHideButton);
		this.buttonPanel.add(this.toggleYAxisButton);

		// add components
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(0, 0, 0, 0);

		// add namelabel
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 2;
		this.add(this.nameLabel, c);

		// add valuelabel
		this.valueLabel.setText("");
		c.gridwidth = 1;
		c.gridy = 1;
		c.weightx = 0.5;
		this.add(this.valueLabel, c);

		// add buttonpanel
		c.gridx = 1;
		this.add(this.buttonPanel, c);
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
}
