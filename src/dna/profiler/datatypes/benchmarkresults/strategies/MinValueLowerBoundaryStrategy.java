package dna.profiler.datatypes.benchmarkresults.strategies;

import java.util.ArrayList;
import java.util.Collections;

public class MinValueLowerBoundaryStrategy extends
		DataListResultProcessingStrategy {

	public double preprocess(ArrayList<Double> valueSet) {
		return Collections.min(valueSet);
	}

	@Override
	public ResultProcessingStrategy clone() {
		return new MinValueLowerBoundaryStrategy();
	}

	@Override
	public int selectBucket(double meanListSize) {
		Integer bucketSelector = buckets.floorKey((int) Math
				.floor(meanListSize));
		if (bucketSelector == null) {
			bucketSelector = buckets.firstKey();
		}
		return bucketSelector;
	}

}
