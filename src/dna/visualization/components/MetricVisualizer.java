package dna.visualization.components;

import info.monitorenter.gui.chart.Chart2D;
import info.monitorenter.gui.chart.IAxis;
import info.monitorenter.gui.chart.ITrace2D;
import info.monitorenter.gui.chart.ITracePainter;
import info.monitorenter.gui.chart.axis.AAxis;
import info.monitorenter.gui.chart.axis.AxisLinear;
import info.monitorenter.gui.chart.traces.Trace2DLtd;
import info.monitorenter.gui.chart.traces.Trace2DSimple;
import info.monitorenter.gui.chart.traces.painters.TracePainterDisc;
import info.monitorenter.gui.chart.traces.painters.TracePainterFill;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
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
	private int TRACE_LENGTH;
	private Boolean TRACE_MODE_LTD;

	private Boolean DEFAULT_TRACE_MODE_LTD = true;
	private Boolean DEFAULT_PAINT_LINESPOINT = true;
	private Boolean DEFAULT_PAINT_FILL = false;

	private Chart2D chart;
	private IAxis yRight;
	private IAxis yLeft;
	private IAxis xAxis;

	private ArrayList<String> availableValues;

	private HashMap<String, ITrace2D> traces;
	private HashMap<String, String> tracesYAxis;

	private MetricVisualizer thisM;

	private int linespointSize = 5;

	private Legend legend;

	private JPanel menuBar;

	private JComboBox<String> intervalBox;
	private String[] intervalOptions = { "Trace length", "- show all",
			"-fixed length: 10", "-fixed length: 20", "-fixed length: 30",
			"-fixed length: 40", "-fixed length: 50" };

	@SuppressWarnings("deprecation")
	public MetricVisualizer() {
		super();
		this.thisM = this;
		this.TRACE_MODE_LTD = DEFAULT_TRACE_MODE_LTD;
		this.TRACE_LENGTH = DEFAULT_TRACE_LENGTH;

		this.traces = new HashMap<String, ITrace2D>();
		this.availableValues = new ArrayList<String>();

		this.setPreferredSize(new Dimension(670, 385));
		// set title and border of the metric visualizer
		TitledBorder title = BorderFactory
				.createTitledBorder("Metric Visualizer");
		title.setBorder(BorderFactory
				.createEtchedBorder((EtchedBorder.LOWERED)));
		this.setBorder(title);

		GridBagConstraints mainConstraints = new GridBagConstraints();
		mainConstraints.fill = GridBagConstraints.HORIZONTAL;
		this.setLayout(new GridBagLayout());

		this.chart = new Chart2D();

		this.chart.setPreferredSize(new Dimension(450, 320));

		this.xAxis = this.chart.getAxisX();
		this.yLeft = this.chart.getAxisY();
		this.xAxis.setTitle("Timestamp");
		this.yLeft.setTitle("");

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
		this.menuBar.setPreferredSize(new Dimension(450, 25));
		this.menuBar.setBorder(BorderFactory
				.createEtchedBorder((EtchedBorder.LOWERED)));
		this.add(this.menuBar, mainConstraints);

		GridBagConstraints menuBarConstraints = new GridBagConstraints();

		// intervalBox dropdown menu
		this.intervalBox = new JComboBox<String>(intervalOptions);
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
		menuBarConstraints.weightx = 0;
		menuBarConstraints.gridx = 0;
		menuBarConstraints.gridy = 0;
		this.menuBar.add(this.intervalBox, menuBarConstraints);

		// add dummy panel
		menuBarConstraints.gridx = 1;
		menuBarConstraints.weightx = 0.1;
		this.menuBar.add(new JPanel(), menuBarConstraints);
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
			}
			if (m.substring(0, 2).equals("-f")) {
				this.TRACE_MODE_LTD = true;
				this.updateTraceLength(Integer.parseInt(m.substring(15)));
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
			if (this.TRACE_MODE_LTD) {
				Trace2DLtd newTrace = new Trace2DLtd(this.TRACE_LENGTH);
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
		for (String metric : b.getMetrics().getNames()) {
			for (String value : b.getMetrics().get(metric).getValues()
					.getNames()) {
				this.availableValues.add(metric + "." + value);
			}
		}
		for (String runtime : b.getGeneralRuntimes().getNames()) {
			// graphGeneration runtime will be ignored cause it is only present
			// in the initial batch
			if (!runtime.equals("graphGeneration"))
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
		tempValues = this.gatherValues(b);
		this.toggleYAxisVisibility();
		this.legend.updateAddBox(tempValues);
		this.validate();
	}

	/** resets the metric visualizer **/
	public void reset() {
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
}
