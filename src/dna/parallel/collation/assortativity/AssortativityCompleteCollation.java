package dna.parallel.collation.assortativity;

import dna.metrics.assortativity.Assortativity;
import dna.metrics.assortativity.AssortativityR;
import dna.parallel.collation.Collation;
import dna.parallel.collation.CollationData;
import dna.parallel.partition.CompletePartition;
import dna.parallel.partition.Partition.PartitionType;
import dna.parallel.util.Sleeper;
import dna.series.data.MetricData;

public class AssortativityCompleteCollation extends
		Collation<Assortativity, CompletePartition> {

	public AssortativityCompleteCollation(String auxDir, String inputDir,
			int partitionCount, int run, Sleeper sleeper) {
		super("AssortativityCompleteCollation", MetricType.exact,
				PartitionType.Complete, new AssortativityR(), auxDir, inputDir,
				partitionCount, run, sleeper, new String[] {
						"AssortativityR-In-Weighted",
						"AssortativityR-In-Unweighted",
						"AssortativityR-Out-Weighted",
						"AssortativityR-Out-Unweighted",
						"AssortativityU-In-Weighted",
						"AssortativityU-In-Unweighted",
						"AssortativityU-Out-Weighted",
						"AssortativityU-Out-Unweighted" }, new String[] {
						"totalEdgeWeight", "sum1", "sum2", "sum3" },
				new String[] {}, new String[] {});
	}

	@Override
	public boolean collate(CollationData cd) {
		m.totalEdgeWeight = 0;
		m.sum1 = 0;
		m.sum2 = 0;
		m.sum3 = 0;

		for (MetricData md : this.getSources(cd)) {
			m.totalEdgeWeight += md.getValues().get("totalEdgeWeight")
					.getValue();
			m.sum1 += md.getValues().get("sum1").getValue();
			m.sum2 += md.getValues().get("sum2").getValue();
			m.sum3 += md.getValues().get("sum3").getValue();
		}
		m.setR();
		return true;
	}

}
