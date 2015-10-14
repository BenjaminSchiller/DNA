package dna.io.filesystem;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Path;
import java.util.ArrayList;

import dna.io.ZipReader;
import dna.io.filter.SuffixFilenameFilter;
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
			return name + Config.get("SUFFIX_DIST_BINNED_DOUBLE");
		case BINNED_INT:
			return name + Config.get("SUFFIX_DIST_BINNED_INT");
		case BINNED_LONG:
			return name + Config.get("SUFFIX_DIST_BINNED_LONG");
		case DOUBLE:
			return name + Config.get("SUFFIX_DIST_DOUBLE");
		case INT:
			return name + Config.get("SUFFIX_DIST_INT");
		case LONG:
			return name + Config.get("SUFFIX_DIST_LONG");
		default:
			return name + Config.get("SUFFIX_DIST");
		}
	}

	public static DistrType getDistributionTypeFromFilename(String name) {
		if (name.endsWith(Config.get("SUFFIX_DIST_BINNED_DOUBLE"))) {
			return DistrType.BINNED_DOUBLE;
		} else if (name.endsWith(Config.get("SUFFIX_DIST_BINNED_INT"))) {
			return DistrType.BINNED_INT;
		} else if (name.endsWith(Config.get("SUFFIX_DIST_BINNED_LONG"))) {
			return DistrType.BINNED_LONG;
		} else if (name.endsWith(Config.get("SUFFIX_DIST_DOUBLE"))) {
			return DistrType.DOUBLE;
		} else if (name.endsWith(Config.get("SUFFIX_DIST_INT"))) {
			return DistrType.INT;
		} else if (name.endsWith(Config.get("SUFFIX_DIST_LONG"))) {
			return DistrType.BINNED_LONG;
		}
		return null;
	}

	public static String getDistributionFilename(String name) {
		return name + Config.get("SUFFIX_DIST");
	}

	public static String getDistributionBinnedFilename(String name) {
		return name + Config.get("SUFFIX_DIST_BINNED");
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

	public static String getDistributionBinnedIntFilename(String name) {
		return name + Config.get("SUFFIX_DIST_BINNED_INT");
	}

	public static String getDistributionBinnedLongFilename(String name) {
		return name + Config.get("SUFFIX_DIST_BINNED_LONG");
	}

	public static String getDistributionBinnedDoubleFilename(String name) {
		return name + Config.get("SUFFIX_DIST_BINNED_DOUBLE");
	}

	public static String[] getDistributions(String dir) throws IOException {
		if (ZipReader.isZipOpen()) {
			Path p = ZipReader.getPath(dir);
			ArrayList<String> fileList = new ArrayList<String>();
			try (DirectoryStream<Path> directoryStream = java.nio.file.Files
					.newDirectoryStream(p)) {
				for (Path file : directoryStream) {
					if ((file.getFileName().toString())
							.endsWith("distribution")) {
						fileList.add(file.getFileName().toString());
					}
				}
			}
			return (String[]) fileList.toArray(new String[0]);
		} else {
			return (new File(dir))
					.list(new SuffixFilenameFilter("distribution"));
		}
	}

	public static String getDistributionNameFromFilename(String filename,
			DistrType type) {
		switch (type) {
		case BINNED_DOUBLE:
			return filename
					.replace(Config.get("SUFFIX_DIST_BINNED_DOUBLE"), "");
		case BINNED_INT:
			return filename.replace(Config.get("SUFFIX_DIST_BINNED_INT"), "");
		case BINNED_LONG:
			return filename.replace(Config.get("SUFFIX_DIST_BINNED_LONG"), "");
		case DOUBLE:
			return filename.replace(Config.get("SUFFIX_DIST_DOUBLE"), "");
		case INT:
			return filename.replace(Config.get("SUFFIX_DIST_INT"), "");
		case LONG:
			return filename.replace(Config.get("SUFFIX_DIST_LONG"), "");
		default:
			return filename.replace(Config.get("SUFFIX_DIST"), "");
		}
	}

	public static String getDistributionName(String filename) {
		return filename.replace(Config.get("SUFFIX_DIST"), "");
	}

	public static String getDistributionBinnedName(String filename) {
		return filename.replace(Config.get("SUFFIX_DIST_BINNED"), "");
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
}
