package dna.visualization.components.visualizer;

import info.monitorenter.gui.chart.IAxis;
import info.monitorenter.gui.chart.ITrace2D;
import info.monitorenter.gui.chart.ITracePoint2D;
import info.monitorenter.gui.chart.traces.Trace2DSimple;
import info.monitorenter.gui.chart.traces.painters.TracePainterDisc;
import info.monitorenter.gui.chart.traces.painters.TracePainterFill;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

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
import dna.visualization.GuiOptions;

@SuppressWarnings("serial")
public class MultiScalarVisualizer extends Visualizer {
	// available values and traces
	private ArrayList<String> availableValues;
	private HashMap<String, ITrace2D> traces;

	// saved values
	private HashMap<String, Long> longDenominators;
	private HashMap<String, long[]> longValues;
	private HashMap<String, Integer> intDenominators;
	private HashMap<String, int[]> intValues;
	private HashMap<String, double[]> doubleValues;

	/** sort mode used to plot nodevaluelists **/
	public enum SortModeNVL {
		index, ascending, descending
	};

	/** sort mode used to plot distributions **/
	public enum SortModeDist {
		distribution, cdf
	}

	// constructor
	public MultiScalarVisualizer() {
		// initialization
		this.traces = new HashMap<String, ITrace2D>();
		this.availableValues = new ArrayList<String>();

		this.longDenominators = new HashMap<String, Long>();
		this.longValues = new HashMap<String, long[]>();

		this.intDenominators = new HashMap<String, Integer>();
		this.intValues = new HashMap<String, int[]>();

		this.doubleValues = new HashMap<String, double[]>();

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
		this.minTimestamp = b.getTimestamp();
		this.maxTimestamp = b.getTimestamp();
		this.minShownTimestamp = b.getTimestamp();
		this.maxShownTimestamp = this.minShownTimestamp + 10;

		for (ITrace2D t : this.chart.getTraces()) {
			t.removeAllPoints();
		}

		for (String metric : b.getMetrics().getNames()) {
			for (String dist : b.getMetrics().get(metric).getDistributions()
					.getNames()) {
				this.availableValues.add(metric + "." + dist);
			}
		}

		for (String metric : b.getMetrics().getNames()) {
			for (String nvl : b.getMetrics().get(metric).getNodeValues()
					.getNames()) {
				this.availableValues.add(metric + "." + nvl);
			}
		}

		// init addbox
		String[] tempValues = this.availableValues
				.toArray(new String[this.availableValues.size()]);
		tempValues = this.gatherValues(b);
		this.toggleYAxisVisibility();
		this.legend.updateAddBox(tempValues);
		this.validate();
	}

	public void updateData(BatchData b) {
		this.clearPoints();

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
								tempSortMode);
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
								tempDenominator, tempSortMode);
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
								tempDenominator, tempSortMode);
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
					this.addPoints(metric + "." + nvl, tempValues, tempSortMode);
				}
			}
		}
		updateX1Ticks();
		updateX2Ticks();
		updateY1Ticks();
		updateY2Ticks();
	}

	/** adds points sorted and normalized by dividing through denominator **/
	private void addDistributionPoints(String name, long[] values,
			long denominator, SortModeDist sort) {
		ITrace2D tempTrace = this.traces.get(name);

		switch (sort) {
		case cdf:
			double sum = 0;
			Arrays.sort(values);
			for (int i = 0; i < values.length; i++) {
				sum += (1.0 * values[i]) / denominator;
				tempTrace.addPoint(i, sum);
			}
			break;
		case distribution:
			for (int i = 0; i < values.length; i++) {
				tempTrace.addPoint(i, (1.0 * values[i]) / denominator);
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
			int denominator, SortModeDist sort) {
		ITrace2D tempTrace = this.traces.get(name);

		switch (sort) {
		case cdf:
			double sum = 0;
			Arrays.sort(values);
			for (int i = 0; i < values.length; i++) {
				sum += (1.0 * values[i]) / denominator;
				tempTrace.addPoint(i, sum);
			}
			break;
		case distribution:
			for (int i = 0; i < values.length; i++) {
				tempTrace.addPoint(i, (1.0 * values[i]) / denominator);
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
			SortModeDist sort) {
		ITrace2D tempTrace = this.traces.get(name);

		switch (sort) {
		case cdf:
			double sum = 0;
			Arrays.sort(values);
			for (int i = 0; i < values.length; i++) {
				sum += values[i];
				tempTrace.addPoint(i, sum);
			}
			break;
		case distribution:
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

	/** adds points sorted **/
	private void addPoints(String name, double[] values, SortModeNVL sort) {
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

	/** adds trace to the visualizer with default trace length **/
	public void addTrace(String name, Color color) {
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

	/** removes a trace from the chart and the traces-list **/
	public void removeTrace(String name) {
		if (this.traces.containsKey(name)) {
			this.chart.removeTrace(this.traces.get(name));
			this.traces.remove(name);
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
	}

	/** called from an item to get resorted while paused **/
	public void sortItem(String name, SortModeNVL s) {
		// TODO: when paused?
		if (this.traces.containsKey(name)) {
			this.traces.get(name).removeAllPoints();
			// this.chart.removeTrace(this.traces.get(name));
			if (this.doubleValues.containsKey(name)) {
				this.addPoints(name, this.doubleValues.get(name), s);
			}
		}
	}

	/** called from an item to get resorted while paused **/
	public void sortItem(String name, SortModeDist s) {
		System.out.println("sort dist " + name + " with " + s);
	}
}
