package dna.visualization.graph.rules.nodes;

import java.awt.Color;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;

import dna.util.Config;
import dna.visualization.graph.rules.GraphStyleRule;
import dna.visualization.graph.rules.GraphStyleUtils;

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
		// set color
		GraphStyleUtils.setColor(n, new Color(0, 255, 0));
	}

	@Override
	public void onEdgeAddition(Edge e, Node n1, Node n2) {
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
