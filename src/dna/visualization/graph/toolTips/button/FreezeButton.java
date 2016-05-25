package dna.visualization.graph.toolTips.button;

import org.graphstream.graph.Element;
import org.graphstream.graph.Node;
import org.graphstream.ui.spriteManager.Sprite;

import dna.util.parameters.Parameter;
import dna.visualization.graph.GraphVisualization;
import dna.visualization.graph.toolTips.ToolTip;

/**
 * Button implementation used to freeze a node. This is done via
 * setting/removing the freeze-key on the node the button is attached to.
 * 
 * @author Rwilmes
 * @date 15.12.2015
 */
public class FreezeButton extends Button {

	// labels
	private static final String defaultLabel = "Freeze";
	private static final String pressedLabel = "Unfreeze";

	/** FreezeButton constructor. **/
	public FreezeButton(Sprite s, String name, Node n, Parameter[] params) {
		super(s, name, n, params);

		// if node is frozen -> use pressed style
		Element e = s.getAttachment();
		if (e.hasAttribute(GraphVisualization.frozenKey))
			setPressedStyle();
		else
			setDefaultStyle();
	}

	/** Returns this Button from a sprite. **/
	public static FreezeButton getFromSprite(Sprite s) {
		return (FreezeButton) ToolTip.getToolTipFromSprite(s);
	}

	@Override
	protected String getDefaultStyle() {
		return defaultStyle;
	}

	@Override
	protected String getPressedStyle() {
		return pressedStyle;
	}

	@Override
	public void onLeftClick() {
		Element e = s.getAttachment();

		// toggle freeze
		if (e.hasAttribute(GraphVisualization.frozenKey)) {
			e.removeAttribute(GraphVisualization.frozenKey);
			setDefaultStyle();
		} else {
			e.setAttribute(GraphVisualization.frozenKey);
			setPressedStyle();
		}
	}

	@Override
	protected String getDefaultLabel() {
		return FreezeButton.defaultLabel;
	}

	@Override
	protected String getPressedLabel() {
		return FreezeButton.pressedLabel;
	}
}
