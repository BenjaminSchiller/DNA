package dna.visualization.graph.rules.nodes;

import org.graphstream.graph.Node;

import dna.visualization.graph.rules.GraphStyleRule;
import dna.visualization.graph.rules.GraphStyleUtils;

/**
 * Simple rule which adds the index of a node to the label.
 * 
 * @author Rwilmes
 * 
 */
public class NodeIndexLabel extends GraphStyleRule {

	@Override
	public void onNodeAddition(Node n) {
		GraphStyleUtils.appendToLabel(n, "" + n.getIndex());
	}

	@Override
	public String toString() {
		return "NodeIndexLabel-Rule";
	}

}
