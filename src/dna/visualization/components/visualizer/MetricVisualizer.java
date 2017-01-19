package dna.visualization.components.visualizer;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.BorderFactory;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import dna.series.data.BatchData;
import dna.series.data.MetricData;
import dna.series.data.RunTime;
import dna.series.data.Value;
import dna.util.Config;
import dna.visualization.MainDisplay;
import dna.visualization.config.ConfigItem;
import dna.visualization.config.MetricVisualizerItem;
import dna.visualization.config.VisualizerListConfig;
import dna.visualization.config.components.MetricVisualizerConfig;
import info.monitorenter.gui.chart.IAxis;
import info.monitorenter.gui.chart.IAxis.AxisTitle;
import info.monitorenter.gui.chart.ITrace2D;
import info.monitorenter.gui.chart.ITracePainter;
import info.monitorenter.gui.chart.ITracePoint2D;
import info.monitorenter.gui.chart.axis.scalepolicy.AxisScalePolicyAutomaticBestFit;
import info.monitorenter.gui.chart.labelformatters.LabelFormatterDate;
import info.monitorenter.gui.chart.rangepolicies.RangePolicyFixedViewport;
import info.monitorenter.gui.chart.rangepolicies.RangePolicyUnbounded;
import info.monitorenter.gui.chart.traces.Trace2DLtd;
import info.monitorenter.gui.chart.traces.Trace2DSimple;
import info.monitorenter.gui.chart.traces.painters.TracePainterDisc;
import info.monitorenter.gui.chart.traces.painters.TracePainterFill;
import info.monitorenter.gui.chart.traces.painters.TracePainterLine;
import info.monitorenter.gui.chart.traces.painters.TracePainterPolyline;
import info.monitorenter.gui.chart.traces.painters.TracePainterVerticalBar;
import info.monitorenter.util.Range;

@SuppressWarnings("serial")
public class MetricVisualizer extends Visualizer {
	// available values and traces
	private ArrayList<String> availableValues;
	private HashMap<String, ITrace2D> traces;

	private LinkedList<BatchData> batchBuffer;
	private BatchData initBatch;
	private int bufferSize;
	private boolean reload;

	private boolean xAxisTypeTimestamp;
	private long currentTimestamp;
	private double xAxisOffset;

	// config
	private VisualizerListConfig listConfig;
	MainDisplay mainDisplay;
	protected MetricVisualizerConfig config;

	// static strings
	private static String metricRuntimesPrefix = "metric runtimes.";
	private static String generalRuntimesPrefix = "general runtimes.";
	private static String statisticsPrefix = "statistics.";

	// constructor
	public MetricVisualizer(MainDisplay mainDisplay, MetricVisualizerConfig config) {
		// initialization
		super(config.getChartSize(), config.getLegendSize());

		this.x1Connected = config.getMenuBarConfig().isX1AxisConnected();
		this.mainDisplay = mainDisplay;
		this.traces = new HashMap<String, ITrace2D>();
		this.availableValues = new ArrayList<String>();
		this.listConfig = config.getListConfig();
		this.bufferSize = config.getTraceLength();
		this.TRACE_LENGTH = config.getTraceLength();
		this.config = config;

		// batch buffer
		this.batchBuffer = new LinkedList<BatchData>();
		this.reload = false;

		// set title and border of the metric visualizer
		TitledBorder title = BorderFactory.createTitledBorder(config.getName());
		title.setBorder(BorderFactory.createEtchedBorder((EtchedBorder.LOWERED)));
		title.setTitleFont(new Font(this.mainDisplay.getDefaultFont().getName(), Font.BOLD,
				this.mainDisplay.getDefaultFont().getSize()));
		title.setTitleColor(this.mainDisplay.getDefaultFontColor());
		this.setBorder(title);

		// if x axis type is date
		this.xAxisTypeTimestamp = true;
		if (config.getxAxisType().equals("date")) {
			this.xAxisTypeTimestamp = false;
			this.xAxis1.setFormatter(new LabelFormatterDate(new SimpleDateFormat(config.getxAxisFormat())));
			this.xAxis1.setMajorTickSpacing(5);
			this.xAxis1.setMinorTickSpacing(1);
			this.xAxis1.setAxisScalePolicy(new AxisScalePolicyAutomaticBestFit());
		}

		this.xAxis1.setRangePolicy(new RangePolicyFixedViewport(new Range(0, 1)));

		// add menu bar
		super.addMenuBar(config.getMenuBarConfig());

		// add coordinate parsing to mouseover on chart
		this.chart.addMouseMotionListener(new MouseMotionListener() {
			@Override
			public void mouseMoved(MouseEvent e) {
				if (chart.getPointFinder().getNearestPoint(e, chart) != null) {
					ITracePoint2D tempPointFinder = chart.getPointFinder().getNearestPoint(e, chart);
					menuBar.updateCoordsPanel(tempPointFinder.getX(), tempPointFinder.getY());
				}
			}

			public void mouseDragged(MouseEvent e) {
			}
		});

		// apply config
		this.chart.setPreferredSize(config.getChartSize());
		this.legend.setLegendSize(config.getLegendSize());
		this.xAxisOffset = config.getxAxisOffset();
		this.xAxis1.setAxisTitle(new AxisTitle(config.getx1AxisTitle()));
		this.yAxis1.setAxisTitle(new AxisTitle(config.getY1AxisTitle()));
		this.yAxis2.setAxisTitle(new AxisTitle(config.getY2AxisTitle()));

		this.menuBar.setVisible(config.getMenuBarConfig().isVisible());
	}

