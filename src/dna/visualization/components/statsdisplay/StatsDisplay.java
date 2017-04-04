package dna.visualization.components.statsdisplay;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;

import javax.swing.BorderFactory;
import javax.swing.JButton;
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
import dna.series.data.Value;
import dna.util.Config;
import dna.util.Log;
import dna.visualization.MainDisplay;
import dna.visualization.config.components.StatsDisplayConfig;
import dna.visualization.config.components.StatsDisplayConfig.RunTimeConfig;

/**
 * A statsdisplay is used to monitor several statistics of a dynamic graph.
 * 
 * @author Rwilmes
 * 
 */
@SuppressWarnings("serial")
public class StatsDisplay extends JPanel implements ChangeListener {
	// components
	private JPanel settingsPanel;
	private JPanel timePanel;

	private JLabel directoryLabel;
	private JTextField directoryValue;

	private JLabel statusLabel;
	private JLabel statusValue;

	private JLabel timestampLongLabel;
	private JLabel timestampLongValue;
	private JLabel timestampDateLabel;
	private JLabel timestampDateValue;

	private JLabel batchesLabel;
	private JLabel batchesValue;

	private JLabel[] shownStatisticsLabels;
	private JLabel[] shownStatisticsValues;

	private JProgressBar ProgressBar;
	private JSlider TimeSlider;

	private JSlider SpeedSlider;
	private final int SPEED_MIN = 0;
	private final int SPEED_MAX = 2000;
	private final int SPEED_INIT = 1000;

	private RunTimeStatsGroup metRuntimes;
	private RunTimeStatsGroup genRuntimes;

	private RunTimeStatsGroup statistics;

	// timestamps
	private long minTimestamp;
	private long maxTimestamp;

	// layout constraints
	GridBagConstraints mainConstraints;
	GridBagConstraints settingsPanelConstraints;

	// registered components
	private StatsDisplay statsdis;
	private MainDisplay mainDisplay;

	// flags
	private boolean init = false;
	private boolean paused;
	private boolean timesliderAdjustingPause = false;
	private boolean liveDisplay;
	private boolean started;

	// date format
	private SimpleDateFormat dateFormat;

	// config
	StatsDisplayConfig config;

	// constructor
	public StatsDisplay(MainDisplay mainDisplay, StatsDisplayConfig config,
			boolean liveDisplay) {
		// initialization
		this.statsdis = this;
		this.paused = false;
		this.started = false;
		this.dateFormat = config.getDateFormat();
		this.mainDisplay = mainDisplay;
		this.liveDisplay = liveDisplay;
		this.config = config;

		// size
		this.setPreferredSize(config.getSize());

		// set title and border of statistics
		TitledBorder title = BorderFactory.createTitledBorder(config.getName());
		title.setBorder(BorderFactory
				.createEtchedBorder((EtchedBorder.LOWERED)));
		title.setTitleFont(new Font(
				this.mainDisplay.getDefaultFont().getName(), Font.BOLD,
				this.mainDisplay.getDefaultFont().getSize()));
		title.setTitleColor(this.mainDisplay.getDefaultFontColor());
		this.setBorder(title);

		// set layout
		this.mainConstraints = new GridBagConstraints();
		this.mainConstraints.fill = GridBagConstraints.HORIZONTAL;
		this.mainConstraints.anchor = GridBagConstraints.NORTHWEST;
		this.mainConstraints.insets = new Insets(0, 0, 5, 0);
		this.setLayout(new GridBagLayout());

		this.mainConstraints.gridx = 0;
		this.mainConstraints.gridy = 0;

		/*
		 * ADD COMPONENTS
		 */
		if (!liveDisplay && config.isAddTimePanel())
			this.addTimePanel();

		if (config.isAddSettingsPanel())
			this.addSettingsPanel(!liveDisplay && config.isAddSpeedSlider());

		if (config.isAddStatistics())
			this.addStats(config.getStatisticsConfig());

		if (config.isAddMetRuntimes())
			this.addMetricRuntimes(config.getMetricRuntimeConfig());

		if (config.isAddGenRuntimes())
			this.addGeneralRuntimes(config.getGeneralRuntimeConfig());

		// validate ui
		this.validate();
	}

