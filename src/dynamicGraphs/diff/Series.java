package dynamicGraphs.diff;

import dynamicGraphs.graph.Edge;
import dynamicGraphs.graph.Graph;
import dynamicGraphs.metrics.Metric;
import dynamicGraphs.util.ArrayUtils;
import dynamicGraphs.util.Stats;

public class Series {
	public Series(Graph g, Diff[] diffs, Metric[] metrics) {
		this.g = g;
		this.diffs = diffs;
		this.metrics = metrics;
		this.diffApplication = new Stats[this.diffs.length];
		this.initialComputation = new Stats[this.metrics.length];
		this.steps = new Stats[this.metrics.length][this.diffs.length];
		this.total = new Stats[this.diffs.length];
	}

	private Graph g;

	private Diff[] diffs;

	private Metric[] metrics;

	private Stats[] initialComputation;

	private Stats[] diffApplication;

	private Stats[][] steps;

	private Stats[] total;

	public void process(boolean checkEquality)
			throws DiffNotApplicableException {
		for (int m = 0; m < this.metrics.length; m++) {
			this.initialComputation[m] = new Stats(this.metrics[m]);
			this.metrics[m].compute();
			this.initialComputation[m].end();
		}

		if (checkEquality) {
			Series.checkEquality(this.metrics);
		}

		for (int d = 0; d < this.diffs.length; d++) {
			this.total[d] = new Stats(this.g);

			// APPLY BEFORE DIFF
			for (int m = 0; m < this.metrics.length; m++) {
				this.steps[m][d] = new Stats(this.metrics[m]);
				if (this.metrics[m].isAppliedBeforeDiff()) {
					this.metrics[m].applyBeforeDiff(this.diffs[d]);
				}
				this.steps[m][d].end();
			}

			this.diffApplication[d] = new Stats(this.g);
			this.diffApplication[d].end();

			// REMOVE EDGES
			for (Edge e : this.diffs[d].getRemovedEdges()) {
				this.diffApplication[d].restart();
				boolean removed = this.g.removeEdge(e);
				this.diffApplication[d].end();
				if (!removed) {
					continue;
				}
				// APPLY AFTER EDGE
				for (int m = 0; m < this.metrics.length; m++) {
					if (this.metrics[m].isAppliedAfterEdge()) {
						this.steps[m][d].restart();
						this.metrics[m].applyAfterEdgeRemoval(this.diffs[d], e);
						this.steps[m][d].end();
					}
				}
			}

			// ADD EDGES
			for (Edge e : this.diffs[d].getAddedEdges()) {
				this.diffApplication[d].restart();
				boolean added = this.g.addEdge(e);
				this.diffApplication[d].end();
				if (!added) {
					continue;
				}
				// APPLY AFTER EDGE
				for (int m = 0; m < this.metrics.length; m++) {
					if (this.metrics[m].isAppliedAfterEdge()) {
						this.steps[m][d].restart();
						this.metrics[m]
								.applyAfterEdgeAddition(this.diffs[d], e);
						this.steps[m][d].end();
					}
				}
			}

			this.g.setTimestamp(this.diffs[d].getTo());

			// APPLY AFTER DIFF
			for (int m = 0; m < this.metrics.length; m++) {
				this.steps[m][d].restart();
				if (this.metrics[m].isAppliedAfterDiff()) {
					this.metrics[m].applyAfterDiff(this.diffs[d]);
				}
				this.steps[m][d].end();
			}

			// COMPUTE / CLEANUP
			for (int m = 0; m < this.metrics.length; m++) {
				this.steps[m][d].restart();
				if (this.metrics[m].isComputed()) {
					this.metrics[m].compute();
				} else {
					this.metrics[m].cleanupApplication();
				}
				this.steps[m][d].end();
			}

			if (checkEquality) {
				Series.checkEquality(this.metrics);
			}

			this.total[d].end();
		}

		this.g = null;
		this.diffs = null;
		for (Metric m : this.metrics) {
			m.reset();
		}
	}

