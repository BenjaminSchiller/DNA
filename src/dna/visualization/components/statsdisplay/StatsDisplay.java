package dna.visualization.components.statsdisplay;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import dna.series.data.BatchData;
import dna.series.data.RunTime;
import dna.util.Log;
import dna.visualization.GuiOptions;
import dna.visualization.MainDisplay;

/**
 * A statsdisplay is used to monitor several statistics of a dynamic graph.
 * 
 * @author Rwilmes
 * 
 */
@SuppressWarnings("serial")
public class StatsDisplay extends JPanel implements ChangeListener {
	// settingspanel
	private JPanel SettingsPanel;
	private JPanel SettingsNotSpeedPanel;

	// direcotry
	private JLabel DirectoryLabel;
	private JTextField DirectoryValue;

	// timestamp
	private JLabel TimestampLabel;
	private JLabel TimestampValue;

	// nodes
	private JLabel NodesLabel;
	private JLabel NodesValue;

	// edges
	private JLabel EdgesLabel;
	private JLabel EdgesValue;

	// progressbar
	private JProgressBar ProgressBar;
	private JSlider TimeSlider;

	// slider
	private JSlider SpeedSlider;
	private final int SPEED_MIN = 0;
	private final int SPEED_MAX = 2000;
	private final int SPEED_INIT = 1000;

	// statsgroups
	private StatsGroup genRuntimes;
	private StatsGroup metRuntimes;

	// timestamps
	private long minTimestamp;
	private long maxTimestamp;

	// registered components
	private StatsDisplay statsdis;
	private MainDisplay mainDisplay;

	// init flag
	private boolean init = false;

	// paused flag
	private boolean paused;

