package dna.parallel.collation.similarityMeasures;

import dna.metrics.similarityMeasures.Measures;
import dna.parallel.collation.Collation;
import dna.parallel.collation.CollationData;
import dna.parallel.partition.CompletePartition;
import dna.parallel.partition.Partition.PartitionType;
import dna.parallel.util.Sleeper;
import dna.series.data.MetricData;

public class SimilarityCompleteCollation<M extends Measures> extends
		Collation<M, CompletePartition> {

	public SimilarityCompleteCollation(String name, M m, String auxDir,
			String inputDir, int partitionCount, int run, Sleeper sleeper,
			String sourceMetricName) {
		super(name, MetricType.exact, PartitionType.Complete, m, auxDir,
				inputDir, partitionCount, run, sleeper, new String[] {
						sourceMetricName + "-with_diagonal-in",
						sourceMetricName + "-with_diagonal-out",
						sourceMetricName + "-without_diagonal-in",
						sourceMetricName + "-without_diagonal-out" },
				new String[] { "MatchingAvg" }, new String[] {},
				new String[] {});
	}

	@Override
	public boolean collate(CollationData cd) {
		for (int i = 0; i < cd.bd.length; i++) {
			MetricData md = this.getSource(cd.bd[i]);
		}
		return true;
	}

}