	/** handles the ticks that are shown on the x axis **/
	@Override
	protected void updateX1Ticks() {
		if (this.xAxisTypeTimestamp) {
			double minTemp = 0;
			double maxTemp = 10;
			if (this.xAxis1.getRangePolicy() instanceof RangePolicyUnbounded) {
				minTemp = this.minTimestamp * 1.0;
				maxTemp = this.maxTimestamp * 1.0;
			} else {
				if (this.xAxis1.getRangePolicy() instanceof RangePolicyFixedViewport) {
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
					this.xAxis1.setMajorTickSpacing(tickSpacingNew);
					this.xAxis1.setMinorTickSpacing(tickSpacingNew);
				}
			}
		}
	}

	/**
	 * Updates the chart and the legend with a new batch.
	 * 
	 * @param b
	 *            New batch
	 */
	public void updateData(BatchData b) {
		long timestamp = b.getTimestamp();

		if (Config.getBoolean("VISUALIZATION_TIMESTAMP_AS_SECOND")) {
			timestamp = (timestamp + Config.getInt("VISUALIZATION_TIMESTAMP_OFFSET")) * 1000;
		}

		double timestampDouble = timestamp;

		// check if new batch is before last one which means time slided
		// backwards
		if (timestamp < this.currentTimestamp) {
			while (this.batchBuffer.size() > 0) {
				if (this.batchBuffer.getLast().getTimestamp() <= b.getTimestamp()) {
					break;
				} else {
					this.batchBuffer.removeLast();
				}
			}
			// reload data
			this.reloadData();
			this.updateData(b);
		} else {
			// if reload flag is set, dont add batch to buffer
			if (!this.reload) {
				// if buffer sizer is reached, remove batches until its
				if (this.batchBuffer.size() >= this.bufferSize)
					while (!(this.batchBuffer.size() < this.bufferSize))
						this.batchBuffer.removeFirst();
				// add new batch as last batch to list
				this.batchBuffer.addLast(b);
			}

			this.currentTimestamp = timestamp;

			if (timestamp < this.minTimestamp)
				this.minTimestamp = timestamp;
			if (timestamp > this.maxTimestamp)
				this.maxTimestamp = timestamp;

			double offsetX = 0;

			// update values
			for (String metric : b.getMetrics().getNames()) {
				for (String value : b.getMetrics().get(metric).getValues().getNames()) {
					if (this.traces.containsKey(metric + "." + value)) {
						String tempName = metric + "." + value;
						double tempValue = b.getMetrics().get(metric).getValues().get(value).getValue();
						this.traces.get(tempName).addPoint(timestampDouble + offsetX, tempValue);
						offsetX += this.xAxisOffset;
						this.legend.updateItem(tempName, tempValue);
					}
				}
			}
			// update general runtimes
			for (String runtime : b.getGeneralRuntimes().getNames()) {
				if (this.traces.containsKey(MetricVisualizer.generalRuntimesPrefix + runtime)) {
					String tempName = MetricVisualizer.generalRuntimesPrefix + runtime;
					double tempValue = b.getGeneralRuntimes().get(runtime).getRuntime();
					this.traces.get(tempName).addPoint(timestampDouble + offsetX, tempValue);
					offsetX += this.xAxisOffset;
					this.legend.updateItem(tempName, tempValue);
				}
			}
			// update metric runtimes
			for (String runtime : b.getMetricRuntimes().getNames()) {
				if (this.traces.containsKey(MetricVisualizer.metricRuntimesPrefix + runtime)) {
					String tempName = MetricVisualizer.metricRuntimesPrefix + runtime;
					double tempValue = b.getMetricRuntimes().get(runtime).getRuntime();

					this.traces.get(tempName).addPoint(timestampDouble + offsetX, tempValue);
					offsetX += this.xAxisOffset;
					this.legend.updateItem(tempName, tempValue);
				}
			}
			// update statistics
			for (String value : b.getValues().getNames()) {
				if (this.traces.containsKey(MetricVisualizer.statisticsPrefix + value)) {
					String tempName = MetricVisualizer.statisticsPrefix + value;
					double tempValue = b.getValues().get(value).getValue();

					this.traces.get(tempName).addPoint(timestampDouble + offsetX, tempValue);
					offsetX += this.xAxisOffset;
					this.legend.updateItem(tempName, tempValue);
				}
			}

			if (this.FIXED_VIEWPORT) {
				this.xAxis1.setRange(new Range(minTimestamp, maxTimestamp));
			} else {
				this.setXAxis1RangeByIntervalSelection();
			}

			// update chart axis ticks
			this.updateTicks();
		}
		this.validate();
	}

