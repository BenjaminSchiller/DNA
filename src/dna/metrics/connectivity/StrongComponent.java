package dna.metrics.connectivity;

import java.util.HashMap;
import java.util.Map;

public class StrongComponent extends WeakComponent {

	public Map<Integer, Integer> ed;

	public StrongComponent(int index) {
		super(index);
		this.ed = new HashMap<Integer, Integer>();
	}

	@Override
	public boolean equals(Object other) {
		if (other == null) {
			return false;
		}
		if (other == this) {
			return true;
		}
		if (!(other instanceof StrongComponent)) {
			return false;
		}
		StrongComponent other_ = (StrongComponent) other;
		return this.index == other_.index;
	}

}