	public static boolean checkEquality(Metric[] metrics) {
		if (metrics.length == 0) {
			return true;
		}
		System.out.println("comparing " + metrics.length + " metrics @ "
				+ metrics[0].getTimestamp());
		boolean ok = true;
		for (Metric m : metrics) {
			System.out.println("  " + m);
		}
		for (int i = 0; i < metrics.length; i++) {
			for (int j = i + 1; j < metrics.length; j++) {
				Metric m1 = metrics[i];
				Metric m2 = metrics[j];
				if (m1.equals(m2)) {
					System.out.println("    OK - " + m1.getKey() + " == "
							+ m2.getKey());
				} else {
					System.out.println("    !! - " + m1.getKey() + " != "
							+ m2.getKey());
				}
			}
		}
		return ok;
	}

	public void printStats() {
		System.out.println("INIT:");
		for (Stats s : this.initialComputation) {
			System.out.println("  " + s);
		}
		System.out.println("STEPS:");
		for (int m = 0; m < this.metrics.length; m++) {
			System.out.println("*** " + this.metrics[m]);
			for (int d = 0; d < this.diffs.length; d++) {
				System.out.println("  " + this.steps[m][d]);
			}
			System.out.println("  - - - - - - - -  - -");
			System.out.println("  " + Stats.avg(this.steps[m]));
		}
	}

	public void printSummaryStats() {
		for (int m = 0; m < this.metrics.length; m++) {
			System.out.println(this.metrics[m] + " --- "
					+ Stats.avg(this.steps[m]));
		}
	}

	public static void printStats(Series[] series) {
		double[] avg = new double[series.length];
		double[] total = new double[series.length];

		double[] avgSum = new double[series.length];
		double[] totalSum = new double[series.length];

		for (int m = 0; m < series[0].metrics.length; m++) {
			for (int i = 0; i < series.length; i++) {
				avg[i] = Stats.avgRuntime(series[i].steps[m]);
				total[i] = Stats.totalRuntime(series[i].steps[m]);
				avgSum[i] += Stats.avgRuntime(series[i].steps[m]);
				totalSum[i] += Stats.totalRuntime(series[i].steps[m]);
			}
			System.out.println(series[0].metrics[m].getKey() + "	"
					+ series[0].metrics[m].getTimestamp() + "	"
					+ (ArrayUtils.med(total) / 1000) + "	"
					+ (ArrayUtils.med(avg) / 1000));

		}

		for (int i = 0; i < series.length; i++) {
			avg[i] = Stats.avgRuntime(series[i].diffApplication);
			total[i] = Stats.totalRuntime(series[i].diffApplication);
			avgSum[i] += Stats.avgRuntime(series[i].diffApplication);
			totalSum[i] += Stats.totalRuntime(series[i].diffApplication);
		}
		System.out.println("GRAPH" + "	" + series[0].metrics[0].getTimestamp()
				+ "	" + (ArrayUtils.med(total) / 1000) + "	"
				+ (ArrayUtils.med(avg) / 1000));

		for (int i = 0; i < series.length; i++) {
			avg[i] = Stats.avgRuntime(series[i].total);
			total[i] = Stats.totalRuntime(series[i].total);
			avgSum[i] += Stats.avgRuntime(series[i].total);
			totalSum[i] += Stats.totalRuntime(series[i].total);
		}
		System.out.println("TOTAL" + "	" + series[0].metrics[0].getTimestamp()
				+ "	" + (ArrayUtils.med(total) / 1000) + "	"
				+ (ArrayUtils.med(avg) / 1000));

		System.out.println("SUM" + "	" + series[0].metrics[0].getTimestamp()
				+ "	" + (ArrayUtils.med(totalSum) / 1000) + "	"
				+ (ArrayUtils.med(avgSum) / 1000));
	}
}
