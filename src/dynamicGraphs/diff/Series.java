package dynamicGraphs.diff;

import dynamicGraphs.graph.Graph;
import dynamicGraphs.metrics.Metric;
import dynamicGraphs.util.ArrayUtils;
import dynamicGraphs.util.GraphStats;
import dynamicGraphs.util.MetricStats;
import dynamicGraphs.util.Stats;

public class Series {
	public Series(Graph g, Diff[] diffs, Metric[] metrics) {
		this.g = g;
		this.diffs = diffs;
		this.metrics = metrics;
		this.diffApplication = new Stats[this.diffs.length];
		this.init = new Stats[this.metrics.length];
		this.steps = new Stats[this.metrics.length][this.diffs.length];
	}

	private Graph g;

	private Diff[] diffs;

	private Metric[] metrics;

	private Stats[] diffApplication;

	private Stats[] init;

	private Stats[][] steps;

	public void processIncremental() throws DiffNotApplicableException {
		this.process(true);
	}

	public void processIterative() throws DiffNotApplicableException {
		this.process(false);
	}

	public static Series[] process(Graph g, Diff[] diffs, Metric[] metrics,
			boolean iterative, int runs) throws DiffNotApplicableException {
		Series[] s = new Series[runs];
		for (int i = 0; i < s.length; i++) {
			s[i] = new Series(g, diffs, metrics);
			s[i].process(iterative);
		}
		return s;
	}

	public void process(boolean incremental) throws DiffNotApplicableException {
		for (int m = 0; m < this.metrics.length; m++) {
			this.init[m] = new MetricStats(this.metrics[m]);
			this.metrics[m].compute();
			this.init[m].end();
		}
		for (int d = 0; d < this.diffs.length; d++) {
			for (int m = 0; m < this.metrics.length; m++) {
				if (incremental && this.metrics[m].isIncremental()) {
					this.steps[m][d] = new MetricStats(this.metrics[m]);
					this.metrics[m].applyBefore(this.diffs[d]);
					this.steps[m][d].end();
				}
			}
			this.diffApplication[d] = new GraphStats(this.g);
			g.apply(this.diffs[d]);
			this.diffApplication[d].end();
			for (int m = 0; m < this.metrics.length; m++) {
				if (incremental && this.metrics[m].isIncremental()) {
					this.steps[m][d].restart();
					this.metrics[m].applyAfter(this.diffs[d]);
					this.steps[m][d].end();
				} else {
					this.steps[m][d] = new MetricStats(this.metrics[m]);
					this.metrics[m].compute();
					this.steps[m][d].end();
				}
			}
		}
	}

	public void printStats() {
		System.out.println("INIT:");
		for (Stats s : this.init) {
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
		for (int m = 0; m < series[0].metrics.length; m++) {
			double[] avg = new double[series.length];
			double[] total = new double[series.length];
			for (int i = 0; i < series.length; i++) {
				avg[i] = Stats.avgRuntime(series[i].steps[m]);
				total[i] = Stats.totalRuntime(series[i].steps[m]);
			}
			// System.out.println(series[0].metrics[m] + " ==> "
			// + (int) ArrayUtils.med(total) + " msec (step: "
			// + (int) ArrayUtils.med(avg) + " msec)");
			System.out.println(series[0].metrics[m].getKey() + "	"
					+ series[0].metrics[m].getTimestamp() + "	"
					+ (int) ArrayUtils.med(total) + "	"
					+ (int) ArrayUtils.med(avg));

		}
	}
}
