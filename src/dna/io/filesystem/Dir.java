package dna.io.filesystem;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import dna.io.ZipReader;
import dna.io.filter.PrefixFilenameFilter;
import dna.metrics.IMetric;
import dna.util.Config;
import dna.util.Log;

/**
 * 
 * Gives the default storage path for data objects.
 * 
 * @author benni
 * 
 */
public class Dir {
	public static final String delimiter = "/";
	public static final String tempSuffix = "_";

	/*
	 * AGGREGATION
	 */

	public static String getAggregationDataDir(String dir) {
		return dir + Config.get("RUN_AGGREGATION") + Dir.delimiter;
	}

	public static String getAggregationBatchDir(String dir, long timestamp) {
		return Dir.getAggregationDataDir(dir)
				+ Config.get("PREFIX_BATCHDATA_DIR") + timestamp
				+ Dir.delimiter;
	}

	public static String getAggregatedMetricDataDir(String dir, long timestamp,
			String name) {
		return Dir.getAggregationBatchDir(dir, timestamp)
				+ Config.get("PREFIX_METRICDATA_DIR") + name + Dir.delimiter;
	}

	/*
	 * RUN data
	 */

	public static String getRunDataDir(String dir, int run) {
		return dir + Config.get("PREFIX_RUNDATA_DIR") + run + Dir.delimiter;
	}

	public static String[] getRuns(String dir) {
		return (new File(dir)).list(new PrefixFilenameFilter(Config
				.get("PREFIX_RUNDATA_DIR")));
	}

	public static int getRun(String runFolderName) {
		return Integer.parseInt(runFolderName.replaceFirst(
				Config.get("PREFIX_RUNDATA_DIR"), ""));
	}

	/*
	 * BATCH data
	 */

	public static String getBatchDataDir(String dir, long timestamp) {
		return dir + Config.get("PREFIX_BATCHDATA_DIR") + timestamp
				+ Dir.delimiter;
	}

	public static String getBatchDataDir(String dir, int run, long timestamp) {
		return Dir.getRunDataDir(dir, run) + Config.get("PREFIX_BATCHDATA_DIR")
				+ timestamp + Dir.delimiter;
	}

	public static String[] getBatchesIntelligent(String dir) throws IOException {
		if (Config.get("GENERATION_AS_ZIP").equals("runs")) {
			String splits[] = dir.split(Dir.delimiter);
			String tempDir = "";

			String fileName = splits[splits.length - 1]
					+ Config.get("SUFFIX_ZIP_FILE");
			for (int i = 0; i < splits.length - 1; i++)
				tempDir += splits[i] + Dir.delimiter;

			if (new File(tempDir + fileName).exists()) {
				ZipReader.setReadFilesystem(ZipReader.getFileSystem(
						tempDir,
						splits[splits.length - 1]
								+ Config.get("SUFFIX_ZIP_FILE")));
				String[] tempBatches = Dir.getBatches(Dir.delimiter);
				// System.out.println("tempBatches size: " + tempBatches.length);
				ZipReader.closeReadFilesystem();
				return tempBatches;
			}
		} else {
			return Dir.getBatches(dir);
		}
		return null;
	}

	public static String[] getBatches(String dir) throws IOException {
		if (ZipReader.isZipOpen()) {
			Path p = ZipReader.getPath(dir);
			ArrayList<String> fileList = new ArrayList<String>();
			try (DirectoryStream<Path> directoryStream = java.nio.file.Files
					.newDirectoryStream(p)) {
				for (Path file : directoryStream) {
					if ((file.getFileName().toString()).startsWith(Config
							.get("PREFIX_BATCHDATA_DIR"))) {
						fileList.add(file
								.getFileName()
								.toString()
								.substring(
										0,
										file.getFileName().toString().length() - 1));
					}
				}
			}

			// reverse list
			Collections.reverse(fileList);

			// return
			return (String[]) fileList.toArray(new String[0]);
		} else {
			String[] names = (new File(dir)).list(new PrefixFilenameFilter(
					Config.get("PREFIX_BATCHDATA_DIR")));
			int[] timestamps = new int[names.length];
			for (int i = 0; i < names.length; i++) {
				timestamps[i] = Integer.parseInt((names[i].replace(
						Config.get("PREFIX_BATCHDATA_DIR"), "")).replace(
						Config.get("SUFFIX_ZIP_FILE"), ""));
			}
			Arrays.sort(timestamps);
			for (int i = 0; i < timestamps.length; i++) {
				names[i] = Config.get("PREFIX_BATCHDATA_DIR") + timestamps[i];
			}
			return names;
		}
	}

	public static long getTimestamp(String batchFolderName) {
		return Long.parseLong(batchFolderName.replaceFirst(
				Config.get("PREFIX_BATCHDATA_DIR"), ""));
	}

