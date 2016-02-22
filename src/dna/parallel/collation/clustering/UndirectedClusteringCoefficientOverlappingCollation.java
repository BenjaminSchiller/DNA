package dna.parallel.collation.clustering;

import dna.metrics.IMetric.MetricType;
import dna.metrics.clustering.UndirectedClusteringCoefficient;
import dna.metrics.clustering.UndirectedClusteringCoefficientR;
import dna.parallel.collation.Collation;
import dna.parallel.partition.OverlappingPartition;
import dna.series.data.BatchData;

public class UndirectedClusteringCoefficientOverlappingCollation extends
		Collation<UndirectedClusteringCoefficient, OverlappingPartition> {

	public UndirectedClusteringCoefficientOverlappingCollation(String dir,
			int partitionCount, int run) {
		super("UndirectedClusteringCoefficientOverlappingCollation",
				MetricType.exact, new UndirectedClusteringCoefficientR(), dir,
				partitionCount, run);
	}

	@Override
	public boolean collate(BatchData[] bd) {
		// TODO Auto-generated method stub
		return false;
	}

}
