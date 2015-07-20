package dna.visualization.graph;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
	protected final Layout layouter;
	public View view;

	// enable 3d projection
	public static final boolean enable3dProjection = Config
			.getBoolean("GRAPH_VIS_3D_PROJECTION_ENABLED");

	// scaling matrix
	protected final double s0x = Config
			.getDouble("GRAPH_VIS_3D_PROJECTION_S0X");
	protected final double s0y = Config
			.getDouble("GRAPH_VIS_3D_PROJECTION_S0Y");
	protected final double s0z = Config
			.getDouble("GRAPH_VIS_3D_PROJECTION_S0Z");

	protected final double s1x = Config
			.getDouble("GRAPH_VIS_3D_PROJECTION_S1X");
	protected final double s1y = Config
			.getDouble("GRAPH_VIS_3D_PROJECTION_S1Y");
	protected final double s1z = Config
			.getDouble("GRAPH_VIS_3D_PROJECTION_S1Z");

	// offset vector
	protected final double cx = Config
			.getDouble("GRAPH_VIS_3D_PROJECTION_OFFSETX");
	protected final double cy = Config
			.getDouble("GRAPH_VIS_3D_PROJECTION_OFFSETY");

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
				String screenshotsDir = Config.get("GRAPH_VIS_SCREENSHOT_DIR");
				String screenshotsSuffix = Config
						.get("GRAPH_VIS_SCREENSHOT_SUFFIX");
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
				Log.info("GraphVis - saving screenshot to '" + f2.getPath()
						+ "'");
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
		this.add(textPanel, BorderLayout.PAGE_START);
		this.add(graphView, BorderLayout.CENTER);
	}

	/** Sets the text-label to the input text. **/
	public void setText(String text) {
		textLabel.setText(text);
	}

	/** Returns the embedded graphstream.graph. **/
	public Graph getGraph() {
		return this.graph;
	}

	/** Returns the layouter of the embedded graphstream graph. **/
	public Layout getLayouter() {
		return this.layouter;
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

		// if 3d projection is enabled, project coordinates
		if (enable3dProjection) {
			double[] projected2DCoordinates = project3DPointToCoordinates(x, y,
					z);
			return new float[] { (float) projected2DCoordinates[0],
					(float) projected2DCoordinates[1], 0 };
		} else {
			return new float[] { x, y, z };
		}
	}

	/** Projects the (x,y,z)-coordinates to (x,y). **/
	public double[] project3DPointToCoordinates(double x, double y, double z) {
		double x2 = s0x * x + s0y * y + s0z * z + cx;
		double y2 = s1x * x + s1y * y + s1z * z + cy;

		return new double[] { x2, y2 };

	}
}
