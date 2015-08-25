package dna.visualization.graph;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
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
import org.graphstream.ui.view.View;
import org.graphstream.ui.view.Viewer;

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

/**
 * The GraphPanel class is used as a JPanel which contains a text-panel and a
 * graph-visualization panel underneath.
 **/
public class GraphPanel extends JPanel {

	public enum PositionMode {
		twoDimension, threeDimension, auto
	};

	private static final long serialVersionUID = 1L;

	// font
	protected final static Font font = new Font("Verdana", Font.PLAIN, 14);

	// name & graph
	protected final JFrame parentFrame;
	protected final String name;
	protected final Graph graph;
	protected final PositionMode mode;

	// rules
	protected ArrayList<GraphStyleRule> rules;
	protected int nextRuleIndex;

	// panels
	protected final JPanel textPanel;
	protected final JLabel textLabel;
	protected final JLabel zoomLabel;
	protected final Layout layouter;
	protected final JButton captureButton;
	protected Color captureButtonFontColor;
	protected Color captureButtonFontColorRecording = new Color(200, 30, 30);
	protected View view;

	// speed factors
	protected double zoomSpeedFactor = Config.getDouble("GRAPH_VIS_ZOOM_SPEED");
	protected double scrollSpeecFactor = Config
			.getDouble("GRAPH_VIS_SCROLL_SPEED");

	// used for dragging
	protected Point dragPos;

