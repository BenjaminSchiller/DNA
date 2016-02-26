package dna.io.filesystem;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Path;
import java.util.ArrayList;

import dna.io.LegacyDistributionReader.LegacyDistributionType;
import dna.io.ZipReader;
import dna.io.filter.SuffixFilenameFilter;
import dna.series.aggdata.AggregatedBatch;
import dna.series.aggdata.AggregatedBinnedDistribution;
import dna.series.data.BatchData;
import dna.series.data.IBatch;
import dna.series.data.distr.Distr;
import dna.series.data.distr.Distr.DistrType;
import dna.util.Config;

public class Files {
	/*
	 * ZIP FILENAMES
	 */
	public static String getBatchFilename(long timestamp) {
		return Config.get("PREFIX_BATCHDATA_DIR") + timestamp;
	}

	public static String getRunFilename(int run) {
		return Config.get("PREFIX_RUNDATA_DIR") + run
				+ Config.get("SUFFIX_ZIP_FILE");
	}

	public static String getAggregationFileName() {
		return Config.get("RUN_AGGREGATION") + Config.get("SUFFIX_ZIP_FILE");
	}

	/*
	 * DISTRIBUTION
	 */
	public static String getDistributionFilename(String name, DistrType type) {
		switch (type) {
		case BINNED_DOUBLE:
			return name + Config.get("SUFFIX_DIST_DOUBLE");
		case BINNED_INT:
			return name + Config.get("SUFFIX_DIST_INT");
		case BINNED_LONG:
			return name + Config.get("SUFFIX_DIST_LONG");
		case QUALITY_DOUBLE:
			return name + Config.get("SUFFIX_DIST_QUALITY_DOUBLE");
		case QUALITY_INT:
			return name + Config.get("SUFFIX_DIST_QUALITY_INT");
		case QUALITY_LONG:
			return name + Config.get("SUFFIX_DIST_QUALITY_LONG");
		default:
			return name + Config.get("SUFFIX_DIST");
		}
	}

	public static DistrType getDistributionTypeFromFilename(String name) {
		if (name.endsWith(Config.get("SUFFIX_DIST_DOUBLE"))) {
			return DistrType.BINNED_DOUBLE;
		} else if (name.endsWith(Config.get("SUFFIX_DIST_INT"))) {
			return DistrType.BINNED_INT;
		} else if (name.endsWith(Config.get("SUFFIX_DIST_LONG"))) {
			return DistrType.BINNED_LONG;
		} else if (name.endsWith(Config.get("SUFFIX_DIST_QUALITY_DOUBLE"))) {
			return DistrType.QUALITY_DOUBLE;
		} else if (name.endsWith(Config.get("SUFFIX_DIST_QUALITY_INT"))) {
			return DistrType.QUALITY_INT;
		} else if (name.endsWith(Config.get("SUFFIX_DIST_QUALITY_LONG"))) {
			return DistrType.QUALITY_LONG;
		}
		return null;
	}

	public static String getDistributionFilename(IBatch batch, String metric,
			String dist, boolean aggregated) {
		if (aggregated) {
			if (((AggregatedBatch) batch).getMetrics().get(metric)
					.getDistributions().get(dist) instanceof AggregatedBinnedDistribution)
				return Files.getAggregatedBinnedDistributionFilename(dist);
			else
				return Files.getAggregatedDistributionFilename(dist);
		} else {
			Distr<?, ?> d = ((BatchData) batch).getMetrics().get(metric)
					.getDistributions().get(dist);
			return Files.getDistributionFilename(d.getName(), d.getDistrType());
		}
	}

	public static String getAggregatedDistributionFilename(String name) {
		return name + Config.get("SUFFIX_DIST_AGGR");
	}

	public static String getAggregatedBinnedDistributionFilename(String name) {
		return name + Config.get("SUFFIX_DIST_AGGR_BINNED");
	}

	public static String[] getDistributions(String dir) throws IOException {
		return getDistributions(dir,
				Config.getBoolean("READ_LEGACY_DISTRIBUTIONS"));
	}

	public static String[] getDistributions(String dir, boolean supportLegacy)
			throws IOException {
		if (ZipReader.isZipOpen()) {
			Path p = ZipReader.getPath(dir);
			ArrayList<String> fileList = new ArrayList<String>();
			try (DirectoryStream<Path> directoryStream = java.nio.file.Files
					.newDirectoryStream(p)) {
				for (Path file : directoryStream) {
					if (Files.endsWithDistributionSuffix(file.getFileName()
							.toString(), supportLegacy)) {
						fileList.add(file.getFileName().toString());
					}
				}
			}
			return (String[]) fileList.toArray(new String[0]);
		} else {
			return (new File(dir)).list(new SuffixFilenameFilter(Files
					.getDistributionSuffixes(supportLegacy)));
		}
	}

	public static String getDistributionNameFromFilename(String filename,
			DistrType type) {
		switch (type) {
		case BINNED_DOUBLE:
			return filename.replace(Config.get("SUFFIX_DIST_DOUBLE"), "");
		case BINNED_INT:
			return filename.replace(Config.get("SUFFIX_DIST_INT"), "");
		case BINNED_LONG:
			return filename.replace(Config.get("SUFFIX_DIST_LONG"), "");
		case QUALITY_DOUBLE:
			return filename.replace(Config.get("SUFFIX_DIST_QUALITY_DOUBLE"),
					"");
		case QUALITY_INT:
			return filename.replace(Config.get("SUFFIX_DIST_QUALITY_INT"), "");
		case QUALITY_LONG:
			return filename.replace(Config.get("SUFFIX_DIST_QUALITY_LONG"), "");
		default:
			return null;
		}
	}

