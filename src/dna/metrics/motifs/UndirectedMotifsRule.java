package dna.metrics.motifs;

import dna.series.data.distr.BinnedIntDistr;

public interface UndirectedMotifsRule {
	public void execute(BinnedIntDistr motifs, boolean add);
}
