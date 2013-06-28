package dna.series;

<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import dna.io.filesystem.Dir;
import dna.io.filesystem.Files;
import dna.series.aggdata.AggregatedBatch;
import dna.series.aggdata.AggregatedData;
import dna.series.aggdata.AggregatedDistribution;
import dna.series.aggdata.AggregatedDistributionList;
import dna.series.aggdata.AggregatedMetric;
import dna.series.aggdata.AggregatedMetricList;
import dna.series.aggdata.AggregatedNodeValueList;
import dna.series.aggdata.AggregatedNodeValueListList;
import dna.series.aggdata.AggregatedRunTimeList;
import dna.series.aggdata.AggregatedSeries;
import dna.series.aggdata.AggregatedValue;
import dna.series.aggdata.AggregatedValueList;
import dna.series.data.Data;
import dna.series.data.DistributionDouble;
import dna.series.data.DistributionInt;
import dna.series.data.DistributionLong;
import dna.series.data.MetricData;
=======
=======
=======
import java.util.ArrayList;
import java.util.HashMap;

import dna.io.etc.Keywords;
import dna.io.filesystem.Names;
>>>>>>> Codeupdate 13-06-28
import dna.series.data.BatchData;
>>>>>>> Codeupdate 13-06-10.
import dna.series.aggdata.AggregatedData;
import dna.series.aggdata.AggregatedDataList;
import dna.series.aggdata.AggregatedDistribution;
import dna.series.aggdata.AggregatedNodeValueList;
import dna.series.aggdata.AggregatedRunTimeList;
import dna.series.aggdata.AggregatedSeries;
import dna.series.aggdata.AggregatedValue;
import dna.series.data.Data;
<<<<<<< HEAD
import dna.series.data.DiffData;
<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
>>>>>>> Codeupdate 13-06-10.
=======
import dna.series.data.Distribution;
import dna.series.data.DistributionInt;
import dna.series.data.DistributionLong;
import dna.series.data.NodeValueList;
>>>>>>> Codeupdate 13-06-18
=======
>>>>>>> Codeupdate 13-06-10.
=======
=======
>>>>>>> Codeupdate 13-06-28
import dna.series.data.Distribution;
import dna.series.data.DistributionInt;
import dna.series.data.DistributionLong;
import dna.series.data.NodeValueList;
>>>>>>> Codeupdate 13-06-18
import dna.series.data.RunData;
import dna.series.data.SeriesData;
import dna.series.data.Value;
import dna.series.lists.DistributionList;
import dna.series.lists.NodeValueListList;
<<<<<<< HEAD
import dna.series.lists.RunTimeList;
=======
>>>>>>> Codeupdate 13-06-28
import dna.series.lists.ValueList;
import dna.util.ArrayUtils;
<<<<<<< HEAD
<<<<<<< HEAD
import dna.util.Config;
=======
import dna.util.Log;
>>>>>>> Codeupdate 13-06-10.
=======
import dna.util.Log;
>>>>>>> Codeupdate 13-06-10.

/**
 * 
 * This class provides methods to aggregate data from different sources, e.g.,
 * multiple runs of the same configuration, int a unified datastructure.
 * 
 * @author benni
 * 
 */
public class Aggregation {

	/**
<<<<<<< HEAD
=======
	 * aggregates all data in the given series, i.e., aggregate each diff of all
	 * runs.
	 * 
	 * @param seriesData
	 *            data of the series to be aggregated
	 * @return RunData object containing aggregated versions of all
	 * @throws AggregationException
	 *             in case the various values are not consistent in all runs
	 */
	public static RunData aggregate(SeriesData seriesData)
			throws AggregationException {
		Aggregation.test(seriesData);

		RunData aggregatedRun = new RunData(-1, seriesData.getRun(0).getDiffs()
				.size());
		for (int i = 0; i < seriesData.getRun(0).getDiffs().size(); i++) {
			DiffData[] diffs = new DiffData[seriesData.getRuns().size()];
			for (int j = 0; j < seriesData.getRuns().size(); j++) {
				diffs[j] = seriesData.getRun(j).getDiffs().get(i);
			}
			Aggregation.test(diffs);

			DiffData d = diffs[0];
			DiffData aggregatedDiff = new DiffData(d.getTimestamp(), d
					.getValues().size(), d.getGeneralRuntimes().size(), d
					.getMetricRuntimes().size(), d.getMetrics().size());

			for (Value v : d.getValues().getList()) {
				double[] values = new double[diffs.length];
				for (int j = 0; j < diffs.length; j++) {
					try {
						values[j] = diffs[j].getValues().get(v.getName())
								.getValue();
					} catch (NullPointerException e) {
						throw new AggregationException("value " + v.getName()
								+ " not found @ " + j);
					}
				}
				aggregatedDiff.getValues().add(
						new Value(v.getName(), ArrayUtils.med(values)));
			}

			for (RunTime rt : d.getGeneralRuntimes().getList()) {
				double[] values = new double[diffs.length];
				for (int j = 0; j < diffs.length; j++) {
					try {
						values[j] = diffs[j].getGeneralRuntimes()
								.get(rt.getName()).getRuntime();
					} catch (NullPointerException e) {
						throw new AggregationException("general-runtime "
								+ rt.getRuntime() + " not found @ " + j);
					}
				}

				aggregatedDiff.getGeneralRuntimes()
						.add(new RunTime(rt.getName(), (long) ArrayUtils
								.med(values)));
			}

			for (RunTime rt : d.getMetricRuntimes().getList()) {
				double[] values = new double[diffs.length];
				for (int j = 0; j < diffs.length; j++) {
					try {
						values[j] = diffs[j].getMetricRuntimes()
								.get(rt.getName()).getRuntime();
					} catch (NullPointerException e) {
						throw new AggregationException("metric-runtime "
								+ rt.getRuntime() + " not found @ " + j);
					}
				}

				aggregatedDiff.getMetricRuntimes()
						.add(new RunTime(rt.getName(), (long) ArrayUtils
								.med(values)));
			}

			aggregatedRun.getDiffs().add(aggregatedDiff);
		}

		return aggregatedRun;
	}

	/**
>>>>>>> datatype NodeValueList added
	 * Computes the average value of a list of values.
	 * 
	 * @param list
	 *            list of values to compute the average of.
	 * @param name
	 *            name for the computed value
	 * @return average value of the given list of values.
	 */
	public static Value average(Value[] list, String name) {
		double[] values = new double[list.length];

		for (int i = 0; i < list.length; i++) {
			values[i] = list[i].getValue();
		}

		return new Value(name, ArrayUtils.avg(values));
	}

