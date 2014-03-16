package dna.series;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import dna.io.ZipReader;
import dna.io.ZipWriter;
import dna.io.filesystem.Dir;
import dna.io.filesystem.Files;
import dna.series.aggdata.AggregatedBatch;
import dna.series.aggdata.AggregatedBinnedDistribution;
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
import dna.series.data.BinnedDistributionDouble;
import dna.series.data.BinnedDistributionInt;
import dna.series.data.BinnedDistributionLong;
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
		// number of runs
		int runs = rdList.size();

		// maximum number of batches
		int maxBatches = rdList.get(0).getBatches().size();
		int maxBatchesRunIndex = 0;
		for (int i = 1; i < rdList.size(); i++) {
			if (rdList.get(i).getBatches().size() > maxBatches) {
				maxBatches = rdList.get(i).getBatches().size();
				maxBatchesRunIndex = i;
			}
		}

		// for every run collect data of same batch of same metric of same type
		// and aggregated them
		// note: compatibility between batches and metrics already checked above
		String seriesDir = seriesData.getDir();

		String aggDir = Dir.getAggregationDataDir(seriesDir);

		/*
		 * BATCHES
		 */
		AggregatedBatch[] aggBatches = new AggregatedBatch[maxBatches];

		for (int batchX = 0; batchX < maxBatches; batchX++) {
			int batchXTimestamp = (int) rdList.get(maxBatchesRunIndex)
					.getBatches().get(batchX).getTimestamp();
			String batchDir;
			if (!SeriesGeneration.singleFile)
				batchDir = Dir.getBatchDataDir(aggDir, batchXTimestamp);
			else
				batchDir = Dir.delimiter;

			/*
			 * GENERAL RUNTIMES
			 */
			AggregatedRunTimeList aggGeneralRuntime = new AggregatedRunTimeList(
					Config.get("BATCH_GENERAL_RUNTIMES"));
			HashMap<String, double[]> aggGeneralRunTime = new HashMap<String, double[]>();
			for (String genRuntimeX : rdList.get(maxBatchesRunIndex)
					.getBatches().get(batchX).getGeneralRuntimes().getNames()) {

				double[] values = new double[runs];

				for (int i = 0; i < runs; i++) {
					// if batches out of bounds, assume 0
					if (batchX >= rdList.get(i).getBatches().size()) {
						values[i] = 0;
					} else {
						long tempTimestamp = rdList.get(i).getBatches()
								.get(batchX).getTimestamp();
						String dir;

						// if batch in zip
						if (SeriesGeneration.singleFile) {
							try {
								SeriesGeneration.readFileSystem = ZipReader
										.getBatchFileSystem(
												Dir.getRunDataDir(seriesDir, i),
												batchXTimestamp);
							} catch (Throwable e1) {
								e1.printStackTrace();
							}
							dir = Dir.delimiter;
						} else {
							dir = Dir.getBatchDataDir(
									Dir.getRunDataDir(seriesData.getDir(),
											rdList.get(i).getRun()),
									tempTimestamp);
						}
						RunTimeList tempGeneralRunTime = RunTimeList.read(
								dir,
								Config.get("BATCH_GENERAL_RUNTIMES")
										+ Config.get("SUFFIX_RUNTIME"));
						values[i] = tempGeneralRunTime.get(genRuntimeX)
								.getRuntime();
						if (SeriesGeneration.readFileSystem != null) {
							SeriesGeneration.readFileSystem.close();
							SeriesGeneration.readFileSystem = null;
						}
					}
				}
				aggGeneralRunTime.put(genRuntimeX,
						Aggregation.aggregate(values));
			}
			if (SeriesGeneration.singleFile) {
				try {
					SeriesGeneration.writeFileSystem = ZipWriter
							.createBatchFileSystem(aggDir, batchXTimestamp);
				} catch (Throwable e1) {
					e1.printStackTrace();
				}
			}
			AggregatedData.write(aggGeneralRunTime, batchDir, Files
					.getRuntimesFilename(Config.get("BATCH_GENERAL_RUNTIMES")));
			if (SeriesGeneration.writeFileSystem != null) {
				SeriesGeneration.writeFileSystem.close();
				SeriesGeneration.writeFileSystem = null;
			}

			/*
			 * METRIC RUNTIMES
			 */
			AggregatedRunTimeList aggMetricRuntime = new AggregatedRunTimeList(
					Config.get("BATCH_METRIC_RUNTIMES"));
			HashMap<String, double[]> aggMetricRunTime = new HashMap<String, double[]>();

			for (String metRuntimeX : rdList.get(maxBatchesRunIndex)
					.getBatches().get(batchX).getMetricRuntimes().getNames()) {
				double[] values = new double[runs];

				for (int i = 0; i < runs; i++) {
					// if batches out of bounds, assume 0
					if (batchX >= rdList.get(i).getBatches().size()) {
						values[i] = 0;
					} else {
						long tempTimestamp = rdList.get(i).getBatches()
								.get(batchX).getTimestamp();
						String dir;
						if (SeriesGeneration.singleFile) {
							try {
								SeriesGeneration.readFileSystem = ZipReader
										.getBatchFileSystem(
												Dir.getRunDataDir(seriesDir, i),
												batchXTimestamp);
							} catch (Throwable e1) {
								e1.printStackTrace();
							}
							dir = Dir.delimiter;
						} else {
							dir = Dir.getBatchDataDir(
									Dir.getRunDataDir(seriesData.getDir(),
											rdList.get(i).getRun()),
									tempTimestamp);
						}
						RunTimeList tempMetricRunTime = RunTimeList.read(
								dir,
								Config.get("BATCH_METRIC_RUNTIMES")
										+ Config.get("SUFFIX_RUNTIME"));
						values[i] = tempMetricRunTime.get(metRuntimeX)
								.getRuntime();
						if (SeriesGeneration.readFileSystem != null) {
							SeriesGeneration.readFileSystem.close();
							SeriesGeneration.readFileSystem = null;
						}
					}
				}
				aggMetricRunTime
						.put(metRuntimeX, Aggregation.aggregate(values));
			}
			if (SeriesGeneration.singleFile) {
				try {
					SeriesGeneration.writeFileSystem = ZipWriter
							.createBatchFileSystem(aggDir, batchXTimestamp);
				} catch (Throwable e1) {
					e1.printStackTrace();
				}
			}
			AggregatedData.write(aggMetricRunTime, batchDir, Files
					.getRuntimesFilename(Config.get("BATCH_METRIC_RUNTIMES")));
			if (SeriesGeneration.writeFileSystem != null) {
				SeriesGeneration.writeFileSystem.close();
				SeriesGeneration.writeFileSystem = null;
			}

			/*
			 * BATCH STATISTICS
			 */
			AggregatedValueList aggStats = new AggregatedValueList();
			HashMap<String, double[]> aggBatchStats = new HashMap<String, double[]>();
			for (String statX : rdList.get(maxBatchesRunIndex).getBatches()
					.get(batchX).getValues().getNames()) {
				double[] values = new double[runs];

				for (int i = 0; i < runs; i++) {
					// if batches out of bounds, assume 0
					if (batchX >= rdList.get(i).getBatches().size()) {
						values[i] = 0;
					} else {
						long tempTimestamp = rdList.get(i).getBatches()
								.get(batchX).getTimestamp();
						String dir;
						if (SeriesGeneration.singleFile) {
							try {
								SeriesGeneration.readFileSystem = ZipReader
										.getBatchFileSystem(
												Dir.getRunDataDir(seriesDir, i),
												batchXTimestamp);
							} catch (Throwable e1) {
								e1.printStackTrace();
							}
							dir = Dir.delimiter;
						} else {
							dir = Dir.getBatchDataDir(
									Dir.getRunDataDir(seriesData.getDir(),
											rdList.get(i).getRun()),
									tempTimestamp);
						}
						ValueList vList = ValueList.read(
								dir,
								Config.get("BATCH_STATS")
										+ Config.get("SUFFIX_VALUE"));
						values[i] = vList.get(statX).getValue();
						if (SeriesGeneration.readFileSystem != null) {
							SeriesGeneration.readFileSystem.close();
							SeriesGeneration.readFileSystem = null;
						}
					}
				}
				aggBatchStats.put(statX, Aggregation.aggregate(values));
			}
			if (SeriesGeneration.singleFile) {
				try {
					SeriesGeneration.writeFileSystem = ZipWriter
							.createBatchFileSystem(aggDir, batchXTimestamp);
				} catch (Throwable e1) {
					e1.printStackTrace();
				}
			}
			AggregatedData.write(aggBatchStats, batchDir,
					Files.getValuesFilename(Config.get("BATCH_STATS")));
			if (SeriesGeneration.writeFileSystem != null) {
				SeriesGeneration.writeFileSystem.close();
				SeriesGeneration.writeFileSystem = null;
			}

			/*
			 * METRICS
			 */
			AggregatedMetricList aggMetrics = new AggregatedMetricList();
			for (String metricX : rdList.get(maxBatchesRunIndex).getBatches()
					.get(batchX).getMetrics().getNames()) {
				DistributionList dbList1 = rdList.get(maxBatchesRunIndex)
						.getBatches().get(batchX).getMetrics().get(metricX)
						.getDistributions();
				NodeValueListList nvList1 = rdList.get(maxBatchesRunIndex)
						.getBatches().get(batchX).getMetrics().get(metricX)
						.getNodeValues();
				ValueList nList1 = rdList.get(maxBatchesRunIndex).getBatches()
						.get(batchX).getMetrics().get(metricX).getValues();
				String destDir;
				if (!SeriesGeneration.singleFile)
					destDir = Dir.getMetricDataDir(
							Dir.getBatchDataDir(aggDir, batchXTimestamp),
							metricX,
							rdList.get(maxBatchesRunIndex).getBatches()
									.get(batchX).getMetrics().get(metricX)
									.getType());
				else
					destDir = Dir.getMetricDataDir(
							Dir.delimiter,
							metricX,
							rdList.get(maxBatchesRunIndex).getBatches()
									.get(batchX).getMetrics().get(metricX)
									.getType());

				// reading metric X for batch X for each run from filesystem
				MetricData[] Metrics = new MetricData[runs];

				for (int i = 0; i < runs; i++) {
					// if batches out of bounds, put in empty MetricData object
					if (batchX >= rdList.get(i).getBatches().size()) {
						Metrics[i] = null;
					} else {
						long tempTimestamp = rdList.get(i).getBatches()
								.get(batchX).getTimestamp();
						String dir;
						if (SeriesGeneration.singleFile) {
							try {
								System.out
										.println("establishing single read filesystem on "
												+ Dir.getRunDataDir(seriesDir,
														i)
												+ " with timestamp: "
												+ batchXTimestamp);
								SeriesGeneration.readFileSystem = ZipReader
										.getBatchFileSystem(
												Dir.getRunDataDir(seriesDir, i),
												batchXTimestamp);
							} catch (Throwable e1) {
								e1.printStackTrace();
							}
							dir = Dir.delimiter;
						} else {
							dir = Dir.getBatchDataDir(
									Dir.getRunDataDir(seriesData.getDir(),
											rdList.get(i).getRun()),
									tempTimestamp);
						}

						Metrics[i] = MetricData.read(
								Dir.getMetricDataDir(dir, metricX, rdList
										.get(i).getBatches().get(batchX)
										.getMetrics().get(metricX).getType()),
								metricX, true);
						if (SeriesGeneration.readFileSystem != null) {
							SeriesGeneration.readFileSystem.close();
							SeriesGeneration.readFileSystem = null;
						}
					}
				}

				// DISTRIBUTIONS
				AggregatedDistributionList aggDistributions = new AggregatedDistributionList();
				for (String distributionX : dbList1.getNames()) {
					boolean aggregated = false;
					// DistributionInt
					if (!aggregated
							&& seriesData.getRun(maxBatchesRunIndex)
									.getBatches().get(batchX).getMetrics()
									.get(metricX).getDistributions()
									.get(distributionX) instanceof DistributionInt) {
						int amountValues = 0;
						for (int i = 0; i < runs; i++) {
							if (Metrics[i] != null) {
								if (((DistributionInt) Metrics[i]
										.getDistributions().get(distributionX))
										.getIntValues().length > amountValues)
									amountValues = ((DistributionInt) Metrics[i]
											.getDistributions().get(
													distributionX))
											.getIntValues().length;
							}
						}

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
									} catch (NullPointerException e) {
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
						if (SeriesGeneration.singleFile) {
							try {
								SeriesGeneration.writeFileSystem = ZipWriter
										.createBatchFileSystem(aggDir,
												batchXTimestamp);
							} catch (Throwable e1) {
								e1.printStackTrace();
							}
						}
						// BinnedDistributionInt
						if (seriesData.getRun(maxBatchesRunIndex).getBatches()
								.get(batchX).getMetrics().get(metricX)
								.getDistributions().get(distributionX) instanceof BinnedDistributionInt) {
							double binsizeTemp = ((BinnedDistributionInt) seriesData
									.getRun(maxBatchesRunIndex).getBatches()
									.get(batchX).getMetrics().get(metricX)
									.getDistributions().get(distributionX))
									.getBinSize();
							AggregatedBinnedDistribution
									.write(destDir,
											Files.getDistributionBinnedFilename(distributionX),
											binsizeTemp, aggregatedValues);
							aggDistributions
									.add(new AggregatedBinnedDistribution(
											distributionX));
						} else {
							AggregatedDistribution.write(destDir, Files
									.getDistributionFilename(distributionX),
									aggregatedValues);
							aggDistributions.add(new AggregatedDistribution(
									distributionX));
						}
						if (SeriesGeneration.writeFileSystem != null) {
							SeriesGeneration.writeFileSystem.close();
							SeriesGeneration.writeFileSystem = null;
						}
						aggregated = true;
					}

					// DistributionLong
					if (!aggregated
							&& seriesData.getRun(maxBatchesRunIndex)
									.getBatches().get(batchX).getMetrics()
									.get(metricX).getDistributions()
									.get(distributionX) instanceof DistributionLong) {
						int amountValues = 0;
						for (int i = 0; i < runs; i++) {
							if (Metrics[i] != null) {
								if (((DistributionLong) Metrics[i]
										.getDistributions().get(distributionX))
										.getLongValues().length > amountValues)
									amountValues = ((DistributionLong) Metrics[i]
											.getDistributions().get(
													distributionX))
											.getLongValues().length;
							}
						}
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
									} catch (NullPointerException e) {
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
						if (SeriesGeneration.singleFile) {
							try {
								SeriesGeneration.writeFileSystem = ZipWriter
										.createBatchFileSystem(aggDir,
												batchXTimestamp);
							} catch (Throwable e1) {
								e1.printStackTrace();
							}
						}
						// BinnedDistributionLong
						if (seriesData.getRun(maxBatchesRunIndex).getBatches()
								.get(batchX).getMetrics().get(metricX)
								.getDistributions().get(distributionX) instanceof BinnedDistributionLong) {
							double binsizeTemp = ((BinnedDistributionLong) seriesData
									.getRun(maxBatchesRunIndex).getBatches()
									.get(batchX).getMetrics().get(metricX)
									.getDistributions().get(distributionX))
									.getBinSize();
							AggregatedBinnedDistribution
									.write(destDir,
											Files.getDistributionBinnedFilename(distributionX),
											binsizeTemp, aggregatedValues);
							aggDistributions
									.add(new AggregatedBinnedDistribution(
											distributionX));
						} else {
							AggregatedDistribution.write(destDir, Files
									.getDistributionFilename(distributionX),
									aggregatedValues);
							aggDistributions.add(new AggregatedDistribution(
									distributionX));
						}
						if (SeriesGeneration.writeFileSystem != null) {
							SeriesGeneration.writeFileSystem.close();
							SeriesGeneration.writeFileSystem = null;
						}
						aggregated = true;
					}
					// DistributionDouble
					if (!aggregated
							&& seriesData.getRun(0).getBatches().get(batchX)
									.getMetrics().get(metricX)
									.getDistributions().get(distributionX) instanceof DistributionDouble) {
						int amountValues = 0;
						for (int i = 0; i < runs; i++) {
							if ((Metrics[i] != null)) {
								if (((DistributionDouble) Metrics[i]
										.getDistributions().get(distributionX))
										.getDoubleValues().length > amountValues)
									amountValues = ((DistributionDouble) Metrics[i]
											.getDistributions().get(
													distributionX))
											.getDoubleValues().length;
							}
						}
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
									} catch (NullPointerException e) {
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
						if (SeriesGeneration.singleFile) {
							try {
								SeriesGeneration.writeFileSystem = ZipWriter
										.createBatchFileSystem(aggDir,
												batchXTimestamp);
							} catch (Throwable e1) {
								e1.printStackTrace();
							}
						}
						if (seriesData.getRun(maxBatchesRunIndex).getBatches()
								.get(batchX).getMetrics().get(metricX)
								.getDistributions().get(distributionX) instanceof BinnedDistributionDouble) {
							double binsizeTemp = ((BinnedDistributionDouble) seriesData
									.getRun(maxBatchesRunIndex).getBatches()
									.get(batchX).getMetrics().get(metricX)
									.getDistributions().get(distributionX))
									.getBinSize();
							AggregatedBinnedDistribution
									.write(destDir,
											Files.getDistributionBinnedFilename(distributionX),
											binsizeTemp, aggregatedValues);
							aggDistributions
									.add(new AggregatedBinnedDistribution(
											distributionX));
						} else {
							AggregatedDistribution.write(destDir, Files
									.getDistributionFilename(distributionX),
									aggregatedValues);
							aggDistributions.add(new AggregatedDistribution(
									distributionX));
						}
						if (SeriesGeneration.writeFileSystem != null) {
							SeriesGeneration.writeFileSystem.close();
							SeriesGeneration.writeFileSystem = null;
						}
						aggregated = true;
					}

					// distribution of type distribution, not
					// Int/Long or Double
					if (!aggregated) {
						int amountValues = 0;
						for (int i = 0; i < runs; i++) {
							if (Metrics[i] != null) {
								if (Metrics[i].getDistributions()
										.get(distributionX).getValues().length > amountValues)
									amountValues = Metrics[i]
											.getDistributions()
											.get(distributionX).getValues().length;
							}
						}

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
									} catch (NullPointerException e) {
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
						if (SeriesGeneration.singleFile) {
							try {
								SeriesGeneration.writeFileSystem = ZipWriter
										.createBatchFileSystem(aggDir,
												batchXTimestamp);
							} catch (Throwable e1) {
								e1.printStackTrace();
							}
						}
						AggregatedDistribution.write(destDir,
								Files.getDistributionFilename(distributionX),
								aggregatedValues);
						if (SeriesGeneration.writeFileSystem != null) {
							SeriesGeneration.writeFileSystem.close();
							SeriesGeneration.writeFileSystem = null;
						}
						aggDistributions.add(new AggregatedDistribution(
								distributionX));
						aggregated = true;
					}
				}

				// NODEVALUELISTS
				AggregatedNodeValueListList aggNodeValues = new AggregatedNodeValueListList();
				for (String nodevaluelistX : nvList1.getNames()) {
					int amountValues = 0;
					for (int i = 0; i < runs; i++) {
						if ((Metrics[i] != null)) {
							if (Metrics[i].getNodeValues().get(nodevaluelistX)
									.getValues().length > amountValues)
								amountValues = Metrics[i].getNodeValues()
										.get(nodevaluelistX).getValues().length;
						}
					}

					double[][] aggregatedValues = new double[amountValues][runs];

					for (int i = 0; i < runs; i++) {
						for (int j = 0; j < amountValues; j++) {
							double[] values = new double[runs];
							for (int k = 0; k < runs; k++) {
								try {
									values[k] = Metrics[i].getNodeValues()
											.get(nodevaluelistX).getValues()[j];
								} catch (ArrayIndexOutOfBoundsException e) {
									values[k] = 0;
								} catch (NullPointerException e) {
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
					if (SeriesGeneration.singleFile) {
						try {
							SeriesGeneration.writeFileSystem = ZipWriter
									.createBatchFileSystem(aggDir,
											batchXTimestamp);
						} catch (Throwable e1) {
							e1.printStackTrace();
						}
					}
					AggregatedNodeValueList.write(destDir,
							Files.getNodeValueListFilename(nodevaluelistX),
							aggregatedValues);
					if (SeriesGeneration.writeFileSystem != null) {
						SeriesGeneration.writeFileSystem.close();
						SeriesGeneration.writeFileSystem = null;
					}
					aggNodeValues.add(new AggregatedNodeValueList(
							nodevaluelistX));
				}

				// VALUES
				AggregatedValueList aggValues = new AggregatedValueList();
				HashMap<String, double[]> aggregatedValues = new HashMap<String, double[]>();
				for (String valueX : nList1.getNames()) {
					double[] valueTemp = new double[runs];
					for (int i = 0; i < runs; i++) {
						try {
							valueTemp[i] = Metrics[i].getValues().get(valueX)
									.getValue();
						} catch (ArrayIndexOutOfBoundsException e) {
							valueTemp[i] = 0;
						} catch (NullPointerException e) {
							valueTemp[i] = 0;
						}
					}
					double[] values = Aggregation.aggregate(valueTemp);
					aggregatedValues.put(valueX, values);
					aggValues.add(new AggregatedValue(valueX));
				}
				if (SeriesGeneration.singleFile) {
					try {
						SeriesGeneration.writeFileSystem = ZipWriter
								.createBatchFileSystem(aggDir, batchXTimestamp);
					} catch (Throwable e1) {
						e1.printStackTrace();
					}
				}
				AggregatedValue.write(
						aggregatedValues,
						destDir,
						Config.get("METRIC_DATA_VALUES")
								+ Config.get("SUFFIX_VALUE"));
				if (SeriesGeneration.writeFileSystem != null) {
					SeriesGeneration.writeFileSystem.close();
					SeriesGeneration.writeFileSystem = null;
				}
				aggMetrics.add(new AggregatedMetric(metricX, aggValues,
						aggDistributions, aggNodeValues));
			}
			aggBatches[batchX] = new AggregatedBatch(batchXTimestamp, aggStats,
					aggGeneralRuntime, aggMetricRuntime, aggMetrics);
		}
		if (SeriesGeneration.writeFileSystem != null) {
			SeriesGeneration.writeFileSystem.close();
			SeriesGeneration.writeFileSystem = null;
		}
		if (SeriesGeneration.readFileSystem != null) {
			SeriesGeneration.readFileSystem.close();
			SeriesGeneration.readFileSystem = null;
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
