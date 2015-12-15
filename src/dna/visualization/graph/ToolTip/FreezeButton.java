package dna.visualization.graph.ToolTip;

import org.graphstream.graph.Element;
import org.graphstream.ui.spriteManager.Sprite;

import dna.visualization.graph.GraphVisualization;

public class FreezeButton extends Button {

	// labels
	private static final String defaultLabel = "Freeze";
	private static final String pressedLabel = "Unfreeze";

	// constructor
	public FreezeButton(Sprite s, String name, String attachementId,
			boolean init) {
		super(s, name, attachementId);
		this.s.setAttribute(ToolTip.GraphVisToolTipTypeKey,
				ToolTipType.BUTTON_FREEZE);

		if (init) {
			Element e = s.getAttachment();
			if (e.hasAttribute(GraphVisualization.frozenKey))
				setPressedStyle();
			else
				setDefaultStyle();
		}
	}

	public FreezeButton(Sprite s, String name, String attachementId) {
		this(s, name, attachementId, true);
	}

	// methods
	public ToolTipType getType() {
		return ToolTipType.BUTTON_FREEZE;
	}

	public static FreezeButton getFromSprite(Sprite s) {
		return new FreezeButton(s, s.getAttribute(
				ToolTip.GraphVisToolTipNameKey, String.class), s
				.getAttachment().getId(), false);
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
	public void onRightClick() {
		// DO NOTHING
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
