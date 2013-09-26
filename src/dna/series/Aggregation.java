package dna.series;

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
import dna.series.data.RunData;
import dna.series.data.SeriesData;
import dna.series.data.Value;
import dna.series.lists.DistributionList;
import dna.series.lists.NodeValueListList;
import dna.series.lists.RunTimeList;
import dna.series.lists.ValueList;
import dna.util.ArrayUtils;
import dna.util.Config;

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
	}
}
