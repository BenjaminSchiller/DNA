package dna.parallel.collation.degree;

import dna.metrics.degree.DegreeDistribution;
import dna.metrics.degree.DegreeDistributionR;
import dna.parallel.collation.Collation;
import dna.parallel.collation.CollationData;
import dna.parallel.partition.CompletePartition;
import dna.parallel.partition.Partition.PartitionType;
import dna.parallel.util.Sleeper;
import dna.series.data.MetricData;
import dna.series.data.distr.BinnedIntDistr;

public class DegreeDistributionCompleteCollation extends
		Collation<DegreeDistribution, CompletePartition> {

	public DegreeDistributionCompleteCollation(String auxDir, String inputDir,
			int partitionCount, int run, Sleeper sleeper) {
		super("DegreeDistributionCompleteCollation", MetricType.exact,
				PartitionType.Complete, new DegreeDistributionR(), auxDir,
				inputDir, partitionCount, run, sleeper, new String[] {
						"DegreeDistributionR", "DegreeDistributionU" },
				new String[] {}, new String[] { "DegreeDistribution" },
				new String[] {});
	}

	@Override
	public boolean collate(CollationData cd) {
		m.degree = new BinnedIntDistr("DegreeDistribution");
		if (this.g.isDirected()) {
			m.inDegree = new BinnedIntDistr("InDegreeDistribution");
			m.outDegree = new BinnedIntDistr("OutDegreeDistribution");
		} else {
			m.inDegree = null;
			m.outDegree = null;
		}

		for (MetricData md : this.getSources(cd)) {
			BinnedIntDistr dd = (BinnedIntDistr) md.getDistributions().get(
					"DegreeDistribution");
			for (int i = 0; i <= dd.getMaxNonZeroIndex(); i++) {
				m.degree.incr(i, (int) dd.getValues()[i]);
			}
			if (this.g.isDirected()) {
				BinnedIntDistr in = (BinnedIntDistr) md.getDistributions().get(
						"InDegreeDistribution");
				for (int i = 0; i <= in.getMaxNonZeroIndex(); i++) {
					m.inDegree.incr(i, (int) in.getValues()[i]);
				}
				BinnedIntDistr out = (BinnedIntDistr) md.getDistributions()
						.get("OutDegreeDistribution");
				for (int i = 0; i <= out.getMaxNonZeroIndex(); i++) {
					m.outDegree.incr(i, (int) out.getValues()[i]);
				}
			}
		}
		return true;
	}

}
