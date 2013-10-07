package dna.visualization;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

import dna.series.data.BatchData;
import dna.visualization.components.MetricVisualizer;
import dna.visualization.components.StatsDisplay;

public class MainDisplay extends JFrame {

	static BatchHandler bh;
	static String dir;
	private ArrayList<Component> dataComponents;

	public MainDisplay() {
		setTitle("DNA - Dynamic Network Analyzer");
		setSize(1024, 800);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		setLayout(new GridBagLayout());
		this.dataComponents = new ArrayList<Component>();
	}

	public static void main(String[] args) {
		/****** Input dir ******/
		dir = "data/test15/run.0/";
		/****** ********* ******/

		GridBagConstraints c = new GridBagConstraints();

		// init main window
		final MainDisplay display = new MainDisplay();

		// init stats component, set position in grid and add to mainframe
		final StatsDisplay statsdis = new StatsDisplay();
		c.gridy = 0;
		c.gridx = 0;
		display.getContentPane().add(statsdis, c);

		// register statsDisplay to get batchdata objects
		display.registerDataComponent(statsdis);

		// init batch handler
		bh = new BatchHandler(dir, statsdis, display);
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
		quitButton.setForeground(Color.BLACK);
		quitButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				System.exit(0);
			}
		});

		final JButton pauseButton = new JButton("Pause");
		pauseButton.setBounds(50, 60, 80, 30);
		pauseButton.setForeground(Color.BLACK);
		pauseButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				bh.togglePause();
				if (pauseButton.getForeground().equals(Color.BLACK)) {
					pauseButton.setForeground(Color.RED);
					pauseButton.setText("Resume");
				} else {
					pauseButton.setText("Pause");
					pauseButton.setForeground(Color.BLACK);
				}
			}
		});

		JButton stopButton = new JButton("Stop");
		stopButton.setBounds(50, 60, 80, 30);
		stopButton.setForeground(Color.BLACK);
		stopButton.addActionListener(new ActionListener() {
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
		startButton.setForeground(Color.BLACK);
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
		buttons.add(pauseButton);
		buttons.add(stopButton);
		buttons.add(quitButton);

		// init logo panel, set position in gridlayout and add to mainframe
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

		// init metric visualizer, set position in gridlayout and add to
		// mainframe
		MetricVisualizer metric1 = new MetricVisualizer();
		c.gridx = 1;
		c.gridy = 0;
		display.getContentPane().add(metric1, c);

		display.registerDataComponent(metric1);
		display.setLocationRelativeTo(null);
		bh.init();

		display.setVisible(true);

	}

	/**
	 * Gives a batchdata object to the mainDisplay which will forward it to all
	 * neccessary instances. Note: b doesn't contain any data values, components
	 * will read data on their own.
	 * 
	 * @param b
	 *            BatchData object holding structural information about the data
	 */
	public void updateData(BatchData b) {
		for (Component c : this.dataComponents) {
			if (c instanceof StatsDisplay) {
				((StatsDisplay) c).updateData(b);
			}
			if (c instanceof MetricVisualizer) {
				((MetricVisualizer) c).updateData(b);
			}
		}
	}

	/**
	 * Called by the batch handler with the first batch. Initializes all
	 * registered data components.
	 * 
	 * @param b
	 *            initialization batch
	 */
	public void initData(BatchData b) {
		for (Component c : this.dataComponents) {
			if (c instanceof StatsDisplay) {
				((StatsDisplay) c).initData(b, dir, bh.getMinTimestamp(),
						bh.getMaxTimestamp());
			}
			if (c instanceof MetricVisualizer) {
				((MetricVisualizer) c).initData(b);
			}
		}
	}

	/** register components to recieve the batchdata objects **/
	public void registerDataComponent(Component c) {
		this.dataComponents.add(c);
	}

	/** resets all registered components **/
	public void reset() {
		for (Component c : this.dataComponents) {
			if (c instanceof StatsDisplay) {
				((StatsDisplay) c).reset();
			}
			if (c instanceof MetricVisualizer) {
				((MetricVisualizer) c).reset();
			}
		}
	}
}
