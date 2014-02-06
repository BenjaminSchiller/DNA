package dna.visualization.components;

import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.DefaultCaret;

import dna.util.Log;
import dna.visualization.GuiOptions;

/**
 * A java swing component to display a log in live display visualization mode.
 * 
 * @author Rwilmes
 * @date 03.02.2014
 */
public class LogDisplay extends JPanel implements Runnable {

	// variables
	private String dir;
	private LogDisplay thisItem;
	private long updateInterval;
	private Thread t;

	// components
	private JScrollPane logAreaScrollPane;
	private JTextArea logTextArea;

	private JPanel checkBoxPanel;
	private JButton logLevelButton;

	private JLabel debugLabel;
	private JCheckBox debugCheckBox;

	private JLabel errorLabel;
	private JCheckBox errorCheckBox;

	private JLabel infoLabel;
	private JCheckBox infoCheckBox;

	private JLabel warningLabel;
	private JCheckBox warningCheckBox;

	// flags
	private boolean showInfo = true;
	private boolean showDebug = false;
	private boolean showError = true;
	private boolean showWarning = false;

	// constructor
	public LogDisplay(String dir) {
		// init
		this.dir = dir;
		this.thisItem = this;
		this.updateInterval = 300;

		// set title and border of the metric visualizer
		TitledBorder title = BorderFactory.createTitledBorder("LogDisplay on: "
				+ dir);
		title.setBorder(BorderFactory
				.createEtchedBorder((EtchedBorder.LOWERED)));
		title.setTitleFont(GuiOptions.defaultFontBorders);
		title.setTitleColor(GuiOptions.defaultFontBordersColor);
		this.setBorder(title);

		// layout
		this.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		this.logTextArea = new JTextArea();
		this.logTextArea.setFont(GuiOptions.defaultFont);
		// this.logTextArea.setPreferredSize(new Dimension(300, 100));

		DefaultCaret caret = (DefaultCaret) this.logTextArea.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

		this.logAreaScrollPane = new JScrollPane(this.logTextArea);
		this.logAreaScrollPane.setPreferredSize(new Dimension(380, 80));

		c.gridx = 0;
		c.gridy = 0;
		this.add(this.logAreaScrollPane, c);

		// logLevelButton
		this.logLevelButton = new JButton("Loglevel: 1");
		this.logLevelButton.setFont(GuiOptions.defaultFont);
		this.logLevelButton.setForeground(GuiOptions.defaultFontColor);
		this.logLevelButton.setPreferredSize(new Dimension(100, 20));
		this.logLevelButton.setMargin(new Insets(0, 0, 0, 0));
		this.logLevelButton
				.setToolTipText("Change log-level of the displayed log.");
		this.logLevelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				int logLevel = (Integer.parseInt(logLevelButton.getText()
						.substring(10)) + 1) % 5;
				logLevelButton.setText("LogLevel: " + logLevel);
			}
		});

		// check box panel
		this.checkBoxPanel = new JPanel();
		this.checkBoxPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 0, 0));
		this.checkBoxPanel
				.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		this.checkBoxPanel.setPreferredSize(new Dimension(400, 20));
		// this.checkBoxPanel.add(this.logLevelButton);

		c.gridx = 0;
		c.gridy = 1;
		this.add(checkBoxPanel, c);

		// labels
		this.infoLabel = new JLabel("Info:");
		this.infoLabel.setFont(GuiOptions.defaultFont);
		this.infoLabel.setForeground(GuiOptions.defaultFontColor);

		this.warningLabel = new JLabel("Warn:");
		this.warningLabel.setFont(GuiOptions.defaultFont);
		this.warningLabel.setForeground(GuiOptions.defaultFontColor);

		this.errorLabel = new JLabel("Error:");
		this.errorLabel.setFont(GuiOptions.defaultFont);
		this.errorLabel.setForeground(GuiOptions.defaultFontColor);

		this.debugLabel = new JLabel("Debug:");
		this.debugLabel.setFont(GuiOptions.defaultFont);
		this.debugLabel.setForeground(GuiOptions.defaultFontColor);

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

		// init reader
		try {
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

		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}

	}

	/** processes the input line **/
	public void processLine(String line) {
		if (line.startsWith(Log.infoPrefix) && this.showInfo)
			printNewLine(line);
		if (line.startsWith(Log.warningPrefix) && this.showWarning)
			printNewLine(line);
		if (line.startsWith(Log.errorPrefix) && this.showError)
			printNewLine(line);
		if (line.startsWith(Log.debugPrefix) && this.showDebug)
			printNewLine(line);
	}

	/** Prints a string into the log **/
	private void printNewLine(String data) {
		this.logTextArea.append(data + "\n");

		this.validate();
	}

	/** clears the log **/
	private void clearLog() {
		this.logTextArea.setText("");
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
}
