package dna.visualization;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.jar.JarFile;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EtchedBorder;

import dna.io.filesystem.Dir;
import dna.series.data.BatchData;
import dna.util.Config;
import dna.util.IOUtils;
import dna.util.Log;
import dna.visualization.BatchHandler.ZipMode;
import dna.visualization.components.LogDisplay;
import dna.visualization.components.statsdisplay.StatsDisplay;
import dna.visualization.components.visualizer.MetricVisualizer;
import dna.visualization.components.visualizer.MultiScalarVisualizer;
import dna.visualization.components.visualizer.Visualizer;
import dna.visualization.config.JSON.JSONException;
import dna.visualization.config.JSON.JSONObject;
import dna.visualization.config.JSON.JSONTokener;
import dna.visualization.config.components.LogDisplayConfig;
import dna.visualization.config.components.MainDisplayConfig;
import dna.visualization.config.components.MetricVisualizerConfig;
import dna.visualization.config.components.MultiScalarVisualizerConfig;

@SuppressWarnings("serial")
public class MainDisplay extends JFrame {

	/** MAIN **/
	public static void main(String[] args) throws URISyntaxException,
			IOException {
		Log.infoSep();

		// check cmd line parameters
		boolean helpFlag = false;
		boolean configFlag = false;
		String configPath = null;
		boolean dataFlag = false;
		String dataDir = null;
		boolean liveFlag = false;
		boolean playbackFlag = false;
		boolean zipBatchFlag = false;
		boolean zipRunFlag = false;

		try {
			for (int i = 0; i < args.length; i++) {
				switch (args[i]) {
				case "-c":
					configFlag = true;
					configPath = args[i + 1];
					break;
				case "-d":
					dataFlag = true;
					dataDir = args[i + 1];
					break;
				case "-h":
					helpFlag = true;
					break;
				case "-l":
					liveFlag = true;
					break;
				case "-p":
					playbackFlag = true;
					break;
				case "-z":
					zipBatchFlag = true;
					break;
				case "-zr":
					zipRunFlag = true;
					break;
				}
			}
		} catch (IndexOutOfBoundsException e) {
			Log.error("Error in parameter parsing, please check syntax!");
			System.out.println();
			helpFlag = true;
		}

		if (liveFlag && playbackFlag) {
			Log.warn("Live display AND playback flag set. Showing -help and exiting.");
			helpFlag = true;
		}

		if (liveFlag && zipRunFlag) {
			Log.warn("Live display AND zipped run flag set. Showing -help and exiting.");
			helpFlag = true;
		}

		if (zipBatchFlag && zipRunFlag) {
			Log.warn("Zipped run flag && zipped batch flag set. Showing -help and exiting.");
			helpFlag = true;
		}

		if (helpFlag) {
			System.out.println("DNA - Dynamic Network Analyzer");
			System.out
					.println("Run the program with the following command line parameters to change the GUI's behaviour:");
			System.out.println("Parameter" + "\t\t" + "Function");
			System.out.println("-c <config-path>" + "\t"
					+ "Uses the specified file as main display configuration");
			System.out.println("-d <data-dir>" + "\t\t"
					+ "Specifies the data-dir as default dir");
			System.out.println("-h" + "\t\t\t" + "Displays this help message");
			System.out.println("-l" + "\t\t\t"
					+ "Runs the GUI in live display mode");
			System.out.println("-p" + "\t\t\t"
					+ "Runs the GUI in playback mode");
			System.out.println("-z" + "\t\t\t"
					+ "Enables zipped batches support");
			System.out
					.println("-zr" + "\t\t\t" + "Enables zipped runs support");

			System.out.println("Example: run vis.jar -c " + '"'
					+ "config/my_guy.cfg" + '"' + " -d " + '"'
					+ "data/scenario1337/run.42/" + '"' + " -l -z");
		} else {
			if (!configFlag)
				configPath = defaultConfigPath;

			// boolean runFromJar = false;
			Path pPath = Paths.get(Config.class.getProtectionDomain()
					.getCodeSource().getLocation().toURI());
			// if (pPath.getFileName().toString().endsWith(".jar"))
			// runFromJar = true;

			try {
				InputStream is;
				JSONTokener tk;
				JSONObject jsonConfig;
				JarFile x = null;

				// read default config
				if (runFromJar) {
					String[] splits = defaultConfigPath.split("/");
					x = new JarFile(pPath.toFile(), false);
					Log.info("Loading default config from inside .jar-file: '"
							+ splits[splits.length - 1] + "'");
					is = IOUtils.getInputStreamFromJar(x, defaultConfigPath);
				} else {
					Log.info("Loading default config from '"
							+ defaultConfigPath + "'");
					is = new FileInputStream(defaultConfigPath);
				}
				tk = new JSONTokener(is);
				jsonConfig = new JSONObject(tk);
				MainDisplay.DefaultConfig = MainDisplayConfig
						.createMainDisplayConfigFromJSONObject(jsonConfig
								.getJSONObject("MainDisplayConfig"));

				// free resources
				is.close();
				if (x != null)
					x.close();
				x = null;
				is = null;
				tk = null;
				jsonConfig = null;

				// read main display config
				Log.info("Loading config from '" + configPath + "'");
				File configFile = new File(configPath);
				if (configFile.exists()) {
					is = new FileInputStream(configPath);
				} else {
					if (runFromJar) {
						Log.info("'" + configPath
								+ "' not found. Checking .jar");
						x = new JarFile(pPath.toFile(), false);
						is = IOUtils.getInputStreamFromJar(x, configPath);
					} else {
						Log.info("'" + configPath
								+ "' not found. Using default config.");
					}
				}

				// if inputstream present, read new file
				if (is != null) {
					tk = new JSONTokener(is);
					jsonConfig = new JSONObject(tk);
					config = MainDisplayConfig
							.createMainDisplayConfigFromJSONObject(jsonConfig
									.getJSONObject("MainDisplayConfig"));
					is.close();
					is = null;
				}

				// free resources
				if (x != null)
					x.close();
				x = null;
				tk = null;
				jsonConfig = null;
			} catch (JSONException | IOException e) {
				e.printStackTrace();
			}

			// use cmd line parameters
			if (!dataFlag)
				dataDir = config.getDefaultDir();
			else {
				config.setDefaultDir(dataDir);
				MainDisplay.DefaultConfig.setDefaultDir(dataDir);
			}
			if (!liveFlag) {
				if (playbackFlag) {
					liveFlag = !playbackFlag;
					config.setLiveDisplayMode(liveFlag);
					MainDisplay.DefaultConfig.setLiveDisplayMode(liveFlag);
				} else
					liveFlag = config.isLiveDisplayMode();
			} else {
				config.setLiveDisplayMode(liveFlag);
				MainDisplay.DefaultConfig.setLiveDisplayMode(liveFlag);
			}

			ZipMode zipMode = ZipMode.none;
			if (!zipBatchFlag && !zipRunFlag)
				zipMode = config.getZipMode();
			else if (zipBatchFlag)
				zipMode = ZipMode.batches;
			else if (zipRunFlag)
				zipMode = ZipMode.runs;

			// init main window
			Log.infoSep();
			Log.info("Initializing MainDisplay");
			MainDisplay display = new MainDisplay(liveFlag, zipMode, config);

			if (config.isFullscreen()) {
				display.setExtendedState(display.getExtendedState()
						| JFrame.MAXIMIZED_BOTH);
			}
			display.setVisible(true);

			// if live, start immediately
			if (liveFlag) {
				display.statsDisplay.setStarted();
				display.batchHandler.start();
				display.startLogDisplays();
			}
		}
	}

