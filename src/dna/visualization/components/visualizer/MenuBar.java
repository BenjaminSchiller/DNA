package dna.visualization.components.visualizer;

import info.monitorenter.gui.chart.rangepolicies.RangePolicyFixedViewport;
import info.monitorenter.gui.chart.rangepolicies.RangePolicyUnbounded;
import info.monitorenter.gui.chart.traces.Trace2DLtd;
import info.monitorenter.gui.chart.traces.Trace2DSimple;
import info.monitorenter.util.Range;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
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
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import dna.visualization.GuiOptions;

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

	// creates the default menu with all panels
	public MenuBar(Visualizer parent, Dimension d) {
		this(parent, d, true, true, true, true, true);
	}

	// constructor
	public MenuBar(Visualizer parent, Dimension size, boolean addCoordsPanel,
			boolean addIntervalPanel, boolean addXOptionsPanel,
			boolean addYLeftOptionsPanel, boolean addYRightOptionsPanel) {
		this.parent = parent;
		this.thisMenuBar = this;
		this.setLayout(new GridBagLayout());
		this.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));

		this.setPreferredSize(size);
		this.setBorder(BorderFactory.createEtchedBorder((EtchedBorder.LOWERED)));
		GuiOptions.menuBarItemBorder
				.setTitleFont(GuiOptions.defaultFontBorders);

		int spaceUsed = 0;

		// add coords panel
		if (addCoordsPanel) {
			this.addCoordsPanel(GuiOptions.menuBarCoordsPanelSize);
			spaceUsed += GuiOptions.menuBarCoordsPanelSize.width;
		}

		// add interval panel
		if (addIntervalPanel) {
			this.addIntervalPanel(GuiOptions.menuBarIntervalPanelSize);
			spaceUsed += GuiOptions.menuBarIntervalPanelSize.width;
		}

		// add x axis options panel
		if (addXOptionsPanel) {
			this.addXOptionsPanel(GuiOptions.menuBarXOptionsPanelSize);
			spaceUsed += GuiOptions.menuBarXOptionsPanelSize.width;
		}

		// add left y-axis options panel
		if (addYLeftOptionsPanel) {
			this.addYLeftOptionsPanel(GuiOptions.menuBarYLeftOptionsPanelSize);
			spaceUsed += GuiOptions.menuBarYLeftOptionsPanelSize.width;
		}

		// add right y-axis option panel
		if (addYRightOptionsPanel) {
			this.addYRightOptionsPanel(GuiOptions.menuBarYRightOptionsPanelSize);
			spaceUsed += GuiOptions.menuBarYRightOptionsPanelSize.width;
		}
	}

	// menu bar elements
	private JPanel coordsPanel;
	private JLabel xCoordsValue;
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
		this.intervalPanel.setBorder(GuiOptions.menuBarItemBorder);

		GridBagConstraints c = new GridBagConstraints();

		/** upper panel **/
		final JPanel upperPanel = new JPanel();
		upperPanel.setPreferredSize(new Dimension((int) (size.getWidth() - 5),
				(int) Math.floor((size.getHeight() - 5) / 2)));
		upperPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));

		// x1 label
		JLabel x1Label = new JLabel("x1:");
		x1Label.setPreferredSize(new Dimension(18, 20));
		x1Label.setFont(GuiOptions.menuBarCoordsFont);
		x1Label.setForeground(GuiOptions.defaultFontColor);

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
					parent.xAxis1
							.setRangePolicy(new RangePolicyFixedViewport());
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
					double highP = 1.0 * (x1IntervalScrollBar.getValue() + x1IntervalScrollBar
							.getModel().getExtent()) / 100;

					int minTimestampNew = (int) Math.floor(minTemp)
							+ (int) Math.floor(lowP * (maxTemp - minTemp));
					int maxTimestampNew = (int) Math.floor(minTemp)
							+ (int) Math.floor(highP * (maxTemp - minTemp));
					parent.xAxis1.setRange(new Range(minTimestampNew,
							maxTimestampNew));
					x1IntervalScrollBar.setEnabled(true);
					x1SizeSlider.setEnabled(true);
				}
			}
		});

		// size slider
		this.x1SizeSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 50);
		this.x1SizeSlider.setName("x1SizeSlider");
		this.x1SizeSlider.setPreferredSize(new Dimension(60, 20));
		this.x1SizeSlider.setFont(GuiOptions.defaultFont);
		this.x1SizeSlider.addChangeListener(this);
		this.x1SizeSlider.setEnabled(false);
		this.x1SizeSlider.setToolTipText("Set size of shown interval");

		// interval scroll bar
		this.x1IntervalScrollBar = new JScrollBar(JScrollBar.HORIZONTAL, 0, 50,
				0, 100);
		this.x1IntervalScrollBar.setPreferredSize(new Dimension(100, 20));
		this.x1IntervalScrollBar.setEnabled(false);
		this.x1IntervalScrollBar
				.addAdjustmentListener(new AdjustmentListener() {
					@Override
					public void adjustmentValueChanged(AdjustmentEvent e) {
						if (parent.chart.getAxisX().getRangePolicy() instanceof RangePolicyFixedViewport) {
							double lowP = 1.0 * x1IntervalScrollBar.getValue() / 100;
							double highP = 1.0 * (x1IntervalScrollBar
									.getValue() + x1IntervalScrollBar
									.getModel().getExtent()) / 100;

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
							int minTimestampNew = (int) Math.floor(minTemp)
									+ (int) Math.floor(lowP
											* (maxTemp - minTemp));
							int maxTimestampNew = (int) Math.floor(minTemp)
									+ (int) Math.floor(highP
											* (maxTemp - minTemp));
							parent.setMinShownTimestamp((long) minTimestampNew);
							parent.setMaxShownTimestamp((long) maxTimestampNew);
							parent.xAxis1.setRange(new Range(minTimestampNew,
									maxTimestampNew));
							// update x ticks
							parent.updateX1Ticks();
						}
					}

				});

		// add components
		upperPanel.add(x1Label);
		upperPanel.add(this.x1ShowAllCheckBox);
		upperPanel.add(this.x1SizeSlider);
		upperPanel.add(this.x1IntervalScrollBar);

		/** add uppper panel **/
		c.gridx = 0;
		c.gridy = 0;
		this.intervalPanel.add(upperPanel, c);

		// if menubar of a multi scalar visualizer -> add same options for x2 in
		// a lower panel
		if (parent instanceof MultiScalarVisualizer) {
			// lower panel
			final JPanel lowerPanel = new JPanel();
			lowerPanel.setPreferredSize(new Dimension(
					(int) (size.getWidth() - 5), (int) Math.floor((size
							.getHeight() - 5) / 2)));
			lowerPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));

			// x1 label
			JLabel x2Label = new JLabel("x2:");
			x2Label.setPreferredSize(new Dimension(18, 20));
			x2Label.setFont(GuiOptions.menuBarCoordsFont);
			x2Label.setForeground(GuiOptions.defaultFontColor);

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
							((MultiScalarVisualizer) parent).xAxis2
									.setRangePolicy(new RangePolicyUnbounded());
						x2IntervalScrollBar.setEnabled(false);
						x2SizeSlider.setEnabled(false);
					} else {
						// if not selected: enable bar and slider and set x2 to
						// fixedviewport policy
						if (parent instanceof MultiScalarVisualizer) {
							((MultiScalarVisualizer) parent).xAxis2
									.setRangePolicy(new RangePolicyFixedViewport());
							double lowP = 1.0 * x2IntervalScrollBar.getValue() / 100;
							double highP = 1.0 * (x2IntervalScrollBar
									.getValue() + x2IntervalScrollBar
									.getModel().getExtent()) / 100;

							double minTemp = 0;
							double maxTemp = 10;

							// get range of plotted data
							for (Object t : ((MultiScalarVisualizer) parent).xAxis2
									.getTraces()) {
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
									+ (int) Math.floor(lowP
											* (maxTemp - minTemp));
							int maxTimestampNew = (int) Math.floor(minTemp)
									+ (int) Math.floor(highP
											* (maxTemp - minTemp));

							((MultiScalarVisualizer) parent).xAxis2
									.setRange(new Range(minTimestampNew,
											maxTimestampNew));
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
			this.x2SizeSlider.setFont(GuiOptions.defaultFont);
			this.x2SizeSlider.addChangeListener(this);
			this.x2SizeSlider.setEnabled(false);
			this.x2SizeSlider.setToolTipText("Set size of shown interval");

			// interval scroll bar
			this.x2IntervalScrollBar = new JScrollBar(JScrollBar.HORIZONTAL, 0,
					50, 0, 100);
			this.x2IntervalScrollBar.setPreferredSize(new Dimension(100, 20));
			this.x2IntervalScrollBar.setEnabled(false);
			this.x2IntervalScrollBar
					.addAdjustmentListener(new AdjustmentListener() {
						@Override
						public void adjustmentValueChanged(AdjustmentEvent e) {
							if (parent instanceof MultiScalarVisualizer) {
								if (((MultiScalarVisualizer) parent).xAxis2
										.getRangePolicy() instanceof RangePolicyFixedViewport) {
									double lowP = 1.0 * x2IntervalScrollBar
											.getValue() / 100;
									double highP = 1.0 * (x2IntervalScrollBar
											.getValue() + x2IntervalScrollBar
											.getModel().getExtent()) / 100;

									double minTemp = 0;
									double maxTemp = 10;

									// get range of plotted data
									for (Object t : ((MultiScalarVisualizer) parent).xAxis2
											.getTraces()) {
										if (t instanceof Trace2DSimple) {
											double minX = ((Trace2DSimple) t)
													.getMinX();
											double maxX = ((Trace2DSimple) t)
													.getMaxX();
											if (minTemp > minX)
												minTemp = minX;
											if (maxTemp < maxX)
												maxTemp = maxX;
										}
									}

									int minTimestampNew = (int) Math.floor(lowP
											* (maxTemp - minTemp));
									int maxTimestampNew = (int) Math
											.floor(highP * (maxTemp - minTemp));

									((MultiScalarVisualizer) parent).xAxis2
											.setRange(new Range(
													minTimestampNew,
													maxTimestampNew));
									// update x2 ticks
									((MultiScalarVisualizer) parent)
											.updateX2Ticks();
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
			dummyPanel.setPreferredSize(new Dimension(size.width - 5,
					(int) Math.floor((size.height - 5) / 2)));

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
	private void addYLeftOptionsPanel(Dimension size) {
		this.yLeftOptionsPanel = new JPanel();
		this.yLeftOptionsPanel.setLayout(new GridBagLayout());
		this.yLeftOptionsPanel.setPreferredSize(size);
		this.yLeftOptionsPanel.setBorder(GuiOptions.menuBarItemBorder);

		GridBagConstraints yLeftOptionsPanelConstraints = new GridBagConstraints();

		// toggle left y axis grid button
		final JButton toggleGridYLeftButton = new JButton("+Grid y1");
		toggleGridYLeftButton.setFont(GuiOptions.defaultFont);
		toggleGridYLeftButton.setForeground(GuiOptions.defaultFontColor);
		toggleGridYLeftButton.setPreferredSize(new Dimension(size.width - 5,
				(int) Math.floor((size.getHeight() - 5) / 2)));
		toggleGridYLeftButton.setMargin(new Insets(0, 0, 0, 0));
		toggleGridYLeftButton.setToolTipText("Show grid of left y-axis (y1).");
		toggleGridYLeftButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				if (toggleGridYLeftButton.getText().equals("+Grid y1")) {
					toggleGridYLeftButton.setText("-Grid y1");
					toggleGridYLeftButton
							.setToolTipText("Hide grid of left y-axis (y1).");
				} else {
					toggleGridYLeftButton.setText("+Grid y1");
					toggleGridYLeftButton
							.setToolTipText("Show grid of left y-axis (y1).");
				}
				parent.toggleY1Grid();
			}
		});
		yLeftOptionsPanelConstraints.gridx = 0;
		yLeftOptionsPanelConstraints.gridy = 0;
		this.yLeftOptionsPanel.add(toggleGridYLeftButton,
				yLeftOptionsPanelConstraints);

		// toggle left y axis log button
		final JButton toggleLogYLeftButton = new JButton("+log y2");
		toggleLogYLeftButton.setFont(GuiOptions.defaultFont);
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
		this.yRightOptionsPanel.setBorder(GuiOptions.menuBarItemBorder);

		GridBagConstraints yRightOptionsPanelConstraints = new GridBagConstraints();

		// toggle right y axis grid button
		final JButton toggleGridYRightButton = new JButton("+Grid y2");
		toggleGridYRightButton.setFont(GuiOptions.defaultFont);
		toggleGridYRightButton.setForeground(GuiOptions.defaultFontColor);
		toggleGridYRightButton.setPreferredSize(new Dimension(new Dimension(
				size.width - 5, (int) Math.floor((size.getHeight() - 5) / 2))));
		toggleGridYRightButton.setMargin(new Insets(0, 0, 0, 0));
		toggleGridYRightButton
				.setToolTipText("Show grid of right y-axis (y2).");
		toggleGridYRightButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				if (toggleGridYRightButton.getText().equals("+Grid y2")) {
					toggleGridYRightButton.setText("-Grid y2");
					toggleGridYRightButton
							.setToolTipText("Hide grid of right y-axis (y2).");
				}

				else {
					toggleGridYRightButton.setText("+Grid y2");
					toggleGridYRightButton
							.setToolTipText("Show grid of right y-axis (y2).");
				}
				parent.toggleY2Grid();
			}
		});
		yRightOptionsPanelConstraints.gridx = 0;
		yRightOptionsPanelConstraints.gridy = 0;
		this.yRightOptionsPanel.add(toggleGridYRightButton,
				yRightOptionsPanelConstraints);

		// toggle right y axis log button
		final JButton toggleLogYRightButton = new JButton("+log y1");
		toggleLogYRightButton.setFont(GuiOptions.defaultFont);
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
		this.xOptionsPanel.setBorder(GuiOptions.menuBarItemBorder);

		GridBagConstraints xAxisOptionsPanelConstraints = new GridBagConstraints();

		// toggle x axis grid button
		final JButton toggleGridX1Button = new JButton("+Grid x1");
		toggleGridX1Button.setFont(GuiOptions.defaultFont);
		toggleGridX1Button.setForeground(GuiOptions.defaultFontColor);
		toggleGridX1Button.setPreferredSize(new Dimension(new Dimension(
				size.width - 5, (int) Math.floor((size.getHeight() - 5) / 2))));
		toggleGridX1Button.setMargin(new Insets(0, 0, 0, 0));
		toggleGridX1Button.setToolTipText("Show grid of x1.");
		toggleGridX1Button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				if (toggleGridX1Button.getText().equals("+Grid x1")) {
					toggleGridX1Button.setText("-Grid x1");
					toggleGridX1Button.setToolTipText("Hide grid of x1.");
				} else {
					toggleGridX1Button.setText("+Grid x1");
					toggleGridX1Button.setToolTipText("Show grid of x1.");
				}
				parent.toggleX1Grid();
			}
		});
		xAxisOptionsPanelConstraints.gridx = 0;
		xAxisOptionsPanelConstraints.gridy = 0;
		this.xOptionsPanel
				.add(toggleGridX1Button, xAxisOptionsPanelConstraints);

		// if parent is multiscalarvisualizer -> add grid button for x2
		if (parent instanceof MultiScalarVisualizer) {
			final JButton toggleGridX2Button = new JButton("+Grid x2");
			toggleGridX2Button.setFont(GuiOptions.defaultFont);
			toggleGridX2Button.setForeground(GuiOptions.defaultFontColor);
			toggleGridX2Button.setPreferredSize(new Dimension(new Dimension(
					size.width - 5,
					(int) Math.floor((size.getHeight() - 5) / 2))));
			toggleGridX2Button.setMargin(new Insets(0, 0, 0, 0));
			toggleGridX2Button.setToolTipText("Show grid of x2.");
			toggleGridX2Button.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent event) {
					if (toggleGridX2Button.getText().equals("+Grid x2")) {
						toggleGridX2Button.setText("-Grid x2");
						toggleGridX2Button.setToolTipText("Hide grid of x2.");
					} else {
						toggleGridX2Button.setText("+Grid x2");
						toggleGridX2Button.setToolTipText("Show grid of x2.");
					}
					((MultiScalarVisualizer) parent).toggleX2Grid();
				}
			});
			xAxisOptionsPanelConstraints.gridx = 0;
			xAxisOptionsPanelConstraints.gridy = 1;
			this.xOptionsPanel.add(toggleGridX2Button,
					xAxisOptionsPanelConstraints);
		} else {
			// else add dummy panel
			JPanel dummyP = new JPanel();
			dummyP.setPreferredSize(new Dimension(new Dimension(size.width - 5,
					(int) Math.floor((size.getHeight() - 5) / 2))));
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
		this.coordsPanel.setBorder(GuiOptions.menuBarItemBorder);

		GridBagConstraints coordsPanelConstraints = new GridBagConstraints();
		coordsPanelConstraints.gridx = 0;
		coordsPanelConstraints.gridy = 0;

		// x coords label
		JLabel xCoordsLabel = new JLabel("x:");
		xCoordsLabel.setFont(GuiOptions.menuBarCoordsFont);
		xCoordsLabel.setForeground(GuiOptions.menuBarCoordsFontColor);
		xCoordsLabel.setPreferredSize(new Dimension(10, 20));
		this.coordsPanel.add(xCoordsLabel, coordsPanelConstraints);
		// x coords value
		this.xCoordsValue = new JLabel();
		if (GuiOptions.metricVisualizerXAxisType.equals("date")
				&& (this.parent instanceof MetricVisualizer)) {
			SimpleDateFormat tempDateFormat = new SimpleDateFormat(
					GuiOptions.metricVisualizerXAxisFormat);
			this.xCoordsValue.setText(tempDateFormat.format(new Date(0)));
		} else {
			this.xCoordsValue.setText("0");
		}
		this.xCoordsValue.setFont(GuiOptions.menuBarCoordsFont);
		this.xCoordsValue.setForeground(GuiOptions.menuBarCoordsFontColor);
		this.xCoordsValue.setPreferredSize(new Dimension(120, 20));
		coordsPanelConstraints.gridx = 1;
		coordsPanelConstraints.gridy = 0;
		this.coordsPanel.add(this.xCoordsValue, coordsPanelConstraints);

		// y coords label
		JLabel yCoordsLabel = new JLabel("y:");
		yCoordsLabel.setFont(GuiOptions.menuBarCoordsFont);
		yCoordsLabel.setForeground(GuiOptions.menuBarCoordsFontColor);
		yCoordsLabel.setPreferredSize(new Dimension(10, 20));
		coordsPanelConstraints.gridx = 0;
		coordsPanelConstraints.gridy = 1;
		this.coordsPanel.add(yCoordsLabel, coordsPanelConstraints);

		// y coords value
		this.yCoordsValue = new JLabel("0");
		this.yCoordsValue.setFont(GuiOptions.menuBarCoordsFont);
		this.yCoordsValue.setForeground(GuiOptions.menuBarCoordsFontColor);
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
	public void updateCoordsPanel(int x, double y) {
		if (this.coordsPanel != null) {
			if (GuiOptions.metricVisualizerXAxisType.equals("date")
					&& (this.parent instanceof MetricVisualizer)) {
				SimpleDateFormat tempDateFormat = new SimpleDateFormat(
						GuiOptions.metricVisualizerXAxisFormat);
				this.xCoordsValue.setText(tempDateFormat.format(new Date(x)));
			} else {
				this.xCoordsValue.setText("" + x);
			}
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

	/** Gets called on mouse release after a size-slider has been moved **/
	public void stateChanged(ChangeEvent e) {
		JSlider source;
		JScrollBar intervalScrollBar;

		if (e.getSource() instanceof JSlider) {
			source = (JSlider) e.getSource();
			intervalScrollBar = this.x1IntervalScrollBar;
			// check if event is coming from x2SizeSlider
			if (e.getSource().equals(this.x2SizeSlider))
				intervalScrollBar = this.x2IntervalScrollBar;

			// check if slider is set on the right end
			if (intervalScrollBar.getValue()
					+ intervalScrollBar.getModel().getExtent() == intervalScrollBar
						.getMaximum()) {
				int oldValue = intervalScrollBar.getValue();
				int oldExtent = intervalScrollBar.getModel().getExtent();

				int offset = source.getValue() - oldExtent;

				intervalScrollBar.setValue(oldValue - offset);
				intervalScrollBar.getModel().setExtent(source.getValue());

				// if slider is not set on right end anymore, adjust value
				if (intervalScrollBar.getValue()
						+ intervalScrollBar.getModel().getExtent() != intervalScrollBar
							.getMaximum()) {
					intervalScrollBar.setValue(intervalScrollBar.getMaximum()
							- intervalScrollBar.getModel().getExtent());
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

}
