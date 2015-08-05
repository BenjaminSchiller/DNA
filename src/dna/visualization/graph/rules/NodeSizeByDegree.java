package dna.visualization.graph.rules;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;

import dna.util.Config;

/** Sizes the nodes by their degree. **/
public class NodeSizeByDegree extends GraphStyleRule {

	protected double growthFactor;

	public NodeSizeByDegree(String name) {
		this(name, Config.getDouble("GRAPH_VIS_NODE_GROWTH_PER_DEGREE"));
	}

	public NodeSizeByDegree(String name, double growthFactor) {
		this.name = name;
		this.growthFactor = growthFactor;
	}

	@Override
	public void onNodeAddition(Node n) {
		// sets the size-growth given by this rule
		GraphStyleUtils.setGrowth(n, this.index, calculateGrowth(n));

		// update node style
		GraphStyleUtils.updateStyle(n);
	}

	@Override
	public void onNodeRemoval(Node n) {
	}

	@Override
	public void onNodeWeightChange(Node n) {
	}

	@Override
	public void onEdgeAddition(Edge e, Node n1, Node n2) {
		// set growths
		GraphStyleUtils.setGrowth(n1, this.index, calculateGrowth(n1));
		GraphStyleUtils.setGrowth(n2, this.index, calculateGrowth(n2));

		// update node styles
		GraphStyleUtils.updateStyle(n1);
		GraphStyleUtils.updateStyle(n2);
	}

	@Override
	public void onEdgeRemoval(Edge e, Node n1, Node n2) {
		// set growths
		GraphStyleUtils.setGrowth(n1, this.index, calculateGrowth(n1));
		GraphStyleUtils.setGrowth(n2, this.index, calculateGrowth(n2));

		// update node styles
		GraphStyleUtils.updateStyle(n1);
		GraphStyleUtils.updateStyle(n2);
	}

	@Override
	public void onEdgeWeightChange(Edge e) {
	}

	/** Calculates the nodes size based on its degree. **/
	protected double calculateGrowth(Node n) {
		return n.getDegree() * this.growthFactor;
	}

	@Override
	public String toString() {
		return "NodeSizeByDegree-Rule: '" + this.name + "', growth: "
				+ this.growthFactor;
	}
}
