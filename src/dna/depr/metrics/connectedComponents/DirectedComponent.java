package dna.depr.metrics.connectedComponents;

import java.util.HashMap;
import java.util.Map;

public class DirectedComponent {

	public DirectedComponent(int index) {
		this.index = index;
		this.size = 0;
		this.ed = new HashMap<Integer, Integer>();
	}

	private int index;
	private int size;
	public Map<Integer, Integer> ed;

	public int getIndex() {
		return index;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public void increaseSize(int size) {
		this.size += size;
	}

	public void decreaseSize(int size) {
		this.size -= size;
	}

	@Override
	public boolean equals(Object other) {
		if (other == null) {
			return false;
		}
		if (other == this) {
			return true;
		}
		if (!(other instanceof DirectedComponent)) {
			return false;
		}
		DirectedComponent other_ = (DirectedComponent) other;
		return this.index == other_.index;
	}

}
