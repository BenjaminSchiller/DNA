package dna.profiler.datatypes.benchmarkresults.strategies;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map.Entry;
import java.util.TreeMap;

public class BoundaryStrategy extends ResultProcessingStrategy {

	protected TreeMap<Integer, Double> buckets;
	private Boundary b;
	private Selector s;

	public enum Boundary {
		LOWER, UPPER
	};

	public enum Selector {
		MIN, MAX
	}

	public BoundaryStrategy(Boundary b, Selector s) {
		this.b = b;
		this.s = s;
	}

	@Override
	public void initialize(TreeMap<Integer, ArrayList<Double>> values) {
		this.buckets = new TreeMap<Integer, Double>();
		for (Entry<Integer, ArrayList<Double>> e : values.entrySet()) {
			double prepocessed = preprocess(e.getValue());
			buckets.put(e.getKey(), prepocessed);
		}
	}

	public double preprocess(ArrayList<Double> valueSet) {
		switch (s) {
		case MAX:
			return Collections.max(valueSet);
		case MIN:
			return Collections.min(valueSet);
		default:
			return -1;
		}
	}

	public int selectBucket(double meanListSize) {
		Integer bucketSelector;

		switch (b) {
		case LOWER:
			bucketSelector = buckets.floorKey((int) Math.floor(meanListSize));
			if (bucketSelector == null) {
				bucketSelector = buckets.firstKey();
			}
			return bucketSelector;
		case UPPER:
			bucketSelector = buckets.ceilingKey((int) Math.ceil(meanListSize));
			if (bucketSelector == null) {
				bucketSelector = buckets.lastKey();
			}
			return bucketSelector;
		default:
			return -1;
		}

	}

	@Override
	public double getValue(double meanListSize) {
		return buckets.get(selectBucket(meanListSize));
	}

	@Override
	public ResultProcessingStrategy clone() {
		return new BoundaryStrategy(b, s);
	}
}
