package dna.visualization.graph.rules;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;

import dna.util.Config;

/** Sizes the nodes by their degree. **/
public class NodeSizeByDegree extends GraphStyleRule {

	protected double baseSize;
	protected double growthFactor;

	public NodeSizeByDegree(String name) {
		this(name, Config.getInt("GRAPH_VIS_NODE_DEFAULT_SIZE"), Config
				.getDouble("GRAPH_VIS_NODE_GROWTH_PER_DEGREE"));
	}

	public NodeSizeByDegree(String name, double baseSize, double growthFactor) {
		this.name = name;
		this.baseSize = baseSize;
		this.growthFactor = growthFactor;
	}

	@Override
	public void onNodeAddition(Node n) {
		GraphStyleUtils.setSize(n, calculateSize(n));

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
		// set sizes
		GraphStyleUtils.setSize(n1, calculateSize(n1));
		GraphStyleUtils.setSize(n2, calculateSize(n2));

		// update node styles
		GraphStyleUtils.updateStyle(n1);
		GraphStyleUtils.updateStyle(n2);
	}

	@Override
	public void onEdgeRemoval(Edge e, Node n1, Node n2) {
		// set sizes
		GraphStyleUtils.setSize(n1, calculateSize(n1));
		GraphStyleUtils.setSize(n2, calculateSize(n2));

		// update node styles
		GraphStyleUtils.updateStyle(n1);
		GraphStyleUtils.updateStyle(n2);
	}

	@Override
	public void onEdgeWeightChange(Edge e) {
	}

	/** Calculates the nodes size based on its degree. **/
	protected double calculateSize(Node n) {
		return this.baseSize + (n.getDegree() * this.growthFactor);
	}

	@Override
	public String toString() {
		return "NodeSizeByDegree-Rule: '" + this.name + "', base-size: "
				+ this.baseSize + ", growth: " + this.growthFactor;
	}
}