	/** reloads all data included in the batchBuffer **/
	private void reloadData() {
		// set reload flag
		this.reload = true;

		// reset current timestamp
		this.currentTimestamp = 0;

		// clear chart
		for (ITrace2D t : this.chart.getTraces()) {
			t.removeAllPoints();
		}

		// update with old batches
		Iterator<BatchData> iterator = this.batchBuffer.iterator();
		while (iterator.hasNext()) {
			this.updateData(iterator.next());
		}

		// unset reload flag
		this.reload = false;
	}

	/**
	 * Reloads a trace from buffered data. Note: Only use during pause because
	 * of race conditions!
	 * 
	 * @param name
	 *            Name of the trace
	 */
	private void reloadTrace(String name) {
		Iterator<BatchData> iterator = this.batchBuffer.iterator();
		while (iterator.hasNext()) {
			this.updateTrace(iterator.next(), name);
		}
	}

	/** updates only one specific trace **/
	private void updateTrace(BatchData b, String name) {
		long timestamp = b.getTimestamp();
		double timestampDouble = timestamp;

		if (Config.getBoolean("VISUALIZATION_TIMESTAMP_AS_SECOND")) {
			timestampDouble = (timestampDouble + Config.getInt("VISUALIZATION_TIMESTAMP_OFFSET")) * 1000;
		}

		// update values
		for (String metric : b.getMetrics().getNames()) {
			for (String value : b.getMetrics().get(metric).getValues().getNames()) {
				if (this.traces.containsKey(metric + "." + value)) {
					String tempName = metric + "." + value;
					if (tempName.equals(name)) {
						double tempValue = b.getMetrics().get(metric).getValues().get(value).getValue();
						this.traces.get(tempName).addPoint(timestampDouble, tempValue);
						this.legend.updateItem(tempName, tempValue);
					}
				}
			}
		}
		// update general runtimes
		for (String runtime : b.getGeneralRuntimes().getNames()) {
			if (this.traces.containsKey(MetricVisualizer.generalRuntimesPrefix + runtime)) {
				String tempName = MetricVisualizer.generalRuntimesPrefix + runtime;
				if (tempName.equals(name)) {
					double tempValue = b.getGeneralRuntimes().get(runtime).getRuntime();
					this.traces.get(tempName).addPoint(timestampDouble, tempValue);
					this.legend.updateItem(tempName, tempValue);
				}
			}
		}
		// update metric runtimes
		for (String runtime : b.getMetricRuntimes().getNames()) {
			if (this.traces.containsKey(MetricVisualizer.metricRuntimesPrefix + runtime)) {
				String tempName = MetricVisualizer.metricRuntimesPrefix + runtime;
				double tempValue = b.getMetricRuntimes().get(runtime).getRuntime();
				if (tempName.equals(name)) {
					this.traces.get(tempName).addPoint(timestampDouble, tempValue);
					this.legend.updateItem(tempName, tempValue);
				}
			}
		}
		// update statistics
		for (String value : b.getValues().getNames()) {
			if (this.traces.containsKey(MetricVisualizer.statisticsPrefix + value)) {
				String tempName = MetricVisualizer.statisticsPrefix + value;
				if (tempName.equals(name)) {
					double tempValue = b.getValues().get(value).getValue();
					this.traces.get(tempName).addPoint(timestampDouble, tempValue);
					this.legend.updateItem(tempName, tempValue);
				}
			}
		}
	}

