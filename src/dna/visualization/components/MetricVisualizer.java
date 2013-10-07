package dna.visualization.components;

import info.monitorenter.gui.chart.Chart2D;
import info.monitorenter.gui.chart.IAxis;
import info.monitorenter.gui.chart.ITrace2D;
import info.monitorenter.gui.chart.traces.Trace2DLtd;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import dna.series.data.BatchData;

public class MetricVisualizer extends JPanel {
	private int DEFAULT_TRACE_LENGTH = 10;

	private Chart2D chart;

	private ArrayList<String> availableValues;

	private HashMap<String, ITrace2D> traces;

	private JList<String> selectionListItems;
	private JPanel selectionList;
	private JScrollPane scroll;
	private JButton selectionListUpdate;

	private MetricVisualizer thisM;

	@SuppressWarnings("deprecation")
	public MetricVisualizer() {
		super();
		this.thisM = this;
		this.traces = new HashMap<String, ITrace2D>();
		this.availableValues = new ArrayList<String>();

		this.setPreferredSize(new Dimension(600, 350));
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
		yAxis.setTitle("Value");

		this.add(this.chart);

		mainConstraints.gridx = 0;
		mainConstraints.gridy = 0;

		this.selectionList = new JPanel();
		this.selectionList.setPreferredSize(new Dimension(130, 320));
		this.selectionList.setLayout(new BoxLayout(selectionList,
				BoxLayout.Y_AXIS));

		this.selectionListItems = new JList<String>();
		this.selectionList.add(this.selectionListItems);

		this.add(this.selectionList);
		this.scroll = new JScrollPane(this.selectionListItems);
		this.selectionList.add(scroll);

		this.selectionListUpdate = new JButton("Update");
		this.selectionList.add(this.selectionListUpdate);
		this.selectionListUpdate.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				thisM.updateTraces();
			}
		});
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
				try {
					this.traces.get(metric + "." + value).addPoint(
							timestampDouble,
							b.getMetrics().get(metric).getValues().get(value)
									.getValue());
				} catch (NullPointerException e) {

				}
			}

		}

		this.validate();
	}

	/** adds trace to the visualizer with choosen trace length **/
	public void addTrace(String name, int traceLength) {
		Trace2DLtd newTrace = new Trace2DLtd(traceLength);
		this.traces.put(name, newTrace);
		this.chart.addTrace(newTrace);
	}

	/** adds trace to the visualizer with default trace length **/
	public void addTrace(String name) {
		if (!this.traces.containsKey(name)) {
			Trace2DLtd newTrace = new Trace2DLtd(DEFAULT_TRACE_LENGTH);
			newTrace.setName(name);
			newTrace.setColor(this.getNextColor());
			this.traces.put(name, newTrace);
			this.chart.addTrace(newTrace);
		}
	}

	private int colorCounter = 0;
	private Color[] colors = new Color[] { new Color(255, 0, 0),
			new Color(0, 255, 0), new Color(0, 0, 255), new Color(100, 100, 0),
			new Color(100, 0, 100), new Color(0, 100, 100), new Color(0, 0, 0) };

	/** returns the next unused color **/
	public Color getNextColor() {
		if (this.colorCounter == 6)
			this.colorCounter = 0;
		return colors[(this.colorCounter++)];
	}

	/** initializes the data with the first batch **/
	public void initData(BatchData b) {
		for (String metric : b.getMetrics().getNames()) {
			for (String value : b.getMetrics().get(metric).getValues()
					.getNames()) {
				this.availableValues.add(metric + "." + value);
			}
		}

		// init metric value selection list
		String[] listItems = new String[this.availableValues.size()];
		for (int i = 0; i < this.availableValues.size(); i++) {
			listItems[i] = this.availableValues.get(i);
		}
		this.selectionListItems = new JList(listItems);
		this.selectionList.removeAll();
		this.selectionList.add(this.selectionListItems);
		this.scroll = new JScrollPane(this.selectionListItems);
		this.selectionList.add(scroll);
		this.selectionList.add(this.selectionListUpdate);
		this.validate();
	}

	/** resets the metric visualizer **/
	public void reset() {
		for (String trace : this.traces.keySet()) {
			this.traces.get(trace).removeAllPoints();
		}
	}

	/**
	 * Called by the update button. Updates all visible traces according to the
	 * selected values in the list.
	 */
	private void updateTraces() {
		List<String> selectionList = this.selectionListItems
				.getSelectedValuesList();
		for (String s : this.availableValues) {
			if (selectionList.contains(s)) {
				this.addTrace(s);
			} else {
				if (this.traces.containsKey(s)) {
					this.chart.removeTrace(this.traces.get(s));
					this.traces.remove(s);
				}
			}
		}
	}

}
