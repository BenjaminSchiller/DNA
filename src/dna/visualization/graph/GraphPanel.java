package dna.visualization.graph;

import java.awt.BorderLayout;
import java.awt.Color;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
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
import dna.graph.weights.Double2dWeight;
import dna.graph.weights.Double3dWeight;
import dna.graph.weights.IWeightedEdge;
import dna.graph.weights.IWeightedNode;
import dna.graph.weights.Int2dWeight;
import dna.graph.weights.Int3dWeight;
import dna.graph.weights.Long2dWeight;
import dna.graph.weights.Long3dWeight;
import dna.graph.weights.Weight;
import dna.util.Config;
import dna.util.Log;

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
	protected final String name;
	protected final Graph graph;
	protected final PositionMode mode;

	// panels
	protected final JPanel textPanel;
	protected final JLabel textLabel;
	protected final JLabel zoomLabel;
	protected final Layout layouter;
	protected View view;

	// speed factors
	protected double zoomSpeedFactor = Config.getDouble("GRAPH_VIS_ZOOM_SPEED");
	protected double scrollSpeecFactor = Config
			.getDouble("GRAPH_VIS_SCROLL_SPEED");

	// used for dragging
	protected Point dragPos;

	// enable 3d projection
	protected boolean enable3dProjection = Config
			.getBoolean("GRAPH_VIS_3D_PROJECTION_ENABLED");
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

	protected static double minX = Double.NaN;
	protected static double maxX = Double.NaN;
	protected static double minY = Double.NaN;
	protected static double maxY = Double.NaN;
	protected static double minZ = Double.NaN;
	protected static double maxZ = Double.NaN;

	// constructor
	public GraphPanel(final Graph graph, final String name, PositionMode mode) {
		this.name = name;
		this.graph = graph;
		this.mode = mode;

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
		JButton screenshot = new JButton("Screenshot");
		screenshot.setFont(new Font(font.getName(), font.getStyle(), font
				.getSize() - 3));
		screenshot
				.setToolTipText("Captures a screenshot and saves it to '/images/'");
		textPanel.add(screenshot);
		screenshot.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				makeScreenshot();
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
	public void makeScreenshot() {
		String screenshotsDir = Config.get("GRAPH_VIS_SCREENSHOT_DIR");
		String screenshotsSuffix = Config.get("GRAPH_VIS_SCREENSHOT_SUFFIX");
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

	/*
	 * NODES
	 */

	/** Adds node n to the graph. **/
	public void addNode(dna.graph.nodes.Node n) {
		// add node to graph
		Node node = this.graph.addNode("" + n.getIndex());

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
			float[] coords = getCoordsFromWeight(w);

			// keep record of min/max coordinates
			statRecord(coords);

			// if 3d projection is enabled, project coordinates
			if (this.enable3dProjection) {
				double[] projected2DCoordinates = project3DPointToCoordinates(
						coords[0], coords[1], coords[2]);
				coords = new float[] { (float) projected2DCoordinates[0],
						(float) projected2DCoordinates[1], 0 };
			}

			// add position to node
			node.addAttribute(GraphVisualization.positionKey, coords[0],
					coords[1], coords[2]);
		}

		// update label
		updateLabel(node);

		// change coloring
		if (Config.getBoolean("GRAPH_VIS_COLOR_NODES_BY_DEGREE")
				|| Config.getBoolean("GRAPH_VIS_SIZE_NODES_BY_DEGREE"))
			applyNodeStyleByDegree(node);
	}

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

	/** Removes node n from graph g. **/
	public void removeNode(dna.graph.nodes.Node n) {
		this.graph.removeNode("" + n.getIndex());
	}

	/** Changes node weight on node n IN CURRENT GRAPH!!. **/
	public void changeNodeWeight(IWeightedNode n, Weight w) {
		// get node
		Node node = this.graph.getNode("" + n.getIndex());

		// change weight
		node.changeAttribute(GraphVisualization.weightKey, w);

		// get and set position
		if (w != null
				&& (this.mode.equals(PositionMode.twoDimension) || this.mode
						.equals(PositionMode.threeDimension))) {

			// get coords from weight
			float[] coords = getCoordsFromWeight(w);

			// keep record of min/max coordinates
			statRecord(coords);

			// if 3d projection is enabled, project coordinates
			if (this.enable3dProjection) {
				double[] projected2DCoordinates = project3DPointToCoordinates(
						coords[0], coords[1], coords[2]);
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

			// change node styles
			applyNodeStyleByDegree(this.graph.getNode("" + n1));
			applyNodeStyleByDegree(this.graph.getNode("" + n2));
		}
	}

	/** Removes edge e from graph g. **/
	public void removeEdge(dna.graph.edges.Edge e) {
		// get indizes
		int n1 = e.getN1Index();
		int n2 = e.getN2Index();

		// remove edge
		this.graph.removeEdge(this.graph.getNode("" + n1).getEdgeBetween(
				"" + n2));

		// change coloring
		if (Config.getBoolean("GRAPH_VIS_COLOR_NODES_BY_DEGREE")
				|| Config.getBoolean("GRAPH_VIS_SIZE_NODES_BY_DEGREE")) {
			applyNodeStyleByDegree(graph.getNode("" + n1));
			applyNodeStyleByDegree(graph.getNode("" + n2));
		}
	}

	/** Changes edge weight on edge e IN CURRENT GRAPH!!. **/
	public void changeEdgeWeight(IWeightedEdge e, Weight w) {
		// get indizes
		int n1 = e.getN1().getIndex();
		int n2 = e.getN2().getIndex();

		// get edge
		Edge edge = this.graph.getNode("" + n1).getEdgeBetween("" + n2);

		// change weight
		edge.changeAttribute(GraphVisualization.weightKey, w);

		// update label
		updateLabel(edge);
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

	private void applyNodeStyleByDegree(Node n) {
		// set style stuff
		if (Config.getBoolean("GRAPH_VIS_COLOR_NODES_BY_DEGREE"))
			setNodeColorByDegree(n);
		if (Config.getBoolean("GRAPH_VIS_SIZE_NODES_BY_DEGREE"))
			setNodeSizeByDegree(n);

		// System.out.println(n.getAttribute(GraphVisualization.colorKey) + " "
		// + n.getAttribute(GraphVisualization.sizeKey));

		// set style attribute accordingly
		n.setAttribute(
				GraphVisualization.styleKey,
				n.getAttribute(GraphVisualization.colorKey) + " "
						+ n.getAttribute(GraphVisualization.sizeKey));
	}

	/** Sets the color of the node by its degree. **/
	private void setNodeColorByDegree(Node n) {
		int degree = n.getDegree() - 1;

		// calculate color
		int red = 0;
		int green = 255;
		int blue = 0;
		if (degree >= 0) {
			int weight = degree
					* Config.getInt("GRAPH_VIS_COLOR_AMPLIFICATION");
			if (weight > 255)
				weight = 255;

			red += weight;
			green -= weight;
		}

		// set color attribute
		n.setAttribute(GraphVisualization.colorKey, "fill-color: rgb(" + red
				+ "," + green + "," + blue + ");");
	}

	/** Sets the size of the node by its degree. **/
	private void setNodeSizeByDegree(Node n) {
		// calc size
		int size = Config.getInt("GRAPH_VIS_NODE_DEFAULT_SIZE")
				+ (int) (n.getDegree() * Config
						.getDouble("GRAPH_VIS_NODE_GROWTH_PER_DEGREE"));

		// set size attribute
		n.setAttribute(GraphVisualization.sizeKey, "size: " + size + "px;");
	}

	/**
	 * Gets the coords from the weight, casts it to float and returns it as
	 * float[] = {x, y, z}
	 **/
	protected float[] getCoordsFromWeight(Weight w) {
		float x = 0;
		float y = 0;
		float z = 0;
		if (w instanceof Int2dWeight) {
			x = ((Int2dWeight) w).getX();
			y = ((Int2dWeight) w).getY();
		}
		if (w instanceof Int3dWeight) {
			x = ((Int3dWeight) w).getX();
			y = ((Int3dWeight) w).getY();
			z = ((Int3dWeight) w).getZ();
		}
		if (w instanceof Long2dWeight) {
			x = ((Long2dWeight) w).getX();
			y = ((Long2dWeight) w).getY();
		}
		if (w instanceof Long3dWeight) {
			x = ((Long3dWeight) w).getX();
			y = ((Long3dWeight) w).getY();
			z = ((Long3dWeight) w).getZ();
		}
		if (w instanceof Double2dWeight) {
			x = (float) ((Double2dWeight) w).getX();
			y = (float) ((Double2dWeight) w).getY();
		}
		if (w instanceof Double3dWeight) {
			x = (float) ((Double3dWeight) w).getX();
			y = (float) ((Double3dWeight) w).getY();
			z = (float) ((Double3dWeight) w).getZ();
		}

		return new float[] { x, y, z };
	}

	/** Projects the (x,y,z)-coordinates to (x,y). **/
	public double[] project3DPointToCoordinates(double x, double y, double z) {
		double x2;
		double y2;

		// projection using vanishing point
		if (this.useVanishingPoint) {
			// calc scaling
			double scale = z / this.vp_z;

			// use logarithmic scaling
			if (Config.getBoolean("GRAPH_VIS_3D_PROJECTION_VP_LOGSCALE"))
				scale = Math.log(scale + 1);

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
