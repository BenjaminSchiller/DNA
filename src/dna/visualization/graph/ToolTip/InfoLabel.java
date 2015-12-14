package dna.visualization.graph.ToolTip;

import org.graphstream.ui.spriteManager.Sprite;

public class InfoLabel extends ToolTip {

	protected static final String LabelValueKey = "dna.label.value";
	protected static final String LabelValueTypeKey = "dna.label.valueType";

	protected enum LabelValueType {
		DOUBLE, INT, LONG
	}

	// class variables
	protected LabelValueType valueType;

	// constructor
	public InfoLabel(Sprite s, LabelValueType valueType) {
		this.s = s;
		this.valueType = valueType;
	}

	public InfoLabel(Sprite s, String name, LabelValueType valueType) {
		this.s = s;
		this.setName(name);
		this.valueType = valueType;
	}

	// methods
	public void setValue(String value) {
		this.s.setAttribute(LabelValueKey, value);
	}

	public String getValue() {
		return this.s.getAttribute(LabelValueKey);
	}

	public void increment() {
		this.increment(1);
	}

	public void increment(int steps) {
		String value = this.s.getAttribute(LabelValueKey);
		switch (valueType) {
		case DOUBLE:
			Double d = Double.parseDouble(value);
			this.setValue("" + (d + steps));
			break;
		case INT:
			Integer i = Integer.parseInt(value);
			this.setValue("" + (i + steps));
			break;
		case LONG:
			Long l = Long.parseLong(value);
			this.setValue("" + (l + steps));
			break;
		}
	}

	public void decrement() {
		this.increment(-1);
	}

	public void decrement(int steps) {
		this.increment(-steps);
	}

	public ToolTipType getType() {
		return ToolTipType.INFO;
	}

	// static methods
	public static InfoLabel getFromSprite(Sprite s) {
		return new InfoLabel(s, getValueTypeFromSprite(s));
	}

	public static Number getValueFromSprite(Sprite s) {
		LabelValueType type = s.getAttribute(LabelValueTypeKey,
				LabelValueType.class);
		switch (type) {
		case DOUBLE:
			return s.getAttribute(LabelValueKey, Double.class);
		case INT:
			return s.getAttribute(LabelValueKey, Integer.class);
		case LONG:
			s.getAttribute(LabelValueKey, Long.class);
		default:
			return null;
		}
	}

	public static LabelValueType getValueTypeFromSprite(Sprite s) {
		return (s.getAttribute(LabelValueTypeKey, LabelValueType.class));
	}

}
