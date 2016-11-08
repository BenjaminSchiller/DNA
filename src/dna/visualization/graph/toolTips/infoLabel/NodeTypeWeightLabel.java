package dna.visualization.graph.toolTips.infoLabel;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;
import org.graphstream.ui.spriteManager.Sprite;

import dna.graph.weights.Weight;
import dna.visualization.graph.GraphVisualization;

/** The NodeIdLabel displays the NodeId of the Node it is attached to. **/
public class NodeTypeWeightLabel extends InfoLabel {

	// constructor
	public NodeTypeWeightLabel(Sprite s, String name, Node node) {
		super(s, name, node.getId(), LabelValueType.STRING, getType(node));
	}

	public static String getType(Node n) {
		Weight weight = n.getAttribute(GraphVisualization.weightKey);
		if (weight != null)
			return weight.toString();
		else
			return "null!";
	}

	@Override
	public ToolTipType getType() {
		return ToolTipType.INFO_NODE_TYPE_WEIGHT;
	}

	@Override
	public void onNodeWeightChange(Node n, Weight wNew, Weight wOld) {
		// DO NOTHING
	}

	@Override
	public void onEdgeAddition(Edge e, Node n1, Node n2) {
		// DO NOTHING
	}

	@Override
	public void onEdgeRemoval(Edge e, Node n1, Node n2) {
		// DO NOTHING
	}
}
