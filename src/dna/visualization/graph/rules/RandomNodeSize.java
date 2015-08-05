package dna.visualization.graph.rules;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;

/** Simple rule example which sets all nodes to a random size (10;20). **/
public class RandomNodeSize extends GraphStyleRule {

	protected String name;

	public RandomNodeSize(String name) {
		this.name = name;
	}

	@Override
	public void onNodeAddition(Node n) {
		// TODO Auto-generated method stub
		double size = (10 + Math.floor(Math.random() * 10));

		// setting size
		GraphStyleUtils.setSize(n, size);

		// update style
		GraphStyleUtils.updateStyle(n);
	}

	@Override
	public void onNodeRemoval(Node n) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onNodeWeightChange(Node n) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onEdgeAddition(Edge e, Node n1, Node n2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onEdgeRemoval(Edge e, Node n1, Node n2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onEdgeWeightChange(Edge e) {
		// TODO Auto-generated method stub

	}

}
