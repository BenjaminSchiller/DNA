package dna.parallel.collation.paths;

import dna.metrics.paths.UnweightedAllPairsShortestPaths;
import dna.metrics.paths.UnweightedAllPairsShortestPathsR;
import dna.parallel.collation.Collation;
import dna.parallel.collation.CollationData;
import dna.parallel.partition.OverlappingPartition;
import dna.parallel.partition.Partition.PartitionType;
import dna.parallel.util.Sleeper;

public class UnweightedAllPairsShortestPathsOverlappingCollation extends
		Collation<UnweightedAllPairsShortestPaths, OverlappingPartition> {

	public UnweightedAllPairsShortestPathsOverlappingCollation(String auxDir,
			String inputDir, int partitionCount, int run, Sleeper sleeper) {
		super("UnweightedAllPairsShortestPathsOverlappingCollation",
				MetricType.exact, PartitionType.Overlapping,
				new UnweightedAllPairsShortestPathsR(), auxDir, inputDir,
				partitionCount, run, sleeper, new String[] {
						"UnweightedAllPairsShortestPathsR",
						"UnweightedAllPairsShortestPathsU" }, new String[0],
						new String[]{"APSP"}, new String[0]);
	}

	@Override
	public boolean collate(CollationData cd) {
		// TODO Auto-generated method stub
		return false;
	}
}
