package dna.visualization.graph.toolTips.infoLabel;

import org.graphstream.graph.Node;
import org.graphstream.ui.spriteManager.Sprite;

import dna.util.parameters.Parameter;
import dna.visualization.graph.GraphVisualization;
import dna.visualization.graph.toolTips.ToolTip;

/**
 * InfoLabel extends ToolTip and is therefore a wrapper for a GraphStream
 * Sprite. It is a basic bubble-like ToolTip which stores one value and displays
 * it with a String of the form
 * 
 * <p>
 * 
 * $VALUE_NAME$: $VALUE$
 * 
 * <p>
 * 
 * On initialization one can choose to either store a Double, Long or Int value.
 * However, all values handed over has to be in form of a parse-able String. In
 * addition it offers basic increment and decrement operations on the value.
 **/
public abstract class InfoLabel extends ToolTip {

	protected static final String LabelValueKey = "dna.label.value";
	protected static final String LabelValueTypeKey = "dna.label.valueType";

	/** Different types that can be stored. **/
	public enum LabelValueType {
		STRING, DOUBLE, INT, LONG
	}

	/** Type of the value that is stored. **/
	private LabelValueType valueType;

	/** InfoLabel constructor. **/
	public InfoLabel(Sprite s, String name, Node n, Parameter[] params) {
		this.s = s;
		setName(name);
		setType();

		// default type = STRING
		this.valueType = LabelValueType.STRING;

		for (Parameter p : params) {
			switch (p.getName().toLowerCase()) {
			case "valuetype":
				this.valueType = LabelValueType.valueOf(p.getValue());
				break;
			case "value":
				setValue(p.getValue());
				break;
			}
		}

		// set default style
		setDefaultStyle();

		// attach
		attachToNode("" + n.getIndex());

		// store value type
		this.s.setAttribute(LabelValueTypeKey, valueType);

		// store on sprite
		storeThisOnSprite();
	}

	/** Used to set a value. **/
	public void setValue(String value) {
		this.s.setAttribute(LabelValueKey, value);
		this.updateLabel();
	}

	/** Updates the label based on the stored name and value. **/
	public void updateLabel() {
		this.s.setAttribute(GraphVisualization.labelKey, this.getName() + ": "
				+ this.getValue());
	}

	/** Returns the stored value. **/
	public String getValue() {
		return this.s.getAttribute(LabelValueKey);
	}

	/** Increments the value. **/
	public void increment() {
		this.increment(1);
	}

	/** Increments the value by steps. **/
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

	/** Decrements the value. **/
	public void decrement() {
		this.increment(-1);
	}

	/** Decrements the value by steps. **/
	public void decrement(int steps) {
		this.increment(-steps);
	}

	/** Returns a InfoLabel from a sprite. **/
	public static InfoLabel getFromSprite(Sprite s) {
		return (InfoLabel) ToolTip.getToolTipFromSprite(s);
	}

	/** Returns the ValueType from a sprite. **/
	public static LabelValueType getValueTypeFromSprite(Sprite s) {
		return (s.getAttribute(LabelValueTypeKey, LabelValueType.class));
	}

}
