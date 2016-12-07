package dna.visualization.components.visualizer;

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
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JSlider;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import dna.visualization.MainDisplay;
import dna.visualization.config.components.MenuBarConfig;
import info.monitorenter.gui.chart.rangepolicies.RangePolicyFixedViewport;
import info.monitorenter.gui.chart.rangepolicies.RangePolicyUnbounded;
import info.monitorenter.gui.chart.traces.Trace2DLtd;
import info.monitorenter.gui.chart.traces.Trace2DSimple;
import info.monitorenter.util.Range;

/**
 * The menubar is a bar containing several options for a visualizer, for example
 * showing a grid for the x-axis.
 * 
 * @author Rwilmes
 * 
 */
@SuppressWarnings("serial")
public class MenuBar extends JPanel implements ChangeListener {
	/** general options **/
	private Visualizer parent;
	public MenuBar thisMenuBar;
	private TitledBorder itemBorder = BorderFactory.createTitledBorder("");
	private Font coordsFont = new Font("Dialog", Font.PLAIN, 11);
	private Color coordsFontColor = Color.BLACK;

	// sizes
	public static final Dimension menuBarCoordsPanelSize = new Dimension(145, 45);
	public static final Dimension menuBarXOptionsPanelSize = new Dimension(65, 45);
	public static final Dimension menuBarYOptionsPanelSize = new Dimension(65, 45);
	public static final Dimension menuBarYRightOptionsPanelSize = new Dimension(65, 45);
	public static final Dimension menuBarIntervalPanelSize = new Dimension(225, 45);
	public static final Dimension menuBarMultiScalarIntervalPanelSize = new Dimension(210, 45);

	// creates the default menu with all panels
	public MenuBar(Visualizer parent, Dimension d) {
		this(parent, d, true, true, true, true);
	}

	// creates the menu with a given config
	public MenuBar(Visualizer parent, MenuBarConfig config) {
		this(parent, config.getSize(), config.isAddCoordsPanel(), config.isAddIntervalPanel(),
				config.isAddXOptionsPanel(), config.isAddYOptionsPanel());
	}

