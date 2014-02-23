package dna.profiler.datatypes.complexity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dna.profiler.ProfilerMeasurementData;
import dna.profiler.datatypes.AddedComparableEntry;
import dna.profiler.datatypes.ComparableEntry;


public abstract class AbstractComplexity extends ProfilerMeasurementData {
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
}
