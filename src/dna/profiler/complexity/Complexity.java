package dna.profiler.complexity;

public class Complexity {

	private int counter;
	private int factor;
	private ComplexityClass complexityType;
	
	public Complexity() {
	}

	public Complexity(int factor, ComplexityClass complexityType) {
		this.factor = factor;
		this.complexityType = complexityType;
	}

	public void increaseBy(int increase) {
		this.counter += increase;
	}

	public int getComplexityCounter() {
		return counter;
	}
	
	public String getComplexity() {
		return counter + " of type " + factor + "*" + complexityType.getClass().getSimpleName();
	}

	public String toString() {
		return this.getComplexity();
	}
}
