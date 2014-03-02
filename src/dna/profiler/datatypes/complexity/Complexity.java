package dna.profiler.datatypes.complexity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dna.profiler.datatypes.AddedComparableEntry;
import dna.profiler.datatypes.ComparableEntry;
import dna.profiler.datatypes.ComparableEntryMap;
import dna.profiler.datatypes.complexity.ComplexityType.Base;
import dna.profiler.datatypes.complexity.ComplexityType.Type;

public class Complexity extends ComparableEntry {

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
	
	public static ComparableEntry parseString(String key, String val) {
		ComparableEntry res = null;
		Complexity temp;

		String[] parts = val.split("\\+");
		Pattern splitMatcher = Pattern.compile("\\d+|\\w+");

		for (String part : parts) {
			if (part.length() == 0)
				continue;

			// Split into number and type
			Matcher subparts = splitMatcher.matcher(part);

			try {
				subparts.find();
				int counter = Integer.parseInt(subparts.group());

				if (counter == 0)
					continue;

				subparts.find();
				String type = subparts.group();

				if (ComplexityType.Type.contains(type)) {
					ComplexityType.Type t = ComplexityType.Type.valueOf(type);
					ComplexityType baseType = ComplexityType.Type
							.getBasicComplexity(t);
					temp = new Complexity(counter, baseType);
				} else {
					return null;
				}

				if (res == null) {
					res = temp;
				} else {
					res = new AddedComparableEntry(res, temp);
				}
			} catch (IllegalStateException e) {
				throw new RuntimeException("Could not parse " + part);
			}
		}

		if (res == null)
			return new Complexity();
		else
			return res;
	}	

	@Override
	public void setValues(int numberOfCalls, double meanListSize, Base base) {
		this.counter = numberOfCalls;
		this.setBase(base);
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

	public int getCounter() {
		return counter;
	}

	public ComparableEntryMap getMap() {
		ComparableEntryMap res = new ComplexityMap();
		res.put(this.complexityType, this.counter);
		return res;
	}

	public ComparableEntryMap getWeightedMap() {
		ComparableEntryMap res = new ComplexityMap();
		res.put(this.complexityType, this.counter * this.factor);
		return res;
	}

	public String getData() {
		return counter + " calls of type " + factor + "*"
				+ complexityType.toString();
	}

	public String toString() {
		return this.getData();
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

	public Complexity clone() {
		ComplexityType clonedComplexityType = this.complexityType.clone();
		Complexity res = new Complexity(this.factor, clonedComplexityType);
		res.setCounter(this.getCounter());
		return res;
	}

}
