package dna.parallel.collation.clustering;

import dna.metrics.clustering.UndirectedClusteringCoefficient;
import dna.metrics.clustering.UndirectedClusteringCoefficientR;
import dna.parallel.collation.Collation;
import dna.parallel.collation.CollationData;
import dna.parallel.partition.NodeCutPartition;
import dna.parallel.partition.Partition.PartitionType;
import dna.parallel.util.Sleeper;

public class UndirectedClusteringCoefficientNodeCutCollation extends
		Collation<UndirectedClusteringCoefficient, NodeCutPartition> {

	public UndirectedClusteringCoefficientNodeCutCollation(String auxDir,
			String inputDir, int partitionCount, int run, Sleeper sleeper) {
		super("ClusteringCoefficientNodeCutCollation", MetricType.exact,
				PartitionType.NodeCut, new UndirectedClusteringCoefficientR(),
				auxDir, inputDir, partitionCount, run, sleeper, new String[] {
						"UndirectedClusteringCoefficientR",
						"UndirectedClusteringCoefficientU" }, new String[0],
				new String[0], new String[0]);
	}

	@Override
	public boolean collate(CollationData cd) {
		// TODO Auto-generated method stub
		return false;
	}

}
