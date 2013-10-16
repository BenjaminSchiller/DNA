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
	/** DEFAULT DIR **/
	static String defaultDir = "data/Metrics/cc/run.0/";

	/** MAIN **/
	public static void main(String[] args) {
		// init main window
		MainDisplay display = new MainDisplay();
		display.setVisible(true);

		// init batch handler
		display.setBatchHandler();
		display.initBatchHandler();

		// get initial batch

	}

	// class variables
	private StatsDisplay statsDisplay1;
	private BatchHandler batchHandler;

	private ArrayList<Component> dataComponents;

	private JPanel buttons;
	private JButton pauseButton;
	private JButton startButton;
	private JButton stopButton;
	private JButton quitButton;

	private JPanel logoPanel;

	private MetricVisualizer metric1;

	// constructor
	public MainDisplay() {
		setTitle("DNA - Dynamic Network Analyzer");
		setSize(1024, 800);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		this.setLayout(new GridBagLayout());
		this.dataComponents = new ArrayList<Component>();

		// create constraints for mainDisplay Layout
		GridBagConstraints mainDisplayConstraints = new GridBagConstraints();

		// init stats component, set position in grid and add to mainframe
		this.statsDisplay1 = new StatsDisplay();
		this.statsDisplay1.setParent(this);
		this.statsDisplay1.setDirectory(defaultDir);

		mainDisplayConstraints.gridy = 0;
		mainDisplayConstraints.gridx = 0;
		this.getContentPane().add(this.statsDisplay1, mainDisplayConstraints);

		// register statsDisplay to get batchdata objects
		this.registerDataComponent(this.statsDisplay1);

		// register batchhandler at statsdisplay
		this.statsDisplay1.setBatchHandler(this.batchHandler);

		// add buttons
		this.quitButton = new JButton("Quit");
		this.quitButton.setBounds(50, 60, 80, 30);
		this.quitButton.setForeground(Color.BLACK);
		this.quitButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				System.exit(0);
			}
		});

		this.pauseButton = new JButton("Pause");
		this.pauseButton.setBounds(50, 60, 80, 30);
		this.pauseButton.setForeground(Color.BLACK);
		this.pauseButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				batchHandler.togglePause();
				if (pauseButton.getForeground().equals(Color.BLACK)) {
					pauseButton.setForeground(Color.RED);
					pauseButton.setText("Resume");
				} else {
					pauseButton.setText("Pause");
					pauseButton.setForeground(Color.BLACK);
				}
			}
		});

		this.stopButton = new JButton("Stop");
		this.stopButton.setBounds(50, 60, 80, 30);
		this.stopButton.setForeground(Color.BLACK);
		this.stopButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				pauseButton.setForeground(Color.BLACK);
				pauseButton.setText("Pause");
				try {
					statsDisplay1.setStopped();
					batchHandler.reset();
					initBatchHandler();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});

		this.startButton = new JButton("Start");
		this.startButton.setBounds(50, 60, 80, 30);
		this.startButton.setForeground(Color.BLACK);
		this.startButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				statsDisplay1.setStarted();
				batchHandler.start();
			}
		});

		this.buttons = new JPanel();
		this.buttons.setLayout(new BoxLayout(this.buttons, BoxLayout.X_AXIS));

		// set buttons-panel position in gridlayout and add to mainframe
		mainDisplayConstraints.gridx = 0;
		mainDisplayConstraints.gridy = 1;
		this.getContentPane().add(this.buttons, mainDisplayConstraints);

		this.buttons.add(this.startButton);
		this.buttons.add(this.pauseButton);
		this.buttons.add(this.stopButton);
		this.buttons.add(this.quitButton);

		// init logo panel, set position in gridlayout and add to mainframe
		mainDisplayConstraints.gridx = 0;
		mainDisplayConstraints.gridy = 2;
		this.logoPanel = new JPanel();
		this.logoPanel
				.setLayout(new BoxLayout(this.logoPanel, BoxLayout.X_AXIS));
		this.getContentPane().add(this.logoPanel, mainDisplayConstraints);
		this.logoPanel.setBorder(BorderFactory
				.createEtchedBorder((EtchedBorder.LOWERED)));
		BufferedImage image = null;
		try {
			image = ImageIO.read(new File("logo/dna-logo-v5.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		JLabel logoLabel = new JLabel(new ImageIcon(image));
		this.logoPanel.add(logoLabel);

		this.metric1 = new MetricVisualizer();
		mainDisplayConstraints.gridx = 1;
		mainDisplayConstraints.gridy = 0;
		this.getContentPane().add(this.metric1, mainDisplayConstraints);

		this.registerDataComponent(metric1);
		this.setLocationRelativeTo(null);
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
				((StatsDisplay) c).initData(b, batchHandler.getDir(),
						batchHandler.getMinTimestamp(),
						batchHandler.getMaxTimestamp());
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

	/** clears all list items from metric visualizers **/
	public void clearLists() {
		for (Component c : this.dataComponents) {
			if (c instanceof MetricVisualizer) {
				((MetricVisualizer) c).clearList();
			}
		}
	}

	/**
	 * Resets the batch handler. All previous holded batches get lost. The
	 * former thread that handed over batches gets free. The batchhandler will
	 * call the maindisplay.reset() method, which resets all data components.
	 */
	public void resetBatchHandler() {
		try {
			this.batchHandler.reset();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		this.initBatchHandler();
	}

	/**
	 * Initializes the batchhandler. Will update batch handlers batch
	 * information and then hand over the initialization batch.
	 */
	public void initBatchHandler() {
		this.batchHandler.updateBatches();
		this.batchHandler.init();
	}

	/** sets the batch handlers directory **/
	public void setBatchHandlerDir(String dir) {
		this.batchHandler.setDir(dir);
	}

	/** sets the batch handlers speed **/
	public void setBatchHandlerSpeed(int speed) {
		this.batchHandler.setSpeed(speed);
	}

	/** sets the batch handler **/
	public void setBatchHandler() {
		this.batchHandler = new BatchHandler(defaultDir, this.statsDisplay1,
				this);
	}

}
