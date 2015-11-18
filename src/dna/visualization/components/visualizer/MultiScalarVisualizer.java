package dna.visualization.components.visualizer;

import info.monitorenter.gui.chart.IAxis;
import info.monitorenter.gui.chart.IAxis.AxisTitle;
import info.monitorenter.gui.chart.ITrace2D;
import info.monitorenter.gui.chart.ITracePainter;
import info.monitorenter.gui.chart.ITracePoint2D;
import info.monitorenter.gui.chart.traces.Trace2DSimple;
import info.monitorenter.gui.chart.traces.painters.TracePainterDisc;
import info.monitorenter.gui.chart.traces.painters.TracePainterFill;
import info.monitorenter.gui.chart.traces.painters.TracePainterLine;
import info.monitorenter.gui.chart.traces.painters.TracePainterVerticalBar;

import java.awt.Color;
import java.awt.Font;
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
import dna.series.data.MetricData;
import dna.series.data.distr.BinnedDoubleDistr;
import dna.series.data.distr.BinnedIntDistr;
import dna.series.data.distr.BinnedLongDistr;
import dna.series.data.distr.Distr;
import dna.series.data.distr.Distr.DistrType;
import dna.series.data.distr.QualityDoubleDistr;
import dna.series.data.distr.QualityIntDistr;
import dna.series.data.distr.QualityLongDistr;
import dna.series.data.nodevaluelists.NodeValueList;
import dna.util.Log;
import dna.visualization.MainDisplay;
import dna.visualization.config.ConfigItem;
import dna.visualization.config.MultiScalarDistributionItem;
import dna.visualization.config.MultiScalarNodeValueListItem;
import dna.visualization.config.VisualizerListConfig;
import dna.visualization.config.VisualizerListConfig.SortModeDist;
import dna.visualization.config.VisualizerListConfig.SortModeNVL;
import dna.visualization.config.VisualizerListConfig.xAxisSelection;
import dna.visualization.config.VisualizerListConfig.yAxisSelection;
import dna.visualization.config.components.MultiScalarVisualizerConfig;

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
	private HashMap<String, double[]> doubleValues;
	private HashMap<String, Double> binSizes;

	// config
	private VisualizerListConfig listConfig;
	MainDisplay mainDisplay;
	protected MultiScalarVisualizerConfig config;
	private double xAxisOffset;

	// current batch
	private BatchData currentBatch;

	// constructor
	public MultiScalarVisualizer(MainDisplay mainDisplay,
			MultiScalarVisualizerConfig config) {
		// initialization
		super(config.getChartSize(), config.getLegendSize());
		this.config = config;
		this.mainDisplay = mainDisplay;
		this.traces = new HashMap<String, ITrace2D>();
		this.offsets = new HashMap<ITrace2D, Double>();
		this.addedTraces = new ArrayList<String>();
		this.availableDistributions = new ArrayList<String>();
		this.availableNodeValueLists = new ArrayList<String>();

		this.longDenominators = new HashMap<String, Long>();
		this.longValues = new HashMap<String, long[]>();

		this.doubleValues = new HashMap<String, double[]>();

		this.binSizes = new HashMap<String, Double>();

		this.currentBatch = null;
		this.listConfig = config.getListConfig();

		// remove timestamp-label on x-axis

		// set title and border of the visualizer
		TitledBorder title = BorderFactory.createTitledBorder(config.getName());
		title.setBorder(BorderFactory
				.createEtchedBorder((EtchedBorder.LOWERED)));
		title.setTitleFont(new Font(
				this.mainDisplay.getDefaultFont().getName(), Font.BOLD,
				this.mainDisplay.getDefaultFont().getSize()));
		title.setTitleColor(this.mainDisplay.getDefaultFontColor());
		this.setBorder(title);

		// add menu bar
		super.addMenuBar(config.getMenuBarConfig());

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

		// apply config
		this.chart.setPreferredSize(config.getChartSize());
		this.legend.setLegendSize(config.getLegendSize());
		this.xAxisOffset = config.getxAxisOffset();

		this.xAxis1.setAxisTitle(new AxisTitle(config.getx1AxisTitle()));
		this.xAxis2.setAxisTitle(new AxisTitle(config.getx2AxisTitle()));

		this.yAxis1.setAxisTitle(new AxisTitle(config.getY1AxisTitle()));
		this.yAxis2.setAxisTitle(new AxisTitle(config.getY2AxisTitle()));
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
		if (this.listConfig != null && !super.locked)
			this.loadConfig(this.listConfig);

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
					Distr<?, ?> tempDist = b.getMetrics().get(metric)
							.getDistributions().get(dist);
					String tempName = metric + "." + dist;

					DistrType tempType = tempDist.getDistrType();

					SortModeDist tempSortMode = ((LegendItemDistribution) this.legend
							.getLegendList().getLegendItem(tempName))
							.getSortMode();

					switch (tempType) {
					case BINNED_DOUBLE:
						BinnedDoubleDistr bdd = (BinnedDoubleDistr) tempDist;
						this.addLongDistributionTrace(tempName,
								bdd.getValues(), bdd.getDenominator(),
								bdd.getBinSize(), tempSortMode,
								this.getTraceOffsetX(tempName));
						break;
					case BINNED_INT:
						BinnedIntDistr bid = (BinnedIntDistr) tempDist;
						this.addLongDistributionTrace(tempName,
								bid.getValues(), bid.getDenominator(),
								bid.getBinSize(), tempSortMode,
								this.getTraceOffsetX(tempName));
						break;
					case BINNED_LONG:
						BinnedLongDistr bld = (BinnedLongDistr) tempDist;
						this.addLongDistributionTrace(tempName,
								bld.getValues(), bld.getDenominator(),
								bld.getBinSize(), tempSortMode,
								this.getTraceOffsetX(tempName));
						break;
					case QUALITY_DOUBLE:
						QualityDoubleDistr qdd = (QualityDoubleDistr) tempDist;
						this.addDoubleDistributionTrace(tempName,
								qdd.getValues(), qdd.getBinSize(),
								tempSortMode, this.getTraceOffsetX(tempName));
						break;
					case QUALITY_INT:
						QualityIntDistr qid = (QualityIntDistr) tempDist;
						this.addDoubleDistributionTrace(tempName,
								qid.getValues(), qid.getBinSize(),
								tempSortMode, this.getTraceOffsetX(tempName));
						break;
					case QUALITY_LONG:
						QualityLongDistr qld = (QualityLongDistr) tempDist;
						this.addDoubleDistributionTrace(tempName,
								qld.getValues(), qld.getBinSize(),
								tempSortMode, this.getTraceOffsetX(tempName));
						break;
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
			long denominator, double binsize, SortModeDist sort, double offsetX) {
		ITrace2D tempTrace = this.traces.get(name);
		long[] tempValues = new long[values.length];

		switch (sort) {
		case cdf:
			double sum = 0;
			System.arraycopy(values, 0, tempValues, 0, values.length);
			for (int i = 0; i < values.length; i++) {
				sum += (1.0 * values[i]) / denominator;
				tempTrace.addPoint(i * binsize + offsetX, sum);
			}
			break;
		case distribution:
			for (int i = 0; i < values.length; i++) {
				tempTrace.addPoint(i * binsize + offsetX, (1.0 * values[i])
						/ denominator);
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
			double binsize, SortModeDist sort, double offsetX) {
		ITrace2D tempTrace = this.traces.get(name);
		double[] tempValues = new double[values.length];

		switch (sort) {
		case cdf:
			double sum = 0;
			System.arraycopy(values, 0, tempValues, 0, values.length);
			for (int i = 0; i < values.length; i++) {
				sum += values[i];
				tempTrace.addPoint(i * binsize + offsetX, sum);
			}
			break;
		case distribution:
			for (int i = 0; i < values.length; i++) {
				tempTrace.addPoint(i * binsize + offsetX, values[i]);
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

		if (config.isPaintLinesPoint())
			newTrace.addTracePainter(new TracePainterDisc(config
					.getLinesPointSize()));
		if (config.isPaintFill())
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

		// get sort mode
		SortModeDist tempSortMode = SortModeDist.distribution;
		if (config.getListConfig() != null) {
			tempSortMode = config.getListConfig().getAllDistributionsConfig()
					.getSortMode();
		}

		// add points to chart
		for (String metric : this.currentBatch.getMetrics().getNames()) {
			for (String dist : this.currentBatch.getMetrics().get(metric)
					.getDistributions().getNames()) {
				if ((metric + "." + dist).equals(name)) {
					Distr<?, ?> tempDist = this.currentBatch.getMetrics()
							.get(metric).getDistributions().get(dist);
					String tempName = metric + "." + dist;

					switch (tempDist.getDistrType()) {
					case BINNED_DOUBLE:
						BinnedDoubleDistr bdd = (BinnedDoubleDistr) tempDist;
						this.addLongDistributionTrace(tempName,
								bdd.getValues(), bdd.getDenominator(),
								bdd.getBinSize(), tempSortMode, offsetX);
						break;
					case BINNED_INT:
						BinnedIntDistr bid = (BinnedIntDistr) tempDist;
						this.addLongDistributionTrace(tempName,
								bid.getValues(), bid.getDenominator(),
								bid.getBinSize(), tempSortMode, offsetX);
						break;
					case BINNED_LONG:
						BinnedLongDistr bld = (BinnedLongDistr) tempDist;
						this.addLongDistributionTrace(tempName,
								bld.getValues(), bld.getDenominator(),
								bld.getBinSize(), tempSortMode, offsetX);
						break;
					case QUALITY_DOUBLE:
						QualityDoubleDistr qdd = (QualityDoubleDistr) tempDist;
						this.addDoubleDistributionTrace(tempName,
								qdd.getValues(), qdd.getBinSize(),
								tempSortMode, offsetX);
						break;
					case QUALITY_INT:
						QualityIntDistr qid = (QualityIntDistr) tempDist;
						this.addDoubleDistributionTrace(tempName,
								qid.getValues(), qid.getBinSize(),
								tempSortMode, offsetX);
						break;
					case QUALITY_LONG:
						QualityLongDistr qld = (QualityLongDistr) tempDist;
						this.addDoubleDistributionTrace(tempName,
								qld.getValues(), qld.getBinSize(),
								tempSortMode, offsetX);
						break;
					}
				}
			}
		}
		// update ticks
		this.updateTicks();
	}

	/** adds trace of a double distribution and its points to the visualizer **/
	private void addDoubleDistributionTrace(String name, double[] values,
			double binsize, SortModeDist tempSortMode, double offsetX) {
		this.doubleValues.put(name, values);
		this.binSizes.put(name, binsize);
		this.addDistributionPoints(name, values, binsize, tempSortMode, offsetX);
	}

	/** adds trace of a long distribution and its points to the visualizer **/
	private void addLongDistributionTrace(String name, long[] values,
			long denominator, double binsize, SortModeDist tempSortMode,
			double offsetX) {
		this.longValues.put(name, values);
		this.longDenominators.put(name, denominator);
		this.binSizes.put(name, binsize);
		this.addDistributionPoints(name, values, denominator, binsize,
				tempSortMode, offsetX);
		this.legend.updateItem(name, denominator);
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
		if (this.doubleValues.containsKey(name))
			this.doubleValues.remove(name);
		if (this.binSizes.containsKey(name))
			this.binSizes.remove(name);

		this.toggleXAxisVisibility();
		this.toggleYAxisVisibility();
	}

	/** gathers all plottable values from the batch **/
	public String[] gatherValues(BatchData b) {
		ArrayList<String> tempList = new ArrayList<String>();

		ArrayList<String> tempDists = new ArrayList<String>();
		ArrayList<String> tempNvls = new ArrayList<String>();

		for (MetricData m : b.getMetrics().getList()) {
			if (m.getDistributions().size() > 0 || m.getNodeValues().size() > 0) {
				for (Distr<?, ?> d : m.getDistributions().getList()) {
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
				trace.setTracePainter(new TracePainterDisc(config
						.getLinesPointSize()));
				trace.addTracePainter(new TracePainterLine());
			} else {

				trace.setTracePainter(new TracePainterVerticalBar(config
						.getVerticalBarSize(), this.chart));
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
						this.binSizes.get(name), s, offsetX);
			} else if (this.longValues.containsKey(name)) {
				this.addDistributionPoints(name, this.longValues.get(name),
						this.longDenominators.get(name),
						this.binSizes.get(name), s, offsetX);
			}
		}
	}

	/** loads a config for displayed values etc. **/
	public void loadConfig(VisualizerListConfig config) {
		// check orderIds
		ArrayList<ConfigItem> configsList = new ArrayList<ConfigItem>();

		// add single configs to lists
		for (ConfigItem c : config.getEntries()) {
			if (c instanceof MultiScalarDistributionItem) {
				if (this.availableDistributions.contains(c.getName())
						&& c.getOrderId() >= -1) {
					configsList.add(c);
				}
			}
			if (c instanceof MultiScalarNodeValueListItem) {
				if (this.availableNodeValueLists.contains(c.getName())
						&& c.getOrderId() >= -1) {
					configsList.add(c);
				}
			}
		}

		// add general configs to list
		MultiScalarVisualizer.addGeneralConfigs(configsList, config);

		// craft names list to use as a blacklist for general configs later
		ArrayList<String> configsNamesList = new ArrayList<String>(
				configsList.size());
		for (ConfigItem c : configsList)
			configsNamesList.add(c.getName());

		// convert list to array
		ConfigItem[] configsArray = configsList
				.toArray(new ConfigItem[configsList.size()]);

		// sort array with insertion sort
		for (int i = 1; i < configsArray.length; i++) {
			ConfigItem single = configsArray[i];
			int j = i;
			while (j > 0
					&& configsArray[j - 1].getOrderId() > single.getOrderId()) {
				configsArray[j] = configsArray[j - 1];
				j--;
			}
			configsArray[j] = single;
		}
		;

		// calculate "breakpoint" in sorted list: where does -1 end?
		int breakpoint = 0;
		for (int i = 0; i < configsArray.length
				&& configsArray[i].getOrderId() < 0; i++)
			breakpoint = i + 1;

		// first insert items with id > -1, then those with -1
		this.insertForId(breakpoint, configsArray.length, configsNamesList,
				configsArray, config);
		this.insertForId(0, breakpoint, configsNamesList, configsArray, config);
	}

	/** Inserts items from the configs array to the legend. **/
	private void insertForId(int from, int to,
			ArrayList<String> configsNamesList, ConfigItem[] configsArray,
			VisualizerListConfig config) {
		for (int i = from; i < to; i++) {
			ConfigItem item = configsArray[i];

			// if orderId < -1, skip
			if (item.getOrderId() < -1)
				continue;

			switch (item.getName()) {
			case (VisualizerListConfig.generalDistributionsConfigName):
				this.insertDistributions(config, configsNamesList);
				break;
			case (VisualizerListConfig.generalNodeValueListsConfigName):
				this.insertNodeValueLists(config, configsNamesList);
				break;

			default:
				if (item instanceof MultiScalarDistributionItem) {
					if (this.availableDistributions.contains(item.getName()))
						this.legend
								.addDistributionItemToList((MultiScalarDistributionItem) item);
				}
				if (item instanceof MultiScalarNodeValueListItem) {
					if (this.availableNodeValueLists.contains(item.getName()))
						this.legend
								.addNodeValueListItemToList((MultiScalarNodeValueListItem) item);
				}
				break;
			}
		}
	}

	/** Adds all set general configs to the list. **/
	private static void addGeneralConfigs(ArrayList<ConfigItem> configs,
			VisualizerListConfig config) {
		if (config.isAnyGeneralConfigSet()) {
			if (config.getAllDistributionsConfig() != null
					&& config.getAllDistributionsConfig().getOrderId() >= -1)
				configs.add(config.getAllDistributionsConfig());

			if (config.getAllNodeValueListsConfig() != null
					&& config.getAllNodeValueListsConfig().getOrderId() >= -1)
				configs.add(config.getAllNodeValueListsConfig());
		}
	}

	/** Insert all available distributions. **/
	private void insertDistributions(VisualizerListConfig config,
			ArrayList<String> blackList) {
		MultiScalarDistributionItem c = config.getAllDistributionsConfig();
		for (String dist : this.availableDistributions) {
			if (!blackList.contains(dist)) {
				this.legend
						.addDistributionItemToList(new MultiScalarDistributionItem(
								dist, c.getSortMode(), c.getXAxis(), c
										.getYAxis(), c.getDisplayMode(), c
										.getVisibility()));
			}
		}
	}

	/** Insert all available nodevaluelists. **/
	private void insertNodeValueLists(VisualizerListConfig config,
			ArrayList<String> blackList) {
		MultiScalarNodeValueListItem c = config.getAllNodeValueListsConfig();
		for (String nvl : this.availableNodeValueLists) {
			if (!blackList.contains(nvl)) {
				this.legend
						.addNodeValueListItemToList(new MultiScalarNodeValueListItem(
								nvl, c.getSortMode(), c.getXAxis(), c
										.getYAxis(), c.getDisplayMode(), c
										.getVisibility()));
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
			if (counter * this.xAxisOffset != d) {
				return counter * this.xAxisOffset;
			}
			counter++;
		}
		return counter * this.xAxisOffset;
	}

	/** recalculates the offsets for all traces on x1 **/
	public void decrementOffsetsX1() {
		for (ITrace2D trace : this.offsets.keySet()) {
			if (this.xAxis1.containsTrace(trace))
				if (this.offsets.get(trace) != 0)
					this.offsets.put(trace, this.offsets.get(trace)
							- this.xAxisOffset);
		}
	}

	/** adjusts the offsets for all traces on x2 after **/
	public void decrementOffsetsX2() {
		for (ITrace2D trace : this.offsets.keySet()) {
			if (this.xAxis2.containsTrace(trace))
				if (this.offsets.get(trace) != 0)
					this.offsets.put(trace, this.offsets.get(trace)
							- this.xAxisOffset);
		}
	}
}
