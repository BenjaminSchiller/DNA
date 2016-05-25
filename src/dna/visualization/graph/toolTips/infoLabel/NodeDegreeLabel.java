package dna.visualization.graph.toolTips.infoLabel;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;
import org.graphstream.ui.spriteManager.Sprite;

import dna.graph.weights.Weight;
import dna.util.parameters.Parameter;
import dna.util.parameters.StringParameter;

/** The NodeDegreeLabel shows the Degree of the Node it is attached to. **/
public class NodeDegreeLabel extends InfoLabel {

	// constructor
	public NodeDegreeLabel(Sprite s, String name, Node n, Parameter[] params) {
		super(s, name, n, new Parameter[] {
				new StringParameter("ValueType", "INT"),
				new StringParameter("Value", "" + n.getDegree()) });
	}

	@Override
	public ToolTipType getType() {
		return ToolTipType.INFO_NODE_DEGREE;
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
