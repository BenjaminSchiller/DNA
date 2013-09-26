package dna.profiler.complexity;

import dna.profiler.complexity.ComplexityType.Type;

public class Complexity {

	private int counter = 0;
	private int factor = 0;
	private ComplexityType complexityType = new ComplexityType(Type.Unknown,
			null);

	public Complexity() {
	}

	public Complexity(int factor, ComplexityType complexityType) {
		this.factor = factor;
		this.complexityType = complexityType;
	}

	public void increaseBy(int increase) {
		this.counter += increase;
	}
	
	public void setCounter(int c) {
		this.counter = c;
	}

	public int getComplexityCounter() {
		return counter;
	}

	public ComplexityMap getComplexityMap() {
		ComplexityMap res = new ComplexityMap();
		res.put(this.complexityType, this.counter);
		return res;
	}

	public ComplexityMap getWeightedComplexityMap() {
		ComplexityMap res = new ComplexityMap();
		res.put(this.complexityType, this.counter * this.factor);
		return res;
	}

	public String getComplexity() {
		return counter + " calls of type " + factor + "*"
				+ complexityType.toString();
	}

	public String toString() {
		return this.getComplexity();
	}
}
