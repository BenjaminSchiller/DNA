package dna.profiler.datatypes.benchmarkresults.strategies;

import java.util.ArrayList;
import java.util.Collections;

public class MaxValueUpperBoundaryStrategy extends
		DataListResultProcessingStrategy {

	public double preprocess(ArrayList<Double> valueSet) {
		return Collections.max(valueSet);
	}

	@Override
	public ResultProcessingStrategy clone() {
		return new MaxValueUpperBoundaryStrategy();
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
