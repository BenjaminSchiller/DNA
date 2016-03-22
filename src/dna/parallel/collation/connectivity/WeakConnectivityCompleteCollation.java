package dna.parallel.collation.connectivity;

import dna.metrics.connectivity.WeakConnectivity;
import dna.metrics.connectivity.WeakConnectivityR;
import dna.parallel.collation.Collation;
import dna.parallel.collation.CollationData;
import dna.parallel.partition.CompletePartition;
import dna.parallel.partition.Partition.PartitionType;
import dna.parallel.util.Sleeper;

public class WeakConnectivityCompleteCollation extends
		Collation<WeakConnectivity, CompletePartition> {

	public WeakConnectivityCompleteCollation(String auxDir, String inputDir,
			int partitionCount, int run, Sleeper sleeper) {
		super("WeakConnectivityCompleteCollation", MetricType.exact,
				PartitionType.Complete, new WeakConnectivityR(),
				auxDir, inputDir, partitionCount, run, sleeper, new String[] {
						"WeakConnectivityB", "WeakConnectivityR",
						"WeakConnectivityU" }, new String[0], new String[0],
				new String[] {});
	}

	@Override
	public boolean collate(CollationData cd) {
		// TODO Auto-generated method stub
		return false;
	}
}
