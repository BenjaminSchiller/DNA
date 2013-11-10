package dna.visualization.components.metricvisualizer;

import info.monitorenter.gui.chart.IAxis;
import info.monitorenter.gui.chart.ITrace2D;
import info.monitorenter.gui.chart.ITracePoint2D;
import info.monitorenter.gui.chart.traces.Trace2DLtd;
import info.monitorenter.gui.chart.traces.Trace2DSimple;
import info.monitorenter.gui.chart.traces.painters.TracePainterDisc;
import info.monitorenter.gui.chart.traces.painters.TracePainterFill;
import info.monitorenter.util.Range;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import dna.series.data.BatchData;
import dna.series.data.MetricData;
import dna.series.data.RunTime;
import dna.series.data.Value;
import dna.util.Config;
import dna.visualization.MainDisplay;

public class MetricVisualizer extends Visualizer {
	// fonts
	private Font defaultFontBorders = MainDisplay.defaultFontBorders;
	
	// available values and traces
	private ArrayList<String> availableValues;
	private HashMap<String, ITrace2D> traces;

	// constructor
	public MetricVisualizer() {
		// initialization
		this.traces = new HashMap<String, ITrace2D>();
		this.availableValues = new ArrayList<String>();
		
		// set title and border of the metric visualizer
		TitledBorder title = BorderFactory
				.createTitledBorder("Metric Visualizer");
		title.setBorder(BorderFactory
				.createEtchedBorder((EtchedBorder.LOWERED)));
		title.setTitleFont(this.defaultFontBorders);
		this.setBorder(title);

		// add menu bar
		super.addMenuBar(new Dimension(this.defaultMenuBarSize), true, true,
				true, true, true);

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
		System.out.println(super.isViewPortFixed());
		if (Config.getBoolean("DEFAULT_TRACE_MODE_LTD") && !this.FIXED_VIEWPORT) {
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
		this.updateYTicks();
	}

	/** adds trace to the visualizer with default trace length **/
	public void addTrace(String name, Color color) {
		if (!this.traces.containsKey(name)) {
			if (Config.getBoolean("DEFAULT_TRACE_MODE_LTD")) {
				Trace2DLtd newTrace = new Trace2DLtd(
						Config.getInt("DEFAULT_TRACE_LENGTH"));
				newTrace.setColor(color);
				this.traces.put(name, newTrace);
				this.chart.addTrace(newTrace);

				if (Config.getBoolean("DEFAULT_PAINT_LINESPOINT"))
					newTrace.addTracePainter(new TracePainterDisc(Config
							.getInt("DEFAULT_LINESPOINT_SIZE")));
				if (Config.getBoolean("DEFAULT_PAINT_FILL"))
					newTrace.addTracePainter(new TracePainterFill(this.chart));
			} else {
				Trace2DSimple newTrace = new Trace2DSimple();
				newTrace.setColor(color);
				this.traces.put(name, newTrace);
				this.chart.addTrace(newTrace);

				if (Config.getBoolean("DEFAULT_PAINT_LINESPOINT"))
					newTrace.addTracePainter(new TracePainterDisc(Config
							.getInt("DEFAULT_LINESPOINT_SIZE")));
				if (Config.getBoolean("DEFAULT_PAINT_FILL"))
					newTrace.addTracePainter(new TracePainterFill(this.chart));
			}
		}
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
	
	/** removes a trace from the chart and the traces-list **/
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

}
