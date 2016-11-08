package dna.visualization.graph.toolTips.infoLabel;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;
import org.graphstream.ui.spriteManager.Sprite;

import dna.graph.generators.network.NetflowBatch;
import dna.graph.weights.Weight;

/** The NodeDegreeLabel shows the Degree of the Node it is attached to. **/
public class NetworkNodeKeyLabel extends InfoLabel {

	public static NetflowBatch netflowBatchGenerator;

	// constructor
	public NetworkNodeKeyLabel(Sprite s, String name, Node node) {
		super(s, name, node.getId(), LabelValueType.STRING,
				getCurrentNetflowMapping(node));
	}

	public static String getCurrentNetflowMapping(Node n) {
		if (netflowBatchGenerator != null) {
			return netflowBatchGenerator.getKey(Integer.parseInt(n.getId()));
		} else {
			return "no netflowbatch";
		}
	}

	@Override
	public ToolTipType getType() {
		return ToolTipType.INFO_NETWORK_NODE_KEY;
	}

	@Override
	public void onNodeWeightChange(Node n, Weight wNew, Weight wOld) {
		// DO NOTHING
	}

	@Override
	public void onEdgeAddition(Edge e, Node n1, Node n2) {
		increment();
	}

	@Override
	public void onEdgeRemoval(Edge e, Node n1, Node n2) {
		decrement();
	}

}
