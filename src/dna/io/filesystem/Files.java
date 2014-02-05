package dna.io.filesystem;

import java.io.File;

import dna.io.filter.SuffixFilenameFilter;
import dna.util.Config;

public class Files {
	/*
	 * DISTRIBUTION
	 */
	public static String getDistributionFilename(String name) {
		return name + Config.get("SUFFIX_DIST");
	}

	public static String getDistributionIntFilename(String name) {
		return name + Config.get("SUFFIX_DIST_INT");
	}

	public static String getDistributionLongFilename(String name) {
		return name + Config.get("SUFFIX_DIST_LONG");
	}

	public static String getDistributionDoubleFilename(String name) {
		return name + Config.get("SUFFIX_DIST_DOUBLE");
	}

	public static String[] getDistributions(String dir) {
		return (new File(dir)).list(new SuffixFilenameFilter("distribution"));
	}

	public static String getDistributionName(String filename) {
		return filename.replace(Config.get("SUFFIX_DIST"), "");
	}

	/*
	 * RUNTIMES
	 */
	public static String getRuntimesFilename(String name) {
		return name + Config.get("SUFFIX_RUNTIME");
	}

	public static String[] getRuntimes(String dir) {
		return (new File(dir)).list(new SuffixFilenameFilter(Config
				.get("SUFFIX_RUNTIME")));
	}

	public static String getRuntimesName(String filename) {
		return filename.replace(Config.get("SUFFIX_RUNTIME"), "");
	}

	/*
	 * VALUES
	 */
	public static String getValuesFilename(String name) {
		return name + Config.get("SUFFIX_VALUE");
	}

	public static String[] getValues(String dir) {
		return (new File(dir)).list(new SuffixFilenameFilter(Config
				.get("SUFFIX_VALUE")));
	}

	public static String getValuesName(String filename) {
		return filename.replace(Config.get("SUFFIX_VALUE"), "");
	}

	/*
	 * NODEVALUELISTS
	 */
	public static String getNodeValueListFilename(String name) {
		return name + Config.get("SUFFIX_NVL");
	}

	public static String[] getNodeValueLists(String dir) {
		return (new File(dir)).list(new SuffixFilenameFilter(Config
				.get("SUFFIX_NVL")));
	}

	public static String getNodeValueListName(String filename) {
		return filename.replace(Config.get("SUFFIX_NVL"), "");
	}

	/*
	 * NODENODEVALUELISTS
	 */
	public static String getNodeNodeValueListFilename(String name) {
		return name + Config.get("SUFFIX_NNVL");
	}

	public static String[] getNodeNodeValueLists(String dir) {
		return (new File(dir)).list(new SuffixFilenameFilter(Config
				.get("SUFFIX_NNVL")));
	}

	public static String getNodeNodeValueListName(String filename) {
		return filename.replace(Config.get("SUFFIX_NNVL"), "");
	}

	/*
	 * PROFILER
	 */
	public static String getProfilerFilename(String name) {
		return name + Config.get("SUFFIX_PROFILER")
				+ Config.get("SUFFIX_VALUE");
	}
}
