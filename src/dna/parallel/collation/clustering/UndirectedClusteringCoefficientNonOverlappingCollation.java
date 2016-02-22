package dna.parallel.collation.clustering;

import dna.metrics.clustering.UndirectedClusteringCoefficient;
import dna.metrics.clustering.UndirectedClusteringCoefficientR;
import dna.parallel.collation.Collation;
import dna.parallel.partition.NonOverlappingPartition;
import dna.series.data.BatchData;
import dna.series.data.nodevaluelists.NodeValueList;

public class UndirectedClusteringCoefficientNonOverlappingCollation extends
		Collation<UndirectedClusteringCoefficient, NonOverlappingPartition> {

	public UndirectedClusteringCoefficientNonOverlappingCollation(String dir,
			int partitionCount, int run) {
		super("UndirectedClusteringCoefficientNonOverlappingCollation",
				MetricType.exact, new UndirectedClusteringCoefficientR(), dir,
				partitionCount, run);
	}

	@Override
	public boolean collate(BatchData[] bd) {
		System.out.println("collation: " + this.g.getTimestamp());
		UndirectedClusteringCoefficientR m = (UndirectedClusteringCoefficientR) this.m;
		m.averageCC = 0.1;
		m.globalCC = 0.2;
		m.localCC = new NodeValueList("localCC", this.g.getMaxNodeIndex() + 1);
		m.triangleCount = 0;
		m.potentialCount = 0;
		return true;
	}

}
