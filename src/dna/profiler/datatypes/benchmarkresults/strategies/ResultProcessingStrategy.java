package dna.profiler.datatypes.benchmarkresults.strategies;

import java.util.ArrayList;
import java.util.TreeMap;

public abstract class ResultProcessingStrategy {
	public abstract void initialize(TreeMap<Integer, ArrayList<Double>> values);

	public abstract double getValue(double meanListSize);

	public abstract ResultProcessingStrategy clone();

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() == obj.getClass()) {
			return true;
		}
		return false;
	}
}
