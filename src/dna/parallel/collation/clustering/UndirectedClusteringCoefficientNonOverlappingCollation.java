package dna.parallel.collation.clustering;

import java.util.Set;

import dna.graph.nodes.Node;
import dna.metrics.clustering.UndirectedClusteringCoefficient;
import dna.metrics.clustering.UndirectedClusteringCoefficientR;
import dna.parallel.collation.Collation;
import dna.parallel.collation.CollationData;
import dna.parallel.partition.NonOverlappingPartition;
import dna.parallel.partition.Partition.PartitionType;
import dna.parallel.util.Sleeper;
import dna.series.data.MetricData;
import dna.series.data.lists.LongList;
import dna.series.data.nodevaluelists.NodeValueList;
import dna.util.ArrayUtils;

public class UndirectedClusteringCoefficientNonOverlappingCollation extends
		Collation<UndirectedClusteringCoefficient, NonOverlappingPartition> {

	public UndirectedClusteringCoefficientNonOverlappingCollation(
			String auxDir, String inputDir, int partitionCount, int run,
			Sleeper sleeper) {
		super("UndirectedClusteringCoefficientNonOverlappingCollation",
				MetricType.exact, PartitionType.NonOverlapping,
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
