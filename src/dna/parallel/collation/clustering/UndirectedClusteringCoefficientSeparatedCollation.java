package dna.parallel.collation.clustering;

import dna.metrics.clustering.UndirectedClusteringCoefficient;
import dna.metrics.clustering.UndirectedClusteringCoefficientR;
import dna.parallel.collation.Collation;
import dna.parallel.collation.CollationData;
import dna.parallel.partition.Partition.PartitionType;
import dna.parallel.partition.SeparatedPartition;
import dna.parallel.util.Sleeper;

public class UndirectedClusteringCoefficientSeparatedCollation extends
		Collation<UndirectedClusteringCoefficient, SeparatedPartition> {

	public UndirectedClusteringCoefficientSeparatedCollation(String auxDir,
			String inputDir, int partitionCount, int run, Sleeper sleeper) {
		super("UndirectedClusteringCoefficientSeparatedCollation",
				MetricType.exact, PartitionType.SEPARATED,
				new UndirectedClusteringCoefficientR(), auxDir, inputDir,
				partitionCount, run, sleeper, new String[] {
						"UndirectedClusteringCoefficientR",
						"UndirectedClusteringCoefficientU" });
	}

	@Override
	public boolean collate(CollationData cd) {
		return false;
	}
}
