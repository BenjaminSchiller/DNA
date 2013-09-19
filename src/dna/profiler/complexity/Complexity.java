package dna.profiler.complexity;

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
	
	public String getComplexity() {
		return counter + " of type " + factor + "*" + complexityType.getClass().getSimpleName();
	}

	public String toString() {
		return this.getComplexity();
	}
}
