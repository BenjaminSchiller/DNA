package dna.series;

import java.util.ArrayList;
import java.util.HashMap;

import dna.io.etc.Keywords;
import dna.io.filesystem.Names;
import dna.series.data.BatchData;
import dna.series.aggdata.AggregatedData;
import dna.series.aggdata.AggregatedDataList;
import dna.series.aggdata.AggregatedDistribution;
import dna.series.aggdata.AggregatedNodeValueList;
import dna.series.aggdata.AggregatedRunTimeList;
import dna.series.aggdata.AggregatedSeries;
import dna.series.aggdata.AggregatedValue;
import dna.series.data.Data;
import dna.series.data.Distribution;
import dna.series.data.DistributionInt;
import dna.series.data.DistributionLong;
import dna.series.data.NodeValueList;
import dna.series.data.RunData;
import dna.series.data.RunTime;
import dna.series.data.SeriesData;
import dna.series.data.Value;
import dna.series.lists.DistributionList;
import dna.series.lists.NodeValueListList;
import dna.series.lists.ValueList;
import dna.util.ArrayUtils;
import dna.util.Log;

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
	 * aggregates all data in the given series, i.e., agregate each batch of all
=======
	 * aggregates all data in the given series, i.e., aggregate each diff of all
>>>>>>> datatype NodeValueList added
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

		RunData aggregatedRun = new RunData(-1, seriesData.getRun(0).getBatches()
				.size());
		for (int i = 0; i < seriesData.getRun(0).getBatches().size(); i++) {
			BatchData[] batch = new BatchData[seriesData.getRuns().size()];
			for (int j = 0; j < seriesData.getRuns().size(); j++) {
				batch[j] = seriesData.getRun(j).getBatches().get(i);
			}
			Aggregation.test(batch);

			BatchData d = batch[0];
			BatchData aggregatedBatch = new BatchData(d.getTimestamp(), d
					.getValues().size(), d.getGeneralRuntimes().size(), d
					.getMetricRuntimes().size(), d.getMetrics().size());

			for (Value v : d.getValues().getList()) {
				double[] values = new double[batch.length];
				for (int j = 0; j < batch.length; j++) {
					try {
						values[j] = batch[j].getValues().get(v.getName())
								.getValue();
					} catch (NullPointerException e) {
						throw new AggregationException("value " + v.getName()
								+ " not found @ " + j);
					}
				}
				aggregatedBatch.getValues().add(
						new Value(v.getName(), ArrayUtils.med(values)));
			}

			for (RunTime rt : d.getGeneralRuntimes().getList()) {
				double[] values = new double[batch.length];
				for (int j = 0; j < batch.length; j++) {
					try {
						values[j] = batch[j].getGeneralRuntimes()
								.get(rt.getName()).getRuntime();
					} catch (NullPointerException e) {
						throw new AggregationException("general-runtime "
								+ rt.getRuntime() + " not found @ " + j);
					}
				}

				aggregatedBatch.getGeneralRuntimes()
						.add(new RunTime(rt.getName(), (long) ArrayUtils
								.med(values)));
			}

			for (RunTime rt : d.getMetricRuntimes().getList()) {
				double[] values = new double[batch.length];
				for (int j = 0; j < batch.length; j++) {
					try {
						values[j] = batch[j].getMetricRuntimes()
								.get(rt.getName()).getRuntime();
					} catch (NullPointerException e) {
						throw new AggregationException("metric-runtime "
								+ rt.getRuntime() + " not found @ " + j);
					}
				}

				aggregatedBatch.getMetricRuntimes()
						.add(new RunTime(rt.getName(), (long) ArrayUtils
								.med(values)));
			}

			aggregatedRun.getBatches().add(aggregatedBatch);
		}

		return aggregatedRun;
	}

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
	
	/** UNDER CONSTRUCTION
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
			values[i][1] = ArrayUtils.avg(temp);
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

	private static void test(BatchData[] batches) throws AggregationException {
		BatchData d = batches[0];
		for (int i = 0; i < batches.length; i++) {
			if (d.getTimestamp() != batches[i].getTimestamp()) {
				throw new AggregationException(
						"cannot aggregate batches with different timestamps: "
								+ batches[i] + " != " + d.getTimestamp() + " @ "
								+ i);
			}
			if (d.getValues().size() != batches[i].getValues().size()) {
				throw new AggregationException(
						"cannot aggregate batches with different # of values: "
								+ batches[i].getValues().size() + " != "
								+ d.getValues().size() + " @ " + i);
			}
			if (d.getGeneralRuntimes().size() != batches[i].getGeneralRuntimes()
					.size()) {
				throw new AggregationException(
						"cannot aggregate batches with different # of general-runtimes: "
								+ batches[i].getGeneralRuntimes().size() + " != "
								+ d.getGeneralRuntimes().size() + " @ " + i);
			}
			if (d.getMetricRuntimes().size() != batches[i].getMetricRuntimes()
					.size()) {
				throw new AggregationException(
						"cannot aggregate batches with different # of metric-runtimes: "
								+ batches[i].getMetricRuntimes().size() + " != "
								+ d.getMetricRuntimes().size() + " @ " + i);
			}
			if (d.getMetrics().size() != batches[i].getMetrics().size()) {
				throw new AggregationException(
						"cannot aggregate batches with different # of metrics: "
								+ batches[i].getMetrics().size() + " != "
								+ d.getMetrics().size() + " @ " + i);
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
			AggregatedDistribution aggData2 = new AggregatedDistribution(name, aggregatedData2);
			return aggData2;
		}
		// none of the common data types
		Log.warn("Attempting aggregation for unknown datatype!");
		AggregatedData add = new AggregatedData();
		return add;
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
