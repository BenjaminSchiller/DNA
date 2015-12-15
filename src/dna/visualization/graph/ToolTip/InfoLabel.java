package dna.visualization.graph.ToolTip;

import org.graphstream.ui.spriteManager.Sprite;

import dna.visualization.graph.GraphVisualization;

public class InfoLabel extends ToolTip {

	protected static final String LabelValueKey = "dna.label.value";
	protected static final String LabelValueTypeKey = "dna.label.valueType";

	public enum LabelValueType {
		DOUBLE, INT, LONG
	}

	// class variables
	protected LabelValueType valueType;

	// constructor
	public InfoLabel(Sprite s, String name, LabelValueType valueType) {
		this.s = s;
		this.setName(name);
		this.valueType = valueType;
		this.s.setAttribute(LabelValueTypeKey, valueType);
		this.s.setAttribute(ToolTip.GraphVisToolTipTypeKey, ToolTipType.INFO);
	}

	public InfoLabel(Sprite s, String name, LabelValueType valueType,
			String value) {
		this(s, name, valueType);
		this.setValue(value);
	}

	// methods
	public void setValue(String value) {
		this.s.setAttribute(LabelValueKey, value);
		this.updateLabel();
	}

	/** Updates the label based on the stored name and value. **/
	public void updateLabel() {
		this.s.setAttribute(GraphVisualization.labelKey, this.getName() + ": "
				+ this.getValue());
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

		this.updateLabel();
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
		return new InfoLabel(s, s.getAttribute(GraphVisToolTipNameKey,
				String.class), getValueTypeFromSprite(s));
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
