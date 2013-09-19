package dna.profiler.complexity;

import java.util.EnumMap;

public class Complexity {

	public enum ComplexityType {
		Linear, Static, Unknown
	}
		
	private int counter;
	private int factor;
	private ComplexityType complexityType;
	
	public Complexity() {
	}

	public Complexity(int factor, ComplexityType complexityType) {
		this.factor = factor;
		this.complexityType = complexityType;
	}

	public void increaseBy(int increase) {
		this.counter += increase;
	}

	public int getComplexityCounter() {
		return counter;
	}
	
	public EnumMap<ComplexityType, Integer> getComplexityMap() {
		EnumMap<ComplexityType, Integer> res = new EnumMap<>(
				ComplexityType.class);
		res.put(this.complexityType, this.counter);
		return res;
	}
	
	public EnumMap<ComplexityType, Integer> getWeightedComplexityMap() {
		EnumMap<ComplexityType, Integer> res = new EnumMap<>(
				ComplexityType.class);
		res.put(this.complexityType, this.counter * this.factor);
		return res;
	}
	
	public String getComplexity() {
		return counter + " of type " + factor + "*" + complexityType.getClass().getSimpleName();
	}

	public String toString() {
		return this.getComplexity();
	}
}
