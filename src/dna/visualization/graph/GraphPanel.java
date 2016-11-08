package dna.visualization.graph;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.ui.geom.Point3;
import org.graphstream.ui.layout.Layout;
import org.graphstream.ui.layout.springbox.implementations.LinLog;
import org.graphstream.ui.layout.springbox.implementations.SpringBox;
import org.graphstream.ui.spriteManager.SpriteManager;
import org.graphstream.ui.view.View;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.view.util.DefaultMouseManager;

import dna.graph.edges.DirectedWeightedEdge;
import dna.graph.edges.UndirectedWeightedEdge;
import dna.graph.nodes.DirectedWeightedNode;
import dna.graph.nodes.UndirectedWeightedNode;
import dna.graph.weights.IWeightedEdge;
import dna.graph.weights.IWeightedNode;
import dna.graph.weights.Weight;
import dna.util.Config;
import dna.util.Log;
import dna.visualization.VisualizationUtils;
import dna.visualization.VisualizationUtils.VideoRecorder;
import dna.visualization.graph.rules.GraphStyleRule;
import dna.visualization.graph.rules.GraphStyleUtils;
import dna.visualization.graph.toolTipManager.ToolTipManager;
import dna.visualization.graph.util.GraphVisMouseManager;

/**
 * The GraphPanel class is used as a JPanel which contains a text-panel and a
 * graph-visualization panel underneath.
 **/
public class GraphPanel extends JPanel {

	public enum PositionMode {
		twoDimension, threeDimension, auto
	};

	public enum RecordArea {
		full, content, graph
	};

	private static final long serialVersionUID = 1L;

	// font
	protected final static Font font = new Font("Verdana", Font.PLAIN, 14);

	// name & graph
	protected final JFrame parentFrame;
	protected final Graph graph;
	protected final PositionMode mode;
	protected final String graphGeneratorName;
	protected long timestamp;

	protected boolean tooltips;
	protected SpriteManager spriteManager;

	// rules
	protected ArrayList<GraphStyleRule> rules;
	protected int nextRuleIndex;

	// panels
	protected final JPanel graphView;
	protected JPanel textPanel;
	protected JLabel textLabel;
	protected JLabel zoomLabel;
	protected final Layout layouter;
	protected JButton captureButton;
	protected JButton screenshotButton;
	protected Color captureButtonFontColor;
	protected Color captureButtonFontColorRecording = new Color(200, 30, 30);
	protected View view;
	protected JComboBox<String> recordAreasBox;

	protected JButton pauseButton;
	protected boolean paused;

	// stat panel
	protected JLabel bgNameLabel;
	protected JLabel timestampValue;
	protected JLabel nodesValue;
	protected JLabel edgesValue;
	protected SimpleDateFormat dateFormat;
	protected boolean timestampAsDate;
	protected boolean timestampInSeconds;
	protected int timestampOffset;

	// speed factors
	protected double zoomSpeedFactor = Config.getDouble("GRAPH_VIS_ZOOM_SPEED");
	protected double scrollSpeecFactor = Config
			.getDouble("GRAPH_VIS_SCROLL_SPEED");

	// used for dragging
	protected Point dragPos;

	// tool-tip management
	protected ToolTipManager toolTipManager;

	// recording
	protected boolean recording;
	protected VideoRecorder videoRecorder;
	protected RecordArea recordArea;

	// enable 3d projection
	protected boolean enable3dProjection = Config
			.getBoolean("GRAPH_VIS_3D_PROJECTION_ENABLED");
	protected boolean enable3dProjectionNodeSizing = Config
			.getBoolean("GRAPH_VIS_SIZE_NODES_BY_Z_COORDINATE");
	protected boolean useVanishingPoint = Config
			.getBoolean("GRAPH_VIS_3D_PROJECTION_USE_VANISHING_POINT");

	// scaling matrix
	protected final double s0_x = Config
			.getDouble("GRAPH_VIS_3D_PROJECTION_S0_X");
	protected final double s0_y = Config
			.getDouble("GRAPH_VIS_3D_PROJECTION_S0_Y");
	protected final double s0_z = Config
			.getDouble("GRAPH_VIS_3D_PROJECTION_S0_Z");

	protected final double s1_x = Config
			.getDouble("GRAPH_VIS_3D_PROJECTION_S1_X");
	protected final double s1_y = Config
			.getDouble("GRAPH_VIS_3D_PROJECTION_S1_Y");
	protected final double s1_z = Config
			.getDouble("GRAPH_VIS_3D_PROJECTION_S1_Z");

	// offset vector
	protected final double offset_x = Config
			.getDouble("GRAPH_VIS_3D_PROJECTION_OFFSET_X");
	protected final double offset_y = Config
			.getDouble("GRAPH_VIS_3D_PROJECTION_OFFSET_Y");

	// vanishing point
	protected double vp_x = Config.getDouble("GRAPH_VIS_3D_PROJECTION_VP_X");
	protected double vp_y = Config.getDouble("GRAPH_VIS_3D_PROJECTION_VP_Y");
	protected double vp_z = Config.getDouble("GRAPH_VIS_3D_PROJECTION_VP_Z");
	protected double vp_scalingFactor = Config
			.getDouble("GRAPH_VIS_3D_PROJECTION_VP_SCALING");

