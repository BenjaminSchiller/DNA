package dna.visualization.graph.rules;

import org.graphstream.graph.Node;

/** Simple rule example which sets all added nodes to a random size (10;20). **/
public class RandomNodeSize extends GraphStyleRule {

	protected String name;

	public RandomNodeSize(String name) {
		this.name = name;
	}

	@Override
	public void onNodeAddition(Node n) {
		double size = (10 + Math.floor(Math.random() * 10));

		// setting size
		GraphStyleUtils.setSize(n, size);
	}

	@Override
	public String toString() {
		return "RandomNodeSize-Rule: '" + this.name + "'";
	}

}
