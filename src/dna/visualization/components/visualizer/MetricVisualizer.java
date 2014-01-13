package dna.visualization.components.visualizer;

import info.monitorenter.gui.chart.IAxis;
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

import java.awt.Color;
import java.awt.Dimension;
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
import dna.visualization.GuiOptions;
import dna.visualization.config.Config1;
import dna.visualization.config.ConfigItem;
import dna.visualization.config.MetricVisualizerItem;
import dna.visualization.config.VisualizerListConfig;

@SuppressWarnings("serial")
public class MetricVisualizer extends Visualizer {
	// available values and traces
	private ArrayList<String> availableValues;
	private HashMap<String, ITrace2D> traces;

	private LinkedList<BatchData> batchBuffer;
	private int bufferSize = GuiOptions.metricVisualizerBatchBufferSize;
	private boolean reload;

	private boolean xAxisTypeTimestamp;
	private long currentTimestamp;
	
	// config
	VisualizerListConfig config;

	// constructor
	public MetricVisualizer(VisualizerListConfig config) {
		// initialization
		this.traces = new HashMap<String, ITrace2D>();
		this.availableValues = new ArrayList<String>();
		this.config = config;

		// batch buffer
		this.batchBuffer = new LinkedList<BatchData>();
		this.reload = false;

		// set title and border of the metric visualizer
		TitledBorder title = BorderFactory
				.createTitledBorder("Metric Visualizer");
		title.setBorder(BorderFactory
				.createEtchedBorder((EtchedBorder.LOWERED)));
		title.setTitleFont(GuiOptions.defaultFontBorders);
		title.setTitleColor(GuiOptions.defaultFontBordersColor);
		this.setBorder(title);

		// if x axis type is date
		this.xAxisTypeTimestamp = true;
		if (GuiOptions.metricVisualizerXAxisType.equals("date")) {
			this.xAxisTypeTimestamp = false;
			this.xAxis1
					.setFormatter(new LabelFormatterDate(new SimpleDateFormat(
							GuiOptions.metricVisualizerXAxisFormat)));
			this.xAxis1.setMajorTickSpacing(5);
			this.xAxis1.setMinorTickSpacing(1);
			this.xAxis1
					.setAxisScalePolicy(new AxisScalePolicyAutomaticBestFit());
		}
		// add menu bar
		super.addMenuBar(
				new Dimension(GuiOptions.visualizerDefaultMenuBarSize), true,
				true, true, true, true);

		// add coordinate parsing to mouseover on chart
		this.chart.addMouseMotionListener(new MouseMotionListener() {
			@Override
			public void mouseMoved(MouseEvent e) {
				if (chart.getPointFinder().getNearestPoint(e, chart) != null) {
					ITracePoint2D tempPointFinder = chart.getPointFinder()
							.getNearestPoint(e, chart);
					menuBar.updateCoordsPanel(
							(int) Math.floor(tempPointFinder.getX()),
							tempPointFinder.getY());
				}
			}

			public void mouseDragged(MouseEvent e) {
			}
		});
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
		double timestampDouble = timestamp;

		// check if new batch is before last one which means time slided
		// backwards
		if (timestamp < this.currentTimestamp) {
			while (this.batchBuffer.size() > 0) {
				if (this.batchBuffer.getLast().getTimestamp() <= b
						.getTimestamp()) {
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

			// update values
			for (String metric : b.getMetrics().getNames()) {
				for (String value : b.getMetrics().get(metric).getValues()
						.getNames()) {
					if (this.traces.containsKey(metric + "." + value)) {
						String tempName = metric + "." + value;
						double tempValue = b.getMetrics().get(metric)
								.getValues().get(value).getValue();

						this.traces.get(tempName).addPoint(timestampDouble,
								tempValue);
						this.legend.updateItem(tempName, tempValue);
					}
				}
			}
			// update general runtimes
			for (String runtime : b.getGeneralRuntimes().getNames()) {
				if (this.traces.containsKey("general runtimes." + runtime)) {
					String tempName = "general runtimes." + runtime;
					double tempValue = b.getGeneralRuntimes().get(runtime)
							.getRuntime();
					this.traces.get(tempName).addPoint(timestampDouble,
							tempValue);
					this.legend.updateItem(tempName, tempValue);
				}
			}
			// update metric runtimes
			for (String runtime : b.getMetricRuntimes().getNames()) {
				if (this.traces.containsKey("metric runtimes." + runtime)) {
					String tempName = "metric runtimes." + runtime;
					double tempValue = b.getMetricRuntimes().get(runtime)
							.getRuntime();

					this.traces.get(tempName).addPoint(timestampDouble,
							tempValue);
					this.legend.updateItem(tempName, tempValue);
				}
			}
			// update statistics
			for (String value : b.getValues().getNames()) {
				if (this.traces.containsKey("statistics." + value)) {
					String tempName = "statistics." + value;
					double tempValue = b.getValues().get(value).getValue();

					this.traces.get(tempName).addPoint(timestampDouble,
							tempValue);
					this.legend.updateItem(tempName, tempValue);
				}
			}
			// timestamp adjustmens for x-axis tick calculation
			if (Config.getBoolean("GUI_TRACE_MODE_LTD") && !this.FIXED_VIEWPORT) {
				this.maxShownTimestamp = this.maxTimestamp;
				if (this.maxShownTimestamp - this.TRACE_LENGTH > 0)
					this.minShownTimestamp = this.maxShownTimestamp
							- this.TRACE_LENGTH;
				else
					this.minShownTimestamp = 0;
				this.xAxis1.setRange(new Range(this.minShownTimestamp,
						this.maxShownTimestamp));
			} else {
				if (this.FIXED_VIEWPORT) {
					double lowP = 1.0 * this.menuBar.getIntervalSlider()
							.getValue() / 100;
					double highP = 1.0 * (this.menuBar.getIntervalSlider()
							.getValue() + this.menuBar.getIntervalSlider()
							.getModel().getExtent()) / 100;
					double minD = 0;
					double maxD = 0;

					for (String s : this.traces.keySet()) {
						minD = this.traces.get(s).getMinX();
						maxD = this.traces.get(s).getMaxX();
						if (this.traces.get(s).getMinX() < this.minTimestamp)
							minD = this.traces.get(s).getMinX();
						if (this.traces.get(s).getMaxX() > this.maxTimestamp)
							maxD = this.traces.get(s).getMaxX();
					}
					double tMinNew = minD + (lowP * (maxD - minD));
					double tMaxNew = minD + (highP * (maxD - minD));

					this.xAxis1.setRange(new Range(tMinNew, tMaxNew));
					this.setMinShownTimestamp((long) tMinNew);
					this.setMaxShownTimestamp((long) tMaxNew);
				}
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
		// update values
		for (String metric : b.getMetrics().getNames()) {
			for (String value : b.getMetrics().get(metric).getValues()
					.getNames()) {
				if (this.traces.containsKey(metric + "." + value)) {
					String tempName = metric + "." + value;
					if (tempName.equals(name)) {
						double tempValue = b.getMetrics().get(metric)
								.getValues().get(value).getValue();
						this.traces.get(tempName).addPoint(timestampDouble,
								tempValue);
						this.legend.updateItem(tempName, tempValue);
					}
				}
			}
		}
		// update general runtimes
		for (String runtime : b.getGeneralRuntimes().getNames()) {
			if (this.traces.containsKey("general runtimes." + runtime)) {
				String tempName = "general runtimes." + runtime;
				if (tempName.equals(name)) {
					double tempValue = b.getGeneralRuntimes().get(runtime)
							.getRuntime();
					this.traces.get(tempName).addPoint(timestampDouble,
							tempValue);
					this.legend.updateItem(tempName, tempValue);
				}
			}
		}
		// update metric runtimes
		for (String runtime : b.getMetricRuntimes().getNames()) {
			if (this.traces.containsKey("metric runtimes." + runtime)) {
				String tempName = "metric runtimes." + runtime;
				double tempValue = b.getMetricRuntimes().get(runtime)
						.getRuntime();
				if (tempName.equals(name)) {
					this.traces.get(tempName).addPoint(timestampDouble,
							tempValue);
					this.legend.updateItem(tempName, tempValue);
				}
			}
		}
		// update statistics
		for (String value : b.getValues().getNames()) {
			if (this.traces.containsKey("statistics." + value)) {
				String tempName = "statistics." + value;
				if (tempName.equals(name)) {
					double tempValue = b.getValues().get(value).getValue();
					this.traces.get(tempName).addPoint(timestampDouble,
							tempValue);
					this.legend.updateItem(tempName, tempValue);
				}
			}
		}
	}

	/** adds trace to the visualizer with default trace length **/
	public void addTrace(String name, Color color) {
		if (!this.traces.containsKey(name)) {
			if (Config.getBoolean("GUI_TRACE_MODE_LTD")) {
				Trace2DLtd newTrace = new Trace2DLtd(
						Config.getInt("GUI_TRACE_LENGTH"));
				newTrace.setColor(color);
				this.traces.put(name, newTrace);
				this.chart.addTrace(newTrace);

				if (Config.getBoolean("GUI_PAINT_LINESPOINT"))
					newTrace.addTracePainter(new TracePainterDisc(Config
							.getInt("GUI_LINESPOINT_SIZE")));
				if (Config.getBoolean("GUI_PAINT_FILL"))
					newTrace.addTracePainter(new TracePainterFill(this.chart));
			} else {
				Trace2DSimple newTrace = new Trace2DSimple();
				newTrace.setColor(color);
				this.traces.put(name, newTrace);
				this.chart.addTrace(newTrace);

				if (Config.getBoolean("GUI_PAINT_LINESPOINT"))
					newTrace.addTracePainter(new TracePainterDisc(Config
							.getInt("GUI_LINESPOINT_SIZE")));
				if (Config.getBoolean("GUI_PAINT_FILL"))
					newTrace.addTracePainter(new TracePainterFill(this.chart));
			}
		}
	}

	/** adds trace to the visualizer with default trace length **/
	public void addTrace(String name, Color color, boolean verticalBar) {
		if (!this.traces.containsKey(name)) {
			if (Config.getBoolean("GUI_TRACE_MODE_LTD")) {
				Trace2DLtd newTrace = new Trace2DLtd(
						Config.getInt("GUI_TRACE_LENGTH"));
				newTrace.setColor(color);
				this.traces.put(name, newTrace);
				this.chart.addTrace(newTrace);

				if (verticalBar) {
					for (ITracePainter painter : newTrace.getTracePainters()) {
						if (painter instanceof TracePainterPolyline)
							newTrace.removeTracePainter(painter);
					}
					newTrace.addTracePainter(new TracePainterVerticalBar(Config
							.getInt("GUI_VERTICALBAR_SIZE"), this.chart));
				} else {
					if (Config.getBoolean("GUI_PAINT_LINESPOINT"))
						newTrace.addTracePainter(new TracePainterDisc(Config
								.getInt("GUI_LINESPOINT_SIZE")));

					if (Config.getBoolean("GUI_PAINT_FILL"))
						newTrace.addTracePainter(new TracePainterFill(
								this.chart));
				}
			} else {
				Trace2DSimple newTrace = new Trace2DSimple();
				newTrace.setColor(color);
				this.traces.put(name, newTrace);
				this.chart.addTrace(newTrace);

				if (Config.getBoolean("GUI_PAINT_LINESPOINT"))
					newTrace.addTracePainter(new TracePainterDisc(Config
							.getInt("GUI_LINESPOINT_SIZE")));
				if (Config.getBoolean("GUI_PAINT_FILL"))
					newTrace.addTracePainter(new TracePainterFill(this.chart));
			}
		}
	}

	/** initializes the data with the first batch **/
	public void initData(BatchData b) {
		this.minTimestamp = b.getTimestamp();
		this.maxTimestamp = b.getTimestamp();
		this.minShownTimestamp = b.getTimestamp();
		this.maxShownTimestamp = this.minShownTimestamp;

		this.currentTimestamp = b.getTimestamp();

		// clear chart
		for (ITrace2D t : this.chart.getTraces()) {
			t.removeAllPoints();
		}

		// gather all available values
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
		String[] tempValues = this.gatherValues(b);
		this.legend.updateAddBox(tempValues);

		// load config
		if(this.config != null)
			this.loadConfig(this.config);

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
				trace.setTracePainter(new TracePainterDisc(Config
						.getInt("GUI_LINESPOINT_SIZE")));
				trace.addTracePainter(new TracePainterLine());
			} else {
				trace.setTracePainter(new TracePainterVerticalBar(Config
						.getInt("GUI_VERTICALBAR_SIZE"), this.chart));
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
		for (ConfigItem c : config.getEntries()) {
			if (c instanceof MetricVisualizerItem) {
				if (this.availableValues.contains(c.getName()))
					this.legend.addValueItemToList((MetricVisualizerItem) c);
			}
		}
	}
}
