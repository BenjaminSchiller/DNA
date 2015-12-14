package dna.visualization.graph.ToolTip;

import org.graphstream.ui.spriteManager.Sprite;

public class Button extends ToolTip {

	public Button(Sprite s) {
		this.s = s;
	}

	public Button(Sprite s, String name) {
		this(s);
		this.setName(name);
	}

	public ToolTipType getType() {
		return ToolTipType.BUTTON;
	}

	public static Button getFromSprite(Sprite s) {
		return new Button(s);
	}
}
