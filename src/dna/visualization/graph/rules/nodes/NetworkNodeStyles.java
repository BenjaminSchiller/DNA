package dna.visualization.graph.rules.nodes;

import java.awt.Color;

import org.graphstream.graph.Node;

import dna.graph.generators.network.NetflowBatch;
import dna.graph.weights.TypedWeight;
import dna.graph.weights.Weight;
import dna.util.parameters.Parameter;
import dna.visualization.graph.GraphVisualization;
import dna.visualization.graph.rules.GraphStyleRule;
import dna.visualization.graph.rules.GraphStyleUtils;
import dna.visualization.graph.rules.GraphStyleUtils.ElementShape;

/**
 * This rule shape and colors nodes according to their NetworkNodeWeight.
 * 
 * @author Rwilmes
 * 
 */
public class NetworkNodeStyles extends GraphStyleRule {

	protected String name;

	protected ElementShape defaultShape;
	protected ElementShape hostShape;
	protected ElementShape portShape;

	protected Color hostColor;
	protected Color portColor;

	public NetworkNodeStyles(String name, Parameter[] params) {
		this(name, ElementShape.circle, ElementShape.rounded_box, ElementShape.circle, new Color(0, 255, 0),
				new Color(255, 0, 0));
	}

	public NetworkNodeStyles(String name, ElementShape defaultShape, ElementShape hostShape, ElementShape portShape,
			Color hostColor, Color portColor) {
		System.out.println("INIT!");
		this.name = name;
		this.defaultShape = defaultShape;
		this.hostShape = hostShape;
		this.portShape = portShape;
		this.hostColor = hostColor;
		this.portColor = portColor;
	}

	@Override
	public void onNodeAddition(Node n, Weight weight) {
		GraphStyleUtils.setStyleLock(n, true);

		// check type and handle accordingly
		if (weight instanceof TypedWeight) {
			TypedWeight w = (TypedWeight) weight;
			if (w.getType().equals("HOST"))
				handleHostNode(n);
			else if (w.getType().equals("PORT"))
				handlePortNode(n);
			else
				GraphStyleUtils.setShape(n, defaultShape);
		}
	}

	public void handleHostNode(Node n) {
		GraphStyleUtils.setLabel(n, getCurrentNetflowMapping(n));
		n.setAttribute(GraphVisualization.styleKey, host_default_style);
	}

	public void handlePortNode(Node n) {
		GraphStyleUtils.setLabel(n, getCurrentNetflowMapping(n));
		n.setAttribute(GraphVisualization.styleKey, port_style);
	}

	// STATIC STYLE DEFINITIONS
	public static final String test = "fill-color: rgb(0, 100, 255);";

	public static final String default_style = "" + "shape:rounded-box; " + "size:100px,30px; " + "fill-mode:plain; "
			+ "fill-color: rgba(220,220,220, 150); " + "stroke-mode:dots; " + "stroke-color: rgb(40, 40, 40); "
			+ "text-alignment:center;";

	public static final String host_default_style = "" + "shape:rounded-box; " + "size:100px,30px; "
			+ "fill-mode:plain; " + "fill-color: rgb(220,220,220); " + "stroke-mode:dots; "
			+ "stroke-color: rgb(40, 40, 40); " + "text-alignment:center;" + "text-size:12;";

	public static final String port_style = "" + "shape:circle; " + "size:30px; " + "fill-mode:plain; "
			+ "fill-color: rgb(255, 220, 220); " + "stroke-mode:dots; " + "stroke-color: rgb(40, 40, 40); "
			+ "text-alignment:center;" + "text-size:12;";

	// STATIC NETFLOW BATCH GENERATOR TO RETRIEVE MAPPING
	public static NetflowBatch netflowBatchGenerator;

	public static String getCurrentNetflowMapping(Node n) {
		if (netflowBatchGenerator != null) {
			return netflowBatchGenerator.getKey(Integer.parseInt(n.getId()));
		} else {
			return "no netflowbatch";
		}
	}

	@Override
	public String toString() {
		return "NetworkPresentation-Rule: '" + this.name + "'";
	}

}