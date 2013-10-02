package dna.visualization.components;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Hashtable;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import dna.series.data.BatchData;
import dna.series.data.RunTime;
import dna.visualization.BatchHandler;

/**
 * A statsdisplay is used to monitor several statistics of a dynamic graph.
 * 
 * @author Rwilmes
 * 
 */
public class StatsDisplay extends JPanel implements ChangeListener {

	private JPanel SettingsPanel;
	private JPanel SettingsNotSpeedPanel;

	private JLabel DirectoryLabel;
	private JLabel DirectoryValue;

	private JLabel TimestampLabel;
	private JLabel TimestampValue;

	private JLabel NodesLabel;
	private JLabel NodesValue;

	private JLabel EdgesLabel;
	private JLabel EdgesValue;

	private JLabel ProgressLabel;
	private JLabel ProgressValue;

	private JSlider SpeedSlider;
	private final int SPEED_MIN = 0;
	private final int SPEED_MAX = 2000;
	private final int SPEED_INIT = 1000;

	private StatisticGroup genRuntimes;
	private StatisticGroup metRuntimes;

	private long minTimestamp;
	private long maxTimestamp;

	private BatchHandler bh;

	// constructor
	public StatsDisplay() {
		super();

		// set title and border of statistics
		TitledBorder title = BorderFactory.createTitledBorder("Statistics");
		title.setBorder(BorderFactory
				.createEtchedBorder((EtchedBorder.LOWERED)));
		this.setBorder(title);

		GridBagConstraints mainConstraints = new GridBagConstraints();
		mainConstraints.fill = GridBagConstraints.HORIZONTAL;
		this.setLayout(new GridBagLayout());
		// this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		// set general settings panel
		this.SettingsPanel = new JPanel();
		this.SettingsPanel.setName("SettingsPanel");
		this.SettingsPanel.setLayout(new GridBagLayout());
		GridBagConstraints settingsPanelConstraints = new GridBagConstraints();
		settingsPanelConstraints.fill = GridBagConstraints.HORIZONTAL;

		this.SettingsPanel.setBorder(BorderFactory
				.createEtchedBorder((EtchedBorder.LOWERED)));
		this.SettingsPanel.setBorder(BorderFactory.createTitledBorder(""));

		// adding SettingsPanel to mainPanel
		mainConstraints.gridy = 0;
		mainConstraints.gridx = 0;
		this.add(SettingsPanel, mainConstraints);

		this.SettingsNotSpeedPanel = new JPanel();
		this.SettingsNotSpeedPanel.setLayout(new BoxLayout(
				this.SettingsNotSpeedPanel, BoxLayout.X_AXIS));
		this.SettingsPanel.add(this.SettingsNotSpeedPanel,
				settingsPanelConstraints);

		JPanel settingLabels = new JPanel();
		settingLabels.setName("SettingLabels-Panel");
		settingLabels.setLayout(new BoxLayout(settingLabels, BoxLayout.Y_AXIS));
		this.SettingsNotSpeedPanel.add(settingLabels);

		JPanel settingValues = new JPanel();
		settingValues.setName("SettingValues-Panel");
		settingValues.setLayout(new BoxLayout(settingValues, BoxLayout.Y_AXIS));
		this.SettingsNotSpeedPanel.add(settingValues);

		// directory
		this.DirectoryLabel = new JLabel("Directory: ");
		this.DirectoryValue = new JLabel("./..");
		settingLabels.add(this.DirectoryLabel);
		settingValues.add(this.DirectoryValue);

		// timestamp
		this.TimestampLabel = new JLabel("Timestamp: ");
		this.TimestampValue = new JLabel("0");
		settingLabels.add(this.TimestampLabel);
		settingValues.add(this.TimestampValue);

		// amount of nodes
		this.NodesLabel = new JLabel("Nodes: ");
		this.NodesValue = new JLabel("0");
		settingLabels.add(this.NodesLabel);
		settingValues.add(this.NodesValue);

		// amount of edges
		this.EdgesLabel = new JLabel("Edges: ");
		this.EdgesValue = new JLabel("0");
		settingLabels.add(this.EdgesLabel);
		settingValues.add(this.EdgesValue);

		// progress
		this.ProgressLabel = new JLabel("Progress: ");
		this.ProgressValue = new JLabel("00.00 %");
		settingLabels.add(this.ProgressLabel);
		settingValues.add(this.ProgressValue);

		// speed slider
		this.SpeedSlider = new JSlider(JSlider.HORIZONTAL, this.SPEED_MIN,
				this.SPEED_MAX, this.SPEED_INIT);
		this.SpeedSlider.setName("SpeedSlider");
		this.SpeedSlider
				.setToolTipText("Set the playback speed for simulation in seconds");
		this.SpeedSlider.setMajorTickSpacing(500);
		this.SpeedSlider.setMinorTickSpacing(100);
		this.SpeedSlider.setPaintTicks(true);
		this.SpeedSlider.setPaintLabels(true);

		// change speed slider labels
		Hashtable<Integer, JLabel> labelTable = new Hashtable<Integer, JLabel>();
		labelTable.put(new Integer(0), new JLabel("0.0"));
		labelTable.put(new Integer(500), new JLabel("0.5"));
		labelTable.put(new Integer(1000), new JLabel("1.0"));
		labelTable.put(new Integer(1500), new JLabel("1.5"));
		labelTable.put(new Integer(2000), new JLabel("2.0"));
		this.SpeedSlider.setLabelTable(labelTable);

		// add event listener
		this.SpeedSlider.addChangeListener(this);

		settingsPanelConstraints.fill = GridBagConstraints.HORIZONTAL;
		settingsPanelConstraints.gridy = 1;
		this.SettingsPanel.add(SpeedSlider, settingsPanelConstraints);

		// general and metric runtime panels
		this.genRuntimes = new StatisticGroup("GeneralRuntimes");
		this.genRuntimes.setLayout(new BoxLayout(this.genRuntimes,
				BoxLayout.X_AXIS));
		this.metRuntimes = new StatisticGroup("MetricRuntimes");
		this.metRuntimes.setLayout(new BoxLayout(this.metRuntimes,
				BoxLayout.X_AXIS));

		// add metric runtimes panel to main panel
		mainConstraints.gridy = 1;
		mainConstraints.gridx = 0;
		this.add(this.metRuntimes, mainConstraints);

		// add general runtimes panel to main panel
		mainConstraints.gridy = 2;
		mainConstraints.gridx = 0;
		this.add(this.genRuntimes, mainConstraints);

	}

