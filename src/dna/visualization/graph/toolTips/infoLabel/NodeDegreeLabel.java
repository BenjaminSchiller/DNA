package dna.visualization.graph.toolTips.infoLabel;

import org.graphstream.graph.Node;
import org.graphstream.ui.spriteManager.Sprite;

import dna.util.parameters.Parameter;
import dna.util.parameters.StringParameter;
import dna.visualization.graph.toolTips.ToolTip;

/** The NodeDegreeLabel shows the Degree of the Node it is attached to. **/
public class NodeDegreeLabel extends InfoLabel {

	// constructor
	public NodeDegreeLabel(Sprite s, String name, Node n, Parameter[] params) {
		super(s, name, n, new Parameter[] {
				new StringParameter("ValueType", "INT"),
				new StringParameter("Value", "" + n.getDegree()) });
	}

	/** Returns this Button from a sprite. **/
	public static NodeDegreeLabel getFromSprite(Sprite s) {
		return (NodeDegreeLabel) ToolTip.getToolTipFromSprite(s);
	}
}
