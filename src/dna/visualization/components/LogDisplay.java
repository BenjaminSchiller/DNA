package dna.visualization.components;

import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

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

	// components
	private JTextArea logTextArea;

	private JPanel buttonPanel;
	private JButton logLevelButton;

	private LogDisplay thisItem;

	public LogDisplay(String dir) {
		// init
		this.dir = dir;
		this.thisItem = this;

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
		this.logTextArea.setPreferredSize(new Dimension(300, 300));

		JScrollPane scrollPane = new JScrollPane(this.logTextArea);
		scrollPane.setPreferredSize(new Dimension(380, 80));

		c.gridx = 0;
		c.gridy = 0;
		this.add(scrollPane, c);

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

		// button panel
		this.buttonPanel = new JPanel();
		this.buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 0, 0));
		this.buttonPanel
				.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		this.buttonPanel.setPreferredSize(new Dimension(400, 20));
		this.buttonPanel.add(this.logLevelButton);

		c.gridx = 0;
		c.gridy = 1;
		this.add(buttonPanel, c);

		this.validate();
	}

	public void run() {
		//TODO: tail log file
	}

	public void showInfo(String data) {
		this.logTextArea.append(data + "\n");
		this.validate();
	}

}
