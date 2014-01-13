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
import dna.visualization.components.statsdisplay.StatsDisplay;
import dna.visualization.components.visualizer.MetricVisualizer;
import dna.visualization.components.visualizer.MultiScalarVisualizer;
import dna.visualization.components.visualizer.Visualizer;

@SuppressWarnings("serial")
public class MainDisplay extends JFrame {
	/** MAIN **/
	public static void main(String[] args) {
		// init main window
		MainDisplay display = new MainDisplay();

		// init batch handler, hand over directory and maindisplay
		display.setBatchHandler(new BatchHandler(GuiOptions.defaultDir, display));
		display.initBatchHandler();

		display.setVisible(true);
	}

	/** MAIN-END **/

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

	private Visualizer metric1;
	private Visualizer metric2;

	// constructor
	public MainDisplay() {
		setTitle("DNA - Dynamic Network Analyzer");
		setSize(GuiOptions.mainDisplaySize);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		this.setLayout(new GridBagLayout());
		this.dataComponents = new ArrayList<Component>();

		// create constraints for mainDisplay Layout
		GridBagConstraints mainDisplayConstraints = new GridBagConstraints();

		// init stats component, set position in grid and add to mainframe
		this.statsDisplay1 = new StatsDisplay();
		this.statsDisplay1.setLocation(0, 0);
		this.statsDisplay1.setParent(this);
		this.statsDisplay1.setDirectory(GuiOptions.defaultDir);

		mainDisplayConstraints.gridy = 0;
		mainDisplayConstraints.gridx = 0;
		this.getContentPane().add(this.statsDisplay1, mainDisplayConstraints);

		// register statsDisplay to get batchdata objects
		this.registerDataComponent(this.statsDisplay1);

		// create buttons
		this.quitButton = new JButton("Quit");
		this.quitButton.setPreferredSize(GuiOptions.mainDisplayButtonSize);
		this.quitButton.setFont(GuiOptions.defaultFont);
		this.quitButton.setForeground(GuiOptions.defaultFontColor);
		this.quitButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				System.exit(0);
			}
		});

		this.pauseButton = new JButton("Pause");
		this.pauseButton.setPreferredSize(GuiOptions.mainDisplayButtonSize);
		this.pauseButton.setFont(GuiOptions.defaultFont);
		this.pauseButton.setForeground(GuiOptions.defaultFontColor);
		this.pauseButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				togglePause();
				if (pauseButton.getForeground().equals(
						GuiOptions.defaultFontColor)) {
					pauseButton.setForeground(Color.RED);
					pauseButton.setText("Resume");
				} else {
					pauseButton.setText("Pause");
					pauseButton.setForeground(GuiOptions.defaultFontColor);
				}
			}
		});

		this.stopButton = new JButton("Stop");
		this.stopButton.setPreferredSize(GuiOptions.mainDisplayButtonSize);
		this.stopButton.setFont(GuiOptions.defaultFont);
		this.stopButton.setForeground(GuiOptions.defaultFontColor);
		this.stopButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				pauseButton.setForeground(GuiOptions.defaultFontColor);
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
		this.startButton.setPreferredSize(GuiOptions.mainDisplayButtonSize);
		this.startButton.setFont(GuiOptions.defaultFont);
		this.startButton.setForeground(GuiOptions.defaultFontColor);
		this.startButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				statsDisplay1.setStarted();
				batchHandler.start();
			}
		});

		final JButton toggleM1 = new JButton("-M1");
		toggleM1.setPreferredSize(GuiOptions.mainDisplayButtonSize);
		toggleM1.setFont(GuiOptions.defaultFont);
		toggleM1.setForeground(GuiOptions.defaultFontColor);
		toggleM1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				if (metric1.isVisible()) {
					toggleM1.setText("+M1");
					metric1.setVisible(false);
				} else {
					toggleM1.setText("-M1");
					metric1.setVisible(true);
				}
			}
		});

		final JButton toggleM2 = new JButton("-M2");
		toggleM2.setPreferredSize(GuiOptions.mainDisplayButtonSize);
		toggleM2.setFont(GuiOptions.defaultFont);
		toggleM2.setForeground(GuiOptions.defaultFontColor);
		toggleM2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				if (metric2.isVisible()) {
					toggleM2.setText("+M2");
					metric2.setVisible(false);
				} else {
					toggleM2.setText("-M2");
					metric2.setVisible(true);
				}
			}
		});

		this.buttons = new JPanel();
		this.buttons.setLayout(new GridBagLayout());
		GridBagConstraints buttonPanelConstraints = new GridBagConstraints();
		// this.buttons.setLayout(new BoxLayout(this.buttons,
		// BoxLayout.X_AXIS));

		// set buttons-panel position in gridlayout and add them to mainframe
		mainDisplayConstraints.gridx = 0;
		mainDisplayConstraints.gridy = 1;
		this.getContentPane().add(this.buttons, mainDisplayConstraints);

		buttonPanelConstraints.gridx = 0;
		buttonPanelConstraints.gridy = 0;
		this.buttons.add(this.startButton, buttonPanelConstraints);
		buttonPanelConstraints.gridx++;
		this.buttons.add(this.pauseButton, buttonPanelConstraints);
		buttonPanelConstraints.gridx++;
		this.buttons.add(this.stopButton, buttonPanelConstraints);
		buttonPanelConstraints.gridx = 0;
		buttonPanelConstraints.gridy = 1;
		this.buttons.add(toggleM1, buttonPanelConstraints);
		buttonPanelConstraints.gridx++;
		this.buttons.add(toggleM2, buttonPanelConstraints);
		buttonPanelConstraints.gridx++;
		this.buttons.add(this.quitButton, buttonPanelConstraints);

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
		this.logoPanel.setLayout(new GridBagLayout());
		this.logoPanel.setPreferredSize(GuiOptions.logoSize);
		this.logoPanel.add(logoLabel);

		// add metric visualizer
		this.metric1 = new MetricVisualizer();
		mainDisplayConstraints.gridx = 1;
		mainDisplayConstraints.gridy = 0;
		this.getContentPane().add(this.metric1, mainDisplayConstraints);

		// add multi scalar visualizer
		this.metric2 = new MultiScalarVisualizer();
		mainDisplayConstraints.gridx = 2;
		mainDisplayConstraints.gridy = 0;
		this.getContentPane().add(this.metric2, mainDisplayConstraints);

		this.registerDataComponent(metric1);
		this.registerDataComponent(metric2);
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
			if (c instanceof MultiScalarVisualizer) {
				((MultiScalarVisualizer) c).updateData(b);
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
			if (c instanceof MultiScalarVisualizer) {
				((MultiScalarVisualizer) c).initData(b);
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
			if (c instanceof MultiScalarVisualizer) {
				((MultiScalarVisualizer) c).reset();
			}
		}
	}

	/** clears all list items from metric visualizers **/
	public void clearLists() {
		for (Component c : this.dataComponents) {
			if (c instanceof Visualizer) {
				((Visualizer) c).clearList();
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
	public void setBatchHandler(BatchHandler bh) {
		this.batchHandler = bh;
	}

	/** called from the pause-button to pause the gui **/
	public void togglePause() {
		batchHandler.togglePause();
		for (Component c : this.dataComponents) {
			if (c instanceof StatsDisplay) {
				((StatsDisplay) c).togglePause();
			}
			if (c instanceof MetricVisualizer) {
				((MetricVisualizer) c).togglePause();
			}
			if (c instanceof MultiScalarVisualizer) {
				((MultiScalarVisualizer) c).togglePause();
			}
		}
	}

	/** sets the batchhandler paused or unpaused **/
	public void setPaused(boolean paused) {
		if (paused) {
			this.pauseButton.setForeground(Color.RED);
			this.pauseButton.setText("Resume");
		} else {
			this.pauseButton.setForeground(GuiOptions.defaultFontColor);
			this.pauseButton.setText("Pause");
		}
		this.batchHandler.setPaused(paused);
	}

	/** returns if the batchhandler is paused or not **/
	public boolean isPaused() {
		return this.batchHandler.isPaused();
	}

	/** called from the statsdisplay timeslider to move in time **/
	public long setTime(int timeValue) {
		return this.batchHandler.setTime(timeValue);
	}
}
