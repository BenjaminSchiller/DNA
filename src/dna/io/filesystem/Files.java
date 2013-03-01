package dna.io.filesystem;

import java.io.File;

import dna.io.filter.SuffixFilenameFilter;

public class Files {
	/*
	 * DISTRIBUTION
	 */
	public static String getDistributionFilename(String name) {
		return name + Suffix.distribution;
	}

	public static String[] getDistributions(String dir) {
		return (new File(dir)).list(new SuffixFilenameFilter(
				Suffix.distribution));
	}

	public static String getDistributionName(String filename) {
		return filename.replace(Suffix.distribution, "");
	}

	/*
	 * RUNTIMES
	 */
	public static String getRuntimesFilename(String name) {
		return name + Suffix.runtimes;
	}

	public static String[] getRuntimes(String dir) {
		return (new File(dir)).list(new SuffixFilenameFilter(Suffix.runtimes));
	}

	public static String getRuntimesName(String filename) {
		return filename.replace(Suffix.runtimes, "");
	}

	/*
	 * VALUES
	 */
	public static String getValuesFilename(String name) {
		return name + Suffix.values;
	}

	public static String[] getValues(String dir) {
		return (new File(dir)).list(new SuffixFilenameFilter(Suffix.values));
	}

	public static String getValuesName(String filename) {
		return filename.replace(Suffix.values, "");
	}
}
