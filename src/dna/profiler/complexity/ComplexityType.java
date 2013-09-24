package dna.profiler.complexity;

public class ComplexityType {
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

	public String toString() {
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
}