	/** adds a statistics panel **/
	public void addStatistics(String[] statistics) {
		JPanel statisticsPanel = new JPanel();
		statisticsPanel.setName("Statistics");

		// set border
		TitledBorder border = BorderFactory.createTitledBorder("Statistics");
		border.setTitleFont(new Font(this.getDefaultFont().getName(),
				Font.BOLD, this.getDefaultFont().getSize()));
		border.setTitleColor(this.getDefaultFontColor());
		statisticsPanel.setBorder(border);

		// set layout
		statisticsPanel.setLayout(new GridBagLayout());
		GridBagConstraints statisticsPanelConstraints = new GridBagConstraints();
		statisticsPanelConstraints.insets = new Insets(0, 0, 0, 0);
		statisticsPanelConstraints.anchor = GridBagConstraints.NORTH;
		statisticsPanelConstraints.fill = GridBagConstraints.HORIZONTAL;
		statisticsPanelConstraints.gridx = 0;
		statisticsPanelConstraints.gridy = 0;
		statisticsPanelConstraints.weightx = 0.5;

		/*
		 * STATISTICS
		 */
		String[] shownStatistics = statistics;

		this.shownStatisticsLabels = new JLabel[shownStatistics.length];
		this.shownStatisticsValues = new JLabel[shownStatistics.length];

		for (int i = 0; i < shownStatistics.length; i++) {
			this.shownStatisticsLabels[i] = new JLabel(shownStatistics[i]
					.substring(0, 1).toUpperCase()
					+ shownStatistics[i].substring(1) + ": ");
			this.shownStatisticsLabels[i].setFont(this.mainDisplay
					.getDefaultFont());
			this.shownStatisticsLabels[i].setForeground(this.mainDisplay
					.getDefaultFontColor());
			this.shownStatisticsValues[i] = new JLabel("" + 0);
			this.shownStatisticsValues[i].setFont(this.mainDisplay
					.getDefaultFont());
			this.shownStatisticsValues[i].setForeground(this.mainDisplay
					.getDefaultFontColor());

			statisticsPanelConstraints.gridx = 0;
			statisticsPanel.add(this.shownStatisticsLabels[i],
					statisticsPanelConstraints);
			statisticsPanelConstraints.gridx = 1;
			statisticsPanel.add(this.shownStatisticsValues[i],
					statisticsPanelConstraints);
			statisticsPanelConstraints.gridy++;
		}

		// adding statistics to mainPanel
		this.mainConstraints.gridx = 0;
		this.add(statisticsPanel, this.mainConstraints);
		this.mainConstraints.gridy++;
	}

