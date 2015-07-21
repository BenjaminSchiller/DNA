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
import dna.visualization.graph.GraphPanel.PositionMode;

/** The GraphVisualization class offers methods to visualize graphs used in DNA. **/
public class GraphVisualization {
	// statics
	public static final String positionKey = "xyz";
	public static final String weightKey = "dna.weight";
	public static final String labelKey = "ui.label";
	public static final String directedKey = "dna.directed";
	public static final String screenshotsKey = "ui.screenshot";
	public static final String qualityKey = "ui.quality";
	public static final String antialiasKey = "ui.antialias";
	public static final String colorKey = "dna.color";
	public static final String sizeKey = "dna.size";
	public static final String styleKey = "ui.style";
	public static final String zKey = "dna.z";

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

		// check nodeweighttypes for position modes
		PositionMode mode = PositionMode.auto;
		Class<? extends Weight> nwt = g.getGraphDatastructures()
				.getNodeWeightType();
		if (nwt != null
				&& (nwt.equals(Int2dWeight.class)
						|| nwt.equals(Long2dWeight.class) || nwt
							.equals(Double2dWeight.class)))
			mode = PositionMode.twoDimension;
		if (nwt != null
				&& (nwt.equals(Int3dWeight.class)
						|| nwt.equals(Long3dWeight.class) || nwt
							.equals(Double3dWeight.class)))
			mode = PositionMode.threeDimension;

		// main frame
		GraphPanel panel = new GraphPanel(graph, name, mode);
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

		// add node
		map.get(g).addNode(n);
	}

	/** Removes node n from graph g. **/
	public static void removeNode(Graph g, Node n) {
		// wait some time
		waitTime(Config.getInt("GRAPH_VIS_WAIT_NODE_REMOVAL"));

		// get graph
		map.get(g).removeNode(n);
	}

	/** Changes node weight on node n IN CURRENT GRAPH!!. **/
	public static void changeNodeWeight(IWeightedNode n, Weight w) {
		// wait some time
		waitTime(Config.getInt("GRAPH_VIS_WAIT_NODE_WEIGHT_CHANGE"));

		// get graph
		currentGraphPanel.changeNodeWeight(n, w);
	}

	/*
	 * EDGE
	 */

	/** Adds edge e to graph g. **/
	public static void addEdge(Graph g, Edge e) {
		// wait some time
		waitTime(Config.getInt("GRAPH_VIS_WAIT_EDGE_ADDITION"));

		// add edge
		map.get(g).addEdge(e);
	}

	/** Removes edge e from graph g. **/
	public static void removeEdge(Graph g, Edge e) {
		// wait some time
		waitTime(Config.getInt("GRAPH_VIS_WAIT_EDGE_REMOVAL"));

		// get graph
		map.get(g).removeEdge(e);
	}

	/** Changes edge weight on edge e IN CURRENT GRAPH!!. **/
	public static void changeEdgeWeight(IWeightedEdge e, Weight w) {
		// wait some time
		waitTime(Config.getInt("GRAPH_VIS_WAIT_EDGE_WEIGHT_CHANGE"));

		// get graph
		currentGraphPanel.changeEdgeWeight(e, w);
	}

	/*
	 * MISC
	 */

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
