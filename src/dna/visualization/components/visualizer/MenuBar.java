package dna.visualization.components.visualizer;

import info.monitorenter.gui.chart.rangepolicies.RangePolicyFixedViewport;
import info.monitorenter.gui.chart.rangepolicies.RangePolicyUnbounded;
import info.monitorenter.util.Range;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import dna.visualization.MainDisplay;
import dna.visualization.components.BoundsPopupMenuListener;
import dna.visualization.components.visualizer.MultiScalarVisualizer.SortMode;

/**
 * The menubar is a bar containing several options for a visualizer, for example
 * showing a grid for the x-axis.
 * 
 * @author Rwilmes
 * 
 */
public class MenuBar extends JPanel implements ChangeListener {
	/** defaults **/
	private Font defaultFont = MainDisplay.defaultFont;
	private Font defaultFontBorders = MainDisplay.defaultFontBorders;
	private Font coordsFont = new Font("Dialog", Font.PLAIN, 11);

	private TitledBorder menuBarItemBorder = BorderFactory
			.createTitledBorder("");

	/** general options **/
	private Visualizer parent;

	// sizes
	private Dimension coordsPanelSize = new Dimension(145, 45);
	private Dimension intervalPanelSize = new Dimension(240, 45);
	private Dimension xOptionsPanelSize = new Dimension(65, 45);
	private Dimension yLeftOptionsPanelSize = new Dimension(65, 45);
	private Dimension yRightOptionsPanelSize = new Dimension(65, 45);
	private Dimension sortOptionsPanelSize = new Dimension(130, 45);

	// creates the default menu with all panels
	public MenuBar(Visualizer parent, Dimension d) {
		this(parent, d, true, true, true, true, true, false);
	}

