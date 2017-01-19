package dna.visualization.components.visualizer;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.BorderFactory;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import dna.labels.Label;
import dna.labels.LabelList;
import dna.series.data.BatchData;
import dna.util.Config;
import dna.visualization.MainDisplay;
import dna.visualization.components.visualizer.traces.LabelTrace;
import dna.visualization.config.components.LabelVisualizerConfig;
import dna.visualization.config.components.LabelVisualizerConfig.LabelAdditionPolicy;
import info.monitorenter.gui.chart.IAxis.AxisTitle;
import info.monitorenter.gui.chart.ITrace2D;
import info.monitorenter.gui.chart.ITracePoint2D;
import info.monitorenter.gui.chart.axis.scalepolicy.AxisScalePolicyAutomaticBestFit;
import info.monitorenter.gui.chart.axis.scalepolicy.AxisScalePolicyManualTicks;
import info.monitorenter.gui.chart.labelformatters.LabelFormatterDate;
import info.monitorenter.gui.chart.rangepolicies.RangePolicyFixedViewport;
import info.monitorenter.gui.chart.rangepolicies.RangePolicyUnbounded;
import info.monitorenter.util.Range;

public class LabelVisualizer extends Visualizer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected HashMap<String, LabelTrace> labelTraces;

	protected HashMap<String, Integer> mapping;
	protected HashMap<String, Color> colorMap;
	protected int mappingCounter;

	protected ArrayList<String> availableValues;
	private LinkedList<BatchData> batchBuffer;
	private BatchData initBatch;
	private int bufferSize;
	private boolean reload;

	private boolean xAxisTypeTimestamp;
	private long currentTimestamp;

	// config
	protected MainDisplay mainDisplay;
	protected LabelVisualizerConfig config;

	protected boolean automaticAddition;

	protected LabelAdditionPolicy labelAdditionPolicy;
	protected String[] labelAdditionList;

	public LabelVisualizer(MainDisplay mainDisplay, LabelVisualizerConfig config) {
		super(config.getChartSize(), config.getLegendSize());

		this.x1Connected = config.getMenuBarConfig().isX1AxisConnected();
		this.mainDisplay = mainDisplay;
		this.labelTraces = new HashMap<String, LabelTrace>();
		this.mapping = new HashMap<String, Integer>();
		this.colorMap = new HashMap<String, Color>();
		this.mappingCounter = 0;
		this.availableValues = new ArrayList<String>();

		this.bufferSize = config.getTraceLength();
		this.TRACE_LENGTH = config.getTraceLength();

		this.labelAdditionPolicy = config.getAdditionPolicy();
		this.labelAdditionList = config.getAdditionList();

		this.config = config;
		this.automaticAddition = true;

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

		this.yAxis1.setAxisScalePolicy(new AxisScalePolicyManualTicks());
		this.yAxis1.setRangePolicy(new RangePolicyFixedViewport(new Range(0, 1)));
		this.yAxis1.setPaintScale(false);

		// add menu bar
		super.addMenuBar(config.getMenuBarConfig());
		this.menuBar.setYCoordsLabelText("v:");
		this.menuBar.setVisible(config.getMenuBarConfig().isVisible());

		// add coordinate parsing to mouseover on chart
		this.chart.addMouseMotionListener(new MouseMotionListener() {
			@Override
			public void mouseMoved(MouseEvent e) {
				if (chart.getPointFinder().getNearestPoint(e, chart) != null) {
					ITracePoint2D tempPointFinder = chart.getPointFinder().getNearestPoint(e, chart);
					if (tempPointFinder != null)
						menuBar.updateCoordsPanel(tempPointFinder.getX(), tempPointFinder.getY());
				}
			}

			public void mouseDragged(MouseEvent e) {
			}
		});

		// apply config
		this.chart.setPreferredSize(config.getChartSize());
		this.legend.setLegendSize(config.getLegendSize());

		this.xAxis1.setAxisTitle(new AxisTitle(config.getX1AxisTitle()));
		this.yAxis1.setAxisTitle(new AxisTitle(config.getY1AxisTitle()));

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

		for (String name : this.labelTraces.keySet()) {
			this.labelTraces.get(name).setLastTimestamp(timestamp - 1);
		}

		// gather all available values
		for (Label label : b.getLabels().getList()) {
			String key = getLabelKey(label.getName(), label.getType());
			this.availableValues.add(key);

			this.handleAutomaticAdditions(key);
			// if (this.automaticAddition)
			// this.legend.addLabelItemToList(key);
		}

		// init addbox
		String[] tempValues = this.gatherValues(b);
		this.legend.updateAddBox(tempValues);

		// toggle visibility and validate
		this.toggleYAxisVisibility();
		this.validate();
	}

	/** gathers all plottable values from the batch **/
	public String[] gatherValues(BatchData b) {
		ArrayList<String> tempList = new ArrayList<String>();
		tempList.add("labels");

		ArrayList<String> labelers = new ArrayList<String>();
		ArrayList<ArrayList<String>> types = new ArrayList<ArrayList<String>>();

		for (Label l : b.getLabels().getList()) {
			String name = l.getName();
			String type = l.getType();

			if (!labelers.contains(name)) {
				labelers.add(name);
				types.add(new ArrayList<String>());
			}

			int index = labelers.indexOf(name);
			ArrayList<String> typesList = types.get(index);

			if (!typesList.contains(type))
				typesList.add(type);
		}

		// store original indices before sorting
		HashMap<String, Integer> indexMap = new HashMap<String, Integer>();
		for (int i = 0; i < labelers.size(); i++)
			indexMap.put(labelers.get(i), i);

		// sort labelers alphabetically
		Collections.sort(labelers);

		// fill templist
		for (int i = 0; i < labelers.size(); i++) {
			String name = labelers.get(i);
			tempList.add("--- " + name);

			ArrayList<String> ts = types.get(indexMap.get(name));
			Collections.sort(ts);

			for (String type : ts)
				tempList.add("----- " + name + "." + type);
		}

		String[] tempValues = tempList.toArray(new String[tempList.size()]);
		return tempValues;
	}

	/** adds trace to the visualizer with default trace length **/
	public void addTrace(String name, Color color) {
		if (!this.labelTraces.containsKey(name)) {
			int yMapping = this.getLabelerTypeKeyMapping(name);
			this.colorMap.put(name, color);
			LabelTrace labelTrace = new LabelTrace(this, this.chart, name, yMapping, 10, color, this.currentTimestamp);
			this.labelTraces.put(name, labelTrace);
		}

		this.updateTraceSizes();
		this.updateYAxisRange();
	}

	/**
	 * If the name is already being mapped it returns the mapping. Else a new
	 * one is created and returned.
	 **/
	protected int getLabelerTypeKeyMapping(String labelerTypeKey) {
		if (this.mapping.containsKey(labelerTypeKey))
			return this.mapping.get(labelerTypeKey);

		int newMapping = this.mappingCounter;
		this.mapping.put(labelerTypeKey, newMapping);
		this.mappingCounter--;

		return newMapping;
	}

	/**
	 * removes a trace from the chart and the traces-list and the current traces
	 **/
	public void removeTrace(String name) {
		if (this.labelTraces.containsKey(name)) {
			LabelTrace trace = this.labelTraces.get(name);
			trace.clear();
			this.labelTraces.remove(name);
		}

		this.toggleXAxisVisibility();
		this.toggleYAxisVisibility();

		this.updateTraceSizes();
		this.updateYAxisRange();
	}

	/** Updates the trace sizes. **/
	protected void updateTraceSizes() {
		int traces = this.labelTraces.size();
		for (String name : this.labelTraces.keySet()) {
			LabelTrace trace = this.labelTraces.get(name);
			trace.setSize(this.getTraceSize(traces));
		}
	}

	/** Returns the trace size of a trace based on the number of traces. **/
	protected int getTraceSize(int traces) {
		int paddings = 50; // this will be substracted from the absolute height
		int chartHeight = (int) Math.floor(this.config.getChartSize().getHeight()) - paddings;
		int individualHeight = (int) Math.floor((this.config.getBarThickness() * chartHeight) / traces);

		return individualHeight;
	}

	/** Updates the YAxisRange. **/
	protected void updateYAxisRange() {
		this.yAxis1.setRangePolicy(new RangePolicyFixedViewport(getYAxisRange()));
	}

	/** Returns the y-axis range based on the number of traces. **/
	protected Range getYAxisRange() {
		int max = 0;
		int min = 0;
		boolean first = true;
		for (String name : this.labelTraces.keySet()) {
			int y = this.labelTraces.get(name).getYMapping();
			if (first) {
				max = y;
				min = y;
				first = false;
			} else {
				max = Math.max(max, y);
				min = Math.min(min, y);
			}
		}
		return new Range(min - 1, max + 1);
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
			// this.updateData(b);
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

			// iterate over all added traces
			LabelList labels = b.getLabels();

			for (String name : this.labelTraces.keySet()) {
				LabelTrace labelTrace = this.labelTraces.get(name);
				boolean updated = false;
				for (Label l : labels.getList()) {
					String key = getLabelKey(l.getName(), l.getType());
					if (key.equals(name)) {
						labelTrace.update(timestampDouble, l);
						updated = true;
						break;
					}
				}

				if (!updated)
					labelTrace.update(timestampDouble, null);
			}

			if (this.FIXED_VIEWPORT) {
				this.setXAxis1RangeByIntervalSelection();
			} else {
				this.xAxis1.setRange(new Range(minTimestamp, maxTimestamp));
			}

			// update chart axis ticks
			this.updateTicks();

			// check if new label contained in the batch
			boolean newLabelFound = false;

			for (Label l : labels.getList()) {
				String name = l.getName();
				String type = l.getType();

				String key = getLabelKey(name, type);

				if (!this.availableValues.contains(key)) {
					newLabelFound = true;
					this.availableValues.add(key);

					this.handleAutomaticAdditions(key);
					// if (this.automaticAddition)
					// this.legend.addLabelItemToList(key);
				}
			}

			if (newLabelFound)
				this.legend.updateAddBox(this.gatherAddBoxChoicesFromAvailableValues());
		}

		this.validate();
	}

	/** Handles the automatic additions given the policy. **/
	protected void handleAutomaticAdditions(String key) {
		switch (this.labelAdditionPolicy) {
		case AUTOMATIC_ADDITION_ALL:
			this.addLabelItemToList(key);
			break;
		case AUTOMATIC_ADDITION_LIST:
			for (String s : this.labelAdditionList) {
				if (s.equals(key)) {
					this.addLabelItemToList(key);
					break;
				}
			}
			break;
		case MANUAL:
			// do nothing
			break;
		}
	}

	/**
	 * Adds a label item to the legend list (same as when selected from addbox.
	 **/
	protected void addLabelItemToList(String key) {
		this.legend.addLabelItemToList(key);
	}

	/** Gathers all currently available addbox-choices. **/
	protected String[] gatherAddBoxChoicesFromAvailableValues() {
		ArrayList<String> addBoxList = new ArrayList<String>();
		addBoxList.add("labels");

		HashMap<String, ArrayList<String>> labelers = new HashMap<String, ArrayList<String>>();

		// split available value-keys by .
		for (String keys : this.availableValues) {
			String[] splits = keys.split("\\.");
			String name = splits[0];
			String type = splits[1];

			for (int i = 2; i < splits.length; i++)
				type += "." + splits[i];

			ArrayList<String> tempList;
			if (labelers.containsKey(name)) {
				tempList = labelers.get(name);
			} else {
				tempList = new ArrayList<String>();
				labelers.put(name, tempList);
			}

			tempList.add(type);
		}

		for (String l : labelers.keySet()) {
			addBoxList.add("--- " + l);
			for (String t : labelers.get(l))
				addBoxList.add("----- " + getLabelKey(l, t));
		}

		String[] tempValues = addBoxList.toArray(new String[addBoxList.size()]);
		return tempValues;
	}

	/** Returns the key of a label based on its name and type. **/
	protected String getLabelKey(String name, String type) {
		return name + "." + type;
	}

	/** reloads all data included in the batchBuffer **/
	private void reloadData() {
		// set reload flag
		this.reload = true;

		// reset current timestamp
		this.currentTimestamp = 0;

		// clear chart
		for (String name : this.labelTraces.keySet()) {
			LabelTrace trace = this.labelTraces.get(name);
			trace.clear();
		}

		// update with old batches
		Iterator<BatchData> iterator = this.batchBuffer.iterator();
		while (iterator.hasNext()) {
			this.updateData(iterator.next());
		}

		// unset reload flag
		this.reload = false;
	}

	/** resets the metric visualizer **/
	public void reset() {
		this.minShownTimestamp = 0;
		this.maxShownTimestamp = 10;

		// clear labels
		for (String name : this.labelTraces.keySet()) {
			LabelTrace trace = this.labelTraces.get(name);
			trace.clear();
		}
		this.batchBuffer.clear();
		this.availableValues.clear();
		this.chart.updateUI();
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

	/** shows/hides a trace from the chart without deleting it **/
	public void toggleTraceVisibility(String name) {
		if (this.labelTraces.containsKey(name)) {
			LabelTrace trace = this.labelTraces.get(name);
			if (trace.isVisible())
				trace.setVisible(false);
			else
				trace.setVisible(true);
		}
		this.chart.setRequestedRepaint(true);
	}

	/** Returns the value at the given y-location. **/
	public String getValue(double x, double y) {
		String value = "unknown";
		int yFloored = (int) Math.floor(y);
		for (String key : this.labelTraces.keySet()) {
			LabelTrace label = this.labelTraces.get(key);
			if (yFloored == label.getYMapping()) {
				value = label.getValue(x);
				break;
			}
		}
		return value;
	}

	/** Updates the according legend item. **/
	public void updateItem(String name, String value) {
		this.legend.updateItem(name, value);
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
