package dna.visualization.graph.toolTips.infoLabel;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;
import org.graphstream.ui.spriteManager.Sprite;

import dna.graph.weights.TypedWeight;
import dna.graph.weights.Weight;
import dna.util.parameters.Parameter;
import dna.util.parameters.StringParameter;
import dna.visualization.graph.GraphVisualization;

/** The NodeIdLabel displays the NodeId of the Node it is attached to. **/
public class NodeTypeWeightLabel extends InfoLabel {

	// constructor
	public NodeTypeWeightLabel(Sprite s, String name, Node node) {
		super(s, name, node, new Parameter[] {
				new StringParameter("valuetype",
						LabelValueType.STRING.toString()),
				new StringParameter("value", getType(node)) });
	}

	public static String getType(Node n) {
		Weight weight = n.getAttribute(GraphVisualization.weightKey);
		String type = "null!";
		if (weight != null) {
			if (weight instanceof TypedWeight)
				type = ((TypedWeight) weight).getType();
			else
				type = weight.toString();
		}
		return type;
	}

}
