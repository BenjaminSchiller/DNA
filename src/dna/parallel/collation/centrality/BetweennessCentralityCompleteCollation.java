package dna.parallel.collation.centrality;

import dna.metrics.centrality.BetweennessCentrality;
import dna.metrics.centrality.BetweennessCentralityR;
import dna.parallel.collation.Collation;
import dna.parallel.collation.CollationData;
import dna.parallel.partition.CompletePartition;
import dna.parallel.partition.Partition.PartitionType;
import dna.parallel.util.Sleeper;
import dna.series.data.MetricData;
import dna.series.data.distr.BinnedDoubleDistr;
import dna.series.data.nodevaluelists.NodeValueList;

public class BetweennessCentralityCompleteCollation extends
		Collation<BetweennessCentrality, CompletePartition> {

	public BetweennessCentralityCompleteCollation(String auxDir,
			String inputDir, int partitionCount, int run, Sleeper sleeper) {
		super("BetweennessCentralityCollation", MetricType.exact,
				PartitionType.Complete, new BetweennessCentralityR(), auxDir,
				inputDir, partitionCount, run, sleeper, new String[] {
						"BetweennessCentralityR", "BetweennessCentralityU" },
				new String[] { "bCSum", "sumShortestPaths" },
				new String[] { "Normalized-BC" }, new String[] { "BC_Score" });
	}

	@Override
	public boolean collate(CollationData cd) {
		m.bCC = new NodeValueList("BC_Score",
				new double[this.g.getMaxNodeIndex() + 1]);
		m.binnedBC = new BinnedDoubleDistr("Normalized-BC", 0.01d);
		m.bCSum = 0;
		m.sumShortestPaths = 0;

		for (int i = 0; i < cd.bd.length; i++) {
			MetricData md = this.getSource(cd.bd[i]);
			NodeValueList bCC = md.getNodeValues().get("BC_Score");
			for (int j = 0; j < bCC.getValues().length; j++) {
				m.bCC.setValue(j, m.bCC.getValue(j) + bCC.getValue(j));
			}
			m.bCSum += md.getValues().get("bCSum").getValue();
			m.sumShortestPaths += md.getValues().get("sumShortestPaths")
					.getValue();
			BinnedDoubleDistr binnedBC = (BinnedDoubleDistr) md
					.getDistributions().get("Normalized-BC");
			for (int j = 0; j <= binnedBC.getMaxNonZeroIndex(); j++) {
				m.binnedBC.incr(0.01d * j, (int) binnedBC.getValues()[j]);
			}
		}
		return true;
	}

}
