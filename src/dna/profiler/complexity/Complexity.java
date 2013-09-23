package dna.profiler.complexity;

import java.util.EnumMap;

public class Complexity {

	public enum ComplexityType {
		Linear, Static, Unknown
	}
	
	public enum ComplexityBase {
		NodeSize, EdgeSize, Degree
	}
		
	private int counter;
	private int factor;
	private ComplexityType complexityType;
	private ComplexityBase complexityBase;
	
	public Complexity() {
	}

	public Complexity(int factor, ComplexityType complexityType, ComplexityBase base) {
		this.factor = factor;
		this.complexityType = complexityType;
		this.complexityBase = base;
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
		return counter + " calls of type " + factor + "*" + getFormattedComplexity(complexityType, complexityBase);
	}

	private String getFormattedComplexity(ComplexityType complexityType,
			ComplexityBase complexityBase) {
		switch(complexityType) {
		case Linear:
			return complexityBase.toString();
		case Static:
			return "1";
		case Unknown:
			return "unknown";
		default:
			throw new RuntimeException("Unknown type " + complexityType);
		}
	}

	public String toString() {
		return this.getComplexity();
	}
}
