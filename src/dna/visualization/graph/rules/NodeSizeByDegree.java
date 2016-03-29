package dna.visualization.graph.rules;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;

import dna.graph.weights.Weight;
import dna.util.Config;
import dna.util.parameters.DoubleParameter;
import dna.util.parameters.Parameter;

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
	public void onNodeAddition(Node n) {
	}

	@Override
	public void onNodeRemoval(Node n) {
	}

	@Override
	public void onNodeWeightChange(Node n, Weight wNew, Weight wOld) {
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
	public void onEdgeWeightChange(Edge e, Weight wNew, Weight wOld) {
	}

	@Override
	public String toString() {
		return "NodeSizeByDegree-Rule: '" + this.name + "', growth: "
				+ this.growthFactor;
	}
}
