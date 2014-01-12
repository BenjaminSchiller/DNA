package dna.profiler.complexity;

import dna.profiler.complexity.ComplexityType.Base;
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

	public void setBase(Base base) {
		this.complexityType.setBase(base);
	}

	public void setCounter(int c) {
		this.counter = c;
	}

	public void multiplyFactorBy(int factorMultiplyer) {
		this.factor *= factorMultiplyer;
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((complexityType == null) ? 0 : complexityType.hashCode());
		result = prime * result + counter;
		result = prime * result + factor;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Complexity other = (Complexity) obj;
		if (complexityType == null) {
			if (other.complexityType != null) {
				return false;
			}
		} else if (!complexityType.equals(other.complexityType)) {
			return false;
		}
		if (counter != other.counter) {
			return false;
		}
		if (factor != other.factor) {
			return false;
		}
		return true;
	}

}