	/** adds trace to the visualizer with default trace length **/
	public void addTrace(String name, Color color) {
		if (!this.traces.containsKey(name)) {
			if (config.isTraceModeLtd()) {
				Trace2DLtd newTrace = new Trace2DLtd(this.TRACE_LENGTH);
				newTrace.setColor(color);
				this.traces.put(name, newTrace);
				this.chart.addTrace(newTrace);
				if (config.isPaintLinesPoint())
					newTrace.addTracePainter(new TracePainterDisc(config.getLinesPointSize()));
				if (config.isPaintFill())
					newTrace.addTracePainter(new TracePainterFill(this.chart));
			} else {
				Trace2DSimple newTrace = new Trace2DSimple();
				newTrace.setColor(color);
				this.traces.put(name, newTrace);
				this.chart.addTrace(newTrace);

				if (config.isPaintLinesPoint())
					newTrace.addTracePainter(new TracePainterDisc(config.getLinesPointSize()));
				if (config.isPaintFill())
					newTrace.addTracePainter(new TracePainterFill(this.chart));
			}
		}
	}

	/** adds trace to the visualizer with default trace length **/
	public void addTrace(String name, Color color, boolean verticalBar) {
		if (!this.traces.containsKey(name)) {
			if (config.isTraceModeLtd()) {
				Trace2DLtd newTrace = new Trace2DLtd(this.TRACE_LENGTH);
				newTrace.setColor(color);
				this.traces.put(name, newTrace);
				this.chart.addTrace(newTrace);

				if (verticalBar) {
					for (ITracePainter painter : newTrace.getTracePainters()) {
						if (painter instanceof TracePainterPolyline)
							newTrace.removeTracePainter(painter);
					}
					newTrace.addTracePainter(new TracePainterVerticalBar(config.getVerticalBarSize(), this.chart));
				} else {
					if (config.isPaintLinesPoint())
						newTrace.addTracePainter(new TracePainterDisc(config.getLinesPointSize()));

					if (config.isPaintFill())
						newTrace.addTracePainter(new TracePainterFill(this.chart));
				}
			} else {
				Trace2DSimple newTrace = new Trace2DSimple();
				newTrace.setColor(color);
				this.traces.put(name, newTrace);
				this.chart.addTrace(newTrace);

				if (config.isPaintLinesPoint())
					newTrace.addTracePainter(new TracePainterDisc(config.getLinesPointSize()));
				if (config.isPaintFill())
					newTrace.addTracePainter(new TracePainterFill(this.chart));
			}
		}
	}

	/** initializes the data with the first batch **/
	public void initData(BatchData b) {
		long timestamp = b.getTimestamp();

		if (Config.getBoolean("VISUALIZATION_TIMESTAMP_AS_SECOND")) {
			timestamp = (timestamp + Config.getInt("VISUALIZATION_TIMESTAMP_OFFSET")) * 1000;
		}

		this.initBatch = b;
		this.minTimestamp = timestamp;
		this.maxTimestamp = timestamp;
		this.minShownTimestamp = timestamp;
		this.maxShownTimestamp = this.minShownTimestamp;

		this.currentTimestamp = timestamp;

		// clear chart
		for (ITrace2D t : this.chart.getTraces()) {
			t.removeAllPoints();
		}

		// gather all available values
		for (String metric : b.getMetrics().getNames()) {
			for (String value : b.getMetrics().get(metric).getValues().getNames()) {
				this.availableValues.add(metric + "." + value);
			}
		}
		for (String runtime : b.getGeneralRuntimes().getNames()) {
			// graphGeneration runtime will be ignored cause it is only present
			// in the initial batch
			if (!runtime.equals("graphGeneration")) {
				this.availableValues.add(MetricVisualizer.generalRuntimesPrefix + runtime);
			}
		}
		for (String runtime : b.getMetricRuntimes().getNames()) {
			this.availableValues.add(MetricVisualizer.metricRuntimesPrefix + runtime);
		}
		for (String value : b.getValues().getNames()) {
			this.availableValues.add(MetricVisualizer.statisticsPrefix + value);
		}

		// init addbox
		String[] tempValues = this.gatherValues(b);
		this.legend.updateAddBox(tempValues);

		// load config
		if (this.listConfig != null && !super.locked)
			this.loadConfig(this.listConfig);

		// toggle visibility and validate
		this.toggleYAxisVisibility();
		this.validate();
	}

