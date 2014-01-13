package dna.profiler.complexity;

import java.util.TreeSet;

public class ComplexityType implements Comparable<ComplexityType> {
	/**
	 * List of complexity types. Keep it sorted to enable comparisons in
	 * {@link ComplexityType#compareTo(ComplexityType)}
	 * 
	 * @author Nico
	 * 
	 */
	public enum Type {
		Static, Linear, Unknown;

		public static ComplexityType staticType = new ComplexityType(Static,
				null);
		public static ComplexityType linearType = new ComplexityType(Linear,
				null);
		public static ComplexityType unknownType = new ComplexityType(Unknown,
				null);

		public static ComplexityType getBasicComplexity(Type selector) {
			switch (selector) {
			case Static:
				return staticType;
			case Linear:
				return linearType;
			case Unknown:
				return unknownType;
			default:
				throw new RuntimeException("Unknwon ComplexityType " + selector);
			}
		}

		public static boolean contains(String type) {
			Type[] rawValues = values();
			for (int i = 0; i < rawValues.length; i++) {
				if (rawValues[i].toString().equals(type))
					return true;
			}
			return false;
		}
	}

	public enum Base {
		Degree, NodeSize, EdgeSize;

		@Override
		public String toString() {
			switch (this) {
			case Degree:
				return "d";
			case EdgeSize:
				return "E";
			case NodeSize:
				return "N";
			default:
				return "";
			}
		}
	}

	private Type complexityType;
	private Base complexityBase;

	public ComplexityType(Type t, Base b) {
		this.complexityType = t;
		this.complexityBase = b;
	}

	public void setBase(Base base) {
		this.complexityBase = base;
	}

	public static TreeSet<ComplexityType> getAllComplexityTypes() {
		TreeSet<ComplexityType> complexityTypes = new TreeSet<>();
		for (Type t : ComplexityType.Type.values()) {
			for (Base b : ComplexityType.Base.values()) {
				complexityTypes.add(new ComplexityType(t, b));
			}
		}
		return complexityTypes;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((complexityType == null) ? 0 : complexityType.hashCode());

		/**
		 * The following looks strange, but is important: only if the Type is
		 * different from Static or Unknown, it is important for the
		 * calculation. All calls from these two types do not need further
		 * differentiation
		 */

		if (complexityType != Type.Static) {
			result = prime
					* result
					+ ((complexityBase == null) ? 0 : complexityBase.hashCode());
		}
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

		/**
		 * The following looks strange, but is important: only if the Type is
		 * different from Static or Unknown, it is important for the
		 * calculation. All calls from these two types do not need further
		 * differentiation
		 */

		if ((complexityType == Type.Static && other.complexityType == Type.Static)
				|| (complexityType == Type.Unknown && other.complexityType == Type.Unknown)) {
			return true;
		}
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
			return "O("
					+ (complexityBase != null ? complexityBase.toString()
							: "unknown") + ")";
		case Static:
			return "O(1)";
		case Unknown:
			return "unknown";
		default:
			throw new RuntimeException("Unknown type " + complexityType);
		}
	}

	/**
	 * As a remark: a.compareTo(b) returns the following results:
	 * 		-1 iff a < b
	 * 		0  iff a == b
	 * 		1  iff a > b
	 */
	@Override
	public int compareTo(ComplexityType o) {
		int res = this.complexityType.compareTo(o.complexityType);
		if (res == 0 && this.complexityType == Type.Linear) {
			res = this.complexityBase.compareTo(o.complexityBase);
		}
		return res;
	}
}
