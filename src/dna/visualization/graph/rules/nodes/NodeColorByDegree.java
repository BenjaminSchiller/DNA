package dna.visualization.graph.rules.nodes;

import java.awt.Color;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;

import dna.graph.weights.Weight;
import dna.util.parameters.DoubleParameter;
import dna.util.parameters.Parameter;
import dna.visualization.graph.rules.GraphStyleRule;
import dna.visualization.graph.rules.GraphStyleUtils;

public class NodeColorByDegree extends GraphStyleRule {

	protected double amplification;

	public NodeColorByDegree(String name) {
		this(name, new Parameter[0]);
	}

	public NodeColorByDegree(String name, double amplification) {
		this(name, new Parameter[] { new DoubleParameter("amplification",
				amplification) });
	}

	public NodeColorByDegree(String name, Parameter[] params) {
		this.name = name;
		this.amplification = 20;

		for (Parameter p : params) {
			if (p.getName().toLowerCase().equals("amplification")) {
				this.amplification = Double.parseDouble(p.getValue());
			}
		}
	}

	@Override
	public void onNodeAddition(Node n, Weight w) {
		// set color
		GraphStyleUtils.setColor(n, new Color(0, 255, 0));
	}

	@Override
	public void onEdgeAddition(Edge e, Weight w, Node n1, Node n2) {
		// get current colors
		Color c1 = GraphStyleUtils.getColor(n1);
		Color c2 = GraphStyleUtils.getColor(n2);

		// set colors
		GraphStyleUtils.setColor(n1, adaptColor(c1, this.amplification));
		GraphStyleUtils.setColor(n2, adaptColor(c2, this.amplification));
	}

	@Override
	public void onEdgeRemoval(Edge e, Node n1, Node n2) {
		// get current colors
		Color c1 = GraphStyleUtils.getColor(n1);
		Color c2 = GraphStyleUtils.getColor(n2);

		// set colors
		GraphStyleUtils.setColor(n1, adaptColor(c1, -this.amplification));
		GraphStyleUtils.setColor(n2, adaptColor(c2, -this.amplification));
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
