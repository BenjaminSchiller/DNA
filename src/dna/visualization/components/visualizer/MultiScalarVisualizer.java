package dna.visualization.components.visualizer;

import info.monitorenter.gui.chart.IAxis;
import info.monitorenter.gui.chart.ITrace2D;
import info.monitorenter.gui.chart.ITracePainter;
import info.monitorenter.gui.chart.ITracePoint2D;
import info.monitorenter.gui.chart.traces.Trace2DSimple;
import info.monitorenter.gui.chart.traces.painters.TracePainterDisc;
import info.monitorenter.gui.chart.traces.painters.TracePainterFill;
import info.monitorenter.gui.chart.traces.painters.TracePainterLine;
import info.monitorenter.gui.chart.traces.painters.TracePainterVerticalBar;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import dna.series.data.BatchData;
import dna.series.data.Distribution;
import dna.series.data.DistributionDouble;
import dna.series.data.DistributionInt;
import dna.series.data.DistributionLong;
import dna.series.data.MetricData;
import dna.series.data.NodeValueList;
import dna.util.Config;
import dna.util.Log;
import dna.visualization.GuiOptions;
import dna.visualization.config.ConfigItem;
import dna.visualization.config.MultiScalarDistributionItem;
import dna.visualization.config.MultiScalarNodeValueListItem;
import dna.visualization.config.VisualizerListConfig;
import dna.visualization.config.VisualizerListConfig.SortModeDist;
import dna.visualization.config.VisualizerListConfig.SortModeNVL;
import dna.visualization.config.VisualizerListConfig.xAxisSelection;
import dna.visualization.config.VisualizerListConfig.yAxisSelection;

@SuppressWarnings("serial")
public class MultiScalarVisualizer extends Visualizer {
	// available values and traces
	private ArrayList<String> availableDistributions;
	private ArrayList<String> availableNodeValueLists;
	private ArrayList<String> addedTraces;
	private HashMap<String, ITrace2D> traces;
	private HashMap<ITrace2D, Double> offsets;

	// saved values
	private HashMap<String, Long> longDenominators;
	private HashMap<String, long[]> longValues;
	private HashMap<String, Integer> intDenominators;
	private HashMap<String, int[]> intValues;
	private HashMap<String, double[]> doubleValues;

	// config
	VisualizerListConfig config;

	// current batch
	private BatchData currentBatch;