	// constructor
	public StatsDisplay(Dimension size) {
		// initialization
		this.statsdis = this;
		this.paused = true;
		System.out.println("StatsDisplay Size: " + size.toString());
		// size
		size = new Dimension(280, 350);
		this.setPreferredSize(size);

		// set title and border of statistics
		TitledBorder title = BorderFactory.createTitledBorder("Statistics");
		title.setBorder(BorderFactory
				.createEtchedBorder((EtchedBorder.LOWERED)));
		title.setTitleFont(GuiOptions.defaultFontBorders);
		title.setTitleColor(GuiOptions.defaultFontBordersColor);
		this.setBorder(title);

		// set layout
		GridBagConstraints mainConstraints = new GridBagConstraints();
		mainConstraints.fill = GridBagConstraints.HORIZONTAL;
		this.setLayout(new GridBagLayout());

		// set general settings panel
		this.SettingsPanel = new JPanel();
		this.SettingsPanel.setName("SettingsPanel");
		// this.SettingsPanel.setPreferredSize(new Dimension(245, 500));
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
		settingsPanelConstraints.gridy = 0;
		settingsPanelConstraints.gridx = 0;
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
		this.DirectoryLabel.setFont(GuiOptions.defaultFont);
		this.DirectoryValue = new JTextField("./..");
		this.DirectoryValue.setFont(GuiOptions.defaultFont);
		this.DirectoryValue.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				mainDisplay.reset();
				mainDisplay.clearLists();
				mainDisplay.setBatchHandlerDir(DirectoryValue.getText());
				mainDisplay.resetBatchHandler();
				statsdis.grabFocus();
			}
		});
		this.DirectoryValue.setEditable(true);

		settingLabels.add(this.DirectoryLabel);
		settingValues.add(this.DirectoryValue);

		// timestamp
		this.TimestampLabel = new JLabel("Timestamp: ");
		this.TimestampLabel.setFont(GuiOptions.defaultFont);
		this.TimestampValue = new JLabel("0");
		this.TimestampValue.setFont(GuiOptions.defaultFont);
		settingLabels.add(this.TimestampLabel);
		settingValues.add(this.TimestampValue);

		// amount of nodes
		this.NodesLabel = new JLabel("Nodes: ");
		this.NodesLabel.setFont(GuiOptions.defaultFont);
		this.NodesValue = new JLabel("0");
		this.NodesValue.setFont(GuiOptions.defaultFont);
		settingLabels.add(this.NodesLabel);
		settingValues.add(this.NodesValue);

		// amount of edges
		this.EdgesLabel = new JLabel("Edges: ");
		this.EdgesLabel.setFont(GuiOptions.defaultFont);
		this.EdgesValue = new JLabel("0");
		this.EdgesValue.setFont(GuiOptions.defaultFont);
		settingLabels.add(this.EdgesLabel);
		settingValues.add(this.EdgesValue);

		// progress bar
		this.ProgressBar = new JProgressBar();
		this.ProgressBar.setName("ProgressBar");
		settingsPanelConstraints.gridy = 1;
		settingsPanelConstraints.gridx = 0;
		this.SettingsPanel.add(this.ProgressBar, settingsPanelConstraints);
		this.ProgressBar.setStringPainted(true);

		// time slider
		this.TimeSlider = new JSlider(JSlider.HORIZONTAL, 0, 1, 0);
		this.TimeSlider.setName("TimeSlider");
		this.TimeSlider.addChangeListener(this);
		// this.TimeSlider.setToolTipText("Adjust to ");
		settingsPanelConstraints.gridy = 2;
		settingsPanelConstraints.gridx = 0;
		this.SettingsPanel.add(this.TimeSlider, settingsPanelConstraints);

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

		settingsPanelConstraints.fill = GridBagConstraints.VERTICAL;
		settingsPanelConstraints.gridy = 3;
		settingsPanelConstraints.gridx = 0;
		this.SettingsPanel.add(SpeedSlider, settingsPanelConstraints);

		// general and metric runtime panels
		this.genRuntimes = new StatsGroup("GeneralRuntimes", new Dimension(150,
				300));
		// this.genRuntimes.setLayout(new BoxLayout(this.genRuntimes,
		// BoxLayout.X_AXIS));
		this.genRuntimes.setPreferredSize(new Dimension(260, 105));
		this.metRuntimes = new StatsGroup("MetricRuntimes", new Dimension(150,
				300));
		this.metRuntimes.setPreferredSize(new Dimension(260, 55));
		this.metRuntimes.setMinimumSize(new Dimension(280, 55));
		// this.metRuntimes.setLayout(new BoxLayout(this.metRuntimes,
		// BoxLayout.X_AXIS));

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
		this.init = false;
		this.setDirectory(directory);
		this.minTimestamp = minTimestamp;
		this.maxTimestamp = maxTimestamp;
		this.setTimestamp(minTimestamp);
		this.setNodes(b.getValues().get("nodes").getValue());
		this.setEdges(b.getValues().get("edges").getValue());

		if (minTimestamp < Integer.MIN_VALUE
				|| minTimestamp > Integer.MAX_VALUE
				|| maxTimestamp < Integer.MIN_VALUE
				|| maxTimestamp > Integer.MAX_VALUE) {
			Log.warn("Long timestamp couldn't be cast to int in StatsDisplay");
		} else {
			this.TimeSlider.setMinimum((int) minTimestamp);
			this.TimeSlider.setMaximum((int) maxTimestamp);
		}

		genRuntimes.clear();
		metRuntimes.clear();

		for (RunTime rt : b.getGeneralRuntimes().getList()) {
			this.genRuntimes.addValue(rt.getName(), rt.getRuntime());
		}
		for (RunTime rt : b.getMetricRuntimes().getList()) {
			this.metRuntimes.addValue(rt.getName(), rt.getRuntime());
		}

		this.validate();
		this.init = true;
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
		if (b.getTimestamp() == this.maxTimestamp) {
			this.setProgess(100.0);
			this.setStopped();
		} else {
			long amount = this.maxTimestamp - this.minTimestamp;
			long pr = b.getTimestamp() - this.minTimestamp;
			double percent = (Math.floor(((1.0 * pr) / (1.0 * amount)) * 10000) / 100);
			this.setProgess(percent);
		}
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

	/** Sets the shown timestamp **/
	public void setTimestamp(long timestamp) {
		this.TimestampValue.setText("" + timestamp);
		this.validate();
	}

	/** Sets the shown directory **/
	public void setDirectory(String directory) {
		this.DirectoryValue.setText(directory);
		this.validate();
	}

	/** Returns the shown directory **/
	public String getDirectory() {
		return this.DirectoryValue.getText();
	}

	/** Sets the shown amount of nodes **/
	public void setNodes(int nodes) {
		this.NodesValue.setText("" + nodes);
		this.validate();
	}

	/** Sets the shown amount of nodes **/
	public void setNodes(double nodes) {
		this.setNodes((int) nodes);
	}

	/** Sets the shown amount of edges **/
	public void setEdges(int edges) {
		this.EdgesValue.setText("" + edges);
		this.validate();
	}

	/** Sets the shown amount of edges **/
	public void setEdges(double edges) {
		this.setEdges((int) edges);
	}

	/** Sets the shown amount of progress **/
	public void setProgess(double progress) {
		this.ProgressBar.setValue((int) Math.floor(progress));
		this.validate();
	}

	/** Registers the parent maindisplay **/
	public void setParent(MainDisplay mainDisplay) {
		this.mainDisplay = mainDisplay;
	}

	/**
	 * Called when the program starts to prevent changes on sensitive data while
	 * running
	 **/
	public void setStarted() {
		this.DirectoryValue.setEditable(false);
		this.validate();
		this.repaint();
	}

	/**
	 * Called when the program stops to make changes on sensitive data available
	 **/
	public void setStopped() {
		this.DirectoryValue.setEditable(true);
		this.validate();
		this.repaint();
	}

	/** Resets the statistic display **/
	public void reset() {
		init = false;
		this.TimestampValue.setText("" + 0);
		this.NodesValue.setText("" + 0);
		this.EdgesValue.setText("" + 0);
		this.ProgressBar.setValue(0);

		this.metRuntimes.reset();
		this.genRuntimes.reset();
		this.TimeSlider.setMaximum(1);
		this.TimeSlider.setMinimum(0);
		this.TimeSlider.setValue(0);
		this.validate();
		init = true;
	}

	/** Gets called on mouse release after a slider has been moved **/
	public void stateChanged(ChangeEvent e) {
		if (e.getSource() instanceof JSlider) {
			JSlider source = (JSlider) e.getSource();
			// check if it was the speed slider
			if (source.getName().equals("SpeedSlider")) {
				if (!source.getValueIsAdjusting()) {
					this.mainDisplay.setBatchHandlerSpeed((int) source
							.getValue());
				}
			}
			// check if it was the time slider
			if (source.getName().equals("TimeSlider")) {
				if (!source.getValueIsAdjusting()) {
					if (init) {
						this.mainDisplay.setPaused(true);
						this.mainDisplay.setTime((int) source.getValue());
					}
				}
			}
		}
	}

	/** Called when the UI gets pause/unpaused **/
	public void togglePause() {
		this.paused = !this.paused;
	}

	/** sets the timeslider to the desired value **/
	public void setTimeSlider(long timestamp) {
		if (timestamp < Integer.MIN_VALUE || timestamp > Integer.MAX_VALUE) {
			Log.warn("Long timestamp couldn't be cast to int in StatsDisplay");
		} else {
			if (this.TimeSlider.getMinimum() <= timestamp
					&& timestamp <= this.TimeSlider.getMaximum())
				this.TimeSlider.setValue((int) timestamp);
		}
	}

}
