package dna.profiler.complexity;

public class Complexity {

	private int counter;
	private ComplexityClass complexityType;

	public Complexity(int count, ComplexityClass complexityType) {
		this.counter = count;
		this.complexityType = complexityType;
	}

	public void increaseBy(int increase) {
		this.counter += increase;
	}

	public int getComplexityCounter() {
		return counter;
	}
	
	public String getComplexity() {
		return counter + " of type " + complexityType.getClass().getSimpleName();
	}

	public String toString() {
		return this.getComplexity();
	}
}