	/** MAIN-END **/

	// class variables
	private StatsDisplay statsDisplay;
	private BatchHandler batchHandler;

	private JPanel visualizerPanel;
	private JPanel leftSidePanel;

	private ArrayList<Component> dataComponents;

	public JPanel buttons;
	private JButton pauseButton;
	private JButton startButton;
	private JButton stopButton;
	private JButton quitButton;

	private JCheckBox mainLock;

	private JPanel logoPanel;

	private Font defaultFont;
	private Color defaultFontColor;

	// config
	public static boolean runFromJar = Config.isRunFromJar();
	public static MainDisplayConfig config = MainDisplay.getDefaultConfig();
	public static MainDisplayConfig DefaultConfig = MainDisplay
			.getDefaultConfig();

	public static final String defaultConfigPath = "config/gui_default.cfg";
	public static final String minConfigPath = "config/gui_min.cfg";
	public static final String displayConfigPath = "config/displayConfig.cfg";

	// live display flag
	public boolean liveDisplay;
	public ZipMode zipMode;

	// constructor

	public MainDisplay() {
		this(MainDisplay.getDefaultConfig());
	}

	public MainDisplay(String configPath) {
		this(MainDisplayConfig.getConfig(configPath));
	}

	public MainDisplay(String configPath, String dataDir) {
		this(MainDisplayConfig.getConfig(configPath, dataDir));
	}

