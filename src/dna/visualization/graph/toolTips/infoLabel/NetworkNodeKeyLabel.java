package dna.visualization.graph.toolTips.infoLabel;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;
import org.graphstream.ui.spriteManager.Sprite;

import dna.graph.generators.network.NetflowBatch;
import dna.graph.weights.Weight;
import dna.util.parameters.Parameter;
import dna.util.parameters.StringParameter;

/** The NodeDegreeLabel shows the Degree of the Node it is attached to. **/
public class NetworkNodeKeyLabel extends InfoLabel {

	public static NetflowBatch netflowBatchGenerator;

	// constructor
	public NetworkNodeKeyLabel(Sprite s, String name, Node node) {
		super(s, name, node, new Parameter[] {
				new StringParameter("valutetype",
						LabelValueType.STRING.toString()),
				new StringParameter("value", getCurrentNetflowMapping(node)) });
	}

	public static String getCurrentNetflowMapping(Node n) {
		if (netflowBatchGenerator != null) {
			return netflowBatchGenerator.getKey(Integer.parseInt(n.getId()));
		} else {
			return "no netflowbatch";
		}
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