	protected static double minX = Double.NaN;
	protected static double maxX = Double.NaN;
	protected static double minY = Double.NaN;
	protected static double maxY = Double.NaN;
	protected static double minZ = Double.NaN;
	protected static double maxZ = Double.NaN;

	// constructors
	public GraphPanel(JFrame parentFrame, final Graph graph, final String name,
			final String graphGeneratorName, PositionMode mode) {
		this(parentFrame, graph, name, graphGeneratorName, mode,
				new ArrayList<GraphStyleRule>(0));
	}

	public GraphPanel(JFrame parentFrame, final Graph graph, final String name,
			final String graphGeneratorName, PositionMode mode,
			ArrayList<GraphStyleRule> rules) {
		this.parentFrame = parentFrame;
		this.setName(name);
		this.graph = graph;
		this.mode = mode;
		this.graphGeneratorName = graphGeneratorName;
		this.timestamp = 0;
		this.dateFormat = new SimpleDateFormat(
				Config.get("GRAPH_VIS_DATETIME_FORMAT"));
		this.timestampAsDate = Config.getBoolean("GRAPH_VIS_TIMESTAMP_AS_DATE");
		this.timestampInSeconds = Config
				.getBoolean("GRAPH_VIS_TIMESTAMP_IN_SECONDS");
		this.timestampOffset = Config.getInt("GRAPH_VIS_TIMESTAMP_OFFSET");
		this.rules = rules;
		this.nextRuleIndex = 0;

		this.recording = false;
		this.paused = false;
		this.tooltips = false;

		if (Config.getBoolean("GRAPH_VIS_TOOLTIPS_ENABLED")) {
			this.spriteManager = new SpriteManager(graph);
			this.tooltips = true;
		}

		boolean addStatPanel = Config
				.getBoolean("GRAPH_VIS_STAT_PANEL_ENABLED");
		boolean addTextPanel = Config
				.getBoolean("GRAPH_VIS_TEXT_PANEL_ENABLED");

		// create viewer and show graph
		Viewer v = new Viewer(graph,
				Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);

		boolean useLayouter3dMode = Config.getBoolean("GRAPH_VIS_LAYOUT_3D");

		// create and configure layouter
		if (Config.getBoolean("GRAPH_VIS_AUTO_LAYOUT_ENABLED")) {
			Layout layouter = new SpringBox(useLayouter3dMode);
			if (Config.getBoolean("GRAPH_VIS_LAYOUT_LINLOG"))
				layouter = new LinLog(useLayouter3dMode);

			layouter.setForce(Config.getDouble("GRAPH_VIS_LAYOUT_FORCE"));
			v.enableAutoLayout(layouter);

			this.layouter = layouter;
		} else {
			this.layouter = null;
			v.disableAutoLayout();
		}

		// get view
		View view = v.addDefaultView(false);
		this.view = view;
		this.graphView = (JPanel) view;
		this.graphView.setName(name);

		// main panel
		this.setLayout(new BorderLayout());
		this.add(this.graphView, BorderLayout.CENTER);

		// bottom panel contains the stats and text panels
		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));

		// add panels to bottom pannel
		if (addStatPanel)
			addStatPanel(bottomPanel);
		if (addTextPanel)
			addTextPanel(bottomPanel, (addStatPanel) ? false : true);

		// add bottom panel to frame
		this.add(bottomPanel, BorderLayout.PAGE_END);

		// add listeners
		addZoomListener();
		addMoveListener();
		changeMouseManager();
	}

	/** Adds the stat-panel. **/
	protected void addStatPanel(JPanel panel) {
		// init statpanel
		JPanel statPanel = new JPanel();
		statPanel.setLayout(new BoxLayout(statPanel, BoxLayout.X_AXIS));
		statPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));

		// timestamp
		JLabel timestampLabel = new JLabel("Timestamp: ");
		timestampLabel.setFont(font);
		this.timestampValue = new JLabel();
		timestampValue.setFont(font);
		this.setTimestampLabel(0);

		// nodes
		JLabel nodesLabel = new JLabel("Nodes: ");
		nodesLabel.setFont(font);
		this.nodesValue = new JLabel("0", JLabel.RIGHT);
		nodesValue.setFont(font);

		// edges
		JLabel edgesLabel = new JLabel("Edges: ");
		edgesLabel.setFont(font);
		this.edgesValue = new JLabel("0", JLabel.RIGHT);
		edgesValue.setFont(font);

		// bg
		JLabel bgLabel = new JLabel("GraphGenerator: ");
		bgLabel.setFont(font);
		this.bgNameLabel = new JLabel(this.graphGeneratorName);
		bgNameLabel.setFont(font);

		// bg
		statPanel.add(bgLabel);
		statPanel.add(bgNameLabel);

		// dummy
		JPanel statDummy = new JPanel();
		statDummy.setPreferredSize(new Dimension(100, 25));
		statPanel.add(statDummy);

		// timestamp
		timestampValue.setPreferredSize(new Dimension(100, 25));
		statPanel.add(timestampLabel);
		statPanel.add(timestampValue);

		// dummy2
		JLabel dummy2 = new JLabel();
		dummy2.setPreferredSize(new Dimension(15, 25));
		statPanel.add(dummy2);

		// nodes
		nodesValue.setPreferredSize(new Dimension(45, 25));
		statPanel.add(nodesLabel);
		statPanel.add(nodesValue);

		// dummy3
		JLabel dummy3 = new JLabel();
		dummy3.setPreferredSize(new Dimension(15, 25));
		statPanel.add(dummy3);

		// edges
		edgesValue.setPreferredSize(new Dimension(45, 25));
		statPanel.add(edgesLabel);
		statPanel.add(edgesValue);

		// dummy4
		JLabel dummy4 = new JLabel();
		dummy4.setPreferredSize(new Dimension(2, 25));
		statPanel.add(dummy4);

		panel.add(statPanel);
	}

	/** Adds the text-panel. **/
	protected void addTextPanel(JPanel panel, boolean border) {
		// init textpanel
		this.textPanel = new JPanel();
		textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.X_AXIS));
		if (border)
			textPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));

		// set text panel
		textLabel = new JLabel();
		textLabel.setFont(font);
		textLabel.setText("Initialization");
		textLabel.setBackground(new Color(230, 230, 230));
		textPanel.add(textLabel);

		// dummy panel
		JPanel dummy = new JPanel();
		textPanel.add(dummy);

		// zoom label
		this.zoomLabel = new JLabel();
		zoomLabel.setFont(font);
		zoomLabel.setText("100%  ");
		zoomLabel.setToolTipText("Zoom");
		textPanel.add(zoomLabel);

		// screenshot button
		this.screenshotButton = new JButton("Screenshot");
		screenshotButton.setPreferredSize(new Dimension(100, 25));
		screenshotButton.setFont(new Font(font.getName(), font.getStyle(), font
				.getSize() - 3));
		screenshotButton
				.setToolTipText("Captures a screenshot and saves it to '"
						+ Config.get("GRAPH_VIS_SCREENSHOT_DIR") + "'");
		screenshotButton.setFocusPainted(false);
		screenshotButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				captureScreenshot(false);
			}
		});
		textPanel.add(screenshotButton);

		this.pauseButton = new JButton("Pause");
		pauseButton.setPreferredSize(new Dimension(100, 25));
		pauseButton.setFont(new Font(font.getName(), font.getStyle(), font
				.getSize() - 3));
		pauseButton.setToolTipText("Pauses the current video recording.");
		pauseButton.setFocusPainted(false);
		pauseButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				toggleVideoPause();
			}
		});
		pauseButton.setVisible(false);
		textPanel.add(pauseButton);

		this.captureButton = new JButton("Video");
		captureButton.setPreferredSize(new Dimension(100, 25));
		captureButton.setFont(new Font(font.getName(), font.getStyle(), font
				.getSize() - 3));
		captureButton.setToolTipText("Captures a video and saves it to '"
				+ Config.get("GRAPH_VIS_VIDEO_DIR") + "'");
		captureButton.setFocusPainted(false);
		this.captureButtonFontColor = captureButton.getForeground();
		captureButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					if (!isRecording())
						captureVideo();
					else
						stopVideo();
				} catch (InterruptedException | IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		textPanel.add(captureButton);

		String[] strings = { "Full", "Content", "Graph" };
		this.recordAreasBox = new JComboBox<String>(strings);
		recordAreasBox.setPrototypeDisplayValue("Content");
		recordAreasBox
				.setToolTipText("Selects the area that will be captured.");
		recordAreasBox.setPreferredSize(new Dimension(90, 25));
		recordAreasBox.setMaximumSize(recordAreasBox.getPreferredSize());
		recordAreasBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				@SuppressWarnings("unchecked")
				JComboBox<String> box = (JComboBox<String>) e.getSource();
				switch ((String) box.getSelectedItem()) {
				case "Content":
					setRecordArea(RecordArea.content);
					break;
				case "Graph":
					setRecordArea(RecordArea.graph);
					break;
				default:
					setRecordArea(RecordArea.full);
					break;
				}
			}
		});
		textPanel.add(recordAreasBox);

		// get record area mode
		switch (Config.get("GRAPH_VIS_DEFAULT_RECORD_AREA")) {
		case "content":
			this.setRecordArea(RecordArea.content);
			recordAreasBox.setSelectedIndex(1);
			break;
		case "graph":
			this.setRecordArea(RecordArea.graph);
			recordAreasBox.setSelectedIndex(2);
			break;
		default:
			this.setRecordArea(RecordArea.full);
			recordAreasBox.setSelectedIndex(0);
			break;
		}

		panel.add(textPanel);

	}

	/** Adds the zoom listener. **/
	protected void addZoomListener() {
		this.graphView.addMouseWheelListener(new MouseWheelListener() {

			@Override
			public void mouseWheelMoved(MouseWheelEvent arg0) {
				double currentZoom = getZoomPercent();

				// zoom speed
				double speed;
				if (currentZoom < 0.3) {
					if (currentZoom < 0.1) {
						if (currentZoom < 0.01)
							speed = zoomSpeedFactor / 30;
						else
							speed = zoomSpeedFactor / 10;
					} else {
						speed = zoomSpeedFactor / 3;
					}
				} else {
					speed = zoomSpeedFactor;
				}

				// calc new zoom amount
				double zoom = currentZoom
						+ (arg0.getWheelRotation() * arg0.getScrollAmount() * speed);
				if (zoom < 0)
					zoom = 0;
				if (Math.abs(1 - zoom) < 0.04)
					zoom = 1;

				// set new zoom
				setZoom(zoom);
			}
		});
	}

	/** Adds the listener for moving in graph. **/
	protected void addMoveListener() {
		this.graphView.addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseMoved(MouseEvent arg0) {
				// update latest cursor position
				dragPos = arg0.getPoint();
			}

			@Override
			public void mouseDragged(MouseEvent arg0) {
				if ((arg0.getModifiersEx() & InputEvent.BUTTON3_DOWN_MASK) == InputEvent.BUTTON3_DOWN_MASK) {
					Point3 currentCenter = getViewCenter();

					// calc new position
					double x_new = currentCenter.x
							+ (dragPos.getX() - arg0.getX())
							* scrollSpeecFactor * getZoomPercent()
							* getGraphDimension() / 13;
					double y_new = currentCenter.y
							- (dragPos.getY() - arg0.getY())
							* scrollSpeecFactor * getZoomPercent()
							* getGraphDimension() / 13;

					// set viewcenter
					setViewCenter(x_new, y_new, currentCenter.z);

					// update new position
					dragPos = arg0.getPoint();
				}
			}
		});
	}

	/**
	 * Removes the default mouse manager and replaces it with a
	 * GraphVisMouseManager.
	 **/
	protected void changeMouseManager() {
		JPanel view = this.getGraphView();
		MouseListener[] mouseListeners = view.getMouseListeners();

		// release current mouse listeners
		for (MouseListener ml : mouseListeners)
			((DefaultMouseManager) ml).release();

		// init graphvis mouse manager
		GraphVisMouseManager graphVisMouseManager = new GraphVisMouseManager(
				this);

		// add new mouse manager
		((View) view).setMouseManager(graphVisMouseManager);
	}

	/** Adds a graph style rule. **/
	public void addGraphStyleRule(GraphStyleRule r) {
		this.rules.add(r);
		r.setIndex(this.getNextIndex());
	}

	/** Adds a ToolTipManager. **/
	public void addToolTipManager(ToolTipManager ttm) {
		this.rules.add(ttm);
		this.toolTipManager = ttm;
		ttm.setIndex(this.getNextIndex());
	}

	/** Returns the next rule index and increments it afterwards. **/
	protected int getNextIndex() {
		int temp = this.nextRuleIndex;
		this.nextRuleIndex++;
		return temp;
	}

	/** Sets the graph style rules. **/
	public void setGraphStyleRules(ArrayList<GraphStyleRule> rules) {
		this.rules = rules;
	}

	/** Returns the graph style rules. **/
	public ArrayList<GraphStyleRule> getGraphStyleRules() {
		return this.rules;
	}

	/** Sets the zoom. **/
	public void setZoom(double percent) {
		if (this.zoomLabel != null)
			this.zoomLabel.setText((int) Math.floor(percent * 100) + "%  ");
		this.view.getCamera().setViewPercent(percent);
	}

	/** Returns the current zoom in percent. **/
	public double getZoomPercent() {
		return this.view.getCamera().getViewPercent();
	}

	/** Returns the current view center. **/
	public Point3 getViewCenter() {
		return this.view.getCamera().getViewCenter();
	}

	/** Sets the view center. **/
	public void setViewCenter(double x, double y, double z) {
		this.view.getCamera().setViewCenter(x, y, z);
	}

	/** Returns the graphs dimension. **/
	protected double getGraphDimension() {
		return this.view.getCamera().getGraphDimension();
	}

	/** Returns the parent frame this GraphPanel is embedded in. **/
	public JFrame getParentFrame() {
		return this.parentFrame;
	}

	/** Returns the JPanel containing only the graph-view. **/
	public JPanel getGraphView() {
		return this.graphView;
	}

	/** Returns the embedded graphstream.graph. **/
	public Graph getGraph() {
		return this.graph;
	}

	/** Returns the layouter of the embedded graphstream graph. **/
	public Layout getLayouter() {
		return this.layouter;
	}

	/** Returns the vanishing point as {x, y, z}. **/
	public double[] getVanishingPoint() {
		return new double[] { this.vp_x, this.vp_y, this.vp_z };
	}

	/** Sets the vanishing point. **/
	public void setVanishingPoint(double x, double y, double z) {
		this.vp_x = x;
		this.vp_y = y;
		this.vp_z = z;
	}

	/** Returns if 3d projection is enabled. **/
	public boolean is3dProjectionEnabled() {
		return this.enable3dProjection;
	}

	/** Returns if vanishing point is used for 3d projection. **/
	public boolean isVanishingPointUsed() {
		return this.useVanishingPoint;
	}

	/** Enables/Disables 3d pojeton. **/
	public void set3dProjection(boolean enabled) {
		this.enable3dProjection = enabled;
	}

	/** Sets if vanishing points will be used for 3d projection. **/
	public void setVanishingPointUse(boolean enabled) {
		this.useVanishingPoint = enabled;
	}

	/** Sets the text-label to the input text. **/
	public void setText(String text) {
		if (this.textLabel != null)
			textLabel.setText(text);
	}

	/** Updates the timestamp. **/
	public void setTimestamp(long timestamp) {
		long t = timestamp + this.timestampOffset;
		if (this.timestampInSeconds)
			t *= 1000;
		this.timestamp = t;

		if (this.timestampValue != null)
			this.setTimestampLabel(t);
	}

	/** Updates the timestamp label. **/
	protected void setTimestampLabel(long timestamp) {
		if (this.timestampAsDate)
			this.timestampValue.setText(this.dateFormat.format(new Date(
					timestamp)));
		else
			this.timestampValue.setText("" + timestamp);
	}

	/** Increments the nodes-count label. **/
	public void incrementNodesCount() {
		if (this.nodesValue != null)
			this.nodesValue.setText(""
					+ (Integer.parseInt(this.nodesValue.getText()) + 1));
	}

	/** Decrements the nodes-count label. **/
	public void decrementNodesCount() {
		if (this.nodesValue != null)
			this.nodesValue.setText(""
					+ (Integer.parseInt(this.nodesValue.getText()) - 1));
	}

	/** Sets the nodes-count label. **/
	public void setNodesCount(int nodes) {
		if (this.nodesValue != null)
			this.nodesValue.setText("" + nodes);
	}

	/** Increments the edges-count label. **/
	public void incrementEdgesCount() {
		if (this.edgesValue != null)
			this.edgesValue.setText(""
					+ (Integer.parseInt(this.edgesValue.getText()) + 1));
	}

	/** Decrements the edges-count label. **/
	public void decrementEdgesCount() {
		if (this.edgesValue != null)
			this.edgesValue.setText(""
					+ (Integer.parseInt(this.edgesValue.getText()) - 1));
	}

	/** Sets the eges-count label. **/
	public void setEdgesCount(int edges) {
		if (this.edgesValue != null)
			this.edgesValue.setText("" + edges);
	}

	/** Returns the record area. **/
	public RecordArea getRecordArea() {
		return this.recordArea;
	}

	/** Sets the record area. **/
	public void setRecordArea(RecordArea recordArea) {
		this.recordArea = recordArea;
	}

	/** Records the min/max coordinates for each dimension. **/
	public void statRecord(float[] coords) {
		if (!Double.isNaN(minX)) {
			if (coords[0] < minX)
				minX = coords[0];
		} else {
			minX = coords[0];
		}
		if (!Double.isNaN(maxX)) {
			if (coords[0] > maxX)
				maxX = coords[0];
		} else {
			maxX = coords[0];
		}
		if (!Double.isNaN(minY)) {
			if (coords[1] < minY)
				minY = coords[1];
		} else {
			minY = coords[1];
		}
		if (!Double.isNaN(maxY)) {
			if (coords[1] > maxY)
				maxY = coords[1];
		} else {
			maxY = coords[1];
		}
		if (!Double.isNaN(minZ)) {
			if (coords[2] < minZ)
				minZ = coords[2];
		} else {
			minZ = coords[2];
		}
		if (!Double.isNaN(maxZ)) {
			if (coords[2] > maxZ)
				maxZ = coords[2];
		} else {
			maxZ = coords[2];
		}
	}

	/*
	 * GRAPH METHODS
	 */

	/*
	 * NODES
	 */

	/** Adds node n to the graph. **/
	public void addNode(dna.graph.nodes.Node n) {
		// add node to graph
		Node node = this.graph.addNode("" + n.getIndex());
		node.addAttribute(GraphVisualization.sizeKey,
				Config.getDouble("GRAPH_VIS_NODE_DEFAULT_SIZE"));
		node.addAttribute(GraphVisualization.colorKey,
				Config.getColor("GRAPH_VIS_NODE_DEFAULT_COLOR"));

		// init weight
		Weight w = null;
		if (n instanceof DirectedWeightedNode) {
			w = ((DirectedWeightedNode) n).getWeight();
			node.addAttribute(GraphVisualization.weightKey, w);
		} else if (n instanceof UndirectedWeightedNode) {
			w = ((UndirectedWeightedNode) n).getWeight();
			node.addAttribute(GraphVisualization.weightKey, w);
		}

		// get and set position
		if (w != null
				&& (this.mode.equals(PositionMode.twoDimension) || this.mode
						.equals(PositionMode.threeDimension))) {

			// get coords from weight
			float[] coords = GraphVisualization.getCoordsFromWeight(w);

			// keep record of min/max coordinates
			// statRecord(coords);

			// if 3d projection is enabled, project coordinates
			if (this.enable3dProjection) {
				double[] projected2DCoordinates = project3DPointToCoordinates(
						coords[0], coords[1], coords[2]);
				node.addAttribute(GraphVisualization.zKey, coords[2]);
				coords = new float[] { (float) projected2DCoordinates[0],
						(float) projected2DCoordinates[1], 0 };
			}

			// add position to node
			node.addAttribute(GraphVisualization.positionKey, coords[0],
					coords[1], coords[2]);
		}

		// if no z-value has been set, set 0
		if (!node.hasAttribute(GraphVisualization.zKey))
			node.setAttribute(GraphVisualization.zKey, 0F);

		// update label
		updateLabel(node);

		// apply style rules
		for (GraphStyleRule r : rules)
			r.onNodeAddition(node);

		// update style
		GraphStyleUtils.updateStyle(node);

		// update node count
		this.incrementNodesCount();
	}

	/** Removes node n from graph g. **/
	public void removeNode(dna.graph.nodes.Node n) {
		Node node = this.graph.removeNode("" + n.getIndex());

		for (GraphStyleRule r : rules)
			r.onNodeRemoval(node);

		// update style
		GraphStyleUtils.updateStyle(node);

		// update node count
		this.decrementNodesCount();
	}

	/** Changes node weight on node n IN CURRENT GRAPH!!. **/
	public void changeNodeWeight(IWeightedNode n, Weight w) {
		// get node
		Node node = this.graph.getNode("" + n.getIndex());

		// if node not in graph, dont do anything
		if (node == null)
			return;

		// get old weight
		Weight wOld = node.getAttribute(GraphVisualization.weightKey);

		// change weight
		node.changeAttribute(GraphVisualization.weightKey, w);

		// get and set position
		if (w != null
				&& (this.mode.equals(PositionMode.twoDimension) || this.mode
						.equals(PositionMode.threeDimension))) {

			// get coords from weight
			float[] coords = GraphVisualization.getCoordsFromWeight(w);

			// keep record of min/max coordinates
			// statRecord(coords);

			// if 3d projection is enabled, project coordinates
			if (this.enable3dProjection) {
				double[] projected2DCoordinates = project3DPointToCoordinates(
						coords[0], coords[1], coords[2]);
				node.addAttribute(GraphVisualization.zKey, coords[2]);
				coords = new float[] { (float) projected2DCoordinates[0],
						(float) projected2DCoordinates[1], 0 };
			}

			// add position to node
			node.changeAttribute(GraphVisualization.positionKey, coords[0],
					coords[1], coords[2]);
		}

		// show weight
		if (Config.getBoolean("GRAPH_VIS_SHOW_NODE_WEIGHTS")) {
			if (node.hasAttribute(GraphVisualization.labelKey))
				node.changeAttribute(GraphVisualization.labelKey, w.toString());
			else
				node.addAttribute(GraphVisualization.labelKey, w.toString());
		}

		// apply style rules
		for (GraphStyleRule r : rules)
			r.onNodeWeightChange(node, w, wOld);

		// update style
		GraphStyleUtils.updateStyle(node);
	}

	/*
	 * EDGES
	 */

	/** Adds edge e to graph g. **/
	public void addEdge(dna.graph.edges.Edge e) {
		// get directed flag
		boolean directedEdges = Config
				.getBoolean("GRAPH_VIS_SHOW_DIRECTED_EDGE_ARROWS")
				&& (boolean) this.graph
						.getAttribute(GraphVisualization.directedKey);

		// get indizes
		int n1 = e.getN1Index();
		int n2 = e.getN2Index();

		// get nodes and edges
		Node node1 = this.graph.getNode("" + n1);
		Node node2 = this.graph.getNode("" + n2);
		Edge edge = node1.getEdgeBetween(node2);

		// if edge not there, add it
		if ((edge == null) || (directedEdges && node2.equals(edge.getNode0()))) {
			edge = this.graph.addEdge(n1 + "-" + n2, "" + n1, "" + n2,
					directedEdges);

			// init weight
			if (e instanceof DirectedWeightedEdge) {
				Weight w = ((DirectedWeightedEdge) e).getWeight();
				edge.addAttribute(GraphVisualization.weightKey, w);
			} else if (e instanceof UndirectedWeightedEdge) {
				Weight w = ((UndirectedWeightedEdge) e).getWeight();
				edge.addAttribute(GraphVisualization.weightKey, w);
			}

			// update label
			updateLabel(edge);

			// set edge size / thickness
			edge.setAttribute(GraphVisualization.sizeKey,
					Config.getDouble("GRAPH_VIS_EDGE_DEFAULT_SIZE"));
			edge.setAttribute(GraphVisualization.styleKey,
					"size: " + edge.getAttribute(GraphVisualization.sizeKey)
							+ "px;");

			// apply style rules
			for (GraphStyleRule r : rules)
				r.onEdgeAddition(edge, node1, node2);

			// update styles
			GraphStyleUtils.updateStyle(edge);
			GraphStyleUtils.updateStyle(node1);
			GraphStyleUtils.updateStyle(node2);
		}

		// update edge count
		this.incrementEdgesCount();
	}

	/** Removes edge e from graph g. **/
	public void removeEdge(dna.graph.edges.Edge e) {
		// get indizes
		int n1 = e.getN1Index();
		int n2 = e.getN2Index();

		// remove edge
		Edge edge = this.graph.removeEdge(this.graph.getNode("" + n1)
				.getEdgeBetween("" + n2));

		// apply style rules
		Node node1 = this.graph.getNode("" + n1);
		Node node2 = this.graph.getNode("" + n2);
		for (GraphStyleRule r : rules)
			r.onEdgeRemoval(edge, node1, node2);

		// update styles
		GraphStyleUtils.updateStyle(node1);
		GraphStyleUtils.updateStyle(node2);

		// update edge count
		this.decrementEdgesCount();
	}

	/** Changes edge weight on edge e IN CURRENT GRAPH!!. **/
	public void changeEdgeWeight(IWeightedEdge e, Weight w) {
		// get indizes
		int n1 = e.getN1().getIndex();
		int n2 = e.getN2().getIndex();

		// if one of the nodes is null -> return
		if (this.graph.getNode("" + n1) == null
				|| this.graph.getNode("" + n2) == null)
			return;

		// get edge
		Edge edge = this.graph.getNode("" + n1).getEdgeBetween("" + n2);

		if (edge == null)
			return;

		// get old weight
		Weight wOld = edge.getAttribute(GraphVisualization.weightKey);

		// change weight
		edge.changeAttribute(GraphVisualization.weightKey, w);

		// update label
		updateLabel(edge);

		for (GraphStyleRule r : rules)
			r.onEdgeWeightChange(edge, w, wOld);

		// update styles
		GraphStyleUtils.updateStyle(edge);
	}

	/** Makes a screenshot of the current graph. **/
	public void captureScreenshotUsingGraphstream() {
		String screenshotsDir = Config.get("GRAPH_VIS_SCREENSHOT_DIR");
		String screenshotsSuffix = "."
				+ Config.get("GRAPH_VIS_SCREENSHOT_FORMAT");
		// create dir
		File f = new File(screenshotsDir);
		if (!f.exists() && !f.isFile())
			f.mkdirs();

		// get date format
		DateFormat df = new SimpleDateFormat("yyyy_MM_dd-HH_mm_ss");

		String filename = this.getName() + "-" + df.format(new Date());
		String path = screenshotsDir + filename;

		// get name
		File f2 = new File(path + screenshotsSuffix);
		int id = 0;
		while (f2.exists()) {
			id++;
			f2 = new File(path + "_" + id + screenshotsSuffix);
		}

		// create screenshot
		graph.addAttribute(GraphVisualization.screenshotsKey,
				f2.getAbsolutePath());
		Log.info("GraphVis - saving screenshot to '" + f2.getPath() + "'");
	}

	/** Makes a screenshot of the current JFrame. **/
	public void captureScreenshot(boolean waitForStabilization) {
		this.captureScreenshot(waitForStabilization,
				Config.get("GRAPH_VIS_SCREENSHOT_DIR"), null,
				Config.getInt("GRAPH_VIS_SCREENSHOT_FOREGROUND_DELAY"));
	}

	/** Makes a screenshot of the current JFrame. **/
	public void captureScreenshot(boolean waitForStabilization, String dstDir,
			String filename) {
		this.captureScreenshot(waitForStabilization, dstDir, filename,
				Config.getInt("GRAPH_VIS_SCREENSHOT_FOREGROUND_DELAY"));
	}

	/** Makes a screenshot of the current JFrame. **/
	public void captureScreenshot(boolean waitForStabilization, String dstDir,
			String filename, long screenshotDelay) {
		if (waitForStabilization) {
			long start = System.currentTimeMillis();
			long timeout = Config
					.getInt("GRAPH_VIS_SCREENSHOT_STABILITY_TIMEOUT");
			double stabilityThreshold = Config
					.getDouble("GRAPH_VIS_SCREENSHOT_STABILITY_THRESHOLD");

			// while not stable or timeout not reached, wait
			while ((this.getLayouter().getStabilization() < stabilityThreshold)
					&& ((System.currentTimeMillis() - start) < timeout)) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		// bring to front
		this.parentFrame.toFront();

		try {
			Thread.sleep(screenshotDelay);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// capture screenshot
		VisualizationUtils.captureScreenshot(this.getRecordComponent(), dstDir,
				filename);
	}

	/** Makes a video of the JFrame the panel is embedded in. **/
	public void captureVideo() throws InterruptedException, IOException {
		if (this.videoRecorder == null) {
			this.videoRecorder = new VideoRecorder(this.getRecordComponent());
			this.videoRecorder.registerComponent(this);
		} else {
			this.videoRecorder.updateDestinationPath();
			this.videoRecorder.updateSourceComponent(this.getRecordComponent());
		}
		this.videoRecorder.start();
	}

	/** Returns the component that should be recorded. **/
	protected Component getRecordComponent() {
		Component c;
		switch (this.recordArea) {
		case full:
			c = this.parentFrame;
			break;
		case content:
			c = this;
			break;
		case graph:
			c = this.graphView;
			break;
		default:
			c = this.parentFrame;
			break;
		}
		return c;
	}

	/** Stops the current video recording prematurely. **/
	public void stopVideo() {
		if (this.videoRecorder != null)
			this.videoRecorder.stop();
	}

	/** Toggles pause on the current video recording. **/
	public void toggleVideoPause() {
		if (this.recording) {
			if (this.paused)
				this.videoRecorder.resume();
			else
				this.videoRecorder.pause();
		}
	}

	/** Pauses the current video recording. **/
	public void pauseVideo() {
		if (this.recording) {
			if (!this.paused) {
				this.videoRecorder.pause();
				this.paused = true;

				if (this.pauseButton != null)
					this.pauseButton.setText("Resume");
			}
		}
	}

	/** Resumes the current video recording. **/
	public void resumeVideo() {
		if (this.recording) {
			if (this.paused) {
				this.videoRecorder.resume();
				this.paused = false;

				if (this.pauseButton != null)
					this.pauseButton.setText("Pause");
			}
		}
	}

	/** Report that the video has been paused. **/
	public void reportVideoPaused() {
		if (this.recording) {
			this.paused = true;

			if (this.pauseButton != null) {
				this.pauseButton.setText("Resume");
				this.pauseButton.setForeground(captureButtonFontColorRecording);
			}
		}
	}

	/** Report that the video has been resumed. **/
	public void reportVideoResumed() {
		if (this.recording) {
			this.paused = false;

			if (this.pauseButton != null) {
				this.pauseButton.setText("Pause");
				this.pauseButton.setForeground(captureButtonFontColor);
			}
		}
	}

	/** Updates the video progress. **/
	public void updateVideoProgress(double percent) {
		if (this.captureButton != null)
			this.captureButton.setText((int) Math.floor(percent * 100) + "%");
	}

	/** Updates the text on the video button. **/
	public void setVideoButtonText(String text) {
		if (this.captureButton != null)
			this.captureButton.setText(text);
	}

	/** Updates the video progress. **/
	public void updateElapsedVideoTime(int seconds) {
		if (this.captureButton != null)
			this.captureButton.setText(seconds + "s");
	}

	/** Updates the video progress as text. **/
	public void updateVideoProgressText(String text) {
		if (this.captureButton != null)
			this.captureButton.setText(text);
	}

	/** Report that the recording has started. **/
	public void reportVideoStarted() {
		if (this.captureButton != null) {
			this.captureButton.setText("Recording");
			this.captureButton
					.setForeground(this.captureButtonFontColorRecording);
		}
		if (this.pauseButton != null)
			this.pauseButton.setVisible(true);

		this.recording = true;
	}

	/** Report that the recording has been stopped. **/
	public void reportVideoStopped() {
		if (this.captureButton != null) {
			this.captureButton.setText("Video");
			this.captureButton.setForeground(this.captureButtonFontColor);
		}
		if (this.pauseButton != null) {
			this.pauseButton.setText("Pause");
			this.pauseButton.setForeground(captureButtonFontColor);
			this.pauseButton.setVisible(false);
		}
		this.paused = false;
		this.recording = false;
	}

	/** Returns if the panel is currently being recorded. **/
	public boolean isRecording() {
		return this.recording;
	}

	/** Returns if the panels video recording is currently paused. **/
	public boolean isPaused() {
		return this.paused;
	}

	/** Updates the label on node n. **/
	private static void updateLabel(Node n) {
		if (Config.getBoolean("GRAPH_VIS_SHOW_NODE_INDEX")
				|| Config.getBoolean("GRAPH_VIS_SHOW_NODE_WEIGHTS")) {
			String label = "";
			if (Config.getBoolean("GRAPH_VIS_SHOW_NODE_INDEX")) {
				if (Config.getBoolean("GRAPH_VIS_SHOW_NODE_INDEX_VERBOSE"))
					label += n.getIndex();
				else
					label += "Node " + n.getIndex();
			}
			if (Config.getBoolean("GRAPH_VIS_SHOW_NODE_WEIGHTS")) {
				if (n.getAttribute(GraphVisualization.weightKey) != null) {
					if (!label.equals(""))
						label += ", ";
					if (Config
							.getBoolean("GRAPH_VIS_SHOW_NODE_WEIGHTS_VERBOSE"))
						label += n.getAttribute(GraphVisualization.weightKey);
					else
						label += "w="
								+ n.getAttribute(GraphVisualization.weightKey);
				}
			}

			n.addAttribute(GraphVisualization.labelKey, label);
		} else {
			if (n.hasAttribute(GraphVisualization.labelKey))
				n.removeAttribute(GraphVisualization.labelKey);
		}
	}

	/** Updates the label on edge e. **/
	private static void updateLabel(Edge e) {
		if (Config.getBoolean("GRAPH_VIS_SHOW_EDGE_WEIGHTS")) {
			if (e.getAttribute(GraphVisualization.weightKey) != null)
				e.addAttribute(GraphVisualization.labelKey,
						"" + e.getAttribute(GraphVisualization.weightKey));
		} else {
			if (e.hasAttribute(GraphVisualization.labelKey))
				e.removeAttribute(GraphVisualization.labelKey);
		}
	}

	/** Projects the (x,y,z)-coordinates to (x,y). **/
	public double[] project3DPointToCoordinates(double x, double y, double z) {
		double x2;
		double y2;

		// projection using vanishing point
		if (this.useVanishingPoint) {
			// calc scaling
			double scale = z / this.vp_z * this.vp_scalingFactor;

			// use logarithmic scaling
			if (Config.getBoolean("GRAPH_VIS_3D_PROJECTION_VP_LOGSCALE"))
				scale = Math.log(scale + 1);

			// keep boundaries
			if (scale > 1)
				scale = 1;
			if (scale < 0)
				scale = 0;

			// calc coordinates
			x2 = x + scale * (this.vp_x - x);
			y2 = y + scale * (this.vp_y - y);
		} else {
			// ortographic projection
			x2 = this.s0_x * x + this.s0_y * y + this.s0_z * z + this.offset_x;
			y2 = this.s1_x * x + this.s1_y * y + this.s1_z * z + this.offset_y;
		}

		// return
		return new double[] { x2, y2 };
	}

	/** Returns the sprite manager. **/
	public SpriteManager getSpriteManager() {
		return this.spriteManager;
	}

	/** Returns the tooltip manager. **/
	public ToolTipManager getToolTipManager() {
		return this.toolTipManager;
	}

	/** Returns if tooltips are enabled. **/
	public boolean isToolTipsEnabled() {
		return this.tooltips;
	}

	/**
	 * Returns the stabilization of the layouter. <br>
	 * 
	 * Returned values will be between 0.0 and 1.0, where 0.0 is unstable and
	 * 1.0 means its completely stabilized and not moving.
	 **/
	public double getStabilization() {
		return this.layouter.getStabilization();
	}

}
