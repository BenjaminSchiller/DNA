package dna.visualization.components;

import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.DefaultCaret;

import dna.util.Log;
import dna.visualization.MainDisplay;
import dna.visualization.config.components.LogDisplayConfig;

/**
 * A java swing component to display a log in live display visualization mode.
 * 
 * @author Rwilmes
 * @date 03.02.2014
 */
public class LogDisplay extends JPanel implements Runnable {

	// variables
	private String dir;
	private long updateInterval;
	private Thread t;
	private boolean running;
	private String name;

	// components
	private JScrollPane logAreaScrollPane;
	private JTextArea logTextArea;

	private JPanel checkBoxPanel;

	private JLabel debugLabel;
	private JCheckBox debugCheckBox;

	private JLabel errorLabel;
	private JCheckBox errorCheckBox;

	private JLabel infoLabel;
	private JCheckBox infoCheckBox;

	private JLabel warningLabel;
	private JCheckBox warningCheckBox;

	// flags
	private boolean showInfo;
	private boolean showWarning;
	private boolean showError;
	private boolean showDebug;

	// constructor
	public LogDisplay(MainDisplay mainDisplay, LogDisplayConfig config) {
		// init
		this.dir = config.getDir();
		this.name = config.getName();
		this.updateInterval = config.getUpdateInterval();

		this.showInfo = config.isInfoShown();
		this.showWarning = config.isWarningShown();
		this.showError = config.isErrorShown();
		this.showDebug = config.isDebugShown();

		// set title and border of the metric visualizer
		TitledBorder title = BorderFactory.createTitledBorder(config.getName()
				+ " on: " + dir);
		title.setBorder(BorderFactory
				.createEtchedBorder((EtchedBorder.LOWERED)));
		title.setTitleFont(new Font(mainDisplay.getDefaultFont().getName(),
				Font.BOLD, mainDisplay.getDefaultFont().getSize()));
		title.setTitleColor(mainDisplay.getDefaultFontColor());
		this.setBorder(title);

		// layout
		this.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		this.logTextArea = new JTextArea();
		this.logTextArea.setFont(config.getLogFont());
		this.logTextArea.setForeground(config.getLogFontColor());

		DefaultCaret caret = (DefaultCaret) this.logTextArea.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

		this.logAreaScrollPane = new JScrollPane(this.logTextArea);
		this.logAreaScrollPane.setPreferredSize(config.getTextFieldSize());

		c.gridx = 0;
		c.gridy = 0;
		this.add(this.logAreaScrollPane, c);

		// check box panel
		this.checkBoxPanel = new JPanel();
		this.checkBoxPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 0, 0));
		this.checkBoxPanel
				.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		this.checkBoxPanel.setPreferredSize(new Dimension(config
				.getTextFieldSize().width, 20));

		c.gridx = 0;
		c.gridy = 1;
		this.add(checkBoxPanel, c);

		// labels
		this.infoLabel = new JLabel("Info:");
		this.infoLabel.setFont(mainDisplay.getDefaultFont());
		this.infoLabel.setForeground(mainDisplay.getDefaultFontColor());

		this.warningLabel = new JLabel("Warn:");
		this.warningLabel.setFont(mainDisplay.getDefaultFont());
		this.warningLabel.setForeground(mainDisplay.getDefaultFontColor());

		this.errorLabel = new JLabel("Error:");
		this.errorLabel.setFont(mainDisplay.getDefaultFont());
		this.errorLabel.setForeground(mainDisplay.getDefaultFontColor());

		this.debugLabel = new JLabel("Debug:");
		this.debugLabel.setFont(mainDisplay.getDefaultFont());
		this.debugLabel.setForeground(mainDisplay.getDefaultFontColor());

		// check boxes
		this.infoCheckBox = new JCheckBox("", this.showInfo);
		this.infoCheckBox.setToolTipText("Check to show info messages.");
		this.infoCheckBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				if (infoCheckBox.isSelected())
					showInfo = true;
				else
					showInfo = false;
			}
		});

		this.warningCheckBox = new JCheckBox("", this.showWarning);
		this.warningCheckBox.setToolTipText("Check to show warnings.");
		this.warningCheckBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				if (warningCheckBox.isSelected())
					showWarning = true;
				else
					showWarning = false;
			}
		});

		this.errorCheckBox = new JCheckBox("", this.showError);
		this.errorCheckBox.setToolTipText("Check to show error messages.");
		this.errorCheckBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				if (errorCheckBox.isSelected())
					showError = true;
				else
					showError = false;
			}
		});

		this.debugCheckBox = new JCheckBox("", this.showDebug);
		this.debugCheckBox.setToolTipText("Check to show debug messages.");
		this.debugCheckBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				if (debugCheckBox.isSelected())
					showDebug = true;
				else
					showDebug = false;
			}
		});

		// add to panel
		this.checkBoxPanel.add(this.debugCheckBox);
		this.checkBoxPanel.add(this.debugLabel);
		this.checkBoxPanel.add(new JPanel());

		this.checkBoxPanel.add(this.errorCheckBox);
		this.checkBoxPanel.add(this.errorLabel);
		this.checkBoxPanel.add(new JPanel());

		this.checkBoxPanel.add(this.warningCheckBox);
		this.checkBoxPanel.add(this.warningLabel);
		this.checkBoxPanel.add(new JPanel());

		this.checkBoxPanel.add(this.infoCheckBox);
		this.checkBoxPanel.add(this.infoLabel);

		this.validate();
	}

	/** Run method tailing the log file **/
	public void run() {
		Thread thisThread = Thread.currentThread();
		this.running = true;

		try {
			// check if dir is present
			File f = new File(this.dir);
			if (!f.exists() || f.isDirectory()) {
				Log.info("LogDisplay " + this.name + ": Logfile '" + this.dir
						+ "' is not present, waiting for it to appear.");
			}
			while (!f.exists() || f.isDirectory()) {
				Thread.sleep(1000);
			}

			// init reader
			BufferedReader reader = new BufferedReader(new FileReader(this.dir));
			String line;
			while (t == thisThread) {
				// start time
				long startProcessing = System.currentTimeMillis();

				// read line
				line = reader.readLine();

				if (line == null) {
					// nothing new, wait until there is more to read
					long elapsedTime = System.currentTimeMillis()
							- startProcessing;
					if (this.updateInterval > elapsedTime)
						Thread.sleep(updateInterval - elapsedTime);
				} else {
					this.processLine(line);
				}
			}
			reader.close();
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}

	/** processes the input line **/
	public void processLine(String line) {
		synchronized (this.logTextArea) {
			if (line.startsWith(Log.infoPrefix) && this.showInfo)
				printNewLine(line);
			if (line.startsWith(Log.warningPrefix) && this.showWarning)
				printNewLine(line);
			if (line.startsWith(Log.errorPrefix) && this.showError)
				printNewLine(line);
			if (line.startsWith(Log.debugPrefix) && this.showDebug)
				printNewLine(line);
		}
	}

	/** Prints a string into the log **/
	private void printNewLine(String data) {
		this.logTextArea.append(data + "\n");

		this.validate();
	}

	/** clears the log **/
	public void clearLog() {
		synchronized (this.logTextArea) {
			this.logTextArea.setText("");
		}
	}

	/** stops the log display thread **/
	public void stop() {
		this.t = null;
	}

	/** starts a new log display thread **/
	public void start() {
		if (this.t == null) {
			Random random = new Random();
			this.t = new Thread(this, "LogDisplay-Thread" + random.nextFloat());
			Log.info("Starting LogDisplay in new thread: " + t);
			this.t.start();
		}
	}

	/** Returns if the logdisplay is running. **/
	public boolean isRunning() {
		return this.running;
	}
}
