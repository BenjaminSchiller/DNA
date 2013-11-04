package dna.visualization.components;

import info.monitorenter.gui.chart.Chart2D;
import info.monitorenter.gui.chart.IAxis;
import info.monitorenter.gui.chart.IAxis.AxisTitle;
import info.monitorenter.gui.chart.ITrace2D;
import info.monitorenter.gui.chart.ITracePainter;
import info.monitorenter.gui.chart.ITracePoint2D;
import info.monitorenter.gui.chart.axis.AAxis;
import info.monitorenter.gui.chart.axis.AxisLinear;
import info.monitorenter.gui.chart.axis.scalepolicy.AxisScalePolicyManualTicks;
import info.monitorenter.gui.chart.rangepolicies.RangePolicyFixedViewport;
import info.monitorenter.gui.chart.rangepolicies.RangePolicyUnbounded;
import info.monitorenter.gui.chart.traces.Trace2DLtd;
import info.monitorenter.gui.chart.traces.Trace2DSimple;
import info.monitorenter.gui.chart.traces.painters.TracePainterDisc;
import info.monitorenter.gui.chart.traces.painters.TracePainterFill;
import info.monitorenter.util.Range;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import dna.series.data.BatchData;
import dna.series.data.MetricData;
import dna.series.data.RunTime;
import dna.series.data.Value;
import dna.visualization.MainDisplay;
import dna.visualization.components.legend.Legend;

public class MetricVisualizer extends JPanel {
	// fonts
	private Font defaultFont = MainDisplay.defaultFont;
	private Font coordsFont = new Font("Dialog", Font.PLAIN, 11);

	private int DEFAULT_TRACE_LENGTH = 1000;
	private int TRACE_LENGTH;
	private Boolean TRACE_MODE_LTD;

	private Boolean DEFAULT_TRACE_MODE_LTD = false;
	private Boolean DEFAULT_PAINT_LINESPOINT = true;
	private Boolean DEFAULT_PAINT_FILL = false;

	private Chart2D chart;
	private IAxis yRight;
	private IAxis yLeft;
	private IAxis xAxis;

	// timestamps
	private long minTimestamp;
	private long maxTimestamp;
	private long minShownTimestamp;
	private long maxShownTimestamp;

	private ArrayList<String> availableValues;

	private HashMap<String, ITrace2D> traces;
	private HashMap<String, String> tracesYAxis;

	private MetricVisualizer thisM;

	private int linespointSize = 5;

	private Legend legend;

	private JPanel menuBar;

	// interval and x-range panel
	private JPanel intervalPanel;
	private JComboBox<String> intervalBox;
	private String[] intervalOptions = { "- show all", "- fixed interval",
			"-fixed length: 10", "-fixed length: 20", "-fixed length: 30",
			"-fixed length: 40", "-fixed length: 50", "-fixed length: 100",
			"-fixed length: 150", "-fixed length: 200", "-fixed length: 300",
			"-fixed length: 500" };

	private JTextField lowerBound;
	private JTextField upperBound;

	// coords view
	private JPanel coordsPanel;
	private JLabel xCoordsLabel;
	private JLabel xCoordsValue;
	private JLabel yCoordsLabel;
	private JLabel yCoordsValue;

	// yLeft options
	private JPanel yLeftOptionsPanel;
	private JButton toggleLogYLeftButton;
	private JButton toggleGridYLeftButton;

	// yRight options
	private JPanel yRightOptionsPanel;
	private JButton toggleLogYRightButton;
	private JButton toggleGridYRightButton;

	// x axis options
	private JPanel xOptionsPanel;
	private JButton toggleGridXButton;

