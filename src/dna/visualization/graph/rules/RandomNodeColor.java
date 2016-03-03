package dna.visualization.graph.rules;

import java.awt.Color;

import org.graphstream.graph.Node;

import dna.graph.weights.Weight;
import dna.util.parameters.Parameter;
import dna.visualization.components.ColorHandler;
import dna.visualization.graph.GraphVisualization;

/** Simple rule example which colors all nodes on addition with colors. **/
public class RandomNodeColor extends GraphStyleRule {

	protected ColorHandler colorHandler;

	public RandomNodeColor(String name) {
		this(name, new Parameter[0]);
	}

	public RandomNodeColor(String name, Parameter[] params) {
		this.name = name;

		// init color handler
		this.colorHandler = new ColorHandler();
	}

	@Override
	public void onNodeAddition(Node n) {
		// set color
		GraphStyleUtils.setColor(n, this.colorHandler.getNextColor());
	}

	@Override
	public void onNodeRemoval(Node n) {
		// free color
		this.colorHandler.removeColor(((Color) n
				.getAttribute(GraphVisualization.colorKey)));
	}

	@Override
	public String toString() {
		return "RandomNodeColor-Rule: '" + this.name + "'";
	}
}