	/** adds a settingspanel containing general information and speedslider **/
	public void addSettingsPanel(boolean addSpeedSlider) {
		this.settingsPanel = new JPanel();
		this.settingsPanel.setName("SettingsPanel");
		this.settingsPanel.setPreferredSize(this.config.getSettingsPanelSize());
		this.settingsPanel.setMinimumSize(this.config.getSettingsPanelSize());

		// set border
		this.settingsPanel.setBorder(BorderFactory
				.createEtchedBorder((EtchedBorder.LOWERED)));
		this.settingsPanel.setBorder(BorderFactory.createTitledBorder(""));

		// set border
		TitledBorder border = BorderFactory.createTitledBorder("Control");
		border.setTitleFont(new Font(this.getDefaultFont().getName(),
				Font.BOLD, this.getDefaultFont().getSize()));
		border.setTitleColor(this.getDefaultFontColor());
		this.settingsPanel.setBorder(border);

		// set layout
		this.settingsPanel.setLayout(new GridBagLayout());
		this.settingsPanelConstraints = new GridBagConstraints();
		this.settingsPanelConstraints.insets = new Insets(0, 0, 0, 0);
		this.settingsPanelConstraints.anchor = GridBagConstraints.NORTH;
		this.settingsPanelConstraints.fill = GridBagConstraints.HORIZONTAL;
		this.settingsPanelConstraints.gridx = 0;
		this.settingsPanelConstraints.gridy = 0;

		/*
		 * DIRECTORY
		 */
		this.directoryLabel = new JLabel("Directory: ");
		this.directoryLabel.setFont(this.mainDisplay.getDefaultFont());
		this.directoryLabel.setForeground(this.mainDisplay
				.getDefaultFontColor());

		this.directoryValue = new JTextField("./..");
		this.directoryValue.setFont(this.mainDisplay.getDefaultFont());
		this.directoryValue.setForeground(this.mainDisplay
				.getDefaultFontColor());
		this.directoryValue.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				mainDisplay.reset();
				mainDisplay.clearLists();
				mainDisplay.setBatchHandlerDir(directoryValue.getText());
				mainDisplay.resetBatchHandler();
				if (liveDisplay)
					mainDisplay.startLiveMonitoring();
				statsdis.grabFocus();
			}
		});
		this.directoryValue.setEditable(true);

		// adding
		this.settingsPanelConstraints.weightx = 0.5;
		this.settingsPanelConstraints.gridx = 0;
		this.settingsPanelConstraints.gridy = 0;
		this.settingsPanel.add(directoryLabel, this.settingsPanelConstraints);
		this.settingsPanelConstraints.gridx = 1;
		this.settingsPanel.add(directoryValue, this.settingsPanelConstraints);
		this.settingsPanelConstraints.gridy++;

		/*
		 * STATUS PANEL
		 */
		if (this.liveDisplay) {
			this.statusLabel = new JLabel("Status: ");
			this.statusLabel.setFont(this.mainDisplay.getDefaultFont());
			this.statusLabel.setForeground(this.mainDisplay
					.getDefaultFontColor());

			this.statusValue = new JLabel("Idle");
			this.statusValue.setFont(this.mainDisplay.getDefaultFont());
			this.statusValue.setForeground(this.mainDisplay
					.getDefaultFontColor());

			// adding
			this.settingsPanelConstraints.gridx = 0;
			this.settingsPanel.add(statusLabel, this.settingsPanelConstraints);
			this.settingsPanelConstraints.gridx = 1;
			this.settingsPanel.add(statusValue, this.settingsPanelConstraints);
			this.settingsPanelConstraints.gridy++;
		}

		/*
		 * BATCHES TOTAL
		 */
		this.batchesLabel = new JLabel("Batches total: ");
		this.batchesLabel.setFont(this.mainDisplay.getDefaultFont());
		this.batchesLabel.setForeground(this.mainDisplay.getDefaultFontColor());

		this.batchesValue = new JLabel("" + 0);
		this.batchesValue.setFont(this.mainDisplay.getDefaultFont());
		this.batchesValue.setForeground(this.mainDisplay.getDefaultFontColor());

		// adding
		this.settingsPanelConstraints.gridx = 0;
		this.settingsPanel
				.add(this.batchesLabel, this.settingsPanelConstraints);
		this.settingsPanelConstraints.gridx = 1;
		this.settingsPanel
				.add(this.batchesValue, this.settingsPanelConstraints);
		this.settingsPanelConstraints.gridy++;

		/*
		 * TIMESTAMP LONG
		 */
		this.timestampLongLabel = new JLabel("Timestamp (long): ");
		this.timestampLongLabel.setFont(this.mainDisplay.getDefaultFont());
		this.timestampLongLabel.setForeground(this.mainDisplay
				.getDefaultFontColor());

		this.timestampLongValue = new JLabel("" + 0);
		this.timestampLongValue.setFont(this.mainDisplay.getDefaultFont());
		this.timestampLongValue.setForeground(this.mainDisplay
				.getDefaultFontColor());

		// adding
		this.settingsPanelConstraints.gridx = 0;
		this.settingsPanel.add(this.timestampLongLabel,
				this.settingsPanelConstraints);
		this.settingsPanelConstraints.gridx = 1;
		this.settingsPanel.add(this.timestampLongValue,
				this.settingsPanelConstraints);
		this.settingsPanelConstraints.gridy++;

		/*
		 * TIMESTAMP DATEFORMAT
		 */
		this.timestampDateLabel = new JLabel("Timestamp: ");
		this.timestampDateLabel.setFont(this.mainDisplay.getDefaultFont());
		this.timestampDateLabel.setForeground(this.mainDisplay
				.getDefaultFontColor());

		this.timestampDateValue = new JLabel("00:00:00:000");
		this.timestampDateValue.setToolTipText("Dateformat: "
				+ config.getDateFormat().toPattern());
		this.timestampDateValue.setFont(this.mainDisplay.getDefaultFont());
		this.timestampDateValue.setForeground(this.mainDisplay
				.getDefaultFontColor());

		// adding
		this.settingsPanelConstraints.gridx = 0;
		this.settingsPanel.add(this.timestampDateLabel,
				this.settingsPanelConstraints);
		this.settingsPanelConstraints.gridx = 1;
		this.settingsPanel.add(this.timestampDateValue,
				this.settingsPanelConstraints);
		this.settingsPanelConstraints.gridy++;

		/*
		 * SPEED SLIDER
		 */
		if (addSpeedSlider) {
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
			this.SpeedSlider.setFont(this.mainDisplay.getDefaultFont());
			this.SpeedSlider.setForeground(this.mainDisplay
					.getDefaultFontColor());
			// add event listener
			this.SpeedSlider.addChangeListener(this);

			// adding
			this.settingsPanelConstraints.gridwidth = 2;
			this.settingsPanelConstraints.gridx = 0;
			this.settingsPanel.add(this.SpeedSlider,
					this.settingsPanelConstraints);
			this.settingsPanelConstraints.gridy++;
			this.settingsPanelConstraints.gridwidth = 1;
		}

		// add buttons to settingspanel
		this.settingsPanelConstraints.gridx = 0;
		this.settingsPanelConstraints.gridwidth = 2;
		this.settingsPanelConstraints.ipady = 10;
		this.settingsPanel.add(this.mainDisplay.buttons,
				settingsPanelConstraints);
		this.settingsPanelConstraints.gridy++;

		// reset constraints
		this.settingsPanelConstraints.gridx = 0;
		this.settingsPanelConstraints.gridwidth = 1;
		this.settingsPanelConstraints.ipady = 0;

		// adding SettingsPanel to mainPanel
		this.mainConstraints.gridx = 0;
		this.add(this.settingsPanel, this.mainConstraints);
		this.mainConstraints.gridy++;
	}

	/** adds timepanel containing a progressbar and timeslider. */
	public void addTimePanel() {
		this.timePanel = new JPanel();
		this.timePanel.setName("timePanel");

		// set layout
		this.timePanel.setLayout(new GridBagLayout());
		GridBagConstraints timePanelConstraints = new GridBagConstraints();
		timePanelConstraints.fill = GridBagConstraints.HORIZONTAL;

		// set settingspanel border
		this.timePanel.setBorder(BorderFactory
				.createEtchedBorder((EtchedBorder.LOWERED)));
		this.timePanel.setBorder(BorderFactory.createTitledBorder(""));

		// progress bar
		this.ProgressBar = new JProgressBar();
		this.ProgressBar.setName("ProgressBar");

		timePanelConstraints.weightx = 1;
		timePanelConstraints.gridwidth = 3;
		timePanelConstraints.gridy = 0;
		timePanelConstraints.gridx = 0;
		this.timePanel.add(this.ProgressBar, timePanelConstraints);
		this.ProgressBar.setStringPainted(true);

		// time slider decrement button
		final JButton timeSliderDecrButton = new JButton("<");
		timeSliderDecrButton.setMargin(new Insets(0, 0, 0, 0));
		timeSliderDecrButton
				.setPreferredSize(config.getTimeSliderButtonsSize());
		timeSliderDecrButton.setFont(this.mainDisplay.getDefaultFont());
		timeSliderDecrButton.setForeground(this.mainDisplay
				.getDefaultFontColor());
		timeSliderDecrButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				mainDisplay.setPaused(true);
				decrementTimeSlider();
			}
		});

		// add decr button
		timePanelConstraints.gridwidth = 1;
		timePanelConstraints.gridy = 1;
		timePanelConstraints.gridx = 0;
		timePanelConstraints.weightx = 0.1;
		this.timePanel.add(timeSliderDecrButton, timePanelConstraints);

		// time slider
		this.TimeSlider = new JSlider(JSlider.HORIZONTAL, 0, 1, 0);
		this.TimeSlider.setName("TimeSlider");
		this.TimeSlider.addChangeListener(this);
		this.TimeSlider
				.setToolTipText("Move the timeslider to move to a certain point of time.");
		// add slider
		timePanelConstraints.weightx = 0.8;
		timePanelConstraints.gridy = 1;
		timePanelConstraints.gridx = 1;
		this.timePanel.add(this.TimeSlider, timePanelConstraints);

		// time slider increment button
		final JButton timeSliderIncrButton = new JButton(">");
		timeSliderIncrButton.setMargin(new Insets(0, 0, 0, 0));
		timeSliderIncrButton
				.setPreferredSize(config.getTimeSliderButtonsSize());
		timeSliderIncrButton.setFont(this.mainDisplay.getDefaultFont());
		timeSliderIncrButton.setForeground(this.mainDisplay
				.getDefaultFontColor());
		timeSliderIncrButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				mainDisplay.setPaused(true);
				incrementTimeSlider();
			}
		});

		// add incr button
		timePanelConstraints.gridwidth = 1;
		timePanelConstraints.gridy = 1;
		timePanelConstraints.gridx = 2;
		timePanelConstraints.weightx = 0.1;
		this.timePanel.add(timeSliderIncrButton, timePanelConstraints);

		this.mainConstraints.gridx = 0;
		this.add(this.timePanel, this.mainConstraints);
		this.mainConstraints.gridy++;
	}

	/** adds a statistics panel as runtimegroup **/
	public void addStats(RunTimeConfig config) {
		this.statistics = new RunTimeStatsGroup(this, config);
		this.mainConstraints.gridx = 0;
		this.add(this.statistics, this.mainConstraints);
		this.mainConstraints.gridy++;
	}

	/** adds a metric runtimes statsgroup **/
	public void addMetricRuntimes(RunTimeConfig config) {
		this.metRuntimes = new RunTimeStatsGroup(this, config);
		this.mainConstraints.gridx = 0;
		this.add(this.metRuntimes, this.mainConstraints);
		this.mainConstraints.gridy++;
	}

	/** adds a general runtimes statsgroup **/
	public void addGeneralRuntimes(RunTimeConfig config) {
		this.genRuntimes = new RunTimeStatsGroup(this, config);
		this.mainConstraints.gridx = 0;
		this.add(this.genRuntimes, this.mainConstraints);
		this.mainConstraints.gridy++;
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
		this.setTimestamp(b.getTimestamp());

		if (!this.liveDisplay) {
			this.minTimestamp = minTimestamp;
			this.maxTimestamp = maxTimestamp;

			if (minTimestamp < Integer.MIN_VALUE
					|| minTimestamp > Integer.MAX_VALUE
					|| maxTimestamp < Integer.MIN_VALUE
					|| maxTimestamp > Integer.MAX_VALUE) {
				Log.warn("Long timestamp couldn't be cast to int in StatsDisplay");
			} else {
				if (this.TimeSlider != null) {
					this.TimeSlider.setMinimum((int) minTimestamp);
					this.TimeSlider.setMaximum((int) maxTimestamp);
				}
			}
		}

		if (this.statistics != null) {
			this.statistics.clear();
			for (Value v : b.getValues().getList()) {
				this.statistics.addValue(v.getName(), v.getValue());
			}
		}

		if (this.genRuntimes != null) {
			this.genRuntimes.clear();
			for (RunTime rt : b.getGeneralRuntimes().getList()) {
				this.genRuntimes.addValue(rt.getName(), rt.getRuntime());
			}
		}

		if (this.metRuntimes != null) {
			this.metRuntimes.clear();
			for (RunTime rt : b.getMetricRuntimes().getList()) {
				this.metRuntimes.addValue(rt.getName(), rt.getRuntime());
			}
		}

		this.validate();
		this.init = true;
	}

	/** called in case of liveDisplay **/
	public void initData(BatchData b, String directory) {
		this.initData(b, directory, 0, 0);
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
		this.init = false;
		this.setTimestamp(b.getTimestamp());

		if (this.statistics != null) {
			for (Value v : b.getValues().getList()) {
				this.statistics.updateValue(v.getName(), v.getValue());
			}
		}

		if (this.genRuntimes != null) {
			for (RunTime rt : b.getGeneralRuntimes().getList()) {
				this.genRuntimes.updateValue(rt.getName(), rt.getRuntime());
			}
		}
		if (this.metRuntimes != null) {
			for (RunTime rt : b.getMetricRuntimes().getList()) {
				this.metRuntimes.updateValue(rt.getName(), rt.getRuntime());
			}
		}

		if (this.liveDisplay) {
			this.incrementBatchesCount();
		} else {
			this.setBatchesCount(this.mainDisplay
					.getAmountOfPreviousTimestamps(b.getTimestamp()) + 1);

			if (b.getTimestamp() == this.maxTimestamp) {
				this.setProgess(100.0);
				this.setStopped();
			} else {
				if (this.ProgressBar != null) {
					long amount = this.maxTimestamp - this.minTimestamp;
					long pr = b.getTimestamp() - this.minTimestamp;
					double percent = (Math
							.floor(((1.0 * pr) / (1.0 * amount)) * 10000) / 100);
					this.setProgess(percent);
				}
			}
			this.setTimeSlider(b.getTimestamp());
		}

		this.init = true;
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
		this.timestampLongValue.setText("" + timestamp);
		
		long tempTimestamp = timestamp;
		if (Config.getBoolean("VISUALIZATION_TIMESTAMP_AS_SECOND")) {
			tempTimestamp= (timestamp+ Config.getInt("VISUALIZATION_TIMESTAMP_OFFSET")) * 1000;
		}
		
		this.timestampDateValue.setText(this.dateFormat.format(new Date(
				tempTimestamp)));
		this.validate();
	}

	/** Sets the shown directory **/
	public void setDirectory(String directory) {
		if (this.directoryValue != null) {
			this.directoryValue.setText(directory);
			this.validate();
		}
	}

	/** Returns the shown directory **/
	public String getDirectory() {
		return this.directoryValue.getText();
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

	/** increments the batches count **/
	public void incrementBatchesCount() {
		int temp = Integer.parseInt(this.batchesValue.getText()) + 1;
		this.batchesValue.setText("" + temp);
	}

	/** sets the batches count **/
	public void setBatchesCount(int value) {
		this.batchesValue.setText("" + value);
	}

	/**
	 * Called when the program starts to prevent changes on sensitive data while
	 * running
	 **/
	public void setStarted() {
		if (!this.liveDisplay)
			this.directoryValue.setEditable(false);
		this.started = true;
		this.validate();
		this.repaint();
	}

	/**
	 * Called when the program stops to make changes on sensitive data available
	 **/
	public void setStopped() {
		this.directoryValue.setEditable(true);
		if (this.liveDisplay)
			this.statusValue.setText("Idle");
		this.validate();
		this.repaint();
	}

	/** Resets the statistic display **/
	public void reset() {
		init = false;
		this.setTimestamp(0);

		this.batchesValue.setText("" + 0);

		if (this.statistics != null)
			this.statistics.reset();
		if (this.metRuntimes != null)
			this.metRuntimes.reset();
		if (this.genRuntimes != null)
			this.genRuntimes.reset();

		if (!this.liveDisplay) {
			this.ProgressBar.setValue(0);
			this.TimeSlider.setMaximum(1);
			this.TimeSlider.setMinimum(0);
			this.TimeSlider.setValue(0);
		}

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
				if (init) {
					if (source.getValueIsAdjusting())
						if (!this.mainDisplay.isPaused())
							this.timesliderAdjustingPause = true;
					this.mainDisplay.setPaused(true);
					if (!source.getValueIsAdjusting()) {
						if (init) {
							this.mainDisplay.setTime((int) source.getValue());
							if (timesliderAdjustingPause) {
								this.timesliderAdjustingPause = false;
								this.mainDisplay.setPaused(false);
							}
						}
					}
				}
			}
		}
	}

	/** Called when the UI gets pause/unpaused **/
	public void togglePause() {
		this.paused = !this.paused;
		if (this.liveDisplay) {
			if (this.paused) {
				this.statusValue.setText("Paused..");
			} else {
				if (this.started)
					this.statusValue.setText("Waiting for Batches..");
				else
					this.statusValue.setText("Idle");
			}
		}
	}

	/** sets the timeslider to the desired value **/
	public void setTimeSlider(long timestamp) {
		if (timestamp < Integer.MIN_VALUE || timestamp > Integer.MAX_VALUE) {
			Log.warn("Long timestamp couldn't be cast to int in StatsDisplay");
		} else {
			if (this.TimeSlider != null) {
				if (this.TimeSlider.getMinimum() <= timestamp
						&& timestamp <= this.TimeSlider.getMaximum())
					this.TimeSlider.setValue((int) timestamp);
			}
		}
	}

	/** increments the timeslider **/
	public void incrementTimeSlider() {
		if (this.TimeSlider.getValue() < this.TimeSlider.getMaximum()) {
			long timestamp = this.mainDisplay
					.getNextTimestamp((long) this.TimeSlider.getValue());
			if (timestamp < Integer.MIN_VALUE || timestamp > Integer.MAX_VALUE) {
				Log.warn("Long timestamp couldn't be cast to int in StatsDisplay");
			} else {
				if (this.TimeSlider.getMinimum() <= timestamp
						&& timestamp <= this.TimeSlider.getMaximum())
					this.TimeSlider.setValue((int) timestamp);
			}
		}
	}

	/** decrements the time slider **/
	public void decrementTimeSlider() {
		if (this.TimeSlider.getValue() > this.TimeSlider.getMinimum()) {
			long timestamp = this.mainDisplay
					.getPreviousTimestamp((long) this.TimeSlider.getValue());
			if (timestamp < Integer.MIN_VALUE || timestamp > Integer.MAX_VALUE) {
				Log.warn("Long timestamp couldn't be cast to int in StatsDisplay");
			} else {
				if (this.TimeSlider.getMinimum() <= timestamp
						&& timestamp <= this.TimeSlider.getMaximum())
					this.TimeSlider.setValue((int) timestamp);
			}
		}
	}

	/** Returns the default font. **/
	public Font getDefaultFont() {
		return this.mainDisplay.getDefaultFont();
	}

	/** Returns the default font color. **/
	public Color getDefaultFontColor() {
		return this.mainDisplay.getDefaultFontColor();
	}

	/**
	 * Sets the status message in the statsdisplay.
	 * 
	 * Note: For livedisplay only!
	 */
	public void setStatusMessage(String msg) {
		if (this.statusValue != null) {
			this.statusValue.setText(msg);
		}
	}
}
