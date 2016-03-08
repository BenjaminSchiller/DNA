package dna.labels;

import dna.series.lists.ListItem;
import dna.util.Config;

/**
 * Labels are used to label batches.
 * 
 * @author Rwilmes
 * 
 */
public class Label implements ListItem {

	private String name;
	private String type;
	private String value;

	public Label(String name) {
		this(name, null, null);
	}

	public Label(String name, String type, String value) {
		this.name = name;
		this.type = type;
		this.value = value;
	}

	@Override
	public String getName() {
		return this.name;
	}

	public String getType() {
		return this.type;
	}

	public String getValue() {
		return this.value;
	}

	public String toString() {
		return this.name + Config.get("LABEL_NAME_TYPE_SEPARATOR") + this.type
				+ Config.get("LABEL_VALUE_SEPARATOR") + this.value;
	}
}
