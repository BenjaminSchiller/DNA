package dna.visualization.graph.rules.nodes;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;

import dna.util.Config;
import dna.util.parameters.DoubleParameter;
import dna.util.parameters.Parameter;
import dna.visualization.graph.rules.GraphStyleRule;
import dna.visualization.graph.rules.GraphStyleUtils;

/** Sizes the nodes by their degree. **/
public class NodeSizeByDegree extends GraphStyleRule {

	protected double growthFactor;

	public NodeSizeByDegree(String name) {
		this(name, new Parameter[0]);
	}

	public NodeSizeByDegree(String name, double growthFactor) {
		this(name,
				new Parameter[] { new DoubleParameter("growth", growthFactor) });
	}

	public NodeSizeByDegree(String name, Parameter[] params) {
		this.name = name;
		this.growthFactor = Config
				.getDouble("GRAPH_VIS_NODE_GROWTH_PER_DEGREE");

		for (Parameter p : params) {
			if (p.getName().toLowerCase().equals("growth"))
				this.growthFactor = Double.parseDouble(p.getValue());
		}
	}

	@Override
	public void onEdgeAddition(Edge e, Node n1, Node n2) {
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
