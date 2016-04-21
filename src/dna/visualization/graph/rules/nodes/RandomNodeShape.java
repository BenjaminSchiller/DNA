package dna.visualization.graph.rules.nodes;

import org.graphstream.graph.Node;

import dna.graph.weights.Weight;
import dna.util.parameters.Parameter;
import dna.visualization.graph.rules.GraphStyleRule;
import dna.visualization.graph.rules.GraphStyleUtils;

/** Simple rule example which sets all added nodes to a random shape. **/
public class RandomNodeShape extends GraphStyleRule {

	protected String name;

	public RandomNodeShape(String name) {
		this(name, new Parameter[0]);
	}

	public RandomNodeShape(String name, Parameter[] params) {
		this.name = name;
	}

	@Override
	public void onNodeAddition(Node n, Weight w) {
//		int x = (int) Math.floor(Math.random() * 6);
//
//		// setting shape
//		switch (x) {
//		case 0:
//			GraphStyleUtils.setShape(n, ElementShape.box);
//			break;
//		case 1:
//			GraphStyleUtils.setShape(n, ElementShape.circle);
//			break;
//		case 2:
//			GraphStyleUtils.setShape(n, ElementShape.cross);
//			break;
//		case 3:
//			GraphStyleUtils.setShape(n, ElementShape.diamond);
//			break;
//		case 4:
//			GraphStyleUtils.setShape(n, ElementShape.rounded_box);
//			break;
//		case 5:
//			GraphStyleUtils.setShape(n, ElementShape.triangle);
//			break;
//		}
	}

	@Override
	public String toString() {
		return "RandomNodeShape-Rule: '" + this.name + "'";
	}

}