	// recording
	protected boolean recording;
	protected VideoRecorder videoRecorder;

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
			PositionMode mode) {
		this(parentFrame, graph, name, mode, new ArrayList<GraphStyleRule>(0));
	}

	public GraphPanel(JFrame parentFrame, final Graph graph, final String name,
			PositionMode mode, ArrayList<GraphStyleRule> rules) {
		this.parentFrame = parentFrame;
		this.name = name;
		this.graph = graph;
		this.mode = mode;
		this.rules = rules;
		this.nextRuleIndex = 0;

		this.recording = false;

		// init textpanel
		this.textPanel = new JPanel();
		textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.X_AXIS));
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
		JButton screenshotButton = new JButton("Screenshot");
		screenshotButton.setPreferredSize(new Dimension(100, 25));
		screenshotButton.setFont(new Font(font.getName(), font.getStyle(), font
				.getSize() - 3));
		screenshotButton
				.setToolTipText("Captures a screenshot and saves it to '"
						+ Config.get("GRAPH_VIS_SCREENSHOT_DIR") + "'");
		textPanel.add(screenshotButton);
		screenshotButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				makeScreenshot(false);
			}
		});

		this.captureButton = new JButton("Video");
		captureButton.setPreferredSize(new Dimension(100, 25));
		captureButton.setFont(new Font(font.getName(), font.getStyle(), font
				.getSize() - 3));
		captureButton.setToolTipText("Captures a video and saves it to '"
				+ Config.get("GRAPH_VIS_VIDEO_DIR") + "'");
		this.captureButtonFontColor = captureButton.getForeground();
		textPanel.add(captureButton);
		captureButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					if (!isRecording())
						makeVideo();
					else
						stopVideo();
				} catch (InterruptedException | IOException e1) {
					e1.printStackTrace();
				}
			}
		});

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
		JPanel graphView = (JPanel) view;

		// main panel
		this.setLayout(new BorderLayout());
		this.add(graphView, BorderLayout.CENTER);
		this.add(textPanel, BorderLayout.PAGE_END);

		// zoom
		graphView.addMouseWheelListener(new MouseWheelListener() {

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

				// set new zoom
				setZoom(zoom);
			}
		});

		// add listener for moving in graph
		graphView.addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseMoved(MouseEvent arg0) {
				// update latest cursor position
				dragPos = arg0.getPoint();
			}

			@Override
			public void mouseDragged(MouseEvent arg0) {
				if ((arg0.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) == InputEvent.CTRL_DOWN_MASK) {
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

	/** Adds a graph style rule. **/
	public void addGraphStyleRule(GraphStyleRule r) {
		this.rules.add(r);
		r.setIndex(this.getNextIndex());
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

	/** Makes a screenshot of the current graph. **/
	public void makeScreenshotUsingGraphstream() {
		String screenshotsDir = Config.get("GRAPH_VIS_SCREENSHOT_DIR");
		String screenshotsSuffix = "."
				+ Config.get("GRAPH_VIS_SCREENSHOT_FORMAT");
		// create dir
		File f = new File(screenshotsDir);
		if (!f.exists() && !f.isFile())
			f.mkdirs();

		// get date format
		DateFormat df = new SimpleDateFormat("yyyy_MM_dd-HH_mm_ss");

		String filename = name + "-" + df.format(new Date());
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
	public void makeScreenshot(boolean waitForStabilization) {
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

		// capture screenshot
		VisualizationUtils.captureScreenshot(this.parentFrame);
	}

	/** Returns the parent frame this GraphPanel is embedded in. **/
	public JFrame getParentFrame() {
		return this.parentFrame;
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
		textLabel.setText(text);
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
			node.addAttribute(GraphVisualization.weightKey, w.toString());
		} else if (n instanceof UndirectedWeightedNode) {
			w = ((UndirectedWeightedNode) n).getWeight();
			node.addAttribute(GraphVisualization.weightKey, w.toString());
		}

		// get and set position
		if (w != null
				&& (this.mode.equals(PositionMode.twoDimension) || this.mode
						.equals(PositionMode.threeDimension))) {

			// get coords from weight
			float[] coords = GraphVisualization.getCoordsFromWeight(w);

			// keep record of min/max coordinates
			statRecord(coords);

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

		// update label
		updateLabel(node);

		// apply style rules
		for (GraphStyleRule r : rules)
			r.onNodeAddition(node);

		// update style
		GraphStyleUtils.updateStyle(node);
	}

	/** Removes node n from graph g. **/
	public void removeNode(dna.graph.nodes.Node n) {
		Node node = this.graph.removeNode("" + n.getIndex());

		for (GraphStyleRule r : rules)
			r.onNodeRemoval(node);

		// update style
		GraphStyleUtils.updateStyle(node);
	}

	/** Changes node weight on node n IN CURRENT GRAPH!!. **/
	public void changeNodeWeight(IWeightedNode n, Weight w) {
		// get node
		Node node = this.graph.getNode("" + n.getIndex());

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
			statRecord(coords);

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

		// if edge not there, add it
		if (this.graph.getNode("" + n1).getEdgeBetween("" + n2) == null) {
			Edge edge = this.graph.addEdge(n1 + "-" + n2, "" + n1, "" + n2,
					directedEdges);

			// init weight
			if (e instanceof DirectedWeightedEdge) {
				Weight w = ((DirectedWeightedEdge) e).getWeight();
				edge.addAttribute(GraphVisualization.weightKey, w.toString());
			} else if (e instanceof UndirectedWeightedEdge) {
				Weight w = ((UndirectedWeightedEdge) e).getWeight();
				edge.addAttribute(GraphVisualization.weightKey, w.toString());
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
			Node node1 = this.graph.getNode("" + n1);
			Node node2 = this.graph.getNode("" + n2);
			for (GraphStyleRule r : rules)
				r.onEdgeAddition(edge, node1, node2);

			// update styles
			GraphStyleUtils.updateStyle(edge);
			GraphStyleUtils.updateStyle(node1);
			GraphStyleUtils.updateStyle(node2);
		}
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
	}

	/** Changes edge weight on edge e IN CURRENT GRAPH!!. **/
	public void changeEdgeWeight(IWeightedEdge e, Weight w) {
		// get indizes
		int n1 = e.getN1().getIndex();
		int n2 = e.getN2().getIndex();

		// get edge
		Edge edge = this.graph.getNode("" + n1).getEdgeBetween("" + n2);

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

	/** Makes a video of the JFrame the panel is embedded in. **/
	public void makeVideo() throws InterruptedException, IOException {
		this.recordingStarted();
		if (this.videoRecorder == null)
			this.videoRecorder = new VideoRecorder(this, this.parentFrame);
		else
			this.videoRecorder.updateDestinationPath();

		this.videoRecorder.start();
	}

	/** Stops the current video recording prematurely. **/
	public void stopVideo() {
		this.videoRecorder.stop();
	}

	/** Updates the video progress. **/
	public void updateVideoProgress(double percent) {
		this.captureButton.setText((int) Math.floor(percent * 100) + "%");
	}

	/** Updates the text on the video button. **/
	public void setVideoButtonText(String text) {
		this.captureButton.setText(text);
	}

	/** Updates the video progress. **/
	public void updateElapsedVideoTime(int seconds) {
		this.captureButton.setText(seconds + "s");
	}

	/** Report that the recording has started. **/
	public void recordingStarted() {
		this.captureButton.setText("Recording");
		this.captureButton.setForeground(this.captureButtonFontColorRecording);

		this.recording = true;
	}

	/** Report that the recording has been stopped. **/
	public void recordingStopped() {
		this.captureButton.setText("Video");
		this.captureButton.setForeground(this.captureButtonFontColor);
		this.recording = false;
	}

	/** Returns if the panel is currently being recorded. **/
	public boolean isRecording() {
		return this.recording;
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
}