	public static String getDistributionNameFromFilename(String filename,
			LegacyDistributionType type) {
		switch (type) {
		case BINNED_DOUBLE:
			return filename.replace(
					Config.get("LEGACY_SUFFIX_DIST_BINNED_DOUBLE"), "");
		case BINNED_INT:
			return filename.replace(
					Config.get("LEGACY_SUFFIX_DIST_BINNED_INT"), "");
		case BINNED_LONG:
			return filename.replace(
					Config.get("LEGACY_SUFFIX_DIST_BINNED_LONG"), "");
		case DIST:
			return filename.replace(Config.get("LEGACY_SUFFIX_DIST"), "");
		case DOUBLE:
			return filename
					.replace(Config.get("LEGACY_SUFFIX_DIST_DOUBLE"), "");
		case INT:
			return filename.replace(Config.get("LEGACY_SUFFIX_DIST_INT"), "");
		case LONG:
			return filename.replace(Config.get("LEGACY_SUFFIX_DIST_LONG"), "");
		default:
			return null;
		}
	}

	public static String getAggregatedBinnedDistributionName(String filename) {
		return filename.replace(Config.get("SUFFIX_DIST_AGGR_BINNED"), "");
	}

	public static String getAggregatedDistributionName(String filename) {
		return filename.replace(Config.get("SUFFIX_DIST_AGGR"), "");
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
	 * LABELS
	 */
	public static String getLabelsFilename(String name) {
		return name + Config.get("SUFFIX_LABEL");
	}

	public static String getLabelsListFilename(int run) {
		return Config.get("BATCH_LABELS") + Config.get("FILE_NAME_DELIMITER")
				+ Config.get("PREFIX_RUNDATA_DIR") + run
				+ Config.get("SUFFIX_LABEL");
	}

	/*
	 * NODEVALUELISTS
	 */
	public static String getNodeValueListFilename(String name) {
		return name + Config.get("SUFFIX_NVL");
	}

	public static String[] getNodeValueLists(String dir) throws IOException {
		if (ZipReader.isZipOpen()) {
			Path p = ZipReader.getPath(dir);
			ArrayList<String> fileList = new ArrayList<String>();
			try (DirectoryStream<Path> directoryStream = java.nio.file.Files
					.newDirectoryStream(p)) {
				for (Path file : directoryStream) {
					if ((file.getFileName().toString()).endsWith(Config
							.get("SUFFIX_NVL"))) {
						fileList.add(file.getFileName().toString());
					}
				}
			}
			return (String[]) fileList.toArray(new String[0]);
		} else {
			return (new File(dir)).list(new SuffixFilenameFilter(Config
					.get("SUFFIX_NVL")));
		}

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

	/*
	 * FILESYSTEM OPERATIONS
	 */
	public static void delete(File file) {
		if (file.isDirectory()) {
			if (file.list().length == 0) {
				file.delete();
			} else {
				String files[] = file.list();

				for (String temp : files) {
					File fileDelete = new File(file, temp);
					delete(fileDelete);
				}
			}
			if (file.list().length == 0) {
				file.delete();
			}
		} else {
			file.delete();
		}
	}

	public static boolean endsWithDistributionSuffix(String name) {
		return Files.endsWithDistributionSuffix(name,
				Config.getBoolean("READ_LEGACY_DISTRIBUTIONS"));
	}

	public static boolean endsWithDistributionSuffix(String name,
			boolean supportLegacy) {
		for (String suffix : Files.getDistributionSuffixes()) {
			if (name.endsWith(suffix))
				return true;
		}

		if (supportLegacy) {
			for (String suffix : Files.getLegacyDistributionSuffixes()) {
				if (name.endsWith(suffix))
					return true;
			}
		}
		return false;
	}

	public static boolean endsWithLegacyDistributionSuffix(String name) {
		for (String suffix : Files.getLegacyDistributionSuffixes()) {
			if (name.endsWith(suffix))
				return true;
		}
		return false;
	}

	public static String[] getDistributionSuffixes(boolean includeLegacy) {
		if (!includeLegacy)
			return getDistributionSuffixes();

		String[] regularSuffixes = getDistributionSuffixes();
		String[] legacySuffixes = getLegacyDistributionSuffixes();

		int regLength = regularSuffixes.length;
		int legLength = legacySuffixes.length;

		String[] allSuffixes = new String[regLength + legLength];

		System.arraycopy(regularSuffixes, 0, allSuffixes, 0, regLength);
		System.arraycopy(legacySuffixes, 0, allSuffixes, regLength, legLength);

		return allSuffixes;
	}

	public static String[] getDistributionSuffixes() {
		return new String[] { Config.get("SUFFIX_DIST_DOUBLE"),
				Config.get("SUFFIX_DIST_INT"), Config.get("SUFFIX_DIST_LONG"),
				Config.get("SUFFIX_DIST_QUALITY_DOUBLE"),
				Config.get("SUFFIX_DIST_QUALITY_INT"),
				Config.get("SUFFIX_DIST_QUALITY_LONG"),
				Config.get("SUFFIX_DIST_AGGR"),
				Config.get("SUFFIX_DIST_AGGR_BINNED") };
	}

	public static String[] getLegacyDistributionSuffixes() {
		return new String[] { Config.get("LEGACY_SUFFIX_DIST_INT"),
				Config.get("LEGACY_SUFFIX_DIST_LONG"),
				Config.get("LEGACY_SUFFIX_DIST_DOUBLE"),
				Config.get("LEGACY_SUFFIX_DIST_BINNED_INT"),
				Config.get("LEGACY_SUFFIX_DIST_BINNED_LONG"),
				Config.get("LEGACY_SUFFIX_DIST_BINNED_DOUBLE"),
				Config.get("LEGACY_SUFFIX_DIST"),
				Config.get("LEGACY_SUFFIX_DIST_BINNED") };
	}

}
