package dna.profiler.datatypes.benchmarkresults.strategies;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map.Entry;
import java.util.TreeMap;

import com.google.common.math.DoubleMath;

public class BoundaryStrategy extends ResultProcessingStrategy {

	protected TreeMap<Integer, Double> buckets;
	private BucketSelector b;
	private ListAggregator s;

	public enum BucketSelector {
		LOWER, UPPER, INTERPOLATE
	};

	public enum ListAggregator {
		MIN, MAX, MEAN
	}

	public BoundaryStrategy(BucketSelector b, ListAggregator s) {
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
		case MEAN:
			return DoubleMath.mean(valueSet);
		default:
			return -1;
		}
	}

	public int getLowerKey(double meanListSize) {
		Integer bucketSelector = buckets.floorKey((int) Math
				.floor(meanListSize));
		if (bucketSelector == null) {
			bucketSelector = buckets.firstKey();
		}
		return bucketSelector;
	}

	public int getUpperKey(double meanListSize) {
		Integer bucketSelector = buckets.ceilingKey((int) Math
				.ceil(meanListSize));
		if (bucketSelector == null) {
			bucketSelector = buckets.lastKey();
			System.err
					.println("The "
							+ this.getClass().getSimpleName()
							+ " will return erroneous results, as the given meanListSize of "
							+ meanListSize + " exceeds the upper bound of "
							+ bucketSelector
							+ " in the benchmarking results");
		}
		return bucketSelector;
	}

	@Override
	public double getValue(double meanListSize) {
		switch (b) {
		case LOWER:
			return buckets.get(getLowerKey(meanListSize));
		case UPPER:
			return buckets.get(getUpperKey(meanListSize));
		case INTERPOLATE:
			int lowerSelector = getLowerKey(meanListSize);
			double lowerValue = buckets.get(lowerSelector);
			int higherSelector = getUpperKey(meanListSize);

			if (lowerSelector == higherSelector) {
				if (lowerValue == meanListSize)
					return lowerValue;

				if (lowerSelector != buckets.firstKey()) {
					lowerSelector = getLowerKey(lowerSelector - 0.1);
					lowerValue = buckets.get(lowerSelector);
				} else {
					higherSelector = getUpperKey(higherSelector + 0.1);
				}
			}

			double higherValue = buckets.get(higherSelector);

			if (lowerSelector == higherSelector)
				return lowerValue;

			double res = lowerValue
					+ ((higherValue - lowerValue) / (higherSelector - lowerSelector))
					* (meanListSize - lowerSelector);
			return res;
		default:
			throw new RuntimeException("How did I even get here?");
		}
	}

	@Override
	public ResultProcessingStrategy clone() {
		return new BoundaryStrategy(b, s);
	}
}
