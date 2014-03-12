package dna.profiler.datatypes.benchmarkresults.strategies;

import java.util.ArrayList;
import java.util.TreeMap;
import java.util.Map.Entry;

public abstract class ComputingResultProcessingStrategy extends
		ResultProcessingStrategy {

	protected TreeMap<Integer, Double> buckets;

	@Override
	public void initialize(TreeMap<Integer, ArrayList<Double>> values) {
		this.buckets = new TreeMap<Integer, Double>();
		for (Entry<Integer, ArrayList<Double>> e : values.entrySet()) {
			double prepocessed = preprocess(e.getValue());
			buckets.put(e.getKey(), prepocessed);
		}
	}
	
	public abstract double preprocess(ArrayList<Double> valueSet);
}
