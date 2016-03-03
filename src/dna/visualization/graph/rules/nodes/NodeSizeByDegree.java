package dna.visualization.graph.rules.nodes;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;

import dna.graph.weights.Weight;
import dna.util.Config;
import dna.visualization.graph.rules.GraphStyleRule;
import dna.visualization.graph.rules.GraphStyleUtils;

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
	public void onEdgeAddition(Edge e, Weight w, Node n1, Node n2) {
		// increase size
		GraphStyleUtils.increaseSize(n1, this.growthFactor);
		GraphStyleUtils.increaseSize(n2, this.growthFactor);
	}

	@Override
	public void onEdgeRemoval(Edge e, Node n1, Node n2) {
		// decrease size
		GraphStyleUtils.decreaseSize(n1, this.growthFactor);
		GraphStyleUtils.decreaseSize(n2, this.growthFactor);
	}

	@Override
	public String toString() {
		return "NodeSizeByDegree-Rule: '" + this.name + "', growth: "
				+ this.growthFactor;
	}
}
