package dna.profiler.datatypes.benchmarkresults.strategies;

import java.util.ArrayList;
import java.util.Collections;

public class MinValueUpperBoundaryStrategy extends
		DataListResultProcessingStrategy {

	public double preprocess(ArrayList<Double> valueSet) {
		return Collections.min(valueSet);
	}

	@Override
	public ResultProcessingStrategy clone() {
		return new MinValueUpperBoundaryStrategy();
	}

	@Override
	public int selectBucket(double meanListSize) {
		Integer bucketSelector = buckets.ceilingKey((int) meanListSize);
		if (bucketSelector == null) {
			bucketSelector = buckets.lastKey();
		}
		return bucketSelector;
	}

}
