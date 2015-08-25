package dna.visualization.graph.rules;

import java.awt.Color;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;

import dna.graph.weights.Weight;
import dna.visualization.components.ColorHandler;
import dna.visualization.graph.GraphVisualization;

/** Simple rule example which colors all nodes on addition with colors. **/
public class RandomNodeColor extends GraphStyleRule {

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
				.getAttribute(GraphVisualization.colorKey)));
	}

	@Override
	public void onNodeWeightChange(Node n, Weight wNew, Weight wOld) {
	}

	@Override
	public void onEdgeAddition(Edge e, Node n1, Node n2) {
	}

	@Override
	public void onEdgeRemoval(Edge e, Node n1, Node n2) {
	}

	@Override
	public void onEdgeWeightChange(Edge e, Weight wNew, Weight wOld) {
	}

	@Override
	public String toString() {
		return "RandomNodeColor-Rule: '" + this.name + "'";
	}
}
