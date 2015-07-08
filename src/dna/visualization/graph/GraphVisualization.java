package dna.visualization.graph;

import java.awt.Dimension;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;

import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.ui.layout.Layout;

import dna.graph.Graph;
import dna.graph.edges.Edge;
import dna.graph.nodes.Node;
import dna.graph.weights.IWeightedEdge;
import dna.graph.weights.IWeightedNode;
import dna.graph.weights.Weight;
import dna.util.Config;
import dna.util.Log;

/** The GraphVisualization class offers methods to visualize graphs used in DNA. **/
public class GraphVisualization {
	// statics
	public static final String weightKey = "dna.weight";
	public static final String labelKey = "ui.label";
	public static final String directedKey = "dna.directed";
	public static final String screenshotsKey = "ui.screenshot";
	public static final String qualityKey = "ui.quality";
	public static final String antialiasKey = "ui.antialias";
	public static final String colorKey = "dna.color";
	public static final String sizeKey = "dna.size";
	public static final String styleKey = "ui.style";

	// GUI CONFIG
	protected static final Dimension size = new Dimension(
			Config.getInt("GRAPH_VIS_FRAME_WIDTH"),
			Config.getInt("GRAPH_VIS_FRAME_HEIGHT"));

	// graph map
	protected static HashMap<Graph, GraphPanel> map = new HashMap<Graph, GraphPanel>();

	// current GraphPanel
	protected static GraphPanel currentGraphPanel;

	/*
	 * GRAPH
	 */
	/** Init graph g. **/
	public static void init(Graph g) {
		Log.info("GraphVis - init graph: " + g);

		final String name = g.getName();

		// init graph
		final org.graphstream.graph.Graph graph = new MultiGraph(g.getName());

		// set if directed or undirected
		if (g.getGraphDatastructures().createsDirected())
			graph.addAttribute(directedKey, true);
		else
			graph.addAttribute(directedKey, false);

		// rendering options
		if (Config.getBoolean("GRAPH_VIS_RENDERING_HQ"))
			graph.addAttribute(GraphVisualization.qualityKey);
		if (Config.getBoolean("GRAPH_VIS_RENDERING_ANTIALIAS"))
			graph.addAttribute(GraphVisualization.antialiasKey);

		// main frame

		GraphPanel panel = new GraphPanel(graph, name);
		JFrame mainFrame = new JFrame("Graph-Vis Mainframe");
		mainFrame.add(panel);
		mainFrame.setTitle(g.getName());
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setSize(size);
		mainFrame.setLocationRelativeTo(null);

		// set visible
		mainFrame.setVisible(true);

		// set as current frame
		currentGraphPanel = panel;

		// add to map
		map.put(g, panel);
	}

	/*
	 * NODE
	 */

	/** Adds node n to graph g. **/
	public static void addNode(Graph g, Node n) {
		// wait some time
		waitTime(Config.getInt("GRAPH_VIS_WAIT_NODE_ADDITION"));

		// add node to graph
		org.graphstream.graph.Node node = map.get(g).getGraph()
				.addNode("" + n.getIndex());

		// init weight
		node.addAttribute(weightKey, 0);

		// set label
		if (Config.getBoolean("GRAPH_VIS_SHOW_NODE_INDEX")) {
			String label = "";
			if (Config.getBoolean("GRAPH_VIS_SHOW_NODE_INDEX_VERBOSE"))
				label += n.getIndex();
			else
				label += "Node " + n.getIndex();
			node.addAttribute(labelKey, label);
		}

		// change coloring
		if (Config.getBoolean("GRAPH_VIS_COLOR_NODES_BY_DEGREE")
				|| Config.getBoolean("GRAPH_VIS_SIZE_NODES_BY_DEGREE"))
			applyNodeStyleByDegree(node);
	}

	/** Removes node n from graph g. **/
	public static void removeNode(Graph g, Node n) {
		// wait some time
		waitTime(Config.getInt("GRAPH_VIS_WAIT_NODE_REMOVAL"));

		// get graph
		org.graphstream.graph.Graph graph = map.get(g).getGraph();

		// remove node
		graph.removeNode("" + n.getIndex());
	}

	/** Changes node weight on node n IN CURRENT GRAPH!!. **/
	public static void changeNodeWeight(IWeightedNode n, Weight w) {
		// wait some time
		waitTime(Config.getInt("GRAPH_VIS_WAIT_NODE_WEIGHT_CHANGE"));

		// get graph
		org.graphstream.graph.Graph graph = currentGraphPanel.getGraph();

		// get node
		org.graphstream.graph.Node node = graph.getNode("" + n.getIndex());

		// change weight
		node.changeAttribute(weightKey, w);

		// show weight
		if (Config.getBoolean("GRAPH_VIS_SHOW_EDGE_WEIGHTS")) {
			if (node.hasAttribute(labelKey))
				node.changeAttribute(labelKey, w.toString());
			else
				node.addAttribute(labelKey, w.toString());
		}
	}

	/*
	 * EDGE
	 */

