package dna.labels.labeler.runtimes;

import java.util.ArrayList;

import dna.graph.Graph;
import dna.graph.generators.GraphGenerator;
import dna.labels.Label;
import dna.labels.labeler.Labeler;
import dna.metrics.IMetric;
import dna.series.data.BatchData;
import dna.series.data.RunTime;
import dna.series.lists.RunTimeList;
import dna.updates.batch.Batch;
import dna.updates.generators.BatchGenerator;
import dna.util.Log;

/**
 * A Labeller which compares two runtimes. The purpose is to print out a label
 * when the cumulative runtime of metric A gets ahead of the cumulative runtime
 * of metric B.<br>
 * <br>
 * 
 * <b>Note:</b> Metric A and B are order-sensitive. The Labeler will only check
 * if A surpasses B on one batch compared to the batch before.
 * 
 * @author Rwilmes
 * 
 */
public class MetricRuntimeIntersectionLabeler extends Labeler {

	private static String name = "MetricRuntimeIntersectionLabeler";
	private String label;
	private String value = "true";

	private String metricA;
	private String metricB;

	private double metricAMillis = 0;
	private double metricBMillis = 0;

	public MetricRuntimeIntersectionLabeler(String metricA, String metricB) {
		super(name);
		this.metricA = metricA;
		this.metricB = metricB;
		this.label = "A>B";
	}

	@Override
	public boolean isApplicable(GraphGenerator gg, BatchGenerator bg,
			IMetric[] metrics) {
		if (Labeler.getMetric(metrics, metricA) == null) {
			Log.warn(getName() + ": metric '" + metricA + "' not found!");
			return false;
		}
		if (Labeler.getMetric(metrics, metricB) == null) {
			Log.warn(getName() + ": metric '" + metricB + "' not found!");
			return false;
		}
		return true;
	}

	@Override
	public ArrayList<Label> computeLabels(Graph g, Batch batch,
			BatchData batchData, IMetric[] metrics) {
		ArrayList<Label> list = new ArrayList<Label>();

		double oldA = metricAMillis;
		double oldB = metricBMillis;

		RunTimeList runtimes = batchData.getMetricRuntimes();

		for (RunTime rt : runtimes.getList()) {
			if (rt.getName().equals(metricA))
				metricAMillis += rt.getMilliSec();
			if (rt.getName().equals(metricB))
				metricBMillis += rt.getMilliSec();
		}

		double oldDiff = oldB - oldA;
		double diff = metricBMillis - metricAMillis;

		if (oldDiff > 0 && diff <= 0) {
			Label l = new Label(getName(), this.label, this.value);
			list.add(l);
			Log.info(batchData.getTimestamp() + "  <-  " + l.toString());
		}
		return list;
	}

}
