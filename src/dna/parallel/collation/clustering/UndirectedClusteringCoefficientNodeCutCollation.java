package dna.parallel.collation.clustering;

import dna.metrics.clustering.UndirectedClusteringCoefficient;
import dna.metrics.clustering.UndirectedClusteringCoefficientR;
import dna.parallel.collation.Collation;
import dna.parallel.partition.NodeCutPartition;
import dna.series.data.BatchData;

public class UndirectedClusteringCoefficientNodeCutCollation extends
		Collation<UndirectedClusteringCoefficient, NodeCutPartition> {

	public UndirectedClusteringCoefficientNodeCutCollation(String dir,
			int partitionCount, int run) {
		super("ClusteringCoefficientNodeCutCollation", MetricType.exact,
				new UndirectedClusteringCoefficientR(), dir, partitionCount,
				run);
	}

	@Override
	public boolean collate(BatchData[] bd) {
		// TODO Auto-generated method stub
		return false;
	}

}
