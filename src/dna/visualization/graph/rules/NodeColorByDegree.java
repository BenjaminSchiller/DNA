package dna.visualization.graph.rules;

import java.awt.Color;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;

import dna.util.Config;

public class NodeColorByDegree extends GraphStyleRule {

	protected double amplification;

	public NodeColorByDegree(String name) {
		this(name, Config.getDouble("GRAPH_VIS_COLOR_AMPLIFICATION"));
	}

	public NodeColorByDegree(String name, double amplification) {
		this.name = name;
		this.amplification = amplification;
	}

	@Override
	public void onNodeAddition(Node n) {
		// set size
		GraphStyleUtils.setColor(n, calculateColor(n));

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
		GraphStyleUtils.setColor(n1, calculateColor(n1));
		GraphStyleUtils.setColor(n2, calculateColor(n2));

		// update node styles
		GraphStyleUtils.updateStyle(n1);
		GraphStyleUtils.updateStyle(n2);
	}

	@Override
	public void onEdgeRemoval(Edge e, Node n1, Node n2) {
		// set sizes
		GraphStyleUtils.setColor(n1, calculateColor(n1));
		GraphStyleUtils.setColor(n2, calculateColor(n2));

		// update node styles
		GraphStyleUtils.updateStyle(n1);
		GraphStyleUtils.updateStyle(n2);
	}

	@Override
	public void onEdgeWeightChange(Edge e) {
	}

	/** Sets the color of the node by its degree. **/
	protected Color calculateColor(Node n) {
		int degree = n.getDegree() - 1;

		// calculate color
		int red = 0;
		int green = 255;
		int blue = 0;
		if (degree >= 0) {
			int weight = (int) Math.floor(degree * this.amplification);
			if (weight > 255)
				weight = 255;

			red += weight;
			green -= weight;
		}

		return new Color(red, green, blue);
	}

	@Override
	public String toString() {
		return "NodeColorByDegree-Rule: '" + this.name + "'";
	}

}
