package dna.metrics.motifs;

import dna.series.data.distr.BinnedIntDistr;

public interface DirectedMotifsRule {
	public void execute(BinnedIntDistr motifs, boolean add);
}
