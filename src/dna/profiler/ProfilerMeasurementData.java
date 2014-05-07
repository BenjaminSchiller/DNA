package dna.profiler;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Properties;
import java.util.Vector;

import dna.graph.datastructures.DataStructure.AccessType;
import dna.profiler.datatypes.CombinedEntry;
import dna.profiler.datatypes.ComparableEntry;
import dna.profiler.datatypes.ComparableEntryMap;
import dna.profiler.datatypes.benchmarkresults.BenchmarkingResult;
import dna.profiler.datatypes.benchmarkresults.BenchmarkingResultsMap;
import dna.profiler.datatypes.complexity.Complexity;
import dna.profiler.datatypes.complexity.ComplexityMap;
import dna.profiler.datatypes.complexity.ComplexityType;
import dna.util.Config;
import dna.util.PropertiesHolder;

public abstract class ProfilerMeasurementData extends PropertiesHolder {
	public enum ProfilerDataType {
		RuntimeComplexity(), MemoryComplexity(AccessType.Init, AccessType.Add), RuntimeBenchmark(), MemoryBenchmark(
				AccessType.Init, AccessType.Add), CombinedBenchmark();

		private ArrayList<AccessType> accessTypesToUseFromAggregation;

		private ProfilerDataType(AccessType... at) {
			this.accessTypesToUseFromAggregation = new ArrayList<>(
					Arrays.asList(at));
		}

		public ArrayList<AccessType> getAccessTypesFromAggregation() {
			return accessTypesToUseFromAggregation;
		}
	}

	private static String folderName = "profilerData/";

	private static HashMap<String, ComparableEntry> measurementData;

	public static void setDataFolder(String df) {
		folderName = df;
	}

	public static String getDataFolder() {
		return folderName;
	}

	public static double[] getWeights(ProfilerDataType pdt) {
		switch (pdt) {
		case CombinedBenchmark:
			return new double[] {
					Config.getDouble("RECOMMENDER_COMBINEDCOMPLEXITY_RUNTIMEWEIGHT"),
					Config.getDouble("RECOMMENDER_COMBINEDCOMPLEXITY_MEMORYWEIGHT") };
		case MemoryBenchmark:
		case MemoryComplexity:
		case RuntimeBenchmark:
		case RuntimeComplexity:
			return null;
		default:
			throw new RuntimeException("No combination weights found for "
					+ pdt);
		}
	}

	public static ProfilerDataType[] getDependencies(ProfilerDataType pdt) {
		switch (pdt) {
		case CombinedBenchmark:
			return new ProfilerDataType[] { ProfilerDataType.RuntimeBenchmark,
					ProfilerDataType.MemoryBenchmark };
		case MemoryBenchmark:
		case MemoryComplexity:
		case RuntimeBenchmark:
		case RuntimeComplexity:
			return null;
		default:
			throw new RuntimeException("No dependencies found for " + pdt);
		}
	}

	public static void init() throws IOException {
		measurementData = null;

		Vector<File> folders = new Vector<File>();
		folders.add(new File(folderName));
		try {
			Path pPath = Paths.get(Config.class.getProtectionDomain()
					.getCodeSource().getLocation().toURI());
			if (pPath.getFileName().toString().endsWith(".jar")) {
				folders.add(pPath.toFile());
			}

		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		loadFromProperties(initFromFolders(folders));
	}

	public static ComparableEntry get(ProfilerDataType complexityType,
			String classname, AccessType accessType, String storedDataClass,
			ComplexityType.Base base) {
		return get(complexityType, classname, accessType, storedDataClass,
				base, false);
	}

	public static ComparableEntry get(ProfilerDataType complexityType,
			String classname, AccessType accessType, String storedDataClass,
			ComplexityType.Base base, boolean checkWithDefaults) {

		ProfilerDataType[] dependencies = getDependencies(complexityType);
		if (dependencies != null) {
			ComparableEntry[] res = new ComparableEntry[dependencies.length];
			int countee = 0;
			for (ProfilerDataType pdt : dependencies) {
				res[countee] = get(pdt, classname, accessType, storedDataClass,
						base, checkWithDefaults);
				countee++;
			}
			return new CombinedEntry(res, getWeights(complexityType));
		}

		String keyName = complexityType.toString().toUpperCase() + "_"
				+ classname.toUpperCase();

		if (checkWithDefaults)
			keyName = "DEFAULT_" + keyName;

		ComparableEntry c = get(keyName + "_"
				+ accessType.toString().toUpperCase() + "_"
				+ storedDataClass.toUpperCase());
		if (c == null)
			c = get(keyName + "_" + accessType.toString().toUpperCase());
		if (c == null)
			c = get(keyName);
		if (c == null) {
			if (checkWithDefaults) {
				/**
				 * This is the call where defaults are taken into account. If
				 * nothing is present here, the data is really missing
				 */
				throw new RuntimeException("Missing complexity entry for "
						+ complexityType.toString().toUpperCase() + "_"
						+ classname.toUpperCase() + "_"
						+ accessType.toString().toUpperCase() + "_"
						+ storedDataClass.toUpperCase());
			} else
				/**
				 * This is the call where defaults are not yet taken into
				 * account. Try to search for a dataset with the default values
				 * before failing
				 */
				return get(complexityType, classname, accessType,
						storedDataClass, base, true);
		}
		return c;
	}

	public static ComparableEntry get(String key) {
		if (measurementData == null)
			try {
				init();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		ComparableEntry res = measurementData.get(key);
		if (res != null) {
			res = res.clone();
		}
		return res;
	}

	public static ComparableEntry parseString(String key, String val) {
		if (key.startsWith("RUNTIMECOMPLEXITY")
				|| key.startsWith("MEMORYCOMPLEXITY")) {
			return Complexity.parseString(key, val);
		} else if (key.startsWith("MEMORYBENCHMARK")
				|| key.startsWith("RUNTIMEBENCHMARK")
				|| key.startsWith("DEFAULT_MEMORYBENCHMARK")
				|| key.startsWith("DEFAULT_RUNTIMEBENCHMARK")) {
			return BenchmarkingResult.parseString(key, val);
		} else {
			return null;
		}
	}

	public static ComparableEntryMap getMap(ProfilerDataType t) {
		switch (t) {
		case MemoryComplexity:
		case RuntimeComplexity:
			return new ComplexityMap();
		case MemoryBenchmark:
		case RuntimeBenchmark:
		case CombinedBenchmark:
			return new BenchmarkingResultsMap();
		default:
			throw new RuntimeException("Cannot create ComparableEntryMap for "
					+ t);
		}
	}

	public static void loadFromProperties(Properties in) {
		if (measurementData == null) {
			measurementData = new HashMap<String, ComparableEntry>();
		}

		for (String key : in.stringPropertyNames()) {
			String val = in.getProperty(key);
			ComparableEntry c = parseString(key, val);
			if (c != null) {
				measurementData.put(key, c);
			}
		}
	}
}
