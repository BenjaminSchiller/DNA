package dna.metrics.motifs;

import dna.metrics.motifs.DirectedMotifs.DirectedMotifType;
import dna.series.data.distributions.DistributionLong;

public class DirectedMotifsRuleChange implements DirectedMotifsRule {

	public DirectedMotifType m1;
	public int i1;

	public DirectedMotifType m2;
	public int i2;

	public DirectedMotifsRuleChange(DirectedMotifType m1, DirectedMotifType m2) {
		this.m1 = m1;
		this.i1 = DirectedMotifs.getIndex(this.m1);
		this.m2 = m2;
		this.i2 = DirectedMotifs.getIndex(this.m2);
	}

	@Override
	public void execute(DistributionLong motifs, boolean add) {
		if (add) {
			motifs.decr(this.i1);
			motifs.incr(this.i2);
		} else {
			motifs.decr(this.i2);
			motifs.incr(this.i1);
		}
	}

	public String toString() {
		return this.m1 + " -> " + this.m2;
	}

}