	@SuppressWarnings("deprecation")
	public MetricVisualizer() {
		super();
		this.thisM = this;
		this.TRACE_MODE_LTD = DEFAULT_TRACE_MODE_LTD;
		this.TRACE_LENGTH = DEFAULT_TRACE_LENGTH;

		this.minTimestamp = 0;
		this.maxTimestamp = 0;
		this.minShownTimestamp = 0;
		this.maxShownTimestamp = 10;

		this.traces = new HashMap<String, ITrace2D>();
		this.availableValues = new ArrayList<String>();

		this.setPreferredSize(new Dimension(670, 410));
		// set title and border of the metric visualizer
		TitledBorder title = BorderFactory
				.createTitledBorder("Metric Visualizer");
		title.setBorder(BorderFactory
				.createEtchedBorder((EtchedBorder.LOWERED)));
		title.setTitleFont(new Font(this.defaultFont.getName(), Font.BOLD,
				this.defaultFont.getSize()));
		this.setBorder(title);

		GridBagConstraints mainConstraints = new GridBagConstraints();
		mainConstraints.fill = GridBagConstraints.HORIZONTAL;
		this.setLayout(new GridBagLayout());

		this.chart = new Chart2D();

		this.chart.setPreferredSize(new Dimension(450, 320));

		this.xAxis = this.chart.getAxisX();
		this.yLeft = this.chart.getAxisY();
		this.xAxis.setAxisTitle(new AxisTitle("Timestamp"));
		this.yLeft.setAxisTitle(new AxisTitle(""));

		this.yRight = new AxisLinear();
		this.chart.addAxisYRight((AAxis) yRight);
		this.yRight.setVisible(false);

		mainConstraints.gridx = 0;
		mainConstraints.gridy = 0;
		this.chart.setPaintLabels(false);
		this.add(this.chart, mainConstraints);

		mainConstraints.gridx = 1;
		mainConstraints.gridy = 0;
		this.legend = new Legend(this);
		this.legend.setPreferredSize(new Dimension(190, 330));
		this.add(this.legend, mainConstraints);

		/** menu bar creation **/
		mainConstraints.gridx = 0;
		mainConstraints.gridy = 1;
		mainConstraints.gridwidth = 2;
		this.menuBar = new JPanel();
		this.menuBar.setLayout(new GridBagLayout());
		this.menuBar.setPreferredSize(new Dimension(450, 50));
		this.menuBar.setBorder(BorderFactory
				.createEtchedBorder((EtchedBorder.LOWERED)));
		this.add(this.menuBar, mainConstraints);

		GridBagConstraints menuBarConstraints = new GridBagConstraints();
		menuBarConstraints.gridy = 0;
		menuBarConstraints.gridx = 0;

		// border for menu bar items
		TitledBorder menuBarItemBorder = BorderFactory.createTitledBorder("");

		/*
		 * coords panel
		 */
		this.coordsPanel = new JPanel();
		this.coordsPanel.setLayout(new GridBagLayout());
		this.coordsPanel.setPreferredSize(new Dimension(145, 45));
		this.coordsPanel.setBorder(menuBarItemBorder);
		menuBarConstraints.gridy = 0;
		menuBarConstraints.gridx = 0;
		this.menuBar.add(coordsPanel, menuBarConstraints);

		GridBagConstraints coordsPanelConstraints = new GridBagConstraints();
		coordsPanelConstraints.gridx = 0;
		coordsPanelConstraints.gridy = 0;

		// x coords label
		this.xCoordsLabel = new JLabel("x:");
		this.xCoordsLabel.setFont(coordsFont);
		this.xCoordsLabel.setPreferredSize(new Dimension(10, 20));
		this.coordsPanel.add(this.xCoordsLabel, coordsPanelConstraints);
		// x coords value
		this.xCoordsValue = new JLabel("0");
		this.xCoordsValue.setFont(coordsFont);
		this.xCoordsValue.setPreferredSize(new Dimension(120, 20));
		coordsPanelConstraints.gridx = 1;
		coordsPanelConstraints.gridy = 0;
		this.coordsPanel.add(this.xCoordsValue, coordsPanelConstraints);

		// y coords label
		this.yCoordsLabel = new JLabel("y:");
		this.yCoordsLabel.setFont(coordsFont);
		this.yCoordsLabel.setPreferredSize(new Dimension(10, 20));
		coordsPanelConstraints.gridx = 0;
		coordsPanelConstraints.gridy = 1;
		this.coordsPanel.add(this.yCoordsLabel, coordsPanelConstraints);

		// y coords value
		this.yCoordsValue = new JLabel("0");
		this.yCoordsValue.setFont(coordsFont);
		this.yCoordsValue.setPreferredSize(new Dimension(120, 20));
		coordsPanelConstraints.gridx = 1;
		coordsPanelConstraints.gridy = 1;
		this.coordsPanel.add(this.yCoordsValue, coordsPanelConstraints);

		/*
		 * interval panel
		 */
		this.intervalPanel = new JPanel();
		this.intervalPanel.setLayout(new GridBagLayout());
		this.intervalPanel.setPreferredSize(new Dimension(130, 45));
		this.intervalPanel.setBorder(menuBarItemBorder);
		menuBarConstraints.gridy = 0;
		menuBarConstraints.gridx = 1;
		this.menuBar.add(this.intervalPanel);

		GridBagConstraints intervalPanelConstraints = new GridBagConstraints();
		intervalPanelConstraints.insets = new Insets(0, 0, 1, 0);

		// intervalBox dropdown menu
		this.intervalBox = new JComboBox<String>(intervalOptions);
		this.intervalBox.setFont(this.defaultFont);
		this.intervalBox.setPreferredSize(new Dimension(125, 20));
		this.intervalBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				thisM.selectInterval(thisM.intervalBox.getSelectedIndex());
			}
		});
		BoundsPopupMenuListener listener = new BoundsPopupMenuListener(true,
				false);
		this.intervalBox.addPopupMenuListener(listener);
		intervalPanelConstraints.gridx = 0;
		intervalPanelConstraints.gridy = 0;
		intervalPanelConstraints.gridwidth = 5;
		this.intervalPanel.add(intervalBox, intervalPanelConstraints);

		// interval panel
		intervalPanelConstraints.gridwidth = 1;
		intervalPanelConstraints.insets = new Insets(0, 0, 0, 0);

		JLabel openInterval = new JLabel("[");
		openInterval.setFont(this.defaultFont);
		openInterval.setPreferredSize(new Dimension(8, 22));
		intervalPanelConstraints.gridx = 0;
		intervalPanelConstraints.gridy = 1;
		this.intervalPanel.add(openInterval, intervalPanelConstraints);

		this.lowerBound = new JTextField("0");
		this.lowerBound.setFont(this.defaultFont);
		this.lowerBound.setPreferredSize(new Dimension(50, 22));
		this.lowerBound.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				if (lowerBound.isEditable()) {
					double lowerbound = Double.parseDouble(lowerBound.getText());
					double upperbound = Double.parseDouble(upperBound.getText());

					xAxis.setRangePolicy(new RangePolicyFixedViewport());
					xAxis.setRange(new Range(lowerbound, upperbound));
					minShownTimestamp = (long) Math.floor(lowerbound);
					maxShownTimestamp = (long) Math.floor(upperbound);
					updateXTicks();
					thisM.grabFocus();
				}

			}
		});
		intervalPanelConstraints.gridx = 1;
		intervalPanelConstraints.gridy = 1;
		this.intervalPanel.add(this.lowerBound, intervalPanelConstraints);

		JLabel points = new JLabel(" : ");
		points.setFont(this.defaultFont);
		points.setPreferredSize(new Dimension(10, 22));
		intervalPanelConstraints.gridx = 2;
		intervalPanelConstraints.gridy = 1;
		this.intervalPanel.add(points, intervalPanelConstraints);

		this.upperBound = new JTextField("10");
		this.upperBound.setFont(this.defaultFont);
		this.upperBound.setPreferredSize(new Dimension(50, 22));

		this.upperBound.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				if (upperBound.isEditable()) {
					double lowerbound = Double.parseDouble(lowerBound.getText());
					double upperbound = Double.parseDouble(upperBound.getText());

					xAxis.setRangePolicy(new RangePolicyFixedViewport());
					xAxis.setRange(new Range(lowerbound, upperbound));
					minShownTimestamp = (long) Math.floor(lowerbound);
					maxShownTimestamp = (long) Math.floor(upperbound);
					updateXTicks();
					thisM.grabFocus();
				}
			}
		});

		intervalPanelConstraints.gridx = 3;
		intervalPanelConstraints.gridy = 1;
		this.intervalPanel.add(this.upperBound, intervalPanelConstraints);

		JLabel closeInterval = new JLabel("]");
		closeInterval.setFont(this.defaultFont);
		closeInterval.setPreferredSize(new Dimension(8, 22));
		intervalPanelConstraints.gridx = 4;
		intervalPanelConstraints.gridy = 1;
		this.intervalPanel.add(closeInterval, intervalPanelConstraints);

		this.lowerBound.setEditable(false);
		this.upperBound.setEditable(false);

		/*
		 * x axis options panel
		 */
		this.xOptionsPanel = new JPanel();
		this.xOptionsPanel.setLayout(new GridBagLayout());
		this.xOptionsPanel.setPreferredSize(new Dimension(65, 45));
		this.xOptionsPanel.setBorder(menuBarItemBorder);
		menuBarConstraints.gridy = 0;
		menuBarConstraints.gridx = 2;
		this.menuBar.add(this.xOptionsPanel, menuBarConstraints);

		GridBagConstraints xAxisOptionsPanelConstraints = new GridBagConstraints();

		// toggle x axis grid button
		this.toggleGridXButton = new JButton("+Grid x");
		this.toggleGridXButton.setFont(this.defaultFont);
		this.toggleGridXButton.setPreferredSize(new Dimension(60, 20));
		this.toggleGridXButton.setMargin(new Insets(0, 0, 0, 0));
		this.toggleGridXButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				if (toggleGridXButton.getText().equals("+Grid x"))
					toggleGridXButton.setText("-Grid x");
				else
					toggleGridXButton.setText("+Grid x");
				thisM.toggleXGrid();
			}
		});
		xAxisOptionsPanelConstraints.gridx = 0;
		xAxisOptionsPanelConstraints.gridy = 0;
		this.xOptionsPanel.add(toggleGridXButton, xAxisOptionsPanelConstraints);

		JPanel dummyP = new JPanel();
		dummyP.setPreferredSize(new Dimension(60, 20));
		xAxisOptionsPanelConstraints.gridx = 0;
		xAxisOptionsPanelConstraints.gridy = 1;
		this.xOptionsPanel.add(dummyP, xAxisOptionsPanelConstraints);

		/*
		 * y left axis options panel
		 */
		this.yLeftOptionsPanel = new JPanel();
		this.yLeftOptionsPanel.setLayout(new GridBagLayout());
		this.yLeftOptionsPanel.setPreferredSize(new Dimension(65, 45));
		this.yLeftOptionsPanel.setBorder(menuBarItemBorder);
		menuBarConstraints.gridy = 0;
		menuBarConstraints.gridx = 3;
		this.menuBar.add(this.yLeftOptionsPanel, menuBarConstraints);

		GridBagConstraints yLeftOptionsPanelConstraints = new GridBagConstraints();

		// toggle left y axis grid button
		this.toggleGridYLeftButton = new JButton("+Grid yL");
		this.toggleGridYLeftButton.setFont(this.defaultFont);
		this.toggleGridYLeftButton.setPreferredSize(new Dimension(60, 20));
		this.toggleGridYLeftButton.setMargin(new Insets(0, 0, 0, 0));
		this.toggleGridYLeftButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				if (toggleGridYLeftButton.getText().equals("+Grid yL"))
					toggleGridYLeftButton.setText("-Grid yL");
				else
					toggleGridYLeftButton.setText("+Grid yL");
				thisM.toggleYLeftGrid();
			}
		});
		yLeftOptionsPanelConstraints.gridx = 0;
		yLeftOptionsPanelConstraints.gridy = 0;
		this.yLeftOptionsPanel.add(this.toggleGridYLeftButton,
				yLeftOptionsPanelConstraints);

		// toggle left y axis log button
		this.toggleLogYLeftButton = new JButton("+log yL");
		this.toggleLogYLeftButton.setFont(this.defaultFont);
		this.toggleLogYLeftButton.setForeground(Color.GRAY);
		this.toggleLogYLeftButton.setPreferredSize(new Dimension(60, 20));
		this.toggleLogYLeftButton.setMargin(new Insets(0, 0, 0, 0));
		yLeftOptionsPanelConstraints.gridx = 0;
		yLeftOptionsPanelConstraints.gridy = 1;
		this.yLeftOptionsPanel.add(this.toggleLogYLeftButton,
				yLeftOptionsPanelConstraints);

		/*
		 * y right axis options panel
		 */
		this.yRightOptionsPanel = new JPanel();
		this.yRightOptionsPanel.setLayout(new GridBagLayout());
		this.yRightOptionsPanel.setPreferredSize(new Dimension(65, 45));
		this.yRightOptionsPanel.setBorder(menuBarItemBorder);
		menuBarConstraints.gridy = 0;
		menuBarConstraints.gridx = 4;
		this.menuBar.add(this.yRightOptionsPanel, menuBarConstraints);

		GridBagConstraints yRightOptionsPanelConstraints = new GridBagConstraints();

		// toggle right y axis grid button
		this.toggleGridYRightButton = new JButton("+Grid yR");
		this.toggleGridYRightButton.setFont(this.defaultFont);
		this.toggleGridYRightButton.setPreferredSize(new Dimension(60, 20));
		this.toggleGridYRightButton.setMargin(new Insets(0, 0, 0, 0));
		this.toggleGridYRightButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				if (toggleGridYRightButton.getText().equals("+Grid yR"))
					toggleGridYRightButton.setText("-Grid yR");
				else
					toggleGridYRightButton.setText("+Grid yR");
				thisM.toggleYRightGrid();
			}
		});
		yRightOptionsPanelConstraints.gridx = 0;
		yRightOptionsPanelConstraints.gridy = 0;
		this.yRightOptionsPanel.add(this.toggleGridYRightButton,
				yRightOptionsPanelConstraints);

		// toggle right y log grid button
		this.toggleLogYRightButton = new JButton("+log yR");
		this.toggleLogYRightButton.setFont(this.defaultFont);
		this.toggleLogYRightButton.setForeground(Color.GRAY);
		this.toggleLogYRightButton.setPreferredSize(new Dimension(60, 20));
		this.toggleLogYRightButton.setMargin(new Insets(0, 0, 0, 0));
		yRightOptionsPanelConstraints.gridx = 0;
		yRightOptionsPanelConstraints.gridy = 1;
		this.yRightOptionsPanel.add(this.toggleLogYRightButton,
				yRightOptionsPanelConstraints);

		/*
		 * dummy panel for menu bar
		 */
		JPanel dummyP2 = new JPanel();
		dummyP2.setPreferredSize(new Dimension(165, 45));
		menuBarConstraints.gridy = 0;
		menuBarConstraints.gridx = 5;
		this.menuBar.add(dummyP2, menuBarConstraints);

		// general settings
		this.xAxis.setMajorTickSpacing(1.0);
		this.xAxis.setStartMajorTick(true);

		AxisScalePolicyManualTicks manualTickScalePolicy = new AxisScalePolicyManualTicks();
		this.xAxis.setAxisScalePolicy(manualTickScalePolicy);

		this.chart.addMouseMotionListener(new MouseMotionListener() {
			@Override
			public void mouseMoved(MouseEvent e) {
				if (chart.getPointFinder().getNearestPoint(e, chart) != null) {
					ITracePoint2D tempPointFinder = chart.getPointFinder()
							.getNearestPoint(e, chart);
					xCoordsValue.setText(""
							+ (int) Math.floor(tempPointFinder.getX()));
					yCoordsValue.setText("" + tempPointFinder.getY());
				}
			}

			public void mouseDragged(MouseEvent e) {
			}

		});

	}

	/** called by the interval combobox to update the interval **/
	private void selectInterval(int selectionIndex) {
		String m = "";
		try {
			m = thisM.intervalOptions[selectionIndex];
		} catch (IndexOutOfBoundsException e) {
			e.printStackTrace();
		}
		if (!m.equals("")) {
			if (m.equals("- show all")) {
				this.TRACE_MODE_LTD = false;
				this.lowerBound.setEditable(false);
				this.upperBound.setEditable(false);
				this.xAxis.setRangePolicy(new RangePolicyUnbounded());
				this.maxShownTimestamp = this.maxTimestamp;
				updateXTicks();
			}
			if (m.equals("- fixed interval")) {
				this.TRACE_MODE_LTD = false;
				this.lowerBound.setEditable(true);
				this.upperBound.setEditable(true);
			}
			if (m.substring(0, 2).equals("-f")) {
				this.TRACE_MODE_LTD = true;
				this.lowerBound.setEditable(false);
				this.upperBound.setEditable(false);
				this.TRACE_LENGTH = Integer.parseInt(m.substring(15));
				// update trace length
				// this.updateTraceLength(Integer.parseInt(m.substring(15)));
				this.xAxis.setRangePolicy(new RangePolicyFixedViewport());
				this.maxShownTimestamp = this.maxTimestamp;
				if (this.maxShownTimestamp - this.TRACE_LENGTH > 0) {
					this.minShownTimestamp = this.maxShownTimestamp
							- this.TRACE_LENGTH;
				} else {
					this.minShownTimestamp = 0;
				}
				this.xAxis.setRange(new Range(this.minShownTimestamp,
						this.maxShownTimestamp));
				this.updateXTicks();
			}
		}
	}

	/** called by the selectInterval(..) method **/
	private void updateTraceLength(int traceLength) {
		this.TRACE_LENGTH = traceLength;
		for (ITrace2D trace : this.traces.values()) {
			if (trace instanceof Trace2DLtd) {
				((Trace2DLtd) trace).setMaxSize(traceLength);
			}
		}
	}

	/**
	 * Updates the chart and the legend with a new batchdata.
	 * 
	 * @param b
	 *            New batch
	 */
	public void updateData(BatchData b) {
		long timestamp = b.getTimestamp();
		double timestampDouble = timestamp;

		if (timestamp < this.minTimestamp)
			this.minTimestamp = timestamp;
		if (timestamp > this.maxTimestamp)
			this.maxTimestamp = timestamp;

		for (String metric : b.getMetrics().getNames()) {
			for (String value : b.getMetrics().get(metric).getValues()
					.getNames()) {
				if (this.traces.containsKey(metric + "." + value)) {
					String tempName = metric + "." + value;
					double tempValue = b.getMetrics().get(metric).getValues()
							.get(value).getValue();
					this.traces.get(tempName).addPoint(timestampDouble,
							tempValue);
					this.legend.updateItem(tempName, tempValue);
				}
			}
		}
		for (String runtime : b.getGeneralRuntimes().getNames()) {
			if (this.traces.containsKey("general runtimes." + runtime)) {
				String tempName = "general runtimes." + runtime;
				double tempValue = b.getGeneralRuntimes().get(runtime)
						.getRuntime();

				this.traces.get(tempName).addPoint(timestampDouble, tempValue);
				this.legend.updateItem(tempName, tempValue);
			}
		}
		for (String runtime : b.getMetricRuntimes().getNames()) {
			if (this.traces.containsKey("metric runtimes." + runtime)) {
				String tempName = "metric runtimes." + runtime;
				double tempValue = b.getMetricRuntimes().get(runtime)
						.getRuntime();

				this.traces.get(tempName).addPoint(timestampDouble, tempValue);
				this.legend.updateItem(tempName, tempValue);
			}
		}
		for (String value : b.getValues().getNames()) {
			if (this.traces.containsKey("statistics." + value)) {
				String tempName = "statistics." + value;
				double tempValue = b.getValues().get(value).getValue();

				this.traces.get(tempName).addPoint(timestampDouble, tempValue);
				this.legend.updateItem(tempName, tempValue);
			}
		}

		if (this.TRACE_MODE_LTD) {
			this.maxShownTimestamp = this.maxTimestamp;
			if (this.maxShownTimestamp - this.TRACE_LENGTH > 0)
				this.minShownTimestamp = this.maxShownTimestamp
						- this.TRACE_LENGTH;
			else
				this.minShownTimestamp = 0;
			this.xAxis.setRange(new Range(this.minShownTimestamp,
					this.maxShownTimestamp));
		}
		this.updateXTicks();
	}

	/** adds trace to the visualizer with default trace length **/
	public void addTrace(String name, Color color) {
		if (!this.traces.containsKey(name)) {
			if (this.TRACE_MODE_LTD) {
				Trace2DSimple newTrace = new Trace2DSimple();
				// Trace2DLtd newTrace = new Trace2DLtd(this.TRACE_LENGTH);
				newTrace.setColor(color);
				this.traces.put(name, newTrace);
				this.chart.addTrace(newTrace);

				if (this.DEFAULT_PAINT_LINESPOINT)
					newTrace.addTracePainter(new TracePainterDisc(
							this.linespointSize));
				if (this.DEFAULT_PAINT_FILL)
					newTrace.addTracePainter(new TracePainterFill(this.chart));
			} else {
				Trace2DSimple newTrace = new Trace2DSimple();
				newTrace.setColor(color);
				this.traces.put(name, newTrace);
				this.chart.addTrace(newTrace);

				if (this.DEFAULT_PAINT_LINESPOINT)
					newTrace.addTracePainter(new TracePainterDisc(
							this.linespointSize));
				if (this.DEFAULT_PAINT_FILL)
					newTrace.addTracePainter(new TracePainterFill(this.chart));
			}
		}

		if (counter == 0)
			trace.addTracePainter(new TracePainterDisc());
	}

	/** sets linespoint plot for a trace **/
	public void setTraceLinesPoint(String name) {
		ITrace2D trace = this.traces.get(name);
		Set<ITracePainter<?>> painters = trace.getTracePainters();
		int counter = 0;
		for (ITracePainter<?> painter : painters) {
			if (painter instanceof TracePainterDisc) {
				counter++;
			}
		}

		if (counter == 0)
			trace.addTracePainter(new TracePainterDisc());
	}

	/** initializes the data with the first batch **/
	public void initData(BatchData b) {
		this.minTimestamp = b.getTimestamp();
		this.maxTimestamp = b.getTimestamp();
		this.minShownTimestamp = b.getTimestamp();
		this.maxShownTimestamp = thisM.minShownTimestamp + 10;

		for (String metric : b.getMetrics().getNames()) {
			for (String value : b.getMetrics().get(metric).getValues()
					.getNames()) {
				this.availableValues.add(metric + "." + value);
			}
		}
		for (String runtime : b.getGeneralRuntimes().getNames()) {
			// graphGeneration runtime will be ignored cause it is only present
			// in the initial batch
			if (!runtime.equals("graphGeneration")) {
				this.availableValues.add("general runtime." + runtime);
			}
		}
		for (String runtime : b.getMetricRuntimes().getNames()) {
			this.availableValues.add("metric runtime." + runtime);
		}
		for (String value : b.getValues().getNames()) {
			this.availableValues.add("statistics." + value);
		}

		// init addbox
		String[] tempValues = this.availableValues
				.toArray(new String[this.availableValues.size()]);
		tempValues = this.gatherValues(b);
		this.toggleYAxisVisibility();
		this.legend.updateAddBox(tempValues);
		this.validate();
	}

	/** resets the metric visualizer **/
	public void reset() {
		this.minShownTimestamp = 0;
		this.maxShownTimestamp = 10;
		for (String trace : this.traces.keySet()) {
			this.traces.get(trace).removeAllPoints();
		}
	}

	/** clears all list items in the legend **/
	public void clearList() {
		this.legend.reset();
	}

	public void removeTrace(String name) {
		if (this.traces.containsKey(name)) {
			this.chart.removeTrace(this.traces.get(name));
			this.traces.remove(name);
		}
		if (this.yRight.getTraces().size() < 1)
			this.yRight.setVisible(false);
	}

	/** gathers all plottable values from the batch **/
	public String[] gatherValues(BatchData b) {
		ArrayList<String> tempList = new ArrayList<String>();
		tempList.add("metrics");

		for (MetricData m : b.getMetrics().getList()) {
			if (m.getValues().size() > 0) {
				tempList.add("--- " + m.getName());
				for (Value v : m.getValues().getList()) {
					tempList.add("----- " + m.getName() + "." + v.getName());
				}
			}
		}

		tempList.add("general runtimes");
		for (RunTime r : b.getGeneralRuntimes().getList()) {
			// graphGeneration runtime will be ignored cause it is only present
			// in the initial batch
			if (!r.getName().equals("graphGeneration"))
				tempList.add("---" + r.getName());
		}

		tempList.add("metric runtimes");
		for (RunTime r : b.getMetricRuntimes().getList()) {
			tempList.add("---" + r.getName());
		}

		tempList.add("statistics");
		for (Value v : b.getValues().getList()) {
			tempList.add("---" + v.getName());
		}

		String[] tempValues = tempList.toArray(new String[tempList.size()]);
		return tempValues;
		// TODO: ADD SUPPORT FOR DISTRIBUTIONS
	}

	/** toggles the y axis for a trace identified by its name **/
	public void toggleYAxis(String name) {
		if (this.traces.containsKey(name)) {
			Boolean left = false;
			Boolean right = false;

			ITrace2D tempTrace = new Trace2DSimple(null);

			for (IAxis leftAxe : this.chart.getAxesYLeft()) {
				for (Object trace : leftAxe.getTraces()) {
					if (trace instanceof ITrace2D) {
						if (((ITrace2D) trace) == this.traces.get(name)) {
							tempTrace = (ITrace2D) trace;
							leftAxe.removeTrace((ITrace2D) trace);
							left = true;
						}
					}
				}
			}

			for (IAxis rightAxe : this.chart.getAxesYRight()) {
				for (Object trace : rightAxe.getTraces()) {
					if (trace instanceof ITrace2D) {
						if (((ITrace2D) trace) == this.traces.get(name)) {
							tempTrace = (ITrace2D) trace;
							rightAxe.removeTrace((ITrace2D) trace);
							right = true;
						}
					}
				}
			}

			if (left) {
				for (IAxis rightAxe : this.chart.getAxesYRight()) {
					rightAxe.addTrace(tempTrace);
				}
			} else {
				if (right) {
					for (IAxis leftAxe : this.chart.getAxesYLeft()) {
						leftAxe.addTrace(tempTrace);
					}
				}

			}

			// toggle right y axis visibility
			this.toggleYAxisVisibility();
		}
	}

	/** toggle right y axis visibility **/
	public void toggleYAxisVisibility() {
		for (IAxis rightAxe : this.chart.getAxesYRight()) {
			if (rightAxe.getTraces().size() < 1)
				rightAxe.setVisible(false);
			else
				rightAxe.setVisible(true);
		}
	}

	/** toggle right y axis logarithmic / linear mode **/
	public void toggleYRightMode() {

	}

	/** toggle left y axis logarithmic / linear mode **/
	public void toggleYLeftMode() {

	}

	/** toggles grid on left y axis **/
	private void toggleYLeftGrid() {
		if (this.yLeft.isPaintGrid())
			this.yLeft.setPaintGrid(false);
		else
			this.yLeft.setPaintGrid(true);
	}

	/** toggles grid on right y axis **/
	private void toggleYRightGrid() {
		if (this.yRight.isPaintGrid())
			this.yRight.setPaintGrid(false);
		else
			this.yRight.setPaintGrid(true);
	}

	/** toggles grid on x axis **/
	private void toggleXGrid() {
		if (this.xAxis.isPaintGrid())
			this.xAxis.setPaintGrid(false);
		else
			this.xAxis.setPaintGrid(true);
	}

	/** handles the ticks that are shown on the x axis **/
	private void updateXTicks() {
		double minTemp = 0;
		double maxTemp = 10;

		if (this.xAxis.getRangePolicy() instanceof RangePolicyUnbounded) {
			minTemp = this.minTimestamp * 1.0;
			maxTemp = this.maxTimestamp * 1.0;
		} else {
			if (this.xAxis.getRangePolicy() instanceof RangePolicyFixedViewport) {
				minTemp = this.minShownTimestamp;
				maxTemp = this.maxShownTimestamp;
			}
		}
		if (maxTemp > minTemp) {
			double range = maxTemp - minTemp;
			if (range > 0) {
				double tickSpacingNew = Math.floor(range / 10);
				if (tickSpacingNew < 1)
					tickSpacingNew = 1.0;
				this.xAxis.setMajorTickSpacing(tickSpacingNew);
				this.xAxis.setMinorTickSpacing(tickSpacingNew);
			}
		}
	}
}
