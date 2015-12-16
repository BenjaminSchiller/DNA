package dna.visualization.graph.toolTip;

import org.graphstream.graph.Element;
import org.graphstream.ui.spriteManager.Sprite;

import dna.visualization.graph.rules.GraphStyleUtils;

/**
 * The HighlightButton is a more elaborate Button implementation. It can be
 * clicked multiple times to increase (left-click) or even decrease
 * (right-click) the size of the node it is attached to.
 * 
 * <p>
 * 
 * In order to maintain, store and retrieve the different states, an Integer is
 * saved on the node to reflect the current level of highlighting.
 * 
 * @author Rwilmes
 * @date 16.12.2015
 */
public class HighlightButton extends Button {

	private static final String highlightButtonKey = "tt.button.highlight";
	private static final double defaultGrowth = 15;

	// labels
	private static final String defaultLabel = "Highlight";
	private static final String pressedLabel = "Unlight";

	private double growth;
	private final int maxLevel = 3;

	/**
	 * HighlightButton constructor. This constructor is called each time the
	 * underlying Sprite is clicked. The init-flag should only be set on actual
	 * first-time initializations.
	 **/
	public HighlightButton(Sprite s, String name, String attachementId,
			double growth, boolean init) {
		super(s, name, attachementId);
		this.growth = growth;
		this.s.setAttribute(ToolTip.GraphVisToolTipTypeKey,
				ToolTipType.BUTTON_HIGHLIGHT);

		int level = 0;
		if (s.attached()) {
			Element e = s.getAttachment();
			if (e.hasAttribute(highlightButtonKey))
				level = e.getAttribute(highlightButtonKey, Integer.class);
		}
		setStyleByLevel(level);
	}

	public HighlightButton(Sprite s, String name, String attachementId) {
		this(s, name, attachementId, defaultGrowth, true);
	}

	@Override
	public ToolTipType getType() {
		return ToolTipType.BUTTON_HIGHLIGHT;
	}

	/** Returns this Button from a sprite. **/
	public static HighlightButton getFromSprite(Sprite s) {
		return new HighlightButton(s, s.getAttribute(
				ToolTip.GraphVisToolTipNameKey, String.class), s
				.getAttachment().getId(), defaultGrowth, false);
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
	/** Increase highlighting and update style / label accordingly. **/
	public void onLeftClick() {
		Element e = s.getAttachment();
		int level = 0;

		if (e.hasAttribute(highlightButtonKey)) {
			level = e.getAttribute(highlightButtonKey, Integer.class);

			if (level >= maxLevel) {
				resetLevel(e, level);
				level = 0;
			} else {
				increaseSize(e);
				e.setAttribute(highlightButtonKey, ++level);
			}
		} else {
			increaseSize(e);
			e.setAttribute(highlightButtonKey, ++level);
		}

		setStyleByLevel(level);
	}

	/** Decrease highlighting and update style / label accordingly. **/
	public void onRightClick() {
		Element e = s.getAttachment();
		int level = 0;

		if (e.hasAttribute(highlightButtonKey)) {
			level = e.getAttribute(highlightButtonKey, Integer.class);

			if (level >= 1) {
				decreaseSize(e);
				e.setAttribute(highlightButtonKey, --level);
			} else {
				resetLevel(e, level);
				level = 0;
			}
		}

		setStyleByLevel(level);
	}

	protected void increaseSize(Element e) {
		GraphStyleUtils.increaseSize(e, growth);
		GraphStyleUtils.updateStyle(e);
	}

	protected void decreaseSize(Element e) {
		GraphStyleUtils.decreaseSize(e, growth);
		GraphStyleUtils.updateStyle(e);
	}

	protected void resetLevel(Element e, int level) {
		GraphStyleUtils.decreaseSize(e, level * growth);
		GraphStyleUtils.updateStyle(e);
		e.removeAttribute(highlightButtonKey);
	}

	private void setStyleByLevel(int level) {
		switch (level) {
		case 0:
			setDefaultStyle();
			setLabel("Highlight");
			break;
		case 1:
			setPressedStyle();
			setLabel("Highlight*");
			break;
		case 2:
			setPressedStyle();
			setLabel("Highlight**");
			break;
		case 3:
			setPressedStyle();
			setLabel("Reset");
			break;
		}
	}

	@Override
	protected String getDefaultLabel() {
		return defaultLabel;
	}

	@Override
	protected String getPressedLabel() {
		return pressedLabel;
	}

}
