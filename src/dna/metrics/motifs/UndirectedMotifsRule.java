package dna.metrics.motifs;

import dna.series.data.distr2.BinnedIntDistr;

public interface UndirectedMotifsRule {
	public void execute(BinnedIntDistr motifs, boolean add);
}
