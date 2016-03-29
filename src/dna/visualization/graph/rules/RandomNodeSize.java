package dna.visualization.graph.rules;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;

import dna.graph.weights.Weight;
import dna.util.parameters.Parameter;

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
	public void onNodeAddition(Node n) {
		double size = (10 + Math.floor(Math.random() * 10));

		// setting size
		GraphStyleUtils.setSize(n, size);
	}

	@Override
	public void onNodeRemoval(Node n) {
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
		return "RandomNodeSize-Rule: '" + this.name + "'";
	}

}
