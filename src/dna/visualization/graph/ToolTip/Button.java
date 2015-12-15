package dna.visualization.graph.ToolTip;

import org.graphstream.ui.spriteManager.Sprite;

import dna.visualization.graph.GraphVisualization;

public abstract class Button extends ToolTip {

	public Button() {

	}

	public Button(Sprite s) {
		this.s = s;
	}

	public Button(Sprite s, String name, String attachementId) {
		this.s = s;
		setName(name);
		attachToNode(attachementId);
	}

	public void setLabel(String label) {
		this.s.setAttribute(GraphVisualization.labelKey, label);
	}

	public abstract ToolTipType getType();

	// default style
	protected abstract String getDefaultStyle();

	protected static final String defaultStyle = "" + "shape:rounded-box; "
			+ "size:100px,30px; " + "fill-mode:plain; "
			+ "fill-color: rgba(155,155,155, 150); " + "stroke-mode:dots; "
			+ "stroke-color: rgb(40, 40, 40); " + "text-alignment:center;";

	protected abstract String getDefaultLabel();

	// style when pressed
	protected abstract String getPressedStyle();

	protected static final String pressedStyle = "" + "shape:rounded-box; "
			+ "size:100px,30px; " + "fill-mode:plain; "
			+ "fill-color: rgba(200,0,0, 150); " + "stroke-mode:dots; "
			+ "stroke-color: rgb(40, 40, 40); " + "text-alignment:center;";

	protected abstract String getPressedLabel();

	public void setDefaultStyle() {
		this.s.setAttribute(GraphVisualization.styleKey, getDefaultStyle());
		setLabel(getDefaultLabel());
	}

	public void setPressedStyle() {
		this.s.setAttribute(GraphVisualization.styleKey, getPressedStyle());
		setLabel(getPressedLabel());
	}

	public abstract void onLeftClick();

	public abstract void onRightClick();
}
