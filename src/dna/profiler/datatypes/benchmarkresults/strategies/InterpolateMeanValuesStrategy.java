package dna.profiler.datatypes.benchmarkresults.strategies;

import java.util.ArrayList;

import com.google.common.math.DoubleMath;

public class InterpolateMeanValuesStrategy extends
		ComputingResultProcessingStrategy {

	@Override
	public double preprocess(ArrayList<Double> valueSet) {
		return DoubleMath.mean(valueSet);
	}

	@Override
	public double getValue(double meanListSize) {
		Integer lowerSelector = buckets
				.floorKey((int) Math.floor(meanListSize));
		if (lowerSelector == null) {
			lowerSelector = buckets.firstKey();
		}
		double lowerValue = buckets.get(lowerSelector);

		Integer higherSelector = buckets.ceilingKey((int) Math
				.ceil(meanListSize));
		if (higherSelector == null) {
			higherSelector = buckets.lastKey();
		}
		double higherValue = buckets.get(higherSelector);

		if (lowerSelector == higherSelector)
			return lowerValue;

		double res = lowerValue
				+ ((higherValue - lowerValue) / (higherSelector - lowerSelector))
				* (meanListSize - lowerSelector);
		return res;
	}

	@Override
	public ResultProcessingStrategy clone() {
		return new InterpolateMeanValuesStrategy();
	}

}
