package dna.visualization.graph.rules.nodes;

import org.graphstream.graph.Node;

import dna.graph.weights.Weight;
import dna.visualization.graph.rules.GraphStyleRule;
import dna.visualization.graph.rules.GraphStyleUtils;

/**
 * Simple rule which adds the index of a node to the label.
 * 
 * @author Rwilmes
 * 
 */
public class NodeIndexLabel extends GraphStyleRule {

	public static final String indexReplacement = "$index$";

	protected String label;

	public NodeIndexLabel() {
		this(indexReplacement);
	}

	public NodeIndexLabel(String label) {
		this.label = label;
	}

	@Override
	public void onNodeAddition(Node n, Weight w) {
		GraphStyleUtils.appendToLabel(n, craftLabel(n.getIndex()));
	}

	@Override
	public String toString() {
		return "NodeIndexLabel-Rule";
	}

	protected String craftLabel(int index) {
		return this.label.replace(indexReplacement, "" + index);
	}
}
