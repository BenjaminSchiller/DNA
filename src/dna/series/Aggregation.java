package dna.series;

import dna.util.ArrayUtils;

public class Aggregation {
	public static RunData aggregate(SeriesData seriesData)
			throws AggregationException {
		Aggregation.test(seriesData);

		RunData aggregatedRun = new RunData(-1, seriesData.getRun(0).getDiffs()
				.size());
		for (int i = 0; i < seriesData.getRun(0).getDiffs().size(); i++) {
			DiffData[] diffs = new DiffData[seriesData.getRuns().size()];
			for (int j = 0; j < seriesData.getRuns().size(); j++) {
				diffs[j] = seriesData.getRun(j).getDiff(i);
			}
			Aggregation.test(diffs);

			DiffData d = diffs[0];
			DiffData aggregatedDiff = new DiffData(d.getTimestamp(), d
					.getValues().size(), d.getGeneralRuntimes().size(), d
					.getMetricRuntimes().size(), d.getMetrics().size());

			for (Value v : d.getValues()) {
				double[] values = new double[diffs.length];
				for (int j = 0; j < diffs.length; j++) {
					try {
						values[j] = diffs[j].getValue(v.getName()).getValue();
					} catch (NullPointerException e) {
						throw new AggregationException("value " + v.getName()
								+ " not found @ " + j);
					}
				}
				aggregatedDiff.addValue(new Value(v.getName(), ArrayUtils
						.med(values)));
			}

			for (RunTime rt : d.getGeneralRuntimes()) {
				double[] values = new double[diffs.length];
				for (int j = 0; j < diffs.length; j++) {
					try {
						values[j] = diffs[j].getGeneralRuntime(rt.getName())
								.getRuntime();
					} catch (NullPointerException e) {
						throw new AggregationException("general-runtime "
								+ rt.getRuntime() + " not found @ " + j);
					}
				}

				aggregatedDiff.addGeneralRuntime(new RunTime(rt.getName(),
						(long) ArrayUtils.med(values)));
			}

			for (RunTime rt : d.getMetricRuntimes()) {
				double[] values = new double[diffs.length];
				for (int j = 0; j < diffs.length; j++) {
					try {
						values[j] = diffs[j].getMetricRuntime(rt.getName())
								.getRuntime();
					} catch (NullPointerException e) {
						throw new AggregationException("metric-runtime "
								+ rt.getRuntime() + " not found @ " + j);
					}
				}

				aggregatedDiff.addMetricRuntime(new RunTime(rt.getName(),
						(long) ArrayUtils.med(values)));
			}
			
			aggregatedRun.addDiff(aggregatedDiff);
		}

		return aggregatedRun;
	}

	public static DiffData aggregate(DiffData[] diffs) {
		DiffData diff = new DiffData(diffs[0].getTimestamp());
		// TODO implement
		return diff;
	}

	public static Value average(Value[] list, String name) {
		double[] values = new double[list.length];

		for (int i = 0; i < list.length; i++) {
			values[i] = list[i].getValue();
		}

		return new Value(name, ArrayUtils.avg(values));
	}

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

	private static void test(SeriesData seriesData) throws AggregationException {
		int diffs = seriesData.getRun(0).getDiffs().size();
		for (int i = 0; i < seriesData.getRuns().size(); i++) {
			if (diffs != seriesData.getRun(i).getDiffs().size()) {
				throw new AggregationException(
						"cannot aggregate runs with different # of diffs: "
								+ seriesData.getRun(i).getDiffs().size()
								+ " != " + diffs + " @");
			}
		}
	}

	private static void test(DiffData[] diffs) throws AggregationException {
		DiffData d = diffs[0];
		for (int i = 0; i < diffs.length; i++) {
			if (d.getTimestamp() != diffs[i].getTimestamp()) {
				throw new AggregationException(
						"cannot aggregate diffs with different timestamps: "
								+ diffs[i] + " != " + d.getTimestamp() + " @ "
								+ i);
			}
			if (d.getValues().size() != diffs[i].getValues().size()) {
				throw new AggregationException(
						"cannot aggregate diffs with different # of values: "
								+ diffs[i].getValues().size() + " != "
								+ d.getValues().size() + " @ " + i);
			}
			if (d.getGeneralRuntimes().size() != diffs[i].getGeneralRuntimes()
					.size()) {
				throw new AggregationException(
						"cannot aggregate diffs with different # of general-runtimes: "
								+ diffs[i].getGeneralRuntimes().size() + " != "
								+ d.getGeneralRuntimes().size() + " @ " + i);
			}
			if (d.getMetricRuntimes().size() != diffs[i].getMetricRuntimes()
					.size()) {
				throw new AggregationException(
						"cannot aggregate diffs with different # of metric-runtimes: "
								+ diffs[i].getMetricRuntimes().size() + " != "
								+ d.getMetricRuntimes().size() + " @ " + i);
			}
			if (d.getMetrics().size() != diffs[i].getMetrics().size()) {
				throw new AggregationException(
						"cannot aggregate diffs with different # of metrics: "
								+ diffs[i].getMetrics().size() + " != "
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
}
