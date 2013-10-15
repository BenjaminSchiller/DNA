package dna.visualization.components;

import info.monitorenter.gui.chart.Chart2D;
import info.monitorenter.gui.chart.IAxis;
import info.monitorenter.gui.chart.ITrace2D;
import info.monitorenter.gui.chart.ITracePainter;
import info.monitorenter.gui.chart.traces.Trace2DLtd;
import info.monitorenter.gui.chart.traces.painters.TracePainterDisc;
import info.monitorenter.gui.chart.traces.painters.TracePainterFill;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import dna.series.data.BatchData;
import dna.series.data.MetricData;
import dna.series.data.RunTime;
import dna.series.data.Value;
import dna.visualization.components.legend.Legend;

public class MetricVisualizer extends JPanel {
	private int DEFAULT_TRACE_LENGTH = 20;

	private Chart2D chart;

	private ArrayList<String> availableValues;

	private HashMap<String, ITrace2D> traces;

	private MetricVisualizer thisM;

	private Boolean DEFAULT_PAINT_LINESPOINT = true;
	private int linespointSize = 5;
	private Boolean DEFAULT_PAINT_FILL = false;

	private Legend legend;

	@SuppressWarnings("deprecation")
	public MetricVisualizer() {
		super();
		this.thisM = this;
		this.traces = new HashMap<String, ITrace2D>();
		this.availableValues = new ArrayList<String>();

		this.setPreferredSize(new Dimension(670, 350));
		// set title and border of statistics
		TitledBorder title = BorderFactory
				.createTitledBorder("Metric Visualizer");
		title.setBorder(BorderFactory
				.createEtchedBorder((EtchedBorder.LOWERED)));
		this.setBorder(title);

		GridBagConstraints mainConstraints = new GridBagConstraints();
		mainConstraints.fill = GridBagConstraints.HORIZONTAL;
		this.setLayout(new GridBagLayout());
		// this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

		this.chart = new Chart2D();

		this.chart.setPreferredSize(new Dimension(450, 320));
		IAxis xAxis = this.chart.getAxisX();
		IAxis yAxis = this.chart.getAxisY();
		xAxis.setTitle("Timestamp");
		yAxis.setTitle("");

		this.chart.setPaintLabels(false);
		this.add(this.chart);

		mainConstraints.gridx = 0;
		mainConstraints.gridy = 0;

		this.legend = new Legend(this);
		this.add(this.legend);
	}

	/**
	 * 
	 * @param b
	 */
	public void updateData(BatchData b) {
		long timestamp = b.getTimestamp();
		double timestampDouble = timestamp;

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
	}

	/** adds trace to the visualizer with default trace length **/
	public void addTrace(String name, Color color) {
		if (!this.traces.containsKey(name)) {
			Trace2DLtd newTrace = new Trace2DLtd(DEFAULT_TRACE_LENGTH);
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
		for (String metric : b.getMetrics().getNames()) {
			for (String value : b.getMetrics().get(metric).getValues()
					.getNames()) {
				this.availableValues.add(metric + "." + value);
			}
		}
		for (String runtime : b.getGeneralRuntimes().getNames()) {
			this.availableValues.add("general runtime." + runtime);
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
		// this.betweenUpdate(tempValues);
		tempValues = this.gatherValues(b);
		this.legend.updateAddBox(tempValues);
		this.validate();
	}

	/** resets the metric visualizer **/
	public void reset() {
		for (String trace : this.traces.keySet()) {
			this.traces.get(trace).removeAllPoints();
		}
	}

	public void removeTrace(String name) {
		if (this.traces.containsKey(name)) {
			this.chart.removeTrace(this.traces.get(name));
			this.traces.remove(name);
		}
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

}
