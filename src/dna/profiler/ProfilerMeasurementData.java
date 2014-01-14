package dna.profiler;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Properties;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dna.graph.datastructures.DataStructure.AccessType;
import dna.profiler.complexity.AddedComplexity;
import dna.profiler.complexity.Complexity;
import dna.profiler.complexity.ComplexityType;
import dna.util.PropertiesHolder;

public class ProfilerMeasurementData extends PropertiesHolder {
	public enum ProfilerDataType {
		RuntimeComplexity
	}

	private static String folderName = "profilerData/";

	private static HashMap<String, Complexity> complexityData;

	public static void init() throws IOException {
		complexityData = null;
		loadFromProperties(initFromFolder(folderName));
	}

	public static Complexity get(ProfilerDataType complexityType,
			String classname, AccessType accessType, String storedDataClass,
			ComplexityType.Base base) {

		String keyName = complexityType.toString().toUpperCase() + "_"
				+ classname.toUpperCase();

		Complexity c = get(keyName + "_" + accessType.toString().toUpperCase()
				+ "_" + storedDataClass.toUpperCase());
		if (c == null)
			c = get(keyName + "_" + accessType.toString().toUpperCase());
		if (c == null)
			c = get(keyName);
		if (c == null)
			throw new RuntimeException("Missing complexity entry " + keyName);
		c.setBase(base);
		return c;
	}

	public static Complexity get(String key) {
		if (complexityData == null)
			try {
				init();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return complexityData.get(key);
	}

	public static Complexity parseComplexityString(String in) {
		Complexity res = null;
		Complexity temp;

		String[] parts = in.split("\\+");
		Pattern splitMatcher = Pattern.compile("\\d+|\\w+");

		for (String part : parts) {
			if (part.length() == 0)
				continue;

			// Split into number and type
			Matcher subparts = splitMatcher.matcher(part);

			try {
				subparts.find();
				int counter = Integer.parseInt(subparts.group());
				subparts.find();
				String type = subparts.group();

				if (ComplexityType.Type.contains(type)) {
					ComplexityType.Type t = ComplexityType.Type.valueOf(type);
					ComplexityType baseType = ComplexityType.Type
							.getBasicComplexity(t);
					temp = new Complexity(counter, baseType);
				} else {
					// Seems to be a complex type - look it up!
					temp = get(type);

					if (temp == null) {
						/**
						 * Complex type that was not defined yet - we cannot
						 * continue with this one, so return to sender
						 */
						return null;
					}

					temp.multiplyFactorBy(counter);
				}

				if (res == null) {
					res = temp;
				} else {
					res = new AddedComplexity(res, temp);
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

	public static void loadFromProperties(Properties in) {
		if (complexityData == null) {
			complexityData = new HashMap<String, Complexity>();
		}

		int queuedParsings = 0;

		/**
		 * It might occur that we cannot parse a complexity entry on the first
		 * try, eg. when it refers to a second one that is not parsed yet. Thats
		 * what we use the todoList for: these entries will be queued for later
		 * parsing
		 */
		Queue<String> todoList = new LinkedList<String>();
		todoList.addAll(in.stringPropertyNames());
		String key;
		while ((key = todoList.poll()) != null) {
			String val = in.getProperty(key);
			Complexity c = parseComplexityString(val);
			if (c == null) {
				todoList.add(key);
				queuedParsings++;
				if (queuedParsings > 2 * in.size()) {
					throw new RuntimeException(
							"Could not properly parse complexities - is there a loop?");
				}
			} else {
				complexityData.put(key, c);
			}
		}
	}
}
