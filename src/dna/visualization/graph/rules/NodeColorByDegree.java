package dna.visualization.graph.rules;

import java.awt.Color;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;

import dna.graph.weights.Weight;
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
		GraphStyleUtils.setColor(n, new Color(0, 255, 0));
		// // set size
		// GraphStyleUtils.setColor(n, calculateColor(n));
		//
		// // update node style
		GraphStyleUtils.updateStyle(n);
	}

	@Override
	public void onNodeRemoval(Node n) {
	}

	@Override
	public void onNodeWeightChange(Node n, Weight wNew, Weight wOld) {
	}

	@Override
	public void onEdgeAddition(Edge e, Node n1, Node n2) {
		// get current colors
		Color c1 = GraphStyleUtils.getColor(n1);
		Color c2 = GraphStyleUtils.getColor(n2);

		// set colors
		GraphStyleUtils.setColor(n1, adaptColor(c1, this.amplification));
		GraphStyleUtils.setColor(n2, adaptColor(c2, this.amplification));

		// update node styles
		GraphStyleUtils.updateStyle(n1);
		GraphStyleUtils.updateStyle(n2);
	}

	@Override
	public void onEdgeRemoval(Edge e, Node n1, Node n2) {
		// get current colors
		Color c1 = GraphStyleUtils.getColor(n1);
		Color c2 = GraphStyleUtils.getColor(n2);

		// set colors
		GraphStyleUtils.setColor(n1, adaptColor(c1, -this.amplification));
		GraphStyleUtils.setColor(n2, adaptColor(c2, -this.amplification));

		// update node styles
		GraphStyleUtils.updateStyle(n1);
		GraphStyleUtils.updateStyle(n2);
	}

	@Override
	public void onEdgeWeightChange(Edge e, Weight wNew, Weight wOld) {
	}

	/** Calculates a new color based on the amplification parameter. **/
	protected Color adaptColor(Color c, double amplification) {
		int red = (int) Math.floor(c.getRed() + this.amplification);
		red = red < 0 ? 0 : red;
		red = red > 255 ? 255 : red;

		int green = (int) Math.floor(c.getGreen() - this.amplification);
		green = green < 0 ? 0 : green;
		green = green > 255 ? 255 : green;

		return new Color(red, green, c.getBlue());
	}

	@Override
	public String toString() {
		return "NodeColorByDegree-Rule: '" + this.name + "'";
	}

}