	public MainDisplay(MainDisplayConfig cfg) {
		this(cfg.isLiveDisplayMode(), cfg.getZipMode(), cfg);
	}

	public MainDisplay(boolean liveDisplay, ZipMode zipMode) {
		this(liveDisplay, zipMode, MainDisplay.getDefaultConfig());
	}

	public MainDisplay(boolean liveDisplay, ZipMode zipMode, String configPath) {
		this(liveDisplay, zipMode, MainDisplayConfig.getConfig(configPath));
	}

	public MainDisplay(boolean liveDisplay, ZipMode zipMode,
			MainDisplayConfig config) {
		// init
		setTitle(config.getName());
		setSize(config.getSize());
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		// setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.defaultFont = config.getDefaultFont();
		this.defaultFontColor = config.getDefaultFontColor();
		this.dataComponents = new ArrayList<Component>();
		this.liveDisplay = liveDisplay;
		this.zipMode = zipMode;

		/*
		 * LEFT SIDE PANEL
		 */
		this.leftSidePanel = new JPanel();
		this.leftSidePanel.setLayout(new GridBagLayout());
		GridBagConstraints leftSideConstraints = new GridBagConstraints();
		leftSideConstraints.anchor = GridBagConstraints.NORTH;

		/*
		 * Create buttons
		 */
		this.quitButton = new JButton("Quit");
		this.quitButton.setPreferredSize(config.getButtonSize());
		this.quitButton.setFont(this.defaultFont);
		this.quitButton.setForeground(this.defaultFontColor);
		this.quitButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				System.exit(0);
			}
		});

		this.pauseButton = new JButton("Pause");
		this.pauseButton.setPreferredSize(config.getButtonSize());
		this.pauseButton.setFont(this.defaultFont);
		this.pauseButton.setForeground(this.defaultFontColor);
		this.pauseButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				togglePause();
				if (pauseButton.getForeground().equals(defaultFontColor)) {
					pauseButton.setForeground(Color.RED);
					pauseButton.setText("Resume");
				} else {
					pauseButton.setText("Pause");
					pauseButton.setForeground(defaultFontColor);
				}
			}
		});

		this.stopButton = new JButton("Stop");
		this.stopButton.setPreferredSize(config.getButtonSize());
		this.stopButton.setFont(this.defaultFont);
		this.stopButton.setForeground(this.defaultFontColor);
		this.stopButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				pauseButton.setForeground(defaultFontColor);
				pauseButton.setText("Pause");
				try {
					statsDisplay.setStopped();
					batchHandler.reset();
					initBatchHandler();
					stopLogDisplays();
				} catch (InterruptedException | IOException e) {
					e.printStackTrace();
				}
			}
		});

		this.startButton = new JButton("Start");
		this.startButton.setPreferredSize(config.getButtonSize());
		this.startButton.setFont(this.defaultFont);
		this.startButton.setForeground(this.defaultFontColor);
		this.startButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				// if livedisplay start the batchhandler, he will take care of
				// missing directory
				if (MainDisplay.this.liveDisplay) {
					statsDisplay.setStarted();
					batchHandler.start();
					startLogDisplays();
				} else {
					// if not livedisplay, only start when directory is there
					File f = new File(batchHandler.getDir());
					if (f.exists() && f.isDirectory()) {
						if (!batchHandler.isInit()) {
							try {
								batchHandler.updateBatches();
								batchHandler.init();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
						statsDisplay.setStarted();
						batchHandler.start();
						startLogDisplays();
					} else {
						Log.info("Dir '"
								+ f.getPath()
								+ "' not present, try again with existing directory.");
					}
				}
			}
		});

		/*
		 * Add buttons to ButtonsPanel and add it to leftSidePanel
		 */
		this.buttons = new JPanel();
		this.buttons.setLayout(new GridBagLayout());
		GridBagConstraints buttonPanelConstraints = new GridBagConstraints();

		leftSideConstraints.gridx = 0;
		leftSideConstraints.gridy = 1;
		// this.statsDisplay.add(this.buttons, leftSideConstraints);

		// this.statsDisplay.add(this.buttons);

		buttonPanelConstraints.gridx = 0;
		buttonPanelConstraints.gridy = 0;
		this.buttons.add(this.startButton, buttonPanelConstraints);
		buttonPanelConstraints.gridx++;
		this.buttons.add(this.pauseButton, buttonPanelConstraints);
		buttonPanelConstraints.gridx++;
		this.buttons.add(this.stopButton, buttonPanelConstraints);
		buttonPanelConstraints.gridx++;
		this.buttons.add(this.quitButton, buttonPanelConstraints);

		// create lock checkbox and add to buttons panel
		this.mainLock = new JCheckBox("Lock");
		this.mainLock
				.setToolTipText("Locks all visualizers, which means their legends wont be altered during reset / directory change.");
		this.mainLock.setFont(new Font(this.getDefaultFont().getName(),
				Font.BOLD, this.getDefaultFont().getSize()));
		this.mainLock.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				boolean locked = false;
				if (mainLock.isSelected())
					locked = true;

				// call method
				setLocked(locked);
			}
		});

		buttonPanelConstraints.gridx++;
		this.buttons.add(this.mainLock, buttonPanelConstraints);

		/*
		 * Create StatsDisplay
		 */
		this.statsDisplay = new StatsDisplay(this,
				config.getStatsDisplayConfig(), liveDisplay);
		this.statsDisplay.setLocation(0, 0);
		this.statsDisplay.setDirectory(config.getDefaultDir());

		leftSideConstraints.gridx = 0;
		leftSideConstraints.gridy = 0;
		this.leftSidePanel.add(this.statsDisplay, leftSideConstraints);

		// register statsDisplay to get batchdata objects
		this.registerDataComponent(this.statsDisplay);

		/*
		 * Init LogoPanel, set position and add to leftSidePanel
		 */
		this.logoPanel = new JPanel();
		this.logoPanel
				.setLayout(new BoxLayout(this.logoPanel, BoxLayout.X_AXIS));

		this.logoPanel.setBorder(BorderFactory
				.createEtchedBorder((EtchedBorder.LOWERED)));
		BufferedImage image = null;
		JarFile x = null;
		InputStream is = null;
		try {
			if (runFromJar) {
				Path pPath = Paths.get(Config.class.getProtectionDomain()
						.getCodeSource().getLocation().toURI());
				String[] splits = config.getLogoDir().split("/");
				x = new JarFile(pPath.toFile(), false);
				is = x.getInputStream(x.getEntry(splits[splits.length - 1]));
				image = ImageIO.read(is);
			} else {
				image = ImageIO.read(new File(config.getLogoDir()));
			}
		} catch (IOException | URISyntaxException e) {
			e.printStackTrace();
		}

		JLabel logoLabel = new JLabel(new ImageIcon(image));
		this.logoPanel.setLayout(new GridBagLayout());
		this.logoPanel.setPreferredSize(config.getLogoSize());
		this.logoPanel.add(logoLabel);

		// free resources
		try {
			if (is != null)
				is.close();
			if (x != null)
				x.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		is = null;
		x = null;

		leftSideConstraints.gridx = 0;
		leftSideConstraints.gridy = 2;
		this.leftSidePanel.add(this.logoPanel, leftSideConstraints);

		/*
		 * VISUALIZER PANEL
		 */
		this.visualizerPanel = new JPanel();
		this.visualizerPanel.setLayout(new GridBagLayout());
		GridBagConstraints visualizerPanelConstraints = new GridBagConstraints();
		visualizerPanelConstraints.gridx = 0;
		visualizerPanelConstraints.gridy = 0;
		visualizerPanelConstraints.gridwidth = 1;
		visualizerPanelConstraints.gridheight = 1;

		int maxYPosition = 0;
		// FILL PANEL
		// add metric visualizer
		for (MetricVisualizerConfig metVisConfig : config
				.getMetricVisualizerConfigs()) {
			MetricVisualizer metricVisualizerTemp = new MetricVisualizer(this,
					metVisConfig);

			if (metVisConfig.getPositionX() >= 0
					&& metVisConfig.getPositionY() >= 0) {
				visualizerPanelConstraints.gridx = metVisConfig.getPositionX();
				visualizerPanelConstraints.gridy = metVisConfig.getPositionY();
				if (visualizerPanelConstraints.gridy > maxYPosition)
					maxYPosition = visualizerPanelConstraints.gridy;
			} else {
				visualizerPanelConstraints.gridy = maxYPosition++;
			}
			if (metVisConfig.getColSpan() >= 1
					&& metVisConfig.getRowSpan() >= 1) {
				visualizerPanelConstraints.gridwidth = metVisConfig
						.getColSpan();
				visualizerPanelConstraints.gridheight = metVisConfig
						.getRowSpan();
			} else {
				visualizerPanelConstraints.gridwidth = 1;
				visualizerPanelConstraints.gridheight = 1;
			}
			this.visualizerPanel.add(metricVisualizerTemp,
					visualizerPanelConstraints);
			this.registerDataComponent(metricVisualizerTemp);
		}
		// add multi scalar visualizer
		for (MultiScalarVisualizerConfig multiVisConfig : config
				.getMultiScalarVisualizerConfigs()) {
			MultiScalarVisualizer metricVisualizerTemp = new MultiScalarVisualizer(
					this, multiVisConfig);

			if (multiVisConfig.getPositionX() >= 0
					&& multiVisConfig.getPositionY() >= 0) {
				visualizerPanelConstraints.gridx = multiVisConfig
						.getPositionX();
				visualizerPanelConstraints.gridy = multiVisConfig
						.getPositionY();
				if (visualizerPanelConstraints.gridy > maxYPosition)
					maxYPosition = visualizerPanelConstraints.gridy;
			} else {
				visualizerPanelConstraints.gridy = maxYPosition++;
			}
			if (multiVisConfig.getColSpan() >= 1
					&& multiVisConfig.getRowSpan() >= 1) {
				visualizerPanelConstraints.gridwidth = multiVisConfig
						.getColSpan();
				visualizerPanelConstraints.gridheight = multiVisConfig
						.getRowSpan();
			} else {
				visualizerPanelConstraints.gridwidth = 1;
				visualizerPanelConstraints.gridheight = 1;
			}

			this.visualizerPanel.add(metricVisualizerTemp,
					visualizerPanelConstraints);
			this.registerDataComponent(metricVisualizerTemp);
		}
		// add log display
		for (LogDisplayConfig logDisConfig : config.getLogDisplayConfigs()) {
			LogDisplay logDisplayTemp = new LogDisplay(this, logDisConfig);

			if (logDisConfig.getPositionX() >= 0
					&& logDisConfig.getPositionY() >= 0) {
				visualizerPanelConstraints.gridx = logDisConfig.getPositionX();
				visualizerPanelConstraints.gridy = logDisConfig.getPositionY();
				if (visualizerPanelConstraints.gridy > maxYPosition)
					maxYPosition = visualizerPanelConstraints.gridy;
			} else {
				visualizerPanelConstraints.gridy = maxYPosition++;
			}
			if (logDisConfig.getColSpan() >= 1
					&& logDisConfig.getRowSpan() >= 1) {
				visualizerPanelConstraints.gridwidth = logDisConfig
						.getColSpan();
				visualizerPanelConstraints.gridheight = logDisConfig
						.getRowSpan();
			} else {
				visualizerPanelConstraints.gridwidth = 1;
				visualizerPanelConstraints.gridheight = 1;
			}
			this.visualizerPanel
					.add(logDisplayTemp, visualizerPanelConstraints);
			this.dataComponents.add(logDisplayTemp);
			logDisplayTemp.start();
		}

		JScrollPane dataScrollPanel = new JScrollPane(this.visualizerPanel);
		dataScrollPanel.setPreferredSize(config.getVisualizerPanelSize());
		this.visualizerPanel.setPreferredSize(config
				.getInnerVisualizerPanelSize());

		this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.X_AXIS));
		this.getContentPane().add(this.leftSidePanel);
		this.getContentPane().add(dataScrollPanel);

		// create batchhandler
		try {
			createBatchHandler(config);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		this.setVisible(true);
		this.validate();
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
		BatchData tempBatch = b;

		// scaling
		if (MainDisplay.config.getScalingExpression() != null
				&& !MainDisplay.config.getScalingExpression().equals("none")) {
			tempBatch = BatchHandler.scaleTimestamp(b,
					MainDisplay.config.getScalingExpression());
		}

		for (Component c : this.dataComponents) {
			if (c instanceof StatsDisplay) {
				((StatsDisplay) c).updateData(tempBatch);
			}
			if (c instanceof MetricVisualizer) {
				((MetricVisualizer) c).updateData(tempBatch);
			}
			if (c instanceof MultiScalarVisualizer) {
				((MultiScalarVisualizer) c).updateData(tempBatch);
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
		BatchData tempBatch = b;

		// scaling
		if (MainDisplay.config.getScalingExpression() != null
				&& !MainDisplay.config.getScalingExpression().equals("none")) {
			tempBatch = BatchHandler.scaleTimestamp(b,
					MainDisplay.config.getScalingExpression());
		}

		for (Component c : this.dataComponents) {
			if (c instanceof StatsDisplay) {
				if (this.liveDisplay)
					((StatsDisplay) c).initData(tempBatch,
							batchHandler.getDir());
				else
					((StatsDisplay) c).initData(tempBatch,
							batchHandler.getDir(),
							batchHandler.getMinTimestamp(),
							batchHandler.getMaxTimestamp());
			}
			if (c instanceof MetricVisualizer) {
				((MetricVisualizer) c).initData(tempBatch);
			}
			if (c instanceof MultiScalarVisualizer) {
				((MultiScalarVisualizer) c).initData(tempBatch);
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
	 * 
	 * @throws IOException
	 */
	public void resetBatchHandler() {
		try {
			this.batchHandler.reset();
			this.initBatchHandler();
		} catch (InterruptedException | IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Initializes the batchhandler. Will update batch handlers batch
	 * information and then hand over the initialization batch.
	 * 
	 * Note: If the desired directory is not existent, the batchhandler will not
	 * be initialized.
	 * 
	 * @throws IOException
	 */
	public void initBatchHandler() throws IOException {
		if (!this.liveDisplay) {
			File f;
			if (this.zipMode.equals(ZipMode.runs)) {
				f = new File(this.batchHandler.getDir() + "run."
						+ this.batchHandler.getRunId()
						+ Config.get("SUFFIX_ZIP_FILE"));
				if (f.exists() && !f.isDirectory()) {
					this.batchHandler.updateBatches();
					this.batchHandler.init();
				} else {
					Log.info("Zip '"
							+ f.getPath()
							+ "' not existing, BatchHandler could not be initialized.");
				}
			} else {
				f = new File(this.batchHandler.getDir());
				if (f.exists() && f.isDirectory()) {
					this.batchHandler.updateBatches();
					this.batchHandler.init();
				} else {
					Log.info("Dir '"
							+ f.getPath()
							+ "' not existing, BatchHandler could not be initialized.");
				}
			}
		}
	}

	/** Creates and inits a new BatchHandler. **/
	protected void createBatchHandler(MainDisplayConfig config)
			throws IOException {
		createBatchHandler(config.getDefaultDir(), this, liveDisplay,
				this.zipMode);
	}

	protected void createBatchHandler(String dataDir, MainDisplay display,
			boolean liveFlag, ZipMode zipMode) throws IOException {
		display.setBatchHandler(new BatchHandler(dataDir, display, liveFlag,
				zipMode));
		display.initBatchHandler();
	}

	/** sets the batch handlers directory **/
	public void setBatchHandlerDir(String dir) {
		if (this.zipMode.equals(ZipMode.runs)) {
			try {
				String[] splits = dir.split(Dir.delimiter);
				this.batchHandler.setRunId(Dir.getRun(splits[splits.length - 1]
						.replace(Config.get("SUFFIX_ZIP_FILE"), "")));
				String tempDir = "";
				for (int i = 0; i < splits.length - 1; i++)
					tempDir += splits[i] + Dir.delimiter;
				this.batchHandler.setDir(tempDir);
			} catch (NumberFormatException e) {
				Log.warn("NumberFormatException on dir '" + dir
						+ "', no run-zip.");
			}
		} else {
			this.batchHandler.setDir(dir);
		}
	}

	/** sets the batch handlers speed **/
	public void setBatchHandlerSpeed(int speed) {
		this.batchHandler.setSpeed(speed);
	}

	/** sets the batch handler **/
	public void setBatchHandler(BatchHandler bh) {
		this.batchHandler = bh;
	}

	/** starts the live monitoring of the batchhandler **/
	public void startLiveMonitoring() {
		if (this.liveDisplay) {
			this.batchHandler.start();
			this.statsDisplay.setStarted();
		}
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
			this.pauseButton.setForeground(this.defaultFontColor);
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

	/** called from the statsdisplay to get next bigger timestamp **/
	public long getNextTimestamp(long timestamp) {
		return this.batchHandler.getNextTimestamp(timestamp);
	}

	/** called from the statsdisplay to get next smaller timestamp **/
	public long getPreviousTimestamp(long timestamp) {
		return this.batchHandler.getPreviousTimestamp(timestamp);
	}

	/** called from the statsdisplay to get the amount of previous timestamps **/
	public int getAmountOfPreviousTimestamps(long timestamp) {
		return this.batchHandler.getAmountOfPreviousTimestamps(timestamp);
	}

	/** Returns the default font. **/
	public Font getDefaultFont() {
		return this.defaultFont;
	}

	/** Returns the default font color. **/
	public Color getDefaultFontColor() {
		return this.defaultFontColor;
	}

	/** Starts all registered log displays. **/
	public void startLogDisplays() {
		for (Component c : this.dataComponents) {
			if (c instanceof LogDisplay) {
				((LogDisplay) c).start();
			}
		}
	}

	/** Stops all registered log displays. **/
	public void stopLogDisplays() {
		for (Component c : this.dataComponents) {
			if (c instanceof LogDisplay) {
				((LogDisplay) c).stop();
				((LogDisplay) c).clearLog();
			}
		}
	}

	/**
	 * Sets the status message in the statsdisplay.
	 * 
	 * Note: For livedisplay only!
	 */
	public void setStatusMessage(String msg) {
		for (Component c : this.dataComponents) {
			if (c instanceof StatsDisplay) {
				((StatsDisplay) c).setStatusMessage(msg);
			}
		}
	}

	public void setLocked(boolean locked) {
		for (Component c : this.dataComponents) {
			if (c instanceof Visualizer) {
				((Visualizer) c).setLockedByMainDisplay(locked);
			}
		}
	}

	public static MainDisplayConfig getDefaultConfig() {
		return MainDisplayConfig.getConfig(MainDisplay.defaultConfigPath,
				runFromJar);
	}

	public static MainDisplayConfig getMinConfig() {
		return MainDisplayConfig.getConfig(MainDisplay.minConfigPath,
				runFromJar);
	}

	public static MainDisplayConfig getDisplayConfig() {
		return MainDisplayConfig.getConfig(MainDisplay.displayConfigPath,
				runFromJar);
	}

}