	/** resets the metric visualizer **/
	public void reset() {
		this.minShownTimestamp = 0;
		this.maxShownTimestamp = 10;
		for (String trace : this.traces.keySet()) {
			this.traces.get(trace).removeAllPoints();
		}
		this.batchBuffer.clear();
		this.availableValues.clear();
		this.chart.updateUI();
	}

	/** shows/hides a trace from the chart without deleting it **/
	public void toggleTraceVisiblity(String name) {
		if (this.traces.containsKey(name)) {
			if (this.traces.get(name).isVisible())
				this.traces.get(name).setVisible(false);
			else
				this.traces.get(name).setVisible(true);
		}
		this.chart.setRequestedRepaint(true);
	}

	/** toggles the display mode of a trace **/
	public void toggleDisplayMode(String name) {
		if (this.traces.containsKey(name)) {
			ITrace2D trace = this.traces.get(name);
			boolean verticalBar = false;
			for (ITracePainter painter : trace.getTracePainters()) {
				if (painter instanceof TracePainterVerticalBar)
					verticalBar = true;
			}
			if (verticalBar) {
				trace.setTracePainter(new TracePainterDisc(config.getLinesPointSize()));
				trace.addTracePainter(new TracePainterLine());
			} else {
				trace.setTracePainter(new TracePainterVerticalBar(config.getVerticalBarSize(), this.chart));
			}
		}
	}

	/** removes a trace from the chart and the traces-list **/
	public void removeTrace(String name) {
		if (this.traces.containsKey(name)) {
			this.chart.removeTrace(this.traces.get(name));
			this.traces.remove(name);
		}
		this.toggleXAxisVisibility();
		this.toggleYAxisVisibility();
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
	}

	/** toggles the y axis for a trace identified by its name **/
	@SuppressWarnings("rawtypes")
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

	/** loads a config for displayed values etc. **/
	public void loadConfig(VisualizerListConfig config) {
		// check orderIds
		ArrayList<MetricVisualizerItem> configsList = new ArrayList<MetricVisualizerItem>();

		// add single configs to lists
		for (ConfigItem c : config.getEntries()) {
			if (c instanceof MetricVisualizerItem) {
				if (this.availableValues.contains(c.getName()) && c.getOrderId() >= -1) {
					configsList.add((MetricVisualizerItem) c);
				}
			}
		}

		// add general configs to list
		MetricVisualizer.addGeneralConfigs(configsList, config);

		// craft names list to use as a blacklist for general configs later
		ArrayList<String> configsNamesList = new ArrayList<String>(configsList.size());
		for (MetricVisualizerItem c : configsList)
			configsNamesList.add(c.getName());

		// convert list to array
		MetricVisualizerItem[] configsArray = configsList.toArray(new MetricVisualizerItem[configsList.size()]);

		// sort array with insertion sort
		for (int i = 1; i < configsArray.length; i++) {
			MetricVisualizerItem single = configsArray[i];
			int j = i;
			while (j > 0 && configsArray[j - 1].getOrderId() > single.getOrderId()) {
				configsArray[j] = configsArray[j - 1];
				j--;
			}
			configsArray[j] = single;
		}

		// add according to right order
		BatchData b = this.initBatch;

		// calculate "breakpoint" in sorted list: where does -1 end?
		int breakpoint = 0;
		for (int i = 0; i < configsArray.length && configsArray[i].getOrderId() < 0; i++)
			breakpoint = i + 1;

		// first insert items with id > -1, then those with -1
		this.insertForId(breakpoint, configsArray.length, b, configsNamesList, configsArray, config);
		this.insertForId(0, breakpoint, b, configsNamesList, configsArray, config);
	}

	/** Inserts items from the configs array to the legend. **/
	private void insertForId(int from, int to, BatchData b, ArrayList<String> configsNamesList,
			MetricVisualizerItem[] configsArray, VisualizerListConfig config) {
		for (int i = from; i < to; i++) {
			MetricVisualizerItem item = configsArray[i];

			// if orderId < -1, skip
			if (item.getOrderId() < -1)
				continue;

			switch (item.getName()) {
			case (VisualizerListConfig.generalGeneralRuntimesConfigName):
				this.insertGeneralRuntimes(b, config, configsNamesList);
				break;
			case (VisualizerListConfig.generalMetricRuntimesConfigName):
				this.insertMetricRuntimes(b, config, configsNamesList);
				break;
			case (VisualizerListConfig.generalStatisticsConfigName):
				this.insertStatistics(b, config, configsNamesList);
				break;
			case (VisualizerListConfig.generalMetricsConfigName):
				this.insertMetrics(b, config, configsNamesList);
				break;

			default:
				if (this.availableValues.contains(item.getName()))
					this.legend.addValueItemToList(item);
				break;
			}
		}
	}