	// constructor
	public MenuBar(Visualizer parent, Dimension size, boolean addCoordsPanel,
			boolean addIntervalPanel, boolean addXOptionsPanel,
			boolean addYLeftOptionsPanel, boolean addYRightOptionsPanel,
			boolean addSortOptionsPanel) {
		this.parent = parent;
		this.setLayout(new GridBagLayout());
		this.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));

		this.setPreferredSize(size);
		this.setBorder(BorderFactory.createEtchedBorder((EtchedBorder.LOWERED)));
		this.menuBarItemBorder.setTitleFont(this.defaultFontBorders);
		int spaceUsed = 0;

		// add coords panel
		if (addCoordsPanel) {
			this.addCoordsPanel(this.coordsPanelSize);
			spaceUsed += this.coordsPanelSize.width;
		}

		// add interval panel
		if (addIntervalPanel) {
			this.addIntervalPanel(this.intervalPanelSize);
			spaceUsed += this.intervalPanelSize.width;
		}

		// add x axis options panel
		if (addXOptionsPanel) {
			this.addXOptionsPanel(this.xOptionsPanelSize);
			spaceUsed += this.xOptionsPanelSize.width;
		}

		// add left y-axis options panel
		if (addYLeftOptionsPanel) {
			this.addYLeftOptionsPanel(this.yLeftOptionsPanelSize);
			spaceUsed += this.yLeftOptionsPanelSize.width;
		}

		// add right y-axis option panel
		if (addYRightOptionsPanel) {
			this.addYRightOptionsPanel(this.yRightOptionsPanelSize);
			spaceUsed += this.yRightOptionsPanelSize.width;
		}

		// add sort-options panel for multi-scalar visualizer
		if (addSortOptionsPanel) {
			this.addSortOptionsPanel(this.sortOptionsPanelSize);
			spaceUsed += this.sortOptionsPanelSize.width;
		}

		this.addDummyPanel(new Dimension((size.width - spaceUsed) - 5,
				size.height - 5));
	}

	// menu bar elements
	private JPanel coordsPanel;
	private JLabel xCoordsValue;
	private JLabel yCoordsValue;
	private JPanel xOptionsPanel;
	private JPanel yLeftOptionsPanel;
	private JPanel yRightOptionsPanel;

	private JPanel intervalPanel;
	private JTextField lowerBound;
	private JTextField upperBound;
	private String[] intervalOptions = { "- show all", "- fixed interval",
			"-fixed length: 10", "-fixed length: 20", "-fixed length: 30",
			"-fixed length: 40", "-fixed length: 50", "-fixed length: 100",
			"-fixed length: 150", "-fixed length: 200", "-fixed length: 300",
			"-fixed length: 500" };
	private JScrollBar intervalScrollBar;
	private JSlider intervalSizeSlider;
	private JLabel intervalSizeLabel;

	private JPanel sortOptionsPanel;
	private String[] sortOptions = { "-sort by index", "-sort ascending",
			"-sort descending" };

	/**
	 * Adds a left y-axis options panel to the menu bar.
	 * 
	 * @param size
	 */
	private void addYLeftOptionsPanel(Dimension size) {
		this.yLeftOptionsPanel = new JPanel();
		this.yLeftOptionsPanel.setLayout(new GridBagLayout());
		this.yLeftOptionsPanel.setPreferredSize(size);
		this.yLeftOptionsPanel.setBorder(menuBarItemBorder);

		GridBagConstraints yLeftOptionsPanelConstraints = new GridBagConstraints();

		// toggle left y axis grid button
		final JButton toggleGridYLeftButton = new JButton("+Grid yL");
		toggleGridYLeftButton.setFont(this.defaultFont);
		toggleGridYLeftButton.setPreferredSize(new Dimension(size.width - 5,
				(int) Math.floor((size.getHeight() - 5) / 2)));
		toggleGridYLeftButton.setMargin(new Insets(0, 0, 0, 0));
		toggleGridYLeftButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				if (toggleGridYLeftButton.getText().equals("+Grid yL"))
					toggleGridYLeftButton.setText("-Grid yL");
				else
					toggleGridYLeftButton.setText("+Grid yL");
				parent.toggleYLeftGrid();
			}
		});
		yLeftOptionsPanelConstraints.gridx = 0;
		yLeftOptionsPanelConstraints.gridy = 0;
		this.yLeftOptionsPanel.add(toggleGridYLeftButton,
				yLeftOptionsPanelConstraints);

		// toggle left y axis log button
		final JButton toggleLogYLeftButton = new JButton("+log yL");
		toggleLogYLeftButton.setFont(this.defaultFont);
		toggleLogYLeftButton.setForeground(Color.GRAY);
		toggleLogYLeftButton.setPreferredSize(new Dimension(new Dimension(
				size.width - 5, (int) Math.floor((size.getHeight() - 5) / 2))));
		toggleLogYLeftButton.setMargin(new Insets(0, 0, 0, 0));
		yLeftOptionsPanelConstraints.gridx = 0;
		yLeftOptionsPanelConstraints.gridy = 1;
		this.yLeftOptionsPanel.add(toggleLogYLeftButton,
				yLeftOptionsPanelConstraints);

		// add to menu bar
		this.add(this.yLeftOptionsPanel);
	}

	/**
	 * Adds a right y-axis options panel to the menu bar.
	 * 
	 * @param size
	 */
	private void addYRightOptionsPanel(Dimension size) {
		this.yRightOptionsPanel = new JPanel();
		this.yRightOptionsPanel.setLayout(new GridBagLayout());
		this.yRightOptionsPanel.setPreferredSize(size);
		this.yRightOptionsPanel.setBorder(menuBarItemBorder);

		GridBagConstraints yRightOptionsPanelConstraints = new GridBagConstraints();

		// toggle right y axis grid button
		final JButton toggleGridYRightButton = new JButton("+Grid yR");
		toggleGridYRightButton.setFont(this.defaultFont);
		toggleGridYRightButton.setPreferredSize(new Dimension(new Dimension(
				size.width - 5, (int) Math.floor((size.getHeight() - 5) / 2))));
		toggleGridYRightButton.setMargin(new Insets(0, 0, 0, 0));
		toggleGridYRightButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				if (toggleGridYRightButton.getText().equals("+Grid yR"))
					toggleGridYRightButton.setText("-Grid yR");
				else
					toggleGridYRightButton.setText("+Grid yR");
				parent.toggleYRightGrid();
			}
		});
		yRightOptionsPanelConstraints.gridx = 0;
		yRightOptionsPanelConstraints.gridy = 0;
		this.yRightOptionsPanel.add(toggleGridYRightButton,
				yRightOptionsPanelConstraints);

		// toggle right y axis log button
		final JButton toggleLogYRightButton = new JButton("+log yR");
		toggleLogYRightButton.setFont(this.defaultFont);
		toggleLogYRightButton.setForeground(Color.GRAY);
		toggleLogYRightButton.setPreferredSize(new Dimension(new Dimension(
				size.width - 5, (int) Math.floor((size.getHeight() - 5) / 2))));
		toggleLogYRightButton.setMargin(new Insets(0, 0, 0, 0));
		yRightOptionsPanelConstraints.gridx = 0;
		yRightOptionsPanelConstraints.gridy = 1;
		this.yRightOptionsPanel.add(toggleLogYRightButton,
				yRightOptionsPanelConstraints);

		// add to menu bar
		this.add(this.yRightOptionsPanel);
	}

	/**
	 * Adds a x-axis options panel to the menu bar.
	 * 
	 * @param size
	 */
	private void addXOptionsPanel(Dimension size) {
		this.xOptionsPanel = new JPanel();
		this.xOptionsPanel.setLayout(new GridBagLayout());
		this.xOptionsPanel.setPreferredSize(size);
		this.xOptionsPanel.setBorder(this.menuBarItemBorder);

		GridBagConstraints xAxisOptionsPanelConstraints = new GridBagConstraints();

		// toggle x axis grid button
		final JButton toggleGridXButton = new JButton("+Grid x");
		toggleGridXButton.setFont(this.defaultFont);
		toggleGridXButton.setPreferredSize(new Dimension(new Dimension(
				size.width - 5, (int) Math.floor((size.getHeight() - 5) / 2))));
		toggleGridXButton.setMargin(new Insets(0, 0, 0, 0));
		toggleGridXButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				if (toggleGridXButton.getText().equals("+Grid x"))
					toggleGridXButton.setText("-Grid x");
				else
					toggleGridXButton.setText("+Grid x");
				parent.toggleXGrid();
			}
		});
		xAxisOptionsPanelConstraints.gridx = 0;
		xAxisOptionsPanelConstraints.gridy = 0;
		this.xOptionsPanel.add(toggleGridXButton, xAxisOptionsPanelConstraints);

		// add dummy panel
		JPanel dummyP = new JPanel();
		dummyP.setPreferredSize(new Dimension(new Dimension(size.width - 5,
				(int) Math.floor((size.getHeight() - 5) / 2))));
		xAxisOptionsPanelConstraints.gridx = 0;
		xAxisOptionsPanelConstraints.gridy = 1;
		this.xOptionsPanel.add(dummyP, xAxisOptionsPanelConstraints);

		// add to menu bar
		this.add(this.xOptionsPanel);
		;
	}

	/**
	 * Adds a coords panel to the menu bar.
	 * 
	 * @param size
	 */
	// private Dimension coordsPanelSize = new Dimension(145, 45)
	private void addCoordsPanel(Dimension size) {
		this.coordsPanel = new JPanel();
		this.coordsPanel.setLayout(new GridBagLayout());
		this.coordsPanel.setPreferredSize(size);
		this.coordsPanel.setBorder(menuBarItemBorder);

		GridBagConstraints coordsPanelConstraints = new GridBagConstraints();
		coordsPanelConstraints.gridx = 0;
		coordsPanelConstraints.gridy = 0;

		// x coords label
		JLabel xCoordsLabel = new JLabel("x:");
		xCoordsLabel.setFont(coordsFont);
		xCoordsLabel.setPreferredSize(new Dimension(10, 20));
		this.coordsPanel.add(xCoordsLabel, coordsPanelConstraints);
		// x coords value
		this.xCoordsValue = new JLabel("0");
		this.xCoordsValue.setFont(coordsFont);
		this.xCoordsValue.setPreferredSize(new Dimension(120, 20));
		coordsPanelConstraints.gridx = 1;
		coordsPanelConstraints.gridy = 0;
		this.coordsPanel.add(this.xCoordsValue, coordsPanelConstraints);

		// y coords label
		JLabel yCoordsLabel = new JLabel("y:");
		yCoordsLabel.setFont(coordsFont);
		yCoordsLabel.setPreferredSize(new Dimension(10, 20));
		coordsPanelConstraints.gridx = 0;
		coordsPanelConstraints.gridy = 1;
		this.coordsPanel.add(yCoordsLabel, coordsPanelConstraints);

		// y coords value
		this.yCoordsValue = new JLabel("0");
		this.yCoordsValue.setFont(coordsFont);
		this.yCoordsValue.setPreferredSize(new Dimension(120, 20));
		coordsPanelConstraints.gridx = 1;
		coordsPanelConstraints.gridy = 1;
		this.coordsPanel.add(this.yCoordsValue, coordsPanelConstraints);

		// add to menu bar
		this.add(this.coordsPanel);
	}

	/**
	 * Adds a dummy panel to the menu bar
	 * 
	 * @param size
	 */
	private void addDummyPanel(Dimension size) {
		JPanel dummyP = new JPanel();
		dummyP.setPreferredSize(size);
		this.add(dummyP);
	}

	/**
	 * Adds an interval panel to the menu bar.
	 * 
	 * @param size
	 */
	private void addIntervalPanel(Dimension size) {
		this.intervalPanel = new JPanel();
		this.intervalPanel.setLayout(new GridBagLayout());
		this.intervalPanel.setPreferredSize(size);
		this.intervalPanel.setBorder(menuBarItemBorder);

		GridBagConstraints intervalPanelConstraints = new GridBagConstraints();
		intervalPanelConstraints.insets = new Insets(0, 0, 0, 0);

		// intervalBox dropdown menu
		final JComboBox<String> intervalBox = new JComboBox<String>(
				this.intervalOptions);
		intervalBox.setFont(this.defaultFont);

		intervalBox.setPreferredSize(new Dimension(100, (int) Math.floor((size
				.getHeight() - 5) / 2)));
		intervalBox.setPreferredSize(new Dimension(100, 20));
		intervalBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				selectInterval(intervalBox.getSelectedIndex());
			}
		});
		BoundsPopupMenuListener listener = new BoundsPopupMenuListener(true,
				false);
		intervalBox.addPopupMenuListener(listener);
		intervalPanelConstraints.gridx = 0;
		intervalPanelConstraints.gridy = 0;
		intervalPanelConstraints.gridwidth = 1;
		this.intervalPanel.add(intervalBox, intervalPanelConstraints);

		// size label
		this.intervalSizeLabel = new JLabel(" Size:");
		this.intervalSizeLabel.setPreferredSize(new Dimension(30, 20));
		this.intervalSizeLabel.setFont(defaultFont);
		this.intervalSizeLabel.setForeground(Color.GRAY);

		intervalPanelConstraints.gridx = 1;
		this.intervalPanel
				.add(this.intervalSizeLabel, intervalPanelConstraints);

		// size slider
		this.intervalSizeSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 30);
		this.intervalSizeSlider.setPreferredSize(new Dimension(100, 20));
		this.intervalSizeSlider.setFont(defaultFont);
		this.intervalSizeSlider.addChangeListener(this);
		this.intervalSizeSlider.setEnabled(false);

		intervalPanelConstraints.gridheight = 1;
		intervalPanelConstraints.gridx = 2;
		this.intervalPanel.add(this.intervalSizeSlider,
				intervalPanelConstraints);

		// interval panel
		intervalPanelConstraints.gridwidth = 1;
		intervalPanelConstraints.insets = new Insets(0, 0, 0, 0);

		JLabel openInterval = new JLabel("[");
		openInterval.setFont(this.defaultFont);

		openInterval.setPreferredSize(new Dimension(8, ((int) Math
				.floor((size.height - 5) / 2) + 2)));
		intervalPanelConstraints.gridx = 0;
		intervalPanelConstraints.gridy = 1;
		// this.intervalPanel.add(openInterval, intervalPanelConstraints);

		this.lowerBound = new JTextField("0");
		this.lowerBound.setFont(this.defaultFont);
		this.lowerBound.setPreferredSize(new Dimension((int) Math
				.floor((size.width - 30) / 2), ((int) Math
				.floor((size.height - 5) / 2) + 2)));
		intervalPanelConstraints.gridx = 1;
		intervalPanelConstraints.gridy = 1;
		// this.intervalPanel.add(this.lowerBound, intervalPanelConstraints);

		JLabel points = new JLabel(" : ");
		points.setFont(this.defaultFont);
		points.setPreferredSize(new Dimension(10, ((int) Math
				.floor((size.height - 5) / 2) + 2)));
		intervalPanelConstraints.gridx = 2;
		intervalPanelConstraints.gridy = 1;
		// this.intervalPanel.add(points, intervalPanelConstraints);

		this.upperBound = new JTextField("10");
		this.upperBound.setFont(this.defaultFont);
		this.upperBound.setPreferredSize(new Dimension((int) Math
				.floor((size.width - 30) / 2), ((int) Math
				.floor((size.height - 5) / 2) + 2)));

		this.upperBound.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				if (upperBound.isEditable()) {
					if (Integer.parseInt(upperBound.getText()) > Integer
							.parseInt(lowerBound.getText())) {
						double lowerbound = Double.parseDouble(lowerBound
								.getText());
						double upperbound = Double.parseDouble(upperBound
								.getText());
						parent.setFixedViewport(true);
						parent.getXAxis().setRangePolicy(
								new RangePolicyFixedViewport());
						parent.getXAxis().setRange(
								new Range(lowerbound, upperbound));
						parent.setMinShownTimestamp((long) Math
								.floor(lowerbound));
						parent.setMaxShownTimestamp((long) Math
								.floor(upperbound));
						parent.updateXTicks();
						parent.updateYTicks();
						grabFocus();
					}
				}
			}
		});
		this.lowerBound.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				if (lowerBound.isEditable()) {
					if (Integer.parseInt(upperBound.getText()) > Integer
							.parseInt(lowerBound.getText())) {
						double lowerbound = Double.parseDouble(lowerBound
								.getText());
						double upperbound = Double.parseDouble(upperBound
								.getText());
						parent.setFixedViewport(true);
						parent.getXAxis().setRangePolicy(
								new RangePolicyFixedViewport());
						parent.getXAxis().setRange(
								new Range(lowerbound, upperbound));
						parent.setMinShownTimestamp((long) Math
								.floor(lowerbound));
						parent.setMaxShownTimestamp((long) Math
								.floor(upperbound));
						parent.updateXTicks();
						parent.updateYTicks();
						grabFocus();
					}
				}
			}
		});

		intervalPanelConstraints.gridx = 3;
		intervalPanelConstraints.gridy = 1;
		// this.intervalPanel.add(upperBound, intervalPanelConstraints);

		JLabel closeInterval = new JLabel("]");
		closeInterval.setFont(this.defaultFont);
		closeInterval.setPreferredSize(new Dimension(8, ((int) Math
				.floor((size.height - 5) / 2) + 2)));
		intervalPanelConstraints.gridx = 4;
		intervalPanelConstraints.gridy = 1;
		// this.intervalPanel.add(closeInterval, intervalPanelConstraints);

		intervalPanelConstraints.gridx = 0;
		intervalPanelConstraints.gridy = 1;
		intervalPanelConstraints.gridheight = 1;
		intervalPanelConstraints.gridwidth = 3;
		intervalPanelConstraints.weightx = 1;
		intervalPanelConstraints.weighty = 1;

		this.intervalScrollBar = new JScrollBar(JScrollBar.HORIZONTAL, 0, 30,
				0, 100);
		this.intervalScrollBar.setPreferredSize(new Dimension(235, 20));
		this.intervalScrollBar.setEnabled(false);

		this.intervalPanel.add(intervalScrollBar, intervalPanelConstraints);
		this.intervalScrollBar.addAdjustmentListener(new AdjustmentListener() {
			@Override
			public void adjustmentValueChanged(AdjustmentEvent e) {
				if (parent.chart.getAxisX().getRangePolicy() instanceof RangePolicyFixedViewport) {
					double lowP = 1.0 * intervalScrollBar.getValue() / 100;
					double highP = 1.0 * (intervalScrollBar.getValue() + intervalScrollBar
							.getModel().getExtent()) / 100;

					long min = parent.getMinTimestamp();
					long max = parent.getMaxTimestamp();

					int minTimestampNew = (int) Math.floor(lowP * (max - min));
					int maxTimestampNew = (int) Math.floor(highP * (max - min));

					parent.getXAxis().setRange(
							new Range(minTimestampNew, maxTimestampNew));
					parent.setMinShownTimestamp((long) minTimestampNew);
					parent.setMaxShownTimestamp((long) maxTimestampNew);

					parent.updateXTicks();
					parent.updateYTicks();
				}
			}
		});

		JPanel dummyPanel = new JPanel();
		// this.intervalPanel.add(dummyPanel, intervalPanelConstraints);

		this.lowerBound.setEditable(false);
		this.upperBound.setEditable(false);

		// add to menu bar
		this.add(this.intervalPanel);
	}

	/**
	 * Adds a panel for sorting options to the menu bar.
	 * 
	 * @param size
	 */
	private void addSortOptionsPanel(Dimension size) {
		this.sortOptionsPanel = new JPanel();
		this.sortOptionsPanel.setLayout(new GridBagLayout());
		this.sortOptionsPanel.setPreferredSize(size);
		this.sortOptionsPanel.setBorder(menuBarItemBorder);

		GridBagConstraints c = new GridBagConstraints();
		// sort options dropdown menu
		final JComboBox<String> sortOptionsBox = new JComboBox<String>(
				this.sortOptions);
		sortOptionsBox.setFont(this.defaultFont);
		sortOptionsBox.setPreferredSize((new Dimension(size.width - 5,
				(int) Math.floor((size.getHeight() - 5) / 2))));
		sortOptionsBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				selectSortOptions(sortOptionsBox.getSelectedIndex());
			}
		});
		BoundsPopupMenuListener listener = new BoundsPopupMenuListener(true,
				false);
		sortOptionsBox.addPopupMenuListener(listener);
		c.gridx = 0;
		c.gridy = 0;
		this.sortOptionsPanel.add(sortOptionsBox, c);

		// add dummy panel
		JPanel dummyP = new JPanel();
		dummyP.setPreferredSize(new Dimension(new Dimension(size.width - 5,
				(int) Math.floor((size.getHeight() - 5) / 2))));
		c.gridx = 0;
		c.gridy = 1;
		this.sortOptionsPanel.add(dummyP, c);

		JScrollBar sliderBar = new JScrollBar(JScrollBar.HORIZONTAL, 10, 50, 0,
				100);
		sliderBar.setPreferredSize(new Dimension(125, 20));

		// this.sortOptionsPanel.add(sliderBar, c);
		// add to menu bar
		this.add(this.sortOptionsPanel);
	}

	/** called by the interval combobox to update the interval **/
	public void selectInterval(int selectionIndex) {
		String m = "";
		try {
			m = this.intervalOptions[selectionIndex];
		} catch (IndexOutOfBoundsException e) {
			e.printStackTrace();
		}
		if (!m.equals("")) {
			if (m.equals("- show all")) {
				parent.setFixedViewport(false);
				this.intervalScrollBar.setEnabled(false);
				this.intervalSizeSlider.setEnabled(false);
				this.intervalSizeLabel.setForeground(Color.GRAY);
				this.lowerBound.setEditable(false);
				this.upperBound.setEditable(false);
				parent.getXAxis().setRangePolicy(new RangePolicyUnbounded());
				parent.setMaxShownTimestamp(parent.getMaxTimestamp());
				parent.updateXTicks();
				parent.updateYTicks();
			}
			if (m.equals("- fixed interval")) {
				parent.setFixedViewport(true);
				parent.getXAxis()
						.setRangePolicy(new RangePolicyFixedViewport());
				parent.getXAxis().setRange(
						new Range(parent.getMinTimestamp(), parent
								.getMaxTimestamp()));
				this.intervalScrollBar.setEnabled(true);
				this.intervalSizeSlider.setEnabled(true);
				this.intervalSizeLabel.setForeground(Color.BLACK);
				double lowP = 1.0 * intervalScrollBar.getValue() / 100;
				double highP = 1.0 * (intervalScrollBar.getValue() + intervalScrollBar
						.getModel().getExtent()) / 100;

				long min = parent.getMinTimestamp();
				long max = parent.getMaxTimestamp();

				int minTimestampNew = (int) Math.floor(lowP * (max - min));
				int maxTimestampNew = (int) Math.floor(highP * (max - min));

				parent.getXAxis().setRange(
						new Range(minTimestampNew, maxTimestampNew));
				parent.setMinShownTimestamp((long) minTimestampNew);
				parent.setMaxShownTimestamp((long) maxTimestampNew);

				parent.updateXTicks();
				parent.updateYTicks();
			}
			if (m.substring(0, 2).equals("-f")) {
				parent.setFixedViewport(false);
				this.lowerBound.setEditable(false);
				this.upperBound.setEditable(false);

				parent.setTraceLength(Integer.parseInt(m.substring(15)));
				parent.getXAxis()
						.setRangePolicy(new RangePolicyFixedViewport());
				parent.setMaxShownTimestamp(parent.getMaxTimestamp());
				if (parent.getMaxShownTimestamp() - parent.getTraceLength() > 0) {
					parent.setMinShownTimestamp(parent.getMaxShownTimestamp()
							- parent.getTraceLength());
				} else {
					parent.setMinShownTimestamp(0);
				}
				parent.getXAxis().setRange(
						new Range(parent.getMinShownTimestamp(), parent
								.getMaxShownTimestamp()));
				parent.updateXTicks();
				parent.updateYTicks();
			}
		}
	}

	/** called by the interval combobox to update the interval **/
	public void selectSortOptions(int selectionIndex) {
		String m = "";
		try {
			m = this.sortOptions[selectionIndex];
		} catch (IndexOutOfBoundsException e) {
			e.printStackTrace();
		}
		if (!m.equals("")) {
			if (m.equals("-sort by index")) {
				if (this.parent instanceof MultiScalarVisualizer)
					((MultiScalarVisualizer) this.parent)
							.setSortOrder(SortMode.index);
			}
			if (m.equals("-sort ascending")) {
				if (this.parent instanceof MultiScalarVisualizer)
					((MultiScalarVisualizer) this.parent)
							.setSortOrder(SortMode.ascending);
			}
			if (m.equals("-sort descending")) {
				if (this.parent instanceof MultiScalarVisualizer)
					((MultiScalarVisualizer) this.parent)
							.setSortOrder(SortMode.descending);
			}
		}
	}

	/**
	 * Updates the coordinate labels in the coordinate-panel.
	 * 
	 * @param x
	 *            x-value
	 * @param y
	 *            y-value
	 */
	public void updateCoordsPanel(int x, double y) {
		if (this.coordsPanel != null) {
			this.xCoordsValue.setText("" + x);
			this.yCoordsValue.setText("" + y);
		}
	}

	/**
	 * Sets if the menubar and its elements are editable or not
	 * 
	 * @param editable
	 */
	public void setEditable(boolean editable) {
		if (editable) {

		}
	}

	/** Gets called on mouse release after the size-slider has been moved **/
	public void stateChanged(ChangeEvent e) {
		JSlider source = (JSlider) e.getSource();
		// check if slider is set on the right end
		if (this.intervalScrollBar.getValue()
				+ this.intervalScrollBar.getModel().getExtent() == this.intervalScrollBar
					.getMaximum()) {
			int oldValue = this.intervalScrollBar.getValue();
			int oldExtent = this.intervalScrollBar.getModel().getExtent();

			int offset = source.getValue() - oldExtent;

			this.intervalScrollBar.setValue(oldValue - offset);
			this.intervalScrollBar.getModel().setExtent(source.getValue());

			// if slider is not set on right end anymore, adjust value
			if (this.intervalScrollBar.getValue()
					+ this.intervalScrollBar.getModel().getExtent() != this.intervalScrollBar
						.getMaximum()) {
				this.intervalScrollBar.setValue(this.intervalScrollBar
						.getMaximum()
						- this.intervalScrollBar.getModel().getExtent());
			}
			// if slider is in between, just resize it
		} else {
			this.intervalScrollBar.getModel().setExtent(source.getValue());
		}
	}

	/** returns the intervalslider **/
	public JScrollBar getIntervalSlider() {
		return this.intervalScrollBar;
	}

}
