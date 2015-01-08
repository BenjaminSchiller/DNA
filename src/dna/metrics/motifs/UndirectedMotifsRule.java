package dna.metrics.motifs;

import dna.series.data.DistributionLong;

public interface UndirectedMotifsRule {
	public void execute(DistributionLong motifs, boolean add);
}