	// constructor
	public MenuBar(Visualizer parent, Dimension size, boolean addCoordsPanel, boolean addIntervalPanel,
			boolean addXOptionsPanel, boolean addYOptionsPanel) {
		this.parent = parent;
		this.thisMenuBar = this;

		this.setLayout(new GridBagLayout());
		this.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));

		this.setPreferredSize(size);
		this.setBorder(BorderFactory.createEtchedBorder((EtchedBorder.LOWERED)));

		// add coords panel
		if (addCoordsPanel) {
			this.addCoordsPanel(menuBarCoordsPanelSize);
		}

		// add interval panel
		if (addIntervalPanel) {
			Dimension tempSize = menuBarIntervalPanelSize;
			if (parent instanceof MultiScalarVisualizer)
				tempSize = menuBarMultiScalarIntervalPanelSize;
			this.addIntervalPanel(tempSize);
		}

		// add x axis options panel
		if (addXOptionsPanel) {
			this.addXOptionsPanel(menuBarXOptionsPanelSize);
		}

		// add y-axis options panel
		if (addYOptionsPanel) {
			this.addYOptionsPanel(menuBarYOptionsPanelSize);
		}
	}

	// menu bar elements
	private JPanel coordsPanel;
	private JLabel xCoordsLabel;
	private JLabel xCoordsValue;
	private JLabel yCoordsLabel;
	private JLabel yCoordsValue;
	private JPanel xOptionsPanel;
	private JPanel yLeftOptionsPanel;
	private JPanel yRightOptionsPanel;

	// interval panel
	private JPanel intervalPanel;
	// for x1
	private JScrollBar x1IntervalScrollBar;
	private JCheckBox x1ShowAllCheckBox;
	private JSlider x1SizeSlider;
	private JCheckBox x1ConnectedCheckBox;

	// for x2
	private JScrollBar x2IntervalScrollBar;
	private JCheckBox x2ShowAllCheckBox;
	private JSlider x2SizeSlider;

	/**
	 * Adds an interval panel to the menu bar.
	 * 
	 * @param size
	 */
	private void addIntervalPanel(Dimension size) {
		this.intervalPanel = new JPanel();
		this.intervalPanel.setLayout(new GridBagLayout());
		this.intervalPanel.setPreferredSize(size);
		this.intervalPanel.setBorder(this.itemBorder);

		GridBagConstraints c = new GridBagConstraints();

		/** upper panel **/
		final JPanel upperPanel = new JPanel();
		upperPanel.setPreferredSize(
				new Dimension((int) (size.getWidth() - 5), (int) Math.floor((size.getHeight() - 5) / 2)));
		upperPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));

		// x1 label
		JLabel x1Label = new JLabel("x1:");
		x1Label.setPreferredSize(new Dimension(18, 20));
		x1Label.setFont(this.coordsFont);
		x1Label.setForeground(MainDisplay.config.getDefaultFontColor());

		// checkbox
		this.x1ShowAllCheckBox = new JCheckBox("", true);
		this.x1ShowAllCheckBox.setToolTipText("Check to show all");
		this.x1ShowAllCheckBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				// check if checkbox is selected
				if (x1ShowAllCheckBox.isSelected()) {
					// if selected: disable bar and slider and set x1 to
					// unbounded policy
					parent.xAxis1.setRangePolicy(new RangePolicyUnbounded());
					parent.setFixedViewport(false);
					x1IntervalScrollBar.setEnabled(false);
					x1SizeSlider.setEnabled(false);
				} else {
					// if not selected: enable bar and slider and set x1 to
					// fixedviewport policy
					double minTemp = 0;
					double maxTemp = 1;
					parent.setFixedViewport(true);
					parent.xAxis1.setRangePolicy(new RangePolicyFixedViewport());
					if (parent instanceof MultiScalarVisualizer) {
						for (Object t : parent.xAxis1.getTraces()) {
							if (t instanceof Trace2DSimple) {
								double minX = ((Trace2DSimple) t).getMinX();
								double maxX = ((Trace2DSimple) t).getMaxX();
								if (minTemp > minX)
									minTemp = minX;
								if (maxTemp < maxX)
									maxTemp = maxX;
							}
						}
					} else if (parent instanceof MetricVisualizer) {
						for (Object t : parent.xAxis1.getTraces()) {
							if (t instanceof Trace2DLtd) {
								minTemp = ((Trace2DLtd) t).getMinX();
								maxTemp = ((Trace2DLtd) t).getMaxX();
								if (((Trace2DLtd) t).getMinX() < minTemp)
									minTemp = ((Trace2DLtd) t).getMinX();
								if (((Trace2DLtd) t).getMaxX() < maxTemp)
									maxTemp = ((Trace2DLtd) t).getMaxX();
							}
						}
					}
					double lowP = 1.0 * x1IntervalScrollBar.getValue() / 100;
					double highP = 1.0 * (x1IntervalScrollBar.getValue() + x1IntervalScrollBar.getModel().getExtent())
							/ 100;

					int minTimestampNew = (int) Math.floor(minTemp) + (int) Math.floor(lowP * (maxTemp - minTemp));
					int maxTimestampNew = (int) Math.floor(minTemp) + (int) Math.floor(highP * (maxTemp - minTemp));
					parent.xAxis1.setRange(new Range(minTimestampNew, maxTimestampNew));
					x1IntervalScrollBar.setEnabled(true);
					x1SizeSlider.setEnabled(true);
				}

				parent.broadcastX1IntervalEnabled(x1ShowAllCheckBox.isSelected());
			}
		});

		// size slider
		this.x1SizeSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 50);
		this.x1SizeSlider.setName("x1SizeSlider");
		this.x1SizeSlider.setPreferredSize(new Dimension(60, 20));
		this.x1SizeSlider.setFont(MainDisplay.config.getDefaultFont());
		this.x1SizeSlider.addChangeListener(this);
		this.x1SizeSlider.setEnabled(false);
		this.x1SizeSlider.setToolTipText("Set size of shown interval");

		// interval scroll bar
		this.x1IntervalScrollBar = new JScrollBar(JScrollBar.HORIZONTAL, 0, 50, 0, 100);
		this.x1IntervalScrollBar.setPreferredSize(new Dimension(100, 20));
		this.x1IntervalScrollBar.setEnabled(false);
		this.x1IntervalScrollBar.addAdjustmentListener(new AdjustmentListener() {
			@Override
			public void adjustmentValueChanged(AdjustmentEvent e) {
				if (parent.chart.getAxisX().getRangePolicy() instanceof RangePolicyFixedViewport) {
					double lowP = 1.0 * x1IntervalScrollBar.getValue() / 100;
					double highP = 1.0 * (x1IntervalScrollBar.getValue() + x1IntervalScrollBar.getModel().getExtent())
							/ 100;

					double minTemp = 0;
					double maxTemp = 1;

					// get range of plotted data
					for (Object t : parent.xAxis1.getTraces()) {
						if (t instanceof Trace2DSimple) {
							double minX = ((Trace2DSimple) t).getMinX();
							double maxX = ((Trace2DSimple) t).getMaxX();
							if (minTemp > minX)
								minTemp = minX;
							if (maxTemp < maxX)
								maxTemp = maxX;
						} else if (t instanceof Trace2DLtd) {
							minTemp = ((Trace2DLtd) t).getMinX();
							maxTemp = ((Trace2DLtd) t).getMaxX();
						}
					}

					if (parent instanceof LabelVisualizer) {
						minTemp = ((LabelVisualizer) parent).getMinTimestamp();
						maxTemp = ((LabelVisualizer) parent).getMaxTimestamp();
					}

					int minTimestampNew = (int) Math.floor(minTemp) + (int) Math.floor(lowP * (maxTemp - minTemp));
					int maxTimestampNew = (int) Math.floor(minTemp) + (int) Math.floor(highP * (maxTemp - minTemp));
					parent.setMinShownTimestamp((long) minTimestampNew);
					parent.setMaxShownTimestamp((long) maxTimestampNew);
					parent.xAxis1.setRange(new Range(minTimestampNew, maxTimestampNew));
					// update x ticks
					parent.updateX1Ticks();

					parent.broadcastX1IntervalSizeSliderChange(x1SizeSlider.getValue());
					parent.broadcastX1IntervalScrollBarChange(x1IntervalScrollBar.getValue());
				}
			}

		});

		// x1 connected checkbox
		this.x1ConnectedCheckBox = new JCheckBox("", this.parent.isX1Connected());
		this.x1ConnectedCheckBox.setToolTipText("Check to connect x1 axis with other components");
		this.x1ConnectedCheckBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				// check if checkbox is selected
				if (x1ConnectedCheckBox.isSelected()) {
					parent.setX1Connected(true);

				} else {
					parent.setX1Connected(false);
				}
			}
		});

		// add components
		upperPanel.add(x1Label);
		upperPanel.add(this.x1ShowAllCheckBox);
		upperPanel.add(this.x1SizeSlider);
		upperPanel.add(this.x1IntervalScrollBar);
		if (!(parent instanceof MultiScalarVisualizer))
			upperPanel.add(this.x1ConnectedCheckBox);

		/** add uppper panel **/
		c.gridx = 0;
		c.gridy = 0;
		this.intervalPanel.add(upperPanel, c);

		// if menubar of a multi scalar visualizer -> add same options for x2 in
		// a lower panel
		if (parent instanceof MultiScalarVisualizer) {
			// lower panel
			final JPanel lowerPanel = new JPanel();
			lowerPanel.setPreferredSize(
					new Dimension((int) (size.getWidth() - 5), (int) Math.floor((size.getHeight() - 5) / 2)));
			lowerPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));

			// x1 label
			JLabel x2Label = new JLabel("x2:");
			x2Label.setPreferredSize(new Dimension(18, 20));
			x2Label.setFont(this.coordsFont);
			x2Label.setForeground(MainDisplay.config.getDefaultFontColor());

			// checkbox
			this.x2ShowAllCheckBox = new JCheckBox("", true);
			this.x2ShowAllCheckBox.setToolTipText("Check to show all");
			this.x2ShowAllCheckBox.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent event) {
					// check if checkbox is selected
					if (x2ShowAllCheckBox.isSelected()) {
						// if selected: disable bar and slider and set x2 to
						// unbounded policy
						if (parent instanceof MultiScalarVisualizer)
							((MultiScalarVisualizer) parent).xAxis2.setRangePolicy(new RangePolicyUnbounded());
						x2IntervalScrollBar.setEnabled(false);
						x2SizeSlider.setEnabled(false);
					} else {
						// if not selected: enable bar and slider and set x2 to
						// fixedviewport policy
						if (parent instanceof MultiScalarVisualizer) {
							((MultiScalarVisualizer) parent).xAxis2.setRangePolicy(new RangePolicyFixedViewport());
							double lowP = 1.0 * x2IntervalScrollBar.getValue() / 100;
							double highP = 1.0
									* (x2IntervalScrollBar.getValue() + x2IntervalScrollBar.getModel().getExtent())
									/ 100;

							double minTemp = 0;
							double maxTemp = 10;

							// get range of plotted data
							for (Object t : ((MultiScalarVisualizer) parent).xAxis2.getTraces()) {
								if (t instanceof Trace2DSimple) {
									double minX = ((Trace2DSimple) t).getMinX();
									double maxX = ((Trace2DSimple) t).getMaxX();
									if (minTemp > minX)
										minTemp = minX;
									if (maxTemp < maxX)
										maxTemp = maxX;
								}
							}

							int minTimestampNew = (int) Math.floor(minTemp)
									+ (int) Math.floor(lowP * (maxTemp - minTemp));
							int maxTimestampNew = (int) Math.floor(minTemp)
									+ (int) Math.floor(highP * (maxTemp - minTemp));

							((MultiScalarVisualizer) parent).xAxis2
									.setRange(new Range(minTimestampNew, maxTimestampNew));
							// update x2 ticks
							((MultiScalarVisualizer) parent).updateX2Ticks();
						}
						x2IntervalScrollBar.setEnabled(true);
						x2SizeSlider.setEnabled(true);
					}
				}
			});

			// size slider
			this.x2SizeSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 50);
			this.x2SizeSlider.setName("x2SizeSlider");
			this.x2SizeSlider.setPreferredSize(new Dimension(60, 20));
			this.x2SizeSlider.setFont(MainDisplay.config.getDefaultFont());
			this.x2SizeSlider.addChangeListener(this);
			this.x2SizeSlider.setEnabled(false);
			this.x2SizeSlider.setToolTipText("Set size of shown interval");

			// interval scroll bar
			this.x2IntervalScrollBar = new JScrollBar(JScrollBar.HORIZONTAL, 0, 50, 0, 100);
			this.x2IntervalScrollBar.setPreferredSize(new Dimension(100, 20));
			this.x2IntervalScrollBar.setEnabled(false);
			this.x2IntervalScrollBar.addAdjustmentListener(new AdjustmentListener() {
				@Override
				public void adjustmentValueChanged(AdjustmentEvent e) {
					if (parent instanceof MultiScalarVisualizer) {
						if (((MultiScalarVisualizer) parent).xAxis2
								.getRangePolicy() instanceof RangePolicyFixedViewport) {
							double lowP = 1.0 * x2IntervalScrollBar.getValue() / 100;
							double highP = 1.0
									* (x2IntervalScrollBar.getValue() + x2IntervalScrollBar.getModel().getExtent())
									/ 100;

							double minTemp = 0;
							double maxTemp = 10;

							// get range of plotted data
							for (Object t : ((MultiScalarVisualizer) parent).xAxis2.getTraces()) {
								if (t instanceof Trace2DSimple) {
									double minX = ((Trace2DSimple) t).getMinX();
									double maxX = ((Trace2DSimple) t).getMaxX();
									if (minTemp > minX)
										minTemp = minX;
									if (maxTemp < maxX)
										maxTemp = maxX;
								}
							}

							int minTimestampNew = (int) Math.floor(lowP * (maxTemp - minTemp));
							int maxTimestampNew = (int) Math.floor(highP * (maxTemp - minTemp));

							((MultiScalarVisualizer) parent).xAxis2
									.setRange(new Range(minTimestampNew, maxTimestampNew));
							// update x2 ticks
							((MultiScalarVisualizer) parent).updateX2Ticks();
						}
					}
				}
			});

			// add components
			lowerPanel.add(x2Label);
			lowerPanel.add(this.x2ShowAllCheckBox);
			lowerPanel.add(this.x2SizeSlider);
			lowerPanel.add(this.x2IntervalScrollBar);

			/** add lower panel **/
			c.gridy = 1;
			this.intervalPanel.add(lowerPanel, c);
		} else {
			// if parent not multiscalarvisualizer -> add dummy panel
			JPanel dummyPanel = new JPanel();
			dummyPanel.setPreferredSize(new Dimension(size.width - 5, (int) Math.floor((size.height - 5) / 2)));

			/** add dummy panel **/
			c.gridy = 1;
			this.intervalPanel.add(dummyPanel, c);
		}

		// add intervalPanel to menubar
		this.add(this.intervalPanel);
	}

	/**
	 * Adds a left y-axis options panel to the menu bar.
	 * 
	 * @param size
	 */
	private void addYOptionsPanel(Dimension size) {
		this.yLeftOptionsPanel = new JPanel();
		this.yLeftOptionsPanel.setLayout(new GridBagLayout());
		this.yLeftOptionsPanel.setPreferredSize(size);
		this.yLeftOptionsPanel.setBorder(this.itemBorder);

		GridBagConstraints yLeftOptionsPanelConstraints = new GridBagConstraints();

		// toggle left y axis grid button
		final JButton toggleGridYLeftButton = new JButton("y1");
		toggleGridYLeftButton.setFont(MainDisplay.config.getDefaultFont());
		toggleGridYLeftButton.setForeground(Color.GRAY);
		toggleGridYLeftButton
				.setPreferredSize(new Dimension(size.width - 5, (int) Math.floor((size.getHeight() - 5) / 2)));
		toggleGridYLeftButton.setMargin(new Insets(0, 0, 0, 0));
		toggleGridYLeftButton.setToolTipText("Show grid of left y-axis (y1).");
		toggleGridYLeftButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				if (toggleGridYLeftButton.getForeground().equals(Color.GRAY)) {
					toggleGridYLeftButton.setForeground(MainDisplay.config.getDefaultFontColor());
					toggleGridYLeftButton.setToolTipText("Hide grid of left y-axis (y1).");
				} else {
					toggleGridYLeftButton.setForeground(Color.GRAY);
					toggleGridYLeftButton.setToolTipText("Show grid of left y-axis (y1).");
				}
				parent.toggleY1Grid();
			}
		});
		yLeftOptionsPanelConstraints.gridx = 0;
		yLeftOptionsPanelConstraints.gridy = 0;
		this.yLeftOptionsPanel.add(toggleGridYLeftButton, yLeftOptionsPanelConstraints);

		if (this.parent instanceof LabelVisualizer) {
			// add dummy panel
			JPanel dummyP = new JPanel();
			dummyP.setPreferredSize(
					new Dimension(new Dimension(size.width - 5, (int) Math.floor((size.getHeight() - 5) / 2))));
			yLeftOptionsPanelConstraints.gridx = 0;
			yLeftOptionsPanelConstraints.gridy = 1;
			this.yLeftOptionsPanel.add(dummyP, yLeftOptionsPanelConstraints);
		} else {
			// toggle right y axis grid button
			final JButton toggleGridYRightButton = new JButton("y2");
			toggleGridYRightButton.setFont(MainDisplay.config.getDefaultFont());
			toggleGridYRightButton.setForeground(Color.GRAY);
			toggleGridYRightButton.setPreferredSize(
					new Dimension(new Dimension(size.width - 5, (int) Math.floor((size.getHeight() - 5) / 2))));
			toggleGridYRightButton.setMargin(new Insets(0, 0, 0, 0));
			toggleGridYRightButton.setToolTipText("Show grid of right y-axis (y2).");
			toggleGridYRightButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent event) {
					if (toggleGridYRightButton.getForeground().equals(Color.GRAY)) {
						toggleGridYRightButton.setForeground(MainDisplay.config.getDefaultFontColor());
						toggleGridYRightButton.setToolTipText("Hide grid of right y-axis (y2).");
					} else {
						toggleGridYRightButton.setForeground(Color.GRAY);
						toggleGridYRightButton.setToolTipText("Show grid of right y-axis (y2).");
					}
					parent.toggleY2Grid();
				}
			});
			yLeftOptionsPanelConstraints.gridy = 1;

			// if(this.parent instanceof LabelVisualizer)
			// toggleGridYRightButton.setVisible(false);
			this.yLeftOptionsPanel.add(toggleGridYRightButton, yLeftOptionsPanelConstraints);
		}

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
		this.yRightOptionsPanel.setBorder(this.itemBorder);

		GridBagConstraints yRightOptionsPanelConstraints = new GridBagConstraints();

		// toggle right y axis grid button
		final JButton toggleGridYRightButton = new JButton("y2");
		toggleGridYRightButton.setFont(MainDisplay.config.getDefaultFont());
		toggleGridYRightButton.setForeground(Color.GRAY);
		toggleGridYRightButton.setPreferredSize(
				new Dimension(new Dimension(size.width - 5, (int) Math.floor((size.getHeight() - 5) / 2))));
		toggleGridYRightButton.setMargin(new Insets(0, 0, 0, 0));
		toggleGridYRightButton.setToolTipText("Show grid of right y-axis (y2).");
		toggleGridYRightButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				if (toggleGridYRightButton.getForeground().equals(Color.GRAY)) {
					toggleGridYRightButton.setForeground(MainDisplay.config.getDefaultFontColor());
					toggleGridYRightButton.setToolTipText("Hide grid of right y-axis (y2).");
				} else {
					toggleGridYRightButton.setForeground(Color.GRAY);
					toggleGridYRightButton.setToolTipText("Show grid of right y-axis (y2).");
				}
				parent.toggleY2Grid();
			}
		});
		yRightOptionsPanelConstraints.gridx = 0;
		yRightOptionsPanelConstraints.gridy = 0;
		this.yRightOptionsPanel.add(toggleGridYRightButton, yRightOptionsPanelConstraints);

		// toggle right y axis log button
		final JButton toggleLogYRightButton = new JButton("+log y1");
		toggleLogYRightButton.setFont(MainDisplay.config.getDefaultFont());
		toggleLogYRightButton.setForeground(Color.GRAY);
		toggleLogYRightButton.setPreferredSize(
				new Dimension(new Dimension(size.width - 5, (int) Math.floor((size.getHeight() - 5) / 2))));
		toggleLogYRightButton.setMargin(new Insets(0, 0, 0, 0));
		yRightOptionsPanelConstraints.gridx = 0;
		yRightOptionsPanelConstraints.gridy = 1;
		this.yRightOptionsPanel.add(toggleLogYRightButton, yRightOptionsPanelConstraints);

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
		this.xOptionsPanel.setBorder(this.itemBorder);

		GridBagConstraints xAxisOptionsPanelConstraints = new GridBagConstraints();

		// toggle x axis grid button
		final JButton toggleGridX1Button = new JButton("x1");
		toggleGridX1Button.setFont(MainDisplay.config.getDefaultFont());
		toggleGridX1Button.setForeground(Color.GRAY);
		toggleGridX1Button.setPreferredSize(
				new Dimension(new Dimension(size.width - 5, (int) Math.floor((size.getHeight() - 5) / 2))));
		toggleGridX1Button.setMargin(new Insets(0, 0, 0, 0));
		toggleGridX1Button.setToolTipText("Show grid of x1.");
		toggleGridX1Button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				if (toggleGridX1Button.getForeground().equals(Color.GRAY)) {
					toggleGridX1Button.setForeground(MainDisplay.config.getDefaultFontColor());
					toggleGridX1Button.setToolTipText("Hide grid of x1.");
				} else {
					toggleGridX1Button.setForeground(Color.GRAY);
					toggleGridX1Button.setToolTipText("Show grid of x1.");
				}
				parent.toggleX1Grid();
			}
		});
		xAxisOptionsPanelConstraints.gridx = 0;
		xAxisOptionsPanelConstraints.gridy = 0;
		this.xOptionsPanel.add(toggleGridX1Button, xAxisOptionsPanelConstraints);

		// if parent is multiscalarvisualizer -> add grid button for x2
		if (parent instanceof MultiScalarVisualizer) {
			final JButton toggleGridX2Button = new JButton("x2");
			toggleGridX2Button.setFont(MainDisplay.config.getDefaultFont());
			toggleGridX2Button.setForeground(Color.GRAY);
			toggleGridX2Button.setPreferredSize(
					new Dimension(new Dimension(size.width - 5, (int) Math.floor((size.getHeight() - 5) / 2))));
			toggleGridX2Button.setMargin(new Insets(0, 0, 0, 0));
			toggleGridX2Button.setToolTipText("Show grid of x2.");
			toggleGridX2Button.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent event) {
					if (toggleGridX2Button.getForeground().equals(Color.GRAY)) {
						toggleGridX2Button.setForeground(MainDisplay.config.getDefaultFontColor());
						toggleGridX2Button.setToolTipText("Hide grid of x2.");
					} else {
						toggleGridX2Button.setForeground(Color.GRAY);
						toggleGridX2Button.setToolTipText("Show grid of x2.");
					}
					((MultiScalarVisualizer) parent).toggleX2Grid();
				}
			});
			xAxisOptionsPanelConstraints.gridx = 0;
			xAxisOptionsPanelConstraints.gridy = 1;
			this.xOptionsPanel.add(toggleGridX2Button, xAxisOptionsPanelConstraints);
		} else {
			// else add dummy panel
			JPanel dummyP = new JPanel();
			dummyP.setPreferredSize(
					new Dimension(new Dimension(size.width - 5, (int) Math.floor((size.getHeight() - 5) / 2))));
			xAxisOptionsPanelConstraints.gridx = 0;
			xAxisOptionsPanelConstraints.gridy = 1;
			this.xOptionsPanel.add(dummyP, xAxisOptionsPanelConstraints);
		}

		// add to menu bar
		this.add(this.xOptionsPanel);
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
		this.coordsPanel.setBorder(this.itemBorder);

		GridBagConstraints coordsPanelConstraints = new GridBagConstraints();
		coordsPanelConstraints.gridx = 0;
		coordsPanelConstraints.gridy = 0;

		// x coords label
		xCoordsLabel = new JLabel("x:");
		xCoordsLabel.setFont(this.coordsFont);
		xCoordsLabel.setForeground(this.coordsFontColor);
		xCoordsLabel.setPreferredSize(new Dimension(10, 20));
		this.coordsPanel.add(xCoordsLabel, coordsPanelConstraints);
		// x coords value
		this.xCoordsValue = new JLabel();
		if ((this.parent instanceof MetricVisualizer)
				&& MainDisplay.config.getMetricVisualizerConfigs()[0].getxAxisType().equals("date")) {
			SimpleDateFormat tempDateFormat = new SimpleDateFormat(
					MainDisplay.config.getMetricVisualizerConfigs()[0].getxAxisFormat());
			this.xCoordsValue.setText(tempDateFormat.format(new Date(0)));
		} else {
			this.xCoordsValue.setText("0");
		}
		this.xCoordsValue.setFont(this.coordsFont);
		this.xCoordsValue.setForeground(this.coordsFontColor);
		this.xCoordsValue.setPreferredSize(new Dimension(120, 20));
		coordsPanelConstraints.gridx = 1;
		coordsPanelConstraints.gridy = 0;
		this.coordsPanel.add(this.xCoordsValue, coordsPanelConstraints);

		// y coords label
		yCoordsLabel = new JLabel("y:");
		yCoordsLabel.setFont(this.coordsFont);
		yCoordsLabel.setForeground(this.coordsFontColor);
		yCoordsLabel.setPreferredSize(new Dimension(10, 20));
		coordsPanelConstraints.gridx = 0;
		coordsPanelConstraints.gridy = 1;
		this.coordsPanel.add(yCoordsLabel, coordsPanelConstraints);

		// y coords value
		this.yCoordsValue = new JLabel("0");
		this.yCoordsValue.setFont(this.coordsFont);
		this.yCoordsValue.setForeground(this.coordsFontColor);
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
	 * Updates the coordinate labels in the coordinate-panel.
	 * 
	 * @param x
	 *            x-value
	 * @param y
	 *            y-value
	 */
	public void updateCoordsPanel(double x, double y) {
		if (this.coordsPanel != null) {
			if (this.parent instanceof LabelVisualizer) {
				if (MainDisplay.config.getMetricVisualizerConfigs()[0].getxAxisType().equals("date")) {
					SimpleDateFormat tempDateFormat = new SimpleDateFormat(
							MainDisplay.config.getMetricVisualizerConfigs()[0].getxAxisFormat());
					this.xCoordsValue.setText(tempDateFormat.format(new Date((long) Math.floor(x))));
				} else {
					this.xCoordsValue.setText("" + x);
				}

				LabelVisualizer labelVisualizer = (LabelVisualizer) this.parent;
				String yValueString = labelVisualizer.getValue(x, y);
				this.yCoordsValue.setText("" + yValueString);
			} else {
				if ((this.parent instanceof MetricVisualizer)
						&& MainDisplay.config.getMetricVisualizerConfigs()[0].getxAxisType().equals("date")) {
					SimpleDateFormat tempDateFormat = new SimpleDateFormat(
							MainDisplay.config.getMetricVisualizerConfigs()[0].getxAxisFormat());
					this.xCoordsValue.setText(tempDateFormat.format(new Date((long) Math.floor(x))));
				} else {
					this.xCoordsValue.setText("" + x);
				}
				this.yCoordsValue.setText("" + y);
			}
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

	/** Gets called on mouse release after a size-slider has been moved **/
	public void stateChanged(ChangeEvent e) {
		JSlider source;
		JScrollBar intervalScrollBar;

		if (e.getSource() instanceof JSlider) {
			source = (JSlider) e.getSource();
			intervalScrollBar = this.x1IntervalScrollBar;
			// check if event is coming from x2SizeSlider
			if (e.getSource().equals(this.x2SizeSlider)) {
				intervalScrollBar = this.x2IntervalScrollBar;
			} else {
				// if its x1 and parent is not multiscalar vis --> broadcast new
				// value
				if (!(this.parent instanceof MultiScalarVisualizer))
					this.parent.broadcastX1IntervalSizeSliderChange(source.getValue());
			}

			// check if slider is set on the right end
			if (intervalScrollBar.getValue() + intervalScrollBar.getModel().getExtent() == intervalScrollBar
					.getMaximum()) {
				int oldValue = intervalScrollBar.getValue();
				int oldExtent = intervalScrollBar.getModel().getExtent();

				int offset = source.getValue() - oldExtent;

				intervalScrollBar.setValue(oldValue - offset);
				intervalScrollBar.getModel().setExtent(source.getValue());

				// if slider is not set on right end anymore, adjust value
				if (intervalScrollBar.getValue() + intervalScrollBar.getModel().getExtent() != intervalScrollBar
						.getMaximum()) {
					intervalScrollBar
							.setValue(intervalScrollBar.getMaximum() - intervalScrollBar.getModel().getExtent());
				}
				// if slider is in between, just resize it
			} else {
				intervalScrollBar.getModel().setExtent(source.getValue());
			}
		}
		parent.updateX1Ticks();
		if (parent instanceof MultiScalarVisualizer)
			((MultiScalarVisualizer) parent).updateX2Ticks();
	}

	/** returns the intervalslider **/
	public JScrollBar getIntervalSlider() {
		return this.x1IntervalScrollBar;
	}

	/** Sets the x-coords label text. **/
	public void setXCoordsLabelText(String text) {
		this.xCoordsLabel.setText(text);
	}

	/** Sets the y-coords label text. **/
	public void setYCoordsLabelText(String text) {
		this.yCoordsLabel.setText(text);
	}

	/** Sets the x1 interval size slider to a value. **/
	public void setX1IntervalSizeSlider(int value) {
		if (this.x1SizeSlider != null && this.x1SizeSlider.isEnabled())
			this.x1SizeSlider.setValue(value);
	}

	/** Set x1 interval scroll bar. **/
	public void setX1IntervalScrollBar(int value) {
		if (this.x1IntervalScrollBar != null & this.x1IntervalScrollBar.isEnabled())
			this.x1IntervalScrollBar.setValue(value);
	}

	/** Set x1 interval enabled. **/
	public void setX1IntervalEnabled(boolean enabled) {
		if (this.x1ShowAllCheckBox != null) {
			if (this.x1ShowAllCheckBox.isSelected()) {
				if (!enabled)
					this.x1ShowAllCheckBox.doClick();
			} else {
				if (enabled)
					this.x1ShowAllCheckBox.doClick();
			}
		}
	}

}
