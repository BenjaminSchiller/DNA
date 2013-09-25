package dna.profiler.complexity;

public class ComplexityType implements Comparable<ComplexityType> {
	/**
	 * List of complexity types. Keep it sorted to enable comparisons in
	 * {@link ComplexityType#compareTo(ComplexityType)}
	 * 
	 * @author Nico
	 * 
	 */
	public enum Type {
		Linear, Static, Unknown
	}

	public enum Base {
		NodeSize, EdgeSize, Degree
	}

	private Type complexityType;
	private Base complexityBase;

	public ComplexityType(Type t, Base b) {
		this.complexityType = t;
		this.complexityBase = b;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((complexityType == null) ? 0 : complexityType.hashCode());

		result = prime * result
				+ ((complexityBase == null) ? 0 : complexityBase.hashCode());
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
		ComplexityType other = (ComplexityType) obj;

		if (complexityType != other.complexityType) {
			return false;
		}
		if (complexityBase != other.complexityBase) {
			return false;
		}
		return true;
	}

	public String toString() {
		switch (complexityType) {
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

	@Override
	public int compareTo(ComplexityType o) {
		return this.complexityType.compareTo(o.complexityType);
	}
}
