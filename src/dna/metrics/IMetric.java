package dna.metrics;

import dna.graph.Graph;
import dna.series.data.Distribution;
import dna.series.data.MetricData;
import dna.series.data.NodeNodeValueList;
import dna.series.data.NodeValueList;
import dna.series.data.Value;
import dna.updates.batch.Batch;

public interface IMetric {

	public static enum MetricType {
		exact, heuristic, quality, unknown
	}

	public Graph getGraph();

	public void setGraph(Graph g);

	public MetricData getData();

	public String getName();

	public String getNamePlain();

	public String getDescription();

	public Metric.MetricType getMetricType();

	/**
	 * 
	 * @return all the values computed by this metric
	 */
	public Value[] getValues();

	/**
	 * 
	 * @return all the distributions computed by this metric
	 */
	public Distribution[] getDistributions();

	/**
	 * 
	 * @return all the nodevaluelists computed by this metric
	 */
	public NodeValueList[] getNodeValueLists();

	/**
	 * 
	 * @return all the nodenodevaluelists computed by this metric
	 */
	public NodeNodeValueList[] getNodeNodeValueLists();

	/**
	 * 
	 * @param m
	 * @return true, if the metric can be compared, i.e., they compute the same
	 *         properties of a graph
	 */
	public boolean isComparableTo(IMetric m);

	/**
	 * 
	 * @param m
	 *            metric to compare to
	 * @return true, if the metric is of the same type and all computed values
	 *         are equal (can be used to compare different implementations of
	 *         the same metric)
	 */
	public boolean equals(IMetric m);

	/**
	 * 
	 * @param g
	 *            graph to check for applicability
	 * @return true, if the metric can be applied to the given graph
	 */
	public boolean isApplicable(Graph g);

	/**
	 * 
	 * @param b
	 *            batch to check for applicability
	 * @return true, if the batch can be applied to this graph (also false in
	 *         case of a re-computation metric)
	 */
	public boolean isApplicable(Batch b);
}
