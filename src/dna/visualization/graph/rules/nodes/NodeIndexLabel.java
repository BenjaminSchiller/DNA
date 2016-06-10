package dna.visualization.graph.rules.nodes;

import org.graphstream.graph.Node;

import dna.graph.weights.Weight;
import dna.util.parameters.Parameter;
import dna.util.parameters.StringParameter;
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

	public NodeIndexLabel(String name) {
		this(name, new Parameter[0]);
	}

	public NodeIndexLabel(String name, String label) {
		this(name, new Parameter[] { new StringParameter("label", label) });
	}

	public NodeIndexLabel(String name, Parameter[] params) {
		this.name = name;
		this.label = indexReplacement;

		for (Parameter p : params) {
			if (p.getName().toLowerCase().equals("label"))
				this.label = p.getValue();
		}
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