	public static String[] getBatchesFromTo(String dir, long timestampFrom,
			long timestampTo, long stepSize, boolean intervalByIndex)
			throws IOException {
		// read batches from dir
		String[] tempBatches = Dir.getBatchesIntelligent(dir);

		// if interval by index
		if (intervalByIndex) {
			ArrayList<String> batchesList = new ArrayList<String>();

			if (timestampTo > Integer.MAX_VALUE
					|| timestampFrom > Integer.MAX_VALUE
					|| stepSize > Integer.MAX_VALUE)
				Log.error("Plotting interavl timestamps out of range. Take integers for plotting by index!");

			// parse index
			int indexFrom = (int) timestampFrom;
			int indexTo = (int) timestampTo;
			int step = (int) stepSize;

			// if indexTo below zero, plot until lastbatch + indexTo
			if (indexTo < 0)
				indexTo = tempBatches.length + indexTo;

			for (int i = indexFrom; i < indexTo; i += step) {
				// if out of bounds, continue
				if (i >= tempBatches.length)
					continue;

				// add to list
				batchesList.add(tempBatches[i]);
			}

			if (batchesList.size() == 0) {
				Log.warn("No batches found for plotting. Interval ["
						+ indexFrom + ":" + indexTo + "] stepsize " + step);
			}

			return batchesList.toArray(new String[batchesList.size()]);
		} else {
			// if interval by timestamps
			// init timestamps array
			long[] timestamps = new long[tempBatches.length];

			// get timestamps
			for (int i = 0; i < tempBatches.length; i++) {
				String[] splits = tempBatches[i].split("\\.");
				timestamps[i] = Long.parseLong(splits[splits.length - 1]);
			}

			// sort timestamps
			Arrays.sort(timestamps);

			// gather relevant batches
			ArrayList<String> batchesList = new ArrayList<String>();
			boolean firstBatch = true;
			int counter = 0;
			int firstBatchIndex = 0;
			for (int i = 0; i < timestamps.length; i++) {
				if (timestamps[i] < timestampFrom
						|| timestamps[i] > timestampTo)
					continue;
				if (timestamps[i] >= timestampFrom) {
					if (firstBatch) {
						batchesList.add(Config.get("PREFIX_BATCHDATA_DIR")
								+ timestamps[i]);
						firstBatch = false;
						firstBatchIndex = i;
						counter = 1;
					} else {
						long offset = counter * stepSize;
						if (i == firstBatchIndex + offset) {
							batchesList.add(Config.get("PREFIX_BATCHDATA_DIR")
									+ timestamps[i]);
							counter++;
						}
					}
				}
			}

			return batchesList.toArray(new String[batchesList.size()]);
		}
	}

	/*
	 * METRIC data
	 */

	public static String getMetricDataDir(String dir, String name) {
		return dir + Config.get("PREFIX_METRICDATA_DIR") + name + Dir.delimiter;
	}

	public static String getMetricDataDir(String dir, String name,
			IMetric.MetricType type) {
		switch (type) {
		case exact:
			return dir + Config.get("PREFIX_METRICDATA_DIR") + name
					+ Config.get("SUFFIX_METRIC_EXACT") + Dir.delimiter;
		case heuristic:
			return dir + Config.get("PREFIX_METRICDATA_DIR") + name
					+ Config.get("SUFFIX_METRIC_HEURISTIC") + Dir.delimiter;
		case quality:
			return dir + Config.get("PREFIX_METRICDATA_DIR") + name
					+ Dir.delimiter;
		default:
			return dir + Config.get("PREFIX_METRICDATA_DIR") + name
					+ Dir.delimiter;
		}
	}

	public static String getMetricDataDir(String dir, int run, long timestamp,
			String name) {
		return Dir.getBatchDataDir(dir, run, timestamp)
				+ Config.get("PREFIX_METRICDATA_DIR") + name + Dir.delimiter;
	}

	public static String getMetricDataDir(String dir, int run, long timestamp,
			String name, IMetric.MetricType type) {
		switch (type) {
		case exact:
			return Dir.getBatchDataDir(dir, run, timestamp)
					+ Config.get("PREFIX_METRICDATA_DIR") + name
					+ Config.get("SUFFIX_METRIC_EXACT") + Dir.delimiter;
		case heuristic:
			return Dir.getBatchDataDir(dir, run, timestamp)
					+ Config.get("PREFIX_METRICDATA_DIR") + name
					+ Config.get("SUFFIX_METRIC_HEURISTIC") + Dir.delimiter;
		case quality:
			return Dir.getBatchDataDir(dir, run, timestamp)
					+ Config.get("PREFIX_METRICDATA_DIR") + name
					+ Config.get("SUFFIX_METRIC_QUALITY") + Dir.delimiter;
		default:
			return Dir.getBatchDataDir(dir, run, timestamp)
					+ Config.get("PREFIX_METRICDATA_DIR") + name
					+ Dir.delimiter;
		}
	}

	public static String[] getMetrics(String dir) throws IOException {
		if (ZipReader.isZipOpen()) {
			Path p = ZipReader.getPath(dir);
			ArrayList<String> fileList = new ArrayList<String>();
			try (DirectoryStream<Path> directoryStream = java.nio.file.Files
					.newDirectoryStream(p)) {
				for (Path file : directoryStream) {
					if ((file.getFileName().toString()).startsWith(Config
							.get("PREFIX_METRICDATA_DIR"))) {
						fileList.add(file
								.getFileName()
								.toString()
								.substring(
										0,
										file.getFileName().toString().length() - 1));
					}
				}
			}
			return (String[]) fileList.toArray(new String[0]);
		} else {
			return (new File(dir)).list(new PrefixFilenameFilter(Config
					.get("PREFIX_METRICDATA_DIR")));
		}
	}

	public static String getMetricName(String metricFolderName) {
		return metricFolderName.replaceFirst(
				Config.get("PREFIX_METRICDATA_DIR"), "");
	}

}