	/** Adds all set general configs to the list. **/
	private static void addGeneralConfigs(ArrayList<MetricVisualizerItem> configs, VisualizerListConfig config) {
		if (config.isAnyGeneralConfigSet()) {
			if (config.getAllGeneralRuntimesConfig() != null && config.getAllGeneralRuntimesConfig().getOrderId() >= -1)
				configs.add(config.getAllGeneralRuntimesConfig());

			if (config.getAllMetricRuntimesConfig() != null && config.getAllMetricRuntimesConfig().getOrderId() >= -1)
				configs.add(config.getAllMetricRuntimesConfig());

			if (config.getAllStatisticsConfig() != null && config.getAllStatisticsConfig().getOrderId() >= -1)
				configs.add(config.getAllStatisticsConfig());

			if (config.getAllMetricsConfig() != null && config.getAllMetricsConfig().getOrderId() >= -1)
				configs.add(config.getAllMetricsConfig());
		}
	}

	/** Insert all available metric runtimes. **/
	private void insertMetricRuntimes(BatchData b, VisualizerListConfig config, ArrayList<String> blackList) {
		// insert all available metric runtimes
		for (String runtime : b.getMetricRuntimes().getNames()) {
			if (!blackList.contains(MetricVisualizer.metricRuntimesPrefix + runtime)) {
				MetricVisualizerItem c = config.getAllMetricRuntimesConfig();
				this.legend.addValueItemToList(new MetricVisualizerItem(MetricVisualizer.metricRuntimesPrefix + runtime,
						c.getDisplayMode(), c.getYAxis(), c.getVisibility()));
			}
		}
	}

	/** Insert all available general runtimes. **/
	private void insertGeneralRuntimes(BatchData b, VisualizerListConfig config, ArrayList<String> blackList) {
		for (String runtime : b.getGeneralRuntimes().getNames()) {
			// graphGeneration runtime will be ignored cause it is only present
			// in the initial batch
			if (!runtime.equals("graphGeneration")
					&& !blackList.contains(MetricVisualizer.generalRuntimesPrefix + runtime)) {
				MetricVisualizerItem c = config.getAllGeneralRuntimesConfig();
				this.legend
						.addValueItemToList(new MetricVisualizerItem(MetricVisualizer.generalRuntimesPrefix + runtime,
								c.getDisplayMode(), c.getYAxis(), c.getVisibility()));
			}
		}
	}

	/** Insert all available statistics. **/
	private void insertStatistics(BatchData b, VisualizerListConfig config, ArrayList<String> blackList) {
		for (String value : b.getValues().getNames()) {
			if (!blackList.contains(MetricVisualizer.statisticsPrefix + value)) {
				MetricVisualizerItem c = config.getAllStatisticsConfig();
				this.legend.addValueItemToList(new MetricVisualizerItem(MetricVisualizer.statisticsPrefix + value,
						c.getDisplayMode(), c.getYAxis(), c.getVisibility()));
			}
		}
	}

	/** Insert all available metrics. **/
	private void insertMetrics(BatchData b, VisualizerListConfig config, ArrayList<String> blackList) {
		for (String metric : b.getMetrics().getNames()) {
			for (String value : b.getMetrics().get(metric).getValues().getNames()) {
				if (!blackList.contains(metric + "." + value)) {
					MetricVisualizerItem c = config.getAllMetricsConfig();
					this.legend.addValueItemToList(new MetricVisualizerItem(metric + "." + value, c.getDisplayMode(),
							c.getYAxis(), c.getVisibility()));
				}
			}
		}
	}

	@Override
	public void broadcastX1IntervalSizeSliderChange(int value) {
		if (this.isX1Connected())
			this.mainDisplay.broadcastX1SizeSliderChange(this, value);
	}

	@Override
	public void broadcastX1IntervalScrollBarChange(int value) {
		if (this.isX1Connected())
			this.mainDisplay.broadcastX1IntervalScrollBarChange(this, value);
	}

	@Override
	public void broadcastX1IntervalEnabled(boolean enabled) {
		if (this.isX1Connected())
			this.mainDisplay.broadcastX1IntervalEnabled(this, enabled);
	}

}
