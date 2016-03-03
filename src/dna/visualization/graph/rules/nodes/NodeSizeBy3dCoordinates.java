package dna.visualization.graph.rules.nodes;

import org.graphstream.graph.Node;

import dna.graph.weights.Weight;
import dna.util.Config;
import dna.util.parameters.DoubleParameter;
import dna.util.parameters.Parameter;
import dna.visualization.graph.GraphVisualization;
import dna.visualization.graph.rules.GraphStyleRule;
import dna.visualization.graph.rules.GraphStyleUtils;

public class NodeSizeBy3dCoordinates extends GraphStyleRule {

	protected double baseGrowth;
	protected double shrinkFactor;

	public NodeSizeBy3dCoordinates(String name) {
		this(name, new Parameter[0]);
	}

	public NodeSizeBy3dCoordinates(String name, double growthFactor,
			double shrinkFactor) {
		this(name, new Parameter[] {
				new DoubleParameter("baseGrowth", growthFactor),
				new DoubleParameter("shrinkFactor", shrinkFactor) });
	}

	public NodeSizeBy3dCoordinates(String name, Parameter[] params) {
		this.name = name;
		this.baseGrowth = Config
				.getDouble("GRAPH_VIS_3D_PROJECTION_NODE_GROWTH");
		this.shrinkFactor = Config
				.getDouble("GRAPH_VIS_3D_PROJECTION_NODE_SHRINK_FACTOR");

		for (Parameter p : params) {
			switch (p.getName().toLowerCase()) {
			case "basegrowth":
				this.baseGrowth = Double.parseDouble(p.getValue());
				break;
			case "shrinkfactor":
				this.shrinkFactor = Double.parseDouble(p.getValue());
				break;
			}
		}
	}

	@Override
	public void onNodeAddition(Node n) {
		// increase size by base growth and reduze by z*shrinkfactor
		float z = n.getAttribute(GraphVisualization.zKey);

		GraphStyleUtils.increaseSize(n,
				this.baseGrowth - Math.floor(z * this.shrinkFactor));
	}

	@Override
	public void onNodeWeightChange(Node n, Weight wNew, Weight wOld) {
		// get z-coords
		float zNew = GraphVisualization.getCoordsFromWeight(wNew)[2];
		float zOld = GraphVisualization.getCoordsFromWeight(wOld)[2];

		// calc shrink based on z-diff
		float zDiff = zNew - zOld;
		GraphStyleUtils.decreaseSize(n, Math.floor(zDiff * this.shrinkFactor));
	}

	@Override
	public String toString() {
		return "NodeSizeBy3dCoordinates-Rule: '" + this.name + "'";
	}
}
