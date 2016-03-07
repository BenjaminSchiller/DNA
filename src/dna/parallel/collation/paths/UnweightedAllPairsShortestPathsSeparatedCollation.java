package dna.parallel.collation.paths;

import dna.metrics.paths.UnweightedAllPairsShortestPaths;
import dna.metrics.paths.UnweightedAllPairsShortestPathsR;
import dna.parallel.collation.Collation;
import dna.parallel.collation.CollationData;
import dna.parallel.partition.Partition.PartitionType;
import dna.parallel.partition.SeparatedPartition;
import dna.parallel.util.Sleeper;

public class UnweightedAllPairsShortestPathsSeparatedCollation extends
		Collation<UnweightedAllPairsShortestPaths, SeparatedPartition> {

	public UnweightedAllPairsShortestPathsSeparatedCollation(String auxDir,
			String inputDir, int partitionCount, int run, Sleeper sleeper) {
		super("UnweightedAllPairsShortestPathsSeparatedCollation",
				MetricType.exact, PartitionType.Separated,
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