	// constructor
	public MultiScalarVisualizer(VisualizerListConfig config) {
		// initialization
		this.traces = new HashMap<String, ITrace2D>();
		this.offsets = new HashMap<ITrace2D, Double>();
		this.addedTraces = new ArrayList<String>();
		this.availableDistributions = new ArrayList<String>();
		this.availableNodeValueLists = new ArrayList<String>();

		this.longDenominators = new HashMap<String, Long>();
		this.longValues = new HashMap<String, long[]>();

		this.intDenominators = new HashMap<String, Integer>();
		this.intValues = new HashMap<String, int[]>();

		this.doubleValues = new HashMap<String, double[]>();

		this.currentBatch = null;
		this.config = config;

		// remove timestamp-label on x-axis
		this.xAxis1.getAxisTitle().setTitle("x1");
		this.xAxis2.getAxisTitle().setTitle("x2");

		// set title and border of the visualizer
		TitledBorder title = BorderFactory
				.createTitledBorder("Multi-Scalar Visualizer");
		title.setBorder(BorderFactory
				.createEtchedBorder((EtchedBorder.LOWERED)));
		title.setTitleFont(GuiOptions.defaultFontBorders);
		title.setTitleColor(GuiOptions.defaultFontBordersColor);
		this.setBorder(title);

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

	/** initializes the data with the first batch **/
	public void initData(BatchData b) {
		// set current batch
		this.currentBatch = b;

		// set timestamps;
		this.minTimestamp = b.getTimestamp();
		this.maxTimestamp = b.getTimestamp();
		this.minShownTimestamp = b.getTimestamp();
		this.maxShownTimestamp = this.minShownTimestamp + 10;

		// clear chart
		for (ITrace2D t : this.chart.getTraces()) {
			t.removeAllPoints();
		}

		// gather all available values
		for (String metric : b.getMetrics().getNames()) {
			for (String dist : b.getMetrics().get(metric).getDistributions()
					.getNames()) {
				this.availableDistributions.add(metric + "." + dist);
			}
			for (String nvl : b.getMetrics().get(metric).getNodeValues()
					.getNames()) {
				this.availableNodeValueLists.add(metric + "." + nvl);
			}
		}

		// init addbox
		String[] tempValues = this.gatherValues(b);
		this.legend.updateAddBox(tempValues);

		// load config
		if (this.config != null)
			this.loadConfig(this.config);

		// put in first batch to update data
		this.updateData(b);

		// toggle visibility and validate
		this.toggleYAxisVisibility();
		this.validate();
	}

	/**
	 * Updates the chart and the legend with a new batchdata.
	 * 
	 * @param b
	 *            New batch
	 */
	public void updateData(BatchData b) {
		// update batch
		this.currentBatch = b;

		// clear chart
		this.clearPoints();

		// add points
		for (String metric : b.getMetrics().getNames()) {
			for (String dist : b.getMetrics().get(metric).getDistributions()
					.getNames()) {
				if (this.traces.containsKey(metric + "." + dist)) {
					Distribution tempDist = b.getMetrics().get(metric)
							.getDistributions().get(dist);
					String tempName = metric + "." + dist;

					if (tempDist instanceof DistributionDouble) {
						double[] tempValues = ((DistributionDouble) b
								.getMetrics().get(metric).getDistributions()
								.get(dist)).getDoubleValues();
						SortModeDist tempSortMode = ((LegendItemDistribution) this.legend
								.getLegendList().getLegendItem(tempName))
								.getSortMode();
						this.doubleValues.put(tempName, tempValues);
						this.addDistributionPoints(tempName, tempValues,
								tempSortMode, this.getTraceOffsetX(tempName));
					}
					if (tempDist instanceof DistributionInt) {
						int[] tempValues = ((DistributionInt) b.getMetrics()
								.get(metric).getDistributions().get(dist))
								.getIntValues();
						int tempDenominator = ((DistributionInt) b.getMetrics()
								.get(metric).getDistributions().get(dist))
								.getDenominator();
						SortModeDist tempSortMode = ((LegendItemDistribution) this.legend
								.getLegendList().getLegendItem(tempName))
								.getSortMode();
						this.addDistributionPoints(tempName, tempValues,
								tempDenominator, tempSortMode,
								this.getTraceOffsetX(tempName));
						this.intValues.put(tempName, tempValues);
						this.intDenominators.put(tempName, tempDenominator);
						this.legend.updateItem(tempName, tempDenominator);
					}
					if (tempDist instanceof DistributionLong) {
						long[] tempValues = ((DistributionLong) b.getMetrics()
								.get(metric).getDistributions().get(dist))
								.getLongValues();
						long tempDenominator = ((DistributionLong) b
								.getMetrics().get(metric).getDistributions()
								.get(dist)).getDenominator();
						SortModeDist tempSortMode = ((LegendItemDistribution) this.legend
								.getLegendList().getLegendItem(tempName))
								.getSortMode();
						this.addDistributionPoints(tempName, tempValues,
								tempDenominator, tempSortMode,
								this.getTraceOffsetX(tempName));
						this.longValues.put(tempName, tempValues);
						this.longDenominators.put(tempName, tempDenominator);
						this.legend.updateItem(tempName, tempDenominator);
					}
				}
			}
			for (String nvl : b.getMetrics().get(metric).getNodeValues()
					.getNames()) {
				String tempName = metric + "." + nvl;
				if (this.traces.containsKey(tempName)) {
					SortModeNVL tempSortMode = ((LegendItemNodeValueList) this.legend
							.getLegendList().getLegendItem(tempName))
							.getSortMode();
					double[] tempValues = b.getMetrics().get(metric)
							.getNodeValues().get(nvl).getValues();
					this.doubleValues.put(tempName, tempValues);
					this.addPoints(metric + "." + nvl, tempValues,
							tempSortMode, this.getTraceOffsetX(tempName));
				}
			}
		}
		// update ticks
		this.updateTicks();
	}

	/** adds points sorted and normalized by dividing through denominator **/
	private void addDistributionPoints(String name, long[] values,
			long denominator, SortModeDist sort, double offsetX) {
		ITrace2D tempTrace = this.traces.get(name);
		long[] tempValues = new long[values.length];

		switch (sort) {
		case cdf:
			double sum = 0;
			System.arraycopy(values, 0, tempValues, 0, values.length);
			Arrays.sort(tempValues);
			for (int i = 0; i < values.length; i++) {
				sum += (1.0 * values[i]) / denominator;
				tempTrace.addPoint(i + offsetX, sum);
			}
			break;
		case distribution:
			for (int i = 0; i < values.length; i++) {
				tempTrace
						.addPoint(i + offsetX, (1.0 * values[i]) / denominator);
			}
			break;
		}

		if (values.length - 1 > this.maxShownTimestamp)
			this.maxShownTimestamp = values.length - 1;
		if (values.length - 1 > this.maxTimestamp)
			this.maxTimestamp = values.length - 1;
	}

	/** adds points sorted and normalized by dividing through denominator **/
	private void addDistributionPoints(String name, int[] values,
			int denominator, SortModeDist sort, double offsetX) {
		ITrace2D tempTrace = this.traces.get(name);
		int[] tempValues = new int[values.length];

		switch (sort) {
		case cdf:
			double sum = 0;
			System.arraycopy(values, 0, tempValues, 0, values.length);
			Arrays.sort(tempValues);
			for (int i = 0; i < values.length; i++) {
				sum += (1.0 * tempValues[i]) / denominator;
				tempTrace.addPoint(i + offsetX, sum);
			}
			break;
		case distribution:
			for (int i = 0; i < values.length; i++) {
				tempTrace
						.addPoint(i + offsetX, (1.0 * values[i]) / denominator);
			}
			break;
		}

		if (values.length - 1 > this.maxShownTimestamp)
			this.maxShownTimestamp = values.length - 1;
		if (values.length - 1 > this.maxTimestamp)
			this.maxTimestamp = values.length - 1;
	}

	/** adds points sorted and normalized by dividing through denominator **/
	private void addDistributionPoints(String name, double[] values,
			SortModeDist sort, double offsetX) {
		ITrace2D tempTrace = this.traces.get(name);
		double[] tempValues = new double[values.length];

		switch (sort) {
		case cdf:
			double sum = 0;
			System.arraycopy(values, 0, tempValues, 0, values.length);
			Arrays.sort(tempValues);
			for (int i = 0; i < values.length; i++) {
				sum += values[i];
				tempTrace.addPoint(i + offsetX, sum);
			}
			break;
		case distribution:
			for (int i = 0; i < values.length; i++) {
				tempTrace.addPoint(i + offsetX, values[i]);
			}
			break;
		}

		if (values.length - 1 > this.maxShownTimestamp)
			this.maxShownTimestamp = values.length - 1;
		if (values.length - 1 > this.maxTimestamp)
			this.maxTimestamp = values.length - 1;
	}

	/** adds points sorted **/
	private void addPoints(String name, double[] values, SortModeNVL sort,
			double offsetX) {
		ITrace2D tempTrace = this.traces.get(name);
		double[] tempValues = new double[values.length];
		int tempIndex = 0;
		switch (sort) {
		case ascending:
			System.arraycopy(values, 0, tempValues, 0, values.length);
			Arrays.sort(tempValues);

			// check how many double.nan's are in the array
			for (int j = tempValues.length - 1; j >= 0; j--) {
				if (Double.isNaN(tempValues[j]))
					tempIndex++;
				else
					break;
			}
			// add points
			for (int i = 0; i < tempValues.length - tempIndex; i++) {
				tempTrace.addPoint(i, tempValues[i]);
			}
			break;
		case descending:
			System.arraycopy(values, 0, tempValues, 0, values.length);
			Arrays.sort(tempValues);

			// check how many double.nan's are in the array
			for (int j = tempValues.length - 1; j >= 0; j--) {
				if (Double.isNaN(tempValues[j]))
					tempIndex++;
				else
					break;
			}
			for (int i = 0, j = tempValues.length - 1; i < tempValues.length
					- tempIndex; i++) {
				tempTrace.addPoint(j - (i + tempIndex), tempValues[i]);
			}
			break;
		case index:

			for (int i = 0; i < values.length; i++) {
				tempTrace.addPoint(i, values[i]);
			}
			break;
		}

		if (values.length - 1 > this.maxShownTimestamp)
			this.maxShownTimestamp = values.length - 1;
		if (values.length - 1 > this.maxTimestamp)
			this.maxTimestamp = values.length - 1;
	}

	/** resets the metric visualizer **/
	public void clearPoints() {
		this.minShownTimestamp = 0;
		this.maxShownTimestamp = 10;
		for (String trace : this.traces.keySet()) {
			this.traces.get(trace).removeAllPoints();
		}
	}

	/** adds trace to the visualizer **/
	public void addTrace(String name, Color color, xAxisSelection xAxis,
			yAxisSelection yAxis) {
		Trace2DSimple newTrace = new Trace2DSimple();
		newTrace.setColor(color);
		this.traces.put(name, newTrace);
		this.addedTraces.add(name);
		if (xAxis.equals(xAxisSelection.x1))
			this.xAxis1.addTrace(newTrace);
		else
			this.xAxis2.addTrace(newTrace);
		if (yAxis.equals(yAxisSelection.y1))
			this.yAxis1.addTrace(newTrace);
		else
			this.yAxis2.addTrace(newTrace);

		if (Config.getBoolean("GUI_PAINT_LINESPOINT"))
			newTrace.addTracePainter(new TracePainterDisc(Config
					.getInt("GUI_LINESPOINT_SIZE")));
		if (Config.getBoolean("GUI_PAINT_FILL"))
			newTrace.addTracePainter(new TracePainterFill(this.chart));
	}

	/** adds trace of a distribution and its points to the visualizer **/
	public void addDistributionTrace(String name, Color color,
			xAxisSelection xAxis, yAxisSelection yAxis) {
		// calculate offset
		double offsetX = this.calculateOffsetX(name, xAxis);

		// add trace
		this.addTrace(name, color, xAxis, yAxis);

		// set offset
		this.setTraceOffsetX(name, offsetX);

		// add points to chart
		for (String metric : this.currentBatch.getMetrics().getNames()) {
			for (String dist : this.currentBatch.getMetrics().get(metric)
					.getDistributions().getNames()) {
				if ((metric + "." + dist).equals(name)) {
					Distribution tempDist = this.currentBatch.getMetrics()
							.get(metric).getDistributions().get(dist);
					String tempName = metric + "." + dist;

					if (tempDist instanceof DistributionDouble) {
						double[] tempValues = ((DistributionDouble) this.currentBatch
								.getMetrics().get(metric).getDistributions()
								.get(dist)).getDoubleValues();
						SortModeDist tempSortMode = Config
								.getSortModeDist("GUI_SORT_MODE_DIST");

						this.doubleValues.put(tempName, tempValues);

						this.addDistributionPoints(tempName, tempValues,
								tempSortMode, offsetX);
					}
					if (tempDist instanceof DistributionInt) {
						int[] tempValues = ((DistributionInt) this.currentBatch
								.getMetrics().get(metric).getDistributions()
								.get(dist)).getIntValues();
						int tempDenominator = ((DistributionInt) this.currentBatch
								.getMetrics().get(metric).getDistributions()
								.get(dist)).getDenominator();
						SortModeDist tempSortMode = Config
								.getSortModeDist("GUI_SORT_MODE_DIST");

						this.addDistributionPoints(tempName, tempValues,
								tempDenominator, tempSortMode, offsetX);
						this.intValues.put(tempName, tempValues);
						this.intDenominators.put(tempName, tempDenominator);
						this.legend.updateItem(tempName, tempDenominator);
					}
					if (tempDist instanceof DistributionLong) {
						long[] tempValues = ((DistributionLong) this.currentBatch
								.getMetrics().get(metric).getDistributions()
								.get(dist)).getLongValues();
						long tempDenominator = ((DistributionLong) this.currentBatch
								.getMetrics().get(metric).getDistributions()
								.get(dist)).getDenominator();
						SortModeDist tempSortMode = Config
								.getSortModeDist("GUI_SORT_MODE_DIST");

						this.addDistributionPoints(tempName, tempValues,
								tempDenominator, tempSortMode, offsetX);
						this.longValues.put(tempName, tempValues);
						this.longDenominators.put(tempName, tempDenominator);
						this.legend.updateItem(tempName, tempDenominator);
					}
				}
			}
		}
		// update ticks
		this.updateTicks();
	}

	/** adds trace of a nodevaluelist and its points to the visualizer **/
	public void addNodeValueListTrace(String name, Color color,
			xAxisSelection xAxis, yAxisSelection yAxis) {
		// calculate offset
		double offsetX = this.calculateOffsetX(name, xAxis);

		// add trace
		this.addTrace(name, color, xAxis, yAxis);

		// set offset
		this.setTraceOffsetX(name, offsetX);

		// add points to chart
		for (String metric : this.currentBatch.getMetrics().getNames()) {
			for (String nvl : this.currentBatch.getMetrics().get(metric)
					.getNodeValues().getNames()) {
				String tempName = metric + "." + nvl;
				if (this.traces.containsKey(tempName)) {
					SortModeNVL tempSortMode = ((LegendItemNodeValueList) this.legend
							.getLegendList().getLegendItem(tempName))
							.getSortMode();
					double[] tempValues = this.currentBatch.getMetrics()
							.get(metric).getNodeValues().get(nvl).getValues();

					this.doubleValues.put(tempName, tempValues);
					this.addPoints(metric + "." + nvl, tempValues,
							tempSortMode, offsetX);
				}
			}
		}
		// update ticks
		this.updateTicks();
	}

	/** removes a trace from the chart and the traces-list **/
	public void removeTrace(String name) {
		if (this.traces.containsKey(name)) {
			// check on which axis the trace will be removed
			boolean removedFromX1;
			if (this.xAxis1.getTraces().contains(this.traces.get(name)))
				removedFromX1 = true;
			else
				removedFromX1 = false;

			// remove trace
			this.offsets.remove(this.traces.get(name));
			this.chart.removeTrace(this.traces.get(name));
			this.traces.remove(name);
			this.addedTraces.remove(name);

			// adjust offsets
			// if (removedFromX1)
			// this.offsets.remove(this.traces.get(name));
			// else
			// this.offsetsX2.remove(this.traces.get(name));
		}
		if (this.longValues.containsKey(name))
			this.longValues.remove(name);
		if (this.longDenominators.containsKey(name))
			this.longDenominators.remove(name);
		if (this.intValues.containsKey(name))
			this.intValues.remove(name);
		if (this.intDenominators.containsKey(name))
			this.intDenominators.remove(name);
		if (this.doubleValues.containsKey(name))
			this.doubleValues.remove(name);

		this.toggleXAxisVisibility();
		this.toggleYAxisVisibility();
	}

	/** gathers all plottable values from the batch **/
	public String[] gatherValues(BatchData b) {
		ArrayList<String> tempList = new ArrayList<String>();

		ArrayList<String> tempDists = new ArrayList<String>();
		ArrayList<String> tempNvls = new ArrayList<String>();

		for (MetricData m : b.getMetrics().getList()) {
			if (m.getValues().size() > 0) {
				for (Distribution d : m.getDistributions().getList()) {
					tempDists.add(m.getName() + "." + d.getName());
				}
				for (NodeValueList n : m.getNodeValues().getList()) {
					tempNvls.add(m.getName() + "." + n.getName());
				}
			}
		}

		tempList.add("--- Distributions");
		for (String s : tempDists) {
			tempList.add("----- " + s);
		}
		tempList.add("--- NodeValueLists");
		for (String s : tempNvls) {
			tempList.add("----- " + s);
		}

		String[] tempValues = tempList.toArray(new String[tempList.size()]);
		return tempValues;
	}

	/** handles the ticks that are shown on the second x axis **/
	@Override
	public void updateX1Ticks() {
		double minTemp = 0;
		double maxTemp = 10;

		// get range of plotted data
		for (Object t : this.xAxis1.getTraces()) {
			if (t instanceof Trace2DSimple) {
				double minX = ((Trace2DSimple) t).getMinX();
				double maxX = ((Trace2DSimple) t).getMaxX();
				if (minTemp > minX)
					minTemp = minX;
				if (maxTemp < maxX)
					maxTemp = maxX;
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

	/** handles the ticks that are shown on the second x axis **/
	public void updateX2Ticks() {
		double minTemp = 0;
		double maxTemp = 10;

		// get range of plotted data
		for (Object t : this.xAxis2.getTraces()) {
			if (t instanceof Trace2DSimple) {
				double minX = ((Trace2DSimple) t).getMinX();
				double maxX = ((Trace2DSimple) t).getMaxX();
				if (minTemp > minX)
					minTemp = minX;
				if (maxTemp < maxX)
					maxTemp = maxX;
			}
		}

		if (maxTemp > minTemp) {
			double range = maxTemp - minTemp;
			if (range > 0) {
				double tickSpacingNew = Math.floor(range / 10);
				if (tickSpacingNew < 1)
					tickSpacingNew = 1.0;
				this.xAxis2.setMajorTickSpacing(tickSpacingNew);
				this.xAxis2.setMinorTickSpacing(tickSpacingNew);
			}
		}
	}

	/** toggles the y axis for a trace identified by its name **/
	@SuppressWarnings("rawtypes")
	public void toggleYAxis(String name) {
		if (this.traces.containsKey(name)) {
			Boolean left = false;
			Boolean right = false;

			ITrace2D tempTrace = new Trace2DSimple(null);

			// check if trace shown on left y axis
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
			// check if trace shown on right y axis
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
			// if on left -> swap to right
			if (left) {
				for (IAxis rightAxe : this.chart.getAxesYRight()) {
					rightAxe.addTrace(tempTrace);
				}
			} else {
				// else if on right -> swap to left
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

	/** toggles the x axis for a trace identified by its name **/
	public void toggleXAxis(String name) {
		if (this.traces.containsKey(name)) {
			if (this.chart.getAxesXBottom().get(0).getTraces()
					.contains(this.traces.get(name))) {
				this.chart.getAxesXBottom().get(0)
						.removeTrace(this.traces.get(name));
				this.chart.getAxesXBottom().get(1)
						.addTrace(this.traces.get(name));
			} else {
				this.chart.getAxesXBottom().get(1)
						.removeTrace(this.traces.get(name));
				this.chart.getAxesXBottom().get(0)
						.addTrace(this.traces.get(name));
			}
			this.toggleXAxisVisibility();
			this.updateTicks();
		}
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

	/** toggles grid on second x axis **/
	public void toggleX2Grid() {
		if (this.xAxis2.isPaintGrid())
			this.xAxis2.setPaintGrid(false);
		else
			this.xAxis2.setPaintGrid(true);
	}

	/** called when the gui is resetted **/
	public void reset() {
		for (String trace : this.traces.keySet()) {
			this.traces.get(trace).removeAllPoints();
		}
		this.availableDistributions.clear();
		this.availableNodeValueLists.clear();
	}

	/** called from an item to get resorted during pause **/
	public void sortItem(String name, SortModeNVL s) {
		if (this.traces.containsKey(name)) {
			this.traces.get(name).removeAllPoints();
			if (this.doubleValues.containsKey(name)) {
				// TODO OFFSET
				double offsetX = 0;
				this.addPoints(name, this.doubleValues.get(name), s, offsetX);
			}
		}
	}

	/** called from an item to get resorted during pause **/
	public void sortItem(String name, SortModeDist s) {
		if (this.traces.containsKey(name)) {
			this.traces.get(name).removeAllPoints();

			// TODO OFFSET
			double offsetX = 0;

			if (this.doubleValues.containsKey(name)) {
				this.addDistributionPoints(name, this.doubleValues.get(name),
						s, offsetX);
			} else if (this.intValues.containsKey(name)) {
				this.addDistributionPoints(name, this.intValues.get(name),
						this.intDenominators.get(name), s, offsetX);
			} else if (this.longValues.containsKey(name)) {
				this.addDistributionPoints(name, this.longValues.get(name),
						this.longDenominators.get(name), s, offsetX);
			}
		}
	}

	/** loads a config for displayed values etc. **/
	public void loadConfig(VisualizerListConfig config) {
		// add possible items
		for (ConfigItem c : config.getEntries()) {
			if (c instanceof MultiScalarDistributionItem) {
				if (this.availableDistributions.contains(c.getName()))
					this.legend
							.addDistributionItemToList(((MultiScalarDistributionItem) c));
			}
			if (c instanceof MultiScalarNodeValueListItem) {
				if (this.availableNodeValueLists.contains(c.getName()))
					this.legend
							.addNodeValueListItemToList((MultiScalarNodeValueListItem) c);
			}
		}
		// check if any general configuration is set
		if (config.isAnyGeneralConfigSet()) {
			// insert all available metrics
			if (config.getAllDistributionsConfig() != null) {
				MultiScalarDistributionItem c = config
						.getAllDistributionsConfig();
				for (String dist : this.availableDistributions) {
					this.legend
							.addDistributionItemToList(new MultiScalarDistributionItem(
									dist, c.getSortMode(), c.getXAxis(), c
											.getYAxis(), c.getDisplayMode(), c
											.getVisibility()));
				}
			}
			// insert all available general runtimes
			if (config.getAllNodeValueListsConfig() != null) {
				MultiScalarNodeValueListItem c = config
						.getAllNodeValueListsConfig();
				for (String nvl : this.availableNodeValueLists) {
					this.legend
							.addNodeValueListItemToList(new MultiScalarNodeValueListItem(
									nvl, c.getSortMode(), c.getXAxis(), c
											.getYAxis(), c.getDisplayMode(), c
											.getVisibility()));
				}
			}
		}
	}

	/** sets the offset of a given trace on x1 **/
	public void setTraceOffsetX(String name, double offsetX) {
		if (this.traces.containsKey(name))
			this.offsets.put(this.traces.get(name), offsetX);
	}

	/** gets the offset of a given trace on x1 **/
	public double getTraceOffsetX(String name) {
		if (this.traces.containsKey(name))
			return this.offsets.get(this.traces.get(name));
		Log.error("Error when getting offset of non-existing trace");
		return 0;
	}

	/** calculates the offset for a new trace **/
	public double calculateOffsetX(String name, xAxisSelection axis) {
		// gather used offsets
		List<Double> usedOffsets = new ArrayList<Double>();
		for (ITrace2D trace : this.offsets.keySet()) {
			if (this.xAxis1.containsTrace(trace)
					&& axis.equals(xAxisSelection.x1)) {
				usedOffsets.add(this.offsets.get(trace));
			}
			if (this.xAxis2.containsTrace(trace)
					&& axis.equals(xAxisSelection.x2)) {
				usedOffsets.add(this.offsets.get(trace));
			}
		}

		if (usedOffsets.size() == 0)
			return 0;

		// sort used offsets
		Collections.sort(usedOffsets);

		// calculate new offset
		int counter = 0;
		for (double d : usedOffsets) {
			if (counter * GuiOptions.multiScalarVisualizerXAxisOffset != d) {
				return counter * GuiOptions.multiScalarVisualizerXAxisOffset;
			}
			counter++;
		}
		return counter * GuiOptions.multiScalarVisualizerXAxisOffset;
	}

	/** recalculates the offsets for all traces on x1 **/
	public void decrementOffsetsX1() {
		for (ITrace2D trace : this.offsets.keySet()) {
			if (this.xAxis1.containsTrace(trace))
				if (this.offsets.get(trace) != 0)
					this.offsets.put(trace, this.offsets.get(trace)
							- GuiOptions.multiScalarVisualizerXAxisOffset);
		}
	}

	/** adjusts the offsets for all traces on x2 after **/
	public void decrementOffsetsX2() {
		for (ITrace2D trace : this.offsets.keySet()) {
			if (this.xAxis2.containsTrace(trace))
				if (this.offsets.get(trace) != 0)
					this.offsets.put(trace, this.offsets.get(trace)
							- GuiOptions.multiScalarVisualizerXAxisOffset);
		}
	}
}
