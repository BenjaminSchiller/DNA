package dna.parallel.collation.paths;

import dna.metrics.paths.UnweightedAllPairsShortestPaths;
import dna.metrics.paths.UnweightedAllPairsShortestPathsR;
import dna.parallel.collation.Collation;
import dna.parallel.collation.CollationData;
import dna.parallel.partition.CompletePartition;
import dna.parallel.partition.Partition.PartitionType;
import dna.parallel.util.Sleeper;
import dna.series.data.MetricData;
import dna.series.data.distr.BinnedIntDistr;

public class UnweightedAllPairsShortestPathsCompleteCollation extends
		Collation<UnweightedAllPairsShortestPaths, CompletePartition> {
	
	// TODO existing paths and possible paths are wrong...

	public UnweightedAllPairsShortestPathsCompleteCollation(String auxDir,
			String inputDir, int partitionCount, int run, Sleeper sleeper) {
		super("UnweightedAllPairsShortestPathsCompleteCollation",
				MetricType.exact, PartitionType.Complete,
				new UnweightedAllPairsShortestPathsR(), auxDir, inputDir,
				partitionCount, run, sleeper, new String[] {
						"UnweightedAllPairsShortestPathsR",
						"UnweightedAllPairsShortestPathsU" }, new String[0],
				new String[] { "APSP" }, new String[0]);
	}

	@Override
	public boolean collate(CollationData cd) {
		m.apsp = new BinnedIntDistr("APSP");

		for (MetricData md : this.getSources(cd)) {
			BinnedIntDistr distr = (BinnedIntDistr) md.getDistributions().get(
					"APSP");
			long[] values = distr.getValues();
			for (int i = 0; i < values.length; i++) {
				m.apsp.incr((Integer) i, (int) values[i]);
			}
		}
		return true;
	}

}
