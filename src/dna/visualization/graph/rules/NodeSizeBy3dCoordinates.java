package dna.visualization.graph.rules;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;

import dna.util.Config;
import dna.visualization.graph.GraphVisualization;

public class NodeSizeBy3dCoordinates extends GraphStyleRule {

	protected double baseGrowth;
	protected double shrinkFactor;

	public NodeSizeBy3dCoordinates(String name) {
		this(name, Config.getDouble("GRAPH_VIS_3D_PROJECTION_NODE_GROWTH"),
				Config.getDouble("GRAPH_VIS_3D_PROJECTION_NODE_SHRINK_FACTOR"));
	}

	public NodeSizeBy3dCoordinates(String name, double growthFactor,
			double shrinkFactor) {
		this.name = name;
		this.baseGrowth = growthFactor;
		this.shrinkFactor = shrinkFactor;
	}

	@Override
	public void onNodeAddition(Node n) {
		// set growth
		GraphStyleUtils.setGrowth(n, this.index, calculateGrowth(n));

		// update style
		GraphStyleUtils.updateStyle(n);
	}

	@Override
	public void onNodeRemoval(Node n) {
	}

	@Override
	public void onNodeWeightChange(Node n) {
		// set growth
		GraphStyleUtils.setGrowth(n, this.index, calculateGrowth(n));

		// update style
		GraphStyleUtils.updateStyle(n);
	}

	@Override
	public void onEdgeAddition(Edge e, Node n1, Node n2) {
	}

	@Override
	public void onEdgeRemoval(Edge e, Node n1, Node n2) {
	}

	@Override
	public void onEdgeWeightChange(Edge e) {
	}

	/** Calculates the size. **/
	protected double calculateGrowth(Node n) {
		float z = n.getAttribute(GraphVisualization.zKey);

		// calc size
		double growth = this.baseGrowth - Math.floor(z * this.shrinkFactor);

		// return growth
		return growth;
	}

	@Override
	public String toString() {
		return "NodeSizeBy3dCoordinates-Rule: '" + this.name + "'";
	}
}
