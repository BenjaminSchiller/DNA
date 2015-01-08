package dna.metrics.motifs;

import dna.metrics.motifs.UndirectedMotifs.UndirectedMotifType;
import dna.series.data.DistributionLong;

public class UndirectedMotifsRuleChange implements UndirectedMotifsRule {

	public UndirectedMotifType m1;
	public int i1;

	public UndirectedMotifType m2;
	public int i2;

	public UndirectedMotifsRuleChange(UndirectedMotifType m1,
			UndirectedMotifType m2) {
		this.m1 = m1;
		this.i1 = UndirectedMotifs.getIndex(this.m1);
		this.m2 = m2;
		this.i2 = UndirectedMotifs.getIndex(this.m2);
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
		return this.i1 + " => " + this.i2;
	}

}
