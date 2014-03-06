package dna.profiler.datatypes.benchmarkresults.strategies;

import java.util.ArrayList;
import java.util.Collections;

public class MaxValueLowerBoundaryStrategy extends
		DataListResultProcessingStrategy {

	public double preprocess(ArrayList<Double> valueSet) {
		return Collections.max(valueSet);
	}

	@Override
	public ResultProcessingStrategy clone() {
		return new MaxValueLowerBoundaryStrategy();
	}

	@Override
	public int selectBucket(double meanListSize) {
		Integer bucketSelector = buckets.floorKey((int) meanListSize);
		if (bucketSelector == null) {
			bucketSelector = buckets.firstKey();
		}
		return bucketSelector;
	}

}