	/**
	 * Initializes the stats display.
	 * 
	 * @param b
	 *            initial batch
	 * @param directory
	 *            directory
	 * @param minTimestamp
	 *            timestamp of first batch
	 * @param maxTimestamp
	 *            timestamp of last batch
	 * 
	 * @author Rwilmes
	 */
	public void initData(BatchData b, String directory, long minTimestamp,
			long maxTimestamp) {
		this.setDirectory(directory);
		this.minTimestamp = minTimestamp;
		this.maxTimestamp = maxTimestamp;
		this.setTimestamp(minTimestamp);
		this.setNodes(b.getValues().get("nodes").getValue());
		this.setEdges(b.getValues().get("edges").getValue());

		genRuntimes.clear();
		metRuntimes.clear();

		for (RunTime rt : b.getGeneralRuntimes().getList()) {
			this.genRuntimes.addValue(rt.getName(), rt.getRuntime());
		}
		for (RunTime rt : b.getMetricRuntimes().getList()) {
			this.metRuntimes.addValue(rt.getName(), rt.getRuntime());
		}

		this.validate();
	}

	/**
	 * Updates the shown data by providing a new batch.
	 * 
	 * @param b
	 *            batch used to update
	 * 
	 * @author Rwilmes
	 */
	public void updateData(BatchData b) {
		this.setTimestamp(b.getTimestamp());
		this.setNodes(b.getValues().get("nodes").getValue());
		this.setEdges(b.getValues().get("edges").getValue());

		for (RunTime rt : b.getGeneralRuntimes().getList()) {
			this.genRuntimes.updateValue(rt.getName(), rt.getRuntime());
		}
		for (RunTime rt : b.getMetricRuntimes().getList()) {
			this.metRuntimes.updateValue(rt.getName(), rt.getRuntime());
		}

		long amount = this.maxTimestamp - this.minTimestamp;
		long pr = b.getTimestamp() - this.minTimestamp;
		double percent = (Math.floor(((1.0 * pr) / (1.0 * amount)) * 10000) / 100);
		this.setProgess(percent);
		this.validate();
	}

	/** Add a new value to the general runtime panel **/
	public void addGeneralRuntimeValue(String name, double value) {
		this.genRuntimes.addValue(name, value);
		this.validate();
	}

	/** Add a new value to the metric runtime panel **/
	public void addMetricRuntimeValue(String name, double value) {
		this.metRuntimes.addValue(name, value);
		this.validate();
	}

	/** Update a value from general runtime panel **/
	public void updateGeneralRuntimeValue(String name, double value) {
		this.genRuntimes.updateValue(name, value);
		this.validate();
	}

	/** Update a value from metric runtime panel **/
	public void updateMetricRuntimeValue(String name, double value) {
		this.metRuntimes.updateValue(name, value);
		this.validate();
	}

	public void setTimestamp(long timestamp) {
		this.TimestampValue.setText("" + timestamp);
		this.validate();
	}

	public void setDirectory(String directory) {
		this.DirectoryValue.setText(directory);
		this.validate();
	}

	public void setNodes(int nodes) {
		this.NodesValue.setText("" + nodes);
		this.validate();
	}

	public void setNodes(double nodes) {
		this.setNodes((int) nodes);
	}

	public void setEdges(int edges) {
		this.EdgesValue.setText("" + edges);
		this.validate();
	}

	public void setEdges(double edges) {
		this.setEdges((int) edges);
	}

	public void setProgess(double progress) {
		String text = "" + progress;
		if (text.charAt(1) == '.')
			text = "0" + text;
		if (text.length() == 4)
			text += "0";
		text += " %";
		this.ProgressValue.setText(text);
	}

	/** Resets the statistic display **/
	public void reset() {
		System.out.println("reset");
		this.TimestampValue.setText("" + 0);
		this.NodesValue.setText("" + 0);
		this.EdgesValue.setText("" + 0);
		this.ProgressValue.setText("00.00 %");

		this.metRuntimes.reset();
		this.genRuntimes.reset();
		this.validate();
	}

	public void setBatchHandler(BatchHandler bh) {
		this.bh = bh;
	}

	/** Listen to the speed slider. */
	public void stateChanged(ChangeEvent e) {
		JSlider source = (JSlider) e.getSource();
		if (!source.getValueIsAdjusting()) {
			if (this.bh == null)
				System.out
						.println("Warning: Attempting speed change on unknown BatchHandler");
			else
				this.bh.setSpeed((int) source.getValue());
		}
	}

}