	/**
	 * Average Values object of a list of Values objects.
	 * 
	 * @param list
	 *            list of Values object to compute the average for
	 * @param name
	 *            name of the new Values object
	 * @return average Values object of the given list
	 * @throws AggregationException
	 */
	public static Values average(Values[] list, String name)
			throws AggregationException {
		Aggregation.test(list);

		double[][] values = new double[list[0].getValues().length][2];
		for (int i = 0; i < values.length; i++) {
			values[i][0] = list[0].getValues()[i][0];
			double[] temp = new double[list.length];
			for (int j = 0; j < list.length; j++) {
				temp[j] = list[j].getValues()[i][1];
			}
			values[i][1] = ArrayUtils.avg(temp);
		}
		return new Values(values, name);
	}

<<<<<<< HEAD
<<<<<<< HEAD
	/**
	 * Calculates the maximum of Value objects of a list of Values objects.
	 * 
	 * @param list
	 *            list of Values object to compute the maximum for
	 * 
	 * @param name
	 *            name of the new Values object
	 * 
	 * @return maximum Values object of the given list
	 * @throws AggregationException
	 */
	public static Values maximum(Values[] list, String name)
			throws AggregationException {
=======
	/** UNDER CONSTRUCTION
=======
	/**
>>>>>>> Codeupdate 13-06-10.
	 * Calculates the maximum of Value objects of a list of Values objects.
	 * 
	 * @param list list of Values object to compute the maximum for
	 *            
	 * @param name name of the new Values object
	 *            
	 * @return maximum Values object of the given list
	 * @throws AggregationException
	 */
	public static Values maximum(Values[] list, String name) throws AggregationException {
>>>>>>> datatype NodeValueList added
		Aggregation.test(list);

		double[][] values = new double[list[0].getValues().length][2];
		for (int i = 0; i < values.length; i++) {
			values[i][0] = list[0].getValues()[i][0];
			double[] temp = new double[list.length];
			for (int j = 0; j < list.length; j++) {
				temp[j] = list[j].getValues()[i][1];
<<<<<<< HEAD
=======
			}
			values[i][1] = ArrayUtils.max(temp);
		}
		return new Values(values, name);
	}
	
	private static void test(SeriesData seriesData) throws AggregationException {
		int diffs = seriesData.getRun(0).getDiffs().size();
		for (int i = 0; i < seriesData.getRuns().size(); i++) {
			if (diffs != seriesData.getRun(i).getDiffs().size()) {
				throw new AggregationException(
						"cannot aggregate runs with different # of diffs: "
								+ seriesData.getRun(i).getDiffs().size()
								+ " != " + diffs + " @");
>>>>>>> datatype NodeValueList added
			}
			values[i][1] = ArrayUtils.max(temp);
		}
		return new Values(values, name);
	}

	/**
	 * Calculates the maximum of Value objects of a list of Values objects.
	 * 
	 * @param list list of Values object to compute the maximum for
	 *            
	 * @param name name of the new Values object
	 *            
	 * @return maximum Values object of the given list
	 * @throws AggregationException
	 */
	public static Values maximum(Values[] list, String name) throws AggregationException {
		Aggregation.test(list);

		double[][] values = new double[list[0].getValues().length][2];
		for (int i = 0; i < values.length; i++) {
			values[i][0] = list[0].getValues()[i][0];
			double[] temp = new double[list.length];
			for (int j = 0; j < list.length; j++) {
				temp[j] = list[j].getValues()[i][1];
			}
			values[i][1] = ArrayUtils.max(temp);
		}
		return new Values(values, name);
	}
	
	private static void test(SeriesData seriesData) throws AggregationException {
		int batches = seriesData.getRun(0).getBatches().size();
		for (int i = 0; i < seriesData.getRuns().size(); i++) {
			if (batches != seriesData.getRun(i).getBatches().size()) {
				throw new AggregationException(
						"cannot aggregate runs with different # of batches: "
								+ seriesData.getRun(i).getBatches().size()
								+ " != " + batches + " @");
			}
		}
	}

	private static void test(Values[] list) throws AggregationException {
		int length = list[0].getValues().length;
		for (int i = 0; i < list.length; i++) {
			for (int j = 0; j < list[i].getValues().length; j++) {
				if (list[i].getValues().length != length) {
					throw new AggregationException(
							"cannot aggregate values of different length ("
									+ list[i].getValues().length + " != "
									+ length + " @ " + i);
				}
				if (list[i].getValues()[j].length != 2) {
					throw new AggregationException(
							"cannot aggregate values with length "
									+ list[i].getValues().length + " @ " + i
									+ "/" + j);
				}
				if (list[i].getValues()[j][0] != list[(i + 1) % list.length]
						.getValues()[j][0]) {
					throw new AggregationException(
							"cannot aggregate values with differing x values @ "
									+ i
									+ "/"
									+ j
									+ " ("
									+ list[i].getValues()[j][0]
									+ " != "
									+ list[(i + 1) % list.length].getValues()[j][0]);
				}
			}
		}
	}
<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD

	/**
	 * Tests the obejcts of an arry of Data objects if they are compatible for
	 * an aggregation. Throws AggregationException when not.
	 * 
	 * @param inputData
	 *            Array of Data objects that are about to be tested
	 * 
	 * @throws AggregationException
	 */
	public static void test(Data[] inputData) throws AggregationException {

		// if (inputData.length < 2) {
		// throw new AggregationException(
		// "cannot aggregate on less than 2 values");
		// }

		for (int i = 0; i < inputData.length - 1; i++) {
			if (!Data.equals(inputData[i], inputData[i + 1])) {
				throw new AggregationException(
						"cannot aggregate values of different type "
								+ inputData[i].getName() + " != "
								+ inputData[i + 1].getName());
			}
		}

		// if(inputData[0] instanceof Distribution) {
		// if(inputData[0] instanceof DistributionInt) {
		// length = ((DistributionInt) inputData[0]).getIntValues().length;
		//
		// for(int i = 1; i < inputData.length; i++) {
		// if(length != ((DistributionInt) inputData[i]).getIntValues().length)
		// {
		// throw new AggregationException (
		// "cannot aggregate values of different length ("
		// + ((DistributionInt) inputData[i]).getIntValues().length + " != "
		// + length + " @ " + i);
		// }
		// }
		// } else {
		// if(inputData[0] instanceof DistributionLong) {
		// length = ((DistributionLong) inputData[0]).getLongValues().length;
		//
		// for(int i = 1; i < inputData.length; i++) {
		// if(length != ((DistributionLong)
		// inputData[i]).getLongValues().length) {
		// throw new AggregationException (
		// "cannot aggregate values of different length ("
		// + ((DistributionLong) inputData[i]).getLongValues().length + " != "
		// + length + " @ " + i);
		// }
		// }
		// } else {
		// length = ((Distribution) inputData[0]).getValues().length;
		//
		// for(int i = 1; i < inputData.length; i++) {
		// if(length != ((Distribution) inputData[i]).getValues().length) {
		// throw new AggregationException (
		// "cannot aggregate values of different length ("
		// + ((Distribution) inputData[i]).getValues().length + " != "
		// + length + " @ " + i);
		// }
		// }
		// }
		// }
		// }
		// if(inputData[0] instanceof NodeValueList) {
		// length = ((NodeValueList) inputData[0]).getValues().length;
		//
		// for(int i = 1; i < inputData.length; i++) {
		// if(length != ((NodeValueList) inputData[i]).getValues().length) {
		// throw new AggregationException (
		// "cannot aggregate values of different length ("
		// + ((NodeValueList) inputData[i]).getValues().length + " != "
		// + length + " @ " + i);
		// }
		// }
		// }
	}

	/**
	 * Aggregates the values for a SeriesData object.
	 * 
	 * @param seriesData
	 *            SeriesData object that is about to be aggregated
	 * @return AggregatedSeries object containing the aggregated values
	 * @throws AggregationException
	 * @throws IOException
	 */
	public static AggregatedSeries aggregate(SeriesData seriesData)
			throws AggregationException, IOException {
		return Aggregation.aggregateRuns(seriesData, seriesData.getRuns());
	}

	/**
	 * Method is used to aggregate runs of a given range.
	 * 
	 * @param seriesData
	 *            SeriesData object that is about to be aggregated
	 * @param from
	 *            Index of the first run
	 * @param to
	 *            Index of the last run
	 * @return AggregatedSeries object containing the aggregated runs
	 * @throws AggregationException
	 * @throws IOException
	 */
	public static AggregatedSeries aggregate(SeriesData seriesData, int from,
			int to) throws AggregationException, IOException {
		ArrayList<RunData> rdList = new ArrayList<RunData>();

		Aggregation.test(seriesData);
		// check all RunData-Objects for compatibility
		for (int i = 0; i < rdList.size() - 1; i++) {
			if (!RunData.isSameType(seriesData.getRun(i),
					seriesData.getRun(i + 1)))
				throw new AggregationException("RunDatas not of the same type!");
		}

		for (int i = from; i < to + 1; i++) {
			try {
				rdList.add(seriesData.getRun(i));
			} catch (IndexOutOfBoundsException e) {
				throw new AggregationException("Trying to aggregate over run "
						+ i + " from series " + seriesData.getName()
						+ " which is not available.");
			}
		}
		return Aggregation.aggregateRuns(seriesData, rdList);
	}

	/**
	 * Aggregates the runs of a given SeriesData object.
	 * 
	 * @param seriesData
	 *            SeriesData object that is about to be aggregated
	 * @param rdList
	 *            RunDataList containing the runs that are about to be
	 *            aggregated
	 * @return AggregatedSeries object containing the aggregated values
	 * @throws AggregationException
	 * @throws IOException
	 */
	private static AggregatedSeries aggregateRuns(SeriesData seriesData,
			ArrayList<RunData> rdList) throws AggregationException, IOException {
		int runs = rdList.size();
		int batches = rdList.get(0).getBatches().size();

		// for every run collect data of same batch of same metric of same type
		// and aggregated them
		// note: compatibility between batches and metrics already checked above
		String seriesDir = seriesData.getDir();

		String aggDir = Dir.getAggregationDataDir(seriesDir);

		/*
		 * BATCHES
		 */
		AggregatedBatch[] aggBatches = new AggregatedBatch[batches];

		for (int batchX = 0; batchX < batches; batchX++) {
			int batchXTimestamp = (int) rdList.get(0).getBatches().get(batchX)
					.getTimestamp();
			String batchDir = Dir.getBatchDataDir(aggDir, batchXTimestamp);

			/*
			 * GENERAL RUNTIMES
			 */
			AggregatedRunTimeList aggGeneralRuntime = new AggregatedRunTimeList(
					Config.get("BATCH_GENERAL_RUNTIMES"));
			HashMap<String, double[]> aggGeneralRunTime = new HashMap<String, double[]>();
			for (String genRuntimeX : rdList.get(0).getBatches().get(batchX)
					.getGeneralRuntimes().getNames()) {

				double[] values = new double[runs];

				for (int i = 0; i < runs; i++) {
					long tempTimestamp = rdList.get(i).getBatches().get(batchX)
							.getTimestamp();
					String dir = Dir.getBatchDataDir(Dir.getRunDataDir(
							seriesData.getDir(), rdList.get(i).getRun()),
							tempTimestamp);
					RunTimeList tempGeneralRunTime = RunTimeList.read(
							dir,
							Config.get("BATCH_GENERAL_RUNTIMES")
									+ Config.get("SUFFIX_RUNTIME"));
					values[i] = tempGeneralRunTime.get(genRuntimeX)
							.getRuntime();
				}
				aggGeneralRunTime.put(genRuntimeX,
						Aggregation.aggregate(values));
			}
			AggregatedData.write(aggGeneralRunTime, batchDir, Files
					.getRuntimesFilename(Config.get("BATCH_GENERAL_RUNTIMES")));

			/*
			 * METRIC RUNTIMES
			 */
			AggregatedRunTimeList aggMetricRuntime = new AggregatedRunTimeList(
					Config.get("BATCH_METRIC_RUNTIMES"));
			HashMap<String, double[]> aggMetricRunTime = new HashMap<String, double[]>();

			for (String metRuntimeX : rdList.get(0).getBatches().get(batchX)
					.getMetricRuntimes().getNames()) {
				double[] values = new double[runs];

				for (int i = 0; i < runs; i++) {
					long tempTimestamp = rdList.get(i).getBatches().get(batchX)
							.getTimestamp();
					String dir = Dir.getBatchDataDir(Dir.getRunDataDir(
							seriesData.getDir(), rdList.get(i).getRun()),
							tempTimestamp);
					RunTimeList tempMetricRunTime = RunTimeList.read(
							dir,
							Config.get("BATCH_METRIC_RUNTIMES")
									+ Config.get("SUFFIX_RUNTIME"));
					values[i] = tempMetricRunTime.get(metRuntimeX).getRuntime();
				}
				aggMetricRunTime
						.put(metRuntimeX, Aggregation.aggregate(values));
			}
			AggregatedData.write(aggMetricRunTime, batchDir, Files
					.getRuntimesFilename(Config.get("BATCH_METRIC_RUNTIMES")));

			/*
			 * BATCH STATISTICS
			 */
			AggregatedValueList aggStats = new AggregatedValueList();
			HashMap<String, double[]> aggBatchStats = new HashMap<String, double[]>();
			for (String statX : rdList.get(0).getBatches().get(batchX)
					.getValues().getNames()) {
				double[] values = new double[runs];

				for (int i = 0; i < runs; i++) {
					long tempTimestamp = rdList.get(i).getBatches().get(batchX)
							.getTimestamp();
					String dir = Dir.getBatchDataDir(Dir.getRunDataDir(
							seriesData.getDir(), rdList.get(i).getRun()),
							tempTimestamp);
					ValueList vList = ValueList.read(
							dir,
							Config.get("BATCH_STATS")
									+ Config.get("SUFFIX_VALUE"));
					values[i] = vList.get(statX).getValue();
				}
				aggBatchStats.put(statX, Aggregation.aggregate(values));
			}
			AggregatedData.write(aggBatchStats, batchDir,
					Files.getValuesFilename(Config.get("BATCH_STATS")));

			/*
			 * METRICS
			 */
			AggregatedMetricList aggMetrics = new AggregatedMetricList();
			for (String metricX : rdList.get(0).getBatches().get(batchX)
					.getMetrics().getNames()) {
				DistributionList dbList1 = rdList.get(0).getBatches()
						.get(batchX).getMetrics().get(metricX)
						.getDistributions();
				NodeValueListList nvList1 = rdList.get(0).getBatches()
						.get(batchX).getMetrics().get(metricX).getNodeValues();
				ValueList nList1 = rdList.get(0).getBatches().get(batchX)
						.getMetrics().get(metricX).getValues();

				String destDir = Dir.getMetricDataDir(
						Dir.getBatchDataDir(aggDir, batchXTimestamp), metricX,
						rdList.get(0).getBatches().get(batchX).getMetrics()
								.get(metricX).getType());

				// reading metric X for batch X for each run from filesystem
				MetricData[] Metrics = new MetricData[runs];

				for (int i = 0; i < runs; i++) {
					long tempTimestamp = rdList.get(i).getBatches().get(batchX)
							.getTimestamp();
					String dir = Dir.getBatchDataDir(Dir.getRunDataDir(
							seriesData.getDir(), rdList.get(i).getRun()),
							tempTimestamp);
					Metrics[i] = MetricData.read(
							Dir.getMetricDataDir(dir, metricX,
									rdList.get(i).getBatches().get(batchX)
											.getMetrics().get(metricX)
											.getType()), metricX, true);
				}

				// DISTRIBUTIONS
				AggregatedDistributionList aggDistributions = new AggregatedDistributionList();
				for (String distributionX : dbList1.getNames()) {
					boolean aggregated = false;
					// DistributionInt
					if (!aggregated
							&& seriesData.getRun(0).getBatches().get(batchX)
									.getMetrics().get(metricX)
									.getDistributions().get(distributionX) instanceof DistributionInt) {
						int amountValues = ((DistributionInt) Metrics[0]
								.getDistributions().get(distributionX))
								.getIntValues().length;
						double[][] aggregatedValues = new double[amountValues][runs];

						for (int i = 0; i < runs; i++) {
							for (int j = 0; j < amountValues; j++) {
								long[] values = new long[runs];
								for (int k = 0; k < runs; k++) {
									try {
										values[k] = ((DistributionInt) Metrics[k]
												.getDistributions().get(
														distributionX))
												.getIntValues()[j];
									} catch (ArrayIndexOutOfBoundsException e) {
										values[k] = 0;
									}
								}
								double[] temp = Aggregation.aggregate(values);
								double[] temp2 = new double[temp.length + 1];
								temp2[0] = j;
								for (int k = 0; k < temp.length; k++) {
									temp2[k + 1] = temp[k];
								}
								aggregatedValues[j] = temp2;
							}
						}
						AggregatedDistribution.write(destDir,
								Files.getDistributionFilename(distributionX),
								aggregatedValues);
						aggDistributions.add(new AggregatedDistribution(
								distributionX));
						aggregated = true;
					}

					// DistributionLong
					if (!aggregated
							&& seriesData.getRun(0).getBatches().get(batchX)
									.getMetrics().get(metricX)
									.getDistributions().get(distributionX) instanceof DistributionLong) {
						int amountValues = ((DistributionLong) Metrics[0]
								.getDistributions().get(distributionX))
								.getLongValues().length;
						double[][] aggregatedValues = new double[amountValues][runs];

						for (int i = 0; i < runs; i++) {
							for (int j = 0; j < amountValues; j++) {
								long[] values = new long[runs];
								for (int k = 0; k < runs; k++) {
									try {
										values[k] = ((DistributionLong) Metrics[k]
												.getDistributions().get(
														distributionX))
												.getLongValues()[j];
									} catch (ArrayIndexOutOfBoundsException e) {
										values[k] = 0;
									}
								}
								double[] temp = Aggregation.aggregate(values);
								double[] temp2 = new double[temp.length + 1];
								temp2[0] = j;
								for (int k = 0; k < temp.length; k++) {
									temp2[k + 1] = temp[k];
								}
								aggregatedValues[j] = temp2;
							}
						}
						AggregatedDistribution.write(destDir,
								Files.getDistributionFilename(distributionX),
								aggregatedValues);
						aggDistributions.add(new AggregatedDistribution(
								distributionX));
						aggregated = true;
					}
					// DistributionDouble
					if (!aggregated
							&& seriesData.getRun(0).getBatches().get(batchX)
									.getMetrics().get(metricX)
									.getDistributions().get(distributionX) instanceof DistributionDouble) {
						int amountValues = ((DistributionDouble) Metrics[0]
								.getDistributions().get(distributionX))
								.getDoubleValues().length;
						double[][] aggregatedValues = new double[amountValues][runs];

						for (int i = 0; i < runs; i++) {
							for (int j = 0; j < amountValues; j++) {
								double[] values = new double[runs];
								for (int k = 0; k < runs; k++) {
									try {
										values[k] = ((DistributionDouble) Metrics[k]
												.getDistributions().get(
														distributionX))
												.getDoubleValues()[j];
									} catch (ArrayIndexOutOfBoundsException e) {
										values[k] = 0;
									}
								}
								double[] temp = Aggregation.aggregate(values);
								double[] temp2 = new double[temp.length + 1];
								temp2[0] = j;
								for (int k = 0; k < temp.length; k++) {
									temp2[k + 1] = temp[k];
								}
								aggregatedValues[j] = temp2;
							}
						}
						AggregatedDistribution.write(destDir,
								Files.getDistributionFilename(distributionX),
								aggregatedValues);
						aggDistributions.add(new AggregatedDistribution(
								distributionX));
						aggregated = true;
					}

					// distribution of type distribution, not
					// Int/Long or Double
					if (!aggregated) {
						int amountValues = Metrics[0].getDistributions()
								.get(distributionX).getValues().length;
						double[][] aggregatedValues = new double[amountValues][runs];

						for (int i = 0; i < runs; i++) {
							for (int j = 0; j < amountValues; j++) {
								double[] values = new double[runs];
								for (int k = 0; k < runs; k++) {
									try {
										values[k] = Metrics[k]
												.getDistributions()
												.get(distributionX).getValues()[j];
									} catch (ArrayIndexOutOfBoundsException e) {
										values[k] = 0;
									}
								}
								double[] temp = Aggregation.aggregate(values);
								double[] temp2 = new double[temp.length + 1];
								temp2[0] = j;
								for (int k = 0; k < temp.length; k++) {
									temp2[k + 1] = temp[k];
								}
								aggregatedValues[j] = temp2;
							}
						}
						AggregatedDistribution.write(destDir,
								Files.getDistributionFilename(distributionX),
								aggregatedValues);
						aggDistributions.add(new AggregatedDistribution(
								distributionX));
						aggregated = true;
					}
				}

				// NODEVALUELISTS
				AggregatedNodeValueListList aggNodeValues = new AggregatedNodeValueListList();
				for (String nodevaluelistX : nvList1.getNames()) {
					int amountValues = Metrics[0].getNodeValues()
							.get(nodevaluelistX).getValues().length;
					double[][] aggregatedValues = new double[amountValues][runs];

					for (int i = 0; i < runs; i++) {
						for (int j = 0; j < amountValues; j++) {
							double[] values = new double[runs]; //
							for (int k = 0; k < runs; k++) {
								values[k] = Metrics[i].getNodeValues()
										.get(nodevaluelistX).getValues()[j]; //
							}
							double[] temp = Aggregation.aggregate(values);
							double[] temp2 = new double[temp.length + 1];
							temp2[0] = j;
							for (int k = 0; k < temp.length; k++) {
								temp2[k + 1] = temp[k];
							}
							aggregatedValues[j] = temp2;
						}
					}
					AggregatedNodeValueList.write(destDir,
							Files.getNodeValueListFilename(nodevaluelistX),
							aggregatedValues);
					aggNodeValues.add(new AggregatedNodeValueList(
							nodevaluelistX));
				}

				// VALUES
				AggregatedValueList aggValues = new AggregatedValueList();
				HashMap<String, double[]> aggregatedValues = new HashMap<String, double[]>();
				for (String valueX : nList1.getNames()) {
					double[] valueTemp = new double[runs];
					for (int i = 0; i < runs; i++) {
						valueTemp[i] = Metrics[i].getValues().get(valueX)
								.getValue();
					}
					double[] values = Aggregation.aggregate(valueTemp);
					aggregatedValues.put(valueX, values);
					aggValues.add(new AggregatedValue(valueX));
				}
				AggregatedValue.write(
						aggregatedValues,
						destDir,
						Config.get("METRIC_DATA_VALUES")
								+ Config.get("SUFFIX_VALUE"));
				aggMetrics.add(new AggregatedMetric(metricX, aggValues,
						aggDistributions, aggNodeValues));
			}
			aggBatches[batchX] = new AggregatedBatch(batchXTimestamp, aggStats,
					aggGeneralRuntime, aggMetricRuntime, aggMetrics);
		}
		return new AggregatedSeries(aggBatches);
	}

	/**
	 * Aggregates over the given inputData.
	 * 
	 * @param inputData
	 * @return double array containing the aggregated data
	 */
	public static double[] aggregate(double[] inputData) {
		// aggregated array structure: { avg, min, max, median, variance,
		// variance-low, variance-up, confidence-low, confidence-up }
		double avg = ArrayUtils.avg(inputData);
		double[] varLowUp = ArrayUtils.varLowUp(inputData, avg);
		double[] conf = ArrayUtils.conf(inputData);
		double[] temp = { avg, ArrayUtils.min(inputData),
				ArrayUtils.max(inputData), ArrayUtils.med(inputData),
				varLowUp[0], varLowUp[1], varLowUp[2], conf[0], conf[1] };

		return temp;
	}

	/**
	 * Aggregates over the given inputData.
	 * 
	 * @param inputData
	 * @return double array containing the aggregated data
	 */
	public static double[] aggregate(long[] inputData) {
		// aggregated array structure: { avg, min, max, median, variance,
		// variance-low, variance-up, confidence-low, confidence-up }
		double avg = ArrayUtils.avg(inputData);
		double[] varLowUp = ArrayUtils.varLowUp(inputData, avg);
		double[] conf = ArrayUtils.conf(inputData);
		double[] temp = { avg, ArrayUtils.min(inputData),
				ArrayUtils.max(inputData), ArrayUtils.med(inputData),
				varLowUp[0], varLowUp[1], varLowUp[2], conf[0], conf[1] };

		return temp;
	}

	/**
	 * Aggregates over the given inputData.
	 * 
	 * @param inputData
	 * @return double array containing the aggregated data
	 */
	public static double[] aggregate(int[] inputData) {
		// aggregated array structure: { avg, min, max, median, variance,
		// variance-low, variance-up, confidence-low, confidence-up }
		double avg = ArrayUtils.avg(inputData);
		double[] varLowUp = ArrayUtils.varLowUp(inputData, avg);
		double[] conf = ArrayUtils.conf(inputData);
		double[] temp = { avg, ArrayUtils.min(inputData),
				ArrayUtils.max(inputData), ArrayUtils.med(inputData),
				varLowUp[0], varLowUp[1], varLowUp[2], conf[0], conf[1] };

		return temp;
=======
	
=======
	/*
>>>>>>> Codeupdate 13-06-18
=======
	
>>>>>>> Codeupdate 13-06-24
	/**
	 * Tests the obejcts of an arry of Data objects if they are compatible for an aggregation.
	 * Throws AggregationException when not.
	 * 
	 * @param inputData Array of Data objects that are about to be tested         
	 *            
	 * @throws AggregationException
	 */
	public static void test(Data[] inputData) throws AggregationException {
		int length;
		
		if(inputData.length < 2) {
			throw new AggregationException(
					"cannot aggregate on less than 2 values");
		}
		for(int i = 0; i < inputData.length-1; i++) {
			if(!Data.equals(inputData[i], inputData[i+1])) {
				throw new AggregationException(
					"cannot aggregate values of different type "
						+	inputData[i].getName() 
						+ " != " 
						+ 	inputData[i+1].getName());
			}
			
		}

		if(inputData[0] instanceof Distribution) {
			if(inputData[0] instanceof DistributionInt) {
				length = ((DistributionInt) inputData[0]).getIntValues().length;
				
				for(int i = 1; i < inputData.length; i++) {
					if(length != ((DistributionInt) inputData[i]).getIntValues().length) {
						throw new AggregationException (
							"cannot aggregate values of different length ("
									+ ((DistributionInt) inputData[i]).getIntValues().length + " != "
									+ length + " @ " + i);
					}	
				}
			} else {
				if(inputData[0] instanceof DistributionLong) {
					length = ((DistributionLong) inputData[0]).getLongValues().length;
					
					for(int i = 1; i < inputData.length; i++) {
						if(length != ((DistributionLong) inputData[i]).getLongValues().length) {
							throw new AggregationException (
								"cannot aggregate values of different length ("
										+ ((DistributionLong) inputData[i]).getLongValues().length + " != "
										+ length + " @ " + i);
						}	
					}
				} else {
					length = ((Distribution) inputData[0]).getValues().length;
					
					for(int i = 1; i < inputData.length; i++) {
						if(length != ((Distribution) inputData[i]).getValues().length) {
							throw new AggregationException (
								"cannot aggregate values of different length ("
										+ ((Distribution) inputData[i]).getValues().length + " != "
										+ length + " @ " + i);
						}	
					}
				}
			}
		}
		if(inputData[0] instanceof NodeValueList) {
			length = ((NodeValueList) inputData[0]).getValues().length;
			
			for(int i = 1; i < inputData.length; i++) {
				if(length != ((NodeValueList) inputData[i]).getValues().length) {
					throw new AggregationException (
						"cannot aggregate values of different length ("
								+ ((NodeValueList) inputData[i]).getValues().length + " != "
								+ length + " @ " + i);
				}	
			}
		}
	}
	
	
	/**
	 * Aggregates the values of a list of Data objects and returns a AggregatedData object.
	 * 
	 * @param inputData Array of Data objects that are about to be aggregated         
	 * @param name name of the new AggregatedData object
	 *            
	 * @return AggregatedData object containing the aggregated values
	 * @throws AggregationException
	 */
	public static AggregatedData aggregateData(Data[] inputData, String name) throws AggregationException {
		Aggregation.test(inputData);
		
		
		if(inputData[0] instanceof Value) {
			// AggregatedValue array structure:  { avg, min, max, median, variance, variance-low, variance-up, confidence-low, confidence-up }
			// aggregation of values: put all values in an array and calculate aggregated values over the array
			double[] values0 = new double[inputData.length];
			for (int i = 0; i < values0.length-1; i++) {
				values0[i] = ((Value) inputData[i]).getValue();
			}
			
			double avg0 = ArrayUtils.avg(values0);
			double[] varLowUp0 = ArrayUtils.varLowUp(values0, avg0);
			double[] conf0 = ArrayUtils.conf(values0);
			
			double[] temp0 = { avg0, ArrayUtils.min(values0), ArrayUtils.max(values0), ArrayUtils.med(values0), varLowUp0[0], varLowUp0[1], varLowUp0[2], conf0[0], conf0[1] };
			
			AggregatedValue aggData0 = new AggregatedValue(name, temp0);
			return aggData0;
		}

		if(inputData[0] instanceof NodeValueList) {
			// AggregatedNodeValueList array structure:  { x (diff number), avg, min, max, median, variance, variance-low, variance-up, confidence-low, confidence-up }
			int amountValues1 = ((NodeValueList) inputData[0]).getValues().length; 
			int amountLists1 = inputData.length;
			
			AggregatedValue[] aggregatedData1 = new AggregatedValue[amountValues1];
			
			for (int i = 0; i < amountValues1; i++) {
				double[] values1 = new double[amountLists1];

				for (int j = 0; j < amountLists1; j++) {
					values1[j] = ((NodeValueList) inputData[j]).getValues()[i];
				}	
				double avg1 = ArrayUtils.avg(values1);
				double[] varLowUp1 = ArrayUtils.varLowUp(values1, avg1);
				double[] conf1 = ArrayUtils.conf(values1);
				// AggregatedNodeValueList array structure:  { x (diff number), avg, min, max, median, variance, variance-low, variance-up, confidence-low, confidence-up }
				double[] temp1 = { i, avg1, ArrayUtils.min(values1), ArrayUtils.max(values1), ArrayUtils.med(values1), varLowUp1[0] , varLowUp1[1], varLowUp1[2], conf1[0], conf1[1]};
				aggregatedData1[i] = new AggregatedValue(name + i, temp1);				
			}
			
			AggregatedNodeValueList aggData1 = new AggregatedNodeValueList(name, aggregatedData1);
			return aggData1;
		}
		
		if(inputData[0] instanceof Distribution) {
			// AggregatedDistribution array structure:  { x (diff number), avg, min, max, median, variance, variance-low, variance-up, confidence-low, confidence-up }
			int amountValues2 = ((Distribution) inputData[0]).getValues().length; 
			int amountDistributions2 = inputData.length;
			
			AggregatedValue[] aggregatedData2 = new AggregatedValue[amountValues2];

			for (int i = 0; i < amountValues2; i++) {
				double[] values2 = new double[amountDistributions2];

				for (int j = 0; j < amountDistributions2; j++) {
					values2[j] = ((Distribution) inputData[j]).getValues()[i];
				}	
				double avg2 = ArrayUtils.avg(values2);
				double[] varLowUp2 = ArrayUtils.varLowUp(values2, avg2);
				double[] conf2 = ArrayUtils.conf(values2);
				// AggregatedDistribution array structure:  { x (diff number), avg, min, max, median, variance, variance-low, variance-up, confidence-low, confidence-up }
				double[] temp2 = { i, avg2, ArrayUtils.min(values2), ArrayUtils.max(values2), ArrayUtils.med(values2), varLowUp2[0] , varLowUp2[1], varLowUp2[2], conf2[0], conf2[1]};
				aggregatedData2[i] = new AggregatedValue(name + i, temp2);				
			}
			
			AggregatedDistribution aggData2 = new AggregatedDistribution("ok", aggregatedData2);
			return aggData2;
			//return aggregatedData2;
=======
	
=======
	/*
>>>>>>> Codeupdate 13-06-18
=======
	
>>>>>>> Codeupdate 13-06-24
	/**
	 * Tests the obejcts of an arry of Data objects if they are compatible for an aggregation.
	 * Throws AggregationException when not.
	 * 
	 * @param inputData Array of Data objects that are about to be tested         
	 *            
	 * @throws AggregationException
	 */
	public static void test(Data[] inputData) throws AggregationException {
		int length;
		
		if(inputData.length < 2) {
			throw new AggregationException(
					"cannot aggregate on less than 2 values");
		}
		for(int i = 0; i < inputData.length-1; i++) {
			if(!Data.equals(inputData[i], inputData[i+1])) {
				throw new AggregationException(
					"cannot aggregate values of different type "
						+	inputData[i].getName() 
						+ " != " 
						+ 	inputData[i+1].getName());
			}
			
		}

		if(inputData[0] instanceof Distribution) {
			if(inputData[0] instanceof DistributionInt) {
				length = ((DistributionInt) inputData[0]).getIntValues().length;
				
				for(int i = 1; i < inputData.length; i++) {
					if(length != ((DistributionInt) inputData[i]).getIntValues().length) {
						throw new AggregationException (
							"cannot aggregate values of different length ("
									+ ((DistributionInt) inputData[i]).getIntValues().length + " != "
									+ length + " @ " + i);
					}	
				}
			} else {
				if(inputData[0] instanceof DistributionLong) {
					length = ((DistributionLong) inputData[0]).getLongValues().length;
					
					for(int i = 1; i < inputData.length; i++) {
						if(length != ((DistributionLong) inputData[i]).getLongValues().length) {
							throw new AggregationException (
								"cannot aggregate values of different length ("
										+ ((DistributionLong) inputData[i]).getLongValues().length + " != "
										+ length + " @ " + i);
						}	
					}
				} else {
					length = ((Distribution) inputData[0]).getValues().length;
					
					for(int i = 1; i < inputData.length; i++) {
						if(length != ((Distribution) inputData[i]).getValues().length) {
							throw new AggregationException (
								"cannot aggregate values of different length ("
										+ ((Distribution) inputData[i]).getValues().length + " != "
										+ length + " @ " + i);
						}	
					}
				}
			}
		}
		if(inputData[0] instanceof NodeValueList) {
			length = ((NodeValueList) inputData[0]).getValues().length;
			
			for(int i = 1; i < inputData.length; i++) {
				if(length != ((NodeValueList) inputData[i]).getValues().length) {
					throw new AggregationException (
						"cannot aggregate values of different length ("
								+ ((NodeValueList) inputData[i]).getValues().length + " != "
								+ length + " @ " + i);
				}	
			}
		}
	}
	
	
	/**
	 * Aggregates the values of a list of Data objects and returns a AggregatedData object.
	 * 
	 * @param inputData Array of Data objects that are about to be aggregated         
	 * @param name name of the new AggregatedData object
	 *            
	 * @return AggregatedData object containing the aggregated values
	 * @throws AggregationException
	 */
	public static AggregatedData aggregateData(Data[] inputData, String name) throws AggregationException {
		// check if inputData got the proper format to be aggregated
		Aggregation.test(inputData);
		
		if(inputData[0] instanceof Value) {	
			// AggregatedValue array structure:  { avg, min, max, median, variance, variance-low, variance-up, confidence-low, confidence-up }
			// aggregation of values: put all values in an array and calculate aggregated values over the array
			double[] values0 = new double[inputData.length];
			for (int i = 0; i < values0.length; i++) {
				values0[i] = ((Value) inputData[i]).getValue();
			}
			
			double avg0 = ArrayUtils.avg(values0);
			double[] varLowUp0 = ArrayUtils.varLowUp(values0, avg0);
			double[] conf0 = ArrayUtils.conf(values0);
			double[] temp0 = { avg0, ArrayUtils.min(values0), ArrayUtils.max(values0), ArrayUtils.med(values0), varLowUp0[0], varLowUp0[1], varLowUp0[2], conf0[0], conf0[1] };
			
			AggregatedValue aggData0 = new AggregatedValue(name, temp0);
			return aggData0;
		}

		if(inputData[0] instanceof NodeValueList) {
			// AggregatedNodeValueList array structure:  { x (diff number), avg, min, max, median, variance, variance-low, variance-up, confidence-low, confidence-up }
			int amountValues1 = ((NodeValueList) inputData[0]).getValues().length; 
			int amountLists1 = inputData.length;
			
			AggregatedValue[] aggregatedData1 = new AggregatedValue[amountValues1];
			
			for (int i = 0; i < amountValues1; i++) {
				double[] values1 = new double[amountLists1];

				for (int j = 0; j < amountLists1; j++) {
					values1[j] = ((NodeValueList) inputData[j]).getValues()[i];
				}	
				double avg1 = ArrayUtils.avg(values1);
				double[] varLowUp1 = ArrayUtils.varLowUp(values1, avg1);
				double[] conf1 = ArrayUtils.conf(values1);
				// AggregatedNodeValueList array structure:  { x (diff number), avg, min, max, median, variance, variance-low, variance-up, confidence-low, confidence-up }
				double[] temp1 = { i, avg1, ArrayUtils.min(values1), ArrayUtils.max(values1), ArrayUtils.med(values1), varLowUp1[0] , varLowUp1[1], varLowUp1[2], conf1[0], conf1[1]};
				aggregatedData1[i] = new AggregatedValue(name + i, temp1);				
			}
			
			AggregatedNodeValueList aggData1 = new AggregatedNodeValueList(name, aggregatedData1);
			return aggData1;
		}
		
		if(inputData[0] instanceof Distribution) {
			// AggregatedDistribution array structure:  { x (diff number), avg, min, max, median, variance, variance-low, variance-up, confidence-low, confidence-up }
			int amountValues2 = ((Distribution) inputData[0]).getValues().length; 
			int amountDistributions2 = inputData.length;
			
			AggregatedValue[] aggregatedData2 = new AggregatedValue[amountValues2];

<<<<<<< HEAD
				for (int i = 0; i < amountValues2; i++) {
					for (int j = 0; j < amountDistributions2; j++) {
						double[] values2 = new double[amountDistributions2];
						values2[j] = inputData[j].getValues()[i];
						
						double avg2 = ArrayUtils.avg(values2);
						double[] varLowUp2 = ArrayUtils.varLowUp(values2, avg2);
						double[] conf2 = ArrayUtils.conf(values2);
						// AggregatedDistribution array structure:  { x, Aggregated-y, avg, min, max, median, variance, variance-low, variance-up, confidence-low, confidence-up }
						double[] temp2 = { 0, 0, avg2, ArrayUtils.min(values2), ArrayUtils.max(values2), ArrayUtils.med(values2), varLowUp2[0] , varLowUp2[1], varLowUp2[2], conf2[0], conf2[1]};
						
						aggregatedData2[i] = new AggregatedDistribution(name, temp2);
					}
				}
				return aggregatedData2;
>>>>>>> Codeupdate 13-06-10.
=======
			for (int i = 0; i < amountValues2; i++) {
				double[] values2 = new double[amountDistributions2];
				for (int j = 0; j < amountDistributions2; j++) {
					values2[j] = ((Distribution) inputData[j]).getValues()[i];
				}	
				double avg2 = ArrayUtils.avg(values2);
				double[] varLowUp2 = ArrayUtils.varLowUp(values2, avg2);
				double[] conf2 = ArrayUtils.conf(values2);
				// AggregatedDistribution array structure:  { x (diff number), avg, min, max, median, variance, variance-low, variance-up, confidence-low, confidence-up }
				double[] temp2 = { i, avg2, ArrayUtils.min(values2), ArrayUtils.max(values2), ArrayUtils.med(values2), varLowUp2[0] , varLowUp2[1], varLowUp2[2], conf2[0], conf2[1]};
				aggregatedData2[i] = new AggregatedValue(name + i, temp2);
			}
<<<<<<< HEAD
<<<<<<< HEAD
			return aggregatedData2;
>>>>>>> Codeupdate 13-06-18
=======
			
			AggregatedDistribution aggData2 = new AggregatedDistribution("ok", aggregatedData2);
			return aggData2;
			//return aggregatedData2;
>>>>>>> Codeupdate 13-06-24
=======
			AggregatedDistribution aggData2 = new AggregatedDistribution(name, aggregatedData2);
			return aggData2;
>>>>>>> Codeupdate 13-06-28
		}
		// none of the common data types
		Log.warn("Attempting aggregation for unknown datatype!");
<<<<<<< HEAD
<<<<<<< HEAD
		AggregatedData add = new AggregatedData();
		return add;
>>>>>>> Codeupdate 13-06-10.
=======
		AggregatedData[] add = { new AggregatedData() };
=======
		AggregatedData add = new AggregatedData();
>>>>>>> Codeupdate 13-06-24
		return add;
>>>>>>> Codeupdate 13-06-10.
	}
	
	/**
	 * Aggregates the values for a SeriesData object.
	 * 
	 * 
	 * @param seriesData SeriesData object that is about to be aggregated         
	 * @param name name of the new AggregatedSeries object
	 *            
	 * @return AggregatedSeries object containing the aggregated values
	 * @throws AggregationException
	 */
	public static AggregatedSeries aggregateData(SeriesData seriesData) throws AggregationException {
		ArrayList<RunData> rdList = seriesData.getRuns();
		int runs = rdList.size();
		int batches = rdList.get(0).getBatches().size();
		Aggregation.test(seriesData);
		if(runs < 2)
			throw new AggregationException("Need 2 or more runs to aggregate!");
		// check all RunData-Objects for compatibility
		for(int i = 0; i < rdList.size()-1; i++) {
			if(!RunData.sameType(rdList.get(i), rdList.get(i+1)))
				throw new AggregationException("RunDatas not of the same type!");
		}

		// for every run collect data of same batch of same metric of same type and aggregated them
		// note: compatibility between batches and metrics already checked above
		@SuppressWarnings("unchecked")
		// array containing all aggregated data
		HashMap<String, AggregatedDataList>[] aggDataListMapArray = new HashMap[batches];

		// iterate over batches
		for(int batchX = 0; batchX < batches; batchX++) {

			HashMap<String, AggregatedDataList> aggDataListMap = new HashMap<String, AggregatedDataList>();

			//the aggBatchDataList contains statistical information of a batch like general runtimes, metric runtimes and other batch statistics
			AggregatedDataList aggBatchDataList = new AggregatedDataList();
			
			// iterate over general runtimes
			AggregatedValue[] aggRTLgenTemp = new AggregatedValue[rdList.get(0).getBatches().get(batchX).getGeneralRuntimes().getNames().size()];
			int counter = 0;
			for(String genRuntimeX : rdList.get(0).getBatches().get(batchX).getGeneralRuntimes().getNames()) {
				Value[] valuesTemp = new Value[runs];
				
				for(int i = 0; i < runs; i++) {
					Value tempValue = new Value(rdList.get(i).getBatches().get(batchX).getGeneralRuntimes().get(genRuntimeX).getName(), rdList.get(i).getBatches().get(batchX).getGeneralRuntimes().get(genRuntimeX).getRuntime());
					valuesTemp[i] = tempValue;
				}
				
				AggregatedValue aggValueTemp = (AggregatedValue) Aggregation.aggregateData(valuesTemp, valuesTemp[0].getName());				
				aggRTLgenTemp[counter] = aggValueTemp;
				counter++;
			}
			AggregatedRunTimeList aggRTLgen = new AggregatedRunTimeList(Names.batchGeneralRuntimes, aggRTLgenTemp);
			aggBatchDataList.add(aggRTLgen);
			
			// iterate over metric runtimes
			AggregatedValue[] aggRTLmetTemp = new AggregatedValue[rdList.get(0).getBatches().get(batchX).getMetricRuntimes().getNames().size()];
			counter = 0;
			for(String metRuntimeX : rdList.get(0).getBatches().get(batchX).getMetricRuntimes().getNames()) {
				Value[] valuesTemp = new Value[runs];
				
				for(int i = 0; i < runs; i++) {
					Value tempValue = new Value(rdList.get(i).getBatches().get(batchX).getMetricRuntimes().get(metRuntimeX).getName(), rdList.get(0).getBatches().get(batchX).getMetricRuntimes().get(metRuntimeX).getRuntime());
					valuesTemp[i] = tempValue;
				}
				AggregatedValue aggValueTemp = (AggregatedValue) Aggregation.aggregateData(valuesTemp, valuesTemp[0].getName());
				aggRTLmetTemp[counter] = aggValueTemp;
				counter++;
			}
			AggregatedRunTimeList aggRTLmet = new AggregatedRunTimeList(Names.batchMetricRuntimes, aggRTLmetTemp);
			aggBatchDataList.add(aggRTLmet);
			
			// iterate over batch statistics
			for(String statX : rdList.get(0).getBatches().get(batchX).getValues().getNames()) {
				Value[] valuesTemp = new Value[runs];
				
				for(int i = 0; i < runs; i++) {
					valuesTemp[i] = rdList.get(0).getBatches().get(batchX).getValues().get(statX);
				}
			
				AggregatedValue aggValueTemp = (AggregatedValue) Aggregation.aggregateData(valuesTemp, statX);
				aggBatchDataList.add(aggValueTemp);
			}
			aggDataListMap.put(Keywords.batchData, aggBatchDataList); 
			
			// iterate over metrics
			for(String metricX : rdList.get(0).getBatches().get(batchX).getMetrics().getNames()) {
				// aggDataList containing all aggregated Data for one metric X
				AggregatedDataList aggDataList = new AggregatedDataList();
				
				DistributionList dbList1 = rdList.get(0).getBatches().get(batchX).getMetrics().get(metricX).getDistributions();
				NodeValueListList nvList1 = rdList.get(0).getBatches().get(batchX).getMetrics().get(metricX).getNodeValues();
				ValueList nList1 = rdList.get(0).getBatches().get(batchX).getMetrics().get(metricX).getValues();
				
				for(String distributionX : dbList1.getNames()) {
					Distribution[] distTemp1 = new Distribution[runs];
					
					for(int i = 0; i < runs; i++) {
						distTemp1[i] = rdList.get(i).getBatches().get(batchX).getMetrics().get(metricX).getDistributions().get(distributionX);
					}

					// aggregates distributionX for batchX for every run
					AggregatedDistribution aggDist = (AggregatedDistribution) Aggregation.aggregateData(distTemp1, distributionX);
					aggDataList.add(aggDist);
				}
				for(String nodevaluelistX : nvList1.getNames()) {
					NodeValueList[] nvlTemp1 = new NodeValueList[runs];
					
					for(int i = 0; i < runs; i++) {
						nvlTemp1[i] = rdList.get(i).getBatches().get(batchX).getMetrics().get(metricX).getNodeValues().get(nodevaluelistX);
					}
					// aggregates NodeValueListX for batchX for every run
					AggregatedNodeValueList aggNvl = (AggregatedNodeValueList) Aggregation.aggregateData(nvlTemp1, nodevaluelistX);
					aggDataList.add(aggNvl);
				}
				
				for(String valueX : nList1.getNames()) {
					Value[] valueTemp1 = new Value[runs];
					
					for(int i = 0; i < runs; i++) {
						valueTemp1[i] = rdList.get(i).getBatches().get(batchX).getMetrics().get(metricX).getValues().get(valueX);
					}
					// aggregates ValueX for batchX for every run
					AggregatedValue aggValue = (AggregatedValue) Aggregation.aggregateData(valueTemp1, valueX);
					aggDataList.add(aggValue);
				}
				aggDataListMap.put(metricX, aggDataList);
				
			}
			
			aggDataListMapArray[batchX] = aggDataListMap;
		}
		AggregatedSeries aggregatedSeries = new AggregatedSeries(aggDataListMapArray);
		return aggregatedSeries;
	}
}
