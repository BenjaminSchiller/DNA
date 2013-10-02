package dna.visualization;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

import dna.visualization.components.StatsDisplay;

public class MainDisplay extends JFrame {

	public MainDisplay() {
		setTitle("DNA - Dynamic Network Analyzer");
		setSize(350, 600);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		setLayout(new GridBagLayout());
	}

	public static void main(String[] args) {
		/****** Input dir ******/
		String dir = "data/test15/run.1/";
		/****** ********* ******/

		GridBagConstraints c = new GridBagConstraints();

		// init main window
		final MainDisplay display = new MainDisplay();

		// init stats component, set position in grid and add to mainframe
		final StatsDisplay statsdis = new StatsDisplay();
		c.gridy = 0;
		c.gridx = 0;
		display.getContentPane().add(statsdis, c);

		// init batch handler
		final BatchHandler bh = new BatchHandler(dir, statsdis);
		try {
			bh.updateBatches();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// register batchhandler at statsdisplay
		statsdis.setBatchHandler(bh);

		// add buttons
		JButton quitButton = new JButton("Quit");
		quitButton.setBounds(50, 60, 80, 30);
		quitButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				System.exit(0);
			}
		});

		final JButton addButton = new JButton("Pause");
		addButton.setBounds(50, 60, 80, 30);
		addButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				bh.togglePause();
				if (addButton.getForeground().equals(new Color(51, 51, 51))) {
					addButton.setForeground(Color.RED);
				} else {
					addButton.setForeground(new Color(51, 51, 51));
				}
			}
		});

		JButton resetButton = new JButton("Reset");
		resetButton.setBounds(50, 60, 80, 30);
		resetButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				try {
					bh.reset();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});

		JButton startButton = new JButton("Start");
		startButton.setBounds(50, 60, 80, 30);
		startButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				bh.start();
			}
		});

		JPanel buttons = new JPanel();
		buttons.setLayout(new BoxLayout(buttons, BoxLayout.X_AXIS));

		// set buttons-panel position in gridlayout and add to mainframe
		c.gridx = 0;
		c.gridy = 1;
		display.getContentPane().add(buttons, c);

		buttons.add(startButton);
		buttons.add(addButton);
		buttons.add(resetButton);
		buttons.add(quitButton);

		// init panel, set position in gridlayout and add to mainframe
		c.gridx = 0;
		c.gridy = 2;
		JPanel logoPanel = new JPanel();
		logoPanel.setLayout(new BoxLayout(logoPanel, BoxLayout.X_AXIS));
		display.getContentPane().add(logoPanel, c);
		logoPanel.setBorder(BorderFactory
				.createEtchedBorder((EtchedBorder.LOWERED)));
		BufferedImage image = null;
		try {
			image = ImageIO.read(new File("logo/dna-logo-v5.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		JLabel logoLabel = new JLabel(new ImageIcon(image));
		logoPanel.add(logoLabel);

		// general display settings
		// display.getContentPane().setLayout(
		// new BoxLayout(display.getContentPane(), BoxLayout.Y_AXIS));
		// display.pack();
		display.setVisible(true);
	}
}
