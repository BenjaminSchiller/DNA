package dna.visualization.graph.rules.nodes;

import org.graphstream.graph.Node;

import dna.graph.weights.Weight;
import dna.util.parameters.Parameter;
import dna.visualization.graph.rules.GraphStyleRule;
import dna.visualization.graph.rules.GraphStyleUtils;

/** Simple rule example which sets all added nodes to a random size (10;20). **/
public class RandomNodeSize extends GraphStyleRule {

	protected String name;

	public RandomNodeSize(String name) {
		this(name, new Parameter[0]);
	}

	public RandomNodeSize(String name, Parameter[] params) {
		this.name = name;
	}

	@Override
	public void onNodeAddition(Node n, Weight w) {
		double size = (10 + Math.floor(Math.random() * 10));

		// setting size
		GraphStyleUtils.setSize(n, size);
	}

	@Override
	public String toString() {
		return "RandomNodeSize-Rule: '" + this.name + "'";
	}

}
