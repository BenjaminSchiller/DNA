package dna.visualization.graph.rules;

import java.awt.Color;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;

import dna.visualization.components.ColorHandler;
import dna.visualization.graph.GraphVisualization;

/** Simple rule example which colors all nodes on addition with colors. **/
public class RandomNodeColor extends GraphStyleRule {

	protected String name;

	protected ColorHandler colorHandler;

	public RandomNodeColor(String name) {
		this.name = name;

		// init color handler
		this.colorHandler = new ColorHandler();
	}

	@Override
	public void onNodeAddition(Node n) {
		// set color
		GraphStyleUtils.setColor(n, this.colorHandler.getNextColor());

		// update node style
		GraphStyleUtils.updateStyle(n);
	}

	@Override
	public void onNodeRemoval(Node n) {
		// free color
		this.colorHandler.removeColor(((Color) n
				.getAttribute(GraphVisualization.colorKey2)));
	}

	@Override
	public void onNodeWeightChange(Node n) {
	}

	@Override
	public void onEdgeAddition(Edge e, Node n1, Node n2) {
	}

	@Override
	public void onEdgeRemoval(Edge e, Node n1, Node n2) {
	}

	@Override
	public void onEdgeWeightChange(Edge e) {
	}

	public String toString() {
		return "RandomNodeColor-Rule '" + this.name + "'";
	}
}
