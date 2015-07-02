package dna.visualization.graph;

import java.awt.Dimension;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;
import javax.swing.JLabel;

import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.ui.layout.Layout;

import dna.graph.Graph;
import dna.graph.edges.Edge;
import dna.graph.nodes.Node;
import dna.graph.weights.IWeightedEdge;
import dna.graph.weights.IWeightedNode;
import dna.graph.weights.Weight;
import dna.util.Log;

public class GraphVisualization {
	// config
	protected static boolean enabled = false;

	// GUI CONFIG
	protected static final Dimension size = new Dimension(1024, 768);

	// high quality rendering / anti-aliasing
	protected static final boolean rendering_hq = false;
	protected static final boolean rendering_antialias = false;

	// statics
	protected static final String weightKey = "dna.weight";
	protected static final String labelKey = "ui.label";
	protected static final String directedKey = "dna.directed";
	protected static final String screenshotsKey = "ui.screenshot";
	protected static final String screenshotsDir = "images/";
	protected static final String screenshotsSuffix = ".png";

	// graph map
	protected static HashMap<Graph, org.graphstream.graph.Graph> map = new HashMap<Graph, org.graphstream.graph.Graph>();
	protected static org.graphstream.graph.Graph currentGraph;

	// graph to text-pane map
	protected static HashMap<Graph, JLabel> labelMap = new HashMap<Graph, JLabel>();
	protected static JLabel currentLabel;
	protected static Layout currentLayouter;

	// labels
	public static boolean showNodeIndex = false;
	public static boolean nodeIndexVerbose = true;
	public static boolean showNodeWeight = true;
	public static boolean showEdgeWeights = false;
	public static boolean showDirectedEdgeArrows = true;

	// node color
	public static boolean colorNodesByDegree = true;
	public static int nodeColorAmplification = 20;

	// wait times
	public static boolean waitTimes_enabled = true;

	public static long waitTimeNodeAddition = 20;
	public static long waitTimeNodeRemoval = 20;
	public static long waitTimeNodeWeightChange = 10;

	public static long waitTimeEdgeAddition = 20;
	public static long waitTimeEdgeRemoval = 20;
	public static long waitTimeEdgeWeightChange = 10;

	public static void enable() {
		enabled = true;
	}

	public static void disable() {
		enabled = false;
	}

	public static boolean isEnabled() {
		return enabled;
	}

	public static org.graphstream.graph.Graph getCurrentGraph() {
		return currentGraph;
	}

	public static Layout getCurrentLayouter() {
		return currentLayouter;
	}

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

		// add graph to map
		map.put(g, graph);

		// set graph as current graph
		currentGraph = graph;

		// rendering options
		if (rendering_hq)
			graph.addAttribute("ui.quality");
		if (rendering_antialias)
			graph.addAttribute("ui.antialias");

		// main frame
		JFrame mainFrame = new JFrame("Graph-Vis Mainframe2");
		mainFrame.add(new GraphPanel(graph, name));
		mainFrame.setTitle(g.getName());
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setSize(size);
		mainFrame.setLocationRelativeTo(null);

		// set visible
		mainFrame.setVisible(true);
	}

	/*
	 * NODE
	 */

	/** Adds node n to graph g. **/
	public static void addNode(Graph g, Node n) {
		// wait some time
		waitTime(waitTimeNodeAddition);

		// add node to graph
		org.graphstream.graph.Node node = map.get(g).addNode("" + n.getIndex());

		// init weight
		node.addAttribute(weightKey, 0);

		// set label
		if (showNodeIndex) {
			String label = "";
			if (nodeIndexVerbose)
				label += n.getIndex();
			else
				label += "Node " + n.getIndex();
			node.addAttribute(labelKey, label);
		}

		// change coloring
		if (colorNodesByDegree)
			colorNodeByDegree(node);
	}

	/** Removes node n from graph g. **/
	public static void removeNode(Graph g, Node n) {
		// wait some time
		waitTime(waitTimeNodeRemoval);

		// get graph
		org.graphstream.graph.Graph graph = map.get(g);

		// remove node
		graph.removeNode("" + n.getIndex());
	}

	/** Changes node weight on node n IN CURRENT GRAPH!!. **/
	public static void changeNodeWeight(IWeightedNode n, Weight w) {
		// wait some time
		waitTime(waitTimeNodeWeightChange);

		// get graph
		org.graphstream.graph.Graph graph = currentGraph;

		// get node
		org.graphstream.graph.Node node = graph.getNode("" + n.getIndex());

		// change weight
		node.changeAttribute(weightKey, w);

		// show weight
		if (showEdgeWeights) {
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
		waitTime(waitTimeEdgeAddition);

		// get graph
		org.graphstream.graph.Graph graph = map.get(g);

		// get directed flag
		boolean directedEdges = showDirectedEdgeArrows
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
			if (showEdgeWeights)
				edge.addAttribute(labelKey, 0);
		}

		// change coloring
		if (colorNodesByDegree) {
			colorNodeByDegree(graph.getNode("" + n1));
			colorNodeByDegree(graph.getNode("" + n2));
		}
	}

	public static void colorNodeByDegree(org.graphstream.graph.Node n) {
		int degree = n.getDegree() - 1;

		// calculate color
		int red = 0;
		int green = 255;
		int blue = 0;
		if (degree >= 0) {
			int weight = degree * nodeColorAmplification;
			if (weight > 255)
				weight = 255;

			red += weight;
			green -= weight;
		}

		n.setAttribute("ui.style", "fill-color: rgb(" + red + "," + green + ","
				+ blue + ");");
	}

	/** Removes edge e from graph g. **/
	public static void removeEdge(Graph g, Edge e) {
		// wait some time
		waitTime(waitTimeEdgeRemoval);

		// get graph
		org.graphstream.graph.Graph graph = map.get(g);

		// get indizes
		int n1 = e.getN1Index();
		int n2 = e.getN2Index();

		// remove edge
		graph.removeEdge(graph.getNode("" + n1).getEdgeBetween("" + n2));

		// change coloring
		if (colorNodesByDegree) {
			colorNodeByDegree(graph.getNode("" + n1));
			colorNodeByDegree(graph.getNode("" + n2));
		}
	}

	/** Changes edge weight on edge e IN CURRENT GRAPH!!. **/
	public static void changeEdgeWeight(IWeightedEdge e, Weight w) {
		// wait some time
		waitTime(waitTimeEdgeWeightChange);

		// get graph
		org.graphstream.graph.Graph graph = currentGraph;

		// get indizes
		int n1 = e.getN1().getIndex();
		int n2 = e.getN2().getIndex();

		// get edge
		org.graphstream.graph.Edge edge = graph.getNode("" + n1)
				.getEdgeBetween("" + n2);

		// change weight
		edge.changeAttribute(weightKey, w);

		// show weight
		if (showEdgeWeights) {
			if (edge.hasAttribute(labelKey))
				edge.changeAttribute(labelKey, w.toString());
			else
				edge.addAttribute(labelKey, w.toString());
		}
	}

	/** Wait for specified time in milliseconds. **/
	protected static void waitTime(long milliseconds) {
		if (waitTimes_enabled) {
			try {
				TimeUnit.MILLISECONDS.sleep(milliseconds);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/** Sets the description text for the given graph. **/
	public static void setText(Graph g, String text) {
		labelMap.get(g).setText(text);
	}

	/** Sets the description text for the CURRENT GRAPH. **/
	public static void setText(String text) {
		currentLabel.setText(text);
	}
}
