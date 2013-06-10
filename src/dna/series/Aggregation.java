package dna.series;

import dna.series.aggdata.AggregatedData;
import dna.series.aggdata.AggregatedDistribution;
import dna.series.aggdata.AggregatedNodeValueList;
import dna.series.aggdata.AggregatedValue;
import dna.series.data.BatchData;
import dna.series.data.Data;
import dna.series.data.RunData;
import dna.series.data.RunTime;
import dna.series.data.SeriesData;
import dna.series.data.Value;
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
	 * aggregates all data in the given series, i.e., agregate each batch of all
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
	 * Aggregates the values of one Data object and thus returns a AggregatedData object.
	 * The method distinguishes between different Datatypes via switch-case.
	 * 
	 * @param inputData Data object that is about to be aggregated
	 *            
	 * @param name name of the new AggregatedData object
	 *            
	 * @return AggregatedData object containing the aggregated values
	 * @throws AggregationException
	 */
	public static AggregatedData aggregateData(Data inputData, String name) {
		switch (inputData.getType()) {
		
			case "Value" : 	// Aggregation on single value??
				AggregatedValue aggregatedData0 = new AggregatedValue(name, inputData.getValue());
				return aggregatedData0;

			case "NodeValueList" :	// AggregatedNodeValueList array strucutre:  { 0, 0, avg, min, max, median, variance, variance-low, variance-up, confidence-low, confidence-up }
				double[] values1 = inputData.getValues();
				
				double avg1 = ArrayUtils.avg(values1);
				double[] varLowUp1 = ArrayUtils.varLowUp(values1, avg1);
				double[] conf1 = ArrayUtils.conf(values1);
				
				double[] temp1 = { 0, 0, avg1, ArrayUtils.min(values1), ArrayUtils.max(values1), ArrayUtils.med(values1), varLowUp1[0] , varLowUp1[1], varLowUp1[2], conf1[0], conf1[1]};
				
				AggregatedNodeValueList aggregatedData1 = new AggregatedNodeValueList(name, temp1);
				return aggregatedData1;
				
			case "Distribution" :	// AggregatedDistribution array structure:  { x, Aggregated-y, avg, min, max, median, variance, variance-low, variance-up, confidence-low, confidence-up }
				double[] values2 = inputData.getValues();
				
				double avg2 = ArrayUtils.avg(values2);
				double[] varLowUp2 = ArrayUtils.varLowUp(values2, avg2);
				double[] conf2 = ArrayUtils.conf(values2);
				
				double[] temp2 = { 0, 0, avg2, ArrayUtils.min(values2), ArrayUtils.max(values2), ArrayUtils.med(values2), varLowUp2[0] , varLowUp2[1], varLowUp2[2], conf2[0], conf2[1]};
				
				AggregatedDistribution aggregatedData2 = new AggregatedDistribution(name, temp2);
				return aggregatedData2;
		}
		
		// none of the common data types
		Log.warn("Attempting aggregation for unknown datatype!");
		return new AggregatedData();
	}
	
	/**
	 * Aggregates the values of a list of Data objects and returns a list of AggregatedData objects.
	 * 
	 * @param inputData Array of Data objects that are about to be aggregated
	 *            
	 * @param name name of the new AggregatedData object
	 *            
	 * @return AggregatedData[] array containing the objects with the aggregated values
	 * @throws AggregationException
	 */
	public static AggregatedData[] aggregateData(Data[] inputData, String name) {
		String dataType = inputData[0].getType();
		switch (dataType) {
		
			case "Value" : 	// AggregatedValue array structure:  { 0, 0, avg, min, max, median, variance, variance-low, variance-up, confidence-low, confidence-up }
				// aggregation of values: put all values in an array and calculate aggregated values over the array
				double[] values0 = new double[inputData.length];
				for (int i = 0; i < values0.length-1; i++) {
					values0[i] = inputData[i].getValue();
				}
				
				double avg0 = ArrayUtils.avg(values0);
				double[] varLowUp0 = ArrayUtils.varLowUp(values0, avg0);
				double[] conf0 = ArrayUtils.conf(values0);
				
				double[] temp0 = { 0, 0, avg0, ArrayUtils.min(values0), ArrayUtils.max(values0), ArrayUtils.med(values0), varLowUp0[0], varLowUp0[1], varLowUp0[2], conf0[0], conf0[1] };
				
				AggregatedValue[] aggregatedData0 = { new AggregatedValue(name, temp0) };
				return aggregatedData0;
				
			case "NodeValueList" :	// AggregatedNodeValueList array structure:  { 0, 0, avg, min, max, median, variance, variance-low, variance-up, confidence-low, confidence-up }
				int amountValues1 = inputData[0].getValues().length; 
				int amountLists1 = inputData.length;
				
				AggregatedNodeValueList[] aggregatedData1 = new AggregatedNodeValueList[amountValues1];

				for (int i = 0; i < amountValues1; i++) {
					for (int j = 0; j < amountLists1; j++) {
						double[] values1 = new double[amountLists1];
						values1[j] = inputData[j].getValues()[i];
						
						double avg1 = ArrayUtils.avg(values1);
						double[] varLowUp1 = ArrayUtils.varLowUp(values1, avg1);
						double[] conf1 = ArrayUtils.conf(values1);
						// AggregatedNodeValueList array strucutre:  { 0, 0, avg, min, max, median, variance, variance-low, variance-up, confidence-low, confidence-up }
						double[] temp1 = { 0, 0, avg1, ArrayUtils.min(values1), ArrayUtils.max(values1), ArrayUtils.med(values1), varLowUp1[0] , varLowUp1[1], varLowUp1[2], conf1[0], conf1[1]};
						
						aggregatedData1[i] = new AggregatedNodeValueList(name, temp1);
					}
				}
				return aggregatedData1;

			case "Distribution" :	// AggregatedDistribution array structure:  { x, Aggregated-y, avg, min, max, median, variance, variance-low, variance-up, confidence-low, confidence-up }
				int amountValues2 = inputData[0].getValues().length; 
				int amountDistributions2 = inputData.length;
				
				AggregatedDistribution[] aggregatedData2 = new AggregatedDistribution[amountValues2];

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
		}
		
		// none of the common data types
		Log.warn("Attempting aggregation for unknown datatype!");
		AggregatedData[] add = { new AggregatedData() };
		return add;
	}
}
