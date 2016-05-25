package dna.visualization.graph.toolTips.infoLabel;

import org.graphstream.graph.Node;
import org.graphstream.ui.spriteManager.Sprite;

import dna.util.parameters.Parameter;
import dna.util.parameters.StringParameter;
import dna.visualization.graph.toolTips.ToolTip;

/** The NodeIdLabel displays the NodeId of the Node it is attached to. **/
public class NodeIdLabel extends InfoLabel {

	// constructor
	public NodeIdLabel(Sprite s, String name, Node n, Parameter[] params) {
		super(s, name, n, new Parameter[] {
				new StringParameter("ValueType", "INT"),
				new StringParameter("Value", "" + n.getIndex()) });
	}

	/** Returns this Button from a sprite. **/
	public static NodeIdLabel getFromSprite(Sprite s) {
		return (NodeIdLabel) ToolTip.getToolTipFromSprite(s);
	}
}
