package dna.visualization.graph.rules;

import org.graphstream.graph.Node;

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