	/** Adds edge e to graph g. **/
	public static void addEdge(Graph g, Edge e) {
		// wait some time
		waitTime(Config.getInt("GRAPH_VIS_WAIT_EDGE_ADDITION"));

		// get graph
		org.graphstream.graph.Graph graph = map.get(g).getGraph();

		// get directed flag
		boolean directedEdges = Config
				.getBoolean("GRAPH_VIS_SHOW_DIRECTED_EDGE_ARROWS")
				&& (boolean) graph.getAttribute(directedKey);

		// get indizes
		int n1 = e.getN1Index();
		int n2 = e.getN2Index();

		// if edge not there, add it
		if (graph.getNode("" + n1).getEdgeBetween("" + n2) == null) {
			org.graphstream.graph.Edge edge = graph.addEdge(n1 + "-" + n2, ""
					+ n1, "" + n2, directedEdges);

			// init weight
			edge.addAttribute(weightKey, 0);

			// add label
			if (Config.getBoolean("GRAPH_VIS_SHOW_EDGE_WEIGHTS"))
				edge.addAttribute(labelKey, 0);
		}

		// change node styles
		applyNodeStyleByDegree(graph.getNode("" + n1));
		applyNodeStyleByDegree(graph.getNode("" + n2));
	}

	public static void applyNodeStyleByDegree(org.graphstream.graph.Node n) {
		// set style stuff
		if (Config.getBoolean("GRAPH_VIS_COLOR_NODES_BY_DEGREE"))
			setNodeColorByDegree(n);
		if (Config.getBoolean("GRAPH_VIS_SIZE_NODES_BY_DEGREE"))
			setNodeSizeByDegree(n);

		// set style attribute accordingly
		n.setAttribute(styleKey,
				n.getAttribute(colorKey) + " " + n.getAttribute(sizeKey));
	}

	/** Sets the color of the node by its degree. **/
	protected static void setNodeColorByDegree(org.graphstream.graph.Node n) {
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
		n.setAttribute(colorKey, "fill-color: rgb(" + red + "," + green + ","
				+ blue + ");");
	}

	/** Sets the size of the node by its degree. **/
	protected static void setNodeSizeByDegree(org.graphstream.graph.Node n) {
		// calc size
		int size = Config.getInt("GRAPH_VIS_NODE_DEFAULT_SIZE")
				+ (int) (n.getDegree() * Config
						.getDouble("GRAPH_VIS_NODE_GROWTH_PER_DEGREE"));

		// set size attribute
		n.setAttribute(sizeKey, "size: " + size + "px;");
	}

	/** Removes edge e from graph g. **/
	public static void removeEdge(Graph g, Edge e) {
		// wait some time
		waitTime(Config.getInt("GRAPH_VIS_WAIT_EDGE_REMOVAL"));

		// get graph
		org.graphstream.graph.Graph graph = map.get(g).getGraph();

		// get indizes
		int n1 = e.getN1Index();
		int n2 = e.getN2Index();

		// remove edge
		graph.removeEdge(graph.getNode("" + n1).getEdgeBetween("" + n2));

		// change coloring
		if (Config.getBoolean("GRAPH_VIS_COLOR_NODES_BY_DEGREE")
				|| Config.getBoolean("GRAPH_VIS_SIZE_NODES_BY_DEGREE")) {
			applyNodeStyleByDegree(graph.getNode("" + n1));
			applyNodeStyleByDegree(graph.getNode("" + n2));
		}
	}

	/** Changes edge weight on edge e IN CURRENT GRAPH!!. **/
	public static void changeEdgeWeight(IWeightedEdge e, Weight w) {
		// wait some time
		waitTime(Config.getInt("GRAPH_VIS_WAIT_EDGE_WEIGHT_CHANGE"));

		// get graph
		org.graphstream.graph.Graph graph = currentGraphPanel.getGraph();

		// get indizes
		int n1 = e.getN1().getIndex();
		int n2 = e.getN2().getIndex();

		// get edge
		org.graphstream.graph.Edge edge = graph.getNode("" + n1)
				.getEdgeBetween("" + n2);

		// change weight
		edge.changeAttribute(weightKey, w);

		// show weight
		if (Config.getBoolean("GRAPH_VIS_SHOW_EDGE_WEIGHTS")) {
			if (edge.hasAttribute(labelKey))
				edge.changeAttribute(labelKey, w.toString());
			else
				edge.addAttribute(labelKey, w.toString());
		}
	}

	/** Wait for specified time in milliseconds. **/
	protected static void waitTime(long milliseconds) {
		if (Config.getBoolean("GRAPH_VIS_WAIT_ENABLED")) {
			try {
				TimeUnit.MILLISECONDS.sleep(milliseconds);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/** Sets the description text for the given graph. **/
	public static void setText(Graph g, String text) {
		map.get(g).setText(text);
	}

	/** Returns the GraphPanel belonging to graph g. **/
	public static GraphPanel getGraphPanel(Graph g) {
		return GraphVisualization.map.get(g);
	}

	/** Returns the CURRENT GraphPanel. **/
	public static GraphPanel getCurrentGraphPanel() {
		return GraphVisualization.currentGraphPanel;
	}

	/** Sets the description text for the CURRENT GRAPH. **/
	public static void setText(String text) {
		currentGraphPanel.setText(text);
	}

	/** Returns the layouter belonging to graph g. **/
	public static Layout getLayouter(Graph g) {
		return GraphVisualization.getGraphPanel(g).getLayouter();
	}

	/** Returns the CURRENT layouter. **/
	public static Layout getLayouter() {
		return GraphVisualization.getCurrentGraphPanel().getLayouter();
	}

	/** Enable graph visualization. **/
	public static void enable() {
		Config.overwrite("GRAPH_VIS_ENABLED", "true");
	}

	/** Disable graph visualization. **/
	public static void disable() {
		Config.overwrite("GRAPH_VIS_ENABLED", "false");
	}

	/** Checks if graph visualization is enabled. **/
	public static boolean isEnabled() {
		return Config.getBoolean("GRAPH_VIS_ENABLED");
	}
}
